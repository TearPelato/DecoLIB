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
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class FluidContainerBlockEntity extends BlockEntity {

    public static final int BUCKET_VOLUME = 1000;

    private Fluid fluid = Fluids.EMPTY;
    private int amount = 0;
    private final int capacity;

    public FluidContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, int capacity) {
        super(type,pos,state);
        this.capacity = capacity;
    }

    public Fluid getFluid() { return fluid != null ? fluid : Fluids.EMPTY; }
    public int getStoredAmount() { return amount; }
    public int getCapacity() { return capacity; }
    public boolean isEmpty() { return fluid == Fluids.EMPTY || amount <= 0; }

    public void setFluidAndAmount(Fluid f, int a) {
        if (f == null) f = Fluids.EMPTY;
        int clamped = Math.min(a, capacity);
        if (clamped <= 0) { fluid = Fluids.EMPTY; amount = 0; }
        else { fluid = f; amount = clamped; }
        setChanged();
        if (level != null && !level.isClientSide()) level.sendBlockUpdated(worldPosition,getBlockState(),getBlockState(),3);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putString("FluidName", fluid == Fluids.EMPTY ? "minecraft:empty" : BuiltInRegistries.FLUID.getKey(fluid).toString());
        output.putInt("Amount", amount);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        String name =  input.getString("FluidName").orElse("minecraft:empty");
        if ("minecraft:empty".equals(name)) { fluid = Fluids.EMPTY;  amount = 0;  return; }
        Fluid f = BuiltInRegistries.FLUID.getValue(Identifier.tryParse(name));
        fluid = f != null ? f : Fluids.EMPTY;
        amount = Math.min(input.getInt("Amount").orElse(0), capacity);
        if (amount <= 0) fluid = Fluids.EMPTY;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }
}