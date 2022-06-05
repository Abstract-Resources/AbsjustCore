package dev.absjustcore.provider;

import dev.absjustcore.provider.utils.StoreMeta;

import java.util.Map;

public interface Provider {

    void init(final StoreMeta storeMeta);

    void store(StoreMeta storeMeta);

    Object fetch(StoreMeta storeMeta);

    void close();
}