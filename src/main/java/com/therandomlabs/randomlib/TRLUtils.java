package com.therandomlabs.randomlib;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import net.minecraft.crash.CrashReport;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ReportedException;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.apache.commons.lang3.ArrayUtils;

public final class TRLUtils {
	public static final boolean IS_DEOBFUSCATED =
			(boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");
	public static final boolean IS_CLIENT = FMLLaunchHandler.side().isClient();
	public static final String MC_VERSION = (String) FMLInjectionData.data()[4];
	public static final int MC_VERSION_NUMBER = Integer.parseInt(MC_VERSION.split("\\.")[1]);

	private TRLUtils() {}

	@SuppressWarnings("deprecation")
	public static String localize(String key, Object... args) {
		//Use the fully qualified class name so the compiler doesn't throw a warning at us
		//https://bugs.openjdk.java.net/browse/JDK-8032211
		return net.minecraft.util.text.translation.I18n.translateToLocalFormatted(key, args);
	}

	public static Object toPrimitiveArray(Object[] boxedArray) {
		if(boxedArray instanceof Boolean[]) {
			return ArrayUtils.toPrimitive((Boolean[]) boxedArray);
		}

		if(boxedArray instanceof Byte[]) {
			return ArrayUtils.toPrimitive((Byte[]) boxedArray);
		}

		if(boxedArray instanceof Character[]) {
			return ArrayUtils.toPrimitive((Character[]) boxedArray);
		}

		if(boxedArray instanceof Double[]) {
			return ArrayUtils.toPrimitive((Double[]) boxedArray);
		}

		if(boxedArray instanceof Float[]) {
			return ArrayUtils.toPrimitive((Float[]) boxedArray);
		}

		if(boxedArray instanceof Integer[]) {
			return ArrayUtils.toPrimitive((Integer[]) boxedArray);
		}

		if(boxedArray instanceof Long[]) {
			return ArrayUtils.toPrimitive((Long[]) boxedArray);
		}

		if(boxedArray instanceof Short[]) {
			return ArrayUtils.toPrimitive((Short[]) boxedArray);
		}

		return boxedArray;
	}

	public static Object[] toBoxedArray(Object primitiveArray) {
		if(primitiveArray instanceof Object[]) {
			return (Object[]) primitiveArray;
		}

		if(primitiveArray instanceof boolean[]) {
			return ArrayUtils.toObject((byte[]) primitiveArray);
		}

		if(primitiveArray instanceof byte[]) {
			return ArrayUtils.toObject((byte[]) primitiveArray);
		}

		if(primitiveArray instanceof char[]) {
			return ArrayUtils.toObject((char[]) primitiveArray);
		}

		if(primitiveArray instanceof double[]) {
			return ArrayUtils.toObject((double[]) primitiveArray);
		}

		if(primitiveArray instanceof float[]) {
			return ArrayUtils.toObject((float[]) primitiveArray);
		}

		if(primitiveArray instanceof int[]) {
			return ArrayUtils.toObject((int[]) primitiveArray);
		}

		if(primitiveArray instanceof long[]) {
			return ArrayUtils.toObject((long[]) primitiveArray);
		}

		if(primitiveArray instanceof short[]) {
			return ArrayUtils.toObject((long[]) primitiveArray);
		}

		return null;
	}

	public static Field findField(Class<?> clazz, String... names) {
		for(Field field : clazz.getDeclaredFields()) {
			for(String name : names) {
				if(name.equals(field.getName())) {
					field.setAccessible(true);
					return field;
				}
			}
		}

		return null;
	}

	public static Method findMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
		return findMethod(clazz, name, name, parameterTypes);
	}

	public static Method findMethod(Class<?> clazz, String name, String obfName,
			Class<?>... parameterTypes) {
		for(Method method : clazz.getDeclaredMethods()) {
			final String methodName = method.getName();

			if((name.equals(methodName) || obfName.equals(methodName)) &&
					Arrays.equals(method.getParameterTypes(), parameterTypes)) {
				method.setAccessible(true);
				return method;
			}
		}

		return null;
	}

	public static void crashReport(String message, Throwable throwable) {
		throw new ReportedException(new CrashReport(message, throwable));
	}
}
