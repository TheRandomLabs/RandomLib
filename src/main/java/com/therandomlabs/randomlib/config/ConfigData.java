package com.therandomlabs.randomlib.config;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.therandomlabs.randomlib.TRLUtils;
import net.minecraftforge.common.config.Configuration;

final class ConfigData {
	final Class<?> clazz;
	final String pathString;
	final Path path;
	final List<TRLCategory> categories;
	final Configuration config;
	final Map<String, String> delayedLoad = new HashMap<>();

	ConfigData(Class<?> clazz, String pathString, Path path, List<TRLCategory> categories) {
		this.clazz = clazz;
		this.pathString = pathString;
		this.path = path;
		this.categories = categories;
		config = new Configuration(path.toFile(), TRLUtils.MC_VERSION_NUMBER != 8);
	}
}
