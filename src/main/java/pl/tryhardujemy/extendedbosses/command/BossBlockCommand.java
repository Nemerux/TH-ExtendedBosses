package pl.tryhardujemy.extendedbosses.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mineacademy.boss.lib.boss.api.Boss;
import org.mineacademy.boss.lib.boss.api.BossAPI;
import pl.tryhardujemy.extendedbosses.data.BossBlock;
import pl.tryhardujemy.extendedbosses.data.BossBlockData;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class BossBlockCommand implements CommandExecutor {
    public static final Map<UUID, BossBlock> BLOCK_MAP = new HashMap<>();
    private BossBlockData blockData;

    public BossBlockCommand(BossBlockData blockData) {
        this.blockData = blockData;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("extended.bosses")) {
            sender.sendMessage(ChatColor.RED + "Nie posiadasz wystarczajacych uprawnien do uzycia tej komendy.");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage("Ta komenda jest tylko dla graczy.");
            return true;
        }

        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sender.sendMessage(ChatColor.BLUE + "/bossblock help - " + ChatColor.GRAY + " wyświetla tą wiadomość");
            sender.sendMessage(ChatColor.BLUE + "/bossblock (nazwa bossa) create (musisz trzymac item w ręce) - " + ChatColor.GRAY + "tworzy blok bossa");
            sender.sendMessage(ChatColor.BLUE + "/bossblock (nazwa bossa) delete - " + ChatColor.GRAY + "usuwa blok bossa");
            sender.sendMessage(ChatColor.BLUE + "/bossblock (nazwa bossa) setblock - " + ChatColor.GRAY + "ustawia blok, za pomoca ktorego mozna sie przeteleportowac do bossa");
            sender.sendMessage(ChatColor.BLUE + "/bossblock (nazwa bossa) setteleport - " + ChatColor.GRAY + "ustawia miejsce teleportacji");
            sender.sendMessage(ChatColor.BLUE + "/bossblock (nazwa bossa) setspawn - " + ChatColor.GRAY + "ustawia miejsce spawnu bossa");
        } else if (args[1].equalsIgnoreCase("create")) {
            Player player = (Player) sender;
            Optional<BossBlock> block = blockData.findBlockByName(args[0]);
            if (block.isPresent()) {
                sender.sendMessage(ChatColor.RED + "Ten blok juz istnieje!");
                return true;
            }

            Boss boss = BossAPI.getBoss(args[0]);
            if (boss == null) {
                sender.sendMessage(ChatColor.RED + "Nie znaleziono bossa o nazwie " + args[0] + "...");
                return true;
            }

            if (player.getInventory().getItemInMainHand() == null) {
                sender.sendMessage(org.bukkit.ChatColor.RED + "Nie trzymasz zadnego itemu w rece.");
                return true;
            }

            blockData.createBlock(boss, player.getInventory().getItemInMainHand());
            sender.sendMessage(org.bukkit.ChatColor.GREEN + "Pomyslnie utworzono bossa!");
        } else {
            Optional<BossBlock> block = blockData.findBlockByName(args[0]);
            if (!block.isPresent()) {
                sender.sendMessage(ChatColor.RED + "Nie znaleziono takiego bloku...");
                return true;
            }

            if (args[1].equalsIgnoreCase("delete")) {
                blockData.deleteBlock(block.get());
                sender.sendMessage(ChatColor.GREEN + "Pomyslnie usunieto bossa!");
            } else if (args[1].equalsIgnoreCase("setblock")) {
                sender.sendMessage(ChatColor.GREEN + "Kliknij na blok, ktory chcesz uzyc jako blok teleportacji...");
                BLOCK_MAP.put(((Player) sender).getUniqueId(), block.get());
            } else if (args[1].equalsIgnoreCase("setteleport")) {
                block.get().setTeleportLocation(((Player) sender).getLocation());
                sender.sendMessage(ChatColor.GREEN + "Pomyslnie ustawiono lokalizacje teleportacji!");
            } else if (args[1].equalsIgnoreCase("setspawn")) {
                block.get().setBossSpawnLocation(((Player) sender).getLocation());
                sender.sendMessage(ChatColor.GREEN + "Pomyslnie ustawiono lokalizacje spawnu bossa!");
            }
        }


        return true;
    }
}
