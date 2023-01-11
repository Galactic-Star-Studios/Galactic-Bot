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

package dev.galactic.star.config;

/**
 * Configuration class so SnakeYAML can parse and store the data
 */
public class RolesAndIdsConfig {
    private String name;
    private String type;
    private Object id;

    /**
     * Getter for name
     *
     * @return Name String
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for name
     *
     * @param name Name as String
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for type of id it is
     *
     * @return String type
     */
    public String getType() {
        return type;
    }

    /**
     * Setter for the type
     *
     * @param type Type of id it is
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Getter for the id.
     *
     * @return The id as an object
     */
    public Object getId() {
        return id;
    }

    /**
     * Setter for the ID
     *
     * @param id The id as an integer or String
     */
    public void setId(Object id) {
        this.id = id;
    }
}
