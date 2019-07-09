package com.therandomlabs.randomlib;

import java.util.List;
import com.google.common.collect.ImmutableList;
import net.minecraft.inventory.EntityEquipmentSlot;

public final class EntityUtils {
	public static final List<EntityEquipmentSlot> ARMOR_SLOTS = ImmutableList.of(
			EntityEquipmentSlot.HEAD,
			EntityEquipmentSlot.CHEST,
			EntityEquipmentSlot.LEGS,
			EntityEquipmentSlot.FEET
	);

	private EntityUtils() {}
}
