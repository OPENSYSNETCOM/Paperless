package kr.co.opensysnet.paperless.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RespServiceInfo {
    @SerializedName("seq")
    @Expose
    private int seq;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("organ")
    @Expose
    private String organ;
    @SerializedName("work_type")
    @Expose
    private String workType;
    @SerializedName("workdivision")
    @Expose
    private String workdivision;
    @SerializedName("receipt_datetime")
    @Expose
    private String receiptDatetime;
    @SerializedName("visit_datetime")
    @Expose
    private String visitDatetime;
    @SerializedName("com_datetime")
    @Expose
    private String comDatetime;
    @SerializedName("ce_name")
    @Expose
    private String ceName;
    @SerializedName("ce_sign")
    @Expose
    private String ceSign;
    @SerializedName("owner_name")
    @Expose
    private String ownerName;
    @SerializedName("owner_sign")
    @Expose
    private String ownerSign;
    @SerializedName("owner_comment")
    @Expose
    private String ownerComment;
    @SerializedName("create_date")
    @Expose
    private String createDate;
    @SerializedName("dev_list")
    @Expose
    private List<RespServiceInfoDev> devList;
    @SerializedName("fix_list")
    @Expose
    private List<RespServiceInfoFix> fixList;
    @SerializedName("part_list")
    @Expose
    private List<RespServiceInfoPart> partList;

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getOrgan() {
        return organ;
    }

    public void setOrgan(String organ) {
        this.organ = organ;
    }

    public String getWorkType() {
        return workType;
    }

    public void setWorkType(String workType) {
        this.workType = workType;
    }

    public String getWorkdivision() {
        return workdivision;
    }

    public void setWorkdivision(String workdivision) {
        this.workdivision = workdivision;
    }

    public String getReceiptDatetime() {
        return receiptDatetime;
    }

    public void setReceiptDatetime(String receiptDatetime) {
        this.receiptDatetime = receiptDatetime;
    }

    public String getVisitDatetime() {
        return visitDatetime;
    }

    public void setVisitDatetime(String visitDatetime) {
        this.visitDatetime = visitDatetime;
    }

    public String getComDatetime() {
        return comDatetime;
    }

    public void setComDatetime(String comDatetime) {
        this.comDatetime = comDatetime;
    }

    public String getCeName() {
        return ceName;
    }

    public void setCeName(String ceName) {
        this.ceName = ceName;
    }

    public String getCeSign() {
        return ceSign;
    }

    public void setCeSign(String ceSign) {
        this.ceSign = ceSign;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerSign() {
        return ownerSign;
    }

    public void setOwnerSign(String ownerSign) {
        this.ownerSign = ownerSign;
    }

    public String getOwnerComment() {
        return ownerComment;
    }

    public void setOwnerComment(String ownerComment) {
        this.ownerComment = ownerComment;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public List<RespServiceInfoDev> getDevList() {
        return devList;
    }

    public void setDevList(List<RespServiceInfoDev> devList) {
        this.devList = devList;
    }

    public List<RespServiceInfoFix> getFixList() {
        return fixList;
    }

    public void setFixList(List<RespServiceInfoFix> fixList) {
        this.fixList = fixList;
    }

    public List<RespServiceInfoPart> getPartList() {
        return partList;
    }

    public void setPartList(List<RespServiceInfoPart> partList) {
        this.partList = partList;
    }
}
