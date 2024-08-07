package bewis09.bewisclient.drawable

import bewis09.bewisclient.Bewisclient
import bewis09.bewisclient.drawable.UsableSliderWidget.Companion.withDecimalPlaces
import bewis09.bewisclient.screen.MainOptionsScreen
import bewis09.bewisclient.settingsLoader.SettingsLoader
import bewis09.bewisclient.util.ColorSaver
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import java.awt.Color
import kotlin.math.roundToInt

class ColorPickerElement(title: String, path: String, private val value: String, private val settings: String): WidgetOptionsElement(title, path, arrayListOf()) {

    var posX: Int = 0
    var posY: Int = 0

    var clicked = false

    var hClicked = false

    val colorStart = convertRGBtoHSB(ColorSaver.of(SettingsLoader.getString(settings,path)))

    var changing = ColorSaver.of(SettingsLoader.getString(settings,path)).getColor()<0

    val static = ScalableButtonWidget.builder(Bewisclient.getTranslationText("color.static")) {
        changing = false

        SettingsLoader.set(settings,path,ColorSaver.of(ColorSaver.of(SettingsLoader.getString(settings,path)).getColor()+0x1000000))
    }.build()

    val change = ScalableButtonWidget.builder(Bewisclient.getTranslationText("color.change")) {
        changing = true

        SettingsLoader.set(settings,path,ColorSaver.of((-(widget.getValue()*(19000)+1000)).toInt()))
    }.build()

    val widget = UsableSliderWidget(0, 0, 100, 20, Text.empty(), if(ColorSaver.of(SettingsLoader.getString(settings,path)).getColor()<0)
        (((ColorSaver.of(SettingsLoader.getString(settings,path)).getOriginalColor()*-1).toDouble())-1000)/19000 else 9/19.0, 20000F, 1000F, -2, {
        SettingsLoader.set(settings,path,ColorSaver.of((-(it*(19000F)+1000F)).roundToInt()))
    },{
        Text.of(Bewisclient.getTranslatedString("gui.speed")+": "+withDecimalPlaces((it)*19+1,2)+" ${Bewisclient.getTranslatedString("seconds")}")
    })

    val widget2 = UsableSliderWidget(0, 0, 100, 20, Text.empty(),
            if(ColorSaver.of(SettingsLoader.getString(settings,path)).getColor()>0) colorStart[2].toDouble() else 1.0, 1F, 0F, 2, {
        SettingsLoader.set(settings,path,ColorSaver.of(convertRGBtoHSB(posX/60f,posY/60f,it.toFloat())+0x1000000))
    },{
        Text.of(Bewisclient.getTranslatedString("gui.brightness")+": "+withDecimalPlaces(it,2))
    })

    init {
        val color = convertRGBtoHSB(ColorSaver.of(SettingsLoader.getString(settings,path)))
        posX = MathHelper.clamp((color[0]*60).toInt(),0,58)
        posY = MathHelper.clamp((color[1]*60).toInt(),0,58)
    }

