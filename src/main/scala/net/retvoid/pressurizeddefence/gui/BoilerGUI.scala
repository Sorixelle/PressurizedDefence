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

package net.retvoid.pressurizeddefence.gui

import net.minecraft.util.ResourceLocation
import net.retvoid.pressurizeddefence.PressurizedDefence
import net.retvoid.pressurizeddefence.gui.container.BoilerContainer
import net.retvoid.pressurizeddefence.tile.TileBoiler
import net.retvoid.pressurizeddefence.gui.BoilerGUI._
import net.retvoid.pressurizeddefence.Predefs._

class BoilerGUI(tile: TileBoiler, container: BoilerContainer) extends BaseGUI(container) {
  xSize = bgWidth
  ySize = bgHeight

  def burnIndicatorPos: XY = (guiLeft + 88, guiTop + 52)
  def steamBarPos: XY = (guiLeft + 27, guiTop + 20)

  override def renderHoveredToolTip(x: Int, y: Int): Unit = {
    if (coordsInAABB((x, y), steamBarPos, steamBarSize))
      drawHoveringText(s"Steam: ${tile.getSteam}/10000", x, y)
    else if (coordsInAABB((x, y), burnIndicatorPos, burnIndicatorSize) && tile.burnTime != 0)
      drawHoveringText(s"Burn time: ${math.rint((tile.burnTime / 20D) * 10) / 10}s / ${tile.maxBurnTime / 20}s", x, y)
    else super.renderHoveredToolTip(x, y)
  }

  override def drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int): Unit = {
    drawCenteredStringWithoutShadow("Steam Boiler", xSize / 2, 18, 0x533714)
  }

  override def drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int): Unit = {
    mc.getTextureManager.bindTexture(background)
    drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize)
    if (tile.maxBurnTime != 0)
      drawVerticalProgressBar(burnIndicatorPos, burnIndicatorLoc, burnIndicatorSize, scale(tile.burnTime, tile.maxBurnTime, burnIndicatorSize.y))
    if (tile.getSteam != 0)
      drawVerticalProgressBar(steamBarPos, steamBarLoc, steamBarSize, scale(tile.getSteam, 10000, steamBarSize.y))
  }
}

object BoilerGUI {
  final val bgWidth = 190
  final val bgHeight = 170

  final val burnIndicatorLoc = (190, 0)
  final val burnIndicatorSize = (14, 14)
  final val steamBarLoc = (190, 14)
  final val steamBarSize = (20, 55)

  val background: ResourceLocation = new ResourceLocation(PressurizedDefence.MOD_ID, "textures/gui/boiler.png")
}
