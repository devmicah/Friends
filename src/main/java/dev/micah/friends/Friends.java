package dev.micah.friends;

import dev.micah.friends.commands.FriendCommand;
import dev.micah.friends.commands.FriendRemoveCommand;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Friends extends JavaPlugin {

    /**
     * Usually handle custom yml's in separate classes
     * but just to save time and space do it here.
     */
    private static File friendsFile;
    @Getter
    private static YamlConfiguration friendsConfig;

    @Getter
    private static FriendsHandler friendsHandler;

    @Override @SneakyThrows
    public void onEnable() {
        // Handle data/file creation
        if (!getDataFolder().exists()) getDataFolder().mkdirs();
        // Create custom yml and define to config
        friendsFile = new File(getDataFolder(), "friends.yml");
        if (!friendsFile.exists()) friendsFile.createNewFile();
        friendsConfig = YamlConfiguration.loadConfiguration(friendsFile);

        friendsHandler = new FriendsHandler();
        friendsHandler.loadInformation();

        getCommand("friend").setExecutor(new FriendCommand());
        getCommand("unfriend").setExecutor(new FriendRemoveCommand());
    }

    @Override
    public void onDisable() {
        friendsHandler.flushInformation();
    }

    @SneakyThrows
    public static void saveFriendsConfig() {
        friendsConfig.save(friendsFile);
    }

}
