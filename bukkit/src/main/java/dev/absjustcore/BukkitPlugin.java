package dev.absjustcore;

import dev.absjustcore.factory.GroupFactory;
import dev.absjustcore.provider.MongoDBProvider;
import dev.absjustcore.provider.MysqlProvider;
import dev.absjustcore.provider.Provider;
import dev.absjustcore.provider.utils.StoreMeta;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public final class BukkitPlugin extends JavaPlugin {

    @Getter private static BukkitPlugin instance;

    @Override
    public void onEnable() {
        instance = this;

        this.saveDefaultConfig();

        String providerName = this.getConfig().getString("provider");

        if (!providerName.equalsIgnoreCase("mysql") && !providerName.equalsIgnoreCase("mongodb")) {
            this.getLogger().warning("Invalid provider... Please set it as MySQL or MongoDB");

            return;
        }

        ConfigurationSection section = this.getConfig().getConfigurationSection(providerName);

        Provider provider = providerName.equalsIgnoreCase("mysql") ? new MysqlProvider() : new MongoDBProvider();
        provider.init(StoreMeta.builder()
                .append("address", section.getString("address"))
                .append("password", section.getString("password"))
                .append("username", section.getString("username"))
                .append("dbname", section.getString("dbname"))
                .build()
        );

        AbsjustPlugin.getInstance().setProvider(provider);

        GroupFactory.getInstance().init();
    }

    public void onDisable() {
        Provider provider = AbsjustPlugin.getInstance().getProvider();

        if (provider != null) provider.close();
    }
}