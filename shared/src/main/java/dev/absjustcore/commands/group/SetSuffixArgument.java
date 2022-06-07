package dev.absjustcore.commands.group;

import dev.absjustcore.TaskUtils;
import dev.absjustcore.actionlog.LogManager;
import dev.absjustcore.actionlog.LoggedAction;
import dev.absjustcore.command.AbstractArgument;
import dev.absjustcore.factory.GroupFactory;
import dev.absjustcore.object.Group;
import dev.absjustcore.object.MetaData;
import dev.absjustcore.sender.AbstractSender;

import java.util.Arrays;

public final class SetSuffixArgument extends AbstractArgument<Group> {

    public SetSuffixArgument() {
        super("setsuffix", "Set suffix", "absjustcore.group.setsuffix", "/coreadmin group <group> setsuffix <suffix>");
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

        metaData.invalidateSuffix(args[0]);
        metaData.recalculate();

        TaskUtils.runAsync(() -> {
            GroupFactory.getInstance().deleteMetaData(group.getId(), "suffix");

            GroupFactory.getInstance().storeMetaData(
                    group.getId(),
                    LoggedAction.Type.GROUP,
                    "suffix",
                    args[0]
            );
        });

        LogManager.getInstance().broadcast(LoggedAction.builder()
                .timestamp()
                .source(sender).target(group)
                .type(LoggedAction.Type.GROUP)
                .action("setsuffix", args[0])
                .build(),
                sender
        );
    }
}