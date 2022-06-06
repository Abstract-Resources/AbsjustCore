package dev.absjustcore.provider;

import dev.absjustcore.provider.utils.LocalResultSet;
import dev.absjustcore.provider.utils.StoreMeta;

public interface Provider {

    void init(final StoreMeta storeMeta);

    void store(StoreMeta storeMeta);

    void storeAsync(StoreMeta storeMeta);

    int storeAndFetch(StoreMeta storeMeta);

    LocalResultSet fetch(StoreMeta storeMeta);

    void close();
}