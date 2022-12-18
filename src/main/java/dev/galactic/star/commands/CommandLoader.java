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

public class CommandLoader extends ListenerAdapter {

    private static final HashMap<Command, SlashCommand> slashCommands = new HashMap<>();
    private static final HashMap<Command, ContextCommand> contextCommands = new HashMap<>();

    //Registers the slash/context commands
    public static void registerCommands(JDA jda) {
        //Running it asynchronously
        CompletableFuture.runAsync(() -> {
            slashCommands.clear();
            contextCommands.clear();
            Configuration config = Configuration.getInstance();
            if (config.getSystemConfig().isDel_cmd_on_reload()) {
                for (Command cmd : jda.retrieveCommands().complete()) {
                    cmd.delete().queue();
                }
            }
            config.getModCommandConfig().forEach(e -> slashCommands.put(jda.upsertCommand(e.toData()).complete(), e));
            config.getUserCommandConfig().forEach(e -> slashCommands.put(jda.upsertCommand(e.toData()).complete(), e));
            config.getContextConfig().forEach(e -> contextCommands.put(jda.upsertCommand(e.toData()).complete(), e));
            GalacticBot.getBot().getLogger().info("Updated and registered slash/context commands.");
        });
    }

    public static HashMap<Command, SlashCommand> getSlashCommands() {
        return slashCommands;
    }

    public static HashMap<Command, ContextCommand> getContextCommands() {
        return contextCommands;
    }

    //Returns the slash command
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

    //Returns the context command
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

    //Invokes the method to handle the event
    @Override
    public void onUserContextInteraction(UserContextInteractionEvent event) {
        super.onUserContextInteraction(event);
        try {
            this.getContextCommand(event.getName()).handleEvent(event);
        } catch (UnknownCommandException e) {
            throw new RuntimeException(e);
        }
    }

    //Invokes the method to handle the event
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
