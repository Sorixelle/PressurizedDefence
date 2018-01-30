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

import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.{EntityPlayer, EntityPlayerMP}
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.play.server.SPacketEntityVelocity
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.{AxisAlignedBB, BlockPos}
import net.minecraft.util._
import net.minecraftforge.common.capabilities.Capability
import net.retvoid.pressurizeddefence.block.BlockBlowbackCannon
import net.retvoid.pressurizeddefence.capability.Capabilities
import net.retvoid.pressurizeddefence.capability.steam.SteamHolder

import scala.collection.JavaConverters._

class TileBlowbackCannon extends TileEntity with ITickable {
  private val steam: SteamHolder = new SteamHolder {
    override def onSteamChange(prev: Int): Unit = markDirty()
  }
  var cooldown: Int = 0

  override def hasCapability(capability: Capability[_], facing: EnumFacing): Boolean = {
    if (capability == Capabilities.STEAM_CAPABILITY) facing match {
      case EnumFacing.DOWN => true
      case _ => super.hasCapability(capability, facing)
    } else super.hasCapability(capability, facing)
  }

  override def getCapability[T](capability: Capability[T], facing: EnumFacing): T = {
    if (hasCapability(capability, facing) && capability == Capabilities.STEAM_CAPABILITY)
      Capabilities.STEAM_CAPABILITY.cast(steam)
    else super.getCapability(capability, facing)
  }

  override def readFromNBT(compound: NBTTagCompound): Unit = {
    super.readFromNBT(compound)
    if (compound.hasKey("cooldown")) cooldown = compound.getInteger("cooldown")
    if (compound.hasKey("steam")) steam.set(compound.getInteger("steam"))
  }

  override def writeToNBT(compound: NBTTagCompound): NBTTagCompound = {
    super.writeToNBT(compound)
    compound.setInteger("cooldown", cooldown)
    compound.setInteger("steam", steam.getSteam)
    compound
  }

  def getSteam: Int = steam.getSteam
  def getMaxSteam: Int = steam.getMaxSteam

  override def update(): Unit = {
    if (!world.isRemote) {
      if (cooldown == 0) {
        if (steam.getSteam >= 500) {
          val facing: EnumFacing = world.getBlockState(pos).getValue(BlockBlowbackCannon.FACING)
          val (p1, p2) = facing match {
            case EnumFacing.NORTH => (pos.add(1, 1, -1), pos.add(-1, -1, -5))
            case EnumFacing.SOUTH => (pos.add(-1, 1, 1), pos.add(1, -1, 5))
            case EnumFacing.EAST => (pos.add(1, 1, 1), pos.add(5, -1, -1))
            case EnumFacing.WEST => (pos.add(-1, 1, -1), pos.add(-5, -1, 1))
            case _ => (pos, pos)
          }
          val entities: Seq[EntityLivingBase] = world.getEntitiesWithinAABB(classOf[EntityLivingBase], new AxisAlignedBB(p1, p2)).asScala
          entities.foreach(entity => {
            val (x, y, z) = facing match {
              case EnumFacing.NORTH => (0.0, 0.5, -1.5)
              case EnumFacing.SOUTH => (0.0, 0.5, 1.5)
              case EnumFacing.EAST => (1.5, 0.5, 0.0)
              case EnumFacing.WEST => (-1.5, 0.5, 0.0)
              case _ => (0.0, 0.0, 0.0)
            }
            entity.addVelocity(x, y, z)
            entity match {
              case player: EntityPlayerMP => player.connection.sendPacket(new SPacketEntityVelocity(player))
              case _ =>
            }
          })
          if (entities.nonEmpty) {
            world.playSound(null, pos, SoundEvent.REGISTRY.getObject(new ResourceLocation("entity.ghast.shoot")), SoundCategory.BLOCKS, 1f, 1f)
            steam.consume(500)
            cooldown = 300
            markDirty()
          }
        }
      } else {
        cooldown -= 1
        markDirty()
      }
    }
  }
}
