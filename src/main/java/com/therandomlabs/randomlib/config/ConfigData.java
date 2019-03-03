package com.therandomlabs.randomlib.config;

import java.nio.file.Path;
import java.util.List;
import net.minecraftforge.common.config.Configuration;

final class ConfigData {
	final Class<?> clazz;
	final String pathString;
	final Path path;
	final List<TRLCategory> categories;
	final Configuration config;

	ConfigData(Class<?> clazz, String pathString, Path path, List<TRLCategory> categories) {
		this.clazz = clazz;
		this.pathString = pathString;
		this.path = path;
		this.categories = categories;
		config = new Configuration(path.toFile());
	}
}
