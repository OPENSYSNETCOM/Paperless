package kr.co.opensysnet.paperless.model.request;

import java.util.List;

public class ReqInstall {
    private int seq;
    private String code;
    private String organ;
    private String organ_type;
    private String openday;
    private String setupday;
    private String work_type;
    private String workdivision;
    private String visit_datetime;
    private String com_datetime;
    private String ce_name;
    private String owner_name;
    private String owner_sign;
    private String oper_name;
    private String oper_sign;
    private String comp_name;
    private String comp_sign;
    private String tech_name;
    private String tech_sign;
    private String create_date;
    private List<ReqInstallDev> dev_list;
    private ReqInstallSam sam_info;
    private ReqInstallCheck check_info;
    private List<ReqInstallDongle> dongle_list;

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setOrgan(String organ) {
        this.organ = organ;
    }

    public void setOrgan_type(String organ_type) {
        this.organ_type = organ_type;
    }

    public void setOpenday(String openday) {
        this.openday = openday;
    }

    public void setSetupday(String setupday) {
        this.setupday = setupday;
    }

    public void setWork_type(String work_type) {
        this.work_type = work_type;
    }

    public void setWorkdivision(String workdivision) {
        this.workdivision = workdivision;
    }

    public void setVisit_datetime(String visit_datetime) {
        this.visit_datetime = visit_datetime;
    }

    public void setCom_datetime(String com_datetime) {
        this.com_datetime = com_datetime;
    }

    public void setCe_name(String ce_name) {
        this.ce_name = ce_name;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public void setOwner_sign(String owner_sign) {
        this.owner_sign = owner_sign;
    }

    public void setOper_name(String oper_name) {
        this.oper_name = oper_name;
    }

    public void setOper_sign(String oper_sign) {
        this.oper_sign = oper_sign;
    }

    public void setComp_name(String comp_name) {
        this.comp_name = comp_name;
    }

    public void setComp_sign(String comp_sign) {
        this.comp_sign = comp_sign;
    }

    public void setTech_name(String tech_name) {
        this.tech_name = tech_name;
    }

    public void setTech_sign(String tech_sign) {
        this.tech_sign = tech_sign;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public void setDev_list(List<ReqInstallDev> dev_list) {
        this.dev_list = dev_list;
    }

    public void setSam_info(ReqInstallSam sam_info) {
        this.sam_info = sam_info;
    }

    public void setCheck_info(ReqInstallCheck check_info) {
        this.check_info = check_info;
    }

    public void setDongle_list(List<ReqInstallDongle> dongle_list) {
        this.dongle_list = dongle_list;
    }
}
