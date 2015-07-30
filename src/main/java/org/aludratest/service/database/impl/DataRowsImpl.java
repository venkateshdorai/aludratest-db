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

import java.sql.SQLException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import javax.sql.rowset.CachedRowSet;

import org.aludratest.service.database.DataRow;
import org.aludratest.service.database.DataRows;
import org.aludratest.service.database.DatabaseService;
import org.aludratest.service.database.tablecolumn.TableColumn;

public class DataRowsImpl implements DataRows {

    private CachedRowSet cachedRowSet;

    private int rowCount;

    public DataRowsImpl(CachedRowSet cachedRowSet) throws SQLException {
        this.cachedRowSet = cachedRowSet;
        rowCount = cachedRowSet.size();
    }

    public CachedRowSet getCachedRowSet() {
        return cachedRowSet;
    }

    @Override
    public Iterator<DataRow> iterator() {
        return new DataRowIterator();
    }

    @Override
    public int getRowCount() {
        return rowCount;
    }

    private class DataRowIterator implements Iterator<DataRow> {

        private int rowNum;

        @Override
        public boolean hasNext() {
            return rowNum < rowCount;
        }

        @Override
        public DataRow next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            return new DataRowImpl(++rowNum);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

    private class DataRowImpl implements DataRow {

        private int rowNum;

        public DataRowImpl(int rowNum) {
            this.rowNum = rowNum;
        }

        @Override
        public <T> T getValue(DatabaseService db, TableColumn<T> column) throws NoSuchElementException {
            return db.perform().getColumnValue(DataRowsImpl.this, rowNum, column);
        }
    }

}
