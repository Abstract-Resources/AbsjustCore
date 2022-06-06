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
                    .append("timestamp", entry.getTimestamp())
                    .append("source_uuid", entry.getSourceUuid())
                    .append("source_name", entry.getSourceName())
                    .append("type", entry.getType())
                    .append("target_uuid", entry.getTargetUuid())
                    .append("target_name", entry.getTargetName())
                    .append("action", entry.getAction())
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