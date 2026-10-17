package net.mcreator.manfromthefogport.item;

import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Entity;
import net.minecraft.server.level.ServerLevel;

import net.mcreator.manfromthefogport.procedures.TearOfTheManItemInHandTickProcedure;

import javax.annotation.Nullable;

public class TearOfTheManItem extends Item {
	public TearOfTheManItem(Item.Properties properties) {
		super(properties.rarity(Rarity.RARE).stacksTo(1));
	}

	@Override
	public void inventoryTick(ItemStack itemstack, ServerLevel world, Entity entity, @Nullable EquipmentSlot equipmentSlot) {
		super.inventoryTick(itemstack, world, entity, equipmentSlot);
		if (equipmentSlot == EquipmentSlot.MAINHAND)
			TearOfTheManItemInHandTickProcedure.execute(entity);
	}
}