package dev.micah.friends;

import lombok.Getter;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class FriendsHandler {

    private final YamlConfiguration config = Friends.getFriendsConfig();

    /**
     * Key is the user base (or the user we are handling)
     * and value returns a list of the users friends.
     */
    @Getter
    private HashMap<UUID, List<UUID>> friendsList;

    public FriendsHandler() {
        friendsList = new HashMap<>();
    }

    /**
     * Load all data into hashmap on enable, that way we
     * save resources.
     */
    public void loadInformation() {
        // Null check
        if (config.getConfigurationSection("registered-users") == null) return;
        // Get a list of all registered users
        Set<String> keys = config.getConfigurationSection("registered-users").getKeys(false);
        // Null check
        if (keys.isEmpty()) return;
        // Loop through all registered users on enable
        keys.forEach(uuid -> {
            // Get all friends from the users list
            List<String> friends = config.getStringList("registered-users." + uuid);
            // Null check
            if (friends.isEmpty()) return;
            // Convert string UUIDs into a list of UUIDs
            List<UUID> uuidFriends = new ArrayList<>();
            friends.forEach(s -> uuidFriends.add(UUID.fromString(s)));
            // Add to the map
            friendsList.put(UUID.fromString(uuid), uuidFriends);
        });
    }

    /**
     * Simple method that adds a new user
     * as a friend and saves the information.
     *
     * @param executed The player who is executing the friend request/command.
     * @param targeted The player being targeted as the added friend.
     */
    public void addFriend(Player executed, Player targeted) {
        // Get the users list of friends, if not there create them a new list
        List<UUID> executedFriends = friendsList.getOrDefault(executed.getUniqueId(), new ArrayList<>());
        List<UUID> targetedFriends = friendsList.getOrDefault(targeted.getUniqueId(), new ArrayList<>());

        // Check if user is self
        if (executed.getUniqueId().equals(targeted.getUniqueId())) { executed.
                sendMessage(Util.color("&cYou cannot add yourself as a friend!")); return; }

        // Check if the user is already friends
        if (executedFriends.contains(targeted.getUniqueId())) { executed.
                sendMessage(Util.color("&cThis user is already your friend!")); return; }

        // If not add the player as a friend
        executedFriends.add(targeted.getUniqueId());
        targetedFriends.add(executed.getUniqueId());

        // Turn lists to list of strings
        List<String> executedStringFriends = new ArrayList<>();
        List<String> targetedStringFriends = new ArrayList<>();
        executedFriends.forEach(uuid -> executedStringFriends.add(uuid.toString()));
        targetedFriends.forEach(uuid -> targetedStringFriends.add(uuid.toString()));

        // Save information to yml
        config.set("registered-users." + executed.getUniqueId(), executedStringFriends);
        config.set("registered-users." + targeted.getUniqueId(), targetedStringFriends);
        Friends.saveFriendsConfig();

        // Send confirmation messages to each player
        executed.sendMessage(Util.color("&bYou added &7" + targeted.getDisplayName() + "&b as a friend!"));
        targeted.sendMessage(Util.color("&7" + executed.getDisplayName() + " &bhas added you as a friend!"));
    }

    /**
     * Simple method that removes a friend and
     * saves the information.
     *
     * @param executed The player who is executing the friend removal/command.
     * @param targeted The player being targeted as the added friend.
     */
    public void removeFriend(Player executed, Player targeted) {
        // Get the users list of friends, if not there create them a new list
        List<UUID> executedFriends = friendsList.getOrDefault(executed.getUniqueId(), new ArrayList<>());
        List<UUID> targetedFriends = friendsList.getOrDefault(targeted.getUniqueId(), new ArrayList<>());

        // Check to make sure the target is on their friends list
        if (!executedFriends.contains(targeted.getUniqueId())) { executed.
                sendMessage(Util.color("&cThis user is not your friend!")); return; }

        // Update the lists and save the information to config.
        executedFriends.remove(targeted.getUniqueId());
        targetedFriends.remove(executed.getUniqueId());
        config.set("registered-users." + executed.getUniqueId(), executedFriends);
        config.set("registered-users." + targeted.getUniqueId(), targetedFriends);
        Friends.saveFriendsConfig();

        // Send confirmation messages to each player
        executed.sendMessage(Util.color("&bYou removed &7" + targeted.getDisplayName() + " &bfrom your friends list!"));
        targeted.sendMessage(Util.color("&7" + executed.getDisplayName() + " &bhas removed you from their friends list!"));
    }

    /**
     * Gets a list of online friends based on
     * the executors friends list.
     *
     * @param executed The player is executing the friend list check.
     * @return A list of friends that is online (...empty if none)
     */
    @NonNull
    public List<Player> getOnlineFriends(Player executed) {
        // Create a final list that will return empty if no online friends.
        List<Player> onlineFriends = new ArrayList<>();

        // Find the users to check if they are online based on executors friends list.
        List<UUID> executedFriends = friendsList.getOrDefault(executed.getUniqueId(), new ArrayList<>());

        // Null check
        if (executedFriends.isEmpty()) return onlineFriends;

        // Loop through each friend and check if they're online, if they are add them to the list
        executedFriends.forEach(uuid -> {
            Player user = Bukkit.getPlayer(uuid);
            if (user.isOnline()) onlineFriends.add(user.getPlayer());
        });

        return onlineFriends;
    }

}
