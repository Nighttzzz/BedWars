package br.com.cubeland.messages;

import br.com.cubeland.Teams;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.material.Bed;

public class Messages {

    public static String playerJoinMessage(Player player, int currentPlayers, int maxPlayers) {
        return String.format("&a%s &eentrou na partida! (%s/%s)", player, currentPlayers, maxPlayers);
    }

    public static String playerLeaveMessage(Player player, int currentPlayers, int maxPlayers) {
        return String.format("&a%s &eentrou na partida! (%s/%s)", player, currentPlayers, maxPlayers);
    }

    public static String countdownMessage(int second) {
        return String.format("&eA partida será iniciada em &a%s &esegundos. (%s/%s)", second);
    }

    public static String teamAssignedMessage(Teams team) {
        return String.format("&eVocê está no time %s%s&e!", team.getColor(), team.getName());
    }

    public static String playerKillMessage(Player player, Player killer) {
        return String.format("&c%s &efoi abatido por &c%s!", player, killer);
    }

    public static String bedBreakMessage(Player breaker, Block block) {
        Teams.

        return String.format("&a%s &eentrou na partida! (%s/%s)", player, currentPlayers, maxPlayers);
    }

    public static String finalKillMessage(Player player, int currentPlayers, int maxPlayers) {
        return String.format("&a%s &eentrou na partida! (%s/%s)", player, currentPlayers, maxPlayers);
    }

}
