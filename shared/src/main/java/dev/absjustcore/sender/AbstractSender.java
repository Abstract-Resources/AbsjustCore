package dev.absjustcore.sender;

import java.util.UUID;

public abstract class AbstractSender {

    public abstract String getName();

    public abstract UUID getUniqueId();
;
    public abstract boolean hasPermission(String permission);

    public abstract void sendMessage(String k, String... args);
}