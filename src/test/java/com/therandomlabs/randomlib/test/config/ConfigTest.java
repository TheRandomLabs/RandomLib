package com.therandomlabs.randomlib.test.config;

import com.therandomlabs.randomlib.config.Config;
import com.therandomlabs.randomlib.config.ConfigColor;
import com.therandomlabs.randomlib.test.RandomLibTest;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

@Config(modid = RandomLibTest.MOD_ID, path = RandomLibTest.MOD_ID + "/main")
public final class ConfigTest {
	public static final class FlyingPigs {
		@Config.RequiresMCRestart
		@Config.Property("Whether to enable flying pigs.")
		public static boolean flyingPigs = true;

		@Config.RequiresWorldReload
		@Config.RangeInt(min = -3, max = 3)
		@Config.Property("The flying pig range.")
		public static int flyingPigRange = 3;

		@Config.Blacklist({
				"minecraft:air",
				"minecraft:stick"
		})
		@Config.Property("The flying pig item.")
		public static Item flyingPigItem = Items.ACACIA_BOAT;

		@Config.Property("The flying pig color.")
		public static ConfigColor flyingPigColor = ConfigColor.BLUE;

		public static void onReload() {
			if(flyingPigItem == Items.APPLE) {
				flyingPigRange++; //If this is above 3, then it will be reset to 3
			}
		}
	}

	@Config.Category("Configuration properties related to flying pigs.")
	public static final FlyingPigs flyingPigs = null;
}
