package com.therandomlabs.randomlib.config;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.therandomlabs.randomlib.TRLUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.client.config.IConfigElement;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.StringUtils;

public final class ConfigManager {
	private static final Map<Class<?>, ConfigData> CONFIGS = new HashMap<>();
	private static final Map<String, List<ConfigData>> MODID_TO_CONFIGS = new HashMap<>();

	private static final Field COMMENT = TRLUtils.MC_VERSION_NUMBER == 8 ?
			TRLUtils.findField(Property.class, "comment") : null;

	private ConfigManager() {}

	public static void register(Class<?> clazz) {
		final Config config = clazz.getAnnotation(Config.class);

		if(config == null) {
			throw new IllegalArgumentException(clazz.getName() + " is not a configuration class");
		}

		//We have to assume it's valid since if this is being loaded before Minecraft Forge is
		//initialized (i.e. in a coremod), Loader.isModLoaded cannot be called
		final String modid = config.modid();

		//Ensure path is valid by initializing it first
		final String pathData = config.path();
		final String pathString = "config/" + (pathData.isEmpty() ? modid : config.path()) + ".cfg";
		final Path path = Paths.get(pathString).toAbsolutePath();

		final List<TRLCategory> categories = new ArrayList<>();
		loadCategories(modid + ".config.", "", clazz, categories);
		final ConfigData data = new ConfigData(clazz, pathString, path, categories);

		if(CONFIGS.isEmpty()) {
			MinecraftForge.EVENT_BUS.register(ConfigManager.class);
		}

		CONFIGS.put(clazz, data);
		MODID_TO_CONFIGS.computeIfAbsent(modid, id -> new ArrayList<>()).add(data);

		reloadFromDisk(clazz);
	}

	public static void reloadFromDisk(Class<?> clazz) {
		final ConfigData data = CONFIGS.get(clazz);
		data.config.load();
		reloadFromConfig(clazz);
	}

	public static void reloadFromConfig(Class<?> clazz) {
		final ConfigData data = CONFIGS.get(clazz);

		for(TRLCategory category : data.categories) {
			for(TRLProperty property : category.properties) {
				if(property.exists(data.config)) {
					try {
						property.deserialize(data.config);
					} catch(Exception ex) {
						TRLUtils.crashReport(
								"Failed to deserialize configuration property " + property.name, ex
						);
					}
				}
			}
		}

		writeToDisk(clazz);
	}

	public static void writeToDisk(Class<?> clazz) {
		final ConfigData data = CONFIGS.get(clazz);

		//Reset configuration to remove old properties and categories
		data.config.getCategoryNames().
				forEach(name -> data.config.removeCategory(data.config.getCategory(name)));

		data.categories.forEach(category -> category.createPropertyOrder(data.config));

		for(TRLCategory category : data.categories) {
			category.onReload();

			for(TRLProperty property : category.properties) {
				try {
					setComment(property.serialize(data.config), property.commentOnDisk);
				} catch(Exception ex) {
					TRLUtils.crashReport(
							"Failed to serialize configuration property " + property.name, ex
					);
				}
			}
		}

		data.config.save();

		//Reset comments
		for(TRLCategory category : data.categories) {
			for(TRLProperty property : category.properties) {
				property.get(data.config); //Sets comment back to normal
			}
		}
	}

	public static Configuration get(Class<?> clazz) {
		return CONFIGS.get(clazz).config;
	}

	public static List<IConfigElement> getConfigElements(Class<?> clazz) {
		final Configuration config = get(clazz);

		return config.getCategoryNames().stream().
				map(name -> new ConfigElement(config.getCategory(name))).
				collect(Collectors.toList());
	}

	public static String getPathString(Class<?> clazz) {
		return CONFIGS.get(clazz).pathString;
	}

	public static Path getPath(Class<?> clazz) {
		return CONFIGS.get(clazz).path;
	}

	public static String getComment(Property property) {
		if(TRLUtils.MC_VERSION_NUMBER != 8) {
			return property.getComment();
		}

		try {
			return (String) COMMENT.get(property);
		} catch(Exception ex) {
			TRLUtils.crashReport("Error while getting configuration property comment", ex);
		}

		return null;
	}

	public static void setComment(Property property, String comment) {
		if(TRLUtils.MC_VERSION_NUMBER != 8) {
			property.setComment(comment);
			return;
		}

		try {
			COMMENT.set(property, comment);
		} catch(Exception ex) {
			TRLUtils.crashReport("Error while setting configuration property comment", ex);
		}
	}

	@SubscribeEvent
	public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
		MODID_TO_CONFIGS.computeIfAbsent(event.getModID(), id -> new ArrayList<>()).
				forEach(data -> reloadFromConfig(data.clazz));
	}

	private static void loadCategories(String languageKeyPrefix, String parentCategory,
			Class<?> clazz, List<TRLCategory> categories) {
		for(Field field : clazz.getDeclaredFields()) {
			final Config.Category categoryData = field.getAnnotation(Config.Category.class);

			if(categoryData == null) {
				continue;
			}

			final String comment = StringUtils.join(categoryData.value(), "\n");

			if(comment.trim().isEmpty()) {
				throw new IllegalArgumentException("Category comment may not be empty");
			}

			final String name = field.getName();
			final int modifiers = field.getModifiers();

			if(!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers) ||
					!Modifier.isFinal(modifiers)) {
				throw new IllegalArgumentException(name + " is not public static final");
			}

			final Class<?> categoryClass = field.getType();
			final String categoryName = parentCategory + name;

			final TRLCategory category =
					new TRLCategory(languageKeyPrefix, categoryClass, comment, categoryName);
			loadCategory(category);
			categories.add(category);

			//Load subcategories
			loadCategories(languageKeyPrefix, categoryName + ".", categoryClass, categories);
		}
	}

	private static void loadCategory(TRLCategory category) {
		for(Field field : category.clazz.getDeclaredFields()) {
			final Config.Property propertyData = field.getAnnotation(Config.Property.class);

			if(propertyData == null) {
				continue;
			}

			final String comment = StringUtils.join(propertyData.value(), "\n");

			if(comment.trim().isEmpty()) {
				throw new IllegalArgumentException("Property comment may not be empty");
			}

			final String name = field.getName();
			final int modifiers = field.getModifiers();

			if(!Modifier.isPublic(modifiers) || !Modifier.isStatic(modifiers) ||
					Modifier.isFinal(modifiers)) {
				throw new IllegalArgumentException(name + " is not public static non-final");
			}

			try {
				category.properties.add(new TRLProperty(category, name, field, comment));
			} catch(RuntimeException ex) {
				throw new ConfigException(name, ex);
			}
		}
	}
}
