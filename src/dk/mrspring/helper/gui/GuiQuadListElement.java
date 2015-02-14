package dk.mrspring.helper.gui;

import dk.mrspring.helper.gui.screen.GuiScreenIconMaker;
import dk.mrspring.llcore.Color;
import dk.mrspring.llcore.DrawingHelper;
import dk.mrspring.llcore.Quad;
import dk.mrspring.llcore.Vector;

import java.text.DecimalFormat;

/**
 * Created by Konrad on 30-01-2015.
 */
public class GuiQuadListElement
{
    String name;
    public boolean expanded = false;
    DecimalFormat format = new DecimalFormat("0.00");
    public GuiQuadEditor editor;

    public GuiQuadListElement(String name, GuiQuadEditor editor)
    {
        this.name = name;
        this.editor = editor;
    }

    public GuiQuadListElement setEditing(boolean editing)
    {
        this.editor.setEditing(editing);
        expanded = editing;
        return this;
    }

    public int getHeight()
    {
        return expanded ? 40 : 15;
    }

    public int draw(int mouseX, int mouseY, GuiScreenIconMaker.GuiQuadList parent, DrawingHelper helper)
    {
        int maxWidth = parent.getWidth();
        int height = expanded ? 40 : 15;
        boolean isMouseHovering = isMouseHivering(mouseX, mouseY, parent);
        float alpha = isMouseHovering ? 0.75F : 0.3F;

        helper
                .drawShape(new Quad(1, 0, maxWidth - 2, height).setColor(Color.BLACK).setAlpha(alpha))
                .drawShape(new Quad(0, 1, 1, height - 2).setColor(Color.BLACK).setAlpha(alpha))
                .drawShape(new Quad(maxWidth - 1, 1, 1, height - 2).setColor(Color.BLACK).setAlpha(alpha));

        helper
                .drawShape(new Quad(1, 1, maxWidth - 2, 1))
                .drawShape(new Quad(1, height - 2, maxWidth - 2, 1).setColor(Color.LT_GREY))
                .drawShape(new Quad(1, 2, 1, height - 4))
                .drawShape(new Quad(maxWidth - 2, 2, 1, height - 4).setColor(Color.LT_GREY));

        helper.drawText(name, new Vector(maxWidth / 2, 4), 0xFFFFFF, true, -1, DrawingHelper.VerticalTextAlignment.CENTER, DrawingHelper.HorizontalTextAlignment.TOP);

        if (expanded)
        {
            if (editor != null)
            {
                Vector[] vectors = editor.getVectors();

                String vectorString = "1: " + format.format(vectors[0].getX()) + ", " + format.format(vectors[0].getY());
                helper.drawText(vectorString, new Vector(maxWidth / 4, 17);

                vectorString = "2: " + format.format(vectors[1].getX()) + ", " + format.format(vectors[1].getY());
                helper.drawCenteredText(vectorString, maxWidth / 4 * 3, 17);

                vectorString = "4: " + format.format(vectors[3].getX()) + ", " + format.format(vectors[3].getY());
                helper.drawCenteredText(vectorString, maxWidth / 4, 27);

                vectorString = "3: " + format.format(vectors[2].getX()) + ", " + format.format(vectors[2].getY());
                helper.drawCenteredText(vectorString, maxWidth / 4 * 3, 27);
            }
        }

        return height;
    }

    public boolean isMouseHivering(int mouseX, int mouseY, GuiScreenIconMaker.GuiQuadList parent)
    {
        int maxWidth = parent.getWidth();
        int height = expanded ? 40 : 15;
        return isMouseHoveringArea(mouseX, mouseY, 0, 0, maxWidth, height);
    }

    private boolean isMouseHoveringArea(int mouseX, int mouseY, float xPos, float yPos, float width, float height)
    {
        return mouseX >= xPos && mouseY >= yPos && mouseX < xPos + width && mouseY < yPos + height;
    }
}
