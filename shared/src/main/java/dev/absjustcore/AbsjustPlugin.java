package dev.absjustcore;

import dev.absjustcore.provider.Provider;
import dev.absjustcore.sender.AbstractSender;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

public final class AbsjustPlugin {

    @Getter private final static AbsjustPlugin instance = new AbsjustPlugin();
    @Getter private static final Logger logger = LogManager.getLogger("AbsjustCore");

    @Getter @Setter private static Provider provider;

    @Setter private @NonNull Callable<Boolean> futurePrimaryThread;
    @Setter private @NonNull Callable<Stream<AbstractSender>> futureOnlineSenders;

    @Setter private boolean storeLog = false;
    @Setter private boolean logRemote = false;

    public boolean isPrimaryThread() {
        try {
            return this.futurePrimaryThread.call();
        } catch (Exception e) {
            return false;
        }
    }

    public @Nullable Stream<AbstractSender> getOnlineSenders() {
        try {
            return this.futureOnlineSenders.call();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean storeLog() {
        return this.storeLog;
    }

    public boolean logRemote() {
        return this.logRemote;
    }
}