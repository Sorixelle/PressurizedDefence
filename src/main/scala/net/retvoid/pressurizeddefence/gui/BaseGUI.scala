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

import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.inventory.Container

import scala.language.implicitConversions

abstract class BaseGUI(container: Container) extends GuiContainer(container) {
  case class XY(x: Int, y: Int)
  implicit def tupleToXY(tuple: (Int, Int)): XY = XY(tuple._1, tuple._2)

  override def drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float): Unit = {
    drawDefaultBackground()
    super.drawScreen(mouseX, mouseY, partialTicks)
    renderHoveredToolTip(mouseX, mouseY)
  }

  def drawCenteredStringWithoutShadow(text: String, x: Int, y: Int, color: Int): Unit =
    fontRenderer.drawString(text, x - fontRenderer.getStringWidth(text) / 2, y, color)

  def drawVerticalProgressBar(guiXY: XY, barPos: XY, barSize: XY, progress: Int): Unit = drawTexturedModalRect(
    guiXY.x, guiXY.y + (barSize.y - progress),
    barPos.x, barPos.y + (barSize.y - progress),
    barSize.x, progress
  )

  def drawHorizontalProgressBar(guiXY: XY, barPos: XY, barSize: XY, progress: Int): Unit = drawTexturedModalRect(
    guiXY.x, guiXY.y,
    barPos.x, barPos.y,
    progress, barSize.y
  )

  def coordsInAABB(xy: XY, topLeft: XY, aabbSize: XY, offsetXY: XY): Boolean =
    xy.x + offsetXY.x >= topLeft.x && xy.x + offsetXY.x <= topLeft.x + aabbSize.x &&
    xy.y + offsetXY.y >= topLeft.y && xy.y + offsetXY.y <= topLeft.y + aabbSize.y

  def coordsInAABB(xy: XY, topLeft: XY, aabbSize: XY): Boolean = coordsInAABB(xy, topLeft, aabbSize, (0, 0))
}
