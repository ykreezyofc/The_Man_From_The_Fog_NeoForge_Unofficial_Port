
package net.mcreator.manfromthefogport;

import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.mcreator.manfromthefogport.init.ManFromTheFogPortModItems;

public class CreativeTabHandler {

    public static void addToCreativeTabs(
            BuildCreativeModeTabContentsEvent event) {

        if (event.getTabKey().equals(CreativeModeTabs.SPAWN_EGGS)) {
            event.accept(
                ManFromTheFogPortModItems.THE_MAN_SPAWN_EGG.get()
            );
        }

        if (event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) {
            event.accept(
                ManFromTheFogPortModItems.TEAR_OF_THE_MAN.get()
            );
        }
    }
}
