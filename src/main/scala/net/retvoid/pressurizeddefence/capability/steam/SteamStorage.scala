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

package net.retvoid.pressurizeddefence.capability.steam

import net.minecraft.nbt.{NBTBase, NBTPrimitive, NBTTagInt}
import net.minecraft.util.EnumFacing
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.Capability.IStorage

class SteamStorage extends IStorage[ISteamHolder] {
  override def writeNBT(capability: Capability[ISteamHolder], instance: ISteamHolder, side: EnumFacing): NBTBase =
    new NBTTagInt(instance.getSteam)

  override def readNBT(capability: Capability[ISteamHolder], instance: ISteamHolder, side: EnumFacing, nbt: NBTBase): Unit =
    instance.set(nbt.asInstanceOf[NBTPrimitive].getInt)
}
