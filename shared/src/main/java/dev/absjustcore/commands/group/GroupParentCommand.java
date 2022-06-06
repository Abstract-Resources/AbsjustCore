package dev.absjustcore.commands.group;

import com.google.common.collect.ImmutableSet;
import dev.absjustcore.command.AbstractArgument;
import dev.absjustcore.command.AbstractParentCommand;
import dev.absjustcore.factory.GroupFactory;
import dev.absjustcore.object.Group;
import dev.absjustcore.sender.AbstractSender;

public final class GroupParentCommand extends AbstractParentCommand<Group> {

    public GroupParentCommand() {
        super("group", "absjustcore.group.command", Type.TAKES_ARGUMENT_FOR_TARGET, ImmutableSet.<AbstractArgument<Group>>builder()
                .add(new SetPrefixArgument())
                .build());
    }

    @Override
    protected Group getTarget(AbstractSender sender, String targetParsed) {
        Group group = GroupFactory.getInstance().getGroup(targetParsed);

        if (group == null) {
            sender.sendMessage("GROUP_NOT_FOUND", targetParsed);
        }

        return group;
    }
}