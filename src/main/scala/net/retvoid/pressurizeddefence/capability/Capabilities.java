/*
 * Pressurized Defence - Steam-powered weaponry and defences in Minecraft.
 * Copyright (C) 2018  Jacob Juric
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.retvoid.pressurizeddefence.capability;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.retvoid.pressurizeddefence.capability.steam.ISteamHolder;
import net.retvoid.pressurizeddefence.capability.steam.SteamHolder;
import net.retvoid.pressurizeddefence.capability.steam.SteamStorage;

public class Capabilities {
    @CapabilityInject(ISteamHolder.class)
    public static final Capability<ISteamHolder> STEAM_CAPABILITY = null;

    @SuppressWarnings("unchecked")
    public static void registerCapabilities() {
        CapabilityManager.INSTANCE.register(ISteamHolder.class, new SteamStorage(), SteamHolder.class);
    }
}
