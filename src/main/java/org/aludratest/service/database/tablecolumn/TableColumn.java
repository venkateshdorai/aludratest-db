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
package org.aludratest.service.database.tablecolumn;

import org.aludratest.service.database.DataRow;

/** Base interface of a table column. Construct different types of table columns using the TableColumnFactory returned by the
 * database service. Use the columns to retrieve values from a {@link DataRow}. <br>
 * This interface is not intended to be implemented outside the AludraTest Database Service package.
 * 
 * @author falbrech
 * 
 * @param <T> Type of the column, e.g. <code>String</code>. */
public interface TableColumn<T> {

    /** Returns the name of this table column, as it has been set by the TableColumnFactory creation method.
     * 
     * @return The name of this table column. */
    public String getColumnName();

}
