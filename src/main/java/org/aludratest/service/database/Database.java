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

import org.aludratest.dict.ActionWordLibrary;

/** Base class for database related action word pattern classes. <br>
 * A subclass of this class:
 * <ul>
 * <li>offers business specific operations, e.g. <code>checkCustomerExists(CustomerData customer)</code></li>
 * <li>knows all required SQL statements for its operations</li>
 * <li>uses its protected <code>database</code> field to perform checks and operations.</li>
 * </ul>
 * Public methods of subclasses always should return either <code>this</code>, or <code>new MyDatabaseSubclass(database)</code>. <br>
 * Test cases should only use <code>Database</code> subclasses to access the database, instead of working with a
 * <code>DatabaseService</code> instance directly.
 * 
 * @author falbrech
 * @since 3.1.0 */
public abstract class Database implements ActionWordLibrary<Database> {

    protected final DatabaseService database;

    /** Constructs a new Database object which uses the given DatabaseService for its operations.
     * 
     * @param database Database service to use for operations, checks, and verifications. */
    public Database(DatabaseService database) {
        this.database = database;
        verifyState();
    }

    @Override
    public Database verifyState() {
        return this;
    }

    protected abstract void verifyValidDatabaseState();

    protected final DataRows executeQuery(String sql, Object... parameters) {
        return database.perform().query(sql, parameters);
    }

}
