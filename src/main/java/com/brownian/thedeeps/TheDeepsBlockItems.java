package com.brownian.thedeeps;

import com.brownian.thedeeps.items.PortalCatalystItem;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class TheDeepsBlockItems {
    static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TheDeepsMod.MODID);

    private static final RegistryObject<BlockItem> DUMMY_BLOCK_ITEM = blockItem("block/thicc", TheDeepsBlocks.DUMMY_BLOCK);
    private static final RegistryObject<BlockItem> Dummy_FRAME_ITEM = blockItem("block/frame", TheDeepsBlocks.Frame_Block);
    private static final RegistryObject<BlockItem> DUMMY_PORTAL_ITEM = blockItem("block/deeps_portal", TheDeepsBlocks.DUMMY_PORTAL);

    private static final RegistryObject<PortalCatalystItem> PORTAL_CATALYST_ITEM = ITEMS.register("tool/portal_catalyst", PortalCatalystItem::new);

    private static <B extends Block> RegistryObject<BlockItem> blockItem(String name, RegistryObject<B> block) {
        return TheDeepsBlockItems.ITEMS.register(name, () -> new BlockItem(block.get(), new BlockItem.Properties().tab(ItemGroup.TAB_BUILDING_BLOCKS)));
    }
}
