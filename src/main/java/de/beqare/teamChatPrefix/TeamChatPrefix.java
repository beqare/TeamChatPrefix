package de.beqare.teamchatprefix;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public final class TeamChatPrefix extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getLogger().info("TeamChatPrefix enabled!");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        getLogger().info("TeamChatPrefix disabled!");
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Team team = player.getScoreboard().getEntryTeam(player.getName());

        String prefix = "";
        ChatColor color = ChatColor.WHITE;

        if (team != null) {
            prefix = team.getPrefix();
            if (team.getColor() != null) {
                color = team.getColor();
            }
        }

        String format = color + prefix + "%1$s" + ChatColor.DARK_GRAY + " » " + ChatColor.GRAY + "%2$s";

        event.setFormat(format);
    }
}
