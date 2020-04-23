package pl.tryhardujemy.extendedbosses.data;


import org.bukkit.Location;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.boss.lib.boss.api.Boss;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class BossBlockData {
    private final FileConfiguration configuration;
    private final File configurationFile;
    private final HashMap<Location, BossBlock> bossBlockMap = new HashMap<>();

    public BossBlock get(Location location) {
        return bossBlockMap.get(location);
    }

    public BossBlockData(FileConfiguration configuration, File configurationFile) {
        this.configuration = configuration;
        this.configurationFile = configurationFile;
    }

    public void load() {
        for (String key : configuration.getConfigurationSection("blocks").getKeys(false)) {
            BossBlock block = new BossBlock(configuration.getConfigurationSection("blocks").getConfigurationSection(key));
            bossBlockMap.put(block.getBossBlockLocation(), block);
        }
    }

    public Optional<BossBlock> findBlockByName(String name) {
        for (Map.Entry<Location, BossBlock> block : bossBlockMap.entrySet())
            if (block.getValue().getBossName().equalsIgnoreCase(name))
                return Optional.of(block.getValue());
        return Optional.empty();
    }

    public void createBlock(Boss boss, ItemStack item) {
        BossBlock block = new BossBlock(boss, boss.getName(), item);
        updateBlock(block);
    }

    public void updateBlock(BossBlock block) {
        this.bossBlockMap.put(block.getBossBlockLocation(), block);
        block.save(configuration.createSection("blocks." + block.getBossName()));
        save();
    }

    public void deleteBlock(BossBlock bossBlock) {
        this.bossBlockMap.values().remove(bossBlock);
        configuration.set("blocks." + bossBlock.getBossName(), null);
        save();
    }

    private void save() {
        try {
            configuration.save(configurationFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
