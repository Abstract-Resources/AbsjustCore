package dev.absjustcore.command;

import dev.absjustcore.sender.AbstractSender;

import java.util.Arrays;
import java.util.Set;

public abstract class AbstractParentCommand<T> extends AbstractArgument<Void> {

    protected final Type type;
    protected final Set<AbstractArgument<T>> arguments;

    public AbstractParentCommand(String name, String permission, Type type, Set<AbstractArgument<T>> arguments) {
        super(name, "", permission, "");

        this.type = type;

        this.arguments =arguments;
    }

    public void execute(AbstractSender sender, String commandLabel, String argumentLabel, Void ignored, String[] args) {
        if (args.length < this.type.minArgs) {
            this.sendUsage(sender);

            return;
        }

        AbstractArgument<T> argument = this.arguments.stream()
                .filter(arg -> arg.getName().equalsIgnoreCase(args[this.type.cmdIndex]))
                .findFirst()
                .orElse(null);

        if (argument == null) {
            sender.sendMessage("COMMAND_INVALID_USAGE", commandLabel, argumentLabel);

            return;
        }

        if (!argument.authorized(sender)) {
            sender.sendMessage("COMMAND_NO_PERMISSION");

            return;
        }

        T target = null;

        if (this.type == Type.TAKES_ARGUMENT_FOR_TARGET) {
            if ((target = this.getTarget(sender, args[0])) == null) {
                return;
            }
        }

        argument.execute(sender, commandLabel, argumentLabel, target, this.type == Type.TAKES_ARGUMENT_FOR_TARGET ? Arrays.copyOfRange(args, this.type.minArgs, args.length) : args);
    }

    private void sendUsage(AbstractSender sender) {

    }

    protected abstract T getTarget(AbstractSender sender, String targetParsed);

    public enum Type {
        // e.g. /ca log sub-command....
        NO_TARGET_ARGUMENT(0),
        // e.g. /ca group <GROUP> sub-command....
        TAKES_ARGUMENT_FOR_TARGET(1);

        private final int cmdIndex;
        private final int minArgs;

        Type(int cmdIndex) {
            this.cmdIndex = cmdIndex;
            this.minArgs = cmdIndex + 1;
        }
    }
}