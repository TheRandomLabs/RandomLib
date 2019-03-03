package com.therandomlabs.randomlib.test.config;

import com.therandomlabs.randomlib.config.TRLGuiConfigFactory;
import net.minecraft.client.gui.GuiScreen;

public class RLTGuiConfigFactory extends TRLGuiConfigFactory {
	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen) {
		return new RLTGuiConfig(parentScreen);
	}
}
