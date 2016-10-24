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

import org.aludratest.config.Preferences;
import org.aludratest.config.ValidatingPreferencesWrapper;

public class DatabaseConfiguration {

    private ValidatingPreferencesWrapper prefs;

    public DatabaseConfiguration(Preferences prefs) {
        this.prefs = new ValidatingPreferencesWrapper(prefs);
    }

    public String getJdbcUrl() {
        return prefs.getRequiredStringValue("jdbcUrl");
    }

    public String getUser() {
        return prefs.getStringValue("user");
    }

    public String getPassword() {
        return prefs.getStringValue("password");
    }

    public String getJdbcDriverClass() {
        return prefs.getRequiredStringValue("jdbcDriverClass");
    }

	public boolean isDmlEnabled() {
		return prefs.getBooleanValue("enable.dml");
	}

	public boolean isDdlEnabled() {
		return prefs.getBooleanValue("enable.ddl");
	}

	public int getVerifyWaitTimeout() {
		return prefs.getIntValue("verify.wait.timeout");
	}

	public int getVerifyWaitInterval() {
		return prefs.getIntValue("verify.wait.interval");
	}
}
