package net.tearpelato.deco_lib.api.fluid.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;

import javax.annotation.Nullable;

public class FluidContainerRendererUtil {

    public static TextureAtlasSprite[] getFluidSprites(Fluid fluid) {
        IClientFluidTypeExtensions ext = IClientFluidTypeExtensions.of(fluid);

        TextureAtlas atlas = (TextureAtlas) Minecraft.getInstance().getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS);

        TextureAtlasSprite still = ext.getStillTexture() != null ? atlas.getSprite(ext.getStillTexture()) : null;
        TextureAtlasSprite flowing = ext.getFlowingTexture() != null ? atlas.getSprite(ext.getFlowingTexture()) : null;

        if (still == null && flowing == null) return null;
        return new TextureAtlasSprite[]{still, flowing};
    }

    public static int getFluidColor(FluidStack stack, @Nullable BlockAndTintGetter world, @Nullable BlockPos pos) {
        IClientFluidTypeExtensions ext = IClientFluidTypeExtensions.of(stack.getFluid());
        FluidState state = (world != null && pos != null) ? world.getFluidState(pos) : null;
        if (state == null || !state.is(stack.getFluid())) state = stack.getFluid().defaultFluidState();
        int color = ext.getTintColor(state, world, pos);
        return color != -1 ? color : 0xFFFFFFFF;
    }
}