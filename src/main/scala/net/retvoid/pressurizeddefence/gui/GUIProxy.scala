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

package net.retvoid.pressurizeddefence.gui

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import net.retvoid.pressurizeddefence.gui.container.{BoilerContainer, TurretContainer}
import net.retvoid.pressurizeddefence.tile.{TileBoiler, TileTurret}

class GUIProxy extends IGuiHandler {
  @SideOnly(Side.CLIENT)
  override def getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef =
    world.getTileEntity(new BlockPos(x, y, z)) match {
      case boiler: TileBoiler => new BoilerGUI(boiler, new BoilerContainer(player.inventory, boiler))
      case turret: TileTurret => new TurretGUI(turret, new TurretContainer(player.inventory, turret))
      case _ => null
    }

  override def getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): AnyRef =
    world.getTileEntity(new BlockPos(x, y, z)) match {
      case boiler: TileBoiler => new BoilerContainer(player.inventory, boiler)
      case turret: TileTurret => new TurretContainer(player.inventory, turret)
      case _ => null
    }
}
