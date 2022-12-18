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

public class SlashOptions {
    private String name;
    private String description;
    private boolean required;
    private String type;
    private List<SlashChoices> choices;

    public OptionData toData() {
        OptionData data = new OptionData(OptionType.valueOf(this.type.toUpperCase()), this.name, this.description,
                this.required
        );
        //data.setAutoComplete(true);
        if (this.choices != null) {
            data.addChoices(this.choices.stream().map(SlashChoices::toData).collect(Collectors.toList()));
        }
        return data;
    }

    public List<SlashChoices> getChoices() {
        return choices;
    }

    public void setChoices(List<SlashChoices> choices) {
        this.choices = choices;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}