package pl.tryhardujemy.playertime;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class TimeMeasurementCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("tryhardujemy.czas") || !(sender instanceof Player)) {
            if (args.length != 1) {
                sender.sendMessage(ChatColor.RED + "Poprawne uzycie: /" + label + " (nick gracza)");
                return true;
            }
            Optional<Long> time = TimeMeasurmentPlugin.getPluginInstance().getTimeData().getPlayerTime(args[0]);
            if (!time.isPresent()) {
                sender.sendMessage(ChatColor.RED + "Nie znaleziono gracza.");
            } else {
                sender.sendMessage(ChatColor.GRAY + "Gracz " + args[0] + " posiada " + ChatColor.AQUA + TimeParsingUtils.formatSecs(time.get()) + ChatColor.GRAY + " przegranego czasu na serwerze.");
            }
        } else if (!sender.hasPermission("trhardujemy.czas")) {
            Optional<Long> time = TimeMeasurmentPlugin.getPluginInstance().getTimeData().getPlayerTime((Player) sender);
            if (!time.isPresent()) {
                sender.sendMessage(ChatColor.RED + "Wystapil blad! Relognij sie, aby sprawdzic swoj czas na serwerze.");
            } else {
                sender.sendMessage(ChatColor.GRAY + "Posiadasz " + ChatColor.AQUA + TimeParsingUtils.formatSecs(time.get()) + ChatColor.GRAY + " przegranego czasu na serwerze.");
            }
        }
        return true;
    }
}
