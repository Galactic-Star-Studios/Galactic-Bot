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

package dev.galactic.star.config.comands.context;

import net.dv8tion.jda.api.interactions.commands.Command.Type;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;

/**
 * So SnakeYAML can parse the context command file
 */
public class ContextCommand {

    private String name;
    private String type;
    private String handler;

    /**
     * Converts the context command to Command Data
     *
     * @return CommandData
     * @see CommandData
     */
    public CommandData toData() {
        return Commands.context(Type.valueOf(this.type.toUpperCase()), this.name);
    }

    /**
     * Getter for the name
     *
     * @return Name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for name
     *
     * @param name String name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for the type of context command
     *
     * @return String type
     */
    public String getType() {
        return type;
    }

    /**
     * Setter for type
     *
     * @param type String type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Getter for the path to the handler
     *
     * @return String class path
     */
    public String getHandler() {
        return handler;
    }

    /**
     * Setter for the command handler
     *
     * @param handler String path to handler
     */
    public void setHandler(String handler) {
        this.handler = handler;
    }
}
