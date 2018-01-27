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

package net.retvoid.pressurizeddefence.tile

import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.{ItemCoal, ItemStack}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.{TileEntity, TileEntityFurnace}
import net.minecraft.util.{EnumFacing, ITickable}
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.{CapabilityItemHandler, ItemStackHandler}
import net.retvoid.pressurizeddefence.capability.Capabilities
import net.retvoid.pressurizeddefence.capability.steam.{ISteamHolder, SteamHolder}

class TileBoiler extends TileEntity with ITickable {
  private val fuel: ItemStackHandler = new ItemStackHandler(1) {
    override def onContentsChanged(slot: Int): Unit = markDirty()
  }
  private val steam: SteamHolder = new SteamHolder {
    override def onSteamChange(prev: Int): Unit = markDirty()
  }
  var burnTime: Int = 0
  var maxBurnTime: Int = 0

  def canInteractWith(playerIn: EntityPlayer): Boolean = !isInvalid && playerIn.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D

  override def shouldRefresh(world: World, pos: BlockPos, oldState: IBlockState, newState: IBlockState): Boolean = oldState.getBlock != newState.getBlock

  override def hasCapability(capability: Capability[_], facing: EnumFacing): Boolean = {
    if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) true
    else if (capability == Capabilities.STEAM_CAPABILITY) true
    else super.hasCapability(capability, facing)
  }

  override def getCapability[T](capability: Capability[T], facing: EnumFacing): T = {
    if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) facing match {
      case EnumFacing.UP => super.getCapability(capability, facing)
      case _ => CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(fuel)
    } else if (capability == Capabilities.STEAM_CAPABILITY) facing match {
      case EnumFacing.UP => Capabilities.STEAM_CAPABILITY.cast(steam)
      case _ => super.getCapability(capability, facing)
    }
    else super.getCapability(capability, facing)
  }

  override def readFromNBT(compound: NBTTagCompound): Unit = {
    super.readFromNBT(compound)
    if (compound.hasKey("fuel")) fuel.deserializeNBT(compound.getTag("fuel").asInstanceOf[NBTTagCompound])
    if (compound.hasKey("steam")) steam.set(compound.getInteger("steam"))
    if (compound.hasKey("burnTime")) burnTime = compound.getInteger("burnTime")
    if (compound.hasKey("maxBurnTime")) maxBurnTime = compound.getInteger("maxBurnTime")
  }

  override def writeToNBT(compound: NBTTagCompound): NBTTagCompound = {
    super.writeToNBT(compound)
    compound.setTag("fuel", fuel.serializeNBT)
    compound.setInteger("steam", steam.getSteam)
    compound.setInteger("burnTime", burnTime)
    compound.setInteger("maxBurnTime", maxBurnTime)
    compound
  }

  def setField(id: Int, value: Int): Unit = id match {
    case 0 => steam.set(value)
    case 1 => burnTime = value
    case 2 => maxBurnTime = value
  }

  def getSteam: Int = steam.getSteam

  override def update(): Unit = {
    //if (world.isRemote) PressurizedDefence.logger.info(s"\nBurn time: $burnTime\nMax burn time: $maxBurnTime\nSteam: $steam")
    if (!world.isRemote) {
      if (maxBurnTime == 0 && !steam.isFull) {
        if (fuel.getStackInSlot(0) != ItemStack.EMPTY) {
          val burn: Int = TileEntityFurnace.getItemBurnTime(fuel.getStackInSlot(0))
          if (burn != 0) {
            maxBurnTime = burn
            burnTime = burn
            fuel.extractItem(0, 1, false)
            markDirty()
          }
        }
      } else {
        if (!steam.isFull) steam.add(1)
        if (maxBurnTime != 0) burnTime -= 1
        if (burnTime == 0) maxBurnTime = 0
        markDirty()
      }

      if (Option(world.getTileEntity(pos.add(0, 1, 0))).exists(_.hasCapability(Capabilities.STEAM_CAPABILITY, EnumFacing.DOWN))) {
        val prevSteam: Int = steam.getSteam
        val steamHolderAbove: ISteamHolder = world.getTileEntity(pos.add(0, 1, 0)).getCapability(Capabilities.STEAM_CAPABILITY, EnumFacing.DOWN)
        val amount: Int = steamHolderAbove.add(steam.consume(20, true), true)
        if (amount != 0) steamHolderAbove.add(steam.consume(amount))
        if (prevSteam != steam.getSteam) markDirty()
      }
    }
  }
}
