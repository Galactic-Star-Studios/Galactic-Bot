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
import dev.galactic.star.config.system.SystemConfig;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Configuration {
    public static String dataPath;
    private File systemConfigFile;
    private SystemConfig systemConfig;

    //Just so when it gets instantiated, it removes the last bit (The jar file's name) and replaces it with an empty
    // string
    public Configuration() {
        dataPath = GalacticBot.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String[] splitJsonPath = dataPath.split("/");
        Configuration.dataPath = dataPath.replace(splitJsonPath[splitJsonPath.length - 1], "");
    }

    //Reads the config files such as the commands, system config, and other things
    public void readConfigurations(File jsonConfigFile) {
        this.systemConfigFile = jsonConfigFile;
        Gson gson = new Gson();
        this.readSystemConfig(gson);
    }

    //Just reads the system config
    private void readSystemConfig(Gson gson) {
        try (FileReader reader = new FileReader(this.systemConfigFile)) {
            this.systemConfig = null;
            this.systemConfig = gson.fromJson(reader, SystemConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
}
