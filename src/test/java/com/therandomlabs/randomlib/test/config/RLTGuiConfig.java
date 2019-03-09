package com.therandomlabs.randomlib.test.config;

import com.therandomlabs.randomlib.config.ConfigManager;
import com.therandomlabs.randomlib.test.RandomLibTest;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;

public class RLTGuiConfig extends GuiConfig {
	public RLTGuiConfig(GuiScreen parentScreen) {
		super(
				parentScreen,
				ConfigManager.getConfigElements(ConfigTest.class),
				RandomLibTest.MOD_ID,
				RandomLibTest.MOD_ID,
				false,
				false,
				ConfigManager.getPathString(ConfigTest.class)
		);
	}
}
