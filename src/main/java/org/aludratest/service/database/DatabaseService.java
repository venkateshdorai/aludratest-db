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

import org.aludratest.config.ConfigProperties;
import org.aludratest.config.ConfigProperty;
import org.aludratest.service.AludraService;
import org.aludratest.service.ServiceInterface;
import org.aludratest.service.database.tablecolumn.TableColumnFactory;

/**
 * Database service interface for AludraTest.
 * 
 * @author Volker Bergmann
 */
@ServiceInterface(name = "Database Service", description = "Enables Tests of SQL based Databases.")
@ConfigProperties({
		@ConfigProperty(name = "jdbcUrl", description = "JDBC URL for the database connection.", required = true, type = String.class),
		@ConfigProperty(name = "user", description = "User for the JDBC connection.", required = false, type = String.class),
		@ConfigProperty(name = "password", description = "Password for the JDBC connection.", required = false, type = String.class),
		@ConfigProperty(name = "jdbcDriverClass", description = "Fully qualified name of the JDBC Driver Class to use. The class must be on classpath, so consider including the appropriate dependency in your pom.xml. For surefire based executions, the dependency must also be added to the surefire plug-in as dependency (next to the AludraTest Surefire Provider).", required = true, type = String.class),
		@ConfigProperty(name = "enable.dml", description = "Allow INSERT, UPDATE, and DELETE statements. By default, these types of statements are forbidden.", required = true, type = boolean.class, defaultValue = "false"),
		@ConfigProperty(name = "enable.ddl", description = "Allow types of statements other than plain DML (SELECT, INSERT, UPDATE, DELETE), e.g. table creation and dropping, truncations etc. By default, these types of statements are forbidden.", required = true, type = boolean.class, defaultValue = "false") })
public interface DatabaseService extends AludraService {

	/** @return the related {@link DatabaseInteraction} */
	@Override
	DatabaseInteraction perform();

	/** @return the related {@link DatabaseVerification} */
	@Override
	DatabaseVerification verify();

	/** @return the related {@link DatabaseCondition} */
	@Override
	DatabaseCondition check();

	/**
	 * Returns the factory to be used to retrieve instances of different table column types.
	 * 
	 * @return The factory to be used to retrieve instances of different table column types.
	 */
	TableColumnFactory getTableColumnFactory();

}
