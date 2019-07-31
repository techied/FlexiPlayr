/*
 * Copyright (c) 2019 Donovan Nelson
 */

package command;

import audio.AudioSaver;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;
import java.io.File;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import static command.FlexiUtils.logger;

public class Record extends Command {
    public Record() {
        identifiers = new String[]{"record", "rec"};
    }

    @Override
    public boolean execute(MessageReceivedEvent event, String[] input) {
        VoiceChannel channel = event.getMember().getVoiceState().getChannel();
        AudioSaver saver = new AudioSaver();
        AudioManager manager = event.getGuild().getAudioManager();
        manager.setSendingHandler(new AudioSendHandler() {
            @Override
            public boolean canProvide() {
                return false;
            }

            @Override
            public ByteBuffer provide20MsAudio() {
                return ByteBuffer.wrap(new byte[0]);
            }
        });
        manager.setReceivingHandler(saver);
        event.getGuild().getAudioManager().openAudioConnection(channel);
        FlexiUtils.waiter.waitForEvent(MessageReceivedEvent.class, evt -> evt.getAuthor().getId().equalsIgnoreCase(event.getAuthor().getId()) && evt.getMessage().getContentRaw().equalsIgnoreCase(FlexiUtils.PREFIX + "rend"), evt -> closeSaveAndSend(evt, saver), 1, TimeUnit.MINUTES, () -> closeSaveAndSend(event, saver));
        return true;
    }

    private void closeSaveAndSend(MessageReceivedEvent event, AudioSaver saver) {
        Message toDel = event.getChannel().sendMessage("\uD83C\uDF99 Processing...").complete();
        event.getChannel().sendTyping().queue();
        event.getGuild().getAudioManager().closeAudioConnection();
        logger.info("Saving audio for " + event.getGuild().getId());
        File file = saver.save(event.getGuild().getId());
        logger.info("Audio saved, uploading using path " + file.getAbsolutePath());
        event.getChannel().sendMessage(new EmbedBuilder().setColor(new Color(0x7289da)).setTitle("\uD83D\uDCBE Saved audio!").build()).addFile(file).queue();
        toDel.delete().queue();
    }
}
