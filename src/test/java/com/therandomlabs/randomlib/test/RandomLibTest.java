package com.therandomlabs.randomlib.test;

import com.therandomlabs.randomlib.config.CommandConfigReload;
import com.therandomlabs.randomlib.config.ConfigManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(RandomLibTest.MOD_ID)
public final class RandomLibTest {
	public static final String MOD_ID = "randomlibtest";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public RandomLibTest() {
		ConfigManager.register(ConfigTest.class);
		MinecraftForge.EVENT_BUS.addListener(this::serverStarting);
	}

	private void serverStarting(FMLServerStartingEvent event) {
		CommandConfigReload.server(
				event.getCommandDispatcher(), "rltreload", "rltreloadclient", ConfigTest.class,
				"RandomLib Test configuration reloaded!"
		);
	}
}
