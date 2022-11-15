package com.lothrazar.samshorsefood;

import java.lang.reflect.Field;
import java.lang.Math;
import java.text.DecimalFormat;

import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

@Mod(modid = ModHorseFood.MODID, useMetadata=true)
public class ModHorseFood {
    public static final String MODID = "samshorsefood";

    @Instance(value = MODID)
    public static ModHorseFood instance;

    public static Logger logger;
    public static ConfigRegistry cfg;
    public static IAttribute horseJumpStrength = null;

    public static CreativeTabs tabHorseFood = new CreativeTabs("tabHorseFood") {
        @Override
        public Item getTabIconItem() {
            return ItemRegistry.diamondCarrot;
        }
    };

    @EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();

        ItemRegistry.registerItems();

        cfg = new ConfigRegistry(new Configuration(event.getSuggestedConfigurationFile()));

        for(Field f : EntityHorse.class.getDeclaredFields()) {
            try {
                if(f.getName().equals("horseJumpStrength")) {
                    f.setAccessible(true);
                    // save pointer to the obj so we can reference it later
                    ModHorseFood.horseJumpStrength = (IAttribute)f.get(null);
                    break;
                }
            } catch(Exception e) {
                System.err.println("Severe error, please report this to the mod author:");
                System.err.println(e);
            }
        }

        if(ModHorseFood.horseJumpStrength == null) {
            System.err.println(MODID+":horseJumpStrength: Error - field not found using reflection");
        }
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        FMLCommonHandler.instance().bus().register(instance);
        MinecraftForge.EVENT_BUS.register(instance);
    }

    @SubscribeEvent
    public void onEntityInteractEvent(EntityInteractEvent event) {
        ItemStack held = event.entityPlayer.getCurrentEquippedItem();

        if(held != null && held.getItem() instanceof ItemHorseFood) {
            if(event.target instanceof EntityHorse) {
                ItemHorseFood.onHorseInteract((EntityHorse) event.target, event.entityPlayer, held);

                event.setCanceled(true); // stop the GUI inventory opening
            }
        }
    }

    static double getJumpTranslated(double jump) {
        // convert from scale factor to blocks
        double jumpHeight = 0;
        double gravity = 0.98;
        while (jump > 0) {
            jumpHeight += jump;
            jump -= 0.08;
            jump *= gravity;
        }
        return jumpHeight;
    }

    static double getSpeedTranslated(double speed) {
        return speed * 100;
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void addHorseInfo(RenderGameOverlayEvent.Text event) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if(Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            if(player.ridingEntity != null && player.ridingEntity instanceof EntityHorse) {
                EntityHorse horse = (EntityHorse) player.ridingEntity;

                double speed = horse.getEntityAttribute(SharedMonsterAttributes.movementSpeed).getAttributeValue() * 43.1718;
                double jumpHeight = getJumpTranslated(horse.getHorseJumpStrength());

                DecimalFormat df = new DecimalFormat("0.00");

                event.left.add(StatCollector.translateToLocal("debug.horse.speed")+": "+df.format(speed)+" m/s");

                df = new DecimalFormat("0.0");

                event.left.add(StatCollector.translateToLocal("debug.horse.jump")+": "+df.format(jumpHeight)+" m");

                event.left.add(StatCollector.translateToLocal("debug.horse.variant")+": "+horse.getHorseVariant());
            }
        }
    }
}
