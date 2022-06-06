package dev.absjustcore.commands.group;

import dev.absjustcore.actionlog.LogManager;
import dev.absjustcore.actionlog.LoggedAction;
import dev.absjustcore.command.AbstractArgument;
import dev.absjustcore.factory.GroupFactory;
import dev.absjustcore.object.Group;
import dev.absjustcore.object.MetaData;
import dev.absjustcore.sender.AbstractSender;

import java.util.Arrays;

public final class SetPrefixArgument extends AbstractArgument<Group> {

    public SetPrefixArgument() {
        super("setprefix", "Set prefix", "absjustcore.group.setprefix", "/coreadmin group <group> setprefix <prefix>");
    }

    @Override
    public void execute(AbstractSender sender, String commandLabel, String argumentLabel, Group group, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("COMMAND_INVALID_USAGE", commandLabel, argumentLabel);

            return;
        }

        Object priorityArgument = this.parseArgumentContext("priority", Arrays.copyOfRange(args, 1, args.length));

        if (priorityArgument != null && !(priorityArgument instanceof Integer)) {
            sender.sendMessage("INVALID_ARGUMENT_CONTEXT");

            return;
        }

        int priority = priorityArgument == null ? 0 : (int) priorityArgument;

        if (priority < 0) priority = 0;

        MetaData metaData = group.getMetaData();

        if (metaData.findPrefix(args[0])) {
            // TODO: Stuff

            return;
        }

        metaData.invalidatePrefix(args[0]);
        metaData.recalculate();

        GroupFactory.getInstance().storeMetaData(
                group.getId(),
                LoggedAction.Type.GROUP,
                "prefix",
                args[0]
        );

        LogManager.getInstance().broadcast(LoggedAction.builder()
                .timestamp()
                .source(sender).target(group)
                .type(LoggedAction.Type.GROUP)
                .action("setprefix", args[0])
                .build(),
                sender
        );
        // TODO: Send log action
    }
}