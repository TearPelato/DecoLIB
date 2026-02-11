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
import net.neoforged.neoforge.fluids.FluidStack;
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
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag,registries);
        if(stack.isEmpty()) return;
        tag.put("FluidStack", FluidStack.CODEC.encode(stack, NbtOps.INSTANCE, new CompoundTag()).getOrThrow());
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        /* CODICE DA TOGLIERE QUANDO TUTTI AVRANNO CONVERTITO (PROBABILMENTE MAI) */
        if (tag.contains("FluidName")) { // LEGACY ENCODING
            String name = tag.getString("FluidName");
            if ("minecraft:empty".equals(name)) {
                this.stack = new FluidStack(Fluids.EMPTY, 0);
                return;
            }
            Optional<Holder.Reference<Fluid>> fluid =
                    BuiltInRegistries.FLUID.getHolder(ResourceLocation.parse(name));

            if (fluid.isEmpty()) {
                this.stack = new FluidStack(Fluids.EMPTY, 0);
                return;
            }
            int amount = Math.min(tag.getInt("Amount"), capacity);
            if (amount <= 0) {
                this.stack = new FluidStack(Fluids.EMPTY, 0);
                return;
            }
            var ref = fluid.get();
            this.stack = new FluidStack(ref, amount);

        } else if (tag.contains("FluidStack")) {
            this.stack = FluidStack.CODEC.decode(NbtOps.INSTANCE, tag.get("FluidStack"))
                    .getOrThrow()
                    .getFirst();

        } else {
            this.stack = new FluidStack(Fluids.EMPTY, 0);
        }
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() { return ClientboundBlockEntityDataPacket.create(this); }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
}