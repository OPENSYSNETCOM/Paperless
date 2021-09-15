package com.opensysnet.paperless.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ReqParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String user_id;
    private String passwd;
    private String latitude;
    private String longitude;
    private Integer scale;
    private String date;
    private String startdate;
    private String enddate;
    private Integer seq;
    private String type;
    private List<TimeInfo> time_info;

    @Data
    public static class TimeInfo {
        private String type_val;
    }
}
