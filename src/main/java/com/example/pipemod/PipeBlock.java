package com.example.pipemod;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PipeBlock extends Block implements EntityBlock{
    // 6个方向的连接状态属性
    public static final BooleanProperty NORTH = BooleanProperty.create("north");
    public static final BooleanProperty SOUTH = BooleanProperty.create("south");
    public static final BooleanProperty WEST  = BooleanProperty.create("west");
    public static final BooleanProperty EAST  = BooleanProperty.create("east");
    public static final BooleanProperty UP    = BooleanProperty.create("up");
    public static final BooleanProperty DOWN  = BooleanProperty.create("down");

    // 为方便遍历，这里用数组按Direction.ordinal顺序保存
    public static final BooleanProperty[] CONNECTIONS = new BooleanProperty[] {
        DOWN, UP, NORTH, SOUTH, WEST, EAST
    };

    // 中心柱形状 (4x4像素粗细，Minecraft中一个方块是16x16像素)
    private static final VoxelShape CENTER_SHAPE = Block.box(4, 4, 4, 12, 12, 12);
    private static final VoxelShape NORTH_SHAPE = Block.box(5, 5, 0, 11, 11, 4);
    private static final VoxelShape SOUTH_SHAPE = Block.box(5, 5, 12, 11, 11, 16);
    private static final VoxelShape WEST_SHAPE  = Block.box(0, 5, 5, 4, 11, 11);
    private static final VoxelShape EAST_SHAPE  = Block.box(12, 5, 5, 16, 11, 11);
    private static final VoxelShape UP_SHAPE    = Block.box(5, 12, 5, 11, 16, 11);
    private static final VoxelShape DOWN_SHAPE  = Block.box(5, 0, 5, 11, 4, 11);
    
    public PipeBlock() {
        super(BlockBehaviour.Properties.of().strength(0.5f));
        this.registerDefaultState(this.stateDefinition.any()
            .setValue(NORTH, false)
            .setValue(SOUTH, false)
            .setValue(WEST, false)
            .setValue(EAST, false)
            .setValue(UP, false)
            .setValue(DOWN, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        // 注册6个方向的连接属性
        builder.add(NORTH, SOUTH, WEST, EAST, UP, DOWN);
    }

    /**
     * 自动更新连接状态
     * 当方块状态更新（放置/邻居变化）时调用
     */
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

    /**
     * 初始化放置时自动检查连接
     */
    @Override
    public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext context) {
        BlockGetter world = context.getLevel();
        BlockPos pos = context.getClickedPos();

        BlockState state = this.defaultBlockState();
        for (Direction dir : Direction.values()) {
            BlockPos neighborPos = pos.relative(dir);
            BlockState neighborState = world.getBlockState(neighborPos);
            BooleanProperty prop = getPropertyForDirection(dir);
            if (prop != null) {
                state = state.setValue(prop, isConnectable(neighborState));
            }
        }
        return state;
    }

    // 判断某个方块是否可连接（这里简单判断是否同类管道方块）
    private boolean isConnectable(BlockState state) {
        return state.getBlock() instanceof PipeBlock;
    }

    // 根据方向返回对应的连接属性
    private BooleanProperty getPropertyForDirection(Direction direction) {
        switch(direction) {
            case NORTH: return NORTH;
            case SOUTH: return SOUTH;
            case WEST:  return WEST;
            case EAST:  return EAST;
            case UP:    return UP;
            case DOWN:  return DOWN;
            default:    return null;
        }
    }

    // 拼接形状，中心+连接臂
    private VoxelShape makeShape(BlockState state) {
        VoxelShape shape = CENTER_SHAPE;

        if (state.getValue(NORTH)) shape = Shapes.joinUnoptimized(shape, NORTH_SHAPE, BooleanOp.OR);
        if (state.getValue(SOUTH)) shape = Shapes.joinUnoptimized(shape, SOUTH_SHAPE, BooleanOp.OR);
        if (state.getValue(WEST))  shape = Shapes.joinUnoptimized(shape, WEST_SHAPE,  BooleanOp.OR);
        if (state.getValue(EAST))  shape = Shapes.joinUnoptimized(shape, EAST_SHAPE,  BooleanOp.OR);
        if (state.getValue(UP))    shape = Shapes.joinUnoptimized(shape, UP_SHAPE,    BooleanOp.OR);
        if (state.getValue(DOWN))  shape = Shapes.joinUnoptimized(shape, DOWN_SHAPE,  BooleanOp.OR);

        return shape;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return makeShape(state);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext ctx) {
        return makeShape(state);
    }

    public PipeBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new PipeBlockEntity(pos, state);
    }
}
