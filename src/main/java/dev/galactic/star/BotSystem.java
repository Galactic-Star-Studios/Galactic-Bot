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
import net.dv8tion.jda.api.entities.channel.Channel;

import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * The main class that has all the configuration classes for the bot.
 */
public class BotSystem {

    private static BotSystem INSTANCE;
    private Guild guild;
    //Takes 2 Strings, first one is the name, the second one is the description
    private HashMap<String, String> systemCommands = new HashMap<>();
    private SystemConfig systemConfig;
    private Configuration configurations;
    private H2Database db;
    private ScheduledFuture<?> commandListener;
    //This is irrelevant. Just keep it here for now
    private ScheduledFuture<?> dateListener;
    private Channel logChannel;

    /**
     * Used to set the guild so that it can be used at other times
     *
     * @param guild Guild object
     */
    public BotSystem(Guild guild) {
        INSTANCE = this;
        this.guild = guild;
        this.configurations = new Configuration();
    }

    /**
     * Sets the static INSTANCE field to the current instance of the class and sets the configurations field to a new
     * instance of the Configuration class
     */
    public BotSystem() {
        INSTANCE = this;
        this.configurations = new Configuration();
    }

    /**
     * Starts the schedulers such as listening for commands in the console.
     */
    public static void startSchedulers() {
        BotSystem.getInstance().setCommandListener(Scheduler.listenForCommands());
        //BotSystem.getInstance().setDateListener(Scheduler.dateListener());
    }

    /**
     * Starts/stops schedulers
     */
    public static void reloadSchedulers() {
        getInstance().commandListener.cancel(true);
        startSchedulers();
    }

    /**
     * Returns a singleton instance of BytSystem
     *
     * @return BotSystem class instance
     */
    public static BotSystem getInstance() {
        return INSTANCE;
    }

    /**
     * Places the current system commands into the hashmap so that it can be used in the printHelp method
     */
    public void loadSystemCommands() {
        this.systemCommands.put("exit|stop", "Stop and close the connection of the bot.");
        this.systemCommands.put("info|i", "Get information about the bot.");
        this.systemCommands.put("reload|rl", "Reloads the configuration and bot itself.");
    }

    /**
     * Loads and generates the configurations files
     */
    public void loadConfigurations() {
        GalacticBot.getBot().getLogger().info("Loading Configurations...");
        this.configurations.generateFiles();
        this.systemConfig = this.configurations.getSystemConfig();
        GalacticBot.getBot().getLogger().info("Loaded Configurations.");
    }

    /**
     * Getter for the system commands
     *
     * @return HashMap&lt;String, String*gt; name/aliases and the description of them
     */
    public HashMap<String, String> getSystemCommands() {
        return systemCommands;
    }

    /**
     * Sets the hashmap of commands so the help command can print them
     *
     * @param systemCommands HashMap&lt;String, String&gt; name/aliases and the description of them
     */
    public void setSystemCommands(HashMap<String, String> systemCommands) {
        this.systemCommands = systemCommands;
    }

    /**
     * Getter for the System config file
     *
     * @return SystemConfig instance
     */
    public SystemConfig getSystemConfig() {
        return systemConfig;
    }

    /**
     * Setter for the System config file
     *
     * @param systemConfig The System config class
     */
    public void setSystemConfig(SystemConfig systemConfig) {
        this.systemConfig = systemConfig;
    }

    /**
     * Getter for Configuration class
     *
     * @return Configuration instance
     */
    public Configuration getConfigurations() {
        return configurations;
    }

    /**
     * Sets the configurations
     *
     * @param configurations Configuration Object from SnakeYAML
     */
    public void setConfigurations(Configuration configurations) {
        this.configurations = configurations;
    }

    /**
     * Getter for JDA Guild class
     *
     * @return Guild instance
     */
    public Guild getGuild() {
        return guild;
    }

    /**
     * Setter for Guild
     *
     * @param guild Object to set to guild
     */
    public void setGuild(Guild guild) {
        this.guild = guild;
    }

    /**
     * Getter for the database instance
     *
     * @return H2Database instance
     */
    public H2Database getDb() {
        return db;
    }

    /**
     * Setter for the database instance
     *
     * @param db Database instance to set
     */
    public void setDb(H2Database db) {
        this.db = db;
    }

    /**
     * Returns the scheduler that listens for commands
     *
     * @return ScheduledFuture
     */
    public ScheduledFuture<?> getCommandListener() {
        return commandListener;
    }

    /**
     * Setter for the command listener schedulers
     *
     * @param commandListener ScheduledFuture instance to set
     */
    public void setCommandListener(ScheduledFuture<?> commandListener) {
        this.commandListener = commandListener;
    }

    /**
     * Getter for the Date Scheduler
     *
     * @return ScheduledFuture instance
     */
    public ScheduledFuture<?> getDateListener() {
        return dateListener;
    }

    /**
     * Setter for Date Scheduler NOTE: DON'T WORRY ABOUT THIS, MIGHT BE USED FOR FUTURE FEATURES
     *
     * @param dateListener SchedulerFuture listener
     */
    public void setDateListener(ScheduledFuture<?> dateListener) {
        this.dateListener = dateListener;
    }

    /**
     * Getter for the logs channel in your discord server
     *
     * @return Log channel instance
     */
    public Channel getLogChannel() {
        return logChannel;
    }

    /**
     * Setter for the log channel
     *
     * @param logChannel Channel instance to set. Must be a TextChannel
     */
    public void setLogChannel(Channel logChannel) {
        this.logChannel = logChannel;
    }
}
