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
import net.minecraft.block.properties.PropertyDirection
import net.minecraft.block.state.{BlockStateContainer, IBlockState}
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.{EnumFacing, EnumHand}
import net.minecraft.world.World
import net.retvoid.pressurizeddefence.PressurizedDefence
import net.retvoid.pressurizeddefence.tile.TileBlowbackCannon

object BlockBlowbackCannon extends BaseBlock(Material.IRON) with IPipeConnect with ITileEntityProvider {
  lazy val FACING: PropertyDirection = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL)

  setName("blowback_cannon")
  setHardness(5.0F)
  setResistance(10.0F)
  setCreativeTab(PressurizedDefence.creativeTab)

  override def isOpaqueCube(state: IBlockState): Boolean = false

  override def onBlockPlacedBy(worldIn: World, pos: BlockPos, state: IBlockState, placer: EntityLivingBase,
                               stack: ItemStack): Unit =
    worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing.getOpposite), 2)

  override def createBlockState(): BlockStateContainer = new BlockStateContainer(BlockBlowbackCannon, FACING)
  override def createNewTileEntity(worldIn: World, meta: Int): TileEntity = new TileBlowbackCannon

  override def getStateFromMeta(meta: Int): IBlockState = getDefaultState.withProperty(FACING, EnumFacing.getFront((meta & 3) + 2))
  override def getMetaFromState(state: IBlockState): Int = state.getValue(FACING).getIndex - 2

  override def getPipeConnectFaces(state: IBlockState): Seq[EnumFacing] = EnumFacing.DOWN :: Nil

  override def onBlockActivated(worldIn: World, pos: BlockPos, state: IBlockState, playerIn: EntityPlayer,
                                hand: EnumHand, facing: EnumFacing, hitX: Float, hitY: Float, hitZ: Float): Boolean = {
    if (worldIn.isRemote) return true
    worldIn.getTileEntity(pos) match {
      case tile: TileBlowbackCannon =>
        playerIn.sendMessage(new TextComponentTranslation(s"tile.${PressurizedDefence.MOD_ID}.steam", tile.getSteam: Integer, tile.getMaxSteam: Integer))
        true
      case _ => false
    }
  }
}
