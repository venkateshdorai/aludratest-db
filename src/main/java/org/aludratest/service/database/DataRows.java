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

/** Interface of a result of a database query. It offers the total number of rows (maybe zero) and an iterator over all returned
 * rows, in the order they have been returned by the database.
 * 
 * @author falbrech */
public interface DataRows extends Iterable<DataRow> {

    /** Returns the number of rows contained in this object.
     * 
     * @return The number of rows contained in this object. */
    int getRowCount();

}
