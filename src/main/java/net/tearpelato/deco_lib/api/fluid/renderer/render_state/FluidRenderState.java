package net.tearpelato.deco_lib.api.fluid.renderer.render_state;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.tearpelato.deco_lib.api.fluid.block_entity.FluidContainerBlockEntity;
import net.tearpelato.deco_lib.api.fluid.renderer.core.FluidSprites;
import org.jetbrains.annotations.Nullable;

public class FluidRenderState extends BlockEntityRenderState {
    public Direction facing;
    public Fluid fluid;
    public Level level;
    public BlockAndTintGetter world;
    public FluidSprites fluidSprites;
    public FluidContainerBlockEntity be;
    public AABB box;
    public int waterTintAtPos = 0xFFFFFF;


    public boolean valid() {
        return this.facing != null && this.fluid != null;
    }

    public static void extract(FluidRenderState state, @Nullable Level level, BlockPos pos)
    {
        FluidContainerBlockEntity container = state.be;
        if(container != null && !container.isEmpty())
        {
            Fluid fluid = container.getFluid();
            BlockAndTintGetter tintGetter = level instanceof ClientLevel clientLevel ? clientLevel : BlockAndTintGetter.EMPTY;
            state.waterTintAtPos = BiomeColors.getAverageWaterColor(tintGetter, pos);
        }
    }


}
