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
import net.retvoid.pressurizeddefence.gui.container.TurretContainer
import net.retvoid.pressurizeddefence.tile.TileTurret
import net.retvoid.pressurizeddefence.gui.TurretGUI._
import net.retvoid.pressurizeddefence.Predefs._

class TurretGUI(tile: TileTurret, container: TurretContainer) extends BaseGUI(container) {
  xSize = bgWidth
  ySize = bgHeight

  def steamBarPos: XY = (guiLeft + 27, guiTop + 20)

  override def renderHoveredToolTip(mouseX: Int, mouseY: Int): Unit = {
    if (coordsInAABB((mouseX, mouseY), steamBarPos, steamBarSize))
      drawHoveringText(s"Steam: ${tile.getSteam}/10000", mouseX, mouseY)
    else super.renderHoveredToolTip(mouseX, mouseY)
  }

  override def drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int): Unit = {
    drawCenteredStringWithoutShadow("Turret", xSize / 2, 18, 0x533714)
  }

  override def drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int): Unit = {
    mc.getTextureManager.bindTexture(background)
    drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize)
    if (tile.getSteam != 0)
      drawVerticalProgressBar(steamBarPos, steamBarLoc, steamBarSize, scale(tile.getSteam, 10000, steamBarSize.y))
  }

}

object TurretGUI {
  final val bgWidth = 190
  final val bgHeight = 170

  final val steamBarLoc = (190, 0)
  final val steamBarSize = (20, 55)

  val background: ResourceLocation = new ResourceLocation(PressurizedDefence.MOD_ID, "textures/gui/turret.png")
}