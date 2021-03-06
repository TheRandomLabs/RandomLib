package com.therandomlabs.randomlib.test;

import com.therandomlabs.randomlib.config.CommandConfigReload;
import com.therandomlabs.randomlib.config.ConfigColor;
import com.therandomlabs.randomlib.config.ConfigManager;
import com.therandomlabs.randomlib.test.config.ConfigTest;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
		modid = RandomLibTest.MOD_ID, name = RandomLibTest.NAME, version = RandomLibTest.VERSION,
		guiFactory = RandomLibTest.GUI_FACTORY, acceptedMinecraftVersions = "[1.8,1.13)"
)
public final class RandomLibTest {
	public static final String MOD_ID = "randomlibtest";
	public static final String NAME = "RandomLib Test";
	public static final String VERSION = "1.12.2-1.0.0.0";
	public static final String GUI_FACTORY =
			"com.therandomlabs.randomlib.test.config.RLTGuiConfigFactory";

	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	@Mod.EventHandler
	public static void preInit(FMLPreInitializationEvent event) {
		ConfigColor.setTranslationKeyPrefix("randomlibtest.config.color.");
		ConfigManager.register(ConfigTest.class);
		ClientCommandHandler.instance.registerCommand(CommandConfigReload.client(
				"rltreloadclient", ConfigTest.class
		));
	}

	@Mod.EventHandler
	public static void init(FMLInitializationEvent event) {
		ConfigManager.reloadFromDisk(ConfigTest.class);
	}

	@Mod.EventHandler
	public static void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(CommandConfigReload.server(
				"rltreload", "rltreloadclient", ConfigTest.class,
				"RandomLib Test configuration reloaded!"
		));
	}
}
