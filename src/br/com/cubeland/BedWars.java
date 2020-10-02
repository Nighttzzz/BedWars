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

    public void startCountdown() {
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
                            countdownMessage(countdownTimer--);
                        }
                        break;
                    case AWAITING_PLAYERS:
                        this.cancel();
                        cancelCountdownMessage();
                        countdownTimer = 10;
                        break;
                }
            }
        }.runTaskTimer(this, 0, 20);
    }

    public void playerDeathHandler(Player player, Player killer) {
        Team playerTeam = Team.getTeam(player);

        if (playerTeam.hasBed()) {
            player.teleport(playerTeam.getLocation());
            player.setHealth(player.getMaxHealth());
            playerKillMessage(player, killer);
        } else {
            spectator(player);
            finalKillMessage(player, killer);
        }
    }

    public void playerDeathHandler(Player player) {
        Team playerTeam = Team.getTeam(player);

        if (playerTeam.hasBed()) {
            player.teleport(playerTeam.getLocation());
            player.setHealth(player.getMaxHealth());
            playerKillMessage(player);
        } else {
            spectator(player);
            finalKillMessage(player);
        }
    }

    private void spectator(Player player) {
        player.setGameMode(GameMode.SPECTATOR);
    }

    @EventHandler
    public void onWordLoad(WorldLoadEvent event) {
        Bukkit.broadcastMessage("WorldLoadEvent");
        if (event.getWorld().getName().equalsIgnoreCase("world")) {
            registerTeams();
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        onlinePlayers = Bukkit.getOnlinePlayers().size();
        Player player = event.getPlayer();

        event.setJoinMessage(null);
        playerJoinMessage(player, onlinePlayers, maxPlayers);

        if (status.equals(AWAITING_PLAYERS) && onlinePlayers == minPlayers) {
            startCountdown();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        onlinePlayers = Bukkit.getOnlinePlayers().size() - 1; //Correção temporária (caso haja uma forma menos gambiarrenta de resolver).
        Player player = event.getPlayer();

        event.setQuitMessage(null);
        playerLeaveMessage(player, onlinePlayers, maxPlayers);

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
                playerDeathHandler(player, attacker);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (status.equals(AWAITING_PLAYERS) || status.equals(STARTING)) {
                event.setCancelled(true);

                if (event.getCause().equals(DamageCause.VOID)) {
                    player.teleport(new Location(Bukkit.getWorld("world"), 0, 65, 0));
                }
                return;
            }

            if(event.getCause().equals(DamageCause.VOID)) {
                event.setCancelled(true);
                player.setFallDistance(0);
                playerDeathHandler(player);
            }

        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if (event.getBlock().getType().equals(Material.BED_BLOCK) && Team.isBedLocation(teams, block)) {
            Team bedTeam = Team.getTeam(block);

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

}
