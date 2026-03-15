package net.tearpelato.deco_lib.api.fluid.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class FluidInteractionUtil {

    public static Fluid getFluidFromItemStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return Fluids.EMPTY;
        }

        return stack.getCapability(ForgeCapabilities.FLUID_HANDLER_ITEM)
                .map(handler -> {
                    FluidStack fluidStack = handler.getFluidInTank(0);
                    return fluidStack.isEmpty() ? Fluids.EMPTY : fluidStack.getFluid();
                })
                .orElse(Fluids.EMPTY);
    }
}
