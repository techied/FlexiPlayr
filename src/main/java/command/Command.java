package command;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;

public abstract class Command {
    String[] identifiers;
    public abstract boolean execute(MessageReceivedEvent event, String[] input);
    public String[] getIdentifiers(){
        return identifiers;
    }
}
