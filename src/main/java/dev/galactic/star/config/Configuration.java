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

package dev.galactic.star.config;

import com.google.gson.Gson;
import dev.galactic.star.BotSystem;
import dev.galactic.star.GalacticBot;
import dev.galactic.star.H2Database;
import dev.galactic.star.config.comands.context.ContextCommand;
import dev.galactic.star.config.comands.slash.SlashCommand;
import dev.galactic.star.config.system.DatabaseConfig;
import dev.galactic.star.config.system.SystemConfig;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

/**
 * Main configuration file that handles generating, parsing/reading
 */
public class Configuration {
    private static String dataPath;
    private static Configuration instance;
    private File systemConfigFile;
    private SystemConfig systemConfig;
    private List<SlashCommand> modCommandConfig;
    private List<SlashCommand> userCommandConfig;
    private List<ContextCommand> contextConfig;
    private List<RolesAndIdsConfig> rolesAndIdsConfigs;
    private File modCommandFile;
    private File userCommandFile;
    private File contextCommandsFile;
    private File rolesAndIds;
    private File avatarFile;
    private File h2Db;

    /**
     * Just so when it gets instantiated, it removes the last bit (The jar file's name) and replaces it with an empty
     * string
     */
    public Configuration() {
        instance = this;
        dataPath = GalacticBot.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String[] splitJsonPath = dataPath.split("/");
        Configuration.dataPath = dataPath.replace(splitJsonPath[splitJsonPath.length - 1], "");
    }

    /**
     * Returns the instance of the singleton class
     *
     * @return Singleton instance
     */
    public static Configuration getInstance() {
        return instance;
    }

    /**
     * Returns the String path to where the jar is executed
     *
     * @return String path
     */
    public static String getDataPath() {
        return dataPath;
    }

    /**
     * Connects to the database file
     */
    public void connectToDatabase() {
        GalacticBot.getBot().getLogger().info("Connecting to the H2Database...");
        DatabaseConfig config = this.systemConfig.getDatabase();
        BotSystem.getInstance().setDb(new H2Database(config.getUsername(), config.getPassword()));
        GalacticBot.getBot().getLogger().info("Connected!");
    }

    /**
     * Generating all files needed to work.
     */
    public void generateFiles() {
        this.initializeFiles();
        this.generateAvatarFile();
        this.generateAndReadSystemFile();
        this.generateAndReadCommandFiles();
    }

