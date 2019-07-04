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

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.aludratest.testing.service.AbstractAludraServiceTest;
import org.databene.commons.FileUtil;
import org.junit.After;
import org.junit.Before;

public abstract class AbstractDatabaseServiceTest extends AbstractAludraServiceTest {

	protected DatabaseService service;

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

		sql = "CREATE TABLE documents (id INT, text CLOB(64 K))";
		executeStatement(conn, sql);
		sql="INSERT INTO documents VALUES (?, ?)";
		executePreparedStatement(conn, sql);		
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
	
	private static void executePreparedStatement(Connection conn, String sql) throws SQLException, FileNotFoundException {
		PreparedStatement ps = null;
		try {
			// --- add a file
            File file = new File("LICENSE");
            int fileLength = (int) file.length();
 
            // - first, create an input stream
            java.io.InputStream fin = new java.io.FileInputStream(file);
			ps = conn.prepareStatement(sql);
			ps.setInt(1, 1477);
			 
            // - set the value of the input parameter to the input stream
            ps.setAsciiStream(2, fin, fileLength);
            ps.execute();
		}
		finally {
			try {
				ps.close();
			}
			catch (Exception e) {
			}
		}
	}

}
