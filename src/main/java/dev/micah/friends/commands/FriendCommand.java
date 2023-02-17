package dev.micah.friends.commands;

import dev.micah.friends.Friends;
import dev.micah.friends.Util;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class FriendCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            // Check to make sure a target is provided
            if (args.length != 1) { player
                    .sendMessage(Util.color("&cInvalid usage of command: /friend <player>/list")); return false; }

            // Check before seeing if the player is a target if the user wants to see their online friends
            if (args[0].equalsIgnoreCase("list")) {
                // Get the list of online from the handler
                List<Player> online = Friends.getFriendsHandler().getOnlineFriends(player);
                // Check if nobody is online and send error message
                if (online.isEmpty()) { player
                        .sendMessage(Util.color("&cNo friends are currently online!")); return false; }
                // If user(s) are online, send over the list in a message.
                player.sendMessage(Util.color("&aOnline friends: "));
                online.forEach(onlineUser ->
                        player.sendMessage(Util.color("&7- &f" + onlineUser.getDisplayName())));
                return false;
            }

            // Check to make sure the target actually exists (or is online)
            Player target = Bukkit.getPlayer(args[0]);
            if (target != null) {
                // All handling done through method.
                Friends.getFriendsHandler().addFriend(player, target);
            } else {
                player.sendMessage(Util.color("&cThe player specified could not be found!"));
            }

        } else {
            Bukkit.getLogger().info(Util.color("&cYou cannot execute this command from console!"));
        }

        return false;
    }

}
