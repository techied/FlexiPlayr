/*
 * Copyright (c) 2019 Donovan Nelson
 */

package command;

import com.jagrosh.jdautilities.menu.Paginator;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

public class Help extends Command {

    public Help() {
        identifiers = new String[]{"help", "?"};
    }

    @Override
    public boolean execute(MessageReceivedEvent event, String[] input) {
        Paginator.Builder builder = new Paginator.Builder().setBulkSkipNumber(3).setText("Help commands (My prefix is `>`)").setColor(new Color(0x7289da)).setItemsPerPage(6).setEventWaiter(FlexiUtils.waiter);
        ArrayList<String> commands = new ArrayList<>();
        for (Map.Entry<Command, Integer> command : FlexiUtils.commands.entrySet()) {
            commands.addAll(Arrays.asList(command.getKey().getIdentifiers()));
        }
        Collections.sort(commands);
        builder.addItems(commands.toArray(new String[0])).build().display(event.getTextChannel());
        return true;
    }
}
