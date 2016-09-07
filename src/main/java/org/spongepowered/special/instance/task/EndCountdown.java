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
package org.spongepowered.special.instance.task;

import org.spongepowered.api.effect.particle.ParticleEffect;
import org.spongepowered.api.effect.particle.ParticleTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.title.Title;
import org.spongepowered.special.instance.Instance;

public final class EndCountdown {

    private final Title title;

    public EndCountdown(Instance instance, Player winner) {
        final long seconds = instance.getInstanceType().getRoundEndLength();

        this.title = Title.builder()
                .stay((int) ((seconds - 1) * 20))
                .fadeIn(0)
                .fadeOut(20)
                .title(Text.of(TextColors.YELLOW, winner.getDisplayNameData().displayName().get(), TextColors.WHITE, " is the winner!"))
                .build();

        winner.spawnParticles(ParticleEffect.builder()
                        .type(ParticleTypes.FIREWORKS_SPARK)
                        .count(30)
                        .build(),
                winner.getLocation().getPosition());

        // TODO If world is null or not loaded, shut this task down and log it.

        instance.getHandle().ifPresent((world) -> {
            if (world.isLoaded()) {
                world.getPlayers().stream().filter(User::isOnline).forEach(onlinePlayer -> {
                    onlinePlayer.sendTitle(title);
                });
            }
        });
    }
}