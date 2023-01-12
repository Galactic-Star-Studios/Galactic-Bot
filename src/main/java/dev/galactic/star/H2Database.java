/*
 * Copyright 2022-2022 Galactic Star Studios
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

package dev.galactic.star;

import org.h2.jdbcx.JdbcDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

/**
 * The main class that is used to  handle all interactions with the database
 */
public class H2Database {
    private final Connection connection;

    /**
     * Used to connect to the h2 database
     *
     * @param user Your username
     * @param pass Your password
     */
    public H2Database(String user, String pass) {
        JdbcDataSource src = new JdbcDataSource();
        src.setUser(user);
        src.setPassword(pass);
        src.setUrl("jdbc:h2:./Database");
        try {
            this.connection = src.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        this.createTables();
    }

    /**
     * Checks whether the ban id exists
     *
     * @param id Ban ID
     * @return True/False
     */
    public boolean banIdExists(String id) {
        return getFromDb("ban_data", "ban_id", id, "ban_id") != null;
    }

    /**
     * Creates the tables in the database
     */
    private void createTables() {
        this.createTable("user_data", new String[]{"userid VARCHAR(20) NOT NULL PRIMARY KEY", "total_warns INT",
                "severity INT"});
        this.createTable("warn_history", new String[]{"userid VARCHAR(20) NOT NULL", "warn_id VARCHAR(10) NOT NULL " +
                "PRIMARY KEY", "severity INT"});
    }

    /**
     * Creates the table individually
     *
     * @param tableName Table nane
     * @param columns   Array of column names
     */
    private void createTable(String tableName, String[] columns) {
        String columnData = Arrays.toString(columns)
                .replaceAll("[\\[\\]]", "");
        try (PreparedStatement table = this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + tableName +
                "(" + columnData + ");")) {
            table.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks whether the mute id exists
     *
     * @param id Ban ID
     * @return True/False
     */
    public boolean muteIdExists(String id) {
        return this.getFromDb("warn_history", "warn_id", id, "warn_id") != null;
    }

    /**
     * Inserts into the table's columns
     *
     * @param table   Name of the table
     * @param columns Array of the columns that you are going to insert into
     * @param data    Array of Objects that are going to be inserted into the columns specified. It needs to be in
     *                the same order as the columns:
     *                Columns: {"Name", "Age", "Height"}
     *                Data: {"Jake", 15, 5.4}
     *                Data can't be like: {15, "Jake", 5.4}
     * @@return H2Databases so it can be used in a chain
     */
    public H2Database insert(String table, String[] columns, Object[] data) {
        String dataStr = Arrays.toString(data).replaceAll("[\\[\\]]", "").replaceAll(",", "','");
        String columnStr = Arrays.toString(columns).replaceAll("[\\[\\]]", "");
        try (PreparedStatement stmt =
                     this.connection.prepareStatement("INSERT INTO " + table + "(" + columnStr + ") VALUES('" + dataStr + "');")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * Deletes the specified row based on the object value of the column
     *
     * @param table    Table name
     * @param key      Primary key
     * @param keyValue Value to search for
     * @param column   Column name
     * @param value    Value to find and delete
     * @return Current instance of the class
     */
    public H2Database delete(String table, String key, Object keyValue, String column, Object value) {
        try (PreparedStatement stmt = this.connection.prepareStatement("DELETE FROM " + table + " WHERE " + key +
                "='" + keyValue + "' AND WHERE " + key + "='" + keyValue + "';")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * Updates a record
     *
     * @param table    Table name
     * @param key      Primary key
     * @param keyValue Value to search for
     * @param column   Column name
     * @param value    Object value to set to
     * @return Current instance of the class
     */
    public H2Database updateRecord(String table, String key, Object keyValue, String column, Object value) {
        try (PreparedStatement stmt =
                     this.connection.prepareStatement("UPDATE " + table + " SET " + column + "='" + value + "' WHERE "
                             + key + "='" + keyValue + "';")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    /**
     * Returns a value from the table
     *
     * @param table       Table name
     * @param key         Primary key
     * @param keyValue    Value to search for
     * @param columnToGet Column name to get
     * @return Object, if it finds nothing, returns null
     */
    public Object getFromDb(String table, String key, Object keyValue, String columnToGet) {
        try (PreparedStatement stmt = this.connection.prepareStatement("SELECT * FROM " + table + " WHERE " + key +
                "='" + keyValue + "';");
             ResultSet set = stmt.executeQuery()) {
            return set.next() ? set.getObject(columnToGet) : null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the warn severity of a user
     *
     * @param userId String version of the user's Discord id
     * @return Amount of severity the user has
     */
    public int getWarnSeverity(String userId) {
        return Integer.parseInt(this.getFromDb("warn_data", "user_id", userId, "severity").toString());
    }

    /**
     * Gets the total warns a user has
     *
     * @param userId String version of the user's Discord id
     * @return Amount of warns the user has
     */
    public int getTotalWarns(String userId) {
        return Integer.parseInt(this.getFromDb("warn_data", "user_id", userId, "total_warns").toString());
    }

    /**
     * @param userId String version of the user's Discord id
     * @return userId Amount of warns the user has
     */
    public boolean memberIsPunished(String userId) {
        return this.getFromDb("warn_data", "user_id", userId, "user_id") != null;
    }

    /**
     * Checks whether there is a connection to the db or not
     *
     * @return True or False
     */
    public boolean isConnected() {
        try {
            return this.connection != null && !this.connection.isClosed();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Disconnects the database
     */
    public void disconnect() {
        try {
            if (this.connection != null && this.isConnected()) {
                this.connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
