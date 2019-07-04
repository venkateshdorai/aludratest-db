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
package org.aludratest.service.database.impl;

import org.aludratest.service.database.tablecolumn.ClobColumn;
import org.aludratest.service.database.tablecolumn.DateColumn;
import org.aludratest.service.database.tablecolumn.DoubleColumn;
import org.aludratest.service.database.tablecolumn.FloatColumn;
import org.aludratest.service.database.tablecolumn.IntColumn;
import org.aludratest.service.database.tablecolumn.LongColumn;
import org.aludratest.service.database.tablecolumn.StringColumn;
import org.aludratest.service.database.tablecolumn.TableColumnFactory;
import org.aludratest.service.database.tablecolumn.TimeColumn;
import org.aludratest.service.database.tablecolumn.TimestampColumn;

/** Default implementation of the TableColumnFactory interface. Used by default DatabaseService implementation.
 * 
 * @author falbrech */
public class TableColumnFactoryImpl implements TableColumnFactory {

    @Override
    public IntColumn createIntColumn(String columnName) {
        assertNotNull(columnName);
        return new IntColumnImpl(columnName);
    }

    @Override
    public LongColumn createLongColumn(String columnName) {
        assertNotNull(columnName);
        return new LongColumnImpl(columnName);
    }

    @Override
    public FloatColumn createFloatColumn(String columnName) {
        assertNotNull(columnName);
        return new FloatColumnImpl(columnName);
    }

    @Override
    public DoubleColumn createDoubleColumn(String columnName) {
        assertNotNull(columnName);
        return new DoubleColumnImpl(columnName);
    }

    @Override
    public StringColumn createStringColumn(String columnName) {
        assertNotNull(columnName);
        return new StringColumnImpl(columnName);
    }

    @Override
    public DateColumn createDateColumn(String columnName) {
        assertNotNull(columnName);
        return new DateColumnImpl(columnName);
    }

    @Override
    public TimeColumn createTimeColumn(String columnName) {
        assertNotNull(columnName);
        return new TimeColumnImpl(columnName);
    }

    @Override
    public TimestampColumn createTimestampColumn(String columnName) {
        assertNotNull(columnName);
        return new TimestampColumnImpl(columnName);
    }

    private static void assertNotNull(String columnName) {
        if (columnName == null) {
            throw new IllegalArgumentException("Parameter columnName must not be null.");
        }
    }

	@Override
	public ClobColumn createClobColumn(String columnName) {
		 assertNotNull(columnName);
	     return new ClobColumnImpl(columnName);
	}

}
