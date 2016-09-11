/**
 * This file is part of Special, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <http://github.com/SpongePowered>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.spongepowered.special;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.game.state.GameConstructionEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.special.instance.InstanceManager;
import org.spongepowered.special.instance.InstanceType;
import org.spongepowered.special.instance.InstanceTypeRegistryModule;
import org.spongepowered.special.instance.InstanceTypes;
import org.spongepowered.special.instance.gen.MapMutator;
import org.spongepowered.special.instance.gen.MapMutatorRegistryModule;
import org.spongepowered.special.instance.gen.MapMutators;

import java.io.IOException;
import java.nio.file.Path;

import javax.inject.Inject;

@Plugin(
        id = Constants.Meta.ID,
        name = Constants.Meta.NAME,
        version = Constants.Meta.VERSION,
        authors = Constants.Meta.AUTHORS,
        url = Constants.Meta.URL,
        description = Constants.Meta.DESCRIPTION
)
public final class Special {

    public static Special instance;
    public static Cause plugin_cause;

    private final InstanceManager instanceManager = new InstanceManager();
    @Inject private Logger logger;
    @Inject private PluginContainer container;
    @Inject @ConfigDir(sharedRoot = false) private Path configPath;

    @Listener
    public void onGameConstruction(GameConstructionEvent event) {
        instance = this;
        plugin_cause = Cause.builder().named("plugin", this.container).build();
    }

    @Listener
    public void onGamePreinitialization(GamePreInitializationEvent event) {
        Sponge.getRegistry().registerModule(MapMutator.class, MapMutatorRegistryModule.getInstance());
        Sponge.getRegistry().registerModule(InstanceType.class, InstanceTypeRegistryModule.getInstance());

        Sponge.getRegistry().registerBuilderSupplier(InstanceType.Builder.class, InstanceType.Builder::new);

        Sponge.getCommandManager().register(this.container, Commands.rootCommand, Constants.Meta.ID, Constants.Meta.ID.substring(0, 1));

        Sponge.getEventManager().registerListeners(this.container, this.instanceManager);
    }

    @Listener
    public void onGameStartingServer(GameStartingServerEvent event) throws IOException {
        this.logger.error(MapMutators.PLAYER_SPAWN.toString());
        this.logger.error(MapMutators.CHEST_MUTATOR.toString());
        this.logger.error(InstanceTypes.LAST_MAN_STANDING.toString());

        Sponge.getServer()
                .loadWorld(Sponge.getServer().createWorldProperties(Constants.Map.Lobby.DEFAULT_LOBBY_NAME, Constants.Map.Lobby.lobbyArchetype));
    }

    public Logger getLogger() {
        return this.logger;
    }

    public Path getConfigPath() {
        return this.configPath;
    }

    public InstanceManager getInstanceManager() {
        return this.instanceManager;
    }
}
