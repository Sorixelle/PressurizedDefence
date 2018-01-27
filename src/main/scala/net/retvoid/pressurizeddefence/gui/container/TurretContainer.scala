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

package net.retvoid.pressurizeddefence.gui.container

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.{Container, IInventory, Slot}
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import net.minecraftforge.items.{CapabilityItemHandler, IItemHandler, SlotItemHandler}
import net.retvoid.pressurizeddefence.capability.Capabilities
import net.retvoid.pressurizeddefence.tile.TileTurret

import scala.collection.JavaConverters._

class TurretContainer(playerInv: IInventory, tile: TileTurret) extends Container {
  var steam: Int = 0

  // Turret inventory
  private val itemHandler: IItemHandler = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
  addSlotToContainer(new SlotItemHandler(itemHandler, 0, 87, 36))
  addSlotToContainer(new SlotItemHandler(itemHandler, 1, 146, 61))

  // Player inventory
  for {
    row <- 0 until 3
    col <- 0 until 9
  } {
    val x: Int = 16 + col * 18
    val y: Int = 82 + row * 18
    addSlotToContainer(new Slot(playerInv, col + row * 9 + 9, x, y))
  }

  // Player hotbar
  0 until 9 foreach { slot =>
    val x: Int = 16 + slot * 18
    val y: Int = 141
    addSlotToContainer(new Slot(playerInv, slot, x, y))
  }

  override def detectAndSendChanges(): Unit = {
    super.detectAndSendChanges()
    listeners.asScala.foreach(l => {
      if (steam != tile.getSteam) l.sendWindowProperty(this, 0, tile.getSteam)
    })
    steam = tile.getSteam
  }

  @SideOnly(Side.CLIENT)
  override def updateProgressBar(id: Int, data: Int): Unit = id match {
    case 0 => tile.getCapability(Capabilities.STEAM_CAPABILITY, null).set(data)
    case _ =>
  }

  override def canInteractWith(playerIn: EntityPlayer): Boolean = tile.canInteractWith(playerIn)

  override def transferStackInSlot(playerIn: EntityPlayer, index: Int): ItemStack = {
    var previous: ItemStack = ItemStack.EMPTY
    val slot: Slot = inventorySlots.get(index)
    if (slot != null && slot.getHasStack) {
      val current: ItemStack = slot.getStack
      previous = current.copy()

      if (index == 0) {
        if (!mergeItemStack(current, 2, 38, false)) return ItemStack.EMPTY
      } else {
        if (!mergeItemStack(current, 0, 2, false)) return ItemStack.EMPTY
      }

      if (current.getCount == 0) slot.putStack(ItemStack.EMPTY)
      else slot.onSlotChanged()

      if (current.getCount == previous.getCount) return ItemStack.EMPTY
      slot.onTake(playerIn, current)
    }
    previous
  }
}
