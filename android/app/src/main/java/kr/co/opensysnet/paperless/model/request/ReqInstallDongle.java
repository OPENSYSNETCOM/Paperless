package kr.co.opensysnet.paperless.model.request;

public class ReqInstallDongle {
    private String lsam_name;
    private String lsam_flag;
    private String reason;
    private String cr_test;
    private String pay_test;

    public void setLsam_name(String lsam_name) {
        this.lsam_name = lsam_name;
    }

    public void setLsam_flag(String lsam_flag) {
        this.lsam_flag = lsam_flag;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public void setCr_test(String cr_test) {
        this.cr_test = cr_test;
    }

    public void setPay_test(String pay_test) {
        this.pay_test = pay_test;
    }
}
