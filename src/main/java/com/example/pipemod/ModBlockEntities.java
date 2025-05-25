package com.example.pipemod;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

@Mod.EventBusSubscriber(modid = "pipemod", bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBlockEntities {
    // 注册表
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
        DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, "pipemod");

    // 注册 PipeBlockEntity
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<PipeBlockEntity>> PIPE =
        BLOCK_ENTITIES.register("pipe", () ->
            BlockEntityType.Builder.of(PipeBlockEntity::new, ModBlocks.PIPE.get()).build(null)
        );

    // 在 ModMain 或 ModInit 类中调用：
    // ModBlockEntities.BLOCK_ENTITIES.register(eventBus);
}
