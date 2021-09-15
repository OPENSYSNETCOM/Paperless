package com.opensysnet.paperless.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ServiceParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer seq;
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
    private List<DeviceInfo> dev_list;
    private List<FixInfo> fix_list;
    private List<PartInfo> part_list;

    @Data
    public static class DeviceInfo {
        private Integer seq;
        private String dev_name;
        private Integer dev_order;
        private String model_name;
        private Integer dev_num;
    }

    @Data
    public static class FixInfo {
        private Integer seq;
        private String dev_name;
        private String problem;
        private String process;
    }

    @Data
    public static class PartInfo {
        private Integer seq;
        private String part_name;
        private Integer part_num;
    }
}
