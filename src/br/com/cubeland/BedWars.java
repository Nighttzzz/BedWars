package br.com.cubeland;

import static br.com.cubeland.messages.Messages.*;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BedWars extends JavaPlugin implements Listener {
    static int onlinePlayers;
    static int minPlayers = 2;
    static int maxPlayers = 4;
    static boolean starting;
    static Teams[] teams = new Teams[4];


    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);

        teams[0] = new Teams(EnumTeams.RED);
        teams[1] = new Teams(EnumTeams.BLUE);
        teams[2] = new Teams(EnumTeams.GREEN);
        teams[3] = new Teams(EnumTeams.ORANGE);
    }

    @Override
    public void onDisable() {

    }

    public void assignTeams() {
        Bukkit.broadcastMessage("Sorteando times...");

        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Teams team : teams) {
                if (team.teamPlayers.size() < 1) {
                    team.addPlayer(player);
                    break;
                }
            }
        }

        Bukkit.broadcastMessage("Processo de sorteio de times finalizado.");
    }

    public void startCountdown() {
        starting = true;

        for (int i = 10; i > 0; i--) {
            if (starting) {
                countdownMessage(i);
            } else {
                Bukkit.broadcastMessage("&eInício da partida cancelado. Aguardando jogadores.");
                return;
            }
        }

        startMatch();
    }

    public void startMatch() {
        assignTeams();

        for (Player player : Bukkit.getOnlinePlayers()) {
            Teams playerTeam = Teams.getTeam(player);
            player.teleport(playerTeam.location);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        onlinePlayers = Bukkit.getOnlinePlayers().size();
        Player player = event.getPlayer();

        playerJoinMessage(player, onlinePlayers, maxPlayers);

        if (!starting && onlinePlayers == minPlayers) {
            startCountdown();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        onlinePlayers = Bukkit.getOnlinePlayers().size();
        Player player = event.getPlayer();

        playerLeaveMessage(player, onlinePlayers, maxPlayers);

        if (starting && onlinePlayers < minPlayers) {
            starting = false;
        }
    }

    @EventHandler
    public void onPlayerDamageByEntity(EntityDamageByEntityEvent event) {
        Player player = (Player) event.getEntity();
        Player damager = (Player) event.getDamager();
        Teams playerTeam = Teams.getTeam(player);

        if (player.getHealth() - event.getDamage() < 1) {
            event.setCancelled(true);

            if (playerTeam.hasBed()) {
                player.teleport(playerTeam.location);
                player.setHealth(player.getMaxHealth());
                playerKillMessage(player, damager);
            } else {
                spectator(player);
                finalKillMessage(player, damager);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (event.getBlock().getType().equals(Material.BED_BLOCK) && Teams.isBedLocation(teams, block)) {
            Teams bedTeam = Teams.getTeam(block);

            bedTeam.breakBed(player, block);
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    private void spectator(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
    }
}