    override fun render(context: DrawContext, x: Int, y: Int, width: Int, mouseX: Int, mouseY: Int, alphaModifier: Long): Int {

        if(changing) {
            val color = convertRGBtoHSB(ColorSaver.of(SettingsLoader.getString(settings, path)))
            posX = MathHelper.clamp((color[0] * 60).toInt(), 0, 58)
            posY = MathHelper.clamp((color[1] * 60).toInt(), 0, 58)
        }

        val client = MinecraftClient.getInstance()

        val descriptionLines = client.textRenderer.wrapLines(Bewisclient.getTranslationText(description),width-76)

        pos = arrayOf(x,y,x+width,y+60)

        context.fill(x,y,x+width-64,y+60, alphaModifier.toInt())
        context.drawBorder(x,y,width-64,60, (alphaModifier+0xFFFFFF).toInt())

        context.fill(x+width-60,y,x+width,y+60, alphaModifier.toInt())

        RenderSystem.enableBlend()
        RenderSystem.setShaderColor(if(ColorSaver.of(SettingsLoader.getString(settings,path)).getOriginalColor()<0f) 0.5f else widget2.getValue().toFloat(),if(ColorSaver.of(SettingsLoader.getString(settings,path)).getOriginalColor()<0f) 0.5f else widget2.getValue().toFloat(),if(ColorSaver.of(SettingsLoader.getString(settings,path)).getOriginalColor()<0f) 0.5f else widget2.getValue().toFloat(),alphaModifier/(0xFFFFFFFF.toFloat()))
        context.drawTexture(Identifier.of("bewisclient","textures/color_space.png"),x+width-59,y+1,58,58,0f,0f,58,58,58,58)
        RenderSystem.setShaderColor(1f,1f,1f,1f)
        RenderSystem.disableBlend()

        context.drawBorder(x+width-61,y-1,62,62, (alphaModifier+ ColorSaver.of(SettingsLoader.getString(settings,path)).getColor()).toInt())
        context.drawBorder(x+width-60,y,60,60, (alphaModifier+ (ColorSaver.of(SettingsLoader.getString(settings,path)).getColor())).toInt())

        context.drawBorder(x+width-60+posX-1,y+posY-1,3,3, alphaModifier.toInt())

        context.drawTextWithShadow(client.textRenderer, Bewisclient.getTranslationText(title),x+6,y+6,(alphaModifier+0xFFFFFF).toInt())
        descriptionLines.iterator().withIndex().forEach { (index, line) ->
            context.drawTextWithShadow(client.textRenderer, line, x + 6, y + 20 + 10 * index, (alphaModifier + 0x808080).toInt())
        }

        static.setAlpha(alphaModifier.toFloat()/0xFFFFFFFF)
        change.setAlpha(alphaModifier.toFloat()/0xFFFFFFFF)
        widget.setAlpha(alphaModifier.toFloat()/0xFFFFFFFF)
        widget2.setAlpha(alphaModifier.toFloat()/0xFFFFFFFF)

        static.x = x+3
        static.y = y+37
        static.height = 20
        static.width = (width-66)/4-2
        static.render(context,mouseX,mouseY,0f)

        change.x = x+(width-66)/4+2
        change.y = y+37
        change.height = 20
        change.width = (width-66)/4-2
        change.render(context,mouseX,mouseY,0f)

        change.active=true
        static.active=true

        widget.x = x+(width-66)/2
        widget.y = y+37
        widget.width = (width-66)/2-1


        widget2.x = x+(width-66)/2
        widget2.y = y+37
        widget2.width = (width-66)/2-1

        widget.active=true

        if(!changing) {
            static.active=false
            widget.active=false
        } else
            change.active=false

        if(changing)
            widget.render(context,mouseX,mouseY,0f)
        else
            widget2.render(context,mouseX,mouseY,0f)

        return 60
    }

    fun convertRGBtoHSB(hue: Float, saturation: Float): Int {
        val rgb: Int = Color.HSBtoRGB(hue, saturation, 1f)
        return Color(rgb).rgb
    }

    fun convertRGBtoHSB(hue: Float, saturation: Float, brightness: Float): Int {
        val rgb: Int = Color.HSBtoRGB(hue, saturation, brightness)
        return Color(rgb).rgb
    }

    fun convertRGBtoHSB(color: ColorSaver): FloatArray {
        val c = Color(color.getColor())
        val rgb: FloatArray = Color.RGBtoHSB(c.red,c.green,c.blue,null)
        return rgb
    }

    override fun onDrag(mouseX: Double, mouseY: Double, deltaX: Double, deltaY: Double, button: Int) {
        if (clicked && !widget.active) {
            posX = MathHelper.clamp((mouseX-pos[2]+60).toInt(),1,58)
            posY = MathHelper.clamp((mouseY-pos[1]).toInt(),1,58)

            SettingsLoader.set(settings,path,ColorSaver.of(convertRGBtoHSB((posX-1)/58f,(posY-1)/58f,widget2.getValue().toFloat())+0x1000000))
        }
        if(widget.isHovered && hClicked && widget.active)
            widget.mouseDragged(mouseX,mouseY,button,deltaX,deltaY)
        if(widget2.isHovered && hClicked && !widget.active)
            widget2.mouseDragged(mouseX,mouseY,button,deltaX,deltaY)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int, screen: MainOptionsScreen) {
        if(widget.isHovered)
            hClicked = true
        if(widget2.isHovered)
            hClicked = true
        if(widget.active)
            widget.mouseClicked(mouseX, mouseY, button)
        else
            widget2.mouseClicked(mouseX, mouseY, button)
        if (pos[2]-59 < mouseX && pos[1]+1 < mouseY && pos[2]-1 > mouseX && pos[3]-1 > mouseY  && !widget.active) {
            clicked = true

            posX = (mouseX-pos[2]+60).toInt()
            posY = (mouseY-pos[1]).toInt()

            SettingsLoader.set(settings,value,ColorSaver.of(convertRGBtoHSB((posX-1)/58f,(posY-1)/58f)+0x1000000))
        }
        static.mouseClicked(mouseX, mouseY,button)
        change.mouseClicked(mouseX, mouseY,button)
    }

    override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int) {
        clicked = false
        hClicked = false
    }
}