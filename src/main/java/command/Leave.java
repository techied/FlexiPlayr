/*
 * Copyright (c) 2019 Donovan Nelson
 */

package command;


import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public class Leave extends Command {
    public Leave() {
        identifiers = new String[]{"leave"};
    }

    @Override
    public boolean execute(MessageReceivedEvent event, String[] input) {
        event.getGuild().getAudioManager().closeAudioConnection();
        event.getChannel().sendMessage("<a:ablobleavebot:585585375100272640> Left the voice channel!").queue();
        return true;
    }
}
