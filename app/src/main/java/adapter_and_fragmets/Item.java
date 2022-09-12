package adapter_and_fragmets;

import java.util.List;

public class Item {
    private String itemTitle;
    private List<Recordings> arrRecordings;

    public Item() {
    }

    public Item(String itemTitle, List<Recordings> arrRecordings) {
        this.itemTitle = itemTitle;
        this.arrRecordings = arrRecordings;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public void setItemTitle(String itemTitle) {
        this.itemTitle = itemTitle;
    }

    public List<Recordings> getArrRecordings() {
        return arrRecordings;
    }

    public void setArrRecordings(List<Recordings> arrRecordings) {
        this.arrRecordings = arrRecordings;
    }
}
