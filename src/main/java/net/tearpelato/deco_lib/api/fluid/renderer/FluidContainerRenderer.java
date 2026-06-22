package net.tearpelato.deco_lib.api.fluid.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.util.ARGB;
import net.minecraft.world.phys.AABB;
import net.tearpelato.deco_lib.api.fluid.renderer.render_state.FluidRenderState;

public class FluidContainerRenderer {

    public static void submit(FluidRenderState state, PoseStack ps, SubmitNodeCollector collector) {
        if(!state.valid())
            return;
        collector.submitCustomGeometry(ps, RenderTypes.translucentMovingBlock(), (pose, consumer)-> {
            drawContainer(state, pose, consumer);
        });

    }

    public static void drawContainer(FluidRenderState state, PoseStack.Pose pose, VertexConsumer consumer) {

        AABB box = state.box;
        TextureAtlasSprite still = state.fluidSprites.still();
        int color = state.waterTintAtPos;
        float r = ARGB.red(color) / 255f;
        float g = ARGB.green(color) / 255f;
        float b = ARGB.blue(color) / 255f;
        float a = 1.0f;
        float fullness = (float) state.be.getStoredAmount() / state.be.getCapacity();
        float y = (float) box.minY + (float)(box.maxY - box.minY) * fullness;
        y = Math.min((float) box.maxY, Math.max((float)state. box.minY, y));
        float u0 = still.getU0() + (still.getU1() - still.getU0()) * (float) (box.minX - Math.floor(box.minX));
        float u1 = still.getU0() + (still.getU1() - still.getU0()) * (float) (box.maxX - Math.floor(box.minX));
        float v0 = still.getV0() + (still.getV1() - still.getV0()) * (float) (box.minZ - Math.floor(box.minZ));
        float v1 = still.getV0() + (still.getV1() - still.getV0()) * (float) (box.maxZ - Math.floor(box.minZ));

        int light = state.lightCoords;
        consumer.addVertex(pose, (float) box.minX, y, (float) box.minZ).setColor(r,g,b,a).setUv(u0,v0).setLight(light).setNormal(0,1,0);
        consumer.addVertex(pose, (float) box.minX, y, (float) box.maxZ).setColor(r,g,b,a).setUv(u0,v1).setLight(light).setNormal(0,1,0);
        consumer.addVertex(pose, (float) box.maxX, y, (float) box.maxZ).setColor(r,g,b,a).setUv(u1,v1).setLight(light).setNormal(0,1,0);
        consumer.addVertex(pose, (float) box.maxX, y, (float) box.minZ).setColor(r,g,b,a).setUv(u1,v0).setLight(light).setNormal(0,1,0);
    }

    public static AABB createRotatedBox(Direction dir, double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        minX /= 16.0; minY /= 16.0; minZ /= 16.0;
        maxX /= 16.0; maxY /= 17.0; maxZ /= 16.0;
        return switch (dir) {
            case WEST -> new AABB(1-maxX,minY,1-maxZ,1-minX,maxY,1-minZ);
            case NORTH -> new AABB(minZ,minY,1-maxX,maxZ,maxY,1-minX);
            case SOUTH -> new AABB(1-minZ,minY,minX,1-maxZ,maxY,maxX);
            default -> new AABB(minX,minY,minZ,maxX,maxY,maxZ);
        };
    }
}