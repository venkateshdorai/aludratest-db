/*
 * Copyright (C) 2010-2014 Hamburg Sud and the contributors.
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
package org.aludratest.service.database;

import org.aludratest.impl.log4testing.TechnicalArgument;
import org.aludratest.impl.log4testing.TechnicalLocator;
import org.aludratest.service.Interaction;
import org.aludratest.service.database.tablecolumn.TableColumn;

/**
 * {@link Interaction} interface of the {@link DatabaseService}.
 * @author Volker Bergmann
 * @author falbrech */
public interface DatabaseInteraction extends Interaction {

    /** Executes an SQL <code>INSERT</code> statement.
     * 
     * @param sqlStatement SQL <code>INSERT</code> statement.
     * @param parameters Parameters for the SQL statement, if any. The SQL statement must be in PreparedStatement syntax to use
     *            parameters (use ? as placeholder for parameters). Parameters must be in same order as to be used in SQL
     *            statement.
     * 
     * @return Number of affected database rows. */
    int insert(@TechnicalLocator String sqlStatement, @TechnicalArgument Object... parameters);

    /** Executes an SQL <code>UPDATE</code> statement.
     * 
     * @param sqlStatement SQL <code>UPDATE</code> statement.
     * @param parameters Parameters for the SQL statement, if any. The SQL statement must be in PreparedStatement syntax to use
     *            parameters (use ? as placeholder for parameters). Parameters must be in same order as to be used in SQL
     *            statement.
     * 
     * @return Number of affected database rows. */
    int update(@TechnicalLocator String sqlStatement, @TechnicalArgument Object... parameters);

    /** Executes an SQL <code>DELETE</code> statement.
     * 
     * @param sqlStatement SQL <code>DELETE<code> statement.
     * @param parameters Parameters for the SQL statement, if any. The SQL statement must be in PreparedStatement syntax to use
     *            parameters (use ? as placeholder for parameters). Parameters must be in same order as to be used in SQL
     *            statement.
     * 
     * @return Number of deleted database rows. */
    int delete(@TechnicalLocator String sqlStatement, @TechnicalArgument Object... parameters);

    /** Executes an SQL <code>SELECT</code> statement (a query).
     * 
     * @param query SQL query to execute.
     * @param parameters Parameters for the SQL statement, if any. The SQL statement must be in PreparedStatement syntax to use
     *            parameters (use ? as placeholder for parameters). Parameters must be in same order as to be used in SQL
     *            statement.
     * 
     * @return The (possibly empty) collection of returned data rows. Use {@link #getColumnValue(DataRows, int, TableColumn)} to
     *         examine it. */
    DataRows query(@TechnicalLocator String query, @TechnicalArgument Object... parameters);

    /** Returns the value of a column in a given row in a set of data rows.
     * 
     * @param rows The set of data rows.
     * @param rowNum The (1-based) number of the row.
     * @param column The column to retrieve the value of. Must have been created with the TableColumnFactory returned by
     *            {@link DatabaseService#getTableColumnFactory()}.
     * @return The value of the column in the given row, possibly <code>null</code>. */
    <T> T getColumnValue(DataRows rows, @TechnicalArgument int rowNum, @TechnicalLocator TableColumn<T> column);

    /** Fails the current test with an appropriate exception, indicating that the DB has an invalid state.
     * 
     * @param message Message to log. */
    void reportInvalidState(String message);

}
