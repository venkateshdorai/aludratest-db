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
