package br.com.cubeland.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listeners implements Listener {

    int minPlayers = 2;
    int maxPlayers = 4;
    boolean starting;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String playerName = event.getPlayer().getName();
        int onlinePlayers = Bukkit.getOnlinePlayers().size();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(String.format("%s entrou na partida! %d/%d", playerName, onlinePlayers, minPlayers));
        }

        if (!starting && onlinePlayers == minPlayers) {

        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String playerName = event.getPlayer().getName();
        int onlinePlayers = Bukkit.getOnlinePlayers().size();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(String.format("%s saiu da partida! %d/%d", playerName, onlinePlayers, minPlayers));
        }

        if (starting && onlinePlayers < minPlayers) {

        }

    }

}
