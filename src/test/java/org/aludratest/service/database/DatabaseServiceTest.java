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
package org.aludratest.service.database;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.aludratest.service.database.tablecolumn.DoubleColumn;
import org.aludratest.service.database.tablecolumn.FloatColumn;
import org.aludratest.service.database.tablecolumn.IntColumn;
import org.aludratest.service.database.tablecolumn.LongColumn;
import org.aludratest.service.database.tablecolumn.StringColumn;
import org.aludratest.testcase.TestStatus;
import org.aludratest.util.validator.EqualsValidator;
import org.junit.Test;

/** Tests the {@link DatabaseService}.
 * @author Volker Bergmann
 * @author falbrech */
@SuppressWarnings("javadoc")
public class DatabaseServiceTest extends AbstractDatabaseServiceTest {

    @Test
    public void testAssertValidQuery() {
        service.verify().assertValidQuery("SELECT * FROM test1");
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());
        service.verify().assertValidQuery("ELECT * FROM test1");
		assertEquals(TestStatus.FAILED, getLastTestStep().getTestStatus());
    }

    @Test
    public void testAssertEmptyQuery() {
        service.verify().assertEmptyQuery("SELECT * FROM test1 WHERE test_id IS NULL");
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());
        service.verify().assertEmptyQuery("SELECT * FROM test1 WHERE test_id IS NOT NULL");
		assertEquals(TestStatus.FAILED, getLastTestStep().getTestStatus());
    }

    @Test
    public void testAssertEmptyQuery_invalid() {
        service.verify().assertEmptyQuery("ELECT * FROM test1 WHERE test_id IS NULL");
		assertEquals(TestStatus.FAILEDAUTOMATION, getLastTestStep().getTestStatus());
    }

    @Test
    public void testAssertNonEmptyQuery() {
        service.verify().assertNonEmptyQuery("SELECT * FROM test1 WHERE test_id IS NOT NULL");
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());
        service.verify().assertNonEmptyQuery("SELECT * FROM test1 WHERE test_id IS NULL");
		assertEquals(TestStatus.FAILED, getLastTestStep().getTestStatus());
    }

    @Test
    public void testAssertNonEmptyQuery_invalid() {
        service.verify().assertNonEmptyQuery("SELECT * FROM test1 WHERE tst_id IS NULL");
		assertEquals(TestStatus.FAILEDAUTOMATION, getLastTestStep().getTestStatus());
    }

    @Test
    public void testAssertSingleRowQuery_positive() {
        service.verify().assertSingleRowQuery("SELECT * FROM test1 WHERE test_id = 1");
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());
    }

    @Test
    public void testAssertSingleRowQuery_negative_multi() {
        service.verify().assertSingleRowQuery("SELECT * FROM test1 WHERE test_id IS NOT NULL");
		assertEquals(TestStatus.FAILED, getLastTestStep().getTestStatus());
    }

    @Test
    public void testAssertSingleRowQuery_negative_empty() {
        service.verify().assertSingleRowQuery("SELECT * FROM test1 WHERE test_id IS NULL");
		assertEquals(TestStatus.FAILED, getLastTestStep().getTestStatus());
    }

    @Test
    public void testAssertSingleRowQuery_negative_invalid() {
        service.verify().assertSingleRowQuery("SELECT * FROM test1 WHERE tst_id IS NULL");
		assertEquals(TestStatus.FAILEDAUTOMATION, getLastTestStep().getTestStatus());
    }

	@Test
	public void testAssertNonEmptyQuery_withWait() {
		DatabaseService waitService = getLoggingService(DatabaseService.class, "dbtestwait");
		// start a thread which writes the desired row after 1 second (using the other service)
		Runnable r = new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
				}
				catch (InterruptedException e) {
					return;
				}
				service.perform().insert("INSERT INTO test1 (test_id) VALUES(101)");
			}
		};
		new Thread(r).start();
		waitService.verify().assertNonEmptyQuery("SELECT * FROM test1 WHERE test_id = 101");
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());
	}

	@Test
	public void testAssertValueMatches() {
		DataRows rows = service.perform().query("SELECT * FROM test1 WHERE test_id = ?", Integer.valueOf(1));
		service.verify().assertValueMatches(rows, 1, service.getTableColumnFactory().createStringColumn("test_value1"),
				new EqualsValidator("Hello World"));
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());
		service.verify().assertValueMatches(rows, 1, service.getTableColumnFactory().createStringColumn("test_value1"),
				new EqualsValidator("Hello"));
		assertEquals(TestStatus.FAILED, getLastTestStep().getTestStatus());
	}

	@Test
    public void testIsValidQuery() {
        assertTrue(service.check().isValidQuery("SELECT * FROM test1"));
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());
        assertFalse(service.check().isValidQuery("ELECT * FROM test1"));
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());
    }

    @Test
    public void testIsEmptyQuery() {
        assertTrue(service.check().isEmptyQuery("SELECT * FROM test1 WHERE test_id IS NULL"));
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());
        assertFalse(service.check().isEmptyQuery("SELECT * FROM test1"));
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());
        assertFalse(service.check().isEmptyQuery("SELECT * FROM foo"));
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());
    }

    @Test
    public void testIsNonEmptyQuery() {
        assertTrue(service.check().isNonEmptyQuery("SELECT * FROM test1"));
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());
        assertFalse(service.check().isNonEmptyQuery("SELECT * FROM test1 WHERE test_id IS NULL"));
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());
        assertFalse(service.check().isNonEmptyQuery("SELECT * FROM foo"));
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());
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
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());
    }

	@Test
	public void testParameterizedQuery() {
		DataRows rows = service.perform().query("SELECT * FROM test1 WHERE test_value1 = ? AND test_id = ?", "Hello World", 1);
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());
		assertNotNull(rows);
		assertEquals(1, rows.getRowCount());
		rows = service.perform().query("SELECT * FROM test1 WHERE test_value1 = ? AND test_id = ?");
		assertEquals(TestStatus.FAILEDAUTOMATION, getLastTestStep().getTestStatus());
		assertNull(rows);
	}

    @Test
    public void testInvalidQuery() {
        DataRows rows = service.perform().query("SELECT * FROM nosuchtable");
        assertNull(rows);
		assertEquals(TestStatus.FAILEDAUTOMATION, getLastTestStep().getTestStatus());
    }

    @Test
    public void testEmptyQuery() {
        DataRows rows = service.perform().query("SELECT * FROM test1 WHERE test_id IS NULL");
        assertNotNull(rows);
        assertEquals(0, rows.getRowCount());
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());
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
		assertEquals(TestStatus.FAILEDAUTOMATION, getLastTestStep().getTestStatus());
    }

    @Test
    public void testUpdate() {
        assertEquals(1, service.perform().update("UPDATE test1 SET test_value1 = 'My Test Value' WHERE test_id = 1"));
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());
        DataRows rows = service.perform().query("SELECT * FROM test1");
        Iterator<DataRow> iter = rows.iterator();
        DataRow row = iter.next();
        StringColumn sc = service.getTableColumnFactory().createStringColumn("test_value1");
        assertEquals("My Test Value", row.getValue(service, sc));

        // update more than one row
        assertEquals(2, service.perform().update("UPDATE test1 SET test_value1 = 'My Test Value'"));
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());

        for (DataRow dataRow : service.perform().query("SELECT * FROM test1")) {
            assertEquals("My Test Value", dataRow.getValue(service, sc));
        }

        // invalid statement
        assertEquals(0, service.perform().update("UPDATE test1 SET nonexistingcolumn = '!'"));
		assertEquals(TestStatus.FAILEDAUTOMATION, getLastTestStep().getTestStatus());
    }

    @Test
    public void testDelete() {
        assertEquals(2, service.perform().delete("DELETE FROM test1"));
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());
        DataRows rows = service.perform().query("SELECT * FROM test1");
        assertEquals(0, rows.getRowCount());
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());

        // invalid statement
        assertEquals(0, service.perform().delete("DELETE FROM test1 WHERE nonexistingcolumn = '!'"));
		assertEquals(TestStatus.FAILEDAUTOMATION, getLastTestStep().getTestStatus());
    }

    @Test
    public void testInsert() {
        assertEquals(2, service.perform().insert("INSERT INTO test2 SELECT * FROM test1"));
		assertEquals(TestStatus.PASSED, getLastTestStep().getTestStatus());
        DataRows rows = service.perform().query("SELECT * FROM test2");
        assertEquals(2, rows.getRowCount());

        // invalid statement
        assertEquals(0, service.perform().insert("INSERT INTO test2 WHERE nonexistingcolumn = '!'"));
		assertEquals(TestStatus.FAILEDAUTOMATION, getLastTestStep().getTestStatus());
    }

}
