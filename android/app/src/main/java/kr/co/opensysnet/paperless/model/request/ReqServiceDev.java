package kr.co.opensysnet.paperless.model.request;

public class ReqServiceDev {
    private String dev_name;
    private int dev_order;
    private String model_name;
    private int dev_num;

    private ReqServiceFix fix;

    public String getDev_name() {
        return dev_name;
    }

    public void setDev_name(String dev_name) {
        this.dev_name = dev_name;
    }

    public int getDev_order() {
        return dev_order;
    }

    public void setDev_order(int dev_order) {
        this.dev_order = dev_order;
    }

    public String getModel_name() {
        return model_name;
    }

    public void setModel_name(String model_name) {
        this.model_name = model_name;
    }

    public int getDev_num() {
        return dev_num;
    }

    public void setDev_num(int dev_num) {
        this.dev_num = dev_num;
    }

    public ReqServiceFix getFix() {
        return fix;
    }

    public void setFix(ReqServiceFix fix) {
        this.fix = fix;
    }
}
