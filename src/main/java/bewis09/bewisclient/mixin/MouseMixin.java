package bewis09.bewisclient.mixin;

import bewis09.bewisclient.Bewisclient;
import bewis09.bewisclient.JavaSettingsSender;
import bewis09.bewisclient.ZoomImplementer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(Mouse.class)
public class MouseMixin {

    @Inject(method = "onMouseButton", at = @At("HEAD"))
    public void inject(long window, int button, int action, int mods, CallbackInfo ci) {
        if (action == 1) {
            if (button == 0) {
                Bewisclient.Companion.Companion.getLeftList().add(System.currentTimeMillis());
            } else if (button == 1) {
                Bewisclient.Companion.Companion.getRightList().add(System.currentTimeMillis());
            }
        }
    }

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    public void inject(long window, double horizontal, double vertical, CallbackInfo ci) {
        if(JavaSettingsSender.Companion.isZoomed() && JavaSettingsSender.Companion.getSettings().getBoolean("general","zoom_enabled")) {
            var a = (ZoomImplementer)(MinecraftClient.getInstance().gameRenderer);

            if(a.bewisclient5_0$getGoal()-vertical*0.02>0.009 && a.bewisclient5_0$getGoal()-vertical*0.02<0.4)
                a.bewisclient5_0$setGoal(a.bewisclient5_0$getGoal()-vertical*0.02);

            ci.cancel();
        }
    }
}
