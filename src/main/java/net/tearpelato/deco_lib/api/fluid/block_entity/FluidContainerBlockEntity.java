package net.tearpelato.deco_lib.api.fluid.block_entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class FluidContainerBlockEntity extends BlockEntity {

    public static final int BUCKET_VOLUME = 1000;

    private FluidStack stack = new FluidStack(Fluids.EMPTY, 0);
    private final int capacity;

    public FluidContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int capacity) {
        super(type, pos, state);
        this.capacity = capacity;
    }

    public FluidStack getFluidStack() {
        return this.stack;
    }

    public int getCapacity() { return capacity; }
    public boolean isEmpty() {
        return stack.isEmpty();
    }

    public void setFluidStack(FluidStack stack) {
        if(stack.getAmount() > capacity) this.stack = new FluidStack(stack.getFluid(), capacity);
        else this.stack = stack;
        setChanged();
        if (level != null && !level.isClientSide) level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),3);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        if (!stack.isEmpty()) {
            FluidStack.CODEC
                    .encodeStart(NbtOps.INSTANCE, stack)
                    .resultOrPartial(err -> {})
                    .ifPresent(nbt -> pTag.put("FluidStack", nbt));
        }
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        if (pTag.contains("FluidName")) {
            ResourceLocation id = ResourceLocation.tryParse(pTag.getString("FluidName"));
            if (id == null) {
                stack = FluidStack.EMPTY;
                return;
            }

            Fluid fluid = BuiltInRegistries.FLUID.get(id);
            if (fluid == Fluids.EMPTY) {
                stack = FluidStack.EMPTY;
                return;
            }

            int amount = Math.min(pTag.getInt("Amount"), capacity);
            stack = amount > 0 ? new FluidStack(fluid, amount) : FluidStack.EMPTY;
            return;
        }

        if (pTag.contains("FluidStack")) {
            FluidStack.CODEC
                    .parse(NbtOps.INSTANCE, pTag.get("FluidStack"))
                    .resultOrPartial(err -> {})
                    .ifPresent(fs -> stack = fs);
            return;
        }

        stack = FluidStack.EMPTY;
    }



    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }

    @Override
    public CompoundTag getUpdateTag() {
        return saveWithoutMetadata();
    }

}