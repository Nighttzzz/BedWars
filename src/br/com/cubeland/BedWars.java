package br.com.cubeland;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
            Bukkit.broadcastMessage(String.format("Procurando um time para %s.", player.getName()));

            for (Teams team : teams) {
                if (team.teamPlayers.size() < 1) {
                    team.addPlayer(player);
                    Bukkit.broadcastMessage(String.format("O jogador %s foi para o time %s.", player.getName(), team.name));
                    break;
                } else {
                    Bukkit.broadcastMessage(String.format("O time %s não possui uma vaga para %s", team.name, player.getName()));
                }
            }

        }

        Bukkit.broadcastMessage("Processo de sorteio de times finalizado.");

    }

    public void startCountdown() {
        starting = true;

        for (int i = 10; i > 0; i--) {
            if (starting) {
                Bukkit.broadcastMessage(String.format("Iniciando a partida em %d segundos.", i));
            } else {
                Bukkit.broadcastMessage("Início da partida cancelado. Aguardando jogadores.");
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
        String playerName = event.getPlayer().getName();
        onlinePlayers = Bukkit.getOnlinePlayers().size();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(String.format("%s entrou na partida! %d/%d", playerName, onlinePlayers, minPlayers));
        }

        if (!starting && onlinePlayers == minPlayers) {
            startCountdown();
        }

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        String playerName = event.getPlayer().getName();
        onlinePlayers = Bukkit.getOnlinePlayers().size();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.sendMessage(String.format("%s saiu da partida! %d/%d", playerName, onlinePlayers, minPlayers));
        }

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

            if(playerTeam.hasBed()) {
                player.teleport(playerTeam.location);
                player.setHealth(player.getMaxHealth());
            } else {
                spectator(player);
            }

        }

    }

    @EventHandler
    public void onBlockBreakEvent(BlockBreakEvent event) {

        Player player = event.getPlayer();
        Block block = event.getBlock();
        Teams playerTeam = Teams.getTeam(player);

        Bukkit.broadcastMessage("----------------");
        Bukkit.broadcastMessage(String.format("var - %s", block.getLocation().toString()));
        Bukkit.broadcastMessage("----------------");

        if (event.getBlock().getType().equals(Material.BED_BLOCK) && Teams.isBedLocation(teams, block)) {
            Teams bedTeam = Teams.getTeam(block);

            Bukkit.broadcastMessage("Uma cama foi quebrada");
            bedTeam.breakBed(playerTeam);
        }

        Bukkit.broadcastMessage(String.format("bBE - %s", event.getBlock().getType().equals(Material.BED_BLOCK)));
        Bukkit.broadcastMessage(String.format("bBE - %s", Teams.isBedLocation(teams, block)));

    }

    private void spectator(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
    }

}
