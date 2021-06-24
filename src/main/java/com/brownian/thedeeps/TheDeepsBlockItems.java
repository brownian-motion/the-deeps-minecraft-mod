package com.brownian.thedeeps;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TheDeepsBlockItems {
    static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TheDeepsMod.MODID);

    private static final RegistryObject<Item> DUMMY_BLOCK_ITEM = blockItem("block/thicc", TheDeepsBlocks.DUMMY_BLOCK);
    private static final RegistryObject<Item> Dummy_FRAME_ITEM = blockItem("block/frame", TheDeepsBlocks.Frame_Block);
    private static final RegistryObject<Item> DUMMY_PORTAL_ITEM = blockItem("block/deeps_portal", TheDeepsBlocks.DUMMY_PORTAL);

    private static RegistryObject<Item> blockItem(String name, RegistryObject<Block> block){
        return TheDeepsBlockItems.ITEMS.register(name, () -> new BlockItem(block.get(), new BlockItem.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS)));
    }

}
