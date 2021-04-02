package io.craigcarr.mixins;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CraftingScreenHandler.class)
public abstract class CraftingScreenHandlerMixin_Utilities extends AbstractRecipeScreenHandler<CraftingInventory> {
    @Shadow @Final private ScreenHandlerContext context;

    public CraftingScreenHandlerMixin_Utilities(ScreenHandlerType<?> screenHandlerType, int i) {
        super(screenHandlerType, i);
        System.out.println("My mixin loaded...");
    }

    @Shadow
    protected static void updateResult(int syncId, World world, PlayerEntity player, CraftingInventory craftingInventory, CraftingResultInventory resultInventory) {
    }

    @Shadow @Final private PlayerEntity player;

    @Shadow @Final private CraftingInventory input;

    @Shadow @Final private CraftingResultInventory result;

    @Shadow public abstract void close(PlayerEntity player);

    @Inject(method = "onContentChanged", at = @At(value = "HEAD", target = "Lnet/minecraft/screen/CraftingScreenHandler;onContentChanged(Lnet/minecraft/inventory/Inventory;)V"), cancellable = true)
    private void modifyContentChanged(Inventory inventory, CallbackInfo ci) {
        if (context == ScreenHandlerContext.EMPTY) {
            updateResult(this.syncId, this.player.getEntityWorld(), this.player, this.input, this.result);
            ci.cancel();
            System.out.println("My mixin detected modification...");
        }
    }

    @Inject(method = "close", at = @At(value = "HEAD", target = "Lnet/minecraft/screen/CraftingScreenHandler;close(Lnet/minecraft/entity/player/PlayerEntity;)V"), cancellable = true)
    private void modifyClose(PlayerEntity player, CallbackInfo ci) {
        if (context == ScreenHandlerContext.EMPTY) {
            super.close(player);
            this.dropInventory(player, player.world, input);
            ci.cancel();
            System.out.println("My mixin detected close event...");
        }
    }
}