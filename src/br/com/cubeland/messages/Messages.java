package br.com.cubeland.messages;

import static br.com.cubeland.utils.ChatUtils.*;
import br.com.cubeland.Teams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.material.Bed;

public class Messages {
    public static void playerJoinMessage(Player player, int onlinePlayers, int maxPlayers) {
        String message = translateColorCodes(String.format("&7%s &eentrou na partida! &a(%s/%s)", player.getName(), onlinePlayers, maxPlayers));

        Bukkit.broadcastMessage(message);
    }

    public static void playerLeaveMessage(Player player, int onlinePlayers, int maxPlayers) {
        String message = translateColorCodes(String.format("&7%s &saiu da partida! &a(%s/%s)", player.getName(), onlinePlayers, maxPlayers));

        Bukkit.broadcastMessage(message);
    }

    public static void countdownMessage(int second) {
        String message = translateColorCodes(String.format("&eA partida será iniciada em &a%s &esegundos.", second));

        Bukkit.broadcastMessage(message);
    }

    public static void teamAssignedMessage(Player player) {
        Teams team = Teams.getTeam(player);
        String message = translateColorCodes(String.format("&eVocê está no time %s%s&e!", team.getColor(), team.getName()));

        player.sendMessage(message);
    }

    public static void playerKillMessage(Player player, Player killer) {
        Teams playerTeam = Teams.getTeam(player);
        Teams killerTeam = Teams.getTeam(killer);
        String message = translateColorCodes(String.format("%s%s &efoi abatido por %s%s!", playerTeam.getColor(), player.getName(), killerTeam.getColor(), killer.getName()));

        Bukkit.broadcastMessage(message);
    }

    public static void bedBreakMessage(Player breaker, Block block) {
        Teams breakerTeam = Teams.getTeam(breaker);
        Teams bedTeam = Teams.getTeam(block);
        String message = translateColorCodes(String.format("&eA cama do time %s%s &efoi destruída pelo time %s%s&e!", bedTeam.getColor(), bedTeam.getName(), breakerTeam.getColor(), breakerTeam.getName()));

        Bukkit.broadcastMessage(message);

    }

    public static void finalKillMessage(Player player, Player killer) {
        String message = translateColorCodes(String.format("&c%s &efoi eliminado da partida por &c%s!", player.getName(), killer.getName()));

        Bukkit.broadcastMessage(message);
    }
}
