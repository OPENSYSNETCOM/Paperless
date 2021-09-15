package kr.co.opensysnet.paperless.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RespCheckInfoCheck {
    @SerializedName("check_item")
    @Expose
    private String checkItem;
    @SerializedName("check_order")
    @Expose
    private int checkOrder;

    public String getCheckItem() {
        return checkItem;
    }

    public void setCheckItem(String checkItem) {
        this.checkItem = checkItem;
    }

    public int getCheckOrder() {
        return checkOrder;
    }

    public void setCheckOrder(int checkOrder) {
        this.checkOrder = checkOrder;
    }
}
