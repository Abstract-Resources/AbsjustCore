package dev.absjustcore.actionlog;

import dev.absjustcore.object.Group;
import dev.absjustcore.object.PermissionHolder;
import dev.absjustcore.sender.AbstractSender;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE) @Getter
public final class LoggedAction {

    private final long timestamp;

    private final String sourceUuid;
    private final String sourceName;

    private final String targetUuid;
    private final String targetName;

    private final Type type;
    private final String action;

    public static @NonNull  Builder builder() {
        return new Builder();
    }

    public final static class Builder {

        private long timestamp = 0L;

        private String sourceUuid = null;
        private String sourceName;

        private String targetUuid = null;
        private String targetName;

        private Type type;
        private String action = null;

        private Builder() {
            // Not allow make instance
        }

        public @NonNull Builder timestamp() {
            return this.timestamp(Instant.now());
        }

        public @NonNull Builder timestamp(@NonNull Instant instant) {
            this.timestamp = instant.getEpochSecond();

            return this;
        }

        public @NonNull Builder source(String sourceUuidParsed, @NonNull String sourceName) {
            this.sourceUuid = sourceUuidParsed != null && sourceUuidParsed.isEmpty() ? null : sourceUuidParsed;

            this.sourceName = sourceName;

            return this;
        }

        public @NonNull Builder source(AbstractSender sender) {
            return this.source(sender.getUniqueId().toString(), sender.getName());
        }

        public @NonNull Builder target(String targetUuidParsed, @NonNull String targetName) {
            this.targetUuid = targetUuidParsed != null && targetUuidParsed.isEmpty() ? null : targetUuidParsed;

            this.targetName = targetName;

            return this;
        }

        public @NonNull Builder target(AbstractSender target) {
            return this.target(target.getUniqueId().toString(), target.getName());
        }

        public @NonNull Builder target(PermissionHolder target) {
            if (target instanceof Group) {
                return this.target(null, ((Group) target).getName()).type(Type.GROUP);
            }

            return this.target(null, "");
        }

        public Builder type(@NonNull Type type) {
            this.type = type;

            return this;
        }

        public @NonNull Builder action(@NonNull Object... args) {
            List<String> list = new LinkedList<>();

            for (Object arg : args) list.add(arg.toString());

            this.action = String.join(" ", list);

            return this;
        }

        public @NonNull LoggedAction build() {
            return new LoggedAction(this.timestamp, this.sourceUuid, this.sourceName, this.targetUuid, this.targetName, this.type, this.action);
        }
    }

    public enum Type {
        USER, GROUP;

        public String toString() {
            return ((Character) (this.equals(Type.GROUP) ? 'G' : 'U')).toString();
        }
    }
}