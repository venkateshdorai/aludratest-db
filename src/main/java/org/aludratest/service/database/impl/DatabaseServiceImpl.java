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
package org.aludratest.service.database.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.aludratest.config.Preferences;
import org.aludratest.exception.AccessFailure;
import org.aludratest.exception.AutomationException;
import org.aludratest.service.AbstractConfigurableAludraService;
import org.aludratest.service.database.DatabaseCondition;
import org.aludratest.service.database.DatabaseInteraction;
import org.aludratest.service.database.DatabaseService;
import org.aludratest.service.database.DatabaseVerification;
import org.aludratest.service.database.tablecolumn.TableColumnFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseServiceImpl extends AbstractConfigurableAludraService implements DatabaseService {

    private DatabaseActionImpl actions;

    private Connection connection;

    private DatabaseConfiguration config;

    private TableColumnFactoryImpl factory = new TableColumnFactoryImpl();

    private Logger LOG = LoggerFactory.getLogger(DatabaseService.class);

    @Override
    public String getDescription() {
        return "Database connection to " + config.getJdbcUrl();
    }

    @Override
    public void close() {
        try {
            connection.close();
        }
        catch (Exception e) {
            LOG.info("Exception when closing database connection", e);
        }
    }

    @Override
    public String getPropertiesBaseName() {
        return "database";
    }

    @Override
    public void configure(Preferences preferences) {
        config = new DatabaseConfiguration(preferences);
        // assert JDBC URL is configured
        config.getJdbcUrl();

        // check driver class name for validness
        try {
            Class.forName(config.getJdbcDriverClass());
        }
        catch (Exception e) {
            throw new AutomationException("Could not find or initialize JDBC Driver Class", e);
        }
    }

    @Override
    public void initService() {
        try {
            if (config.getUser() != null) {
                connection = DriverManager.getConnection(config.getJdbcUrl(), config.getUser(), config.getPassword());
            }
            else {
                connection = DriverManager.getConnection(config.getJdbcUrl());
            }
        }
        catch (SQLException e) {
            throw new AccessFailure("Could not connect to database", e);
        }
    }

    @Override
    public DatabaseInteraction perform() {
        return getActions();
    }

    @Override
    public DatabaseVerification verify() {
        return getActions();
    }

    @Override
    public DatabaseCondition check() {
        return getActions();
    }

    @Override
    public TableColumnFactory getTableColumnFactory() {
        return factory;
    }

    private DatabaseActionImpl getActions() {
        if (actions == null) {
			actions = new DatabaseActionImpl(connection, config);
        }

        return actions;
    }

}
