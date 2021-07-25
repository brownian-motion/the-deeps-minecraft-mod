package com.brownian.thedeeps.items;

import com.brownian.thedeeps.DeepsPortalBlock;
import lombok.NonNull;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Rarity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PortalCatalystItem extends Item {
    public PortalCatalystItem() {
        super(new Properties()
                .tab(ItemGroup.TAB_TOOLS)
                .stacksTo(1)
                .rarity(Rarity.RARE)
        );
    }

    @Override
    @NonNull
    public ActionResultType useOn(ItemUseContext context) {
        if (context.getPlayer() == null) {
            return ActionResultType.FAIL;
        }

        if (context.getPlayer().level.dimension() != World.OVERWORLD) {
            return ActionResultType.FAIL;
        }

        for (Direction direction : Direction.Plane.VERTICAL) {
            BlockPos framePos = context.getClickedPos().relative(direction);

            if (DeepsPortalBlock.trySpawnPortal(context.getLevel(), framePos)) {
                context.getLevel().playSound(context.getPlayer(), framePos, SoundEvents.PORTAL_TRIGGER, SoundCategory.BLOCKS, 1.0F, 1.0F);
                return ActionResultType.CONSUME; // we've processed the event!
            } else {
                return ActionResultType.FAIL;
            }
        }

        return ActionResultType.FAIL;
    }
}
