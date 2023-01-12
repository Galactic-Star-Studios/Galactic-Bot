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

import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent;

/**
 * The handler interface so on execution, it can handle accordingly based on the type of context command
 */
public interface ContextHandler {
    /**
     * The method that is implemented so it can be handled
     *
     * @param event The type of event that will track
     */
    void handleEvent(UserContextInteractionEvent event);
}
