package mods.trnightmareegoautoclick;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import mods.trnightmareegoautoclick.client.EgoMinigameAutoClicker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("trnightmareegoautoclick")
public class TrNightmareEgoAutoClick {
    private static final Logger LOGGER = LoggerFactory.getLogger("TrNightmareEgoAutoClick");

    public TrNightmareEgoAutoClick(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("TrNightmare Ego Auto Clicker loaded!");
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