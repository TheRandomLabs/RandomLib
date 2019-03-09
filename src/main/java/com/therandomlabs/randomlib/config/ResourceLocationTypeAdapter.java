package com.therandomlabs.randomlib.config;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

public final class ResourceLocationTypeAdapter<V extends IForgeRegistryEntry<V>> implements
		TRLTypeAdapter {
	private final Class<V> registryEntryClass;
	private final IForgeRegistry<V> registry;
	private final boolean isArray;

	public ResourceLocationTypeAdapter(Class<V> registryEntryClass, boolean isArray) {
		this.registryEntryClass = registryEntryClass;
		registry = GameRegistry.findRegistry(registryEntryClass);
		this.isArray = isArray;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object getValue(Property property) {
		if(isArray) {
			final String[] array = property.getStringList();
			final List<V> values = new ArrayList<>(array.length);

			for(String element : array) {
				final V object = registry.getValue(new ResourceLocation(element));

				if(object != null) {
					values.add(object);
				}
			}

			return values.toArray((V[]) Array.newInstance(registryEntryClass, 0));
		}

		final V object = registry.getValue(new ResourceLocation(property.getString()));
		return object == null ?
				registry.getValue(new ResourceLocation(property.getDefault())) : object;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String asString(Object value) {
		return ((V) value).getRegistryName().toString();
	}

	@Override
	public Property.Type getType() {
		return Property.Type.STRING;
	}

	@Override
	public boolean isArray() {
		return isArray;
	}
}
