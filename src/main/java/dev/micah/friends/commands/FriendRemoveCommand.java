package dev.micah.friends.commands;

import dev.micah.friends.Friends;
import dev.micah.friends.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FriendRemoveCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // Check to make sure a target is provided
            if (args.length != 1) { player
                    .sendMessage(Util.color("&cInvalid usage of command: /unfriend <player>")); return false; }

            // Check to make sure the target actually exists (or is online)
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                // All handling done through method.
                Friends.getFriendsHandler().removeFriend(player, target);
            } else {
                player.sendMessage(Util.color("&cThe player specified could not be found!"));
            }

        } else {
            Bukkit.getLogger().info(Util.color("&cYou cannot execute this command from console!"));
        }
        return false;
    }

}
