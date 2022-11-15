package com.lothrazar.samshorsefood;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

public class ItemHorseFood extends Item {
    public static int HEARTS_MAX;
    public static int SPEED_MAX;
    public static int JUMP_MAX;
    private static double JUMP_SCALE = 1.02; //%age
    private static double SPEED_SCALE = 1.05; //%age

    public ItemHorseFood() {
        super();
        this.setCreativeTab(ModHorseFood.tabHorseFood);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List tooltip, boolean advanced) {
        if(stack == null || stack.getItem() == null) {
            return;
        } //just being safe

        Item carrot = stack.getItem();
        tooltip.add(StatCollector.translateToLocal(carrot.getUnlocalizedName(stack)+".effect"));
    }

    public static void addRecipes() {
        int dye_lapis = 4;

        GameRegistry.addShapelessRecipe(new ItemStack(ItemRegistry.emeraldCarrot)
            ,Items.carrot
            ,Items.emerald);

        GameRegistry.addShapelessRecipe(new ItemStack(ItemRegistry.lapisCarrot)
            ,Items.carrot
            ,new ItemStack(Items.dye,1,dye_lapis));

        GameRegistry.addShapelessRecipe(new ItemStack(ItemRegistry.diamondCarrot)
            ,Items.carrot
            ,Items.diamond);

        GameRegistry.addShapelessRecipe(new ItemStack(ItemRegistry.enderCarrot)
            ,Items.carrot
            ,Items.ender_eye);

        GameRegistry.addShapelessRecipe(new ItemStack(ItemRegistry.redstoneCarrot)
            ,Items.carrot
            ,Items.redstone);
    }

    public static void onHorseInteract(EntityHorse horse, EntityPlayer player, ItemStack held) {
        boolean success = false;
        /*
        String ownerID = "..untamed..";
        if(horse.isTame() && horse.getEntityData().hasKey("OwnerUUID")) {
            ownerID = horse.getEntityData().getString("OwnerUUID");
        }

        // or let it through if no owner exists
        ("owner = "+ownerID);
        ("player = "+player.getUniqueID().toString());
        */

        if(held.getItem() == ItemRegistry.emeraldCarrot) {
            switch(horse.getHorseType()) {
                case Horse.type_standard:
                    horse.setHorseType(Horse.type_zombie);
                    success = true;
                    break;
                case Horse.type_zombie:
                    horse.setHorseType(Horse.type_skeleton);
                    success = true;
                    break;
                case Horse.type_skeleton:
                    horse.setHorseType(Horse.type_standard);
                    success = true;
                    break;
                    //donkey and mule ignored by design
            }
        } else if(held.getItem() == ItemRegistry.lapisCarrot) {
            int variant = horse.getHorseVariant();
            if(variant > 1023 && variant < 1030) {
                horse.setHorseVariant(variant % 1024 + 1);
            } else if(variant == 1030 || variant > 1030) {
                horse.setHorseVariant(0);
            } else {
                horse.setHorseVariant(variant + 256);
            }
            success = true;
        } else if(held.getItem() == ItemRegistry.diamondCarrot) {
            float mh = (float) horse.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue();

            if(mh < 2 * HEARTS_MAX) { // 20 hearts == 40 health points
                horse.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(mh + 2);
                success = true;
            }
        } else if(held.getItem() == ItemRegistry.enderCarrot) {
            if(ModHorseFood.horseJumpStrength != null) { // only happpens if mod installing preInit method fails to find it
                double jump = horse.getEntityAttribute(ModHorseFood.horseJumpStrength).getAttributeValue();//horse.getHorseJumpStrength();
                double newjump = jump * JUMP_SCALE;
                // double jumpHeight = getJumpTranslated(horse.getHorseJumpStrength());

                if(ModHorseFood.getJumpTranslated(newjump) < JUMP_MAX) {
                    horse.getEntityAttribute(ModHorseFood.horseJumpStrength).setBaseValue(newjump);
                    // System.out.println("newjump = "+newjump);
                    success = true;
                }
            }
        } else if(held.getItem() == ItemRegistry.redstoneCarrot) {
            double speed = horse.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue();
            double newSpeed = speed * SPEED_SCALE;

            // add ten percent
            if(ModHorseFood.getSpeedTranslated(newSpeed) < SPEED_MAX) {
                horse.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(newSpeed);
                //System.out.println("speed = "+newSpeed);
                success = true;
            }
        }

        if(success) {
            if(player.capabilities.isCreativeMode == false) {
                player.inventory.decrStackSize(player.inventory.currentItem, 1);
            }

            for(int countparticles = 0; countparticles < 10; countparticles++) {
                double x = horse.posX;
                double y = horse.posY;
                double z = horse.posZ;

                horse.worldObj.spawnParticle("largesmoke", x + (horse.worldObj.rand.nextDouble() - 0.5) * 0.8, y + horse.worldObj.rand.nextDouble() * 1.5 - 0.1, z + (horse.worldObj.rand.nextDouble() - 0.5) * 0.8, 0.0, 0.0, 0.0);
            }

            player.worldObj.playSoundAtEntity(player, "random.eat", 1.0f, 1.0f);
            horse.setEating(true); // makes horse animate and bend down to eat
        }
    }

    public static class Horse {
        public static final int type_standard = 0;
        public static final int type_donkey = 1;
        public static final int type_mule = 2;
        public static final int type_zombie = 3;
        public static final int type_skeleton = 4;
    }
}
