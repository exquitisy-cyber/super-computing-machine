package mods.trnightmareegoautoclick;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import mods.trnightmareegoautoclick.client.EgoMinigameAutoClicker;

@Mod("trnightmareegoautoclick")
public class TrNightmareEgoAutoClick {
    public TrNightmareEgoAutoClick(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::onClientTick);
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Screen screen = mc.screen;
        
        if (screen != null && EgoMinigameAutoClicker.isEgoConverseScreen(screen)) {
            EgoMinigameAutoClicker.tick(mc, screen);
        }
    }
}