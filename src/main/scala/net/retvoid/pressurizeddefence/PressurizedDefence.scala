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

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.common.{Mod, SidedProxy}
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.{FMLInitializationEvent, FMLPostInitializationEvent, FMLPreInitializationEvent}
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.retvoid.pressurizeddefence.block.BlockBoiler
import net.retvoid.pressurizeddefence.capability.Capabilities
import net.retvoid.pressurizeddefence.entity.Entities
import net.retvoid.pressurizeddefence.gui.GUIProxy
import net.retvoid.pressurizeddefence.item.upgrade.ItemSpeedUpgrade
import net.retvoid.pressurizeddefence.proxy.CommonProxy
import org.apache.logging.log4j.{LogManager, Logger}

@Mod(
  modid = PressurizedDefence.MOD_ID,
  name = PressurizedDefence.MOD_NAME,
  version = PressurizedDefence.MOD_VERSION,
  modLanguage = "scala",
  useMetadata = true
)
object PressurizedDefence {
  final val MOD_ID = "pressurizeddefence"
  final val MOD_NAME = "Pressurized Defence"
  final val MOD_VERSION = "0.2.2"

  private var loggerOpt: Option[Logger] = None
  def logger: Logger = loggerOpt.getOrElse(LogManager.getLogger(MOD_NAME))

  val creativeTab: CreativeTabs = new CreativeTabs(MOD_ID) {
    override def getTabIconItem: ItemStack = new ItemStack(ItemSpeedUpgrade)
  }

  @SidedProxy(
    clientSide = "net.retvoid.pressurizeddefence.proxy.ClientProxy",
    serverSide = "net.retvoid.pressurizeddefence.proxy.ServerProxy"
  )
  var proxy: CommonProxy = null

  @EventHandler
  def preInit(e: FMLPreInitializationEvent): Unit = {
    loggerOpt = Option(e.getModLog)
    proxy.preInit(e)
    Entities.init()
    Capabilities.registerCapabilities()
  }

  @EventHandler
  def init(e: FMLInitializationEvent): Unit = {
    proxy.init(e)
    NetworkRegistry.INSTANCE.registerGuiHandler(PressurizedDefence, new GUIProxy)
  }

  @EventHandler
  def postInit(e: FMLPostInitializationEvent): Unit = {
    proxy.postInit(e)
  }
}
