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

import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.List;

/**
 * Config class so SnakeYAML can parse the slash commands
 */
public class SlashCommand {
    private String name;
    private String description;
    private boolean enabledByDefault = true;
    private List<SlashPrivilege> privileges;
    private List<SlashOption> options;
    private List<SlashSubCommand> subCommands;
    private List<SlashSubCommandGroup> subCommandGroups;
    private String handler;

    /**
     * Converts the data in this class to a usable SlashCommandData object in the slash command building
     *
     * @return SlashCommandData object
     * @see SlashCommandData
     */

    public SlashCommandData toData() {
        SlashCommandData data = Commands.slash(this.name, this.description);
        if (this.options != null) {
            data.addOptions(this.options.stream().map(SlashOption::toData).toList());
        }
        if (this.subCommands != null) {
            data.addSubcommands(this.subCommands.stream().map(SlashSubCommand::toData).toList());
        }
        if (this.subCommandGroups != null) {
            data.addSubcommandGroups(this.subCommandGroups.stream().map(SlashSubCommandGroup::toData).toList());
        }
        return data;
    }

    /**
     * Getter for List&gt;SlashSubCommandGroup&lt;
     *
     * @return List&gt;SlashSubCommandGroup&lt;
     * @see SlashSubCommandGroup
     */
    public List<SlashSubCommandGroup> getSubCommandGroups() {
        return subCommandGroups;
    }


    /**
     * Setter for sub command group list
     *
     * @param subCommandGroups List&gt;SlashSubCommandGroup&lt;
     */
    public void setSubCommandGroups(List<SlashSubCommandGroup> subCommandGroups) {
        this.subCommandGroups = subCommandGroups;
    }

    /**
     * Getter for List&gt;SlashSubCommand&lt;
     *
     * @return List&gt;SlashSubCommand&lt;
     * @see SlashSubCommand
     */
    public List<SlashSubCommand> getSubCommands() {
        return subCommands;
    }


    /**
     * Setter for List&gt;SlashSubCommand&lt;
     *
     * @param subCommands List&gt;SlashSubCommand&lt;
     */
    public void setSubCommands(List<SlashSubCommand> subCommands) {
        this.subCommands = subCommands;
    }

    /**
     * Getter for List&gt;SlashOptions&lt;
     *
     * @return List&gt;SlashOptions&lt;
     * @see SlashOption
     */
    public List<SlashOption> getOptions() {
        return options;
    }


    /**
     * Setter for List&gt;SlashOptions&lt;
     *
     * @param options List&gt;SlashOptions&lt;
     */
    public void setOptions(List<SlashOption> options) {
        this.options = options;
    }


    /**
     * Getter for the handler path
     *
     * @return String handler path
     */
    public String getHandler() {
        return handler;
    }


    /**
     * Setter for command handler
     *
     * @param handler String command handler
     */
    public void setHandler(String handler) {
        this.handler = handler;
    }


    /**
     * Getter for slash command name
     *
     * @return String name
     */
    public String getName() {
        return name;
    }


    /**
     * Setter for slash command name
     *
     * @param name String command name
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Getter for command description
     *
     * @return String command description
     */
    public String getDescription() {
        return description;
    }


    /**
     * Setter for
     *
     * @param
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * Getter for whether it is enabld by default
     *
     * @return Boolea true/false
     */
    public boolean isEnabledByDefault() {
        return enabledByDefault;
    }


    /**
     * Setter for
     *
     * @param
     */
    public void setEnabledByDefault(boolean enabledByDefault) {
        this.enabledByDefault = enabledByDefault;
    }

    /**
     * Getter for List&gt;SlashPrivilege&lt;
     *
     * @return List&gt;SlashPrivilege&lt;
     * @see SlashSubCommand
     */
    public List<SlashPrivilege> getPrivileges() {
        return privileges;
    }


    /**
     * Setter for
     *
     * @param
     */
    public void setPrivileges(List<SlashPrivilege> privileges) {
        this.privileges = privileges;
    }
}
