package com.therandomlabs.randomlib;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import net.minecraft.crash.CrashReport;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.util.ReportedException;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;

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
