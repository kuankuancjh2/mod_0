package com.example.pipemod;

import java.util.function.Supplier;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * 负责方块和物品的注册
 */
public class ModBlocks {
    public static final String MODID = "pipemod";

    // 创建 Block 和 Item 的 DeferredRegister
    public static final DeferredRegister<Block> BLOCKS =
        DeferredRegister.create(Registries.BLOCK, MODID);
    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(Registries.ITEM, MODID);

    // 注册管道方块（Supplier 风格）
    public static final Supplier<Block> PIPE_BLOCK =
        BLOCKS.register("pipe_block", PipeBlock::new);

    // 注册对应的方块物品
    public static final Supplier<Item> PIPE_BLOCK_ITEM =
        ITEMS.register("pipe_block",
            () -> new BlockItem(PIPE_BLOCK.get(), new Item.Properties()));

    /**
     * 将注册表挂载到 NeoForge 的 mod 事件总线
     */
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}
