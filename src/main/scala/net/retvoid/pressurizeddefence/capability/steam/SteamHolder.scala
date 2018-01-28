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

package net.retvoid.pressurizeddefence.capability.steam

class SteamHolder(maxSteam: Int = 10000) extends ISteamHolder {
  var steam: Int = 0

  override def getMaxSteam: Int = maxSteam

  override def getSteam: Int = steam

  override def add(amount: Int, simulate: Boolean): Int =
    if (steam != getMaxSteam) {
      val prev: Int = getSteam
      if (steam + amount <= getMaxSteam) {
        if (!simulate) {
          steam += amount
          onSteamChange(prev)
        }
        amount
      } else {
        val leftover: Int = (steam + amount) - getMaxSteam
        if (!simulate) {
          steam = getMaxSteam
          onSteamChange(prev)
        }
        leftover
      }
    } else 0

  override def consume(amount: Int, simulate: Boolean): Int =
    if (steam != 0) {
      val prev: Int = getSteam
      if (steam - amount >= 0) {
        if (!simulate) {
          steam -= amount
          onSteamChange(prev)
        }
        amount
      } else {
        val removed: Int = steam
        if (!simulate) {
          steam = 0
          onSteamChange(prev)
        }
        removed
      }
    } else 0

  def onSteamChange(prev: Int): Unit = { }

  override def set(amount: Int): Unit = steam =
    if (amount > getMaxSteam) getMaxSteam
    else if (amount < 0) 0
    else amount
}
