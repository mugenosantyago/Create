package com.simibubi.create.foundation.ponder;

import net.createmod.catnip.gui.ScreenOpener;
import net.createmod.ponder.foundation.ui.PonderTagIndexScreen;
import net.minecraft.client.Minecraft;

public class PonderHelper {
    
    public static boolean openPonder() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return false;
        }
        
        // Open the Ponder index screen
        ScreenOpener.open(new PonderTagIndexScreen());
        return true;
    }
}
