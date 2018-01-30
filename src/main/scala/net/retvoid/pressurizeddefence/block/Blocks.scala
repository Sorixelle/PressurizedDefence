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

import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import net.retvoid.pressurizeddefence.PressurizedDefence
import net.retvoid.pressurizeddefence.tile._

@Mod.EventBusSubscriber
object Blocks {
  final val blocks: Seq[BaseBlock] =
    BlockBoiler ::
    BlockTurret ::
    BlockScaldingTrap ::
    BlockPipe ::
    BlockBlowbackCannon ::
    Nil
  final val tiles: Seq[(Class[_ <: TileEntity], String)] =
    (classOf[TileBoiler], "boiler") ::
    (classOf[TileTurret], "turret") ::
    (classOf[TileScaldingTrap], "scalding_trap") ::
    (classOf[TilePipe], "pipe") ::
    (classOf[TileBlowbackCannon], "blowback_cannon") ::
    Nil

  @SubscribeEvent
  def registerBlocks(e: RegistryEvent.Register[Block]): Unit = {
    blocks foreach { b => e.getRegistry.register(b) }
    tiles foreach { t => GameRegistry.registerTileEntity(t._1, s"${PressurizedDefence.MOD_ID}_${t._2}") }
  }

  @SubscribeEvent
  def registerItems(e: RegistryEvent.Register[Item]): Unit = blocks.filter { _.hasItem } foreach { b => e.getRegistry.register(b.asItem) }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  def registerModels(e: ModelRegistryEvent): Unit = blocks foreach { _.initModel() }
}
