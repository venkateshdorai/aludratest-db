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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

import org.aludratest.service.database.tablecolumn.DoubleColumn;
import org.aludratest.service.database.tablecolumn.FloatColumn;
import org.aludratest.service.database.tablecolumn.IntColumn;
import org.aludratest.service.database.tablecolumn.LongColumn;
import org.aludratest.service.database.tablecolumn.StringColumn;
import org.aludratest.testcase.TestStatus;
import org.aludratest.testing.service.AbstractAludraServiceTest;
import org.aludratest.util.validator.EqualsValidator;
import org.databene.commons.FileUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/** Tests the {@link DatabaseService}.
 * @author Volker Bergmann
 * @author falbrech */
@SuppressWarnings("javadoc")
public class DatabaseServiceTest extends AbstractAludraServiceTest {

    private DatabaseService service;

    @Before
    public void setUp() throws Exception {
        // force delete existing database, if any
        File fDbDir = new File("target/test-db");
        if (fDbDir.isDirectory()) {
            FileUtil.deleteDirectory(fDbDir);
        }

        // create an in-memory Derby database
        System.setProperty("derby.system.home", fDbDir.getAbsolutePath());
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");

        Connection conn = DriverManager.getConnection("jdbc:derby:testdb;create=true");
        String sql = "CREATE TABLE test1 (test_id INTEGER NOT NULL PRIMARY KEY, test_value1 VARCHAR(100), test_value2 CHAR(10), test_value3 BIGINT, test_value4 FLOAT, test_value5 DECIMAL(12,4))";
        executeStatement(conn, sql);

        sql = "INSERT INTO test1 (test_id, test_value1, test_value2, test_value3, test_value4, test_value5) VALUES (1, 'Hello World', 'Bla', "
                + (Integer.MAX_VALUE + 2l) + ", 17.5, 23.1234)";
        executeStatement(conn, sql);
        sql = "INSERT INTO test1 (test_id, test_value1, test_value2, test_value3, test_value4, test_value5) VALUES (2, 'A test', NULL, "
                + (Integer.MIN_VALUE - 2l) + ", NULL, 23.1234)";
        executeStatement(conn, sql);

        // for insert tests
        sql = "CREATE TABLE test2 (test_id INTEGER NOT NULL PRIMARY KEY, test_value1 VARCHAR(100), test_value2 CHAR(10), test_value3 BIGINT, test_value4 FLOAT, test_value5 DECIMAL(12,4))";
        executeStatement(conn, sql);

        conn.close();

        this.service = getLoggingService(DatabaseService.class, "dbtest");
    }

    @After
    public void tearDown() throws Exception {
        if (this.service != null) {
            this.service.close();
        }

        try {
            DriverManager.getConnection("jdbc:derby:testdb;shutdown=true");
        }
        catch (Throwable t) {
        }
        File fDbDir = new File("target/test-db");
        if (fDbDir.isDirectory()) {
            FileUtil.deleteDirectory(fDbDir);
        }
    }

    @Test
    public void testAssertValidQuery() {
        service.verify().assertValidQuery("SELECT * FROM test1");
        assertEquals(TestStatus.PASSED, testCase.getLastTestStep().getStatus());
        service.verify().assertValidQuery("ELECT * FROM test1");
        assertEquals(TestStatus.FAILED, testCase.getLastTestStep().getStatus());
    }

    @Test
    public void testAssertEmptyQuery() {
        service.verify().assertEmptyQuery("SELECT * FROM test1 WHERE test_id IS NULL");
        assertEquals(TestStatus.PASSED, testCase.getLastTestStep().getStatus());
        service.verify().assertEmptyQuery("SELECT * FROM test1 WHERE test_id IS NOT NULL");
        assertEquals(TestStatus.FAILED, testCase.getLastTestStep().getStatus());
    }

    @Test
    public void testAssertEmptyQuery_invalid() {
        service.verify().assertEmptyQuery("ELECT * FROM test1 WHERE test_id IS NULL");
        assertEquals(TestStatus.FAILEDAUTOMATION, testCase.getLastTestStep().getStatus());
    }

    @Test
    public void testAssertNonEmptyQuery() {
        service.verify().assertNonEmptyQuery("SELECT * FROM test1 WHERE test_id IS NOT NULL");
        assertEquals(TestStatus.PASSED, testCase.getLastTestStep().getStatus());
        service.verify().assertNonEmptyQuery("SELECT * FROM test1 WHERE test_id IS NULL");
        assertEquals(TestStatus.FAILED, testCase.getLastTestStep().getStatus());
    }

    @Test
    public void testAssertNonEmptyQuery_invalid() {
        service.verify().assertNonEmptyQuery("SELECT * FROM test1 WHERE tst_id IS NULL");
        assertEquals(TestStatus.FAILEDAUTOMATION, testCase.getLastTestStep().getStatus());
    }

