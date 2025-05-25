package com.example.pipemod;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(ModBlocks.MODID)
public class PipeMod {
    /**
     * NeoForge 会在构造函数中传入该 mod 的专属事件总线
     * （无需手动调用 FMLJavaModLoadingContext）
     */
    public PipeMod(IEventBus modEventBus) {
        ModBlocks.register(modEventBus);
    }
}
