package com.therandomlabs.randomlib.config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import com.therandomlabs.randomlib.TRLUtils;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

final class TRLCategory {
	final String languageKeyPrefix;
	final String languageKey;
	final Class<?> clazz;
	final String comment;
	final String name;
	final List<TRLProperty> properties = new ArrayList<>();

	final Method onReload;
	final Method onReloadClient;

	TRLCategory(String languageKeyPrefix, Class<?> clazz, String comment, String name) {
		this.languageKeyPrefix = languageKeyPrefix;
		languageKey = languageKeyPrefix + name;
		this.clazz = clazz;
		this.comment = comment;
		this.name = TRLUtils.MC_VERSION_NUMBER == 8 ? name.toLowerCase(Locale.ENGLISH) : name;
		onReload = getOnReloadMethod(clazz, "onReload");
		onReloadClient = getOnReloadMethod(clazz, "onReloadClient");
	}

	void onReload(boolean client) {
		final Method method = client ? onReloadClient : onReload;

		if(method != null) {
			try {
				method.invoke(null);
			} catch(IllegalAccessException | InvocationTargetException ex) {
				TRLUtils.crashReport("Failed to reload configuration category", ex);
			}
		}
	}

	ConfigCategory get(Configuration config) {
		final boolean hasCategory = config.hasCategory(name);
		final ConfigCategory category = config.getCategory(name);

		//Backwards compatibility - Forge's config annotation system has case insensitive
		//category names, so old configs will still have lowercase category names
		if(!hasCategory) {
			final String lowerCase = name.toLowerCase(Locale.ENGLISH);

			if(config.hasCategory(lowerCase)) {
				final ConfigCategory oldCategory = config.getCategory(lowerCase);
				category.putAll(oldCategory.getValues());
			}
		}

		config.setCategoryComment(name, comment);
		config.setCategoryLanguageKey(name, languageKey);

		return category;
	}

	void createPropertyOrder(Configuration config) {
		get(config).setPropertyOrder(
				properties.stream().map(property -> property.name).collect(Collectors.toList())
		);
	}

	String getLanguageKeyPrefix() {
		return languageKey + ".";
	}

	private static Method getOnReloadMethod(Class<?> clazz, String name) {
		final Method onReload = TRLUtils.findMethod(clazz, name);

		if(onReload != null) {
			final int modifiers = onReload.getModifiers();

			if(!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers) ||
					onReload.getReturnType() != void.class) {
				throw new IllegalArgumentException(name + " must be public static void");
			}
		}

		return onReload;
	}
}
