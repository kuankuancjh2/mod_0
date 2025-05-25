package com.example.pipemod;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = 
        DeferredRegister.create(Registries.BLOCK, PipeMod.MODID);
    public static final DeferredRegister<Item> ITEMS = 
        DeferredRegister.create(Registries.ITEM, PipeMod.MODID);

    public static final DeferredHolder<Block, Block> PIPE_BLOCK = 
        BLOCKS.register("pipe_block", () -> new PipeBlock(Block.Properties.of()));

    public static final DeferredHolder<Item, Item> PIPE_BLOCK_ITEM = 
        ITEMS.register("pipe_block", () -> new BlockItem(PIPE_BLOCK.get(), new Item.Properties()));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
    }
}
