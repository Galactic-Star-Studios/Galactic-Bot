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

package dev.galactic.star.commands;

import dev.galactic.star.GalacticBot;
import dev.galactic.star.config.Configuration;
import dev.galactic.star.config.comands.context.ContextCommand;
import dev.galactic.star.config.comands.slash.SlashCommand;
import dev.galactic.star.exceptions.UnknownCommandException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

/**
 * This class loads the commands by parsing the file, generating the command from the methods, deleting the commands
 * in the discord server, and creating new ones. On command event, invokes the method in the class that is used to
 * handle it
 */
public class CommandLoader extends ListenerAdapter {

    private static final HashMap<Command, SlashCommand> slashCommands = new HashMap<>();
    private static final HashMap<Command, ContextCommand> contextCommands = new HashMap<>();

    /**
     * Deletes/registers the commands so that they can be updated.
     *
     * @param jda JDA Object
     */
    public static void registerCommands(JDA jda) {
        //Running it asynchronously
        CompletableFuture.runAsync(() -> {
            slashCommands.clear();
            contextCommands.clear();
            Configuration config = Configuration.getInstance();
            if (config.getSystemConfig().isDel_cmd_on_reload()) {
                Thread asyncSlashDeletion = new Thread(() -> {
                    for (Command cmd : jda.retrieveCommands().complete()) {
                        cmd.delete().queue();

                    }
                    GalacticBot.getBot().getLogger().info("Deleted the slash/context commands. Now registering them.");
                });
                asyncSlashDeletion.start();
            }
            Thread asyncModCommandRegister = new Thread(() -> {
                config.getModCommandConfig().forEach(e -> slashCommands.put(
                        jda.upsertCommand(e.toData()).complete(),
                        e
                ));
                GalacticBot.getBot().getLogger().info("Registered the mod slash commands.");
            });
            Thread asyncUserCommandRegister = new Thread(() -> {
                config.getUserCommandConfig().forEach(e -> slashCommands.put(jda.upsertCommand(e.toData()).complete()
                        , e));
                GalacticBot.getBot().getLogger().info("Registered the user slash commands.");
            });
            Thread asyncContextCommandRegister = new Thread(() -> {
                config.getContextConfig().forEach(e -> contextCommands.put(
                        jda.upsertCommand(e.toData()).complete(),
                        e
                ));
                GalacticBot.getBot().getLogger().info("Registered the context commands.");
            });
            asyncModCommandRegister.start();
            asyncUserCommandRegister.start();
            asyncContextCommandRegister.start();
            try {
                asyncModCommandRegister.join();
                asyncUserCommandRegister.join();
                asyncContextCommandRegister.join();

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            GalacticBot.getBot().getLogger().info("Updated and registered all slash and context commands.");
        });
    }

    /**
     * Gets the map of slash commands
     *
     * @return Hashmap&lt;Command, SlashCommand&gt;
     */
    public static HashMap<Command, SlashCommand> getSlashCommands() {
        return slashCommands;
    }

    /**
     * Gets the map of context commands
     *
     * @return Hashmap&lt;Command, ContextCommand&gt;
     */
    public static HashMap<Command, ContextCommand> getContextCommands() {
        return contextCommands;
    }

    /**
     * Gets the handler of the command by its name
     *
     * @param name Name of the command as a String
     * @return SlashHandler
     * @throws UnknownCommandException If the name is invalid
     */
    @SuppressWarnings("deprecation")
    public SlashHandler getCommand(String name) throws UnknownCommandException {
        String cmd = slashCommands.values()
                .stream()
                .filter(e -> e.getName().equals(name))
                .map(SlashCommand::getHandler).toList().get(0);
        if (cmd == null) {
            throw new UnknownCommandException("Unknown Command by the name: \"" + name + "\"");
        }
        try {
            return (SlashHandler) Class.forName(cmd).newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the handler of the context command by its name
     *
     * @param name Name of the context command as a String
     * @return SlashHandler
     * @throws UnknownCommandException If the name is invalid
     */
    @SuppressWarnings("deprecation")
    public ContextHandler getContextCommand(String name) throws UnknownCommandException {
        String cmd = contextCommands.values()
                .stream()
                .filter(e -> e.getName().equals(name))
                .map(ContextCommand::getHandler).toList().get(0);
        if (cmd == null) {
            throw new UnknownCommandException("Unknown Context Command by the name: \"" + name + "\"");
        }
        try {
            return (ContextHandler) Class.forName(cmd).newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Invokes the method to handle the event
     *
     * @param event UserContextInteractionEvent
     */
    @Override
    public void onUserContextInteraction(UserContextInteractionEvent event) {
        super.onUserContextInteraction(event);
        try {
            this.getContextCommand(event.getName()).handleEvent(event);
        } catch (UnknownCommandException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Invokes the method to handle the event
     *
     * @param event SlashCommandInteractionEvent
     */
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        super.onSlashCommandInteraction(event);
        try {
            this.getCommand(event.getName()).handleEvent(event);
        } catch (UnknownCommandException e) {
            throw new RuntimeException(e);
        }
    }
}
