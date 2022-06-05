package dev.absjustcore.object;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.Set;

@RequiredArgsConstructor
public abstract class PermissionHolder {

    @Getter private final MetaData metaData;

    private final Set<Permission> permissions;

    public Permission getPermission(String name) {
        return this.permissions.stream()
                .filter(permission -> permission.getName().equals(name))
                .findFirst().orElse(null);
    }

    public Set<Permission> getPermissions() {
        return Collections.unmodifiableSet(this.permissions);
    }
}