package by.teradapter.limitchanger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public final class LimitChanger extends JavaPlugin implements CommandExecutor, TabCompleter {

    private final FileConfiguration cfg = this.getConfig();

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        this.getCommand("limitchanger").setExecutor(this);
    }

    @Override
    public void onDisable() {
        if (cfg.getBoolean("main-settings.save-new-max-players")) {
            updateServerProperties();
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("limitchanger.command.main")) {
            sender.sendMessage(ColorUtils.colorize(cfg.getString("messages.no-permissions")));
            return true;
        }

        if (args.length > 0) {
            String action = args[0].toLowerCase();

            switch (action) {
                case "reload":
                    this.reloadConfig();
                    sender.sendMessage("Reloaded");
                    break;

                case "set":
                    if (args.length > 1) {
                        try {
                            int maxPlayers = Integer.parseInt(args[1]);
                            if (maxPlayers < 0) {
                                sender.sendMessage(ChatColor.RED + "The value cannot be less than zero or exceed " + Integer.MAX_VALUE);
                            } else {
                                Bukkit.setMaxPlayers(maxPlayers);
                                Bukkit.broadcast(ColorUtils.colorize(cfg.getString("messages.changed"))
                                        .replace("%executor%", sender.getName())
                                        .replace("%max_online%", String.valueOf(maxPlayers)), "limitchanger.notify");
                            }
                        } catch (NumberFormatException e) {
                            sender.sendMessage(ChatColor.RED + "Please enter a valid number for max players.");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Please provide a value after «set»");
                    }
                    break;

                default:
                    sender.sendMessage(ChatColor.RED + "Invalid command! Use /limitchanger reload or /limitchanger set <value>");
                    break;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Please specify «reload» or set «value»");
        }

        return true;
    }

    public void updateServerProperties() {
        Properties properties = new Properties();
        File propertiesFile = new File("server.properties");

        try {
            try (InputStream is = new FileInputStream(propertiesFile)) {
                properties.load(is);
            }

            String maxPlayers = Integer.toString(getServer().getMaxPlayers());

            if (properties.getProperty("max-players").equals(maxPlayers)) {
                return;
            }

             getLogger().info("Save the new value of the maximum online in the server.properties file");
            properties.setProperty("max-players", maxPlayers);

            try (OutputStream os = new FileOutputStream(propertiesFile)) {
                properties.store(os, "Minecraft server properties");
            }
        } catch (IOException e) {
            getLogger().severe("Could not save the new value of the maximum online in the server.properties file: " + e);
        }
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {

        List<String> cmd = new ArrayList<>();

        if (args.length == 1) {
            cmd.add("reload");
            cmd.add("set");
        } else if (args.length == 2 && "set".equalsIgnoreCase(args[0])) {
            cmd.add("[Enter a numeric value]");
        }

        return cmd;
    }
}