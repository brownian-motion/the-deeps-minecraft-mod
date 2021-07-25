package com.brownian.thedeeps;

import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TheDeepsBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TheDeepsMod.MODID);
    //TODO: make a @DropsItem annotation which generates the loot_table for us!
    public static final RegistryObject<DummyThiccBlock> DUMMY_BLOCK = BLOCKS.register("thicc", DummyThiccBlock::new);
    public static final RegistryObject<DummyFrameBlock> Frame_Block = BLOCKS.register("frame", DummyFrameBlock::new);
    public static final RegistryObject<DeepsPortalBlock> DUMMY_PORTAL = BLOCKS.register("deeps_portal", DeepsPortalBlock::new);
}
