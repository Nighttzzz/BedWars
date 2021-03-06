package br.com.cubeland;

import static br.com.cubeland.EnumGameStatus.*;
import static br.com.cubeland.messages.ChatMessages.*;
import static br.com.cubeland.utils.SoundUtils.*;

import br.com.cubeland.utils.AdminCommands;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BedWars extends JavaPlugin implements Listener {
    static EnumGameStatus status = AWAITING_PLAYERS;
    static int onlinePlayers;
    static int countdownTimer = 10;
    static final int minPlayers = 2;
    static final int maxPlayers = 4;
    static final Team[] teams = new Team[4];

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("admin").setExecutor(new AdminCommands());
    }

    public void registerTeams() {
        teams[0] = new Team(EnumTeams.RED);
        teams[1] = new Team(EnumTeams.BLUE);
        teams[2] = new Team(EnumTeams.GREEN);
        teams[3] = new Team(EnumTeams.ORANGE);
    }

    public void assignTeams() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            for (Team team : teams) {
                if (team.getTeamPlayers().size() < 1) {
                    team.addPlayer(player);
                    break;
                }
            }
        }
    }

    public void startMatch() {
        assignTeams();
        status = IN_PROGRESS;

        for (Player player : Team.getPlayers()) {
            Team team = Team.getTeam(player);
            player.teleport(team.getLocation());
        }

        broadcastSoundEffect(Sound.FIREWORK_BLAST2, 20, 2);
    }

    public void countdown() {
        status = STARTING;

        new BukkitRunnable(){
            @Override
            public void run() {
                switch (status) {
                    case STARTING:
                        if (countdownTimer <= 0) {
                            this.cancel();
                            startMatch();
                        } else {
                            sendCountdownMessage(countdownTimer--);
                        }
                        break;
                    case AWAITING_PLAYERS:
                        this.cancel();
                        sendCancelCountdownMessage();
                        countdownTimer = 10;
                        break;
                }
            }
        }.runTaskTimer(this, 0, 20);
    }

    public void handlePlayerDeath(Player player, Player killer) {
        Team playerTeam = Team.getTeam(player);

        if (playerTeam.hasBed()) {
            player.teleport(playerTeam.getLocation());
            player.setHealth(player.getMaxHealth());
            sendPlayerKillMessage(player, killer);
        } else {
            spectator(player);
            sendFinalKillMessage(player, killer);
        }
    }

    public void handlePlayerDeath(Player player) {
        Team playerTeam = Team.getTeam(player);

        if (playerTeam.hasBed()) {
            player.teleport(playerTeam.getLocation());
            player.setHealth(player.getMaxHealth());
            sendPlayerKillMessage(player);
        } else {
            spectator(player);
            sendFinalKillMessage(player);
        }
    }

    public void handleBedBreak(Player playerBreaker, Block block) {
        if (Team.isBedLocation(teams, block)) {
            Team bedTeam = Team.getTeam(block);
            bedTeam.breakBed(playerBreaker, block);
        }
    }

    public void handleVoidDamage(EntityDamageEvent event, Player player) {
        event.setCancelled(true);
        player.setFallDistance(0);

        if (status.equals(AWAITING_PLAYERS) || status.equals(STARTING)) {
            player.teleport(new Location(Bukkit.getWorld("world"), 0, 65, 0));
        } else {
            handlePlayerDeath(player);
        }
    }

    private void spectator(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
    }

    @EventHandler
    public void onWordLoad(WorldLoadEvent event) {
        if (event.getWorld().getName().equalsIgnoreCase("world")) {
            registerTeams();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        onlinePlayers = Bukkit.getOnlinePlayers().size();
        Player player = event.getPlayer();

        event.setJoinMessage(null);
        sendPlayerJoinMessage(player, onlinePlayers, maxPlayers);

        if (status.equals(AWAITING_PLAYERS) && onlinePlayers == minPlayers) {
            countdown();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        onlinePlayers = Bukkit.getOnlinePlayers().size() - 1; //Correção temporária (caso haja uma forma menos gambiarrenta de resolver).
        Player player = event.getPlayer();

        event.setQuitMessage(null);
        sendPlayerLeaveMessage(player, onlinePlayers, maxPlayers);

        if (status.equals(STARTING) && onlinePlayers < minPlayers) {
            status = AWAITING_PLAYERS;
        }
    }

    @EventHandler
    public void onPlayerDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player player = (Player) event.getEntity();
            Player attacker = (Player) event.getDamager();

            if (player.getHealth() - event.getDamage() < 1) {
                event.setCancelled(true);
                handlePlayerDeath(player, attacker);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (event.getCause().equals(DamageCause.VOID)) {
                handleVoidDamage(event, player);
            }

            if (status.equals(AWAITING_PLAYERS) || status.equals(STARTING)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (block.getType().equals(Material.BED_BLOCK)) {
            handleBedBreak(player, block);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

}
