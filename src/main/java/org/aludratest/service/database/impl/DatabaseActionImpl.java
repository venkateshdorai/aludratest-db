/*
 * Copyright (C) 2015 Hamburg Sud and the contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aludratest.service.database.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.List;

import javax.sql.rowset.CachedRowSet;

import org.aludratest.exception.AutomationException;
import org.aludratest.exception.FunctionalFailure;
import org.aludratest.service.SystemConnector;
import org.aludratest.service.database.DataRows;
import org.aludratest.service.database.DatabaseCondition;
import org.aludratest.service.database.DatabaseInteraction;
import org.aludratest.service.database.DatabaseVerification;
import org.aludratest.service.database.tablecolumn.TableColumn;
import org.aludratest.testcase.event.attachment.Attachment;
import org.aludratest.util.poll.PollService;
import org.aludratest.util.poll.PolledTask;
import org.databene.commons.Validator;

public class DatabaseActionImpl implements DatabaseInteraction, DatabaseCondition, DatabaseVerification {

    private Connection connection;

	private DatabaseConfiguration config;

	public DatabaseActionImpl(Connection connection, DatabaseConfiguration config) {
        this.connection = connection;
		this.config = config;
    }

    @Override
    public void setSystemConnector(SystemConnector systemConnector) {
        // stub in action classes
    }

    @Override
    public int insert(String sqlStatement, Object... parameters) {
        return insertUpdateDelete(sqlStatement, "insert", parameters);
    }

    @Override
    public int update(String sqlStatement, Object... parameters) {
        return insertUpdateDelete(sqlStatement, "update", parameters);
    }

    @Override
    public int delete(String sqlStatement, Object... parameters) {
        return insertUpdateDelete(sqlStatement, "delete", parameters);
    }

    @Override
    public DataRows query(String query, Object... parameters) {
		validateStatementPermission(query);
        Statement stmt = null;
        try {
            ResultSet rs;
            if (parameters.length > 0) {
                PreparedStatement ps = connection.prepareStatement(query);
                stmt = ps; // for auto-close in finally
                for (int i = 0; i < parameters.length; i++) {
                    setPreparedStatementParameter(ps, i + 1, parameters[i]);
                }
                rs = ps.executeQuery();
            }
            else {
                stmt = connection.createStatement();
                rs = stmt.executeQuery(query);
            }
            return new DataRowsImpl(createCachedRowSet(rs));
        }
        catch (SQLException e) {
            throw new AutomationException("Could not execute query in database", e);
        }
        finally {
            try {
                stmt.close();
            }
            catch (Throwable t) { // NOPMD
            }
        }
    }

    @Override
    public <T> T getColumnValue(DataRows rows, int rowNum, TableColumn<T> column) {
        if (!(rows instanceof DataRowsImpl)) {
            throw new AutomationException("rows parameter has not been created by this database service");
        }
        if (!(column instanceof TableColumnImpl)) {
            throw new AutomationException("column parameter has not been created by this database service");
        }

        if (rowNum > rows.getRowCount()) {
            throw new AutomationException("Row number out of bounds: " + rowNum + " (only " + rows.getRowCount()
                    + " row(s) in query result)");
        }

        CachedRowSet rowSet = ((DataRowsImpl) rows).getCachedRowSet();
        try {
            // scroll to row num
            rowSet.beforeFirst();
            for (int i = 0; i < rowNum; i++) {
                rowSet.next();
            }

            return ((TableColumnImpl<T>) column).getValueFromRowSet(rowSet);
        }
        catch (SQLException e) {
            throw new AutomationException("Could not retrieve value", e);
        }
    }

    @Override
	public boolean isValidQuery(String query, Object... parameters) {
        try {
			getQueryResultCount(query, 0, parameters);
            return true;
        }
        catch (SQLException e) {
            return false;
        }
    }

    @Override
	public boolean isEmptyQuery(String query, Object... parameters) {
        try {
			return getQueryResultCount(query, 1, parameters) == 0;
        }
        catch (SQLException e) {
            return false;
        }
    }

    @Override
	public boolean isNonEmptyQuery(String query, Object... parameters) {
        try {
			return getQueryResultCount(query, 1, parameters) > 0;
        }
        catch (SQLException e) {
            return false;
        }
    }

	@Override
	public <T> boolean valueMatches(DataRows rows, int rowNum, TableColumn<T> column, Validator<T> validator) {
		T value = getColumnValue(rows, rowNum, column);
		return validator.valid(value);
	}

    @Override
	public void assertEmptyQuery(String query, Object... parameters) {
		waitForStatementRowCountCondition(query, new Validator<Integer>() {
			@Override
			public boolean valid(Integer object) {
				return object.intValue() == 0;
			}
		}, "Query returned at least one row of data", parameters);
    }

    @Override
	public void assertNonEmptyQuery(String query, Object... parameters) {
		waitForStatementRowCountCondition(query, new Validator<Integer>() {
			@Override
			public boolean valid(Integer object) {
				return object.intValue() > 0;
			}
		}, "Query returned no rows of data", parameters);
    }

    @Override
	public void assertSingleRowQuery(String query, Object... parameters) {
		waitForStatementRowCountCondition(query, new Validator<Integer>() {
			@Override
			public boolean valid(Integer object) {
				return object.intValue() == 1;
			}
		}, "Query returned zero rows, or more than one row of data", parameters);
    }

    @Override
	public void assertValidQuery(String query, Object... parameters) {
        try {
			getQueryResultCount(query, 0, parameters);
        }
        catch (SQLException e) {
            throw new FunctionalFailure("Query is not valid");
        }
    }

	@Override
	public <T> void assertValueMatches(DataRows rows, int rowNum, TableColumn<T> column, Validator<T> validator) {
		T value = getColumnValue(rows, rowNum, column);
		if (!validator.valid(value)) {
			throw new FunctionalFailure("Value " + value + " does not match validation criteria " + validator);
		}
	}

    @Override
    public void reportInvalidState(String message) {
        throw new FunctionalFailure(message);
    }

    @Override
    public List<Attachment> createDebugAttachments() {
        return null;
    }

    @Override
    public List<Attachment> createAttachments(Object object, String title) {
        return null;
    }

    private int insertUpdateDelete(String sqlStatement, String messageType, Object... parameters) {
		validateStatementPermission(sqlStatement);
        Statement stmt = null;
        try {
            if (parameters.length > 0) {
                PreparedStatement ps = connection.prepareStatement(sqlStatement);
                stmt = ps; // for auto-close in finally
                for (int i = 0; i < parameters.length; i++) {
                    setPreparedStatementParameter(ps, i + 1, parameters[i]);
                }
                return ps.executeUpdate();
            }
            stmt = connection.createStatement();
            return stmt.executeUpdate(sqlStatement);
        }
        catch (SQLException e) {
            throw new AutomationException("Could not execute " + messageType + " in database", e);
        }
        finally {
            try {
                stmt.close();
            }
            catch (Throwable t) { // NOPMD
            }
        }
    }

	private int getQueryResultCount(String sql, int stopCount, Object... parameters) throws SQLException {
		validateStatementPermission(sql);
		Statement stmt = null;
		ResultSet rs = null;
        try {
			if (parameters.length > 0) {
				PreparedStatement ps = connection.prepareStatement(sql);
				stmt = ps; // for auto-close in finally
				for (int i = 0; i < parameters.length; i++) {
					setPreparedStatementParameter(ps, i + 1, parameters[i]);
				}
				rs = ps.executeQuery();
			}
			else {
				stmt = connection.createStatement();
				rs = stmt.executeQuery(sql);
			}
            int cnt = 0;
            while (rs.next() && cnt < stopCount) {
                cnt++;
            }
            return cnt;
        }
        finally {
			try {
				rs.close();
			}
			catch (Throwable t) { // NOPMD
			}
            try {
                stmt.close();
            }
            catch (Throwable t) { // NOPMD
            }
        }
    }

	private void validateStatementPermission(String sql) {
		sql = sql.trim().toUpperCase();

		// this is a rather simple check. E.g. Teradata would have a "LOCK ROW FOR ACCESS" even in front of
		// simple SELECT statements. Also, a semicolon separating multiple SQL commands (if supported by JDBC
		// driver) would not be detected.
		if (sql.startsWith("SELECT ")) {
			return;
		}

		if (sql.matches("(INSERT INTO|UPDATE|DELETE) .*")) {
			if (!config.isDmlEnabled()) {
				throw new AutomationException(
					"DML statement submitted, but not allowed for this database connection. Set enable.dml to true if required.");
			}
			// otherwise, OK
			return;
		}

		// everything else is treated as DDL
		if (!config.isDdlEnabled()) {
			throw new AutomationException(
					"DDL statement submitted, but not allowed for this database connection. Set enable.ddl to true if required.");
		}
	}

    private static CachedRowSet createCachedRowSet(ResultSet rs) throws SQLException {
        try {
            Class<?> clz = Class.forName("com.sun.rowset.CachedRowSetImpl");
            CachedRowSet rowSet = (CachedRowSet) clz.newInstance();
            rowSet.populate(rs);
            return rowSet;
        }
        catch (ClassNotFoundException e) {
            throw new SQLException("Could not find Sun's CachedRowSetImpl on classpath", e);
        }
        catch (InstantiationException e) {
            throw new SQLException("Could not instantiate Sun's CachedRowSetImpl", e);
        }
        catch (IllegalAccessException e) {
            throw new SQLException("Could not access Sun's CachedRowSetImpl", e);
        }
    }

    private static void setPreparedStatementParameter(PreparedStatement ps, int index, Object value) throws SQLException {
        if (value == null) {
            // this could be a problem with some JDBC drivers.
            ps.setNull(index, Types.NULL);
            return;
        }

        if (value instanceof String) {
            ps.setString(index, value.toString());
        }
        else if (value instanceof Long) {
            ps.setLong(index, ((Long) value).longValue());
        }
        else if (value instanceof Integer) {
            ps.setInt(index, ((Integer) value).intValue());
        }
        else if (value instanceof Float) {
            ps.setFloat(index, ((Float) value).intValue());
        }
        else if (value instanceof Number) {
            // handle all other types via setDouble
            ps.setDouble(index, ((Number) value).doubleValue());
        }
        else if (value instanceof java.sql.Date) {
            ps.setDate(index, (java.sql.Date) value);
        }
        else if (value instanceof Time) {
            ps.setTime(index, (Time) value);
        }
        else if (value instanceof java.util.Date) {
            ps.setTimestamp(index, new Timestamp(((java.util.Date) value).getTime()));
        }
        else {
            // no guarantee this works with used JDBC driver
            ps.setObject(index, value);
        }
    }

	private void waitForStatementRowCountCondition(final String query, final Validator<Integer> validator,
			final String failureMessage, final Object... parameters) {
		PolledTask<Boolean> task = new PolledTask<Boolean>() {
			@Override
			public Boolean timedOut() {
				throw new FunctionalFailure(failureMessage);
			}

			@Override
			public Boolean run() {
				try {
					int cnt = getQueryResultCount(query, 2, parameters);
					return validator.valid(Integer.valueOf(cnt)) ? Boolean.TRUE : null;
				}
				catch (SQLException e) {
					throw new AutomationException("Could not execute query", e);
				}
			}
		};

		if (config.getVerifyWaitTimeout() == 0) {
			// no polling
			if (task.run() == null) {
				task.timedOut();
			}
		}
		else {
			PollService poll = new PollService(config.getVerifyWaitTimeout(), config.getVerifyWaitInterval());
			poll.poll(task);
		}
	}

}
