package com.brownian.thedeeps.blocks;

import com.brownian.thedeeps.TheDeepsBlocks;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BreakableBlock;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Defines the actual pane making up the portal interior,
 * and defines some helper functions for finding, creating, and using those.
 * <p>
 * Note that this relies heavily on Lombok tools like {@link val} to shorten how much I have to write.
 */
public class DeepsPortalBlock extends BreakableBlock {
    public DeepsPortalBlock() {
        super(Properties.copy(Blocks.NETHER_PORTAL));
        registerDefaultState(stateDefinition.any().setValue(BlockStateProperties.HORIZONTAL_AXIS, Direction.Axis.X));
    }

    public static boolean trySpawnPortal(IWorld level, BlockPos framePos) {
        val maybePortalFrame =
                Stream.of(Direction.Axis.Z, Direction.Axis.X)
                        .map(axis -> PortalFrame.findPortalFrameAt(level, framePos, axis))
                        .filter(Objects::nonNull)
                        .filter(PortalFrame::isValidSize)
                        .findAny();

        maybePortalFrame.ifPresent(portalFrame -> {
            // TODO: the Undergarden publishes an event here. Should we do the same?
            portalFrame.placePortalBlocks();
        });

        return maybePortalFrame.isPresent();
    }

    @Override
    public void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_AXIS);
    }

    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    private static class PortalFrame {
        private static final int MAX_WIDTH = 21;
        private static final int MAX_HEIGHT = 21;
        private static final int MIN_WIDTH = 2;
        private static final int MIN_HEIGHT = 2;
        private final @NonNull IWorld world;
        private final @NonNull Direction.Axis axis;
        private final @NonNull Direction rightDir;
        private final @NonNull BlockPos bottomLeft; // is either empty or contains a portal block
        private final int height;
        private final int width;

        @Nullable
        public static PortalFrame findPortalFrameAt(IWorld world, BlockPos pos, Direction.Axis axisIn) {
            final Direction rightDir, leftDir;
            if (axisIn == Direction.Axis.X) {
                leftDir = Direction.EAST;
                rightDir = Direction.WEST;
            } else {
                leftDir = Direction.NORTH;
                rightDir = Direction.SOUTH;
            }

            @Nullable val bottomFrameBlock = getFrameEdgePosition(pos, Direction.DOWN, world);
            if (bottomFrameBlock == null) return null;
            // we found the bottom of the frame, but we really want the air in the bottom row of the frame interior, so step back by one
            val bottomRowEmptyBlock = bottomFrameBlock.above();

            // this is the frame block directly left of `bottomLeft`
            @Nullable val bottomRowLeftFrame = getFrameEdgePosition(bottomRowEmptyBlock, leftDir, world);
            if (bottomRowLeftFrame == null) return null;
            // we found the frame, but we really want the air in the corner of the frame, so step back by one
            val bottomLeft = bottomRowLeftFrame.relative(rightDir);
            assert couldInsertPortalBlock(world.getBlockState(bottomLeft)) : "the calculation to find the bottom-left empty space is wrong";

            // this is the frame block directly right of `bottomLeft`
            @Nullable val bottomRowRightFrame = getFrameEdgePosition(bottomLeft, rightDir, world);
            if (bottomRowRightFrame == null) return null;

            val width = bottomLeft.distManhattan(bottomRowRightFrame);
            if (width < MIN_WIDTH || width > MAX_WIDTH) return null;

            // this is the frame block directly above `bottomLeft`
            @Nullable val leftColTopFrame = getFrameEdgePosition(bottomLeft, Direction.UP, world);
            if (leftColTopFrame == null) return null;
            val height = leftColTopFrame.distManhattan(bottomLeft);
            if (height < MIN_HEIGHT || height > MAX_HEIGHT) return null;

            if (!new Validator(world, width, height, rightDir, bottomLeft).isValid()) return null;

            return new PortalFrame(world, axisIn, rightDir, bottomLeft, height, width);
        }

        private static boolean couldInsertPortalBlock(BlockState pos) {
            return pos.isAir() || pos.getBlock() == TheDeepsBlocks.DUMMY_PORTAL.get();
        }

        /**
         * Finds the portal frame block in the given direction
         * if it's the first block within [MAX_WIDTH] along the given direction,
         * or returns null otherwise.
         *
         * @param startingPoint some empty position in space
         * @param directionIn   which horizontal direction to inspect in
         * @param world         the world we're inspecting
         */
        @Nullable
        private static BlockPos getFrameEdgePosition(BlockPos startingPoint, Direction directionIn, IWorld world) {
            val portalFrameBlock = TheDeepsBlocks.Frame_Block.get();

            return IntStream.rangeClosed(0, MAX_WIDTH)
                    .mapToObj(offset -> startingPoint.relative(directionIn, offset))
                    .filter(pos -> pos.getY() >= 0) // just in case we use this to shoot a line down and there's no bedrock
                    .filter(pos -> !couldInsertPortalBlock(world.getBlockState(pos)))
                    .findFirst()
                    // once we've found the first block, we want to discard it if it's not actually a frame block
                    .filter(pos -> world.getBlockState(pos).is(portalFrameBlock))
                    .orElse(null);
        }

        public boolean isValidSize() {
            return this.width >= MIN_WIDTH
                    && this.width <= MAX_WIDTH
                    && this.height >= MIN_WIDTH
                    && this.height <= MAX_WIDTH;
        }

        private void forEachBlockWithinFrame(Consumer<BlockPos> consumer) {
            for (int dUp = 0; dUp < height; dUp++) {
                BlockPos rowStart = this.bottomLeft.above(dUp);
                for (int dRight = 0; dRight < width; dRight++) {
                    BlockPos blockPos = rowStart.relative(this.rightDir, dRight);
                    consumer.accept(blockPos);
                }
            }
        }

        public void placePortalBlocks() {
            Block portalBlock = TheDeepsBlocks.DUMMY_PORTAL.get();
            forEachBlockWithinFrame(blockPos -> this.world.setBlock(
                    blockPos,
                    portalBlock.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_AXIS, this.axis),
                    18 // wtf does this constant mean?
            ));
        }

        /**
         * Validates the actual blocks forming the portal frame.
         */
        @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
        private static class Validator {
            private final @NonNull IWorld world;
            private final int width, height;
            private final @NonNull Direction right;
            private final @NonNull BlockPos bottomLeft;

            Block portalFrameBlock = TheDeepsBlocks.Frame_Block.get();

            public boolean isValid() {
                return isRowFullOfFrameBlocks(bottomLeft.below()) // bottom
                        && isRowFullOfFrameBlocks(bottomLeft.above(height)) // top
                        // interior and sides:
                        && IntStream.range(0, height)
                        .mapToObj(dUp -> bottomLeft.above(dUp))
                        .allMatch(rowStart -> isRowEmpty(rowStart) && areFrameEdgesPresentOnRow(rowStart));
            }

            // used to validate the insides of the frame
            private boolean isRowEmpty(BlockPos rowStart) {
                return IntStream.range(0, width)
                        .mapToObj(dRight -> rowStart.relative(right, dRight))
                        .map(world::getBlockState)
                        .allMatch(PortalFrame::couldInsertPortalBlock);
            }

            // used to validate lefts and rights
            private boolean areFrameEdgesPresentOnRow(BlockPos rowStart) {
                val leftFramePos = rowStart.relative(right.getOpposite());
                val rightFramePos = rowStart.relative(right, width);

                return Stream.of(leftFramePos, rightFramePos)
                        .map(world::getBlockState)
                        .allMatch(blockState -> blockState.is(portalFrameBlock));
            }

            // used to validate tops and bottoms
            private boolean isRowFullOfFrameBlocks(BlockPos rowStart) {
                return IntStream.range(0, width)
                        .mapToObj(dRight -> rowStart.relative(right, dRight))
                        .map(world::getBlockState)
                        .allMatch(blockState -> blockState.is(portalFrameBlock));
            }
        }
    }
}
