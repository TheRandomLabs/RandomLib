package com.therandomlabs.randomlib;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class CompatForgeRegistry<K> {
	public static final Class<?> CLASS =
			TRLUtils.MC_VERSION_NUMBER > 11 ? IForgeRegistry.class :
					TRLUtils.getClass("net.minecraftforge.fml.common.registry.IForgeRegistry");

	private static final Method FIND_REGISTRY = TRLUtils.MC_VERSION_NUMBER > 11 ?
			null : TRLUtils.findMethod(GameRegistry.class, "findRegistry", Class.class);

	private static final Method GET_VALUE = TRLUtils.MC_VERSION_NUMBER > 11 ?
			null : TRLUtils.findMethod(CLASS, "getValue", ResourceLocation.class);

	private final Object registry;

	public CompatForgeRegistry(Object registry) {
		if(!CLASS.isAssignableFrom(registry.getClass())) {
			throw new IllegalArgumentException("Not an IForgeRegistry: " + registry);
		}

		this.registry = registry;
	}

	public Object getRegistry() {
		return registry;
	}

	@SuppressWarnings("unchecked")
	public K getValue(ResourceLocation key) {
		if(TRLUtils.MC_VERSION_NUMBER > 11) {
			return (K) ((IForgeRegistry) registry).getValue(key);
		}

		try {
			return (K) GET_VALUE.invoke(registry);
		} catch(IllegalAccessException | InvocationTargetException ex) {
			TRLUtils.crashReport("Failed to get registry entry with key: " + key, ex);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public static <K> CompatForgeRegistry<K> findRegistry(Class<K> clazz) {
		if(TRLUtils.MC_VERSION_NUMBER > 11) {
			return new CompatForgeRegistry(
					GameRegistry.findRegistry((Class<? extends IForgeRegistryEntry>) clazz)
			);
		}

		try {
			return new CompatForgeRegistry<>(FIND_REGISTRY.invoke(null, clazz));
		} catch(IllegalAccessException | InvocationTargetException ex) {
			TRLUtils.crashReport("Failed to get Forge registry: " + clazz.getName(), ex);
		}

		return null;
	}
}
