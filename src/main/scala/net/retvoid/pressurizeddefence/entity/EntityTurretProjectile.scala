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

package net.retvoid.pressurizeddefence.entity

import net.minecraft.entity.projectile.EntityThrowable
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.{DamageSource, EnumFacing}
import net.minecraft.util.math.{BlockPos, RayTraceResult, Vec3d, Vec3i}
import net.minecraft.world.World
import net.retvoid.pressurizeddefence.Predefs._

class EntityTurretProjectile(world: World, pos: Vec3d, damage: Int, speed: Float, weight: Float) extends EntityThrowable(world) {
  setSize(0.2f, 0.2f)
  if (!pos.equals(Vec3d.ZERO)) setPosition(pos.x, pos.y, pos.z)

  def this(world: World, pos: Vec3d, damage: Int, speed: Float) = this(world, pos, damage, speed, 3)
  def this(world: World, pos: Vec3d, damage: Int) = this(world, pos, damage, 5)
  def this(world: World, pos: Vec3d) = this(world, pos, 5)
  def this(world: World) = this(world, Vec3d.ZERO)

  def fireInDirection(direction: EnumFacing): Unit =
    setVelocity(direction.getDirectionVec.getX * speed, direction.getDirectionVec.getY * speed, direction.getDirectionVec.getZ * speed)

  override def getGravityVelocity: Float = weight / 100

  override def onImpact(result: RayTraceResult): Unit = {
    if (result.entityHit != null) {
      result.entityHit.attackEntityFrom(ModDamageSources.TURRET, damage)
    }
    if (!world.isRemote) setDead()
  }
}
