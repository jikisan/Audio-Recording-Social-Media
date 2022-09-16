package adapter_and_fragmets;

public class Users {



    String fullName;
    String userId;
    String firstName;
    String lastName;
    String contactNum;
    String email;
    String password;
    String imageName;
    String imageUrl;
    int followers;

    public Users(){}

    public Users(String fullName, String userId, String firstName, String lastName, String contactNum, String email, String password, String imageName, String imageUrl, int followers) {
        this.fullName = fullName;
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.contactNum = contactNum;
        this.email = email;
        this.password = password;
        this.imageName = imageName;
        this.imageUrl = imageUrl;
        this.followers = followers;
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

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getContactNum() {
        return contactNum;
    }

    public void setContactNum(String contactNum) {
        this.contactNum = contactNum;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }
}
