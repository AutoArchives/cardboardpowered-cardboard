package org.bukkit.craftbukkit.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JavaOps;

import me.isaiah.common.cmixin.IMixinMinecraftServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.chat.ChatVersion;
import net.md_5.bungee.chat.VersionedComponentSerializer;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.PlainTextContents.LiteralContents;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.util.ExtraCodecs;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.CraftServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class CraftChatMessage {

    private static final Pattern LINK_PATTERN = Pattern.compile("((?:(?:https?):\\/\\/)?(?:[-\\w_\\.]{2,}\\.[a-z]{2,4}.*?(?=[\\.\\?!,;:]?(?:[" + String.valueOf(org.bukkit.ChatColor.COLOR_CHAR) + " \\n]|$))))");
    private static final Map<Character, ChatFormatting> formatMap;
    private static VersionedComponentSerializer bungeeSerializer;

    static {
        Builder<Character, ChatFormatting> builder = ImmutableMap.builder();
        for (ChatFormatting format : ChatFormatting.values()) builder.put(Character.toLowerCase(format.toString().charAt(1)), format);
        formatMap = builder.build();
        
        bungeeSerializer = VersionedComponentSerializer.forVersion(ChatVersion.V1_21_5);
    }

    public static ChatFormatting getColor(ChatColor color) {
        return formatMap.get(color.getChar());
    }

    public static ChatColor getColor(ChatFormatting format) {
        switch (format) {
            case AQUA:
                return ChatColor.AQUA;
            case BLACK:
                return ChatColor.BLACK;
            case BLUE:
                return ChatColor.BLUE;
            case BOLD:
                return ChatColor.BOLD;
            case DARK_AQUA:
                return ChatColor.DARK_AQUA;
            case DARK_BLUE:
                return ChatColor.DARK_BLUE;
            case DARK_GRAY:
                return ChatColor.DARK_GRAY;
            case DARK_GREEN:
                return ChatColor.DARK_GREEN;
            case DARK_PURPLE:
                return ChatColor.DARK_PURPLE;
            case DARK_RED:
                return ChatColor.DARK_RED;
            case GOLD:
                return ChatColor.GOLD;
            case GRAY:
                return ChatColor.GRAY;
            case GREEN:
                return ChatColor.GREEN;
            case ITALIC:
                return ChatColor.ITALIC;
            case LIGHT_PURPLE:
                return ChatColor.LIGHT_PURPLE;
            case RED:
                return ChatColor.RED;
            case RESET:
                return ChatColor.RESET;
            case STRIKETHROUGH:
                return ChatColor.STRIKETHROUGH;
            case UNDERLINE:
                return ChatColor.UNDERLINE;
            case WHITE:
                return ChatColor.WHITE;
            case YELLOW:
                return ChatColor.YELLOW;
            case OBFUSCATED:
                return ChatColor.MAGIC;
            default:
                return null;
        }
    }

    private static final class StringMessage {
        private static final Pattern INCREMENTAL_PATTERN = Pattern.compile("(" + String.valueOf(org.bukkit.ChatColor.COLOR_CHAR) + "[0-9a-fk-orx])|((?:(?:https?):\\/\\/)?(?:[-\\w_\\.]{2,}\\.[a-z]{2,4}.*?(?=[\\.\\?!,;:]?(?:[" + String.valueOf(org.bukkit.ChatColor.COLOR_CHAR) + " \\n]|$))))|(\\n)", Pattern.CASE_INSENSITIVE);
        private static final Pattern INCREMENTAL_PATTERN_KEEP_NEWLINES = Pattern.compile("(" + String.valueOf(org.bukkit.ChatColor.COLOR_CHAR) + "[0-9a-fk-orx])|((?:(?:https?):\\/\\/)?(?:[-\\w_\\.]{2,}\\.[a-z]{2,4}.*?(?=[\\.\\?!,;:]?(?:[" + String.valueOf(org.bukkit.ChatColor.COLOR_CHAR) + " ]|$))))", Pattern.CASE_INSENSITIVE);
        private static final Style RESET = Style.EMPTY;

        private final List<Component> list = new ArrayList<Component>();
        private Component currentChatComponent = Component.nullToEmpty("");
        private Style modifier = Style.EMPTY;
        private final Component[] output;
        private int currentIndex;
        private StringBuilder hex;
        private final String message;

        private StringMessage(String message, boolean keepNewlines) {
            this.message = message;
            if (message == null) {
                output = new Component[]{currentChatComponent};
                return;
            }
            list.add(currentChatComponent);

            Matcher matcher = (keepNewlines ? INCREMENTAL_PATTERN_KEEP_NEWLINES : INCREMENTAL_PATTERN).matcher(message);
            String match = null;
            boolean needsAdd = false;
            while (matcher.find()) {
                int groupId = 0;
                while ((match = matcher.group(++groupId)) == null) {/*NOOP*/}
                int index = matcher.start(groupId);
                if (index > currentIndex) {
                    needsAdd = false;
                    appendNewComponent(index);
                }
                switch (groupId) {
                case 1:
                    char c = match.toLowerCase(java.util.Locale.ENGLISH).charAt(1);
                    ChatFormatting format = formatMap.get(c);

                    if (c == 'x') {
                        hex = new StringBuilder("#");
                    } else if (hex != null) {
                        hex.append(c);

                        if (hex.length() == 7) {
                            modifier = RESET.withColor(TextColor.parseColor(hex.toString()).result().get());
                            hex = null;
                        }
                    } else if (format.isFormat() && format != ChatFormatting.RESET) {
                        switch (format) {
                        case BOLD:
                            modifier = modifier.withBold(Boolean.TRUE);
                            break;
                        case ITALIC:
                            modifier = modifier.withItalic(Boolean.TRUE);
                            break;
                        case STRIKETHROUGH:
                            modifier.strikethrough = Boolean.TRUE;
                            break;
                        case UNDERLINE:
                            // TODO BROKEN
                            break;
                        case OBFUSCATED:
                            modifier.obfuscated = Boolean.TRUE;
                            break;
                        default:
                            throw new AssertionError("Unexpected message format");
                        }
                    } else modifier = RESET.withColor(format);// Color resets formatting

                    needsAdd = true;
                    break;
                case 2:
                    if (!(match.startsWith("http://") || match.startsWith("https://"))) match = "http://" + match;
                    // modifier = modifier.withClickEvent(new ClickEvent(Action.OPEN_URL, match));
                    ExtraCodecs.UNTRUSTED_URI.parse(JavaOps.INSTANCE, match).ifSuccess(uri -> {
                        this.modifier = this.modifier.withClickEvent(new ClickEvent.OpenUrl(uri));
                    });
                    this.appendNewComponent(matcher.end(groupId));
                    this.modifier = this.modifier.withClickEvent(null);
                    
                    break;
                case 3:
                    if (needsAdd) appendNewComponent(index);
                    currentChatComponent = null;
                    break;
                }
                currentIndex = matcher.end(groupId);
            }
            if (currentIndex < message.length() || needsAdd) appendNewComponent(message.length());
            output = list.toArray(new Component[list.size()]);
        }

        private void appendNewComponent(int index) {
            Component addition = Component.literal(message.substring(currentIndex, index)).setStyle(modifier);
            currentIndex = index;
            if (currentChatComponent == null) {
                currentChatComponent = Component.nullToEmpty("");
                list.add(currentChatComponent);
            }
            currentChatComponent.getSiblings().add(addition);
        }

        private Component[] getOutput() {
            return output;
        }
    }

    public static Component wrapOrNull(String message) {
        return (message == null || message.isEmpty()) ? null : Component.nullToEmpty(message);
    }

    public static Component wrapOrEmpty(String message) {
        return (message == null) ? Component.nullToEmpty("") : Component.nullToEmpty(message);
    }

    public static Component fromStringOrNull(String message) {
        return fromStringOrNull(message, false);
    }

    public static Component fromStringOrNull(String message, boolean keepNewlines) {
        return (message == null || message.isEmpty()) ? null : fromString(message, keepNewlines)[0];
    }

    public static Component[] fromString(String message) {
        return fromString(message, false);
    }

    public static Component[] fromString(String message, boolean keepNewlines) {
        return new StringMessage(message, keepNewlines).getOutput();
    }

    public static String fromComponent(Component component) {
        return fromComponent(component, ChatFormatting.BLACK);
    }

    public static String toJSON(Component component) {
    	return ((IMixinMinecraftServer)CraftServer.server).IC$to_json(component);
    	
    	
        // return Text.Serialization.toJsonString(component);
    }
    
    public static ArrayList<Component> list(Component txt) {
        ArrayList<Component> arr = new ArrayList<>();
        if (!arr.contains(txt))
            arr.add( txt );
        for (Component tx : txt.getSiblings()) {
            arr.addAll( list(tx) );
        }
        return arr;
    }

    public static String fromComponent(Component component, ChatFormatting defaultColor) {
        if (component == null) return "";
        StringBuilder out = new StringBuilder();

       // IText it = (IText) component;

        for (Component c : list(component)) {
            Style modi = ((Component)c).getStyle();
            out.append(modi.getColor() == null ? defaultColor : modi.getColor());
            if (modi.isBold()) out.append(ChatFormatting.BOLD);
            if (modi.isItalic()) out.append(ChatFormatting.ITALIC);
            if (modi.isUnderlined()) out.append(ChatFormatting.UNDERLINE);
            if (modi.isStrikethrough()) out.append(ChatFormatting.STRIKETHROUGH);
            if (modi.isObfuscated()) out.append(ChatFormatting.OBFUSCATED);

            c.visit((x) -> {
                out.append(x);
                return Optional.empty();
            });
        }
        
        return out.toString();//.replaceFirst("^(" + defaultColor + ")*", "");
    }

    public static Component fixComponent(MutableComponent component) {
        Matcher matcher = LINK_PATTERN.matcher("");
        return fixComponent(component, matcher);
    }

    private static Component fixComponent(MutableComponent component, Matcher matcher) {
        LiteralContents text;
        String msg;
        if (component.getContents() instanceof LiteralContents && matcher.reset(msg = (text = (LiteralContents)component.getContents()).text()).find()) {
            matcher.reset();
            Style modifier = component.getStyle();
            ArrayList<Component> extras = new ArrayList<Component>();
            ArrayList<Component> extrasOld = new ArrayList<Component>(component.getSiblings());
            component = Component.empty();
            int pos = 0;
            while (matcher.find()) {
                Object match = matcher.group();
                if (!((String)match).startsWith("http://") && !((String)match).startsWith("https://")) {
                    match = "http://" + (String)match;
                }
                MutableComponent prev = Component.literal(msg.substring(pos, matcher.start()));
                prev.setStyle(modifier);
                extras.add(prev);
                MutableComponent link = Component.literal(matcher.group());
                
                
                Style linkModi = modifier; // .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, (String)match));
                
                link.setStyle(linkModi);
                
                ExtraCodecs.UNTRUSTED_URI.parse(JavaOps.INSTANCE, match).ifSuccess(uri -> {
                	Style linkModi1 = modifier.withClickEvent(new ClickEvent.OpenUrl(uri));
                	link.setStyle(linkModi1);
                });
                
                
                // link.setStyle(linkModi);
                extras.add(link);
                pos = matcher.end();
            }
            MutableComponent prev = Component.literal(msg.substring(pos));
            prev.setStyle(modifier);
            extras.add(prev);
            extras.addAll(extrasOld);
            for (Component c2 : extras) {
                component.append(c2);
            }
        }
        List<Component> extras = component.getSiblings();
        for (int i2 = 0; i2 < extras.size(); ++i2) {
            Component comp = extras.get(i2);
            if (comp.getStyle() == null || comp.getStyle().getClickEvent() != null) continue;
            extras.set(i2, CraftChatMessage.fixComponent(comp.copy(), matcher));
        }
        if (component.getContents() instanceof TranslatableContents) {
            Object[] subs = ((TranslatableContents)component.getContents()).getArgs();
            for (int i3 = 0; i3 < subs.length; ++i3) {
                Object comp = subs[i3];
                if (comp instanceof Component) {
                    Component c3 = (Component)comp;
                    if (c3.getStyle() == null || c3.getStyle().getClickEvent() != null) continue;
                    subs[i3] = CraftChatMessage.fixComponent(c3.copy(), matcher);
                    continue;
                }
                if (!(comp instanceof String) || !matcher.reset((String)comp).find()) continue;
                subs[i3] = CraftChatMessage.fixComponent(Component.literal((String)comp), matcher);
            }
        }
        return component;
    }

    private CraftChatMessage() {
    }

    // Paper start

    public static String trimMessage(String message, int maxLength) {
        if (message != null && message.length() > maxLength) {
            return message.substring(0, maxLength);
        } else {
            return message;
        }
    }

    public static String fromStringToJSON(String message) {
        return fromStringToJSON(message, false);
    }

    public static String fromStringToJSON(String message, boolean keepNewlines) {
        Component component = CraftChatMessage.fromString(message, keepNewlines)[0];
        return CraftChatMessage.toJSON(component);
    }

    public static String fromJSONOrStringToJSON(String message) {
        return fromJSONOrStringToJSON(message, false);
    }

    public static String fromJSONOrStringToJSON(String message, boolean keepNewlines) {
        return fromJSONOrStringToJSON(message, false, keepNewlines, Integer.MAX_VALUE, false);
    }

    public static String fromJSONOrStringOrNullToJSON(String message) {
        return fromJSONOrStringOrNullToJSON(message, false);
    }

    public static String fromJSONOrStringOrNullToJSON(String message, boolean keepNewlines) {
        return fromJSONOrStringToJSON(message, true, keepNewlines, Integer.MAX_VALUE, false);
    }

    public static Component fromJSONOrNull(String jsonMessage) {
        if (jsonMessage == null) return null;
        try {
            return fromJSON(jsonMessage); // Can return null
        } catch (JsonParseException ex) {
            return null;
        }
    }

    public static Component fromJSON(String jsonMessage) throws JsonParseException {
        return ((IMixinMinecraftServer)CraftServer.server).IC$from_json(jsonMessage);
    }

    public static String fromJSONOrStringToJSON(String message, boolean nullable, boolean keepNewlines, int maxLength, boolean checkJsonContentLength) {
        if (message == null) message = "";
        if (nullable && message.isEmpty()) return null;
        // If the input can be parsed as JSON, we use that:
        Component component = fromJSONOrNull(message);
        if (component != null) {
            if (checkJsonContentLength) {
                String content = fromComponent(component);
                String trimmedContent = trimMessage(content, maxLength);
                if (!content.equals(trimmedContent)) { // identity comparison is fine here
                    // Note: The resulting text has all non-plain text features stripped.
                    return fromStringToJSON(trimmedContent, keepNewlines);
                }
            }
            return message;
        } else {
            // Else we interpret the input as legacy text:
            message = trimMessage(message, maxLength);
            return fromStringToJSON(message, keepNewlines);
        }
    }

    public static Component[] fromString(String message, boolean keepNewlines, boolean plain) {
        return new StringMessage(message, keepNewlines/*, plain*/).getOutput();
    }

    public static String fromJSONComponent(String jsonMessage) {
        Component component = CraftChatMessage.fromJSONOrNull(jsonMessage);
        return CraftChatMessage.fromComponent(component);
    }

    public static Component fromJSONOrString(String message) {
        return CraftChatMessage.fromJSONOrString(message, false);
    }

    public static Component fromJSONOrString(String message, boolean keepNewlines) {
        return CraftChatMessage.fromJSONOrString(message, false, keepNewlines);
    }

    public static Component fromJSONOrString(String message, boolean nullable, boolean keepNewlines) {
        if (message == null) {
            message = "";
        }
        if (nullable && message.isEmpty()) {
            return null;
        }
        Component component = CraftChatMessage.fromJSONOrNull(message);
        if (component != null) {
            return component;
        }
        return CraftChatMessage.fromString(message, keepNewlines)[0];
    }

    // 1.20 API
    public static Optional<Component> fromStringOrOptional(String message) {
        return Optional.ofNullable(CraftChatMessage.fromStringOrNull(message));
    }

    public static Optional<Component> fromStringOrOptional(String message, boolean keepNewlines) {
        return Optional.ofNullable(CraftChatMessage.fromStringOrNull(message, keepNewlines));
    }

    public static Component bungeeToVanilla(BaseComponent... components) {
    	return fromJSON(bungeeToJson(components));
    }

    public static String bungeeToJson(BaseComponent... components) {
    	return bungeeSerializer.toString(components);
    }



}
