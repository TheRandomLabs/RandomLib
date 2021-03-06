package com.therandomlabs.randomlib;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class CompatForgeRegistryEntry {
	//null on 1.8
	public static final Class<?> CLASS =
			TRLUtils.MC_VERSION_NUMBER > 11 ? IForgeRegistryEntry.class :
					TRLUtils.getClass("net.minecraftforge.fml.common.registry.IForgeRegistryEntry");

	//null on 1.8
	private static final Method GET_REGISTRY_NAME =
			TRLUtils.MC_VERSION_NUMBER > 11 || CLASS == null ? null :
					TRLUtils.findMethod(CLASS, "getRegistryName");

	private final Object entry;

	public CompatForgeRegistryEntry(Object entry) {
		checkSupported();

		if(!CLASS.isAssignableFrom(entry.getClass())) {
			throw new IllegalArgumentException("Not an IForgeRegistryEntry: " + entry);
		}

		this.entry = entry;
	}

	public Object getEntry() {
		return entry;
	}

	public ResourceLocation getRegistryName() {
		checkSupported();

		if(TRLUtils.MC_VERSION_NUMBER > 11) {
			return ((IForgeRegistryEntry) entry).getRegistryName();
		}

		try {
			return (ResourceLocation) GET_REGISTRY_NAME.invoke(entry);
		} catch(IllegalAccessException | InvocationTargetException ex) {
			TRLUtils.crashReport("Failed to get resource location of registry entry", ex);
		}

		return null;
	}

	private static void checkSupported() {
		if(CLASS == null) {
			throw new UnsupportedOperationException("Not supported on Minecraft 1.8");
		}
	}
}
