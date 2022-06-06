package dev.absjustcore.commands.group;

import dev.absjustcore.TaskUtils;
import dev.absjustcore.actionlog.LogManager;
import dev.absjustcore.actionlog.LoggedAction;
import dev.absjustcore.command.AbstractArgument;
import dev.absjustcore.factory.GroupFactory;
import dev.absjustcore.object.Group;
import dev.absjustcore.sender.AbstractSender;

public final class CreateGroupArgument extends AbstractArgument<Void> {

    public CreateGroupArgument(String name, String description, String permission, String usage) {
        super(name, description, permission, usage);
    }

    @Override
    public void execute(AbstractSender sender, String commandLabel, String argumentLabel, Void ignored, String[] args) {
        if (args.length == 0) {
            sender.sendMessage("GROUP_CREATE_USAGE", commandLabel);

            return;
        }

        GroupFactory factory = GroupFactory.getInstance();

        if (factory.getGroup(args[0]) != null) {
            sender.sendMessage("GROUP_ALREADY_EXISTS", args[0]);

            return;
        }

        TaskUtils.runAsync(() -> {
            Group group = factory.storeGroup(args[0], 0);

            if (group == null) {
                sender.sendMessage("&cAn error occurred when tried insert a group into database");

                return;
            }

            factory.setGroup(group);

            LogManager.getInstance().broadcast(LoggedAction.builder()
                    .timestamp()
                    .source(sender).target(group)
                    .type(LoggedAction.Type.GROUP)
                    .action("create")
                    .build(),
                    sender
            );
        });
    }
}