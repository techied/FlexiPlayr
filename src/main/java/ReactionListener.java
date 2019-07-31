/*
 * Copyright (c) 2019 Donovan Nelson
 */

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import static command.FlexiUtils.getGuildAudioPlayer;

public class ReactionListener extends ListenerAdapter {

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        Message message = event.getTextChannel().retrieveMessageById(event.getMessageId()).complete();
        if (message.getEmbeds().size() > 0 && message.getEmbeds().get(0).getTitle().equals("Player Menu") && message.getAuthor().getId().equals(event.getJDA().getSelfUser().getId()) && !event.getMember().getUser().isBot()) {
            System.out.println(event.getReaction().getReactionEmote().getName());
            switch (event.getReactionEmote().getName()) {
                case "\u23EF": {
                    event.getReaction().removeReaction(event.getUser()).queue();
                    getGuildAudioPlayer(event.getGuild()).scheduler.pause();
                }
                case "\u23E9": {
                    event.getReaction().removeReaction(event.getUser()).queue();
                    getGuildAudioPlayer(event.getGuild()).scheduler.nextTrack();
                }
                case "\u23F9": {
                    event.getReaction().removeReaction(event.getUser()).queue();
                    getGuildAudioPlayer(event.getGuild()).scheduler.clear();
                    getGuildAudioPlayer(event.getGuild()).scheduler.nextTrack();
                }
            }
        }
    }
}
