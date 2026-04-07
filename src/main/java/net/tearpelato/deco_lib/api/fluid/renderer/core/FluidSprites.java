package net.tearpelato.deco_lib.api.fluid.renderer.core;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.FluidModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.material.FluidState;

/**
 * @author MrCrayfish*/

public record FluidSprites(TextureAtlasSprite still, TextureAtlasSprite flowing)
{
    public FluidSprites getFluidSprites(FluidState state)
    {
        FluidModel model = Minecraft.getInstance().getModelManager().getFluidStateModelSet().get(state);
        return new FluidSprites(model.stillMaterial().sprite(), model.flowingMaterial().sprite());
    }
}