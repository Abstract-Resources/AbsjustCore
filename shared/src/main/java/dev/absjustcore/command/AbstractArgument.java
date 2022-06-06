package dev.absjustcore.command;

import dev.absjustcore.sender.AbstractSender;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor @Getter
public abstract class AbstractArgument<T> {

    private final String name;
    private final String description;
    private final String permission;
    private final String usage;

    public abstract void execute(AbstractSender sender, String commandLabel, String argumentLabel, T target, String[] args);

    public boolean authorized(AbstractSender sender) {
        return this.permission == null || sender.hasPermission(this.permission);
    }

    public Object parseArgumentContext(String type, String[] args) {
        for (String entry : args) {
            // TODO: Get the argument context
        }

        return null;
    }
}