    @Test
    public void testAssertSingleRowQuery_positive() {
        service.verify().assertSingleRowQuery("SELECT * FROM test1 WHERE test_id = 1");
        assertEquals(TestStatus.PASSED, testCase.getLastTestStep().getStatus());
    }

    @Test
    public void testAssertSingleRowQuery_negative_multi() {
        service.verify().assertSingleRowQuery("SELECT * FROM test1 WHERE test_id IS NOT NULL");
        assertEquals(TestStatus.FAILED, testCase.getLastTestStep().getStatus());
    }

    @Test
    public void testAssertSingleRowQuery_negative_empty() {
        service.verify().assertSingleRowQuery("SELECT * FROM test1 WHERE test_id IS NULL");
        assertEquals(TestStatus.FAILED, testCase.getLastTestStep().getStatus());
    }

    @Test
    public void testAssertSingleRowQuery_negative_invalid() {
        service.verify().assertSingleRowQuery("SELECT * FROM test1 WHERE tst_id IS NULL");
        assertEquals(TestStatus.FAILEDAUTOMATION, testCase.getLastTestStep().getStatus());
    }

	@Test
	public void testAssertValueMatches() {
		DataRows rows = service.perform().query("SELECT * FROM test1 WHERE test_id = ?", Integer.valueOf(1));
		service.verify().assertValueMatches(rows, 1, service.getTableColumnFactory().createStringColumn("test_value1"),
				new EqualsValidator("Hello World"));
		assertEquals(TestStatus.PASSED, testCase.getLastTestStep().getStatus());
		service.verify().assertValueMatches(rows, 1, service.getTableColumnFactory().createStringColumn("test_value1"),
				new EqualsValidator("Hello"));
		assertEquals(TestStatus.FAILED, testCase.getLastTestStep().getStatus());
	}

	@Test
    public void testIsValidQuery() {
        assertTrue(service.check().isValidQuery("SELECT * FROM test1"));
        assertNull(testCase.getLastTestStep());
        assertFalse(service.check().isValidQuery("ELECT * FROM test1"));
        assertNull(testCase.getLastTestStep());
    }

    @Test
    public void testIsEmptyQuery() {
        assertTrue(service.check().isEmptyQuery("SELECT * FROM test1 WHERE test_id IS NULL"));
        assertNull(testCase.getLastTestStep());
        assertFalse(service.check().isEmptyQuery("SELECT * FROM test1"));
        assertNull(testCase.getLastTestStep());
        assertFalse(service.check().isEmptyQuery("SELECT * FROM foo"));
        assertNull(testCase.getLastTestStep());
    }

    @Test
    public void testIsNonEmptyQuery() {
        assertTrue(service.check().isNonEmptyQuery("SELECT * FROM test1"));
        assertNull(testCase.getLastTestStep());
        assertFalse(service.check().isNonEmptyQuery("SELECT * FROM test1 WHERE test_id IS NULL"));
        assertNull(testCase.getLastTestStep());
        assertFalse(service.check().isNonEmptyQuery("SELECT * FROM foo"));
        assertNull(testCase.getLastTestStep());
    }

    @Test
    public void testSimpleQuery() {
        DataRows rows = service.perform().query("SELECT * FROM test1");
        assertNotNull(rows);
        assertEquals(2, rows.getRowCount());

        // two ways of getting column value
        StringColumn sc = service.getTableColumnFactory().createStringColumn("test_value1");
        Iterator<DataRow> iter = rows.iterator();
        DataRow row = iter.next();
        assertNotNull(row);
        assertEquals("Hello World", row.getValue(service, sc));
        assertEquals("Hello World", service.perform().getColumnValue(rows, 1, sc));

        assertEquals("A test", service.perform().getColumnValue(rows, 2, sc));
        row = iter.next();
        assertEquals("A test", row.getValue(service, sc));

        assertFalse(iter.hasNext());
        assertEquals(TestStatus.PASSED, testCase.getLastTestStep().getStatus());
    }

	@Test
	public void testParameterizedQuery() {
		DataRows rows = service.perform().query("SELECT * FROM test1 WHERE test_value1 = ? AND test_id = ?", "Hello World", 1);
		assertEquals(TestStatus.PASSED, testCase.getLastTestStep().getStatus());
		assertNotNull(rows);
		assertEquals(1, rows.getRowCount());
		rows = service.perform().query("SELECT * FROM test1 WHERE test_value1 = ? AND test_id = ?");
		assertEquals(TestStatus.FAILEDAUTOMATION, testCase.getLastTestStep().getStatus());
		assertNull(rows);
	}

