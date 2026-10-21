package mods.trnightmareegoautoclick.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.joml.Vector2f;
import org.joml.Vector2i;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class EgoMinigameAutoClicker {
    
    // Actual class names from trnightmare 1.0.5.2.2.5
    private static final String EGO_SCREEN_CLASS_NAME = "com.github.hvnbael.trnightmare.client.screen.EgoManagementScreen";
    private static final String CONVERSATION_DOT_CLASS_NAME = "com.github.hvnbael.trnightmare.client.screen.EgoManagementScreen$ConversationDot";
    private static final String CONVERSATION_DOT_TYPE_CLASS_NAME = "com.github.hvnbael.trnightmare.client.screen.EgoManagementScreen$ConversationDotType";
    private static final String DIRECTIVE_OVERLAY_CLASS_NAME = "com.github.hvnbael.trnightmare.client.screen.EgoManagementScreen$DirectiveOverlay";
    
    // Node types we want to auto-click (gold and silver are the rare/reward dots)
    private static final String GOLD_TYPE_NAME = "GOLD";
    private static final String SILVER_TYPE_NAME = "SILVER";
    
    private static boolean initialized = false;
    private static Class<?> egoScreenClass;
    private static Class<?> conversationDotClass;
    private static Class<?> conversationDotTypeClass;
    private static Class<?> directiveOverlayClass;
    
    // Fields in EgoManagementScreen
    private static Field conversationDotsField;        // List<ConversationDot> conversationDots
    private static Field directiveOverlayField;        // DirectiveOverlay directiveOverlay
    private static Field blueDotClicksField;           // int blueDotClicks
    private static Field goldDotClicksField;           // int goldDotClicks
    private static Field silverDotClicksField;         // int silverDotClicks
    private static Field converseBoardXField;          // Not a field, computed via method
    private static Field converseBoardYField;
    
    // Fields in ConversationDot
    private static Field dotTypeField;                 // ConversationDotType type
    private static Field dotXField;                    // float x (or int x)
    private static Field dotYField;                    // float y (or int y)
    private static Field dotRadiusField;               // float radius (or int)
    private static Field dotExpireAtField;             // long expireAt
    private static Field dotHazardousField;            // boolean hazardous (might be on ConversationDotType)
    
    // Methods
    private static Method submitConverseDirectiveMethod; // boolean submitConverseDirective(boolean)
    private static Method converseBoardXMethod;        // int converseBoardX()
    private static Method converseBoardYMethod;        // int converseBoardY()
    private static Method converseBoardWidthMethod;    // int converseBoardWidth()
    private static Method converseBoardHeightMethod;   // int converseBoardHeight()
    private static Method encodeConverseActionMethod;  // static int encodeConverseAction(int, int, int, int, boolean)
    
    // Click cooldown to avoid spamming
    private static long lastClickTime = 0;
    private static final long CLICK_COOLDOWN_MS = 50; // Faster for minigame
    
    // Track which dots we've already clicked
    private static long lastClickedDotId = -1;
    
    public static boolean isEgoConverseScreen(Screen screen) {
        if (!initialized) {
            initializeReflection();
        }
        if (egoScreenClass == null) return false;
        if (!egoScreenClass.isInstance(screen)) return false;
        
        // Also check that the CONVERSE overlay is active
        try {
            Object overlay = directiveOverlayField.get(screen);
            Object converseOverlay = getConverseOverlayEnum();
            return converseOverlay != null && converseOverlay.equals(overlay);
        } catch (Exception e) {
            return false;
        }
    }
    
    private static Object getConverseOverlayEnum() {
        if (directiveOverlayClass == null) return null;
        try {
            // DirectiveOverlay.CONVERSE
            Field converseField = directiveOverlayClass.getDeclaredField("CONVERSE");
            converseField.setAccessible(true);
            return converseField.get(null);
        } catch (Exception e) {
            return null;
        }
    }
    
    private static void initializeReflection() {
        try {
            egoScreenClass = Class.forName(EGO_SCREEN_CLASS_NAME);
            conversationDotClass = Class.forName(CONVERSATION_DOT_CLASS_NAME);
            conversationDotTypeClass = Class.forName(CONVERSATION_DOT_TYPE_CLASS_NAME);
            directiveOverlayClass = Class.forName(DIRECTIVE_OVERLAY_CLASS_NAME);
            
            // Find fields in EgoManagementScreen
            for (Field f : egoScreenClass.getDeclaredFields()) {
                f.setAccessible(true);
                String fname = f.getName();
                Class<?> ftype = f.getType();
                
                if (List.class.isAssignableFrom(ftype) && fname.equals("conversationDots")) {
                    conversationDotsField = f;
                } else if (directiveOverlayClass.isAssignableFrom(ftype) && fname.equals("directiveOverlay")) {
                    directiveOverlayField = f;
                } else if (ftype == int.class) {
                    if (fname.equals("blueDotClicks")) blueDotClicksField = f;
                    else if (fname.equals("goldDotClicks")) goldDotClicksField = f;
                    else if (fname.equals("silverDotClicks")) silverDotClicksField = f;
                }
            }
            
            // Find methods in EgoManagementScreen
            for (Method m : egoScreenClass.getDeclaredMethods()) {
                m.setAccessible(true);
                String mname = m.getName();
                if (mname.equals("converseBoardX") && m.getParameterCount() == 0) {
                    converseBoardXMethod = m;
                } else if (mname.equals("converseBoardY") && m.getParameterCount() == 0) {
                    converseBoardYMethod = m;
                } else if (mname.equals("converseBoardWidth") && m.getParameterCount() == 0) {
                    converseBoardWidthMethod = m;
                } else if (mname.equals("converseBoardHeight") && m.getParameterCount() == 0) {
                    converseBoardHeightMethod = m;
                } else if (mname.equals("submitConverseDirective") && m.getParameterCount() == 1) {
                    submitConverseDirectiveMethod = m;
                }
            }
            
            // Find fields in ConversationDot
            for (Field f : conversationDotClass.getDeclaredFields()) {
                f.setAccessible(true);
                String fname = f.getName();
                Class<?> ftype = f.getType();
                
                if (conversationDotTypeClass.isAssignableFrom(ftype) && fname.equals("type")) {
                    dotTypeField = f;
                } else if ((ftype == float.class || ftype == int.class || ftype == double.class) && fname.equals("x")) {
                    dotXField = f;
                } else if ((ftype == float.class || ftype == int.class || ftype == double.class) && fname.equals("y")) {
                    dotYField = f;
                } else if ((ftype == float.class || ftype == int.class || ftype == double.class) && 
                          (fname.equals("radius") || fname.equals("size"))) {
                    dotRadiusField = f;
                } else if (ftype == long.class && fname.equals("expireAt")) {
                    dotExpireAtField = f;
                }
            }
            
            // Find hazardous field on ConversationDotType
            for (Field f : conversationDotTypeClass.getDeclaredFields()) {
                f.setAccessible(true);
                if (f.getType() == boolean.class && f.getName().equals("hazardous")) {
                    dotHazardousField = f;
                    break;
                }
            }
            
            // Find encodeConverseAction in EgoManagementMenu
            try {
                Class<?> menuClass = Class.forName("com.github.hvnbael.trnightmare.menu.EgoManagementMenu");
                for (Method m : menuClass.getDeclaredMethods()) {
                    if (m.getName().equals("encodeConverseAction") && m.getParameterCount() == 5) {
                        m.setAccessible(true);
                        encodeConverseActionMethod = m;
                        break;
                    }
                }
            } catch (ClassNotFoundException ignored) {}
            
        } catch (ClassNotFoundException e) {
            egoScreenClass = null;
        }
        initialized = true;
    }
    
    public static void tick(Minecraft mc, Screen screen) {
        if (!isEgoConverseScreen(screen) || conversationDotsField == null) return;
        
        long now = System.currentTimeMillis();
        if (now - lastClickTime < CLICK_COOLDOWN_MS) return;
        
        try {
            @SuppressWarnings("unchecked")
            List<Object> dots = (List<Object>) conversationDotsField.get(screen);
            
            if (dots == null || dots.isEmpty()) return;
            
            // Find gold/silver dots that are clickable (not expired, not hazardous)
            for (Object dot : dots) {
                if (isTargetDot(dot, now)) {
                    clickDot(screen, dot, mc);
                    lastClickTime = now;
                    break; // Click one per tick
                }
            }
        } catch (Exception e) {
            // Silently ignore reflection errors
        }
    }
    
    private static boolean isTargetDot(Object dot, long now) {
        if (dotTypeField == null || dotExpireAtField == null) return false;
        
        try {
            // Check if dot is expired
            long expireAt = dotExpireAtField.getLong(dot);
            if (expireAt <= now) return false; // Already expired
            
            // Check type
            Object type = dotTypeField.get(dot);
            if (type == null) return false;
            
            String typeName = type.toString().toUpperCase();
            boolean isGold = typeName.contains(GOLD_TYPE_NAME);
            boolean isSilver = typeName.contains(SILVER_TYPE_NAME);
            
            if (!isGold && !isSilver) return false;
            
            // Check if hazardous (red dots are hazardous)
            if (dotHazardousField != null) {
                boolean hazardous = dotHazardousField.getBoolean(type);
                if (hazardous) return false; // Don't click hazardous dots
            }
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private static void clickDot(Screen screen, Object dot, Minecraft mc) {
        // Try to use the screen's submitConverseDirective method if we can determine the click result
        // But the actual click handling is done via mouse click simulation at the dot's position
        
        if (dotXField != null && dotYField != null) {
            try {
                float x = getFloatValue(dot, dotXField);
                float y = getFloatValue(dot, dotYField);
                
                // The dot positions are relative to the converse board
                // We need to convert to screen coordinates
                int boardX = 0, boardY = 0;
                if (converseBoardXMethod != null && converseBoardYMethod != null) {
                    boardX = (int) converseBoardXMethod.invoke(screen);
                    boardY = (int) converseBoardYMethod.invoke(screen);
                }
                
                int clickX = boardX + (int) x;
                int clickY = boardY + (int) y;
                
                // Simulate mouse click at the dot position
                mc.mouseHandler.handleButton(
                    mc.getWindow().getWindow(),
                    0, // GLFW_MOUSE_BUTTON_LEFT
                    1, // GLFW_PRESS
                    0
                );
                mc.mouseHandler.handleButton(
                    mc.getWindow().getWindow(),
                    0,
                    0, // GLFW_RELEASE
                    0
                );
                
                // Also try to call submitConverseDirective if available
                // This might not be needed as the mouse click should trigger it
                
            } catch (Exception e) {
                // Ignore
            }
        }
    }
    
    private static float getFloatValue(Object obj, Field field) throws IllegalAccessException {
        Object val = field.get(obj);
        if (val instanceof Number n) {
            return n.floatValue();
        }
        return 0f;
    }
    
    // Render overlay for debugging - shows detected dots
    public static void renderOverlay(GuiGraphics guiGraphics, Screen screen, int mouseX, int mouseY) {
        if (!isEgoConverseScreen(screen) || conversationDotsField == null) return;
        
        try {
            @SuppressWarnings("unchecked")
            List<Object> dots = (List<Object>) conversationDotsField.get(screen);
            
            if (dots == null) return;
            
            long now = System.currentTimeMillis();
            
            for (Object dot : dots) {
                if (dotXField != null && dotYField != null) {
                    float x = getFloatValue(dot, dotXField);
                    float y = getFloatValue(dot, dotYField);
                    float radius = 8; // Default dot radius
                    
                    if (dotRadiusField != null) {
                        radius = getFloatValue(dot, dotRadiusField);
                    }
                    
                    int boardX = 0, boardY = 0;
                    if (converseBoardXMethod != null && converseBoardYMethod != null) {
                        try {
                            boardX = (int) converseBoardXMethod.invoke(screen);
                            boardY = (int) converseBoardYMethod.invoke(screen);
                        } catch (Exception ignored) {}
                    }
                    
                    int screenX = boardX + (int) x;
                    int screenY = boardY + (int) y;
                    
                    boolean isTarget = isTargetDot(dot, now);
                    boolean isHazardous = false;
                    boolean isExpired = false;
                    
                    if (dotExpireAtField != null) {
                        try {
                            long expireAt = dotExpireAtField.getLong(dot);
                            isExpired = expireAt <= now;
                        } catch (Exception ignored) {}
                    }
                    
                    if (dotTypeField != null && dotHazardousField != null) {
                        try {
                            Object type = dotTypeField.get(dot);
                            if (type != null) {
                                isHazardous = dotHazardousField.getBoolean(type);
                            }
                        } catch (Exception ignored) {}
                    }
                    
                    int color;
                    if (isExpired) {
                        color = 0xFF888888; // Gray for expired
                    } else if (isHazardous) {
                        color = 0xFFFF0000; // Red for hazardous
                    } else if (isTarget) {
                        color = 0xFF00FF00; // Green for target (gold/silver)
                    } else {
                        color = 0xFF0000FF; // Blue for normal
                    }
                    
                    // Draw circle around dot
                    guiGraphics.pose().pushPose();
                    guiGraphics.pose().translate(0, 0, 300);
                    guiGraphics.drawCircle(screenX, screenY, (int)radius, color);
                    guiGraphics.pose().popPose();
                    
                    // Draw label
                    if (dotTypeField != null) {
                        try {
                            Object type = dotTypeField.get(dot);
                            guiGraphics.drawString(
                                Minecraft.getInstance().font,
                                Component.literal(type.toString() + (isExpired ? " (expired)" : "")),
                                screenX - 20, screenY - 20,
                                0xFFFFFF
                            );
                        } catch (Exception ignored) {}
                    }
                }
            }
        } catch (Exception ignored) {}
    }
}