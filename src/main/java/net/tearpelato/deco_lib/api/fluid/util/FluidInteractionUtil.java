package net.tearpelato.deco_lib.api.fluid.util;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidUtil;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;

public class FluidInteractionUtil {

    public static Fluid getFluidFromItemStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return Fluids.EMPTY;
        }

        // Use FluidUtil to get the fluid handler
        IFluidHandlerItem handler = FluidUtil.getFluidHandler(stack).orElse(null);

        if (handler != null) {
            // Check the fluid in the first tank
            FluidStack fluidInHandler = handler.getFluidInTank(0);
            if (!fluidInHandler.isEmpty()) {
                return fluidInHandler.getFluid();
            }
        }

        return Fluids.EMPTY;
    }
}