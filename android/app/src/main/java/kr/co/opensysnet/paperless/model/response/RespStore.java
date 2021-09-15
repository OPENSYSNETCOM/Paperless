package kr.co.opensysnet.paperless.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RespStore<T> {
    @SerializedName("resultCode")
    @Expose
    private int resultCode;
    @SerializedName("resultMessage")
    @Expose
    private String resultMessage;
    @SerializedName("store_cnt")
    @Expose
    private int storeCnt;
    @SerializedName("store_list")
    @Expose
    private List<T> storeList;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }

    public int getStoreCnt() {
        return storeCnt;
    }

    public void setStoreCnt(int storeCnt) {
        this.storeCnt = storeCnt;
    }

    public List<T> getStoreList() {
        return storeList;
    }

    public void setStoreList(List<T> storeList) {
        this.storeList = storeList;
    }
}
