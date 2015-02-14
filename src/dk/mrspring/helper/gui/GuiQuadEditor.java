package dk.mrspring.helper.gui;

import dk.mrspring.llcore.Color;
import dk.mrspring.llcore.DrawingHelper;
import dk.mrspring.llcore.Quad;
import dk.mrspring.llcore.Vector;

import java.math.BigDecimal;

/**
 * Created by Konrad on 30-01-2015.
 */
public class GuiQuadEditor
{
    Vector v1, v2, v3, v4; // TODO: Convert to array
    boolean editing = false;
    float alpha = 1F;
    int draggingVector = 0;

    public GuiQuadEditor(Quad baseQuad)
    {
        this.v1 = baseQuad.getVectors()[0];
        this.v2 = baseQuad.getVectors()[1];
        this.v3 = baseQuad.getVectors()[2];
        this.v4 = baseQuad.getVectors()[3];
    }

    public Quad toQuad()
    {
        return new Quad(v1, v2, v3, v4); // TODO: Move entire quad
    }                                    // TODO: Copy/paste quads?

    public Vector[] getVectors()
    {
        return new Vector[]{v1, v2, v3, v4};
    }

    public GuiQuadEditor setEditing(boolean editing)
    {
        this.editing = editing;
        return this;
    }

    public void draw(int mouseX, int mouseY, float gridSize, DrawingHelper helper)
    {
        int hoveringVector = getHoveringVector(mouseX, mouseY, gridSize);

        Vector[] scaledVectors = new Vector[]{
                new Vector(v1.getX() * gridSize, v1.getY() * gridSize),
                new Vector(v2.getX() * gridSize, v2.getY() * gridSize),
                new Vector(v3.getX() * gridSize, v3.getY() * gridSize),
                new Vector(v4.getX() * gridSize, v4.getY() * gridSize)};

        helper.drawShape(new Quad(scaledVectors[0], scaledVectors[1], scaledVectors[2], scaledVectors[3]).setAlpha(alpha));
        helper.setZIndex(5);
        if (isEditing())
            for (int i = 0; i < scaledVectors.length; i++)
                helper.drawShape(new Quad(scaledVectors[i].getX() - 3, scaledVectors[i].getY() - 3, 6, 6).setAlpha(hoveringVector == i + 1 ? 1 : 0.5F).setColor(Color.LT_GREY));
        helper.setZIndex(0);
    }

    public int getHoveringVector(int mouseX, int mouseY, float gridSize)
    {
        final int HOVER_BOX_SIZE = 3;

        Vector[] scaledVectors = new Vector[]{
                new Vector(v1.getX() * gridSize, v1.getY() * gridSize),
                new Vector(v2.getX() * gridSize, v2.getY() * gridSize),
                new Vector(v3.getX() * gridSize, v3.getY() * gridSize),
                new Vector(v4.getX() * gridSize, v4.getY() * gridSize)};

        for (int i = 0; i < scaledVectors.length; i++)
            if (isMouseHoveringArea(mouseX, mouseY, scaledVectors[i].getX() - HOVER_BOX_SIZE, scaledVectors[i].getY() - HOVER_BOX_SIZE, 2 * HOVER_BOX_SIZE, 2 * HOVER_BOX_SIZE))
                return i + 1;
        return 0;
    }

    public void mouseDown(int mouseX, int mouseY, float gridSize)
    {
        int hoveringVector = getHoveringVector(mouseX, mouseY, gridSize);
        System.out.println("hoveringVector = " + hoveringVector);

        if (hoveringVector > 0)
            draggingVector = hoveringVector;
    }

    public interface GridScaler
    {
        public BigDecimal getX(int mouseX, float gridScale);

        public BigDecimal getY(int mouseY, float gridScale);
    }

    public boolean mouseDrag(int mouseX, int mouseY, float gridScale, boolean snapToGrid, GridScaler scaler)
    {
        if (draggingVector <= 0)
            return false;
        else
        {
            BigDecimal x = new BigDecimal((mouseX) / gridScale);
            BigDecimal y = new BigDecimal((mouseY) / gridScale);

            if (snapToGrid)
            {
                x = scaler.getX(mouseX, gridScale);
                y = scaler.getY(mouseY, gridScale);//y.setScale(1, BigDecimal.ROUND_HALF_DOWN);
            }

            if (draggingVector == 1)
            {
                v1.setX(x.floatValue());
                v1.setY(y.floatValue());
            } else if (draggingVector == 2)
            {
                v2.setX(x.floatValue());
                v2.setY(y.floatValue());
            } else if (draggingVector == 3)
            {
                v3.setX(x.floatValue());
                v3.setY(y.floatValue());
            } else if (draggingVector == 4)
            {
                v4.setX(x.floatValue());
                v4.setY(y.floatValue());
            } else return false;
            return true;
        }
    }