    /**
     * Generating System.json file
     */
    private void generateAndReadSystemFile() {
        this.generateSystemConfig();
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(this.systemConfigFile)) {
            this.systemConfig = null;
            this.systemConfig = gson.fromJson(reader, SystemConfig.class);
            GalacticBot.getBot().setup();
        } catch (IOException e) {
            GalacticBot.getBot().getLogger().error("Invalid System.json");
        }
    }

    /**
     * Generating the avatar files
     */
    private void generateAvatarFile() {
        try (InputStream in = GalacticBot.class.getResourceAsStream("/Avatar.png")) {
            Files.copy(in, this.avatarFile.toPath());
        } catch (IOException e) {
            //
        }
    }

    /**
     * Generates the system configuration file.
     */
    public void generateSystemConfig() {
        if (!systemConfigFile.exists()) {
            try (InputStream jsonIn = SystemConfig.class
                    .getClassLoader()
                    .getResourceAsStream("System.json")
            ) {
                Files.copy(jsonIn, systemConfigFile.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Assigning the above file objects to these
     */
    private void initializeFiles() {
        new File(Configuration.dataPath + "commands/").mkdirs();
        //this.h2Db = new File(Configuration.dataPath + "Database.h2/");
        this.systemConfigFile = new File(Configuration.dataPath + "System.json");
        this.avatarFile = new File(Configuration.dataPath + "Avatar.png");
        this.modCommandFile = new File(Configuration.dataPath + "commands/", "Mod.yml");
        this.userCommandFile = new File(Configuration.dataPath + "commands/", "User.yml");
        this.contextCommandsFile = new File(Configuration.dataPath + "commands/", "Context.yml");
        this.rolesAndIds = new File(Configuration.dataPath, "RolesAndIds.yml");
    }

    /**
     * Generating all files if it doesn't already exist and reading from them
     */
    private void generateAndReadCommandFiles() {
        this.generateCommandFiles();
        try (InputStream mod = new FileInputStream(this.modCommandFile);
             InputStream user = new FileInputStream(this.userCommandFile);
             InputStream context = new FileInputStream(this.contextCommandsFile);
             InputStream rolesIds = new FileInputStream(this.rolesAndIds)) {
            //this.h2Db.createNewFile();
            this.modCommandConfig = Arrays.asList(new Yaml(new Constructor(SlashCommand[].class)).load(mod));
            this.userCommandConfig = Arrays.asList(new Yaml(new Constructor(SlashCommand[].class)).load(user));
            this.contextConfig = Arrays.asList(new Yaml(new Constructor(ContextCommand[].class)).load(context));
            this.rolesAndIdsConfigs =
                    Arrays.asList(new Yaml(new Constructor(RolesAndIdsConfig[].class)).load(rolesIds));
        } catch (IOException e) {
            //
        }
    }

    /**
     * Generates the command configuration files
     */
    private void generateCommandFiles() {
        try (InputStream mod = SystemConfig.class
                .getClassLoader()
                .getResourceAsStream("commands/Mod.yml");
             InputStream user = SystemConfig.class
                     .getClassLoader()
                     .getResourceAsStream("commands/User.yml");
             InputStream context = SystemConfig.class
                     .getClassLoader()
                     .getResourceAsStream("commands/Context.yml");
             InputStream rolesIds = SystemConfig.class
                     .getClassLoader()
                     .getResourceAsStream("RolesAndIds.yml")) {
            Files.copy(mod, this.modCommandFile.toPath());
            Files.copy(user, this.userCommandFile.toPath());
            Files.copy(context, this.contextCommandsFile.toPath());
            Files.copy(rolesIds, this.rolesAndIds.toPath());
        } catch (IOException e) {
            //
        }
    }

    /**
     * Getter for System config file
     *
     * @return File
     */
    public File getSystemConfigFile() {
        return systemConfigFile;
    }

    /**
     * Setter for SystemConfig file
     *
     * @param systemConfigFile The file to set
     */
    public void setSystemConfigFile(File systemConfigFile) {
        this.systemConfigFile = systemConfigFile;
    }

    /**
     * Getter for SystemConfig class
     *
     * @return SystemConfig
     */
    public SystemConfig getSystemConfig() {
        return systemConfig;
    }

    /**
     * Setter for SystemConfig class
     *
     * @param systemConfig SystemConfig instance
     */
    public void setSystemConfig(SystemConfig systemConfig) {
        this.systemConfig = systemConfig;
    }

    /**
     * Getter for all of the moderator commands that are set in the Mod.yml
     *
     * @return List of SlashCommands
     */
    public List<SlashCommand> getModCommandConfig() {
        return modCommandConfig;
    }

    /**
     * Setter for the list of moderator commands
     *
     * @param modCommandConfig List of SlashCommand
     */
    public void setModCommandConfig(List<SlashCommand> modCommandConfig) {
        this.modCommandConfig = modCommandConfig;
    }

    /**
     * Getter for UserCommandConfig
     *
     * @return List of SlashCommand
     */
    public List<SlashCommand> getUserCommandConfig() {
        return userCommandConfig;
    }

    /**
     * Setter for the list of user commands
     *
     * @param userCommandConfig List of SlashCommand
     */
    public void setUserCommandConfig(List<SlashCommand> userCommandConfig) {
        this.userCommandConfig = userCommandConfig;
    }

    /**
     * Getter for the ContextConfig class
     *
     * @return List of ContextCommand
     */
    public List<ContextCommand> getContextConfig() {
        return contextConfig;
    }

    /**
     * Setter for the list of context commands
     *
     * @param userCommandConfig List of ContextCommand
     */
    public void setContextConfig(List<ContextCommand> contextConfig) {
        this.contextConfig = contextConfig;
    }


    /**
     * Setter for the list of roles and ids from the config
     *
     * @return List of roles and ids
     */
    public List<RolesAndIdsConfig> getRolesAndIdsConfigs() {
        return rolesAndIdsConfigs;
    }


    /**
     * Setter for the list of roles and ids
     *
     * @param userCommandConfig List of RolesAndIdsConfig
     */
    public void setRolesAndIdsConfigs(List<RolesAndIdsConfig> rolesAndIdsConfigs) {
        this.rolesAndIdsConfigs = rolesAndIdsConfigs;
    }

    /**
     * Getter for the Moderator command file
     *
     * @return Mod Command File
     */
    public File getModCommandFile() {
        return modCommandFile;
    }

    /**
     * Setter for Moderator command file
     *
     * @param modCommandFile The moderator command file
     */
    public void setModCommandFile(File modCommandFile) {
        this.modCommandFile = modCommandFile;
    }

    /**
     * Getter for the user command file
     *
     * @return User Command File
     */
    public File getUserCommandFile() {
        return userCommandFile;
    }

    /**
     * Setter for user command file
     *
     * @param modCommandFile The user command file
     */
    public void setUserCommandFile(File userCommandFile) {
        this.userCommandFile = userCommandFile;
    }

    /**
     * Getter for the context command file
     *
     * @return Context Command File
     */
    public File getContextCommandsFile() {
        return contextCommandsFile;
    }

    /**
     * Setter for Context Command file
     *
     * @param contextCommandsFile Context Command File
     */
    public void setContextCommandsFile(File contextCommandsFile) {
        this.contextCommandsFile = contextCommandsFile;
    }

    /**
     * Getter for the roles and ids file
     *
     * @return RolesAndIds File
     */
    public File getRolesAndIds() {
        return rolesAndIds;
    }

    /**
     * Setter for roles and ids file
     *
     * @param rolesAndIds Roles and ids file
     */
    public void setRolesAndIds(File rolesAndIds) {
        this.rolesAndIds = rolesAndIds;
    }

    /**
     * Getter for Avatar image file
     *
     * @return Avatar image file
     */
    public File getAvatarFile() {
        return avatarFile;
    }

    /**
     * Setter for Avatar image file
     *
     * @param avatarFile Avatar file
     */
    public void setAvatarFile(File avatarFile) {
        this.avatarFile = avatarFile;
    }

    /**
     * Getter for the H2 Database file
     *
     * @return Database File
     */
    public File getH2Db() {
        return h2Db;
    }

    /**
     * Setter for the H2 Database file
     *
     * @param h2Db Database File
     */
    public void setH2Db(File h2Db) {
        this.h2Db = h2Db;
    }
}
