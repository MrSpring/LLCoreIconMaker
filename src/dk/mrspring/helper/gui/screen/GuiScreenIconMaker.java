package dk.mrspring.helper.gui.screen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mumfrey.liteloader.gl.GLClippingPlanes;
import dk.mrspring.helper.LiteModHelper;
import dk.mrspring.helper.gui.GuiQuadEditor;
import dk.mrspring.helper.gui.GuiQuadListElement;
import dk.mrspring.llcore.*;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Konrad on 30-01-2015.
 */
public class GuiScreenIconMaker extends GuiScreen
{
    List<GuiQuadEditor> quads;
    GuiQuadList list;

    public GuiScreenIconMaker()
    {
        quads = new ArrayList<GuiQuadEditor>();
        quads.add(new GuiQuadEditor(new Quad(0.4F, 0.4F, 0.2F, 0.2F)));
        list = new GuiQuadList(width - Math.min(height - 10, width - 10 - 40) - 15);
    }

    @Override
    public void initGui()
    {
        super.initGui();
        list.setWidth(width - Math.min(height - 10, width - 10 - 40) - 15);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
        for (GuiQuadEditor quad : quads)
            if (quad.isEditing())
                quad.mouseDrag(mouseX - 5, mouseY - 5, Math.min(height - 10, width - 10 - 40), Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_RCONTROL), new GuiQuadEditor.GridScaler()
                {
                    @Override
                    public BigDecimal getX(int mouseX, float gridScale)
                    {
                        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
                            return new BigDecimal((mouseX) / gridScale).setScale(1, BigDecimal.ROUND_HALF_DOWN);
                        else if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
                        {
                            BigDecimal doub = new BigDecimal((mouseX / gridScale) * 2).setScale(1, BigDecimal.ROUND_HALF_DOWN);
                            return new BigDecimal(doub.floatValue() / 2);
                        }
                        return new BigDecimal(mouseX / gridScale);
                    }

                    @Override
                    public BigDecimal getY(int mouseY, float gridScale)
                    {
                        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
                            return new BigDecimal((mouseY) / gridScale).setScale(1, BigDecimal.ROUND_HALF_DOWN);
                        else if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL) || Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
                        {
                            BigDecimal doub = new BigDecimal((mouseY / gridScale) * 2).setScale(1, BigDecimal.ROUND_HALF_DOWN);
                            return new BigDecimal(doub.floatValue() / 2);
                        }
                        return new BigDecimal(mouseY / gridScale);
                    }
                });
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
        for (GuiQuadEditor quad : quads)
            if (quad.isEditing())
                quad.mouseDown(mouseX - 5, mouseY - 5, Math.min(height - 10, width - 10 - 40));
        if (mouseX > Math.min(height - 10, width - 10 - 40) + 10)
            list.mouseDown(mouseX - Math.min(height - 10, width - 10 - 40) - 10, mouseY - 5);
        super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        for (GuiQuadEditor quad : quads)
            if (quad.isEditing())
                quad.mouseReleased();
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    public void handleMouseInput() throws IOException
    {
        list.handleMouseInput();
        super.handleMouseInput();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        float gridSize = Math.min(height - 10, width - 10 - 40);

        DrawingHelper helper = LiteModHelper.coreHelper.getDrawingHelper();

        helper.drawShape(new Quad(5, 5, gridSize + 1, gridSize + 1).setColor(Color.BLACK).setAlpha(0.25F));
        for (int i = 0; i < 11; i++)
            helper.drawShape(new Quad(5 + (gridSize / 10 * i), 5, 1, gridSize + 1).setColor(Color.WHITE).setAlpha(0.5F));
        for (int i = 0; i < 11; i++)
            helper.drawShape(new Quad(5, 5 + (gridSize / 10 * i), gridSize + 1, 1).setColor(Color.WHITE).setAlpha(0.5F));

        GL11.glPushMatrix();

        GL11.glTranslatef(5, 5, 0);
        int relativeMouseX = mouseX - 5, relativeMouseY = mouseY - 5;
        for (GuiQuadEditor quad : quads)
            quad.draw(relativeMouseX, relativeMouseY, gridSize, helper);

        GL11.glPopMatrix();

        GL11.glPushMatrix();
        GL11.glTranslatef(gridSize + 10, 5, 0);
        relativeMouseX = mouseX - (int) gridSize - 10;
        relativeMouseY = mouseY - 5;
        list.draw(relativeMouseX, relativeMouseY);
        GL11.glPopMatrix();
    }

    public class GuiQuadList
    {
        List<GuiQuadListElement> list = new ArrayList<GuiQuadListElement>();
        public int width, scrollHeight = 0, prevHeight = 0;

        public GuiQuadList(int width)
        {
            for (GuiQuadEditor quad : quads)
                list.add(new GuiQuadListElement("Quad: #" + (list.size() + 1), quad));
            this.width = width - 4;
        }

        public void setWidth(int width)
        {
            this.width = width;
        }

        int getHeight()
        {
            return height - 40;
        }

        public int getWidth()
        {
            if (prevHeight > getHeight())
                return width - 6;
            else return width;
        }

        public void draw(int mouseX, int mouseY)
        {
            DrawingHelper helper = LiteModHelper.coreHelper.getDrawingHelper();

            helper
                    .drawShape(new Quad(1, 1, width + 2, getHeight() - 2).setColor(Color.BLACK).setAlpha(0.25F))
                    .drawShape(new Quad(0, 1, 1, getHeight() - 2).setColor(Color.BLACK).setAlpha(0.5F))
                    .drawShape(new Quad(width + 3, 1, 1, getHeight() - 2).setColor(Color.BLACK).setAlpha(0.5F))
                    .drawShape(new Quad(1, 0, width + 2, 1).setColor(Color.BLACK).setAlpha(0.5F))
                    .drawShape(new Quad(1, getHeight() - 1, width + 2, 1).setColor(Color.BLACK).setAlpha(0.5F));

            GLClippingPlanes.glEnableClipping(0, width + 4, 1, getHeight() - 1);

            GL11.glPushMatrix();

            GL11.glTranslatef(prevHeight > getHeight() ? 8 : 2, -scrollHeight + 2, 0);

            int relativeMouseY = mouseY + scrollHeight - 2;
            int totalHeight = 0;
            for (int i = 0; i < list.size(); i++)
            {
                GuiQuadListElement element = list.get(i);
                element.expanded = quads.get(i).isEditing();
                int elementHeight = element.draw(mouseX, relativeMouseY, this, helper);
                if (mouseY < getHeight())
                    relativeMouseY -= elementHeight + 5;
                GL11.glTranslatef(0, elementHeight + 5, 0);
                totalHeight += elementHeight + 5;
            }
            prevHeight = totalHeight;

            GL11.glPopMatrix();

            if (prevHeight > getHeight())
                this.drawScrollBar(helper);

            GLClippingPlanes.glDisableClipping();

            GL11.glPushMatrix();

            GL11.glTranslatef(0, getHeight() + 2, 0);
            final float BUTTON_SIZE = 30;
            boolean alpha = mouseY > getHeight() && mouseY < getHeight() + BUTTON_SIZE && mouseX > 0 && mouseX < BUTTON_SIZE;

            helper.drawButtonThingy(BUTTON_SIZE, alpha, LiteModHelper.coreHelper.getIcon("plus"));

            GL11.glTranslatef(BUTTON_SIZE + 2, 0, 0);
            alpha = mouseY > getHeight() && mouseY < getHeight() + BUTTON_SIZE && mouseX > BUTTON_SIZE + 2 && mouseX < BUTTON_SIZE * 2 + 2;

            helper.drawButtonThingy(BUTTON_SIZE, alpha, LiteModHelper.coreHelper.getIcon("bin"));

            GL11.glTranslatef(BUTTON_SIZE + 2, 0, 0);
            alpha = mouseY > getHeight() && mouseY < getHeight() + BUTTON_SIZE && mouseX > BUTTON_SIZE * 2 + 2 && mouseX < BUTTON_SIZE * 3 + 2;// TODO: Name quads "Quad #n" when creating new

            helper.drawButtonThingy(BUTTON_SIZE, alpha, LiteModHelper.coreHelper.getIcon("copy"));// TODO: Select quad after creating a new one

            GL11.glTranslatef(BUTTON_SIZE + 2, 0, 0);
            alpha = mouseY > getHeight() && mouseY < getHeight() + BUTTON_SIZE && mouseX > BUTTON_SIZE * 3 + 2 && mouseX < BUTTON_SIZE * 4 + 2;

            helper.drawButtonThingy(BUTTON_SIZE, alpha, LiteModHelper.coreHelper.getIcon("paste"));

            GL11.glPopMatrix();
        }

        private void drawScrollBar(DrawingHelper helper)
        {
            float scrollBarYRange = (getHeight() - 46);
            float maxScrollHeight = getMaxScrollHeight();
            float scrollProgress = (float) this.scrollHeight / maxScrollHeight;
            float scrollBarY = scrollBarYRange * scrollProgress;
            helper.drawShape(new Quad(4, 3 + scrollBarY + 1, 2, 40).setColor(Color.DK_GREY));
            helper.drawShape(new Quad(3, 3 + scrollBarY, 2, 40));
        }

        public void mouseDown(int mouseX, int mouseY)
        {
            if (mouseY < getHeight())
            {
                int relativeMouseY = mouseY + scrollHeight - 2;
                int clicked = -1;
                for (int i = 0; i < list.size(); i++)
                {
                    GuiQuadListElement element = list.get(i);
                    if (element.isMouseHivering(mouseX, relativeMouseY, this))
                    {
                        clicked = i;
                        break;
                    }
                    relativeMouseY -= element.getHeight() + 5;
                }

                for (int i = 0; i < list.size(); i++)
                    list.get(i).setEditing(clicked == i);
            } else
            {
                final float BUTTON_SIZE = 30;

                if (mouseX > 0 && mouseX < BUTTON_SIZE)
                {
                    quads.add(new GuiQuadEditor(new Quad(0.4F, 0.4F, 0.2F, 0.2F)));
                    for (GuiQuadListElement element : list)
                        element.setEditing(false);
                    this.list.add(new GuiQuadListElement("Quad: #" + (list.size() + 1), quads.get(quads.size() - 1)).setEditing(true));
                } else if (mouseX > BUTTON_SIZE + 2 && mouseX < BUTTON_SIZE * 2 + 2)
                {
                    if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
                    {
                        quads.clear();
                        list.clear();
                    } else
                    {
                        for (int i = 0; i < list.size(); i++)
                        {
                            GuiQuadListElement element = list.get(i);
                            if (element.expanded)
                            {
                                quads.remove(element.editor);
                                list.remove(element);
                            }
                        }
                    }
                } else if (mouseX > BUTTON_SIZE * 2 + 4 && mouseX < BUTTON_SIZE * 3 + 2)
                {
                    List<Quad> editedQuads = new ArrayList<Quad>();
                    for (GuiQuadEditor editor : quads)
                        editedQuads.add(editor.toQuad());
                    Icon icon = new Icon(1, 1, editedQuads.toArray(new Quad[editedQuads.size()]));
                    String jsonCode = new GsonBuilder().setPrettyPrinting().create().toJson(icon);
                    setClipboardString(jsonCode);
                } else if (mouseX > BUTTON_SIZE * 3 + 4 && mouseX < BUTTON_SIZE * 4 + 2)
                {
                    String fromClipboard = getClipboardString();
                    if (fromClipboard != null)
                    {
                        Icon icon = new Gson().fromJson(fromClipboard, Icon.class);
                        if (icon != null)
                        {
                            quads.clear();
                            list.clear();
                            Shape[] fromIcon = icon.getShapes();
                            for (Shape shape : fromIcon)
                            {
                                GuiQuadEditor editor = new GuiQuadEditor(new Quad(shape.getVectors()));
                                quads.add(editor);
                                list.add(new GuiQuadListElement("Quad: #" + (list.size() + 1), editor));
                            }
                        }
                    }
                }
            }
        }

        public GuiQuadEditor getQuadSelectedEditor()
        {
            for (GuiQuadEditor editor : quads)
                if (editor.isEditing())
                    return editor;
            return null;
        }

        private int getMaxScrollHeight()
        {
            return prevHeight - getHeight() - 1;
        }

        public void addScroll(int amount)
        {
            int maxScrollHeight = getMaxScrollHeight(), minScrollHeight = 0, scrollHeightAfterAddition = this.scrollHeight + amount;

            if (scrollHeightAfterAddition > maxScrollHeight)
                scrollHeightAfterAddition = maxScrollHeight;
            else if (scrollHeightAfterAddition < minScrollHeight)
                scrollHeightAfterAddition = minScrollHeight;

            this.scrollHeight = scrollHeightAfterAddition;
        }

        public void handleMouseInput()
        {
            int mouseWheel = Mouse.getDWheel();
            mouseWheel /= 4;
            if (mouseWheel != 0)
                this.addScroll(-mouseWheel);
        }
    }
}
