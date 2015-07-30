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

import org.aludratest.impl.log4testing.TechnicalLocator;
import org.aludratest.service.Verification;
import org.aludratest.service.database.tablecolumn.TableColumn;
import org.databene.commons.Validator;

/** {@link Verification} interface of the {@link DatabaseService}.
 * @author Volker Bergmann
 * @author falbrech */
public interface DatabaseVerification extends Verification {

    /** Asserts that the given query can be executed without any database error. This can still mean that the query does not return
     * any results.
     * 
     * @param query SQL query to execute. */
    public void assertValidQuery(@TechnicalLocator String query);

    /** Asserts that the given query can be executed without any database error and does <b>not</b> return any data.
     * 
     * @param query SQL query to execute. */
    public void assertEmptyQuery(@TechnicalLocator String query);

    /** Asserts that the given query can be executed without any database error and returns at least one row of data.
     * 
     * @param query SQL query to execute. */
    public void assertNonEmptyQuery(@TechnicalLocator String query);

    /** Asserts that the given query can be executed without any database error and returns <b>exactly</b> one row of data.
     * 
     * @param query SQL query to execute. */
    public void assertSingleRowQuery(@TechnicalLocator String query);

	/**
	 * Asserts that the value of the given column in the given row of a result set of a query matches the given validator.
	 * 
	 * @param rows
	 *            Result set rows, as returned from {@link DatabaseInteraction#query(String, Object...)}.
	 * @param rowNum
	 *            1-based number of the row to check.
	 * @param column
	 *            Column to check.
	 * @param validator
	 *            Validator to check the column value against.
	 */
	public <T> void assertValueMatches(DataRows rows, int rowNum, TableColumn<T> column, Validator<T> validator);

}
