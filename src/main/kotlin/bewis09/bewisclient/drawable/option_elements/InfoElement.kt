package bewis09.bewisclient.drawable.option_elements

import bewis09.bewisclient.Bewisclient
import bewis09.bewisclient.screen.MainOptionsScreen
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext

/**
 * An [OptionsElement] which displays an information
 *
 * @param text The translation key of the text
 */
class InfoElement(text: String): OptionsElement("",text) {
    override fun render(context: DrawContext, x: Int, y: Int, width: Int, mouseX: Int, mouseY: Int, alphaModifier: Long): Int {
        val client = MinecraftClient.getInstance()
        val descriptionLines = client.textRenderer.wrapLines(Bewisclient.getTranslationText(description),width-12)
        val height = (10+10*descriptionLines.size)

        context.fill(x,y,x+width,y+height, alphaModifier.toInt())
        context.drawBorder(x,y,width,height, (alphaModifier+0xFFFFFF).toInt())

        descriptionLines.iterator().withIndex().forEach { (index, line) ->
            context.drawTextWithShadow(client.textRenderer, line, x + 6, y+6 + 10 * index, (alphaModifier + 0x808080).toInt())
        }

        return height
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int, screen: MainOptionsScreen) {}
}