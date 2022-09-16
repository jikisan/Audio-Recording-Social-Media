package adapter_and_fragmets;

import java.util.List;

public class Item {

    String imageUrl ;
    String fullName ;
    String userId ;
    int followers;
    List<SubItem> subItemList;

    public Item() {
    }

    public Item(String imageUrl, String fullName, String userId, int followers, List<SubItem> subItemList) {
        this.imageUrl = imageUrl;
        this.fullName = fullName;
        this.userId = userId;
        this.followers = followers;
        this.subItemList = subItemList;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public List<SubItem> getSubItemList() {
        return subItemList;
    }

    public void setSubItemList(List<SubItem> subItemList) {
        this.subItemList = subItemList;
    }
}
