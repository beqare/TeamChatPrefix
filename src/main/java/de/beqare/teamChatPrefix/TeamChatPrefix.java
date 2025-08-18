package de.beqare.teamChatPrefix;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.CommandExecutor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class TeamChatPrefix extends JavaPlugin implements Listener, CommandExecutor {

    private String chatFormat;
    private boolean useTeamColor;
    private ChatColor defaultColor;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();
        getServer().getPluginManager().registerEvents(this, this);
        Objects.requireNonNull(getCommand("tcp")).setExecutor(this);
        getLogger().info("TeamChatPrefix enabled");
    }

    private void loadConfig() {
        reloadConfig();
        FileConfiguration config = getConfig();

        config.addDefault("format", "%teamcolor%%prefix%%playername%%suffix%§8 » §7%message%");
        config.addDefault("use-team-color", true);
        config.addDefault("default-color", "§f");
        config.options().copyDefaults(true);
        saveConfig();

        chatFormat = Objects.requireNonNull(config.getString("format"));
        useTeamColor = config.getBoolean("use-team-color");
        defaultColor = ChatColor.getByChar(Objects.requireNonNull(config.getString("default-color")).replace("§", "").charAt(0));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command cmd, @NotNull String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("tcp")) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("teamchatprefix.reload") && !sender.isOp()) {
                    sender.sendMessage(ChatColor.RED + "You don't have permission to reload the config!");
                    return true;
                }

                try {
                    loadConfig();
                    sender.sendMessage(ChatColor.GREEN + "TeamChatPrefix config reloaded successfully!");
                } catch (Exception e) {
                    sender.sendMessage(ChatColor.RED + "Error while reloading config: " + e.getMessage());
                    getLogger().severe("Error reloading config: " + e.getMessage());
                }
                return true;
            }
            sender.sendMessage(ChatColor.YELLOW + "Usage: /tcp reload");
            return true;
        }
        return false;
    }

    @EventHandler
    public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        Team team = player.getScoreboard().getEntryTeam(player.getName());

        String teamName = team != null ? team.getName() : "";
        String prefix = team != null ? team.getPrefix() : "";
        String suffix = team != null ? team.getSuffix() : "";
        ChatColor color = (useTeamColor && team != null) ? team.getColor() : defaultColor;

        String formattedMessage = chatFormat
                .replace("%playername%", player.getName())
                .replace("%team%", teamName)
                .replace("%prefix%", prefix)
                .replace("%suffix%", suffix)
                .replace("%teamcolor%", color.toString())
                .replace("%message%", event.getMessage());

        event.setFormat(formattedMessage);
    }

    @Override
    public void onDisable() {
        getLogger().info("TeamChatPrefix disabled");
    }
}