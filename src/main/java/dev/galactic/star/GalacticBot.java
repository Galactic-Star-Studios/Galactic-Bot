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
import java.util.Objects;
import java.util.Scanner;

/**
 * The main class/starting point
 */
public class GalacticBot {

    private static GalacticBot bot = new GalacticBot();
    private final org.slf4j.Logger logger = LoggerFactory.getLogger("GalacticBot");
    private final Scanner scanner = new Scanner(System.in);
    private JDA jda;
    private BotSystem system;

    private GalacticBot() {
        bot = this;
    }

    /**
     * The main method that starts the bot. Aka the main starting point
     *
     * @param args The JVM arguments
     */
    public static void main(String[] args) {
        bot.logger.info("Starting Discord Bot...");
        bot.loadEverything();
        bot.logger.info("Started Discord Bot.");
    }

    /**
     * Sets the Singleton field to this class
     *
     * @return GalacticBot class (Singleton)
     */
    public static GalacticBot getBot() {
        return bot;
    }

    /**
     * Checks if the configurations are blank
     */
    private boolean isBlank() {
        SystemConfig config = Configuration.getInstance().getSystemConfig();
        //Just so we have to declare it in 2 places instead of 3 like the old way. Not a huge difference, but makes
        // it easier????
        boolean[] isBlankArray = new boolean[]{
                config.getToken().isBlank(),
                config.getMax_severity() < 0,
                config.getGuild_id().isBlank(),
                config.getDatabase().getUsername().isBlank(),
                config.getDatabase().getPassword().isBlank(),
                config.getInvite_link().isBlank()
        };
        return Objects.equals(isBlankArray, true);
    }

    /**
     * Gets the input to set the System.json to
     */
    public void setup() {
        GalacticBot bot = GalacticBot.getBot();
        boolean isConfigured = false;
        SystemConfig config = Configuration.getInstance().getSystemConfig();
        while (isBlank()) {
            isConfigured = false;
            //Except here lol. Check the isBlank() method for the comment
            boolean tokenBlank = config.getToken().isBlank();
            boolean maxSeverityBlank = config.getMax_severity() < 0;
            boolean guildIdBlank = config.getGuild_id().isBlank();
            boolean dbUserBlank = config.getDatabase().getUsername().isBlank();
            boolean dbPassBlank = config.getDatabase().getPassword().isBlank();
            boolean inviteLinkBlank = config.getInvite_link().isBlank();

            if (tokenBlank) {
                bot.getLogger().warn("Please enter the bots token: ");
                String token = scanner.nextLine();
                config.setToken(token);
                isConfigured = true;
                continue;
            }
            if (maxSeverityBlank) {
                bot.getLogger().warn("Please enter the max warn severity before a ban.");
                int maxSeverity = scanner.nextInt();
                config.setMax_severity(maxSeverity);
                isConfigured = true;
                continue;
            }
            if (guildIdBlank) {
                bot.getLogger().warn("Please enter the discord server's id: ");
                String guildId = scanner.next();
                config.setGuild_id(guildId);
                isConfigured = true;
                continue;
            }
            if (inviteLinkBlank) {
                bot.getLogger().warn("Please enter the discord server's invite link: ");
                String link = scanner.next();
                config.setInvite_link(link);
                isConfigured = true;
                continue;
            }
            if (dbUserBlank) {
                bot.getLogger().warn("Please enter a username for the database without any spaces: ");
                String dbUser = scanner.next();
                config.getDatabase().setUsername(dbUser);
                isConfigured = true;
                continue;
            }
            if (dbPassBlank) {
                bot.getLogger().warn("Please enter a password for the database without any spaces: ");
                String dbPass = scanner.next();
                config.getDatabase().setPassword(dbPass);
                isConfigured = true;
            }
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
        Configuration.getInstance().setSystemConfig(config);
    }

    /**
     * Loads everything such as the system commands, configurations, database connection, bot login, command
     * listener, registers commands, and sets the profile pic of the bot to whatever image is named Avatar.png
     */
    public void loadEverything() {
        this.system = new BotSystem();
        this.system.loadSystemCommands();
        this.system.loadConfigurations();
        Configuration.getInstance().connectToDatabase();
        this.loginToBot();
        this.system.setLogChannel(jda.getTextChannelById(this.system.getConfigurations().getRolesAndIdsConfigs()
                .stream()
                .filter(e -> e.getName()
                        .equalsIgnoreCase("log_channel"))
                .toList()
                .get(0)
                .getId()
                .toString()));
        BotSystem.startSchedulers();
        this.jda.addEventListener(new CommandLoader());
        CommandLoader.registerCommands(this.jda);
        try {
            this.jda.getSelfUser().getManager().setAvatar(Icon.from(this.system.getConfigurations().getAvatarFile())).submit().join();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Stops accepting commands by setting isRunning to false. Disconnects the bot from the gateway.
     *
     * @param finalExit A boolean, when true it exits the program entirely, if false it only disconnects the bot
     */
    public void exitBot(boolean finalExit) {
        this.logger.warn("Shutting down Bot...");
        if (this.jda != null) {
            this.jda.shutdownNow();
        }
        if (finalExit) {
            this.system.getCommandListener().cancel(true);
            GalacticBot.getBot().scanner.close();
        }
        this.system.getDb().disconnect();
        this.logger.warn("Shut down Discord Bot.");
    }

    /**
     * Reloads the configuration and restarts the connection.
     */
    public void reload() {
        this.logger.warn("Reloading bot...");
        this.exitBot(false);
        this.loadEverything();
        this.logger.warn("Reloaded bot.");
    }

    /**
     * Connects the bot to Discord's API so that it can be used
     */
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
            exitBot(true);
            System.exit(0);
        }
    }


    /**
     * Prints the list of commands in the system commands hashmap
     */
    public void printHelp() {
        this.logger.info("----------------------------Help Commands-----------------------------");
        for (Entry<String, String> e : this.system.getSystemCommands().entrySet()) {
            this.logger.info(e.getKey() + " - " + e.getValue());
        }
        this.logger.info("----------------------------Help Commands-----------------------------");
    }

    /**
     * Returns the logger object
     *
     * @return Logger Object
     */
    public Logger getLogger() {
        return logger;
    }

    /**
     * Returns the JDA Object instance
     *
     * @return JDA Object
     */
    public JDA getJda() {
        return jda;
    }

    /**
     * Returns Scanner object, so it can be used ot gather input. It is also used so that there is only 1 scanner.
     *
     * @return Scanner Object
     */
    public Scanner getScanner() {
        return scanner;
    }

    /**
     * Returns the system configuration class
     *
     * @return BotSystem configuration Object
     */
    public BotSystem getSystem() {
        return system;
    }
}
