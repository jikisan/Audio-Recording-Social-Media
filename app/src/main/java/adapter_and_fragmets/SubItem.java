package adapter_and_fragmets;

import java.util.List;

public class SubItem {

    String audioName;
    String dateTime;
    String audioLink;
    int recordingCount;

    public SubItem() {
    }

    public SubItem(String audioName, String dateTime, String audioLink, int recordingCount) {
        this.audioName = audioName;
        this.dateTime = dateTime;
        this.audioLink = audioLink;
        this.recordingCount = recordingCount;
    }

    public String getAudioName() {
        return audioName;
    }

    public void setAudioName(String audioName) {
        this.audioName = audioName;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getAudioLink() {
        return audioLink;
    }

    public void setAudioLink(String audioLink) {
        this.audioLink = audioLink;
    }

    public int getRecordingCount() {
        return recordingCount;
    }

    public void setRecordingCount(int recordingCount) {
        this.recordingCount = recordingCount;
    }
}
