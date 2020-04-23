package pl.tryhardujemy.extendedbosses.data;

import net.dzikoysk.funnyguilds.basic.guild.Guild;
import net.dzikoysk.funnyguilds.basic.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.mineacademy.boss.lib.boss.api.Boss;
import org.mineacademy.boss.lib.boss.api.BossAPI;
import org.mineacademy.boss.lib.boss.api.BossSpawnReason;
import org.mineacademy.boss.lib.boss.api.SpawnedBoss;

import java.util.stream.Collectors;

public class BossBlock {
    private String bossName;
    private Location bossBlockLocation;
    private Location teleportLocation;
    private Location bossSpawnLocation;
    private ItemStack bossItem;
    private Guild nowFighting;
    private Boss boss;
    private SpawnedBoss currentBoss;

    BossBlock(ConfigurationSection section) {
        this.bossName = section.getName();
        this.boss = BossAPI.getBoss(bossName);
        this.bossBlockLocation = locationFromString(section.getString("location"));
        this.teleportLocation = locationFromString(section.getString("teleport_location"));
        this.bossSpawnLocation = locationFromString(section.getString("boss_spawn_location"));
        this.bossItem = new ItemStack(Material.getMaterial(section.getString("item.type")));
        ItemMeta bossItemMeta = bossItem.getItemMeta();
        bossItemMeta.setDisplayName(fixColor(section.getString("item.name")));
        bossItemMeta.setLore(section.getStringList("item.lore").stream().map(this::fixColor).collect(Collectors.toList()));
        bossItem.setItemMeta(bossItemMeta);
    }

    public BossBlock(Boss boss, String bossName, ItemStack item) {
        this.bossName = bossName;
        this.boss = boss;
        this.bossItem = item;
    }

    public String getBossName() {
        return bossName;
    }

    public Location getBossBlockLocation() {
        return bossBlockLocation;
    }

    public Location getTeleportLocation() {
        return teleportLocation;
    }

    public ItemStack getBossItem() {
        return bossItem;
    }

    public Guild getNowFighting() {
        return nowFighting;
    }

    public void setNowFighting(Guild guild) {
        this.nowFighting = guild;
    }

    public boolean canTeleport() {
        return nowFighting.getOnlineMembers().stream().map(User::getPlayer).noneMatch(player -> player.getWorld().equals(teleportLocation.getWorld()));
    }

    private Location locationFromString(String location) {
        if(location == null) return null;
        String[] splittedLocation = location.split(",");
        return new Location(Bukkit.getWorld(splittedLocation[0]), Integer.parseInt(splittedLocation[1]), Integer.parseInt(splittedLocation[2]), Integer.parseInt(splittedLocation[3]));
    }

    private String locationToString(Location location) {
        if(location == null) return null;
        return location.getWorld().getName() + "," + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
    }

    private String fixColor(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public SpawnedBoss getCurrentBoss() {
        return currentBoss;
    }

    public void spawnBoss() {
        this.currentBoss = boss.spawn(bossSpawnLocation, BossSpawnReason.CUSTOM);
    }

    public void save(ConfigurationSection configurationSection) {
        configurationSection.set("location", locationToString(this.bossBlockLocation));
        configurationSection.set("teleport_location", locationToString(this.teleportLocation));
        configurationSection.set("boss_spawn_location", locationToString(this.bossSpawnLocation));
        configurationSection.set("item.type", this.bossItem.getType().name());
        configurationSection.set("item.name", this.bossItem.getItemMeta().getDisplayName().replace("\u00A7", "&"));
        configurationSection.set("item.lore", this.bossItem.getItemMeta().getLore().stream().map(str -> str.replace("\u00A7", "&")).collect(Collectors.toList()));
    }

    public void setBossBlockLocation(Location bossBlockLocation) {
        this.bossBlockLocation = bossBlockLocation;
    }

    public void setTeleportLocation(Location teleportLocation) {
        this.teleportLocation = teleportLocation;
    }

    public void setBossSpawnLocation(Location bossSpawnLocation) {
        this.bossSpawnLocation = bossSpawnLocation;
    }
}
