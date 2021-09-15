package kr.co.opensysnet.paperless.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RespCheckInfo<T> {
    @SerializedName("dev_name")
    @Expose
    private String devName;
    @SerializedName("check_list")
    @Expose
    private List<T> checkList;

    public String getDevName() {
        return devName;
    }

    public void setDevName(String devName) {
        this.devName = devName;
    }

    public List<T> getCheckList() {
        return checkList;
    }

    public void setCheckList(List<T> checkList) {
        this.checkList = checkList;
    }
}
