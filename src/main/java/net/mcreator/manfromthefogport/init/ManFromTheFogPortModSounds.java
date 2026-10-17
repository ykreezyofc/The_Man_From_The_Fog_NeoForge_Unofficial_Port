/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.mcreator.manfromthefogport.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.resources.Identifier;
import net.minecraft.core.registries.Registries;

import net.mcreator.manfromthefogport.ManFromTheFogPortMod;

public class ManFromTheFogPortModSounds {
	public static final DeferredRegister<SoundEvent> REGISTRY = DeferredRegister.create(Registries.SOUND_EVENT, ManFromTheFogPortMod.MODID);
	public static final DeferredHolder<SoundEvent, SoundEvent> SPAWN1 = REGISTRY.register("spawn1", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("man_from_the_fog_port", "spawn1")));
	public static final DeferredHolder<SoundEvent, SoundEvent> SPAWN2 = REGISTRY.register("spawn2", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("man_from_the_fog_port", "spawn2")));
	public static final DeferredHolder<SoundEvent, SoundEvent> SPAWN3 = REGISTRY.register("spawn3", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("man_from_the_fog_port", "spawn3")));
	public static final DeferredHolder<SoundEvent, SoundEvent> SPAWN4 = REGISTRY.register("spawn4", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("man_from_the_fog_port", "spawn4")));
	public static final DeferredHolder<SoundEvent, SoundEvent> SPAWN5 = REGISTRY.register("spawn5", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("man_from_the_fog_port", "spawn5")));
	public static final DeferredHolder<SoundEvent, SoundEvent> CHASEDV2 = REGISTRY.register("chasedv2", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("man_from_the_fog_port", "chasedv2")));
	public static final DeferredHolder<SoundEvent, SoundEvent> JUMPSCARE = REGISTRY.register("jumpscare", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("man_from_the_fog_port", "jumpscare")));
	public static final DeferredHolder<SoundEvent, SoundEvent> REVERSED_VERSION_2 = REGISTRY.register("reversed-version-2", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("man_from_the_fog_port", "reversed-version-2")));
	public static final DeferredHolder<SoundEvent, SoundEvent> SPOTTED = REGISTRY.register("spotted", () -> SoundEvent.createVariableRangeEvent(Identifier.fromNamespaceAndPath("man_from_the_fog_port", "spotted")));
}