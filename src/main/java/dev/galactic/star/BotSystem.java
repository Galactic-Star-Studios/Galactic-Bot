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

package dev.galactic.star;

import dev.galactic.star.config.Configuration;
import dev.galactic.star.config.system.SystemConfig;
import net.dv8tion.jda.api.entities.Guild;

import java.util.HashMap;

public class BotSystem {

    private static BotSystem INSTANCE;
    private Guild guild;
    private HashMap<String, String> systemCommands = new HashMap<>();
    private SystemConfig systemConfig;
    private Configuration configurations = new Configuration();

    public BotSystem(Guild guild) {
        INSTANCE = this;
        this.guild = guild;
    }

    public BotSystem() {
        INSTANCE = this;
    }

    public static BotSystem getInstance() {
        return INSTANCE;
    }

    //Places the current system commands into hte hashmap so that it can be used in the printHelp method
    public void loadSystemCommands() {
        this.systemCommands.put("exit|stop", "Stop and close the connection of the bot.");
        this.systemCommands.put("info|i", "Get information about the bot.");
        this.systemCommands.put("reload|rl", "Reloads the configuration and bot itself.");
    }

    public void registerCommands() {
    }

    public void loadConfigurations() {
        GalacticBot.getBot().getLogger().info("Loading Configurations...");
        this.configurations.generateFiles();
        this.systemConfig = this.configurations.getSystemConfig();
        GalacticBot.getBot().getLogger().info("Loaded Configurations.");
    }


    public HashMap<String, String> getSystemCommands() {
        return systemCommands;
    }

    public void setSystemCommands(HashMap<String, String> systemCommands) {
        this.systemCommands = systemCommands;
    }

    public SystemConfig getSystemConfig() {
        return systemConfig;
    }

    public void setSystemConfig(SystemConfig systemConfig) {
        this.systemConfig = systemConfig;
    }

    public Configuration getConfigurations() {
        return configurations;
    }

    public void setConfigurations(Configuration configurations) {
        this.configurations = configurations;
    }

    public Guild getGuild() {
        return guild;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
    }
}
