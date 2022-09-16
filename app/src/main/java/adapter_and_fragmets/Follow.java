package adapter_and_fragmets;

public class Follow {

    String followers;
    String beingFollowed;

    public Follow() {
    }

    public Follow(String followers, String beingFollowed) {
        this.followers = followers;
        this.beingFollowed = beingFollowed;
    }

    public String getFollowers() {
        return followers;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }

    public String getBeingFollowed() {
        return beingFollowed;
    }

    public void setBeingFollowed(String beingFollowed) {
        this.beingFollowed = beingFollowed;
    }
}
