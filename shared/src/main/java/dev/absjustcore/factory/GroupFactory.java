package dev.absjustcore.factory;

import dev.absjustcore.AbsjustPlugin;
import dev.absjustcore.object.Group;
import dev.absjustcore.object.MetaData;
import dev.absjustcore.object.Permission;
import dev.absjustcore.provider.utils.LocalResultSet;
import dev.absjustcore.provider.utils.StoreMeta;
import lombok.Getter;
import lombok.NonNull;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;

public final class GroupFactory {

    @Getter private final static GroupFactory instance = new GroupFactory();

    public void init() {
        LocalResultSet resultSet = AbsjustPlugin.getProvider().fetch(StoreMeta.builder()
                .collection("groups")
                .statement("GROUPS_SELECT_ALL")
                .build()
        );

        if (resultSet == null) {
            AbsjustPlugin.getLogger().log(Level.FATAL, "Error trying fetch all groups and result received null");

            return;
        }

        while (resultSet.next()) {
            this.setGroup(Group.fromResult(
                    resultSet,
                    this.getMetaData(resultSet.fetchInt("id")),
                    this.getPermissions(resultSet.fetchInt("id"))
            ));
        }
    }

    public void setGroup(Group group) {

    }

    public @Nullable Group storeGroup(@NonNull String name, int priority) {
        int id = AbsjustPlugin.getProvider().storeAndFetch(StoreMeta.builder()
                .append("name", name)
                .append("priority", priority)
                .build()
        );

        if (id == -1) return null;

        return new Group(
                id,
                name,
                priority,
                MetaData.empty(),
                new HashSet<>()
        );
    }

    public Group loadGroup(@NonNull String name) {
        LocalResultSet resultSet = AbsjustPlugin.getProvider().fetch(StoreMeta.builder()
                .collection("groups")
                .statement("GROUP_SELECT")
                .append("name", name) // TODO: here i need specify the index "name" because is needed with mongodb
                .build()
        );

        if (resultSet == null || !resultSet.next()) return null;

        try {
            return Group.fromResult(
                    resultSet,
                    this.getMetaData(resultSet.fetchInt("id")),
                    this.getPermissions(resultSet.fetchInt("id"))
            );
        } finally {
            resultSet.invalidate();
        }
    }

    private @NonNull MetaData getMetaData(int id) {
        return MetaData.empty();
    }

    public @NonNull Set<Permission> getPermissions(int id) {
        return null;
    }
}