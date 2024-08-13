package bewis09.bewisclient.drawable.option_elements

import bewis09.bewisclient.Bewisclient
import bewis09.bewisclient.screen.MainOptionsScreen
import bewis09.bewisclient.settingsLoader.SettingsLoader
import bewis09.bewisclient.util.Search
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.util.Identifier
import kotlin.math.max

open class MainOptionsElement: OptionsElement, Search.SearchableElement<OptionsElement> {

    var screen: ((MainOptionsScreen) -> Screen)? = null
    var elements: ArrayList<OptionsElement>? = null
    val image: Identifier

    constructor(title: String, description: String, screen: ((MainOptionsScreen) -> Screen), image: Identifier): super(title,description) {
        this.screen = screen
        this.image = image
        this.select = Identifier.of("bewisclient", "textures/sprites/select.png")
        this.selectHovered = Identifier.of("bewisclient", "textures/sprites/select_highlighted.png")
    }

    constructor(title: String, description: String, image: Identifier): super(title,description) {
        this.image = image
        this.select = Identifier.of("bewisclient", "textures/sprites/select.png")
        this.selectHovered = Identifier.of("bewisclient", "textures/sprites/select_highlighted.png")
    }

    constructor(title: String, description: String, elements: ArrayList<OptionsElement>, image: Identifier): super(title,description) {
        this.elements = elements
        this.image = image
        this.select = Identifier.of("bewisclient", "textures/sprites/select.png")
        this.selectHovered = Identifier.of("bewisclient", "textures/sprites/select_highlighted.png")
    }

    val select: Identifier
    val selectHovered: Identifier

    var allClicked: Boolean = false

    override fun render(context: DrawContext, x: Int, y: Int, width: Int, mouseX: Int, mouseY: Int, alphaModifier: Long): Int {
        val client = MinecraftClient.getInstance()

        allClicked = SettingsLoader.get("design", OPTIONS_MENU, ALL_CLICK)

        val descriptionLines = client.textRenderer.wrapLines(Bewisclient.getTranslationText(description),width-(if(allClicked) 50 else 72))

        val height = max(44,24+10*descriptionLines.size)

        val isSelected: Boolean

        pos = arrayOf(x,y,x+width,y+height)

        context.matrices.push()

        if(!allClicked) {
            isSelected = x+width-20 < mouseX && y < mouseY && x+width > mouseX && y+height > mouseY

            context.fill(x, y, x + width - (22), y + height, alphaModifier.toInt())
            context.drawBorder(x, y, width - (22), height, (alphaModifier + 0xFFFFFF).toInt())
        } else {
            isSelected = x < mouseX && y < mouseY && x+width > mouseX && y+height > mouseY

            if(isSelected) {
                context.matrices.translate(x.toFloat(),y.toFloat(),0f)
                context.matrices.scale(1+2f/width,1+2f/width,1f)
                context.matrices.translate(-x.toFloat()-1,-y.toFloat()-height/width,0f)
            }

            context.fill(x, y, x + width, y + height, alphaModifier.toInt())
            context.drawBorder(x, y, width, height, (alphaModifier + (if(isSelected) 0xAAAAFF else 0xFFFFFF)).toInt())
        }

        RenderSystem.enableBlend()

        context.setShaderColor(1F,1F,1F, (alphaModifier.toFloat()/0xFFFFFFFF))
        context.drawTexture(image,x+6,y+6,32,32,0F,0F,32,32,32,32)
        context.setShaderColor(1F,1F,1F, 1F)

        RenderSystem.disableBlend()

        context.drawTextWithShadow(client.textRenderer,Bewisclient.getTranslationText(title),x+44,y+6,(alphaModifier+0xFFFFFF).toInt())
        descriptionLines.iterator().withIndex().forEach { (index, line) ->
            context.drawTextWithShadow(client.textRenderer, line, x + 44, y + 20 + 10 * index, (alphaModifier + 0x808080).toInt())
        }

        if(!allClicked) {
            if(!isSelected) {
                context.fill(x+width-20,y,x+width,y+height, (alphaModifier).toInt())
                context.drawBorder(x+width-20,y,20,height, (alphaModifier+ 0xFFFFFF).toInt())
            } else {
                context.fill(x+width-20-1,y-1,x+width+1,y+height+1, (alphaModifier).toInt())
                context.drawBorder(x+width-20-1,y-1,22,height+2, (alphaModifier+ 0xAAAAFF).toInt())
            }

            RenderSystem.enableBlend()

            context.setShaderColor(1F, 1F, 1F, (alphaModifier.toFloat() / 0xFFFFFFFF))

            if (!isSelected) {
                context.drawTexture(select, x + width - 27, y + height / 2 - 16, 32, 32, 0F, 0F, 32, 32, 32, 32)
            } else {
                context.drawTexture(selectHovered, x + width - 29, y + height / 2 - 18, 36, 36, 0F, 0F, 36, 36, 36, 36)
            }

            context.setShaderColor(1F, 1F, 1F, 1F)

            RenderSystem.disableBlend()
        }

        context.matrices.pop()

        return height
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int, screen: MainOptionsScreen) {
        val isSelected = pos[2] - 20 < mouseX && pos[1] < mouseY && pos[2] > mouseX && pos[3] > mouseY
        if (!allClicked) {
            if (isSelected) {
                screen.playDownSound(MinecraftClient.getInstance().soundManager)
                if(elements!=null)
                    screen.openNewSlice(elements!!)
                else if(this.screen!=null)
                    screen.startAllAnimation(this.screen!!(screen))
            }
        } else {
            val isSelectedAll = pos[0] < mouseX && pos[1] < mouseY && pos[2] > mouseX && pos[3] > mouseY
            if (isSelectedAll) {
                screen.playDownSound(MinecraftClient.getInstance().soundManager)
                if(elements!=null)
                    screen.openNewSlice(elements!!)
                else if(this.screen!=null)
                    screen.startAllAnimation(this.screen!!(screen))
            }
        }
    }

    override fun getChildElementsForSearch(): ArrayList<OptionsElement>? {
        return elements
    }
}