package net.mcreator.manfromthefogport.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredItem;

import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.Item;

import net.mcreator.manfromthefogport.item.TearOfTheManItem;
import net.mcreator.manfromthefogport.ManFromTheFogPortMod;

import java.util.function.Function;

public class ManFromTheFogPortModItems {
	public static final DeferredRegister.Items REGISTRY = DeferredRegister.createItems(ManFromTheFogPortMod.MODID);
	public static final DeferredItem<Item> THE_MAN_SPAWN_EGG;
	public static final DeferredItem<Item> MAN_AGRESSIVE_SPAWN_EGG;
	public static final DeferredItem<Item> TEAR_OF_THE_MAN;
	static {
		THE_MAN_SPAWN_EGG = register("the_man_spawn_egg", properties -> new SpawnEggItem(properties.spawnEgg(ManFromTheFogPortModEntities.THE_MAN.get())));
		MAN_AGRESSIVE_SPAWN_EGG = register("man_agressive_spawn_egg", properties -> new SpawnEggItem(properties.spawnEgg(ManFromTheFogPortModEntities.MAN_AGRESSIVE.get())));
		TEAR_OF_THE_MAN = register("tear_of_the_man", TearOfTheManItem::new);
	}

	// Start of user code block custom items
	// End of user code block custom items
	private static <I extends Item> DeferredItem<I> register(String name, Function<Item.Properties, ? extends I> supplier) {
		return REGISTRY.registerItem(name, supplier, Item.Properties::new);
	}
}