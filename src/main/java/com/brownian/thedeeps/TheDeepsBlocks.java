package com.brownian.thedeeps;

import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TheDeepsBlocks {
    static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TheDeepsMod.MODID);
    static final RegistryObject<Block> DUMMY_BLOCK = BLOCKS.register("thicc", DummyThiccBlock::new);
    static final RegistryObject<Block> Frame_Block = BLOCKS.register("frame", DummyFrameBlock::new);
//    @SubscribeEvent
//    public static void registerItemblocks(RegistryEvent.Register<Item> evt) {
//        TheDeepsBlockItems.registerBlockItems(evt);
//    }
}
