package com.therandomlabs.randomlib.config;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.therandomlabs.randomlib.TRLUtils;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import org.apache.commons.lang3.ArrayUtils;

//Enums are implemented as a special case here instead of in TRLTypeAdapters
//Numbers also receive some special treatment
final class TRLProperty {
	final TRLCategory category;

	final String name;
	final Field field;

	final TRLTypeAdapter adapter;
	final Property.Type type;
	final boolean isArray;

	final Class<?> enumClass;
	final Enum[] enumConstants;
	final String[] validValues;
	final String[] validValuesDisplay;

	final Object defaultValue;

	final boolean requiresMCRestart;
	final boolean requiresWorldReload;

	final double min;
	final double max;

	final String[] blacklist;

	final String comment;
	final String commentOnDisk;

	@SuppressWarnings("unchecked")
	TRLProperty(TRLCategory category, String name, Field field, String comment) {
		this.category = category;

		this.name = name;
		this.field = field;

		final Class<?> type = field.getType();

		if(Enum.class.isAssignableFrom(type)) {
			enumClass = type;
			adapter = TRLTypeAdapters.get(String.class);
		} else if(Enum[].class.isAssignableFrom(type)) {
			enumClass = type.getComponentType();
			adapter = TRLTypeAdapters.get(String[].class);
		} else {
			enumClass = null;
			adapter = TRLTypeAdapters.get(type);
		}

		if(enumClass == null) {
			enumConstants = null;
			validValues = null;
			validValuesDisplay = null;
		} else {
			final List<String> validValues = new ArrayList<>();
			final List<String> validValuesDisplay = new ArrayList<>();

			enumConstants = ((Class<? extends Enum>) enumClass).getEnumConstants();

			for(Enum element : enumConstants) {
				validValues.add(element.name());
				validValuesDisplay.add(element.toString());
			}

			this.validValues = validValues.toArray(new String[0]);
			this.validValuesDisplay = validValuesDisplay.toArray(new String[0]);
		}

		this.type = adapter.getType();
		isArray = adapter.isArray();

		Object defaultValue = null;

		try {
			defaultValue = field.get(null);
		} catch(IllegalAccessException ex) {
			TRLUtils.crashReport("Failed to load default value of configuration property", ex);
		}

		if(defaultValue == null) {
			throw new IllegalArgumentException(
					"Default value of configuration property may not be null"
			);
		}

		this.defaultValue = defaultValue;

		requiresMCRestart = field.getAnnotation(Config.RequiresMCRestart.class) != null;
		requiresWorldReload = field.getAnnotation(Config.RequiresWorldReload.class) != null;

		if(requiresMCRestart && requiresWorldReload) {
			throw new IllegalArgumentException(
					"A property cannot both require a Minecraft restart and a world reload"
			);
		}

		final double smallestMin;
		final double largestMax;

		if(defaultValue instanceof Byte) {
			smallestMin = Byte.MIN_VALUE;
			largestMax = Byte.MAX_VALUE;
		} else if(defaultValue instanceof Float) {
			smallestMin = Float.MIN_VALUE;
			largestMax = Float.MAX_VALUE;
		} else if(defaultValue instanceof Integer) {
			smallestMin = Integer.MIN_VALUE;
			largestMax = Integer.MAX_VALUE;
		} else if(defaultValue instanceof Long) {
			smallestMin = Long.MIN_VALUE;
			largestMax = Long.MAX_VALUE;
		} else if(defaultValue instanceof Short) {
			smallestMin = Short.MIN_VALUE;
			largestMax = Short.MAX_VALUE;
		} else {
			smallestMin = Double.MIN_VALUE;
			largestMax = Double.MAX_VALUE;
		}

		final Config.RangeInt rangeInt = field.getAnnotation(Config.RangeInt.class);
		final Config.RangeDouble rangeDouble = field.getAnnotation(Config.RangeDouble.class);

		if(rangeInt != null) {
			if(rangeDouble != null) {
				throw new IllegalArgumentException("Two ranges cannot be defined");
			}

			min = rangeInt.min();
			max = rangeInt.max();

			if(min > max) {
				throw new IllegalArgumentException("min cannot be larger than max");
			}
		} else if(rangeDouble != null) {
			min = rangeDouble.min();
			max = rangeDouble.max();

			if(min > max) {
				throw new IllegalArgumentException("min cannot be larger than max");
			}
		} else {
			min = smallestMin;
			max = largestMax;
		}

		if(min < smallestMin) {
			throw new IllegalArgumentException("min is too small");
		}

		if(max > largestMax) {
			throw new IllegalArgumentException("max is too large");
		}

		final Config.Blacklist blacklist = field.getAnnotation(Config.Blacklist.class);
		this.blacklist = blacklist == null ? null : blacklist.value();

		this.comment = comment;
		commentOnDisk = comment; //TODO

		//TODO ensure default value is not blacklisted
	}

