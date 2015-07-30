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

/** Object factory for different table column types. Use the returned objects to retrieve values from a
 * {@link org.aludratest.service.database.DataRow}.
 * 
 * @author falbrech */
public interface TableColumnFactory {

    /** Creates a new IntColumn object for the column with the given name.
     * 
     * @param columnName Name of the column.
     * 
     * @return A new IntColumn object identifying the given column. */
    public IntColumn createIntColumn(String columnName);

    /** Creates a new LongColumn object for the column with the given name.
     * 
     * @param columnName Name of the column.
     * 
     * @return A new LongColumn object identifying the given column. */
    public LongColumn createLongColumn(String columnName);

    /** Creates a new FloatColumn object for the column with the given name.
     * 
     * @param columnName Name of the column.
     * 
     * @return A new FloatColumn object identifying the given column. */
    public FloatColumn createFloatColumn(String columnName);

    /** Creates a new DoubleColumn object for the column with the given name.
     * 
     * @param columnName Name of the column.
     * 
     * @return A new DoubleColumn object identifying the given column. */
    public DoubleColumn createDoubleColumn(String columnName);

    /** Creates a new StringColumn object for the column with the given name.
     * 
     * @param columnName Name of the column.
     * 
     * @return A new StringColumn object identifying the given column. */
    public StringColumn createStringColumn(String columnName);

    /** Creates a new DateColumn object for the column with the given name.
     * 
     * @param columnName Name of the column.
     * 
     * @return A new DateColumn object identifying the given column. */
    public DateColumn createDateColumn(String columnName);

    /** Creates a new TimeColumn object for the column with the given name.
     * 
     * @param columnName Name of the column.
     * 
     * @return A new TimeColumn object identifying the given column. */
    public TimeColumn createTimeColumn(String columnName);

    /** Creates a new TimestampColumn object for the column with the given name.
     * 
     * @param columnName Name of the column.
     * 
     * @return A new TimestampColumn object identifying the given column. */
    public TimestampColumn createTimestampColumn(String columnName);

}
