package dev.absjustcore;

import dev.absjustcore.command.CoreAdminCommand;
import dev.absjustcore.factory.GroupFactory;
import dev.absjustcore.provider.MongoDBProvider;
import dev.absjustcore.provider.MysqlProvider;
import dev.absjustcore.provider.Provider;
import dev.absjustcore.provider.utils.StoreMeta;
import dev.absjustcore.sender.AbstractSender;
import dev.absjustcore.sender.BukkitSender;
import lombok.Getter;
import org.apache.logging.log4j.Level;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.stream.Stream;

public final class BukkitPlugin extends JavaPlugin {

    @Getter private static BukkitPlugin instance;

    private AbstractSender consoleSender;

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();

        this.consoleSender = new BukkitSender(Bukkit.getConsoleSender());

        Configuration configuration = this.getConfig();

        // Load AbsjustPlugin instance
        AbsjustPlugin.getInstance().setStoreLog(configuration.getBoolean("log.store", true));
        AbsjustPlugin.getInstance().setLogRemote(configuration.getBoolean("log.notify-remote"));
        AbsjustPlugin.getInstance().setFuturePrimaryThread(Bukkit::isPrimaryThread);
        AbsjustPlugin.getInstance().setFutureOnlineSenders(() -> Stream.concat(
                Stream.of(this.consoleSender),
                this.getServer().getOnlinePlayers().stream().map(this::wrapSender)
        ));

        String providerName = configuration.getString("provider");

        if (!providerName.equalsIgnoreCase("mysql") && !providerName.equalsIgnoreCase("mongodb")) {
            this.getLogger().warning("Invalid provider... Please set it as MySQL or MongoDB");

            return;
        }

        ConfigurationSection section = configuration.getConfigurationSection(providerName.toLowerCase());

        if (section == null) {
            AbsjustPlugin.getLogger().log(Level.ERROR, "An error occurred when tried get the Provider section");

            return;
        }

        Provider provider = providerName.equalsIgnoreCase("mysql") ? new MysqlProvider() : new MongoDBProvider();

        provider.init(StoreMeta.builder()
                .append("address", section.getString("address"))
                .append("password", section.getString("password", null))
                .append("username", section.getString("username", null))
                .append("dbname", section.getString("dbname"))
                .build()
        );

        AbsjustPlugin.setProvider(provider);

        GroupFactory.getInstance().init();

        this.getCommand("coreadmin").setExecutor((commandSender, command, s, args) -> {
            CoreAdminCommand.getInstance().execute(wrapSender(commandSender), s, null, args);

            return false;
        });
    }

    public void onDisable() {
        Provider provider = AbsjustPlugin.getProvider();

        if (provider != null) provider.close();
    }

    public AbstractSender wrapSender(CommandSender sender) {
        return sender instanceof Player ? new BukkitSender(sender) : this.consoleSender;
    }
}