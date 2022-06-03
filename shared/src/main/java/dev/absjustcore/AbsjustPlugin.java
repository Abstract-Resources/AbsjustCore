package dev.absjustcore;

import dev.absjustcore.provider.Provider;
import lombok.Getter;
import lombok.Setter;

public final class AbsjustPlugin {

    @Getter private final static AbsjustPlugin instance = new AbsjustPlugin();

    @Getter @Setter private Provider provider;
}