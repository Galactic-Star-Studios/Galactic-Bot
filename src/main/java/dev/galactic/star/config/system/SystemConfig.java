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

import dev.galactic.star.config.Configuration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class SystemConfig {
    private String token;
    private Activity activity;
    private String online_status;

    public static File createJsonConfig() {
        File jsonConfigFile = new File(Configuration.dataPath + "System.json");

        if (!jsonConfigFile.exists()) {
            System.out.println("DOESN'T EXIST");
            try (InputStream jsonIn = SystemConfig.class
                    .getClassLoader()
                    .getResourceAsStream("System.json")
            ) {
                Files.copy(jsonIn, jsonConfigFile.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return jsonConfigFile;
    }

    public String getToken() {
        return token;
    }

    public Activity getActivity() {
        return activity;
    }

    public String getOnline_status() {
        return online_status;
    }
}