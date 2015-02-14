package dk.mrspring.helper;

import com.mumfrey.liteloader.Tickable;
import com.mumfrey.liteloader.core.LiteLoader;
import dk.mrspring.helper.gui.screen.GuiScreenIconMaker;
import dk.mrspring.llcore.LLCore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

import java.io.File;

/**
 * Created by Konrad on 30-01-2015.
 */
public class LiteModHelper implements Tickable
{
    KeyBinding openIconMaker = new KeyBinding("keys.mrspring.open_icon_maker", Keyboard.KEY_F, "keys");

    public static LLCore coreHelper;

    @Override
    public void onTick(Minecraft minecraft, float partialTicks, boolean inGame, boolean clock)
    {
        if (openIconMaker.isPressed())
            minecraft.displayGuiScreen(new GuiScreenIconMaker());
    }

    @Override
    public String getVersion()
    {
        return "1.0.0";
    }

    @Override
    public void init(File configPath)
    {
        LiteLoader.getInput().registerKeyBinding(openIconMaker);
        coreHelper = new LLCore("llhelper");

        coreHelper.loadIcon(new ResourceLocation("llhelper", "bin"));
        coreHelper.loadIcon(new ResourceLocation("llhelper", "plus"));
        coreHelper.loadIcon(new ResourceLocation("llhelper", "copy"));
        coreHelper.loadIcon(new ResourceLocation("llhelper", "paste"));
    }

    @Override
    public void upgradeSettings(String version, File configPath, File oldConfigPath)
    {

    }

    @Override
    public String getName()
    {
        return "MrSprings Mod Maker Helper Thingy";
    }
}
