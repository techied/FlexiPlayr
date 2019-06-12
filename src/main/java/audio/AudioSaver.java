/*
 * Copyright (c) 2019 Donovan Nelson
 */

package audio;

import net.dv8tion.jda.core.audio.AudioReceiveHandler;
import net.dv8tion.jda.core.audio.CombinedAudio;
import net.dv8tion.jda.core.audio.UserAudio;
import net.dv8tion.jda.core.entities.User;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AudioSaver implements AudioReceiveHandler {
    private ConcurrentLinkedQueue<byte[]> saveQueue = new ConcurrentLinkedQueue<>();


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
        for (User user : combinedAudio.getUsers()) {
            System.out.println(user.getId());
        }
        double volume = 1.0D;
        saveQueue.add(combinedAudio.getAudioData(volume));

        System.out.println("ADDED!");
    }

    @Override
    public void handleUserAudio(UserAudio userAudio) {
    }

    public File save(String guildID) {
        try {
            ArrayList<Byte> bytesProper = new ArrayList<>();
            for (int i = 0; i < saveQueue.size(); i++) {
                for (byte b : saveQueue.poll()) {
                    bytesProper.add(b);
                    System.out.println(Integer.toBinaryString(b));
                }
            }
            System.out.println(saveQueue.size());
            byte[] finalizedArr = new byte[bytesProper.size()];
            for (int i = 0; i < bytesProper.size(); i++) {
                finalizedArr[i] = bytesProper.get(i);
            }
            InputStream b_in = new ByteArrayInputStream(finalizedArr);
            File file = File.createTempFile("audioSave-" + guildID, ".wav");
            DataOutputStream dos = new DataOutputStream(new FileOutputStream(
                    "audioSave-" + guildID + ".bin"));
            dos.write(finalizedArr);
            AudioInputStream stream = new AudioInputStream(b_in, AudioReceiveHandler.OUTPUT_FORMAT,
                    finalizedArr.length);
            AudioSystem.write(stream, AudioFileFormat.Type.WAVE, file);
            System.out.println("File saved: " + file.getName() + ", bytes: "
                    + finalizedArr.length);
            return file;
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
        return null;
    }

}
