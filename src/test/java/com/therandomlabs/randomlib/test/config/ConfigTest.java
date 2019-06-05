package com.therandomlabs.randomlib.test.config;

import com.therandomlabs.randomlib.config.Config;
import com.therandomlabs.randomlib.config.ConfigColor;
import com.therandomlabs.randomlib.test.RandomLibTest;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

@Config(value = RandomLibTest.MOD_ID, path = RandomLibTest.MOD_ID + "/main")
public final class ConfigTest {
	public static final class FlyingPigs {
		public static final class Lol {
			@Config.Property("test")
			public static boolean test = true;
		}

		@Config.MCVersion("1.12.2")
		@Config.Category("lol")
		public static final Lol lol = null;

		@Config.MCVersion("[1.10,1.11]")
		@Config.RequiresMCRestart
		@Config.Property("Whether to enable flying pigs.")
		public static boolean flyingPigs = true;

		@Config.RequiresWorldReload
		@Config.RangeInt(min = -3, max = 3)
		@Config.Property("The flying pig range.")
		public static int flyingPigRange = 3;

		@Config.NonNull
		@Config.Blacklist({
				"minecraft:air",
				"minecraft:stick"
		})
		@Config.Property("The flying pig item.")
		public static Item flyingPigItem = Items.ACACIA_BOAT;

		@Config.Property("The flying pig color.")
		public static ConfigColor flyingPigColor = ConfigColor.BLUE;

		@Config.Property({
				"Test array property.",
				"Add integers to this array."
		})
		public static int[] testArrayProperty = {
				1, 3, 5, 7
		};

		@Config.Property("Test item array property.")
		public static Item[] testItemArrayProperty = {};

		@Config.Property("Null default item.")
		public static Item nullDefaultItem;

		@Config.Previous("flyingPigs.whereDidHeComeFrom")
		@Config.Property("Where did he come from, where did he go?")
		public static boolean whereDidHeGo;

		public static void onReload() {
			if(flyingPigItem == Items.APPLE) {
				flyingPigRange++; //If this is above 3, then it will be reset to 3
			}
		}
	}

	@Config.Category("Configuration properties related to flying pigs.")
	public static final FlyingPigs flyingPigs = null;
}
