package dev.absjustcore.object;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor @Getter
public final class Permission {

    private final String name;
    private final String serverName;

    @Setter private boolean value;
}