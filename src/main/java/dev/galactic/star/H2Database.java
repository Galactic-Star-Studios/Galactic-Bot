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

public class H2Database {
    private final Connection connection;

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

    private void createTables() {
        try (PreparedStatement table = this.connection.prepareStatement("CREATE TABLE IF NOT EXISTS warn_data(user_id" +
                " VARCHAR(20) NOT NULL PRIMARY KEY," +
                "total_warns INT," +
                "severity INT" +
                ");")) {
            table.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void insert(String table, String[] columns, Object[] data) {
        String dataStr = Arrays.toString(data).replaceAll("[\\[\\]]", "").replaceAll(",", "','");
        String columnStr = Arrays.toString(columns).replaceAll("[\\[\\]]", "");
        try (PreparedStatement stmt =
                     this.connection.prepareStatement("INSERT INTO " + table + "(" + columnStr + ") VALUES('" + dataStr + "');")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void delete(String table, String column, Object value) {
        try (PreparedStatement stmt = this.connection.prepareStatement("DELETE FROM " + table + " WHERE " + column +
                "=" + value + ";")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateRecord(String table, String key, String column, Object value) {
        try (PreparedStatement stmt = this.connection.prepareStatement("")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Object getFromDb(String table, String key, Object keyObj, String columnToGet) {
        try (PreparedStatement stmt = this.connection.prepareStatement("SELECT * FROM " + table + " WHERE " + key +
                "='" + keyObj + "';");
             ResultSet set = stmt.executeQuery()) {
            return set.next() ? set.getObject(columnToGet) : null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int getWarnSeverity(String userId) {
        return Integer.parseInt(this.getFromDb("warn_data", "user_id", userId, "severity").toString());
    }

    public int getTotalWarns(String userId) {
        return Integer.parseInt(this.getFromDb("warn_data", "user_id", userId, "total_warns").toString());
    }

    public boolean memberExists(String userId) {
        return this.getFromDb("warn_data", "user_id", userId, "user_id") != null;
    }

    public boolean isConnected() {
        try {
            return this.connection != null && !this.connection.isClosed();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void disconnect() {
        try {
            if (this.isConnected()) {
                this.connection.close();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
