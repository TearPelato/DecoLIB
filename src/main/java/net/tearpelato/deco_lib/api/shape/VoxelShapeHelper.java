package net.tearpelato.deco_lib.api.shape;

import com.google.common.base.Preconditions;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class VoxelShapeHelper
{
    public static VoxelShape combineAll(Collection<VoxelShape> shapes)
    {
        VoxelShape result = Shapes.empty();
        for(VoxelShape shape : shapes)
        {
            result = Shapes.joinUnoptimized(result, shape, BooleanOp.OR);
        }
        return result.optimize();
    }

    public static VoxelShape setMaxHeight(VoxelShape source, double height)
    {
        AtomicReference<VoxelShape> result = new AtomicReference<>(Shapes.empty());
        source.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) ->
        {
            VoxelShape shape = Shapes.box(minX, minY, minZ, maxX, height, maxZ);
            result.set(Shapes.joinUnoptimized(result.get(), shape, BooleanOp.OR));
        });
        return result.get().optimize();
    }

    public static VoxelShape limitHorizontal(VoxelShape source)
    {
        AtomicReference<VoxelShape> result = new AtomicReference<>(Shapes.empty());
        source.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) ->
        {
            VoxelShape shape = Shapes.box(limit(minX), minY, limit(minZ), limit(maxX), maxY, limit(maxZ));
            result.set(Shapes.joinUnoptimized(result.get(), shape, BooleanOp.OR));
        });
        return result.get().optimize();
    }

    public static VoxelShape[] getRotatedShapes(VoxelShape source)
    {
        VoxelShape shapeNorth = rotate(source, Direction.NORTH);
        VoxelShape shapeEast = rotate(source, Direction.EAST);
        VoxelShape shapeSouth = rotate(source, Direction.SOUTH);
        VoxelShape shapeWest = rotate(source, Direction.WEST);
        return new VoxelShape[] { shapeSouth, shapeWest, shapeNorth, shapeEast };
    }

    public static VoxelShape rotate(VoxelShape source, Direction direction)
    {
        double[] adjustedValues = adjustValues(direction, source.min(Direction.Axis.X), source.min(Direction.Axis.Z), source.max(Direction.Axis.X), source.max(Direction.Axis.Z));
        return Shapes.box(adjustedValues[0], source.min(Direction.Axis.Y), adjustedValues[1], adjustedValues[2], source.max(Direction.Axis.Y), adjustedValues[3]);
    }

    private static double[] adjustValues(Direction direction, double var1, double var2, double var3, double var4)
    {
        switch(direction)
        {
            case WEST:
                double var_temp_1 = var1;
                var1 = 1.0F - var3;
                double var_temp_2 = var2;
                var2 = 1.0F - var4;
                var3 = 1.0F - var_temp_1;
                var4 = 1.0F - var_temp_2;
                break;
            case NORTH:
                double var_temp_3 = var1;
                var1 = var2;
                var2 = 1.0F - var3;
                var3 = var4;
                var4 = 1.0F - var_temp_3;
                break;
            case SOUTH:
                double var_temp_4 = var1;
                var1 = 1.0F - var4;
                double var_temp_5 = var2;
                var2 = var_temp_4;
                double var_temp_6 = var3;
                var3 = 1.0F - var_temp_5;
                var4 = var_temp_6;
                break;
            default:
                break;
        }
        return new double[]{var1, var2, var3, var4};
    }

    private static double limit(double value)
    {
        return Math.max(0.0, Math.min(1.0, value));
    }
    public static VoxelShape rotateHorizontally(VoxelShape shape, Direction direction)
    {
        Preconditions.checkArgument(direction.getAxis().isHorizontal());
        return shape.toAabbs().stream().map(box -> createRotatedShape(box, direction)).reduce(Shapes.empty(), VoxelShapeHelper::join);
    }
    public static VoxelShape combine(List<VoxelShape> shapes)
    {
        return shapes.stream().reduce(Shapes.empty(), VoxelShapeHelper::join).optimize();
    }
    private static VoxelShape join(VoxelShape a, VoxelShape b)
    {
        return Shapes.joinUnoptimized(a, b, BooleanOp.OR);
    }
    private static VoxelShape createRotatedShape(AABB box, Direction direction)
    {
        return switch(direction)
        {
            case WEST -> Shapes.box(1 - box.maxX, box.minY, 1 - box.maxZ, 1 - box.minX, box.maxY, 1 - box.minZ);
            case NORTH -> Shapes.box(box.minZ, box.minY, 1 - box.maxX, box.maxZ, box.maxY, 1 - box.minX);
            case SOUTH -> Shapes.box(1 - box.maxZ, box.minY, box.minX, 1 - box.minZ, box.maxY, box.maxX);
            default -> Shapes.box(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
        };
    }

    public static VoxelShape rotateShape(VoxelShape source, Direction direction) {
        AtomicReference<VoxelShape> newShape = new AtomicReference<>(Shapes.empty());
        source.forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> {
            Vec3 min = new Vec3(minX - 0.5, minY - 0.5, minZ - 0.5);
            Vec3 max = new Vec3(maxX - 0.5, maxY - 0.5, maxZ - 0.5);
            Vec3 v1 = rotateVec3(min, direction);
            Vec3 v2 = rotateVec3(max, direction);
            VoxelShape s = Shapes.create(0.5 + Math.min(v1.x, v2.x), 0.5 + Math.min(v1.y, v2.y), 0.5 + Math.min(v1.z, v2.z),
                    0.5 + Math.max(v1.x, v2.x), 0.5 + Math.max(v1.y, v2.y), 0.5 + Math.max(v1.z, v2.z));
            newShape.set(Shapes.or(newShape.get(), s));
        });
        return newShape.get();
    }

    public static Vec3 rotateVec3(Vec3 vec, Direction dir) {
        double cos = 1;
        double sin = 0;
        switch (dir) {
            case SOUTH -> {
                cos = -1;
                sin = 0;
            }
            case WEST -> {
                cos = 0;
                sin = 1;
            }
            case EAST -> {
                cos = 0;
                sin = -1;
            }
        }
        double d0 = vec.x * cos + vec.z * sin;
        double d1 = vec.y;
        double d2 = vec.z * cos - vec.x * sin;
        return new Vec3(d0, d1, d2);
    }
}
