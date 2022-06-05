package dev.absjustcore.object;

import dev.absjustcore.provider.utils.LocalResultSet;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.Set;

@Getter
public final class Group extends PermissionHolder {

    private final int id;

    @Setter private @NonNull String name;
    @Setter private int priority;

    public Group(int id, @NonNull String name, int priority, MetaData metaData, Set<Permission> permissions) {
        super(metaData, permissions);

        this.id = id;

        this.name = name;

        this.priority = priority;
    }

    public static @NonNull Group fromResult(LocalResultSet resultSet, @NonNull MetaData metaData, @NonNull Set<Permission> permissions) {
        return new Group(
                resultSet.fetchInt("id"),
                resultSet.fetchString("name"),
                resultSet.fetchInt("priority"),
                metaData,
                permissions
        );
    }
}