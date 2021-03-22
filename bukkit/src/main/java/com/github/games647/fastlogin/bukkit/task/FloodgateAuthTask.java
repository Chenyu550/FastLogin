package com.github.games647.fastlogin.bukkit.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.geysermc.connector.GeyserConnector;
import org.geysermc.connector.common.AuthType;
import org.geysermc.connector.network.session.GeyserSession;
import org.geysermc.floodgate.FloodgateAPI;
import org.geysermc.floodgate.FloodgatePlayer;

import com.github.games647.fastlogin.bukkit.BukkitLoginSession;
import com.github.games647.fastlogin.bukkit.FastLoginBukkit;
import com.github.games647.fastlogin.core.hooks.AuthPlugin;

public class FloodgateAuthTask implements Runnable {

    private final FastLoginBukkit plugin;
    private final Player player;

    public FloodgateAuthTask(FastLoginBukkit plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
    }

    @Override
    public void run() {
        FloodgatePlayer floodgatePlayer = FloodgateAPI.getPlayer(player.getUniqueId());
        plugin.getLog().info(
                "Player {} is connecting through Geyser Floodgate.",
                player.getName());
        String allowNameConflict = plugin.getCore().getConfig().getString("allowFloodgateNameConflict");
        // check if the Bedrock player is linked to a Java account 
        boolean isLinked = floodgatePlayer.fetchLinkedPlayer() != null;
        if (allowNameConflict.equalsIgnoreCase("linked") && !isLinked) {
            plugin.getLog().info(
                    "Bedrock Player {}'s name conflits an existing Java Premium Player's name",
                    player.getName());
            player.kickPlayer("This name is allready in use by a Premium Java Player");

        }
        if (!allowNameConflict.equalsIgnoreCase("true") && !allowNameConflict.equalsIgnoreCase("linked")) {
            plugin.getLog().error(
                    "Invalid value detected for 'allowNameConflict' in FasttLogin/config.yml. Aborting login of Player {}",
                    player.getName());
            return;
        }
        
        AuthPlugin<Player> authPlugin = plugin.getCore().getAuthPluginHook();

        String autoLoginFloodgate = plugin.getCore().getConfig().getString("autoLoginFloodgate");
        boolean autoRegisterFloodgate = plugin.getCore().getConfig().getBoolean("autoRegisterFloodgate");
        
        // create fake session to make auto login work
        // the player should only be registered (=> parm. registered = false) if
        // the player is not registered and autoRegister is enabled in the config
        try {
            BukkitLoginSession session = new BukkitLoginSession(player.getName(), authPlugin.isRegistered(player.getName()) || !autoRegisterFloodgate);
            // enable auto login based on the value of 'autoLoginFloodgate' in config.yml
            session.setVerified(autoLoginFloodgate.equalsIgnoreCase("true")
                    || (autoLoginFloodgate.equalsIgnoreCase("linked") && isLinked));
            
            //run login task
            Runnable forceLoginTask = new ForceLoginTask(plugin.getCore(), player, session);
            Bukkit.getScheduler().runTaskAsynchronously(plugin, forceLoginTask);
        } catch (Exception e) {
            plugin.getLog().error(
                    "An error has occured while checking if player {} is registered",
                    player.getName());
            return;
        }
    }
    
    public static GeyserSession getGeyserPlayer(String username) {
        if (Bukkit.getServer().getPluginManager().isPluginEnabled("floodgate-bukkit") &&
                Bukkit.getServer().getPluginManager().isPluginEnabled("Geyser-Spigot") &&
                GeyserConnector.getInstance().getDefaultAuthType() == AuthType.FLOODGATE) {
            // the Floodgate API requires UUID, which is inaccessible at NameCheckTask.java
            // the Floodgate API has a return value for Java (non-bedrock) players, if they
            // are linked to a Bedrock account
            // workaround: iterate over Geyser's player's usernames
            for (GeyserSession geyserPlayer : GeyserConnector.getInstance().getPlayers()) {
                if (geyserPlayer.getName().equals(username)) {
                    return geyserPlayer;
                }
            }
        }
        return null;
    }

}
