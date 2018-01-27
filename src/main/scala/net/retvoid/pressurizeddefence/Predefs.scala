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

package net.retvoid.pressurizeddefence

import net.minecraft.util.EnumFacing
import net.minecraft.util.math.{BlockPos, Vec3d, Vec3i}

object Predefs {
  def scale(value: Double, maxValue: Double, maxScaled: Double): Int = Math.ceil((value / maxValue) * maxScaled).toInt

  def centerPosOnFace(pos: Vec3d, face: EnumFacing, offset: Double): Vec3d = pos.addVector(
    face match {
      case EnumFacing.EAST => 1d + offset
      case EnumFacing.WEST => 0d - offset
      case _ => 0.5d
    },
    face match {
      case EnumFacing.UP => 1d + offset
      case EnumFacing.DOWN => 0d - offset
      case _ => 0.5d
    },
    face match {
      case EnumFacing.SOUTH => 1d + offset
      case EnumFacing.NORTH => 0d - offset
      case _ => 0.5d
    }
  )
  def centerPosOnFace(pos: Vec3d, face: EnumFacing): Vec3d = centerPosOnFace(pos, face, 0d)

  implicit class TertiaryHelper(val boolean: Boolean) extends AnyRef {
    def ?[T](val1: T): Tertiary[T] = new Tertiary(boolean, val1)
    class Tertiary[T](bool: Boolean, val1: T) {
      def |(val2: T): T = if (bool) val1 else val2
    }
  }

  implicit class Vec3iToVec3d(vec3i: Vec3i) extends AnyRef {
    def asDoubles: Vec3d = new Vec3d(vec3i.getX, vec3i.getY, vec3i.getZ)
  }

  implicit class BlockPosUtil(bp: BlockPos) extends AnyRef {
    override def toString: String = s"[${bp.getX}, ${bp.getY}, ${bp.getZ}]"
  }
}
