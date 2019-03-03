package com.therandomlabs.randomlib.config;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.therandomlabs.randomlib.TRLUtils;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;

final class TRLCategory {
	final String languageKeyPrefix;
	final Class<?> clazz;
	final String comment;
	final String name;
	final List<TRLProperty> properties = new ArrayList<>();

	final Method onReload;

	TRLCategory(String languageKeyPrefix, Class<?> clazz, String comment, String name) {
		this.languageKeyPrefix = languageKeyPrefix;
		this.clazz = clazz;
		this.comment = comment;
		this.name = name;

		onReload = TRLUtils.findMethod(clazz, "onReload");

		if(onReload != null) {
			final int modifiers = onReload.getModifiers();

			if(!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers) ||
					onReload.getReturnType() != void.class) {
				throw new IllegalArgumentException("onReload must be public static void");
			}
		}
	}

	void onReload() {
		if(onReload != null) {
			try {
				onReload.invoke(null);
			} catch(IllegalAccessException | InvocationTargetException ex) {
				TRLUtils.crashReport("Failed to reload configuration category", ex);
			}
		}
	}

	ConfigCategory get(Configuration config) {
		final ConfigCategory category = config.getCategory(name);

		config.setCategoryComment(name, comment);
		config.setCategoryLanguageKey(name, languageKeyPrefix + name);

		return category;
	}

	void createPropertyOrder(Configuration config) {
		get(config).setPropertyOrder(
				properties.stream().map(property -> property.name).collect(Collectors.toList())
		);
	}

	String getLanguageKeyPrefix() {
		return languageKeyPrefix + name + ".";
	}
}