    @Test
    public void testInvalidQuery() {
        DataRows rows = service.perform().query("SELECT * FROM nosuchtable");
        assertNull(rows);
        assertEquals(TestStatus.FAILEDAUTOMATION, testCase.getLastTestStep().getStatus());
    }

    @Test
    public void testEmptyQuery() {
        DataRows rows = service.perform().query("SELECT * FROM test1 WHERE test_id IS NULL");
        assertNotNull(rows);
        assertEquals(0, rows.getRowCount());
        assertEquals(TestStatus.PASSED, testCase.getLastTestStep().getStatus());
    }

    @Test
    public void testQueryDatatypes() {
        DataRows rows = service.perform().query("SELECT * FROM test1");

        Iterator<DataRow> iter = rows.iterator();
        DataRow row = iter.next();

        StringColumn sc = service.getTableColumnFactory().createStringColumn("test_value2");
        assertEquals("Bla       ", row.getValue(service, sc));
        IntColumn ic = service.getTableColumnFactory().createIntColumn("test_id");
        assertEquals(1, row.getValue(service, ic).intValue());

        LongColumn lc = service.getTableColumnFactory().createLongColumn("test_value3");
        assertEquals(Integer.MAX_VALUE + 2l, row.getValue(service, lc).longValue());

        FloatColumn fc = service.getTableColumnFactory().createFloatColumn("test_value4");
        assertEquals(17.5f, row.getValue(service, fc).floatValue(), 0.001);

        DoubleColumn dc = service.getTableColumnFactory().createDoubleColumn("test_value5");
        assertEquals(23.1234, row.getValue(service, dc).doubleValue(), 0.000001);
    }

    @Test
    public void testQueryInvalidDatatype() {
        DataRows rows = service.perform().query("SELECT * FROM test1");

        Iterator<DataRow> iter = rows.iterator();
        DataRow row = iter.next();

        IntColumn ic = service.getTableColumnFactory().createIntColumn("test_value3");
        assertNull(row.getValue(service, ic));
        assertEquals(TestStatus.FAILEDAUTOMATION, testCase.getLastTestStep().getStatus());
    }

    @Test
    public void testUpdate() {
        assertEquals(1, service.perform().update("UPDATE test1 SET test_value1 = 'My Test Value' WHERE test_id = 1"));
        assertEquals(TestStatus.PASSED, testCase.getLastTestStep().getStatus());
        DataRows rows = service.perform().query("SELECT * FROM test1");
        Iterator<DataRow> iter = rows.iterator();
        DataRow row = iter.next();
        StringColumn sc = service.getTableColumnFactory().createStringColumn("test_value1");
        assertEquals("My Test Value", row.getValue(service, sc));

        // update more than one row
        assertEquals(2, service.perform().update("UPDATE test1 SET test_value1 = 'My Test Value'"));
        assertEquals(TestStatus.PASSED, testCase.getLastTestStep().getStatus());

        for (DataRow dataRow : service.perform().query("SELECT * FROM test1")) {
            assertEquals("My Test Value", dataRow.getValue(service, sc));
        }

        // invalid statement
        assertEquals(0, service.perform().update("UPDATE test1 SET nonexistingcolumn = '!'"));
        assertEquals(TestStatus.FAILEDAUTOMATION, testCase.getLastTestStep().getStatus());
    }

    @Test
    public void testDelete() {
        assertEquals(2, service.perform().delete("DELETE FROM test1"));
        assertEquals(TestStatus.PASSED, testCase.getLastTestStep().getStatus());
        DataRows rows = service.perform().query("SELECT * FROM test1");
        assertEquals(0, rows.getRowCount());
        assertEquals(TestStatus.PASSED, testCase.getLastTestStep().getStatus());

        // invalid statement
        assertEquals(0, service.perform().delete("DELETE FROM test1 WHERE nonexistingcolumn = '!'"));
        assertEquals(TestStatus.FAILEDAUTOMATION, testCase.getLastTestStep().getStatus());
    }

    @Test
    public void testInsert() {
        assertEquals(2, service.perform().insert("INSERT INTO test2 SELECT * FROM test1"));
        assertEquals(TestStatus.PASSED, testCase.getLastTestStep().getStatus());
        DataRows rows = service.perform().query("SELECT * FROM test2");
        assertEquals(2, rows.getRowCount());

        // invalid statement
        assertEquals(0, service.perform().insert("INSERT INTO test2 WHERE nonexistingcolumn = '!'"));
        assertEquals(TestStatus.FAILEDAUTOMATION, testCase.getLastTestStep().getStatus());
    }

    private static void executeStatement(Connection conn, String sql) throws SQLException {
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            stmt.execute(sql);
        }
        finally {
            try {
                stmt.close();
            }
            catch (Exception e) {
            }
        }
    }

}
