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

import dev.galactic.star.BotSystem;
import net.dv8tion.jda.api.interactions.commands.privileges.IntegrationPrivilege;
import net.dv8tion.jda.api.interactions.commands.privileges.IntegrationPrivilege.Type;

/**
 * Config class so SnakeYAML can parse the privileges
 */
public class SlashPrivilege {
    private String type;
    private boolean enabled = true;
    private String id;

    /**
     * Converts the data in this class to a usable IntegrationPrivilege object in the slash command building
     *
     * @return IntegrationPrivilege object
     * @see IntegrationPrivilege
     */
    public IntegrationPrivilege toData() {
        return new IntegrationPrivilege(BotSystem.getInstance().getGuild(), Type.valueOf(this.type.toUpperCase()),
                this.enabled, Long.parseLong(this.id)
        );
    }

    /**
     * Getter for privilege type
     *
     * @return String type
     * @see IntegrationPrivilege.Type
     */
    public String getType() {
        return type;
    }

    /**
     * Setter for
     *
     * @param
     */
    public void setType(String type) {
        this.type = type;
    }


    /**
     * Getter for privilege id
     *
     * @return String id
     */
    public String getId() {
        return id;
    }


    /**
     * Setter for
     *
     * @param
     */
    public void setId(String id) {
        this.id = id;
    }


    /**
     * Getter for whether it is enabled
     *
     * @return Boolean true/false
     */
    public boolean isEnabled() {
        return enabled;
    }


    /**
     * Setter for
     *
     * @param
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
