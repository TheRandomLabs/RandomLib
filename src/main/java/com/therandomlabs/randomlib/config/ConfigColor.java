package com.therandomlabs.randomlib.config;

import net.minecraft.item.EnumDyeColor;

public enum ConfigColor {
	WHITE("white"),
	ORANGE("orange"),
	MAGENTA("magenta"),
	LIGHT_BLUE("lightBlue"),
	YELLOW("yellow"),
	LIME("lime"),
	PINK("pink"),
	GRAY("gray"),
	SILVER("silver"),
	CYAN("cyan"),
	PURPLE("purple"),
	BLUE("blue"),
	BROWN("brown"),
	GREEN("green"),
	RED("red"),
	BLACK("black");

	private static String translationKeyPrefix = "";

	private final String translationKey;
	private final EnumDyeColor color;

	ConfigColor(String translationKey) {
		this.translationKey = translationKey;
		color = EnumDyeColor.valueOf(name());
	}

	@Override
	public String toString() {
		return translationKeyPrefix + translationKey;
	}

	public EnumDyeColor get() {
		return color;
	}

	public static void setTranslationKeyPrefix(String prefix) {
		translationKeyPrefix = "randomlibtest.config.color.";
	}
}
