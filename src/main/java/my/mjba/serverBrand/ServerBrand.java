package my.mjba.serverBrand;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public final class ServerBrand extends JavaPlugin implements Listener {
    private String channel;
    private static Field playerChannelsField;
    private String customBrand;

    @Override
    public void onEnable() {
        // Save default config
        saveDefaultConfig();
        
        // Determine channel based on version
        try {
            Class.forName("org.bukkit.entity.Dolphin");
            channel = "minecraft:brand";
        } catch (ClassNotFoundException ignored) {
            channel = "MC|Brand";
        }
        
        // Get brand from config and convert & to §
        customBrand = getConfig().getString("brand", "Custom Server").replace("&", "§") + "§r";
        
        // Register channel using reflection to bypass restrictions
        try {
            Method registerMethod = this.getServer().getMessenger().getClass().getDeclaredMethod("addToOutgoing", Plugin.class, String.class);
            registerMethod.setAccessible(true);
            registerMethod.invoke(this.getServer().getMessenger(), this, channel);
        } catch (ReflectiveOperationException e) {
            getLogger().severe("Failed to register plugin message channel!");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Register events
        getServer().getPluginManager().registerEvents(this, this);
        
        getLogger().info("ServerBrand enabled! Using channel: " + channel);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (playerChannelsField == null) {
            try {
                playerChannelsField = event.getPlayer().getClass().getDeclaredField("channels");
                playerChannelsField.setAccessible(true);
            } catch (ReflectiveOperationException e) {
                getLogger().severe("Failed to access player channels!");
                e.printStackTrace();
                getServer().getPluginManager().disablePlugin(this);
                return;
            }
        }

        try {
            // Add our channel to player's channels
            @SuppressWarnings("unchecked")
            Set<String> channels = (Set<String>) playerChannelsField.get(event.getPlayer());
            channels.add(channel);
            
            // Update brand for the player
            updateBrand(event.getPlayer());
        } catch (ReflectiveOperationException e) {
            getLogger().severe("Failed to add channel to player!");
            e.printStackTrace();
        }
    }

    private void updateBrand(Player player) {
        ByteBuf buf = Unpooled.buffer();
        writeString(customBrand, buf);
        player.sendPluginMessage(this, channel, toArray(buf));
        buf.release();
    }

    private void writeString(String s, ByteBuf buf) {
        byte[] bytes = s.getBytes();
        writeVarInt(bytes.length, buf);
        buf.writeBytes(bytes);
    }

    private void writeVarInt(int value, ByteBuf buf) {
        while (true) {
            if ((value & ~0x7F) == 0) {
                buf.writeByte(value);
                return;
            }
            buf.writeByte((value & 0x7F) | 0x80);
            value >>>= 7;
        }
    }

    private byte[] toArray(ByteBuf buf) {
        byte[] bytes = new byte[buf.readableBytes()];
        buf.getBytes(buf.readerIndex(), bytes);
        return bytes;
    }

    public void updateAllPlayers() {
        Bukkit.getOnlinePlayers().forEach(this::updateBrand);
    }
}
