package dev.absjustcore.object;

import dev.absjustcore.provider.utils.LocalResultSet;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor @Getter
public final class Permission {

    private final String name;
    private final String serverName;

    @Setter private boolean value;

    public Permission(String name, String serverName, boolean value) {
        this.name = name;

        this.serverName = serverName;

        this.value = value;
    }

    public static Permission fromResult(LocalResultSet resultSet) {
        return new Permission(
                resultSet.fetchString("name"),
                resultSet.fetchString("server_name"),
                resultSet.fetchBoolean("value")
        );
    }
}