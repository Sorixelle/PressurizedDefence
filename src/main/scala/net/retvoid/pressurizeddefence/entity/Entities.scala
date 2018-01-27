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

import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.registry.RenderingRegistry
import net.minecraftforge.fml.common.registry.EntityRegistry
import net.minecraftforge.fml.relauncher.{Side, SideOnly}
import net.retvoid.pressurizeddefence.PressurizedDefence
import net.retvoid.pressurizeddefence.entity.render.RenderTurretProjectile

object Entities {
  def init(): Unit = {
    EntityRegistry.registerModEntity(
      new ResourceLocation(s"${PressurizedDefence.MOD_ID}:turret_projectile"), classOf[EntityTurretProjectile],
        s"${PressurizedDefence.MOD_ID}.turret_projectile", 1, PressurizedDefence, 64, 20, true)
  }

  @SideOnly(Side.CLIENT)
  def initModels(): Unit = {
    RenderingRegistry.registerEntityRenderingHandler(classOf[EntityTurretProjectile], RenderTurretProjectile.Factory)
  }
}
