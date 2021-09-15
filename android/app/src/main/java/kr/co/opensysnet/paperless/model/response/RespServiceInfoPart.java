package kr.co.opensysnet.paperless.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RespServiceInfoPart {
    @SerializedName("part_name")
    @Expose
    private String partName;
    @SerializedName("part_num")
    @Expose
    private int partNum;

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public int getPartNum() {
        return partNum;
    }

    public void setPartNum(int partNum) {
        this.partNum = partNum;
    }
}
