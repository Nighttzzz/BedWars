package br.com.cubeland;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public enum EnumTeams {
    RED("Vermelho", 14, "&c", new Location(Bukkit.getWorld("world1"), -33.5, 66, 0.5), new Location(Bukkit.getWorld("world1"), -30, 66, 0)),
    GREEN("Verde", 14, "&c", new Location(Bukkit.getWorld("world1"), 0.5, 66, -33.5), new Location(Bukkit.getWorld("world1"), 0, 66, -30)),
    BLUE("Azul", 14, "&c", new Location(Bukkit.getWorld("world1"), 34.5, 66, 0.5), new Location(Bukkit.getWorld("world1"), 31, 66, 0)),
    ORANGE("Laranja", 14, "&c", new Location(Bukkit.getWorld("world1"), 0.5, 66, 34.5), new Location(Bukkit.getWorld("world1"), 0, 66, 31));

    private final String name;
    private final int data;
    private final String color;
    private final Location location;
    private final Location bedLocation;

    EnumTeams(String name, int woolData, String color, Location location, Location bedLocation) {
        this.name = name;
        this.data = woolData;
        this.color = color;
        this.location = location;
        this.bedLocation = bedLocation;
    }

    public String getName() {
        return name;
    }

    public int getData() {
        return data;
    }

    public String getColor() {
        return color;
    }

    public Location getLocation() {
        return location;
    }

    public Location getBedLocation() {
        return bedLocation;
    }
}
