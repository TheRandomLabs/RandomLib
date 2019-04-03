package com.therandomlabs.randomlib.config;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import com.therandomlabs.randomlib.CompatForgeRegistry;
import com.therandomlabs.randomlib.CompatForgeRegistryEntry;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderState;

public final class ResourceLocationTypeAdapter implements TRLTypeAdapter {
	private final Class<?> registryEntryClass;
	private final CompatForgeRegistry<?> registry;
	private final boolean isArray;

	public ResourceLocationTypeAdapter(Class<?> registryEntryClass, boolean isArray) {
		this.registryEntryClass = registryEntryClass;
		registry = CompatForgeRegistry.findRegistry(registryEntryClass);
		this.isArray = isArray;
	}

	@Override
	public Object getValue(Property property) {
		if(isArray) {
			final String[] array = property.getStringList();
			final List<Object> values = new ArrayList<>(array.length);

			for(String element : array) {
				final Object object =
						registry.getValue(new ResourceLocation(element.replaceAll("\\s", "")));

				if(object != null) {
					values.add(object);
				}
			}

			return values.toArray((Object[]) Array.newInstance(registryEntryClass, 0));
		}

		final String location = property.getString();

		if(location.isEmpty()) {
			return null;
		}

		final Object object = registry.getValue(new ResourceLocation(location.replaceAll("\\s", "")));
		return object == null ?
				registry.getValue(new ResourceLocation(property.getDefault())) : object;
	}

	@Override
	public String asString(Object value) {
		return value == null ? "" : new CompatForgeRegistryEntry(value).getRegistryName().toString();
	}

	@Override
	public Property.Type getType() {
		return Property.Type.STRING;
	}

	@Override
	public boolean isArray() {
		return isArray;
	}

	@Override
	public boolean shouldLoad() {
		return Loader.instance().hasReachedState(LoaderState.INITIALIZATION);
	}
}
