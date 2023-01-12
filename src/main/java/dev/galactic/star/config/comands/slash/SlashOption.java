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

import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Config class so SnakeYAML can parse the options of the slash command
 */
public class SlashOption {
    private String name;
    private String description;
    private boolean required;
    private String type;
    private List<SlashChoice> choices;

    /**
     * Converts the data in this class to a usable OptionData object in the slash command building
     *
     * @return OptionData object
     * @see net.dv8tion.jda.api.interactions.commands.build.OptionData
     */
    public OptionData toData() {
        OptionData data = new OptionData(OptionType.valueOf(this.type.toUpperCase()), this.name, this.description,
                this.required
        );
        if (this.choices != null) {
            data.addChoices(this.choices.stream().map(SlashChoice::toData).collect(Collectors.toList()));
        }
        return data;
    }

    /**
     * Getter for List&gt;SlashChoice&lt;
     *
     * @return List&gt;SlashChoice&lt;
     * @see SlashChoice
     */
    public List<SlashChoice> getChoices() {
        return choices;
    }


    /**
     * Setter for the list of choices
     *
     * @param choices List&gt;SlashPrivilege&lt;
     * @see SlashChoice
     */
    public void setChoices(List<SlashChoice> choices) {
        this.choices = choices;
    }


    /**
     * Getter for option name
     *
     * @return String name
     */
    public String getName() {
        return name;
    }


    /**
     * Setter for the list of option name
     *
     * @param name String name
     */
    public void setName(String name) {
        this.name = name;
    }


    /**
     * Getter for option description
     *
     * @return String description
     */
    public String getDescription() {
        return description;
    }


    /**
     * Setter for the option's description
     *
     * @param description Boolean
     */
    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * Getter for whether it is a required option
     *
     * @return Boolean true/false
     */
    public boolean isRequired() {
        return required;
    }


    /**
     * Setter for whether it is required or not
     *
     * @param required Boolean
     */
    public void setRequired(boolean required) {
        this.required = required;
    }


    /**
     * Getter for option type
     *
     * @return String type
     */
    public String getType() {
        return type;
    }


    /**
     * Setter for the option type
     *
     * @param type String option type
     */
    public void setType(String type) {
        this.type = type;
    }
}
