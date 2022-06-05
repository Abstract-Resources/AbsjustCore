package dev.absjustcore;

import dev.absjustcore.provider.Provider;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class AbsjustPlugin {

    @Getter private final static AbsjustPlugin instance = new AbsjustPlugin();
    @Getter private static final Logger logger = LogManager.getLogger("AbsjustCore");

    @Getter @Setter private Provider provider;
}