	boolean exists(Configuration config) {
		return config.getCategory(category.name).get(name) != null;
	}

	Property get(Configuration config) {
		final ConfigCategory category = this.category.get(config);
		Property property = category.get(name);

		if(property == null) {
			if(isArray) {
				property = new Property(name, new String[0], type);
			} else {
				property = new Property(name, (String) null, type);
			}

			category.put(name, property);
		}

		ConfigManager.setComment(property, comment);
		property.setLanguageKey(this.category.getLanguageKeyPrefix() + name);

		if(enumClass == null) {
			adapter.setDefaultValue(property, defaultValue);
		} else {
			if(isArray) {
				property.setDefaultValues(
						Arrays.stream((Enum[]) defaultValue).
								map(Enum::name).
								toArray(String[]::new)
				);
			} else {
				property.setDefaultValue(((Enum) defaultValue).name());
			}
		}

		property.setValidValues(validValues);

		if(TRLUtils.MC_VERSION_NUMBER > 11) {
			property.setValidValuesDisplay(validValuesDisplay);
		}

		if(defaultValue instanceof Double || defaultValue instanceof Float) {
			property.setMinValue(min);
			property.setMaxValue(max);
		} else if(defaultValue instanceof Number) {
			property.setMinValue((int) min);
			property.setMaxValue((int) max);
		}

		return property;
	}

	Object validate(Object value) {
		if(isArray) {
			final List<Object> filtered = new ArrayList<>();

			for(Object element : (Object[]) value) {
				if(ArrayUtils.contains(blacklist, adapter.asString(element))) {
					filtered.add(defaultValue);
				} else {
					filtered.add(element);
				}
			}

			return filtered.toArray(Arrays.copyOf((Object[]) value, 0));
		} else if(ArrayUtils.contains(blacklist, adapter.asString(value))) {
			return defaultValue;
		}

		if(value instanceof Number) {
			double number = ((Number) value).doubleValue();

			if(number < min) {
				number = min;
			} else if(number > max) {
				number = max;
			}

			if(value instanceof Byte) {
				return (byte) number;
			}

			if(value instanceof Double) {
				return number;
			}

			if(value instanceof Float) {
				return (float) number;
			}

			if(value instanceof Integer) {
				return (int) number;
			}

			if(value instanceof Long) {
				return (long) number;
			}

			if(value instanceof Short) {
				return (short) number;
			}
		}

		return value;
	}

	Property serialize(Configuration config) throws IllegalAccessException {
		final Property property = get(config);

		if(enumConstants == null) {
			adapter.setValue(property, field.get(null));
			return property;
		}

		if(!isArray) {
			property.setValue(((Enum) field.get(null)).name());
			return property;
		}

		property.setValues(
				Arrays.stream((Enum[]) field.get(null)).
						map(Enum::name).
						toArray(String[]::new)
		);
		return property;
	}

	Property deserialize(Configuration config) throws IllegalAccessException {
		final Property property = get(config);

		if(enumConstants == null) {
			field.set(null, validate(adapter.getValue(property)));
			return property;
		}

		if(!isArray) {
			for(Enum element : enumConstants) {
				if(element.name().equalsIgnoreCase(property.getString())) {
					field.set(null, element);
					return property;
				}
			}

			field.set(null, defaultValue);
			return property;
		}

		final String[] values = property.getStringList();
		final List<Object> enumValues = new ArrayList<>(values.length);

		for(String value : values) {
			for(Enum element : enumConstants) {
				if(element.name().equalsIgnoreCase(value)) {
					enumValues.add(element);
					break;
				}
			}
		}

		field.set(null, enumValues.toArray((Object[]) Array.newInstance(enumClass, 0)));
		return property;
	}
}
