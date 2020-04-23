package pl.tryhardujemy.extendedbosses;

import net.dzikoysk.funnyguilds.basic.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import pl.tryhardujemy.extendedbosses.command.BossBlockCommand;
import pl.tryhardujemy.extendedbosses.command.PrzepustkaCommand;
import pl.tryhardujemy.extendedbosses.data.BossBlock;
import pl.tryhardujemy.extendedbosses.data.BossBlockData;

import java.io.File;

public final class ExtendedBossesPlugin extends JavaPlugin implements Listener {
    private final BossBlockData bossData = new BossBlockData(getConfig(), new File(getDataFolder(), "config.yml"));

    @Override
    public void onEnable() {
        if (!(new File(getDataFolder(), "config.yml").exists())) saveResource("config.yml", false);
        bossData.load();
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("przepustka").setExecutor(new PrzepustkaCommand(bossData));
        getCommand("bossblock").setExecutor(new BossBlockCommand(bossData));
    }

    @EventHandler
    public void onBlockAdd(PlayerInteractEvent e) {
        if (!e.getPlayer().hasPermission("extended.bosses") || e.getClickedBlock() == null) return;
        BossBlock block = BossBlockCommand.BLOCK_MAP.get(e.getPlayer().getUniqueId());
        if (block == null) return;
        block.setBossBlockLocation(e.getClickedBlock().getLocation());
        e.getPlayer().sendMessage(ChatColor.GREEN + "Pomyslnie ustawiono lokalizacje bloku!");
        BossBlockCommand.BLOCK_MAP.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK || e.getItem() == null) return;
        BossBlock bossBlock = bossData.get(e.getClickedBlock().getLocation());
        if (bossBlock == null) return;

        if (e.getItem().isSimilar(bossBlock.getBossItem())) {
            User clickedUser = User.get(e.getPlayer());

            if (!bossBlock.canTeleport()) {
                e.getPlayer().sendMessage(ChatColor.RED + "Nie mozna sie przeteleportowac do bossa, poniewaz jest on teraz atakowany przez gildie " + bossBlock.getNowFighting().getTag() + ".");
                return;
            }

            if (clickedUser.getGuild() == null) {
                e.getPlayer().sendMessage(ChatColor.RED + "Musisz posiadać gildie, aby przeteleportować się na bossa.");
                return;
            }

            if (e.getItem().getAmount() == 1) e.getItem().setType(Material.AIR);
            else e.getItem().setAmount(e.getItem().getAmount() - 1);
            for (Entity loopedEntity : e.getPlayer().getWorld().getNearbyEntities(e.getClickedBlock().getLocation(), 5, 5, 5)) {
                if (!(loopedEntity instanceof Player)) continue;

                User loopedUser = User.get((Player) loopedEntity);
                if (!loopedUser.getGuild().equals(clickedUser.getGuild())) continue;

                loopedEntity.teleport(bossBlock.getTeleportLocation());
            }

            if (bossBlock.getCurrentBoss() != null && bossBlock.getCurrentBoss().isAlive())
                bossBlock.getCurrentBoss().getEntity().damage(bossBlock.getCurrentBoss().getEntity().getHealth());
            bossBlock.spawnBoss();
            bossBlock.setNowFighting(clickedUser.getGuild());
        }
    }

}
