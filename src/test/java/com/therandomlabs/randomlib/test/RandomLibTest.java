package com.therandomlabs.randomlib.test;

import com.therandomlabs.randomlib.config.ConfigManager;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(RandomLibTest.MOD_ID)
public final class RandomLibTest {
	public static final String MOD_ID = "randomlibtest";
	public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

	public RandomLibTest() {
		ConfigManager.register(ConfigTest.class);
	}

	/*
	* ClientCommandHandler.instance.registerCommand(CommandConfigReload.client(
				"rltreloadclient", ConfigTest.class
		));
	* event.registerServerCommand(CommandConfigReload.server(
				"rltreload", "rltreloadclient", ConfigTest.class,
				"RandomLib Test configuration reloaded!"
		));*/
}
