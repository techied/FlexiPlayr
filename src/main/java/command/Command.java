/*
 * Copyright (c) 2019 Donovan Nelson
 */

package command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public abstract class Command {
    String[] identifiers;

    public abstract boolean execute(MessageReceivedEvent event, String[] input);

    public String[] getIdentifiers(){
        return identifiers;
    }
}
