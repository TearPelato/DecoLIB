package net.tier1234.deco_lib.api.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class BlockEntityUtil {

    /**
     * Invia l'update packet creato da BlockEntity#getUpdatePacket()
     */
    public static void sendUpdate(BlockEntity blockEntity) {
        Packet<?> packet = blockEntity.getUpdatePacket();
        if (packet != null) {
            sendToChunkWatchers(blockEntity.getLevel(), blockEntity.getBlockPos(), packet);
        }
    }

    /**
     * Invia un pacchetto con CompoundTag custom (aggiunge automaticamente id e pos).
     */
    public static void sendUpdatePacket(BlockEntity blockEntity, CompoundTag tag) {
        writeIdAndPos(blockEntity, tag);
        ClientboundBlockEntityDataPacket packet =
                ClientboundBlockEntityDataPacket.create(blockEntity, (entity, registryAccess) -> tag);
        sendToChunkWatchers(blockEntity.getLevel(), blockEntity.getBlockPos(), packet);
    }

    private static void writeIdAndPos(BlockEntity blockEntity, CompoundTag tag) {
        ResourceLocation id = Registries.BLOCK_ENTITY_TYPE.location();
        if (id != null) {
            tag.putString("id", id.toString());
            BlockPos pos = blockEntity.getBlockPos();
            tag.putInt("x", pos.getX());
            tag.putInt("y", pos.getY());
            tag.putInt("z", pos.getZ());
        }
    }

    private static void sendToChunkWatchers(Level level, BlockPos pos, Packet<?> packet) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        List<ServerPlayer> players = serverLevel.getChunkSource()
                .chunkMap.getPlayers(new ChunkPos(pos), false);
        for (ServerPlayer player : players) {
            player.connection.send(packet);
        }
    }
}