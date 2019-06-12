/*
 * Copyright (c) 2019 Donovan Nelson
 */

package audio;

import net.dv8tion.jda.core.audio.AudioReceiveHandler;
import net.dv8tion.jda.core.audio.CombinedAudio;
import net.dv8tion.jda.core.audio.UserAudio;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static command.FlexiUtils.logger;

public class AudioSaver implements AudioReceiveHandler {
    private List<byte[]> saveQueue = new ArrayList<>();


    @Override
    public boolean canReceiveCombined() {
        return true;
    }

    @Override
    public boolean canReceiveUser() {
        return false;
    }

    @Override
    public void handleCombinedAudio(CombinedAudio combinedAudio) {
        double volume = 1.0D;
        saveQueue.add(combinedAudio.getAudioData(volume));
    }

    @Override
    public void handleUserAudio(UserAudio userAudio) {
    }

    public File save(String guildID) {
        try {
            logger.info("Queue size for " + guildID + ": " + saveQueue.size());
            int size = 0;
            for (byte[] bs : saveQueue) {
                size += bs.length;
            }
            byte[] finalizedArr = new byte[size];
            int i = 0;
            for (byte[] bs : saveQueue) {
                for (byte b : bs) {
                    finalizedArr[i] = b;
                }
            }
            InputStream b_in = new ByteArrayInputStream(finalizedArr);
            File file = File.createTempFile("audioSave-" + guildID, ".wav");
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(
                    "audioSave-" + guildID + ".bin"));
            dos.write(finalizedArr);
            AudioInputStream stream = new AudioInputStream(b_in, AudioReceiveHandler.OUTPUT_FORMAT,
                    finalizedArr.length);
            AudioSystem.write(stream, AudioFileFormat.Type.WAVE, file);
            logger.info("File saved for " + guildID + ": " + file.getName() + ", bytes: "
                    + finalizedArr.length);
            return file;
        } catch (Exception e) {
            logger.error("Exception: " + e);
        }
        return null;
    }

}
