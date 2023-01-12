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

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Scheduler {
    /**
     * Creates a listener for the commands on a separate thread, so it doesn't block the main thread.
     *
     * @return ScheduledFuture&lt;?&gt; so that it can stop when the program stops
     */
    public static ScheduledFuture<?> listenForCommands() {
        return Executors.newScheduledThreadPool(0).schedule(() -> {
            String cmd = GalacticBot.getBot().getScanner().next().toLowerCase();
            if (cmd.matches("\\s*")) {
                return;
            }
            switch (cmd) {
                case "exit", "stop" -> {
                    GalacticBot.getBot().exitBot(true);
                }
                case "info", "i" -> {
                    Runtime run = Runtime.getRuntime();
                    long MEGABYTE = 1024L * 1024L;
                    GalacticBot.getBot().getLogger().info("----------------------------------------");
                    GalacticBot.getBot().getLogger().info("JVM Version: " + Runtime.version());
                    GalacticBot.getBot().getLogger().info("Processors: " + run.availableProcessors());
                    GalacticBot.getBot().getLogger().info("Available Memory in MB: " + ((run.totalMemory() - run.freeMemory()) / MEGABYTE));
                    GalacticBot.getBot().getLogger().info("----------------------------------------");
                }
                case "reload", "rl" -> {
                    GalacticBot.getBot().reload();
                }
                case "help" -> {
                    GalacticBot.getBot().printHelp();
                }
                default -> {
                    GalacticBot.getBot().getLogger().warn("Unknown command \"" + cmd + "\"");
                }
            }
        }, 200, TimeUnit.MILLISECONDS);
    }

    //Keep this commented for now
    /*public static ScheduledFuture<?> dateListener() {
        H2Database db = BotSystem.getInstance().getDb();
        Guild guild = BotSystem.getInstance().getGuild();
        return Executors.newScheduledThreadPool(0)
                .schedule(new Runnable() {
                    @Override
                    public void run() {
                        guild.retrieveBanList().stream()
                                .forEach(ban -> {
                                    String date = db.getFromDb("ban_data", "user_id", ban.getUser().getId(),
                                    "date_unbanned").toString();
                                    ZonedDateTime timeNow = ZonedDateTime.now();
                                    ZonedDateTime timeWhenUnbanned = ZonedDateTime.parse(date);
                                    if (timeNow.isBefore(timeWhenUnbanned)) {
                                        return;
                                    }
                                    guild.unban(ban.getUser()).reason("Ban expired").queue();
                                });
                    }
                }, 1, TimeUnit.MINUTES);
    }*/
}
