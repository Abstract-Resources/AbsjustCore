package dev.absjustcore.factory;

import dev.absjustcore.AbsjustPlugin;
import dev.absjustcore.actionlog.LoggedAction;
import dev.absjustcore.object.Group;
import dev.absjustcore.object.MetaData;
import dev.absjustcore.object.Permission;
import dev.absjustcore.provider.utils.LocalResultSet;
import dev.absjustcore.provider.utils.StoreMeta;
import lombok.Getter;
import lombok.NonNull;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

public final class GroupFactory {

    @Getter private final static GroupFactory instance = new GroupFactory();

    private final Set<Group> groups = new HashSet<>();

    public void init() {
        LocalResultSet resultSet = AbsjustPlugin.getProvider().fetch(StoreMeta.builder()
                .collection("groups")
                .statement("GROUP_SELECT_ALL")
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
        this.groups.add(group);
    }

    public Group getGroup(String name) {
        return this.groups.stream()
                .filter(group -> group.getName().equalsIgnoreCase(name))
                .findAny().orElse(null);
    }

    public @Nullable Group storeGroup(@NonNull String name, int priority) {
        int id = AbsjustPlugin.getProvider().storeAndFetch(StoreMeta.builder()
                .collection("groups")
                .statement("GROUP_INSERT")
                .append(1, "name", name)
                .append(2, "priority", priority)
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
                .append(1, "name", name) // TODO: here i need specify the index "name" because is needed with mongodb
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

    public void storeMetaData(int targetId, LoggedAction.Type type, String context, String value) {
        AbsjustPlugin.getProvider().storeAsync(StoreMeta.builder()
                .collection("node_meta")
                .statement("NODE_META_INSERT")
                .append(1, "targetId", targetId)
                .append(2, "type", type.toString())
                .append(3, "context", context)
                .append(4, "value", value)
                .append(5, "created_at", Instant.now().getEpochSecond())
                .build()
        );
    }

    private @NonNull MetaData getMetaData(int targetId) {
        LocalResultSet resultSet = AbsjustPlugin.getProvider().fetch(StoreMeta.builder()
                .collection("node_meta")
                .statement("NODE_META_SELECT_ALL_BY_TARGET_ID")
                .append(1, "targetId", targetId)
                .build()
        );

        if (resultSet == null || !resultSet.next()) {
            return MetaData.empty();
        }

        Set<String> prefixes = new HashSet<>();
        Set<String> suffixes = new HashSet<>();

        while (resultSet.next()) {
            String context = resultSet.fetchString("context");
            String value = resultSet.fetchString("value");

            if (context.equalsIgnoreCase("prefix")) {
                prefixes.add(value);
            } else {
                suffixes.add(value);
            }
        }

        resultSet.invalidate();

        return new MetaData(
                prefixes.stream().findFirst().orElse(null),
                suffixes.stream().findFirst().orElse(null),
                prefixes,
                suffixes
        );
    }

    public @NonNull Set<Permission> getPermissions(int targetId) {
        LocalResultSet resultSet = AbsjustPlugin.getProvider().fetch(StoreMeta.builder()
                .collection("permissions")
                .statement("PERMISSIONS_SELECT_ALL")
                .append(1, "targetId", targetId)
                .build()
        );

        if (resultSet == null) return new HashSet<>();

        Set<Permission> permissions = new HashSet<>();

        while (resultSet.next()) {
            permissions.add(Permission.fromResult(resultSet));
        }

        resultSet.invalidate();

        return permissions;
    }
}