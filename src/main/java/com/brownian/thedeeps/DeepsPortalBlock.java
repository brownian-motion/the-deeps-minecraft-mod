package com.brownian.thedeeps;

import net.minecraft.block.*;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;

public class DeepsPortalBlock extends BreakableBlock {

    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.HORIZONTAL_AXIS;

    public DeepsPortalBlock() {
        super(AbstractBlock.Properties.copy(Blocks.NETHER_PORTAL));
        registerDefaultState(stateDefinition.any().setValue(AXIS, Direction.Axis.X));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    //TODO: override entityInside() to let us respond to something entering the portal
}
