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

import dev.galactic.star.config.Configuration;
import dev.galactic.star.config.system.SystemConfig;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Activity.ActivityType;
import net.dv8tion.jda.api.entities.Icon;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Scanner;

public class GalacticBot {

    private static final GalacticBot bot = new GalacticBot();
    private final Logger logger = LoggerFactory.getLogger("GalacticBot");
    private JDA jda;
    private Configuration configurations;
    private boolean isRunning = true;
    private HashMap<String, String> systemCommands = new HashMap<>();
    private SystemConfig systemConfig;

    public static void main(String[] args) {
        bot.logger.info("Starting Discord Bot...");
        bot.loadEverything();
        bot.logger.info("Started Discord Bot.");
    }

    //Just loads everything
    public void loadEverything() {
        this.configurations = new Configuration();
        this.loadSystemCommands();
        this.loadConfigurations();
        this.loginToBot();
        this.listenForCommands();
    }

    //Places the current system commands into hte hashmap so that it can be used in the printHelp method
    private void loadSystemCommands() {
        this.systemCommands.put("exit|stop", "Stop and close the connection of the bot.");
        this.systemCommands.put("info|i", "Get information about the bot.");
        this.systemCommands.put("reload|rl", "Reloads the configuration and bot itself.");
    }

    //Loads the configuration files
    private void loadConfigurations() {
        this.logger.info("Loading Configurations...");
        File jsonConfig = SystemConfig.createJsonConfig();
        this.configurations.readConfigurations(jsonConfig);
        this.systemConfig = this.configurations.getSystemConfig();
        this.logger.info("Loaded Configurations.");
    }

    //Stops accepting commands by setting isRunning to false. Disconnects the bot from the gateway.
    public void exitBot() {
        this.logger.warn("Shutting down Bot...");
        if (this.jda != null) {
            this.jda.shutdownNow();
        }
        this.logger.warn("Shut down Discord Bot.");
    }

    //Reloads the configuration and restarts the connection.
    public void reload() {
        this.logger.warn("Reloading bot...");
        this.exitBot();
        this.isRunning = true;
        this.loadEverything();
        this.logger.warn("Reloaded bot.");
    }

    //Logs in to the bots gateway
    private void loginToBot() {
        dev.galactic.star.config.system.Activity activity = this.systemConfig.getActivity();
            try {
                jda = JDABuilder.createDefault(this.systemConfig.getToken())
                        .setAutoReconnect(true)
                        .setStatus(OnlineStatus.valueOf(this.systemConfig.getOnline_status().toUpperCase()))
                        .enableCache(CacheFlag.MEMBER_OVERRIDES)
                        .setActivity(Activity.of(ActivityType.valueOf(activity.getType().toUpperCase()), activity.getMessage()))
                        .build();
            }catch (InvalidTokenException | IllegalArgumentException e) {
                this.logger.warn("Invalid token. Please check it and try again: " + e.getMessage());
                this.getLogger().warn(this.systemConfig.getToken());
                exitBot();
                System.exit(0);
            }
    }

    //Creates a listener for the commands on a separate thread, so it doesn't block the main thread.+
    @SuppressWarnings("deprecation")
    public void listenForCommands() {
        Thread commandListener = new Thread(() -> {
            try (Scanner scanner = new Scanner(System.in)) {
                while (this.isRunning) {
                    String cmd = scanner.nextLine().toLowerCase();
                    switch (cmd) {
                        case "exit", "stop" -> {
                            this.isRunning = false;
                            this.exitBot();
                        }
                        case "info", "i" -> {
                            Runtime run = Runtime.getRuntime();
                            long MEGABYTE = 1024L * 1024L;
                            this.logger.info("----------------------------------------");
                            this.logger.info("JVM Version: " + Runtime.version());
                            this.logger.info("Processors: " + run.availableProcessors());
                            this.logger.info("Available Memory in MB: " + ((run.totalMemory() - run.freeMemory()) / MEGABYTE));
                            this.logger.info("----------------------------------------");
                        }
                        case "reload", "rl" -> {
                            this.reload();
                        }
                        case "help" -> {
                            this.printHelp();
                        }
                        default -> {
                            this.logger.warn("Unknown command \"" + cmd + "\"");
                        }
                    }
                }
            }catch (Exception e) {
                //Nothing here
            }
        });
        commandListener.start();
    }

    //Prints the list of commands in the hashmap that  on load, it runs loadSystemCommands which places the name and description into the map.
    private void printHelp() {
        this.logger.info("----------------------------Help Commands-----------------------------");
       for (Entry<String, String> e : this.systemCommands.entrySet()) {
           this.logger.info(e.getKey() + " - " + e.getValue());
       }
        this.logger.info("----------------------------Help Commands-----------------------------");
    }

    public static GalacticBot getBot() {
        return bot;
    }

    public Logger getLogger() {
        return logger;
    }

    public JDA getJda() {
        return jda;
    }

    public Configuration getConfigurations() {
        return configurations;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public HashMap<String, String> getSystemCommands() {
        return systemCommands;
    }

    public SystemConfig getSystemConfig() {
        return systemConfig;
    }
}
