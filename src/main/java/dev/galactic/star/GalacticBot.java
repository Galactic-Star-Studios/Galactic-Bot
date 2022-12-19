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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.galactic.star.commands.CommandLoader;
import dev.galactic.star.config.Configuration;
import dev.galactic.star.config.system.SystemActivity;
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

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class GalacticBot {

    private static GalacticBot bot = new GalacticBot();
    private final org.slf4j.Logger logger = LoggerFactory.getLogger("GalacticBot");
    private JDA jda;
    private boolean isRunning = true;
    private BotSystem system;

    private GalacticBot() {
        bot = this;
    }

    public static void main(String[] args) {
        bot.logger.info("Starting Discord Bot...");
        bot.loadEverything();
        bot.logger.info("Started Discord Bot.");
        //bot.logger.debug(bot.configurations.getModCommandConfig().get(0).getName());
    }

    public static GalacticBot getBot() {
        return bot;
    }

    public void setup() {
        GalacticBot bot = GalacticBot.getBot();
        boolean isConfigured = false;
        SystemConfig config = Configuration.getInstance().getSystemConfig();
        boolean tokenBlank = config.getToken().isBlank();
        boolean maxSeverityBlank = config.getMax_severity() == -1;
        boolean guildIdBlank = config.getGuild_id().isBlank();
        boolean dbUserBlank = config.getDatabase().getUsername().isBlank();
        boolean dbPassBlank = config.getDatabase().getPassword().isBlank();
        if (tokenBlank || maxSeverityBlank || guildIdBlank || dbUserBlank || dbPassBlank) {
            try (Scanner scanner = new Scanner(System.in)) {
                if (tokenBlank) {
                    bot.getLogger().warn("Please enter the bots token: ");
                    String token = scanner.nextLine();
                    config.setToken(token);
                    isConfigured = true;
                }
                if (maxSeverityBlank) {
                    bot.getLogger().warn("Please enter the max warn severity before a ban.");
                    int maxSeverity = scanner.nextInt();
                    config.setMax_severity(maxSeverity);
                    isConfigured = true;
                }
                if (guildIdBlank) {
                    bot.getLogger().warn("Please enter the discord server's id: ");
                    String guildId = scanner.next();
                    config.setGuild_id(guildId);
                    isConfigured = true;
                }
                if (dbUserBlank) {
                    bot.getLogger().warn("Please enter a username for the database without any spaces: ");
                    String dbUser = scanner.next();
                    config.getDatabase().setUsername(dbUser);
                    isConfigured = true;
                }
                if (dbPassBlank) {
                    bot.getLogger().warn("Please enter a password for the database without any spaces: ");
                    String dbPass = scanner.next();
                    config.getDatabase().setPassword(dbPass);
                    isConfigured = true;
                }
                try {
                    Gson gson = new GsonBuilder()
                            .setPrettyPrinting()
                            .create();
                    Files.writeString(Configuration.getInstance().getSystemConfigFile().toPath(), gson.toJson(config));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if (isConfigured) {
                    bot.getLogger().warn("Done with the configuration. If you want to reconfigure these settings, " +
                            "please open the \"System.json\" file.");
                }
            }
        }
        Configuration.getInstance().setSystemConfig(config);
    }

    //Just loads everything
    public void loadEverything() {
        this.system = new BotSystem();
        this.system.loadSystemCommands();
        this.system.loadConfigurations();
        Configuration.getInstance().connectToDatabase();
        this.loginToBot();
        this.jda.addEventListener(new CommandLoader());
        CommandLoader.registerCommands(this.jda);
        try {
            this.jda.getSelfUser().getManager().setAvatar(Icon.from(this.system.getConfigurations().getAvatarFile())).submit().join();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.listenForCommands();
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
        SystemActivity systemActivity = this.system.getSystemConfig().getActivity();
        try {
            jda = JDABuilder.createDefault(this.system.getSystemConfig().getToken())
                    .setAutoReconnect(true)
                    .setStatus(OnlineStatus.valueOf(this.system.getSystemConfig().getOnline_status().toUpperCase()))
                    .enableCache(CacheFlag.MEMBER_OVERRIDES)
                    .setActivity(Activity.of(
                            ActivityType.valueOf(systemActivity.getType().toUpperCase()),
                            systemActivity.getMessage()
                    ))
                    .build();
            String guildId = Configuration.getInstance().getSystemConfig().getGuild_id();
            if (jda.getGuildById(guildId) == null) {
                return;
            }
            this.system.setGuild(jda.getGuildById(guildId));
        } catch (InvalidTokenException | IllegalArgumentException e) {
            this.logger.warn("Invalid token. Please check it and try again: " + e.getMessage());
            exitBot();
            System.exit(0);
        }
    }

    //Creates a listener for the commands on a separate thread, so it doesn't block the main thread.+
    @SuppressWarnings("deprecation")
    public void listenForCommands() {
        CompletableFuture.runAsync(() -> {
            try (Scanner scanner = new Scanner(System.in)) {
                while (this.isRunning) {
                    String cmd = scanner.nextLine().toLowerCase();
                    switch (cmd) {
                        case "exit", "stop" -> {
                            System.out.println("EXITING");
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
            } catch (Exception e) {
                //Nothing here
            }
        });
    }

    //Prints the list of commands in the hashmap that  on load, it runs loadSystemCommands which places the name and
    // description into the map.
    private void printHelp() {
        this.logger.info("----------------------------Help Commands-----------------------------");
        for (Entry<String, String> e : this.system.getSystemCommands().entrySet()) {
            this.logger.info(e.getKey() + " - " + e.getValue());
        }
        this.logger.info("----------------------------Help Commands-----------------------------");
    }

    public Logger getLogger() {
        return logger;
    }

    public JDA getJda() {
        return jda;
    }
}
