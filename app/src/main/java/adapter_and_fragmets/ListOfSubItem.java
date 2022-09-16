package adapter_and_fragmets;

import java.util.List;

public class ListOfSubItem {

    List<SubItem> subItemList;

    public ListOfSubItem() {
    }

    public ListOfSubItem(List<SubItem> subItemList) {
        this.subItemList = subItemList;
    }

    public List<SubItem> getSubItemList() {
        return subItemList;
    }

    public void setSubItemList(List<SubItem> subItemList) {
        this.subItemList = subItemList;
    }
}
