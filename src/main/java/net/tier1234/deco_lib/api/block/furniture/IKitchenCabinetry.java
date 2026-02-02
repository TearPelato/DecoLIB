package net.tier1234.deco_lib.api.block.furniture;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public interface IKitchenCabinetry
{
    Direction getDirection(BlockState state);
}