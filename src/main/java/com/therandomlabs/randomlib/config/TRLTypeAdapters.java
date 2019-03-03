package com.therandomlabs.randomlib.config;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import com.google.common.primitives.Booleans;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Chars;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.primitives.Ints;
import com.google.common.primitives.Longs;
import com.google.common.primitives.Shorts;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.registries.IForgeRegistryEntry;

public final class TRLTypeAdapters {
	private static final Map<Class<?>, TRLTypeAdapter> ADAPTERS = new HashMap<>();

	static {
		register(boolean.class, Boolean.class, new TRLTypeAdapter() {
			@Override
			public Object getValue(Property property) {
				return property.getBoolean();
			}

			@Override
			public void setDefaultValue(Property property, Object value) {
				property.setDefaultValue((boolean) value);
			}

			@Override
			public void setValue(Property property, Object value) {
				property.setValue((boolean) value);
			}

			@Override
			public Property.Type getType() {
				return Property.Type.BOOLEAN;
			}
		});

		register(boolean[].class, new TRLTypeAdapter() {
			@Override
			public Object getValue(Property property) {
				return property.getBooleanList();
			}

			@Override
			public void setDefaultValue(Property property, Object value) {
				property.setDefaultValues((boolean[]) value);
			}

			@Override
			public void setValue(Property property, Object value) {
				property.setValues((boolean[]) value);
			}

			@Override
			public Property.Type getType() {
				return Property.Type.BOOLEAN;
			}

			@Override
			public boolean isArray() {
				return true;
			}
		});

		register(Boolean[].class, new TRLTypeAdapter() {
			@Override
			public Object getValue(Property property) {
				return Booleans.asList(property.getBooleanList()).toArray(new Boolean[0]);
			}

			@Override
			public void setDefaultValue(Property property, Object value) {
				property.setDefaultValues(Booleans.toArray(Arrays.asList((Boolean[]) value)));
			}

			@Override
			public void setValue(Property property, Object value) {
				property.setValues(Booleans.toArray(Arrays.asList((Boolean[]) value)));
			}

			@Override
			public Property.Type getType() {
				return Property.Type.BOOLEAN;
			}

			@Override
			public boolean isArray() {
				return true;
			}
		});

		register(byte.class, Byte.class, new TRLTypeAdapter() {
			@Override
			public Object getValue(Property property) {
				return (byte) property.getInt();
			}

			@Override
			public void setDefaultValue(Property property, Object value) {
				property.setDefaultValue((byte) value);
			}

			@Override
			public void setValue(Property property, Object value) {
				property.setValue((byte) value);
			}

			@Override
			public Property.Type getType() {
				return Property.Type.INTEGER;
			}
		});

		register(byte[].class, new TRLTypeAdapter() {
			@Override
			public Object getValue(Property property) {
				return Bytes.toArray(Ints.asList(property.getIntList()));
			}

			@Override
			public void setDefaultValue(Property property, Object value) {
				property.setDefaultValues(Ints.toArray(Bytes.asList((byte[]) value)));
			}

			@Override
			public void setValue(Property property, Object value) {
				property.setValues(Ints.toArray(Bytes.asList((byte[]) value)));
			}

			@Override
			public Property.Type getType() {
				return Property.Type.INTEGER;
			}

			@Override
			public boolean isArray() {
				return true;
			}
		});

		register(Byte[].class, new TRLTypeAdapter() {
			@Override
			public Object getValue(Property property) {
				return Bytes.asList(Bytes.toArray(Ints.asList(property.getIntList()))).
						toArray(new Byte[0]);
			}

			@Override
			public void setDefaultValue(Property property, Object value) {
				property.setDefaultValues(Ints.toArray(Arrays.asList((Byte[]) value)));
			}

			@Override
			public void setValue(Property property, Object value) {
				property.setValues(Ints.toArray(Arrays.asList((Byte[]) value)));
			}

			@Override
			public Property.Type getType() {
				return Property.Type.INTEGER;
			}

			@Override
			public boolean isArray() {
				return true;
			}
		});

		register(char.class, Character.class, new TRLTypeAdapter() {
			@Override
			public Object getValue(Property property) {
				final String string = property.getString();
				return string.isEmpty() ? ' ' : string.charAt(0);
			}

			@Override
			public void setDefaultValue(Property property, Object value) {
				property.setDefaultValue(Character.toString((char) value));
			}

			@Override
			public void setValue(Property property, Object value) {
				property.setValue(Character.toString((char) value));
			}
		});

		register(char[].class, new TRLTypeAdapter() {
			@Override
			public Object getValue(Property property) {
				return Chars.toArray(
						Arrays.stream(property.getStringList()).
								map(string -> string.isEmpty() ? ' ' : string.charAt(0)).
								collect(Collectors.toList())
				);
			}

			@Override
			public void setDefaultValue(Property property, Object value) {
				property.setDefaultValues(
						Chars.asList((char[]) value).stream().
								map(character -> Character.toString(character)).
								toArray(String[]::new)
				);
			}

			@Override
			public void setValue(Property property, Object value) {
				property.setValues(
						Chars.asList((char[]) value).stream().
								map(character -> Character.toString(character)).
								toArray(String[]::new)
				);
			}

			@Override
			public Property.Type getType() {
				return Property.Type.STRING;
			}

			@Override
			public boolean isArray() {
				return true;
			}
		});

		register(Character[].class, new TRLTypeAdapter() {
			@Override
			public Object getValue(Property property) {
				return Arrays.stream(property.getStringList()).
						map(string -> string.isEmpty() ? ' ' : string.charAt(0)).
						toArray(Character[]::new);
			}

			@Override
			public void setDefaultValue(Property property, Object value) {
				property.setDefaultValues(
						Arrays.stream((Character[]) value).
								map(character -> Character.toString(character)).
								toArray(String[]::new)
				);
			}

			@Override
			public void setValue(Property property, Object value) {
				property.setValues(
						Arrays.stream((Character[]) value).
								map(character -> Character.toString(character)).
								toArray(String[]::new)
				);
			}

			@Override
			public Property.Type getType() {
				return Property.Type.STRING;
			}

			@Override
			public boolean isArray() {
				return true;
			}
		});

		register(double.class, Double.class, new TRLTypeAdapter() {
			@Override
			public Object getValue(Property property) {
				return property.getDouble();
			}

			@Override
			public void setDefaultValue(Property property, Object value) {
				property.setDefaultValue((double) value);
			}

			@Override
			public void setValue(Property property, Object value) {
				property.setValue((double) value);
			}

			@Override
			public Property.Type getType() {
				return Property.Type.DOUBLE;
			}
		});

		register(double[].class, new TRLTypeAdapter() {
			@Override
			public Object getValue(Property property) {
				return property.getDoubleList();
			}

			@Override
			public void setDefaultValue(Property property, Object value) {
				property.setDefaultValues((double[]) value);
			}

			@Override
			public void setValue(Property property, Object value) {
				property.setValues((double[]) value);
			}

			@Override
			public Property.Type getType() {
				return Property.Type.DOUBLE;
			}

			@Override
			public boolean isArray() {
				return true;
			}
		});

		register(Double[].class, new TRLTypeAdapter() {
			@Override
			public Object getValue(Property property) {
				return Doubles.asList(property.getDoubleList()).toArray(new Double[0]);
			}

			@Override
			public void setDefaultValue(Property property, Object value) {
				property.setDefaultValues(Doubles.toArray(Arrays.asList((Double[]) value)));
			}

			@Override
			public void setValue(Property property, Object value) {
				property.setValues(Doubles.toArray(Arrays.asList((Double[]) value)));
			}

			@Override
			public Property.Type getType() {
				return Property.Type.DOUBLE;
			}

			@Override
			public boolean isArray() {
				return true;
			}
		});

		register(float.class, Float.class, new TRLTypeAdapter() {
			@Override
			public Object getValue(Property property) {
				return (float) property.getDouble();
			}

			@Override
			public void setDefaultValue(Property property, Object value) {
				property.setDefaultValue((float) value);
			}

			@Override
			public void setValue(Property property, Object value) {
				property.setValue((double) value);
			}

			@Override
			public Property.Type getType() {
				return Property.Type.DOUBLE;
			}
		});

		register(float[].class, new TRLTypeAdapter() {
			@Override
			public Object getValue(Property property) {
				return Floats.toArray(Doubles.asList(property.getDoubleList()));
			}

			@Override
			public void setDefaultValue(Property property, Object value) {
				property.setDefaultValues(Doubles.toArray(Floats.asList((float[]) value)));
			}

			@Override
			public void setValue(Property property, Object value) {
				property.setValues(Doubles.toArray(Floats.asList((float[]) value)));
			}

			@Override
			public Property.Type getType() {
				return Property.Type.DOUBLE;
			}

			@Override
			public boolean isArray() {
				return true;
			}
		});

		register(Float[].class, new TRLTypeAdapter() {
			@Override
			public Object getValue(Property property) {
				return Floats.asList(Floats.toArray(Doubles.asList(property.getDoubleList()))).
						toArray(new Float[0]);
			}

			@Override
			public void setDefaultValue(Property property, Object value) {
				property.setDefaultValues(Doubles.toArray(Arrays.asList((Float[]) value)));
			}

			@Override
			public void setValue(Property property, Object value) {
				property.setValues(Doubles.toArray(Arrays.asList((Float[]) value)));
			}

			@Override
			public Property.Type getType() {
				return Property.Type.DOUBLE;
			}

			@Override
			public boolean isArray() {
				return true;
			}
		});

		register(int.class, Integer.class, new TRLTypeAdapter() {
			@Override
			public Object getValue(Property property) {
				return property.getInt();
			}

			@Override
			public void setDefaultValue(Property property, Object value) {
				property.setDefaultValue((int) value);
			}

			@Override
			public void setValue(Property property, Object value) {
				property.setValue((int) value);
			}

			@Override
			public Property.Type getType() {
				return Property.Type.INTEGER;
			}
		});

		register(int[].class, new TRLTypeAdapter() {
			@Override
			public Object getValue(Property property) {
				return property.getIntList();
			}

			@Override
			public void setDefaultValue(Property property, Object value) {
				property.setDefaultValues((int[]) value);
			}

			@Override
			public void setValue(Property property, Object value) {
				property.setValues((int[]) value);
			}

			@Override
			public Property.Type getType() {
				return Property.Type.INTEGER;
			}

			@Override
			public boolean isArray() {
				return true;
			}
		});

		register(Integer[].class, new TRLTypeAdapter() {
			@Override
			public Object getValue(Property property) {
				return Ints.asList(property.getIntList()).toArray(new Integer[0]);
			}

			@Override
			public void setDefaultValue(Property property, Object value) {
				property.setDefaultValues(Ints.toArray(Arrays.asList((Integer[]) value)));
			}

			@Override
			public void setValue(Property property, Object value) {
				property.setValues(Ints.toArray(Arrays.asList((Integer[]) value)));
			}

			@Override
			public Property.Type getType() {
				return Property.Type.INTEGER;
			}

			@Override
			public boolean isArray() {
				return true;
			}
		});

		register(long.class, Long.class, new TRLTypeAdapter() {
			@Override
			public Object getValue(Property property) {
				return (long) property.getDouble();
			}

			@Override
			public void setDefaultValue(Property property, Object value) {
				property.setDefaultValue((long) value);
			}

			@Override
			public void setValue(Property property, Object value) {
				property.setValue((long) value);
			}

			@Override
			public Property.Type getType() {
				return Property.Type.DOUBLE;
			}
		});

		register(long[].class, new TRLTypeAdapter() {
			@Override
			public Object getValue(Property property) {
				return Longs.toArray(Doubles.asList(property.getDoubleList()));
			}

			@Override
			public void setDefaultValue(Property property, Object value) {
				property.setDefaultValues(Doubles.toArray(Longs.asList((long[]) value)));
			}

			@Override
			public void setValue(Property property, Object value) {
				property.setValues(Doubles.toArray(Longs.asList((long[]) value)));
			}

			@Override
			public Property.Type getType() {
				return Property.Type.DOUBLE;
			}

			@Override
			public boolean isArray() {
				return true;
			}
		});

		register(Long[].class, new TRLTypeAdapter() {
			@Override
			public Object getValue(Property property) {
				return Longs.asList(Longs.toArray(Doubles.asList(property.getDoubleList()))).
						toArray(new Long[0]);
			}

			@Override
			public void setDefaultValue(Property property, Object value) {
				property.setDefaultValues(Doubles.toArray(Arrays.asList((Long[]) value)));
			}

			@Override
			public void setValue(Property property, Object value) {
				property.setValues(Doubles.toArray(Arrays.asList((Long[]) value)));
			}

			@Override
			public Property.Type getType() {
				return Property.Type.DOUBLE;
			}

			@Override
			public boolean isArray() {
				return true;
			}
		});

		register(short.class, Short.class, new TRLTypeAdapter() {
			@Override
			public Object getValue(Property property) {
				return (short) property.getInt();
			}

			@Override
			public void setDefaultValue(Property property, Object value) {
				property.setDefaultValue((short) value);
			}

			@Override
			public void setValue(Property property, Object value) {
				property.setValue((short) value);
			}

			@Override
			public Property.Type getType() {
				return Property.Type.INTEGER;
			}
		});

		register(short[].class, new TRLTypeAdapter() {
			@Override
			public Object getValue(Property property) {
				return Shorts.toArray(Ints.asList(property.getIntList()));
			}

			@Override
			public void setDefaultValue(Property property, Object value) {
				property.setDefaultValues(Ints.toArray(Shorts.asList((short[]) value)));
			}

			@Override
			public void setValue(Property property, Object value) {
				property.setValues(Ints.toArray(Shorts.asList((short[]) value)));
			}

			@Override
			public Property.Type getType() {
				return Property.Type.INTEGER;
			}

			@Override
			public boolean isArray() {
				return true;
			}
		});

		register(Short[].class, new TRLTypeAdapter() {
			@Override
			public Object getValue(Property property) {
				return Shorts.asList(Shorts.toArray(Ints.asList(property.getIntList()))).
						toArray(new Short[0]);
			}

			@Override
			public void setDefaultValue(Property property, Object value) {
				property.setDefaultValues(Ints.toArray(Arrays.asList((Short[]) value)));
			}

			@Override
			public void setValue(Property property, Object value) {
				property.setValues(Ints.toArray(Arrays.asList((Short[]) value)));
			}

			@Override
			public Property.Type getType() {
				return Property.Type.INTEGER;
			}

			@Override
			public boolean isArray() {
				return true;
			}
		});

		register(String.class, Property::getString);

		register(String[].class, new TRLTypeAdapter() {
			@Override
			public Object getValue(Property property) {
				return property.getStringList();
			}

			@Override
			public boolean isArray() {
				return true;
			}
		});
	}

