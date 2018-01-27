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

package net.retvoid.pressurizeddefence.item

import net.minecraft.item.Item
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import net.retvoid.pressurizeddefence.item.upgrade.ItemSpeedUpgrade

@Mod.EventBusSubscriber
object Items {
  val items: Seq[BaseItem] =
    ItemTurretBullet ::
    ItemTurretBulletLight ::
    ItemTurretBulletHeavy ::
    ItemSpeedUpgrade ::
    ItemBullet ::
    Nil

  @SubscribeEvent
  def registerItems(e: RegistryEvent.Register[Item]): Unit = {
    items foreach { e.getRegistry.register(_) }
  }

  @SubscribeEvent
  @SideOnly(Side.CLIENT)
  def registerModels(e: ModelRegistryEvent): Unit = items foreach { _.initModel() }
}
