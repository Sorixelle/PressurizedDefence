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

package net.retvoid.pressurizeddefence.block

import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.world.World
import net.retvoid.pressurizeddefence.PressurizedDefence
import net.retvoid.pressurizeddefence.tile.TileScaldingTrap

object BlockScaldingTrap extends BaseBlock(Material.IRON) with ITileEntityProvider with IPipeConnect {
  setName("scalding_trap")
  setCreativeTab(PressurizedDefence.creativeTab)

  override def isOpaqueCube(state: IBlockState): Boolean = false

  override def createNewTileEntity(worldIn: World, meta: Int): TileEntity = new TileScaldingTrap

  override def getPipeConnectFaces(state: IBlockState): Seq[EnumFacing] = EnumFacing.DOWN :: Nil
}
