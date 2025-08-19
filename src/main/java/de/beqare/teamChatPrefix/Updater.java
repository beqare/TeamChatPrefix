package de.beqare.teamChatPrefix;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Updater {

    private final JavaPlugin plugin;
    private final String projectSlug;
    private String latestVersion = null;

    public Updater(JavaPlugin plugin, String projectSlug) {
        this.plugin = plugin;
        this.projectSlug = projectSlug;
    }

    public void checkForUpdates() {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                URL url = new URL("https://api.modrinth.com/v2/project/" + projectSlug + "/version");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "SpigotPlugin/" + plugin.getDescription().getVersion());
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);

                JsonArray versions = JsonParser.parseReader(new InputStreamReader(conn.getInputStream())).getAsJsonArray();

                if (!versions.isEmpty()) {
                    JsonElement latest = versions.get(0);
                    latestVersion = latest.getAsJsonObject().get("version_number").getAsString();
                    String currentVersion = plugin.getDescription().getVersion();

                    if (!latestVersion.equalsIgnoreCase(currentVersion)) {
                        plugin.getLogger().warning("A new version of " + plugin.getName() + " is available: " + latestVersion);
                        plugin.getLogger().warning("You are currently on: " + currentVersion);
                        plugin.getLogger().warning("Download the update here: https://modrinth.com/plugin/" + projectSlug);
                    } else {
                        plugin.getLogger().info("You are running the latest version (" + currentVersion + ")");
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Could not check for updates: " + e.getMessage());
            }
        });
    }

    public String getLatestVersion() {
        return latestVersion;
    }
}
