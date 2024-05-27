package android.org.firebasetest;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.widget.Toast;

import java.io.IOException;

public class VoiceHandler {
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private Context context;
    private String audioFilePath;

    public VoiceHandler(Context context){
        this.context = context;
    }

    public void startRecording() {
        try {
            audioFilePath = context.getExternalCacheDir().getAbsolutePath() + "/recorded_audio.3gp";
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mediaRecorder.setOutputFile(audioFilePath);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.prepare();
            mediaRecorder.start();
            Toast.makeText(context, "Recording started", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        if (mediaRecorder != null) {
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            Toast.makeText(context, "Recording stopped", Toast.LENGTH_SHORT).show();
        }
    }

    public void playAudio() {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(audioFilePath);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getAudioFilePath() {
        return audioFilePath;
    }
}
