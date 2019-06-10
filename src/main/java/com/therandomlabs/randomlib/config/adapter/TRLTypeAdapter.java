package com.therandomlabs.randomlib.config.adapter;

import java.util.Arrays;
import net.minecraftforge.common.config.Property;

public interface TRLTypeAdapter {
	Object getValue(Property property);

	default void setDefaultValue(Property property, Object value) {
		if(isArray()) {
			property.setDefaultValues(
					Arrays.stream((Object[]) value).
							map(this::asString).
							toArray(String[]::new)
			);
		} else {
			property.setDefaultValue(asString(value));
		}
	}

	default void setValue(Property property, Object value) {
		if(isArray()) {
			property.setValues(
					Arrays.stream((Object[]) value).
							map(this::asString).
							toArray(String[]::new)
			);
		} else {
			property.setValue(asString(value));
		}
	}

	default String asString(Object value) {
		return String.valueOf(value);
	}

	default Property.Type getType() {
		return Property.Type.STRING;
	}

	default boolean isArray() {
		return false;
	}

	default boolean shouldLoad() {
		return true;
	}
}