    public void mouseReleased()
    {
        draggingVector = 0;
    }

    private boolean isMouseHoveringArea(int mouseX, int mouseY, float xPos, float yPos, float width, float height)
    {
        return mouseX >= xPos && mouseY >= yPos && mouseX < xPos + width && mouseY < yPos + height;
    }

    public boolean isEditing()
    {
        return editing;
    }

/*
    Vector v1, v2, v3, v4;
    boolean expanded = false;
    boolean highlightOne = false;
    boolean highlightTwo = false;
    boolean highlightThr = false;
    boolean highlightFou = false;
    int dragging = 0;
    String name;

    public GuiListQuad(String name)
    {
        this.name = name;
        v1 = new Vector(0, 0);
        v2 = new Vector(1, 0);
        v3 = new Vector(1, 1);
        v4 = new Vector(0, 1);
    }

    public void drawGraph(int mouseX, int mouseY, float gridSize, DrawingHelper helper)
    {
        final int DOT_SIZE = 3;
        final int ONE_DOT_HOVER_SIZE = highlightOne ? 3 : 2;
        final int TWO_DOT_HOVER_SIZE = highlightTwo ? 3 : 2;
        final int THR_DOT_HOVER_SIZE = highlightThr ? 3 : 2;
        final int FOU_DOT_HOVER_SIZE = highlightFou ? 3 : 2;

        Vector scaledV1 = new Vector(v1.getX() * gridSize, v1.getY() * gridSize);
        Vector scaledV2 = new Vector(v2.getX() * gridSize, v2.getY() * gridSize);
        Vector scaledV3 = new Vector(v3.getX() * gridSize, v3.getY() * gridSize);
        Vector scaledV4 = new Vector(v4.getX() * gridSize, v4.getY() * gridSize);

        helper.drawShape(new Quad(scaledV1, scaledV2, scaledV3, scaledV4).setAlpha(expanded ? 1F : 0.5F));

        if (expanded)
        {
            highlightOne = isMouseHoveringArea(mouseX, mouseY, (int) scaledV1.getX() - DOT_SIZE, (int) scaledV1.getY() - DOT_SIZE, 2 * DOT_SIZE, 2 * DOT_SIZE);
            highlightTwo = isMouseHoveringArea(mouseX, mouseY, (int) scaledV2.getX() - DOT_SIZE, (int) scaledV2.getY() - DOT_SIZE, 2 * DOT_SIZE, 2 * DOT_SIZE);
            highlightThr = isMouseHoveringArea(mouseX, mouseY, (int) scaledV3.getX() - DOT_SIZE, (int) scaledV3.getY() - DOT_SIZE, 2 * DOT_SIZE, 2 * DOT_SIZE);
            highlightFou = isMouseHoveringArea(mouseX, mouseY, (int) scaledV4.getX() - DOT_SIZE, (int) scaledV4.getY() - DOT_SIZE, 2 * DOT_SIZE, 2 * DOT_SIZE);

            helper
                    .drawShape(new Quad(scaledV1.getX() - ONE_DOT_HOVER_SIZE, scaledV1.getY() - ONE_DOT_HOVER_SIZE, ONE_DOT_HOVER_SIZE * 2, ONE_DOT_HOVER_SIZE * 2).setAlpha(highlightOne ? 1F : 0.5F))
                    .drawShape(new Quad(scaledV2.getX() - TWO_DOT_HOVER_SIZE, scaledV2.getY() - TWO_DOT_HOVER_SIZE, TWO_DOT_HOVER_SIZE * 2, TWO_DOT_HOVER_SIZE * 2).setAlpha(highlightTwo ? 1F : 0.5F))
                    .drawShape(new Quad(scaledV3.getX() - THR_DOT_HOVER_SIZE, scaledV3.getY() - THR_DOT_HOVER_SIZE, THR_DOT_HOVER_SIZE * 2, THR_DOT_HOVER_SIZE * 2).setAlpha(highlightThr ? 1F : 0.5F))
                    .drawShape(new Quad(scaledV4.getX() - FOU_DOT_HOVER_SIZE, scaledV4.getY() - FOU_DOT_HOVER_SIZE, FOU_DOT_HOVER_SIZE * 2, FOU_DOT_HOVER_SIZE * 2).setAlpha(highlightFou ? 1F : 0.5F));
        } else
        {
            highlightOne = false;
            highlightTwo = false;
            highlightThr = false;
            highlightFou = false;
        }
    }

    public void mouseDown(int mouseX, int mouseY, float gridScale)
    {
        if ((highlightOne || highlightTwo || highlightThr || highlightFou) && expanded)
            if (highlightOne)
                dragging = 1;
            else if (highlightTwo)
                dragging = 2;
            else if (highlightThr)
                dragging = 3;
            else if (highlightFou)
                dragging = 4;
            else dragging = 0;
        else dragging = 0;
    }

    public boolean clickDrag(int mouseX, int mouseY, float gridScale, boolean grid)
    {
        BigDecimal x = new BigDecimal((mouseX - 4) / gridScale);
        BigDecimal y = new BigDecimal((mouseY - 4) / gridScale);

        if (grid)
        {
            x = x.setScale(1, BigDecimal.ROUND_HALF_DOWN);
            y = y.setScale(1, BigDecimal.ROUND_HALF_DOWN);
        }

        switch (dragging)
        {
            case 1:
                v1.setX(x.floatValue());
                v1.setY(y.floatValue());
                return true;
            case 2:
                v2.setX(x.floatValue());
                v2.setY(y.floatValue());
                return true;
            case 3:
                v3.setX(x.floatValue());
                v3.setY(y.floatValue());
                return true;
            case 4:
                v4.setX(x.floatValue());
                v4.setY(y.floatValue());
                return true;
            default:
                return false;
        }
    }

    public void mouseRelease()
    {

    }

    public int drawListElement(int mouseX, int mouseY, int width, DrawingHelper helper)
    {
        int height = expanded ? 30 : 15;
        helper
                .drawShape(new Quad(1, 0, width - 2, height).setColor(Color.BLACK).setAlpha(isMouseHovering(mouseX, mouseY, width) ? 0.75F : 0.25F))
                .drawShape(new Quad(0, 1, 1, height - 2).setColor(Color.BLACK).setAlpha(isMouseHovering(mouseX, mouseY, width) ? 0.75F : 0.25F))
                .drawShape(new Quad(width - 1, 1, 1, height - 2).setColor(Color.BLACK).setAlpha(isMouseHovering(mouseX, mouseY, width) ? 0.75F : 0.25F));
        helper
                .drawShape(new Quad(1, 1, width - 2, 1))
                .drawShape(new Quad(1, height - 2, width - 2, 1).setColor(Color.LT_GREY))
                .drawShape(new Quad(1, 2, 1, height - 4))
                .drawShape(new Quad(width - 2, 2, 1, height - 4).setColor(Color.LT_GREY));

        if (expanded)
        {
            final float V_Y = 18, T_W = 8, D_H_O = 3.5F, D_S = 3;

            final float ONE_SIZE = D_S + (highlightOne ? 2 : 0);
            final float TWO_SIZE = D_S + (highlightTwo ? 2 : 0);
            final float THR_SIZE = D_S + (highlightThr ? 2 : 0);
            final float FOU_SIZE = D_S + (highlightFou ? 2 : 0);

            helper.drawShape(new Quad(2, 14, width - 4, 1));
            helper
                    .drawText("V1", width / 5 * 1 - (int) T_W, (int) V_Y).drawShape(new Quad(width / 5 * 1 + T_W - (ONE_SIZE / 2), V_Y + D_H_O - (ONE_SIZE / 2), ONE_SIZE, ONE_SIZE))
                    .drawText("V2", width / 5 * 2 - (int) T_W, (int) V_Y).drawShape(new Quad(width / 5 * 2 + T_W - (TWO_SIZE / 2), V_Y + D_H_O - (TWO_SIZE / 2), TWO_SIZE, TWO_SIZE))
                    .drawText("V3", width / 5 * 3 - (int) T_W, (int) V_Y).drawShape(new Quad(width / 5 * 3 + T_W - (THR_SIZE / 2), V_Y + D_H_O - (THR_SIZE / 2), THR_SIZE, THR_SIZE))
                    .drawText("V4", width / 5 * 4 - (int) T_W, (int) V_Y).drawShape(new Quad(width / 5 * 4 + T_W - (FOU_SIZE / 2), V_Y + D_H_O - (FOU_SIZE / 2), FOU_SIZE, FOU_SIZE));
        }

        return height;
    }

    public boolean isMouseHovering(int mouseX, int mouseY, int width)
    {
        return isMouseHoveringArea(mouseX, mouseY, 0, 0, width, expanded ? 30 : 15);//mouseX >= 0 && mouseY >= 0 && mouseX < width && mouseY < (expanded ? 30 : 15);
    }

    private boolean isMouseHoveringArea(int mouseX, int mouseY, int xPos, int yPos, int width, int height)
    {
        return mouseX >= xPos && mouseY >= yPos && mouseX < xPos + width && mouseY < yPos + height;
    }

    public GuiListQuad setExpandedList(boolean expanded)
    {
        this.expanded = expanded;
        return this;
    }

    public boolean isExpanded()
    {
        return expanded;
    }
*/
}
