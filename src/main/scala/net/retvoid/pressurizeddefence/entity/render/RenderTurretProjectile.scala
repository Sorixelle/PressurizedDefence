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

package net.retvoid.pressurizeddefence.entity.render

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.entity.{Render, RenderManager}
import net.minecraft.entity.Entity
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.client.registry.IRenderFactory
import net.retvoid.pressurizeddefence.entity.EntityTurretProjectile
import net.retvoid.pressurizeddefence.item.ItemBullet

class RenderTurretProjectile(renderIn: RenderManager) extends Render[EntityTurretProjectile](renderIn) {
  private val item: ItemStack = new ItemStack(ItemBullet)
  override def getEntityTexture(entity: EntityTurretProjectile): ResourceLocation = null

  override def doRender(entity: EntityTurretProjectile, x: Double, y: Double, z: Double, entityYaw: Float, partialTicks: Float): Unit = {
    GlStateManager.pushMatrix()
    GlStateManager.translate(x, y, z)
    GlStateManager.rotate(entityYaw + 180, 0f, 1f, 0f)
    GlStateManager.scale(0.5, 0.5, 0.5)
    Minecraft.getMinecraft.getRenderItem.renderItem(item, ItemCameraTransforms.TransformType.NONE)
    GlStateManager.popMatrix()
  }
}

object RenderTurretProjectile {
  object Factory extends IRenderFactory[EntityTurretProjectile] {
    override def createRenderFor(manager: RenderManager): Render[_ >: EntityTurretProjectile <: Entity] =
      new RenderTurretProjectile(manager)
  }
}
