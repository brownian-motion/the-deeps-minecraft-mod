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

    private static final RegistryObject<Item> DUMMY_BLOCK_ITEM = blockItem("blocks/thicc", TheDeepsBlocks.DUMMY_BLOCK);
    private static final RegistryObject<Item> Dummy_FRAME_ITEM = blockItem("blocks/frame", TheDeepsBlocks.Frame_Block);

    private static RegistryObject<Item> blockItem(String name, RegistryObject<Block> block){
        return TheDeepsBlockItems.ITEMS.register(name, () -> new BlockItem(block.get(), new BlockItem.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS)));
    }

}
