package dev.absjustcore.command;

import dev.absjustcore.commands.group.CreateGroupArgument;
import dev.absjustcore.commands.group.GroupParentCommand;
import dev.absjustcore.sender.AbstractSender;
import lombok.Getter;
import lombok.NonNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class CoreAdminCommand {

    @Getter private final static CoreAdminCommand instance = new CoreAdminCommand();

    private final Set<AbstractArgument<Void>> abstractArguments = new HashSet<>();

    public CoreAdminCommand() {
        this.addArgument(
                new CreateGroupArgument("creategroup", "Create a new group", "absjustcore.command.creategroup", null),
                new GroupParentCommand()
        );
    }

    @SafeVarargs
    private final void addArgument(AbstractArgument<Void>... abstractArguments) {
        this.abstractArguments.addAll(Arrays.asList(abstractArguments));
    }

    private AbstractArgument<Void> getArgument(String argumentLabel) {
        return this.abstractArguments.stream()
                .filter(abstractArgument -> abstractArgument.getName().equalsIgnoreCase(argumentLabel))
                .findFirst()
                .orElse(null);
    }

    public void execute(@NonNull AbstractSender sender, @NonNull String commandLabel, String argumentLabel, String[] args) {
        if (!sender.hasPermission("absjustcore.command")) {
            sender.sendMessage("&cYou don't have permissions to use this command.");

            return;
        }

        if (args.length == 0) {
            sender.sendMessage("&cUse /" + commandLabel + " help");

            return;
        }

        AbstractArgument<Void> argument = this.getArgument(args[0]);

        if (argument == null) {
            // TODO: Send message help

            return;
        }

        if (!argument.authorized(sender)) {
            sender.sendMessage("&cYou don't have permissions to use this command.");

            return;
        }

        argument.execute(sender, commandLabel, argumentLabel, null, Arrays.copyOfRange(args, 1, args.length));
    }
}