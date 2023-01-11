/*
 * This file is part of ViaProtocolHack - https://github.com/RaphiMC/ViaProtocolHack
 * Copyright (C) 2023 RK_01/RaphiMC and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.raphimc.viaprotocolhack;

import com.viaversion.viabackwards.api.ViaBackwardsPlatform;
import com.viaversion.viaversion.ViaManagerImpl;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.data.MappingDataLoader;
import com.viaversion.viaversion.api.platform.ViaInjector;
import com.viaversion.viaversion.api.platform.ViaPlatform;
import com.viaversion.viaversion.api.platform.ViaPlatformLoader;
import com.viaversion.viaversion.commands.ViaCommandHandler;
import com.viaversion.viaversion.protocol.ProtocolManagerImpl;
import de.gerrygames.viarewind.api.ViaRewindPlatform;
import net.raphimc.vialegacy.platform.ViaLegacyPlatform;
import net.raphimc.viaprotocolhack.impl.platform.ViaVersionPlatformImpl;
import net.raphimc.viaprotocolhack.impl.viaversion.VPCommandHandler;
import net.raphimc.viaprotocolhack.impl.viaversion.VPInjector;
import net.raphimc.viaprotocolhack.impl.viaversion.VPLoader;
import net.raphimc.viaprotocolhack.util.JLoggerToSLF4J;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;
import java.util.logging.Logger;

public class ViaProtocolHack {

    public static final String VERSION = "${version}";

    private static final Logger LOGGER = new JLoggerToSLF4J(LoggerFactory.getLogger("ViaProtocolHack"));

    @SuppressWarnings("ReassignedVariable")
    public static void init(ViaPlatform<?> platform, ViaPlatformLoader loader, ViaInjector injector, ViaCommandHandler commandHandler, Supplier<ViaBackwardsPlatform> viaBackwardsPlatformSupplier, Supplier<ViaRewindPlatform> viaRewindPlatformSupplier, Supplier<ViaLegacyPlatform> viaLegacyPlatformSupplier) {
        if (platform == null) platform = new ViaVersionPlatformImpl(null);
        if (loader == null) loader = new VPLoader();
        if (injector == null) injector = new VPInjector();
        if (commandHandler == null) commandHandler = new VPCommandHandler();

        Via.init(ViaManagerImpl.builder()
                .platform(platform)
                .loader(loader)
                .injector(injector)
                .commandHandler(commandHandler)
                .build());
        MappingDataLoader.enableMappingsCache();

        final Supplier<ViaBackwardsPlatform> finalViaBackwardsPlatformSupplier = viaBackwardsPlatformSupplier;
        final Supplier<ViaRewindPlatform> finalViaRewindPlatformSupplier = viaRewindPlatformSupplier;
        final Supplier<ViaLegacyPlatform> finalViaLegacyPlatformSupplier = viaLegacyPlatformSupplier;
        Via.getManager().addEnableListener(() -> {
            if (finalViaBackwardsPlatformSupplier != null) {
                try {
                    finalViaBackwardsPlatformSupplier.get();
                } catch (Throwable e) {
                    LOGGER.severe("ViaBackwards failed to load: " + e.getMessage());
                }
            } else {
                LOGGER.info("ViaBackwards is not loaded.");
            }
            if (finalViaRewindPlatformSupplier != null) {
                try {
                    finalViaRewindPlatformSupplier.get();
                } catch (Throwable e) {
                    LOGGER.severe("ViaRewind failed to load: " + e.getMessage());
                }
            } else {
                LOGGER.info("ViaRewind is not loaded.");
            }
            if (finalViaLegacyPlatformSupplier != null) {
                try {
                    finalViaLegacyPlatformSupplier.get();
                } catch (Throwable e) {
                    LOGGER.severe("ViaLegacy failed to load: " + e.getMessage());
                }
            } else {
                LOGGER.info("ViaLegacy is not loaded.");
            }
        });

        ((ViaManagerImpl) Via.getManager()).init();
        Via.getManager().getProtocolManager().setMaxProtocolPathSize(Integer.MAX_VALUE);
        Via.getManager().getProtocolManager().setMaxPathDeltaIncrease(-1);
        ((ProtocolManagerImpl) Via.getManager().getProtocolManager()).refreshVersions();
    }

}
