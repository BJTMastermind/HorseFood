package com.lothrazar.samshorsefood;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

public class ItemRegistry {
    public static final String TEXTURE_LOCATION = ModHorseFood.MODID + ":";

    public static ItemHorseFood emeraldCarrot;
    public static ItemHorseFood lapisCarrot;
    public static ItemHorseFood diamondCarrot;
    public static ItemHorseFood enderCarrot;
    public static ItemHorseFood redstoneCarrot;

    public static void registerItems() {
        emeraldCarrot = new ItemHorseFood();
        ItemRegistry.registerItem(emeraldCarrot, "horse_upgrade_type");

        lapisCarrot = new ItemHorseFood();
        ItemRegistry.registerItem(lapisCarrot, "horse_upgrade_variant");

        diamondCarrot = new ItemHorseFood();
        ItemRegistry.registerItem(diamondCarrot, "horse_upgrade_health");

        redstoneCarrot = new ItemHorseFood();
        ItemRegistry.registerItem(redstoneCarrot, "horse_upgrade_speed");

        enderCarrot = new ItemHorseFood();
        ItemRegistry.registerItem(enderCarrot, "horse_upgrade_jump");

        ItemHorseFood.addRecipes();
    }

    public static void registerItem(Item item, String name) {
         item.setUnlocalizedName(name).setTextureName(TEXTURE_LOCATION+item.getUnlocalizedName().replace("item.", ""));
         GameRegistry.registerItem(item, name);
    }
}
