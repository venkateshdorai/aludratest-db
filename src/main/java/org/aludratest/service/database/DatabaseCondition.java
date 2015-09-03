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

import org.aludratest.service.Condition;
import org.aludratest.service.TechnicalLocator;
import org.aludratest.service.database.tablecolumn.TableColumn;
import org.databene.commons.Validator;

/** {@link Condition} interface of the {@link DatabaseService}.
 * @author Volker Bergmann
 * @author falbrech */
public interface DatabaseCondition extends Condition {

    /** Checks if the given query can be executed without raising a database exception. This does not ensure that the query returns
     * any results.
     * 
     * @param query SQL query to execute.
     * @return <code>true</code> if the query could be executed without any database exception, <code>false</code> otherwise. The
     *         database exception, if any, is not logged. */
    public boolean isValidQuery(@TechnicalLocator String query);

    /** Returns <code>true</code> if the given query is valid, but does not return a single row of data. If the query is invalid or
     * returns data, <code>false</code> is returned. Any database exception is not logged.
     * 
     * @param query SQL query to execute.
     * @return <code>true</code> if the query is valid and did not return a single row of data, <code>false</code> otherwise. */
    public boolean isEmptyQuery(@TechnicalLocator String query);

    /** Returns <code>true</code> if the given query is valid and returns at least one row of data. If the query is invalid or does
     * not return data, <code>false</code> is returned. Any database exception is not logged.
     * 
     * @param query SQL query to execute.
     * @return <code>true</code> if the query is valid and returned data, <code>false</code> otherwise. */
    public boolean isNonEmptyQuery(@TechnicalLocator String query);

	/**
	 * Checks if the value of the given column in the given row of a result set of a query matches the given validator.
	 * 
	 * @param rows
	 *            Result set rows, as returned from {@link DatabaseInteraction#query(String, Object...)}.
	 * @param rowNum
	 *            1-based number of the row to check.
	 * @param column
	 *            Column to check.
	 * @param validator
	 *            Validator to check the column value against.
	 * 
	 * @return <code>true</code> if the column value matches the validator, <code>false</code> otherwise.
	 */
	public <T> boolean valueMatches(DataRows rows, int rowNum, TableColumn<T> column, Validator<T> validator);

}
