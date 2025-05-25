package com.example.pipemod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

public class PipeBlock extends Block implements EntityBlock {
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST = BooleanProperty.create("west");
    public static final BooleanProperty EAST = BooleanProperty.create("east");
    public static final BooleanProperty UP = BooleanProperty.create("up");
    public static final BooleanProperty DOWN = BooleanProperty.create("down");

    public static final BooleanProperty[] CONNECTIONS = new BooleanProperty[] {
        DOWN, UP, NORTH, SOUTH, WEST, EAST
    };

    private static final VoxelShape CENTER_SHAPE = Block.box(4, 4, 4, 12, 12, 12);
    private static final VoxelShape NORTH_SHAPE = Block.box(5, 5, 0, 11, 11, 4);
    private static final VoxelShape SOUTH_SHAPE = Block.box(5, 5, 12, 11, 11, 16);
    private static final VoxelShape WEST_SHAPE = Block.box(0, 5, 5, 4, 11, 11);
    private static final VoxelShape EAST_SHAPE = Block.box(12, 5, 5, 16, 11, 11);
    private static final VoxelShape UP_SHAPE = Block.box(5, 12, 5, 11, 16, 11);
    private static final VoxelShape DOWN_SHAPE = Block.box(5, 0, 5, 11, 4, 11);

    public PipeBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(NORTH, false)
            .setValue(SOUTH, false)
            .setValue(WEST, false)
            .setValue(EAST, false)
            .setValue(UP, false)
            .setValue(DOWN, false)
        );
    }
    
    @Mod("pipemod")
    public class PipeMod {
        public static final String MODID = "pipemod";
        
        public PipeMod(IEventBus modEventBus) {
            ModBlocks.register(modEventBus);
            ModBlockEntities.register(modEventBus);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, SOUTH, WEST, EAST, UP, DOWN);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState neighborState,
                                LevelAccessor world, BlockPos currentPos, BlockPos neighborPos) {
        BooleanProperty prop = getPropertyForDirection(facing);
        if (prop == null) {
            return super.updateShape(state, facing, neighborState, world, currentPos, neighborPos);
        }
        boolean connected = isConnectable(neighborState);
        return state.setValue(prop, connected);
    }

    private boolean isConnectable(BlockState state) {
        return state.getBlock() instanceof PipeBlock;
    }

    private BooleanProperty getPropertyForDirection(Direction direction) {
        return switch (direction) {
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
            case UP -> UP;
            case DOWN -> DOWN;
        };
    }

    private VoxelShape makeShape(BlockState state) {
        VoxelShape shape = CENTER_SHAPE;
        if (state.getValue(NORTH)) shape = Shapes.joinUnoptimized(shape, NORTH_SHAPE, BooleanOp.OR);
        if (state.getValue(SOUTH)) shape = Shapes.joinUnoptimized(shape, SOUTH_SHAPE, BooleanOp.OR);
        if (state.getValue(WEST)) shape = Shapes.joinUnoptimized(shape, WEST_SHAPE, BooleanOp.OR);
        if (state.getValue(EAST)) shape = Shapes.joinUnoptimized(shape, EAST_SHAPE, BooleanOp.OR);
        if (state.getValue(UP)) shape = Shapes.joinUnoptimized(shape, UP_SHAPE, BooleanOp.OR);
        if (state.getValue(DOWN)) shape = Shapes.joinUnoptimized(shape, DOWN_SHAPE, BooleanOp.OR);
        return shape;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return makeShape(state);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PipeBlockEntity(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            ItemStack itemStack = player.getItemInHand(hand);
            BlockEntity be = level.getBlockEntity(pos);
            
            if (be instanceof PipeBlockEntity pipe) {
                if (itemStack.is(Items.BUCKET)) {
                    // 处理空桶逻辑
                } else if (itemStack.getItem() instanceof BucketItem bucketItem) {
                    // 处理流体桶逻辑
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
