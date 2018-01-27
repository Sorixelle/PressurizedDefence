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

package net.retvoid.pressurizeddefence.block

import net.minecraft.block.Block
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.{Item, ItemBlock}
import net.minecraftforge.client.model.ModelLoader
import net.retvoid.pressurizeddefence.PressurizedDefence

class BaseBlock(material: Material, val hasItem: Boolean) extends Block(material) {

  def this(material: Material) = this(material, true)

  override def setUnlocalizedName(name: String): Block = {
    super.setUnlocalizedName(s"${PressurizedDefence.MOD_ID}.$name")
    this
  }

  def setName(name: String): Block = {
    setUnlocalizedName(name)
    setRegistryName(name)
    this
  }

  def asItem: ItemBlock = new ItemBlock(this).setRegistryName(getRegistryName).asInstanceOf[ItemBlock]

  def initModel(): Unit = ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName, "inventory"))
}
