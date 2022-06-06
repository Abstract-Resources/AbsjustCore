package dev.absjustcore.actionlog;

import dev.absjustcore.AbsjustPlugin;
import dev.absjustcore.provider.utils.StoreMeta;
import dev.absjustcore.sender.AbstractSender;
import lombok.Getter;
import lombok.NonNull;

import java.util.stream.Stream;

public final class LogManager {

    @Getter private final static LogManager instance = new LogManager();

    public void broadcast(@NonNull LoggedAction entry, @NonNull AbstractSender sender) {
        if (AbsjustPlugin.getInstance().storeLog()) {
            AbsjustPlugin.getProvider().storeAndFetch(StoreMeta.builder()
                    .collection("group_logs")
                    .statement("LOGS_INSERT")
                    .append(1, "timestamp", entry.getTimestamp())
                    .append(2, "source_uuid", entry.getSourceUuid())
                    .append(3, "source_name", entry.getSourceName())
                    .append(4, "type", entry.getType().toString())
                    .append(5, "target_uuid", entry.getTargetUuid())
                    .append(6, "target_name", entry.getTargetName())
                    .append(7, "action", entry.getAction())
                    .build()
            );
        }

        if (AbsjustPlugin.getInstance().logRemote()) {
            // TODO: Send the redis packet of log action
            //RedisProvider.getInstance().redisMessage();
        }

        this.broadcast(entry);
    }

    public void broadcast(@NonNull LoggedAction entry) {
        Stream<AbstractSender> onlineSenders = AbsjustPlugin.getInstance().getOnlineSenders();

        if (onlineSenders == null) return;

        onlineSenders.filter(sender -> sender.hasPermission("log.notify"))
                .forEach(sender -> sender.sendMessage("LOG_NOTIFY", entry.getSourceName(), entry.getTargetName(), entry.getAction()));
    }
}