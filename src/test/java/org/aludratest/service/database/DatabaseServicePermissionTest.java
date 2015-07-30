package org.aludratest.service.database;

import static org.junit.Assert.assertEquals;

import org.aludratest.testcase.TestStatus;
import org.junit.Test;

public class DatabaseServicePermissionTest extends AbstractDatabaseServiceTest {

	@Test
	public void testNoPermissions1() {
		this.service = getLoggingService(DatabaseService.class, "dbtestp1");
		service.perform().query("SELECT * FROM test1");
		assertEquals(TestStatus.PASSED, testCase.getLastTestStep().getStatus());
		service.perform().update("UPDATE test1 SET test_value1 = NULL");
		assertEquals(TestStatus.FAILEDAUTOMATION, testCase.getLastTestStep().getStatus());
	}

	@Test
	public void testNoPermissions2() {
		this.service = getLoggingService(DatabaseService.class, "dbtestp1");
		service.perform().update("DROP TABLE test1");
		assertEquals(TestStatus.FAILEDAUTOMATION, testCase.getLastTestStep().getStatus());
	}

	@Test
	public void testDmlPermissions() {
		this.service = getLoggingService(DatabaseService.class, "dbtestp2");
		service.perform().query("SELECT * FROM test1");
		assertEquals(TestStatus.PASSED, testCase.getLastTestStep().getStatus());
		service.perform().update("UPDATE test1 SET test_value1 = NULL");
		assertEquals(TestStatus.PASSED, testCase.getLastTestStep().getStatus());
		service.perform().update("DROP TABLE test1");
		assertEquals(TestStatus.FAILEDAUTOMATION, testCase.getLastTestStep().getStatus());
	}

	public void testDdlPermissions() {
		this.service = getLoggingService(DatabaseService.class, "dbtestp3");
		service.perform().query("SELECT * FROM test1");
		assertEquals(TestStatus.PASSED, testCase.getLastTestStep().getStatus());
		service.perform().update("DROP TABLE test1");
		assertEquals(TestStatus.PASSED, testCase.getLastTestStep().getStatus());
		service.perform().update("UPDATE test1 SET test_value1 = NULL");
		assertEquals(TestStatus.FAILEDAUTOMATION, testCase.getLastTestStep().getStatus());
	}

}
