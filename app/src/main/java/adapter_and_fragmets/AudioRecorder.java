package adapter_and_fragmets;

import android.media.MediaRecorder;

import java.io.IOException;

public class AudioRecorder {

    public MediaRecorder mediaRecorder;

    public void initMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
    }


    public void start(String filePath) throws IOException {
        if (mediaRecorder == null) {
            initMediaRecorder();
        }

        mediaRecorder.setOutputFile(filePath);
        mediaRecorder.prepare();
        mediaRecorder.start();
    }

    public void stop() {
        try {
            mediaRecorder.stop();
            destroyMediaRecorder();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void destroyMediaRecorder() {
        mediaRecorder.release();
        mediaRecorder = null;
    }
}
