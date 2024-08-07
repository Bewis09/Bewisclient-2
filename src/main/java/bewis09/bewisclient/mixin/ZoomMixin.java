package bewis09.bewisclient.mixin;

import bewis09.bewisclient.JavaSettingsSender;
import bewis09.bewisclient.ZoomImplementer;
import bewis09.bewisclient.settingsLoader.Settings;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Environment(EnvType.CLIENT)
@Mixin(GameRenderer.class)
public class ZoomMixin implements ZoomImplementer {

    @Unique
    double zoomStart = 1;

    @Unique
    double zoomEnd = 1;

    @Unique
    long zoomStartTime = 0;

    @Unique
    boolean lastZoomed = false;

    @Unique
    double lastZoomGoal = 0.23;

    @Unique
    double zoomgoal = 0.23;

    @Inject(method = "getFov",at=@At("RETURN"),cancellable = true)
    private void getFov(Camera camera, float tickDelta, boolean changingFov, CallbackInfoReturnable<Double> cir) {
        if(JavaSettingsSender.Companion.getSettings().getBoolean("general","zoom_enabled")) {
            if (!JavaSettingsSender.Companion.getSettings().getBoolean("general","instant_zoom")) {
                if (JavaSettingsSender.Companion.isZoomed() != lastZoomed || zoomgoal != lastZoomGoal) {
                    zoomStart = getZoomFactor();
                    if (!JavaSettingsSender.Companion.isZoomed()) {
                        if (lastZoomed) {
                            zoomEnd = 1;
                            zoomgoal = 0.23f;
                        }
                    } else {
                        zoomEnd = zoomgoal;
                    }
                    lastZoomGoal = zoomgoal;
                    zoomStartTime = System.currentTimeMillis();
                }
                lastZoomed = JavaSettingsSender.Companion.isZoomed();
                cir.setReturnValue(getZoomFactor() * cir.getReturnValue());
            } else {
                cir.setReturnValue(cir.getReturnValue() * (JavaSettingsSender.Companion.isZoomed() ? zoomgoal : 1));
            }
        }
    }

    @Unique
    public double getZoomFactor() {
        long l = System.currentTimeMillis();
        if(zoomStartTime+100 > l) {
            return ((zoomEnd)*((l - zoomStartTime)/100f))+((zoomStart)*(1-(l - zoomStartTime)/100f));
        } else {
            return (zoomEnd);
        }
    }

    @Override
    public void bewisclient5_0$setGoal(double d) {
        zoomgoal = d;
    }

    @Override
    public double bewisclient5_0$getGoal() {
        return zoomgoal;
    }
}

