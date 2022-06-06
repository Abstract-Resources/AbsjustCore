package dev.absjustcore.sender;

import lombok.RequiredArgsConstructor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@RequiredArgsConstructor
public final class BukkitSender extends AbstractSender {

    private final CommandSender sender;

    @Override
    public String getName() {
        return this.sender.getName();
    }

    public UUID getUniqueId() {
        return this.sender instanceof Player ? ((Player) this.sender).getUniqueId() : null;
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.sender.hasPermission(permission);
    }

    @Override
    public void sendMessage(String k, String... args) {
        this.sender.sendMessage(ChatColor.translateAlternateColorCodes('&', k));

        // TODO: Translate the message and send it
    }
}