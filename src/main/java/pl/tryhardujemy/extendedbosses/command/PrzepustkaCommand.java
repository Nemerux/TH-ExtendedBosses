package pl.tryhardujemy.extendedbosses.command;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import pl.tryhardujemy.extendedbosses.data.BossBlock;
import pl.tryhardujemy.extendedbosses.data.BossBlockData;

import java.util.Optional;

public class PrzepustkaCommand implements CommandExecutor {
    private BossBlockData blockData;

    public PrzepustkaCommand(BossBlockData blockData) {
        this.blockData = blockData;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("extended.bosses")) {
            sender.sendMessage(ChatColor.RED + "Nie posiadasz wystarczajacych uprawnien do uzycia tej komendy.");
            return true;
        }

        if(!(sender instanceof Player)) {
            sender.sendMessage("Ta komenda jest tylko dla graczy.");
            return true;
        }

        if(args.length != 2) {
            sender.sendMessage(ChatColor.RED + "Poprawne uzycie: /przepustka (nick gracza) (nazwa bossa)");
            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if(player == null) {
            sender.sendMessage(ChatColor.RED + "Nie znaleziono gracza.");
            return true;
        }

        Optional<BossBlock> block = blockData.findBlockByName(args[1]);
        if(!block.isPresent()) {
            sender.sendMessage(ChatColor.RED + "Nie znaleziono takiego bossa.");
            return true;
        }

        player.getInventory().addItem(block.get().getBossItem());
        player.sendMessage(ChatColor.GREEN + "Otrzymales przepustke na bossa " + args[1] + "!");
        sender.sendMessage(ChatColor.GREEN + "Pomyslnie nadano przepustke!");
        return true;
    }
}
