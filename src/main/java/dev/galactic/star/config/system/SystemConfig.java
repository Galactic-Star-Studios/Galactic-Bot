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

package dev.galactic.star.config.system;

import dev.galactic.star.actions.ConstantInfo;

public class SystemConfig {
    private String token;
    private int max_severity;
    private String guild_id;
    private String invite_link;
    private boolean for_guild;
    private boolean del_cmd_on_reload;
    private DatabaseConfig database;
    private SystemActivity activity;
    private String online_status;

    public String getInvite_link() {
        return invite_link;
    }

    public void setInvite_link(String invite_link) {
        ConstantInfo.inviteLink = invite_link;
        this.invite_link = invite_link;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public SystemActivity getActivity() {
        return activity;
    }

    public void setActivity(SystemActivity activity) {
        this.activity = activity;
    }

    public String getOnline_status() {
        return online_status;
    }

    public void setOnline_status(String online_status) {
        this.online_status = online_status;
    }

    public String getGuild_id() {
        return guild_id;
    }

    public void setGuild_id(String guild_id) {
        this.guild_id = guild_id;
    }

    public boolean isFor_guild() {
        return for_guild;
    }

    public void setFor_guild(boolean for_guild) {
        this.for_guild = for_guild;
    }

    /**
     * Boolean whether the bot should delete all commands from the server when
     *
     * @return
     */
    public boolean isDel_cmd_on_reload() {
        return del_cmd_on_reload;
    }

    public void setDel_cmd_on_reload(boolean del_cmd_on_reload) {
        this.del_cmd_on_reload = del_cmd_on_reload;
    }

    public DatabaseConfig getDatabase() {
        return database;
    }

    public void setDatabase(DatabaseConfig database) {
        this.database = database;
    }

    public int getMax_severity() {
        return max_severity;
    }

    public void setMax_severity(int max_severity) {
        this.max_severity = max_severity;
    }
}