	@SuppressWarnings("unchecked")
	public static TRLTypeAdapter get(Class<?> clazz) {
		final TRLTypeAdapter adapter = ADAPTERS.get(clazz);

		if(adapter != null) {
			return adapter;
		}

		if(IForgeRegistryEntry.class.isAssignableFrom(clazz)) {
			register((Class<? extends IForgeRegistryEntry>) clazz);
		} else if(IForgeRegistryEntry[].class.isAssignableFrom(clazz)) {
			register((
					Class<? extends IForgeRegistryEntry>) clazz.getComponentType()
			);
		} else {
			return null;
		}

		return ADAPTERS.get(clazz);
	}

	public static void register(Class<?> clazz, TRLTypeAdapter adapter) {
		ADAPTERS.put(clazz, adapter);
	}

	public static void register(Class<?> clazz1, Class<?> clazz2, TRLTypeAdapter adapter) {
		register(clazz1, adapter);
		register(clazz2, adapter);
	}

	public static <V extends IForgeRegistryEntry<V>> void register(Class<V> registryEntryClass) {
		register(registryEntryClass, new ResourceLocationTypeAdapter<>(registryEntryClass, false));
		register(
				Array.newInstance(registryEntryClass, 0).getClass(),
				new ResourceLocationTypeAdapter<>(registryEntryClass, true)
		);
	}
}
