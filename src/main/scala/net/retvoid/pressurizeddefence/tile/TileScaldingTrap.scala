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
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.math.{AxisAlignedBB, BlockPos, Vec3d}
import net.minecraft.util._
import net.minecraft.world.WorldServer
import net.minecraftforge.common.capabilities.Capability
import net.retvoid.pressurizeddefence.capability.Capabilities
import net.retvoid.pressurizeddefence.capability.steam.SteamHolder
import net.retvoid.pressurizeddefence.entity.ModDamageSources
import net.retvoid.pressurizeddefence.Predefs._

import scala.collection.JavaConverters._

class TileScaldingTrap extends TileEntity with ITickable {
  private val steam: SteamHolder = new SteamHolder(500) {
    override def onSteamChange(prev: Int): Unit = markDirty()
  }
  var reloadCooldown: Int = 0

  override def hasCapability(capability: Capability[_], facing: EnumFacing): Boolean = {
    if (capability == Capabilities.STEAM_CAPABILITY && (facing == EnumFacing.DOWN || facing == null)) true
    else super.hasCapability(capability, facing)
  }

  override def getCapability[T](capability: Capability[T], facing: EnumFacing): T = {
    if (hasCapability(capability, facing) && capability == Capabilities.STEAM_CAPABILITY)
      Capabilities.STEAM_CAPABILITY.cast(steam)
    else super.getCapability(capability, facing)
  }

  override def readFromNBT(compound: NBTTagCompound): Unit = {
    super.readFromNBT(compound)
    if (compound.hasKey("reloadCooldown")) reloadCooldown = compound.getInteger("reloadCooldown")
    if (compound.hasKey("steam")) steam.set(compound.getInteger("steam"))
  }

  override def writeToNBT(compound: NBTTagCompound): NBTTagCompound = {
    super.writeToNBT(compound)
    compound.setInteger("reloadCooldown", reloadCooldown)
    compound.setInteger("steam", steam.getSteam)
    compound
  }

  override def update(): Unit = {
    if (!world.isRemote) {
      if (reloadCooldown == 0) {
        if (steam.isFull) {
          val entities: List[EntityLivingBase] = world.getEntitiesWithinAABB(classOf[EntityLivingBase], new AxisAlignedBB(pos.add(0, 1, 0))).asScala.toList
          if (entities.nonEmpty) {
            val entity: EntityLivingBase = entities.head
            steam.consume(steam.getMaxSteam)
            doParticles(pos.add(0, 1, 0).asDoubles)
            world.playSound(null, pos, SoundEvent.REGISTRY.getObject(new ResourceLocation("block.lava.extinguish")), SoundCategory.BLOCKS, 1.0f, 1.0f)
            entity.setFire(10)
            entity.attackEntityFrom(ModDamageSources.SCALDING_TRAP, 2f)
            reloadCooldown = 200
            markDirty()
          }
        }
      } else {
        reloadCooldown -= 1
        markDirty()
      }
    }
  }

  private def doParticles(pos: Vec3d): Unit =
    (for {
      x <- (1/16d) * 4 :: (1/16d) * 13 :: Nil
      z <- (1/16d) * 4 :: (1/16d) * 13 :: Nil
    } yield (x, z)) foreach { case (x, z) =>
      world.asInstanceOf[WorldServer].spawnParticle(
        EnumParticleTypes.CLOUD,
        pos.x + x, pos.y, pos.z + z,
        10,
        0, 0, 0,
        0.2f
      )
    }
}
