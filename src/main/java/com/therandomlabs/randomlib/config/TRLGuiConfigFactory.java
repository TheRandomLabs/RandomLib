package com.therandomlabs.randomlib.config;

import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.client.IModGuiFactory;

public abstract class TRLGuiConfigFactory implements IModGuiFactory {
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
}
