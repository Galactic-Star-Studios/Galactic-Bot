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

package dev.galactic.star.config.comands.slash;

import net.dv8tion.jda.api.interactions.commands.Command.Choice;

/**
 * Config class so SnakeYAML can parse the choices
 */
public class SlashChoice {
    private String name;
    private String value;


    /**
     * Converts the data in this class to a usable Choice object in the slash command building
     *
     * @return Choice object
     * @see Choice
     */
    public Choice toData() {
        return new Choice(this.name, this.value);
    }


    /**
     * Getter for choice name
     *
     * @return String name
     */
    public String getName() {
        return name;
    }


    /**
     * Setter for choice name
     *
     * @param name String name
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Getter for choice value
     *
     * @return String value
     */
    public String getValue() {
        return value;
    }

    /**
     * Setter for choice value
     *
     * @param value String value
     */
    public void setValue(String value) {
        this.value = value;
    }
}
