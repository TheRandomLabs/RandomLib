package com.therandomlabs.randomlib.config;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.therandomlabs.randomlib.TRLUtils;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

//Enums are implemented as a special case here instead of in TRLTypeAdapters
//Numbers also receive some special treatment
final class TRLProperty {
	private static boolean canSetValidValuesDisplay = true;

	final TRLCategory category;

	final String name;
	final Field field;

	final String languageKey;

	final String previousName;
	final String previousCategory;

	final TRLTypeAdapter adapter;
	final Property.Type type;
	final boolean isArray;
	final boolean isResourceLocation;

	final Class<?> enumClass;
	final Enum[] enumConstants;
	final String[] validValues;
	final String[] validValuesDisplay;

	final Object defaultValue;

	final boolean nonNull;

	final boolean requiresMCRestart;
	final boolean requiresWorldReload;

	final double min;
	final double max;

	final String[] blacklist;

	final String comment;
	final String commentOnDisk;

	@SuppressWarnings("unchecked")
	TRLProperty(TRLCategory category, String name, Field field, String comment, String previous) {
		this.category = category;

		this.name = name;
		this.field = field;

		languageKey = this.category.getLanguageKeyPrefix() + name;

		if(previous == null) {
			previousName = null;
			previousCategory = null;
		} else {
			final String[] data = StringUtils.split(previous, '.');
			previousName = data[data.length - 1];
			previousCategory = StringUtils.join(data, '.', 0, data.length - 1);
		}

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
		isResourceLocation = IForgeRegistryEntry.class.isAssignableFrom(type);

		Object defaultValue = null;

		try {
			defaultValue = field.get(null);
		} catch(IllegalAccessException ex) {
			TRLUtils.crashReport("Failed to load default value of configuration property", ex);
		}

		nonNull = field.getAnnotation(Config.NonNull.class) != null;

		if(defaultValue == null && (!isResourceLocation || nonNull)) {
			throw new IllegalArgumentException(
					"Default value of configuration property may not be null unless it is a " +
							"registry entry without the @Config.NonNull annotation"
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
			smallestMin = -Float.MAX_VALUE;
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
			smallestMin = -Double.MAX_VALUE;
			largestMax = Double.MAX_VALUE;
		}

		final Config.RangeInt rangeInt = field.getAnnotation(Config.RangeInt.class);
		final Config.RangeDouble rangeDouble = field.getAnnotation(Config.RangeDouble.class);

		double min = -Double.MAX_VALUE;
		double max = Double.MAX_VALUE;

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
		}

		if(min == -Double.MAX_VALUE) {
			min = smallestMin;
		}

		if(max == Double.MAX_VALUE) {
			max = largestMax;
		}

		if(min < smallestMin) {
			throw new IllegalArgumentException(String.format(
					"min is too small: %s < %s", min, smallestMin
			));
		}

		if(max > largestMax) {
			throw new IllegalArgumentException(String.format(
					"max is too large: %s > %s", max, largestMax
			));
		}

		this.min = min;
		this.max = max;

		final Config.Blacklist blacklist = field.getAnnotation(Config.Blacklist.class);
		this.blacklist = blacklist == null ? null : blacklist.value();

		if(isArray) {
			for(Object element : TRLUtils.toBoxedArray(defaultValue)) {
				if(ArrayUtils.contains(this.blacklist, adapter.asString(element))) {
					throw new IllegalArgumentException("Default value is blacklisted");
				}
			}
		} else if(ArrayUtils.contains(this.blacklist, adapter.asString(defaultValue))) {
			throw new IllegalArgumentException("Default value is blacklisted");
		}

		this.comment = comment;

		final StringBuilder commentOnDisk = new StringBuilder(comment);

		if(enumConstants != null) {
			commentOnDisk.append("\nValid values:");

			for(Enum element : enumConstants) {
				commentOnDisk.append("\n").append(element.name());
			}
		}

		if(defaultValue instanceof Number) {
			if(defaultValue instanceof Double || defaultValue instanceof Float) {
				commentOnDisk.append("\nMin: ").
						append(min).
						append("\nMax: ").
						append(max);
			} else {
				commentOnDisk.append("\nMin: ").
						append((long) min).
						append("\nMax: ").
						append((long) max);
			}
		}

		commentOnDisk.append("\nDefault: ");

		if(isArray) {
			commentOnDisk.append(
					Arrays.stream(TRLUtils.toBoxedArray(defaultValue)).
							map(adapter::asString).
							collect(Collectors.toList())
			);
		} else {
			commentOnDisk.append(adapter.asString(defaultValue));
		}

		this.commentOnDisk = commentOnDisk.toString();
	}

	boolean exists(Configuration config) {
		return config.getCategory(category.name).get(name) != null || getPrevious(config) != null;
	}

	Property getPrevious(Configuration config) {
		if(previousName == null || !config.hasCategory(previousCategory)) {
			return null;
		}

		final Property property = config.getCategory(previousCategory).get(previousName);
		return property != null && property.getType() == type ? property : null;
	}

	Property get(Configuration config) {
		final ConfigCategory category = this.category.get(config);
		Property property = category.get(name);

		if(property == null) {
			property = getPrevious(config);

			if(property == null) {
				if(isArray) {
					property = new Property(name, new String[0], type);
				} else {
					property = new Property(name, (String) null, type);
				}
			}

			category.put(name, property);
		}

		ConfigManager.setComment(property, comment);
		property.setLanguageKey(languageKey);

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

		if(TRLUtils.MC_VERSION_NUMBER > 11 && canSetValidValuesDisplay) {
			try {
				property.setValidValuesDisplay(validValuesDisplay);
			} catch(NoSuchMethodError e) {
				canSetValidValuesDisplay = false;
			}
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

	Object validate(Object value, boolean isArray) {
		if(value == null && !isResourceLocation) {
			value = defaultValue;
		}

		if(isArray) {
			final boolean primitive = !(value instanceof Object[]);
			final Object[] boxedArray = TRLUtils.toBoxedArray(value);
			final List<Object> filtered = new ArrayList<>();

			for(Object element : boxedArray) {
				if(element != null) {
					filtered.add(validate(element, false));
				}
			}

			final Object[] filteredArray = filtered.toArray(Arrays.copyOf(boxedArray, 0));
			return primitive ? TRLUtils.toPrimitiveArray(filteredArray) : filteredArray;
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
		final Object value = validate(field.get(null), isArray);

		if(enumConstants == null) {
			adapter.setValue(property, value);
			return property;
		}

		if(!isArray) {
			property.setValue(((Enum) value).name());
			return property;
		}

		property.setValues(Arrays.stream((Enum[]) value).map(Enum::name).toArray(String[]::new));
		return property;
	}

	Property deserialize(Configuration config) throws IllegalAccessException {
		final Property property = get(config);

		if(enumConstants == null) {
			final Object value = adapter.getValue(property);

			if(nonNull && value == null) {
				field.set(null, defaultValue);
			} else {
				field.set(null, validate(value, isArray));
			}

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
