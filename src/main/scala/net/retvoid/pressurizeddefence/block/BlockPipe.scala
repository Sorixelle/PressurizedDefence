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
import net.minecraft.block.properties.PropertyBool
import net.minecraft.block.state.{BlockStateContainer, IBlockState}
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import net.minecraft.world.{IBlockAccess, World}
import net.retvoid.pressurizeddefence.tile.TilePipe

object BlockPipe extends BaseBlock(Material.IRON) with ITileEntityProvider with IPipeConnect {
  type JBool = java.lang.Boolean

  lazy val UP: PropertyBool = PropertyBool.create("up")
  lazy val DOWN: PropertyBool = PropertyBool.create("down")
  lazy val NORTH: PropertyBool = PropertyBool.create("north")
  lazy val SOUTH: PropertyBool = PropertyBool.create("south")
  lazy val WEST: PropertyBool = PropertyBool.create("west")
  lazy val EAST: PropertyBool = PropertyBool.create("east")
  lazy val props: Seq[(PropertyBool, EnumFacing)] =
    (UP :: DOWN :: NORTH :: SOUTH :: WEST:: EAST :: Nil) zip EnumFacing.VALUES
  setName("pipe")

  override def isOpaqueCube(state: IBlockState): Boolean = false
  override def isFullBlock(state: IBlockState): Boolean = false

  override def createBlockState: BlockStateContainer = new BlockStateContainer(BlockPipe, UP, DOWN, NORTH, SOUTH, WEST, EAST)
  override def getMetaFromState(state: IBlockState): Int = 0
  override def getStateFromMeta(meta: Int): IBlockState = getDefaultState
  override def getActualState(state: IBlockState, worldIn: IBlockAccess, pos: BlockPos): IBlockState = {
    val a = props.foldLeft(state) { case (s, (p, f)) => {
      val face: EnumFacing = if (EnumFacing.HORIZONTALS.contains(f)) f else f.getOpposite
      s.withProperty[JBool, JBool](p, isConnectedOn(face, pos, worldIn).booleanValue())
    } }
    //println(isConnectedOn(EnumFacing.DOWN, pos, worldIn))
    a
  }// compiler cannot into type inference

  private def isConnectedOn(facing: EnumFacing, pos: BlockPos, world: IBlockAccess): Boolean = {
    val state: IBlockState = world.getBlockState(pos.offset(facing))
    state.getBlock match {
      case block: IPipeConnect =>
        val face: EnumFacing = if (EnumFacing.HORIZONTALS.contains(facing)) facing else facing.getOpposite
        if (block.connectsOnFace(face, state)) true else false
      case _ => false
    }
  }

  override def getPipeConnectFaces(state: IBlockState): Seq[EnumFacing] = props.map(_._2)

  override def createNewTileEntity(worldIn: World, meta: Int): TileEntity = new TilePipe
}
