package kr.co.opensysnet.paperless.model.request;

import java.util.List;

public class ReqService {
    private int seq;
    private String code;
    private String organ;
    private String work_type;
    private String workdivision;
    private String receipt_datetime;
    private String visit_datetime;
    private String com_datetime;
    private String ce_name;
    private String ce_sign;
    private String owner_name;
    private String owner_sign;
    private String owner_comment;
    private String create_date;
    private List<ReqServiceDev> dev_list;
    private List<ReqServiceFix> fix_list;
    private List<ReqServicePart> part_list;

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setOrgan(String organ) {
        this.organ = organ;
    }

    public void setWork_type(String work_type) {
        this.work_type = work_type;
    }

    public void setWorkdivision(String workdivision) {
        this.workdivision = workdivision;
    }

    public void setReceipt_datetime(String receipt_datetime) {
        this.receipt_datetime = receipt_datetime;
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

    public void setCe_sign(String ce_sign) {
        this.ce_sign = ce_sign;
    }

    public void setOwner_name(String owner_name) {
        this.owner_name = owner_name;
    }

    public void setOwner_sign(String owner_sign) {
        this.owner_sign = owner_sign;
    }

    public void setOwner_comment(String owner_comment) {
        this.owner_comment = owner_comment;
    }

    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }

    public void setDev_list(List<ReqServiceDev> dev_list) {
        this.dev_list = dev_list;
    }

    public void setFix_list(List<ReqServiceFix> fix_list) {
        this.fix_list = fix_list;
    }

    public void setPart_list(List<ReqServicePart> part_list) {
        this.part_list = part_list;
    }
}
