package kr.co.opensysnet.paperless.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RespInstallInfo {
    @SerializedName("seq")
    @Expose
    private int seq;
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("organ")
    @Expose
    private String organ;
    @SerializedName("organ_type")
    @Expose
    private String organType;
    @SerializedName("openday")
    @Expose
    private String openday;
    @SerializedName("setupday")
    @Expose
    private String setupday;
    @SerializedName("work_type")
    @Expose
    private String workType;
    @SerializedName("workdivision")
    @Expose
    private String workdivision;
    @SerializedName("visit_datetime")
    @Expose
    private String visitDatetime;
    @SerializedName("com_datetime")
    @Expose
    private String comDatetime;
    @SerializedName("ce_name")
    @Expose
    private String ceName;
    @SerializedName("owner_name")
    @Expose
    private String ownerName;
    @SerializedName("owner_sign")
    @Expose
    private String ownerSign;
    @SerializedName("oper_name")
    @Expose
    private String operName;
    @SerializedName("oper_sign")
    @Expose
    private String operSign;
    @SerializedName("comp_name")
    @Expose
    private String compName;
    @SerializedName("comp_sign")
    @Expose
    private String compSign;
    @SerializedName("tech_name")
    @Expose
    private String techName;
    @SerializedName("tech_sign")
    @Expose
    private String techSign;
    @SerializedName("create_date")
    @Expose
    private String createDate;
    @SerializedName("dev_list")
    @Expose
    private List<RespInstallInfoDev> devList;
    @SerializedName("sam_info")
    @Expose
    private RespInstallInfoSam samInfo;
    @SerializedName("check_info")
    @Expose
    private RespInstallInfoCheck checkInfo;
    @SerializedName("dongle_list")
    @Expose
    private List<RespInstallInfoDongle> dongleList;

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

    public String getOrganType() {
        return organType;
    }

    public void setOrganType(String organType) {
        this.organType = organType;
    }

    public String getOpenday() {
        return openday;
    }

    public void setOpenday(String openday) {
        this.openday = openday;
    }

    public String getSetupday() {
        return setupday;
    }

    public void setSetupday(String setupday) {
        this.setupday = setupday;
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

    public String getOperName() {
        return operName;
    }

    public void setOperName(String operName) {
        this.operName = operName;
    }

    public String getOperSign() {
        return operSign;
    }

    public void setOperSign(String operSign) {
        this.operSign = operSign;
    }

    public String getCompName() {
        return compName;
    }

    public void setCompName(String compName) {
        this.compName = compName;
    }

    public String getCompSign() {
        return compSign;
    }

    public void setCompSign(String compSign) {
        this.compSign = compSign;
    }

    public String getTechName() {
        return techName;
    }

    public void setTechName(String techName) {
        this.techName = techName;
    }

    public String getTechSign() {
        return techSign;
    }

    public void setTechSign(String techSign) {
        this.techSign = techSign;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public List<RespInstallInfoDev> getDevList() {
        return devList;
    }

    public void setDevList(List<RespInstallInfoDev> devList) {
        this.devList = devList;
    }

    public RespInstallInfoSam getSamInfo() {
        return samInfo;
    }

    public void setSamInfo(RespInstallInfoSam samInfo) {
        this.samInfo = samInfo;
    }

    public RespInstallInfoCheck getCheckInfo() {
        return checkInfo;
    }

    public void setCheckInfo(RespInstallInfoCheck checkInfo) {
        this.checkInfo = checkInfo;
    }

    public List<RespInstallInfoDongle> getDongleList() {
        return dongleList;
    }

    public void setDongleList(List<RespInstallInfoDongle> dongleList) {
        this.dongleList = dongleList;
    }
}
