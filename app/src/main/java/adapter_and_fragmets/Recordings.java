package adapter_and_fragmets;

public class Recordings {

    String audioLink;
    String audioName;
    String userID;
    String dateTime;

    public Recordings() {
    }

    public Recordings(String audioLink, String audioName, String userID, String dateTime) {
        this.audioLink = audioLink;
        this.audioName = audioName;
        this.userID = userID;
        this.dateTime = dateTime;
    }

    public String getAudioLink() {
        return audioLink;
    }

    public void setAudioLink(String audioLink) {
        this.audioLink = audioLink;
    }

    public String getAudioName() {
        return audioName;
    }

    public void setAudioName(String audioName) {
        this.audioName = audioName;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
