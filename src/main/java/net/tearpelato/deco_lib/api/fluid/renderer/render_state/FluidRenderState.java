package net.tearpelato.deco_lib.api.fluid.renderer.render_state;

import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.tearpelato.deco_lib.api.fluid.renderer.core.FluidSprites;

public class FluidRenderState extends BlockEntityRenderState {
    public Direction facing;
    public Fluid fluid;
    public Level level;
    public BlockAndTintGetter world;
    public BlockPos pos;
    //public KitchenSinkBlockEntity be;
    public FluidSprites fluidSprites;
    public int waterTintAtPos = 0xFFFFFF;
}
