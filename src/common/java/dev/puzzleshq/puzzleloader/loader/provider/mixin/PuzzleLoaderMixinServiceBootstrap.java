package dev.puzzleshq.puzzleloader.loader.provider.mixin;/*
 * This file is part of Mixin, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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

import dev.puzzleshq.puzzleloader.loader.launch.Piece;
import org.spongepowered.asm.service.IMixinServiceBootstrap;
import org.spongepowered.asm.service.ServiceInitialisationException;

/**
 * Bootstrap for LaunchWrapper service
 */
public class PuzzleLoaderMixinServiceBootstrap implements IMixinServiceBootstrap {
    private static final String SERVICE_PACKAGE = "org.spongepowered.asm.service.";
    private static final String LAUNCH_PACKAGE = "org.spongepowered.asm.launch.";
    private static final String LOGGING_PACKAGE = "org.spongepowered.asm.logging.";

    private static final String MIXIN_UTIL_PACKAGE = "org.spongepowered.asm.util.";
    private static final String LEGACY_ASM_PACKAGE = "org.spongepowered.asm.lib.";
    private static final String ASM_PACKAGE = "org.objectweb.asm.";
    private static final String MIXIN_PACKAGE = "org.spongepowered.asm.mixin.";

    @Override
    public String getName() {
        return "LaunchWrapper";
    }

    @Override
    public String getServiceClassName() {
        return PuzzleLoaderMixinServiceBootstrap.class.getName();
    }

    @Override
    public void bootstrap() {
        try {
            Piece.classLoader.hashCode();
        } catch (Throwable th) {
            throw new ServiceInitialisationException(this.getName() + " is not available");
        }

        // Essential ones
        Piece.classLoader.addClassLoaderExclusion(PuzzleLoaderMixinServiceBootstrap.SERVICE_PACKAGE);
        Piece.classLoader.addClassLoaderExclusion(PuzzleLoaderMixinServiceBootstrap.LAUNCH_PACKAGE);
        Piece.classLoader.addClassLoaderExclusion(PuzzleLoaderMixinServiceBootstrap.LOGGING_PACKAGE);

        // Important ones
        Piece.classLoader.addClassLoaderExclusion(PuzzleLoaderMixinServiceBootstrap.ASM_PACKAGE);
        Piece.classLoader.addClassLoaderExclusion(PuzzleLoaderMixinServiceBootstrap.LEGACY_ASM_PACKAGE);
        Piece.classLoader.addClassLoaderExclusion(PuzzleLoaderMixinServiceBootstrap.MIXIN_PACKAGE);
        Piece.classLoader.addClassLoaderExclusion(PuzzleLoaderMixinServiceBootstrap.MIXIN_UTIL_PACKAGE);
    }
}