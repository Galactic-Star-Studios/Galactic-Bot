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
import dev.galactic.star.GalacticBot;
import dev.galactic.star.config.comands.context.ContextCommand;
import dev.galactic.star.config.comands.slash.SlashCommand;
import dev.galactic.star.config.system.SystemConfig;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

public class Configuration {
    public static String dataPath;
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

    //Just so when it gets instantiated, it removes the last bit (The jar file's name) and replaces it with an empty
    // string
    public Configuration() {
        instance = this;
        dataPath = GalacticBot.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String[] splitJsonPath = dataPath.split("/");
        Configuration.dataPath = dataPath.replace(splitJsonPath[splitJsonPath.length - 1], "");
        this.generateFiles();
    }

    public static Configuration getInstance() {
        return instance;
    }

    public static void setInstance(Configuration instance) {
        Configuration.instance = instance;
    }

    public static String getDataPath() {
        return dataPath;
    }

    public static void setDataPath(String dataPath) {
        Configuration.dataPath = dataPath;
    }

    //Generating all files needed to work.
    public void generateFiles() {
        this.initializeFiles();
        this.generateAvatarFile();
        this.generateAndReadSystemFile();
        this.generateAndReadCommandFiles();
    }

    //Generating System.json file
    private void generateAndReadSystemFile() {
        this.generateSystemConfig();
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(this.systemConfigFile)) {
            this.systemConfig = null;
            this.systemConfig = gson.fromJson(reader, SystemConfig.class);
        } catch (IOException e) {
            GalacticBot.getBot().getLogger().error("Invalid System.json");
        }
    }

    //Generating the avatar files
    private void generateAvatarFile() {
        try (InputStream in = GalacticBot.class.getResourceAsStream("/Avatar.png")) {
            Files.copy(in, this.avatarFile.toPath());
        } catch (FileNotFoundException e) {
            //
        } catch (IOException e) {
            //
        }
    }

    //Generates the system configuration file.
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

    //Assigning the above file objects to these
    private void initializeFiles() {
        new File(Configuration.dataPath + "commands/").mkdirs();
        new File(Configuration.dataPath + "commands/").mkdirs();
        new File(Configuration.dataPath + "commands/").mkdirs();
        this.systemConfigFile = new File(Configuration.dataPath + "System.json");
        this.avatarFile = new File(Configuration.dataPath + "Avatar.png");
        this.modCommandFile = new File(Configuration.dataPath + "commands/", "Mod.yml");
        this.userCommandFile = new File(Configuration.dataPath + "commands/", "User.yml");
        this.contextCommandsFile = new File(Configuration.dataPath + "commands/", "Context.yml");
        this.rolesAndIds = new File(Configuration.dataPath, "RolesAndIds.yml");
    }

    //Generating all files if it doesn't already exist and reading from them
    private void generateAndReadCommandFiles() {
        this.generateCommandFiles();
        try (InputStream mod = new FileInputStream(this.modCommandFile);
             InputStream user = new FileInputStream(this.userCommandFile);
             InputStream context = new FileInputStream(this.contextCommandsFile);
             InputStream rolesIds = new FileInputStream(this.rolesAndIds)) {
            this.modCommandConfig = Arrays.asList(new Yaml(new Constructor(SlashCommand[].class)).load(mod));
            this.userCommandConfig = Arrays.asList(new Yaml(new Constructor(SlashCommand[].class)).load(user));
            this.contextConfig = Arrays.asList(new Yaml(new Constructor(ContextCommand[].class)).load(context));
        } catch (IOException e) {
            //
        }
    }

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

    public File getSystemConfigFile() {
        return systemConfigFile;
    }

    public void setSystemConfigFile(File systemConfigFile) {
        this.systemConfigFile = systemConfigFile;
    }

    public SystemConfig getSystemConfig() {
        return systemConfig;
    }

    public void setSystemConfig(SystemConfig systemConfig) {
        this.systemConfig = systemConfig;
    }

    public List<SlashCommand> getModCommandConfig() {
        return modCommandConfig;
    }

    public void setModCommandConfig(List<SlashCommand> modCommandConfig) {
        this.modCommandConfig = modCommandConfig;
    }

    public List<SlashCommand> getUserCommandConfig() {
        return userCommandConfig;
    }

    public void setUserCommandConfig(List<SlashCommand> userCommandConfig) {
        this.userCommandConfig = userCommandConfig;
    }

    public List<ContextCommand> getContextConfig() {
        return contextConfig;
    }

    public void setContextConfig(List<ContextCommand> contextConfig) {
        this.contextConfig = contextConfig;
    }

    public List<RolesAndIdsConfig> getRolesAndIdsConfigs() {
        return rolesAndIdsConfigs;
    }

    public void setRolesAndIdsConfigs(List<RolesAndIdsConfig> rolesAndIdsConfigs) {
        this.rolesAndIdsConfigs = rolesAndIdsConfigs;
    }

    public File getModCommandFile() {
        return modCommandFile;
    }

    public void setModCommandFile(File modCommandFile) {
        this.modCommandFile = modCommandFile;
    }

    public File getUserCommandFile() {
        return userCommandFile;
    }

    public void setUserCommandFile(File userCommandFile) {
        this.userCommandFile = userCommandFile;
    }

    public File getContextCommandsFile() {
        return contextCommandsFile;
    }

    public void setContextCommandsFile(File contextCommandsFile) {
        this.contextCommandsFile = contextCommandsFile;
    }

    public File getRolesAndIds() {
        return rolesAndIds;
    }

    public void setRolesAndIds(File rolesAndIds) {
        this.rolesAndIds = rolesAndIds;
    }

    public File getAvatarFile() {
        return avatarFile;
    }

    public void setAvatarFile(File avatarFile) {
        this.avatarFile = avatarFile;
    }
}
