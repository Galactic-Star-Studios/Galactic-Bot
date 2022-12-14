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

import java.util.List;

public class SlashCommand {
    private String name;
    private String description;
    private boolean enabledByDefault;
    private List<SlashPrivilege> privileges;
    private List<SlashOptions> options;
    private List<SlashSubCommand> subCommands;
    private String handler;

    public List<SlashSubCommand> getSubCommands() {
        return subCommands;
    }

    public void setSubCommands(List<SlashSubCommand> subCommands) {
        this.subCommands = subCommands;
    }

    public List<SlashOptions> getOptions() {
        return options;
    }

    public void setOptions(List<SlashOptions> options) {
        this.options = options;
    }

    public String getHandler() {
        return handler;
    }

    public void setHandler(String handler) {
        this.handler = handler;
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

    public boolean isEnabledByDefault() {
        return enabledByDefault;
    }

    public void setEnabledByDefault(boolean enabledByDefault) {
        this.enabledByDefault = enabledByDefault;
    }

    public List<SlashPrivilege> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(List<SlashPrivilege> privileges) {
        this.privileges = privileges;
    }
}
