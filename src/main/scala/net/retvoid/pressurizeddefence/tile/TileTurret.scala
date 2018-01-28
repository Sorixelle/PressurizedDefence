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

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.{EnumFacing, EnumParticleTypes, ITickable}
import net.minecraft.util.math.{BlockPos, Vec3d}
import net.minecraft.world.WorldServer
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.items.{CapabilityItemHandler, ItemStackHandler}
import net.retvoid.pressurizeddefence.block.BlockTurret
import net.retvoid.pressurizeddefence.entity.EntityTurretProjectile
import net.retvoid.pressurizeddefence.Predefs._
import net.retvoid.pressurizeddefence.capability.Capabilities
import net.retvoid.pressurizeddefence.capability.steam.SteamHolder
import net.retvoid.pressurizeddefence.item.upgrade.TurretUpgrade
import net.retvoid.pressurizeddefence.item.ItemTurretAmmo

import scala.language.implicitConversions

class TileTurret extends TileEntity with ITickable {
  private val inventory: ItemStackHandler = new ItemStackHandler(2) {
    override def insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack = {
      slot match {
        case 0 => if (stack.getItem.isInstanceOf[ItemTurretAmmo]) super.insertItem(slot, stack, simulate) else stack
        case 1 => if (stack.getItem.isInstanceOf[TurretUpgrade]) super.insertItem(slot, stack, simulate) else stack
      }
    }
    override def onContentsChanged(slot: Int): Unit = markDirty()
  }
  private val steam: SteamHolder = new SteamHolder {
    override def onSteamChange(prev: Int): Unit = markDirty()
  }
  var fireCooldown: Int = 0

  implicit def blockPosToTuple(bp: BlockPos): (Double, Double, Double) = (bp.getX, bp.getY, bp.getZ)

  def canInteractWith(playerIn: EntityPlayer): Boolean = !isInvalid && playerIn.getDistanceSq(pos.add(0.5d, 0.5d, 0.5d)) <= 64d

  override def hasCapability(capability: Capability[_], facing: EnumFacing): Boolean = {
    if (world.getBlockState(pos).getValue(BlockTurret.FACING) != facing) {
      if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) true
      else if (capability == Capabilities.STEAM_CAPABILITY) true
      else super.hasCapability(capability, facing)
    } else super.hasCapability(capability, facing)
  }

  override def getCapability[T](capability: Capability[T], facing: EnumFacing): T = {
    if (hasCapability(capability, facing)) {
      if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventory)
      else if (capability == Capabilities.STEAM_CAPABILITY)
        Capabilities.STEAM_CAPABILITY.cast(steam)
      else super.getCapability(capability, facing)
    } else super.getCapability(capability, facing)
  }

  override def readFromNBT(compound: NBTTagCompound): Unit = {
    super.readFromNBT(compound)
    if (compound.hasKey("fireCooldown")) fireCooldown = compound.getInteger("fireCooldown")
    if (compound.hasKey("steam")) steam.set(compound.getInteger("steam"))
    if (compound.hasKey("inventory")) inventory.deserializeNBT(compound.getCompoundTag("inventory"))
  }

  override def writeToNBT(compound: NBTTagCompound): NBTTagCompound = {
    super.writeToNBT(compound)
    compound.setInteger("fireCooldown", fireCooldown)
    compound.setInteger("steam", steam.getSteam)
    compound.setTag("inventory", inventory.serializeNBT())
    compound
  }

  def getSteam: Int = steam.getSteam

  override def update(): Unit = {
    if (!world.isRemote) {
      if (fireCooldown == 0) {
        if (inventory.extractItem(0, 1, true).getItem.isInstanceOf[ItemTurretAmmo]) {
          val (cooldown, steamUsed) = inventory.extractItem(1, 1, true).getItem match {
            case upgrade: TurretUpgrade => (upgrade.getTurretFireSpeed, upgrade.getTurretSteamConsumption)
            case _ => (100, 100)
          }
          if (steam.consume(steamUsed, true) == steamUsed) {
            val facing: EnumFacing = world.getBlockState(pos).getValue(BlockTurret.FACING)
            val bullet: ItemTurretAmmo = inventory.extractItem(0, 1, false).getItem.asInstanceOf[ItemTurretAmmo]
            val projectile: EntityTurretProjectile = bullet.createProjectile(world, centerPosOnFace(pos.asDoubles, facing, 0.2))
            projectile.fireInDirection(facing)
            steam.consume(steamUsed)
            world.spawnEntity(projectile)
            doParticles(facing)
          }
          fireCooldown = cooldown
          markDirty()
        }
      } else {
        fireCooldown -= 1
        markDirty()
      }
    }
  }

  private def doParticles(facing: EnumFacing): Unit = {
    val motionY: Double = 0.6
    val motionX: Double = facing match {
      case EnumFacing.EAST => 0.1
      case EnumFacing.WEST => -0.1
      case _ => 0
    }
    val motionZ: Double = facing match {
      case EnumFacing.SOUTH => 0.1
      case EnumFacing.NORTH => -0.1
      case _ => 0
    }
    val particlePos: Vec3d = centerPosOnFace(pos.asDoubles, facing)
    -0.2 to 0.2 by 0.1 foreach { offset =>
      world.asInstanceOf[WorldServer].spawnParticle(
        EnumParticleTypes.SMOKE_NORMAL,
        false,
        particlePos.x,
        particlePos.y,
        particlePos.z,
        2,
        if (motionX == 0) motionX + offset else motionX,
        motionY,
        if (motionZ == 0) motionZ + offset else motionZ,
        0.02d
      )
    }
  }

}

object TileTurret {
  final val AMMO_SLOT: Int = 0
  final val UPGRADE_SLOT: Int = 1
}
