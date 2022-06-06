package dev.absjustcore.provider.utils;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor (access = AccessLevel.PRIVATE) @Builder (builderClassName = "Builder") @Getter
public final class StoreValue {

    @Getter private final int id;

    private final String key;
    private final Object value;
}