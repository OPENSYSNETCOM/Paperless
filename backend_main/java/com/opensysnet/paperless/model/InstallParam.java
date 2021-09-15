package com.opensysnet.paperless.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class InstallParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer seq;
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
    private List<DeviceInfo> dev_list;
    private SamInfo sam_info;
    private CheckInfo check_info;
    private List<DongleInfo> dongle_list;

    @Data
    public static class DeviceInfo {
        private Integer seq;
        private String dev_name;
        private Integer dev_order;
        private String model_name;
        private String asset_code;
        private String dev_type;
        private String status;
        private String description;
    }

    @Data
    public static class SamInfo {
        private String tmoney_1;
        private String tmoney_2;
        private String intsam_1;
        private String intsam_2;
    }

    @Data
    public static class CheckInfo {
        private String pos_num_check_flag;
        private String pos_num_val;
        private String pos_ver_check_flag;
        private String pos_ver_val;
        private String openday;
        private String startday;
        private String got_check_flag;
        private String got_val;
        private String internet_flag;
        private String internet_val;
        private String sc_pgm_flag;
        private String sc_pgm_val;
        private String pos_cash_flag;
        private String pos_cash_val;
        private String pos_credit_flag;
        private String pos_credit_val;
        private String pos_point_flag;
        private String pos_point_val;
        private String sc_work_flag;
        private String sc_work_val;
        private String got_update_flag;
        private String got_update_val;
    }

    @Data
    public static class DongleInfo {
        private Integer seq;
        private String lsam_name;
        private String lsam_flag;
        private String reason;
        private String cr_test;
        private String pay_test;
    }

}
