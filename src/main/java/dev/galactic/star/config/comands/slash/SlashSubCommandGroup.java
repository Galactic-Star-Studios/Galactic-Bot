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

import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;

import java.util.Arrays;

/**
 * Config class so SnakeYAML can parse the sub command groups
 */
public class SlashSubCommandGroup {
    private String name;
    private String description;
    private SlashSubCommand[] subCommands;

    /**
     * Converts the data in this class to a usable SubCommandGroupData object in the slash command building
     *
     * @return SubCommandGroupData object
     * @see SubcommandGroupData
     */
    public SubcommandGroupData toData() {
        SubcommandGroupData data = new SubcommandGroupData(this.name, this.description);
        if (this.subCommands != null) {
            data.addSubcommands(Arrays.stream(subCommands).map(SlashSubCommand::toData).toList());
        }
        return data;
    }

    /**
     * Getter for sub command group name
     *
     * @return String name
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for
     *
     * @param
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for sub command group description
     *
     * @return String description
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
     * Getter for sub command array
     *
     * @return SlashSubCommand[]
     * @see SlashSubCommand
     */
    public SlashSubCommand[] getSubCommands() {
        return subCommands;
    }

    /**
     * Setter for
     *
     * @param
     */
    public void setSubCommands(SlashSubCommand[] subCommands) {
        this.subCommands = subCommands;
    }
}
