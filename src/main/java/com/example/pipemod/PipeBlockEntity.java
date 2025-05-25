package com.example.pipemod;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.capabilities.Capabilities;
import org.jetbrains.annotations.Nullable;

public class PipeBlockEntity extends BlockEntity {
    private final FluidTank tank = new FluidTank(4000); // 4桶容量

    public PipeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.PIPE.get(), pos, state);
    }

    public FluidTank getTank() {
        return tank;
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("tank", tank.writeToNBT(new CompoundTag()));
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        tank.readFromNBT(tag.getCompound("tank"));
    }

    @Override
    public <T> @Nullable T getCapability(net.minecraft.core.Direction side, net.minecraft.core.component.ComponentPath path, Class<T> cap) {
        if (cap == Capabilities.FluidHandler.BLOCK) {
            return cap.cast(tank);
        }
        return super.getCapability(side, path, cap);
    }
}
