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

import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.List;

/**
 * Config class so SnakeYAML can parse the sub commands
 */
public class SlashSubCommand {
    private String name;
    private String description;
    private List<SlashOption> options;

    /**
     * Converts the data in this class to a usable SubCommandData object in the slash command building
     *
     * @return SubCommandData object
     * @see SubcommandData
     */
    public SubcommandData toData() {
        SubcommandData data = new SubcommandData(this.name, this.description);
        if (this.options != null) {
            data.addOptions(this.options.stream().map(SlashOption::toData).toList());
        }
        return data;
    }


    /**
     * Getter for sub command name
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
     * Getter for sub command description
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
     * Getter for List&gt;SlashOptions&lt;
     *
     * @return List&gt;SlashOptiond&lt;
     * @see SlashOption
     */
    public List<SlashOption> getOptions() {
        return options;
    }

    /**
     * Setter for
     *
     * @param
     */
    public void setOptions(List<SlashOption> options) {
        this.options = options;
    }
}
