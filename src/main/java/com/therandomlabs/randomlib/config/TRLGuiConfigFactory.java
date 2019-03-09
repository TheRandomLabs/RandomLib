package com.therandomlabs.randomlib.config;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import com.therandomlabs.randomlib.TRLUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.minecraftforge.fml.client.config.GuiConfig;

public abstract class TRLGuiConfigFactory implements IModGuiFactory {
	@Override
	public GuiScreen createConfigGui(GuiScreen parentScreen) {
		try {
			return mainConfigGuiClass().getDeclaredConstructor(GuiScreen.class).
					newInstance(parentScreen);
		} catch(NoSuchMethodException | InstantiationException | IllegalAccessException |
				InvocationTargetException ex) {
			TRLUtils.crashReport("Failed to create configuration GUI", ex);
		}

		return null;
	}

	@Override
	public void initialize(Minecraft mc) {}

	@Override
	public boolean hasConfigGui() {
		return true;
	}

	@Override
	public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
		return null;
	}

	public abstract Class<? extends GuiConfig> mainConfigGuiClass();
}
