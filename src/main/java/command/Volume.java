/*
 * Copyright (c) 2019 Donovan Nelson
 */

package command;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class Volume extends Command {
    public Volume() {
        identifiers = new String[]{"vol", "volume"};
    }

    @Override
    public boolean execute(MessageReceivedEvent event, String[] input) {
        if (input.length != 2) {
            return false;
        }
        FlexiUtils.api.hasVoted(event.getAuthor().getId()).whenComplete((hasVoted, e) -> {
            if (hasVoted) {
                FlexiUtils.getGuildAudioPlayer(event.getGuild()).getPlayer().setVolume(Integer.parseInt(input[1]));
                event.getMessage().addReaction("\uD83D\uDC4C").queue();
            } else {
                Message message = event.getChannel().sendMessage(new EmbedBuilder()
                        .setTitle("This command requires voting!")
                        .setColor(Color.RED)
                        .setDescription("Since this command requires more processing power than usual, we kindly ask that you vote for free [here](https://discordbots.org/bot/339215794418352129). It only takes a few seconds, I promise. As an alternative, you can donate [here](https://discord.gg/EkaAdNj) to permanently gain access to this command.\n\nOnce you've voted simply add the ✅ reaction to this message")
                        .setAuthor("Vote", null, "https://cdn2.iconfinder.com/data/icons/freecns-cumulus/32/519791-101_Warning-512.png").build()).complete();
                message.addReaction("✅").complete();
                FlexiUtils.waiter.waitForEvent(MessageReactionAddEvent.class, n -> (n.getUser().getIdLong() == event.getAuthor().getIdLong()) && (n.getMessageId().equals(message.getId())), evt -> FlexiUtils.api.hasVoted(event.getAuthor().getId()).whenComplete((voted2, e2) -> {
                    if (voted2) {
                        message.editMessage(new EmbedBuilder()
                                .setTitle("Set volume!")
                                .setColor(Color.GREEN).build()).queue();
                        message.clearReactions().queue();
                        FlexiUtils.getGuildAudioPlayer(event.getGuild()).getPlayer().setVolume(Integer.parseInt(input[1]));
                    } else {
                        message.editMessage(new EmbedBuilder()
                                .setTitle("Couldn't detect a vote. Please try again later.")
                                .setColor(Color.RED).build()).queue();
                        message.clearReactions().queue();
                    }
                }), 5, TimeUnit.MINUTES, () -> {
                    message.editMessage(new EmbedBuilder()
                            .setTitle("Message expired")
                            .setColor(Color.RED).build()).queue();
                    message.clearReactions().queue();
                });
            }
        });

        return true;
    }
}
