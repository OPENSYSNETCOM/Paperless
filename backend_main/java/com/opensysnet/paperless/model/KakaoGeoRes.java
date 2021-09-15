package com.opensysnet.paperless.model;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

@Data
public class KakaoGeoRes implements Serializable {

    private static final long serialVersionUID = 1L;

    private HashMap<String, Object> meta;
    private List<Documents> documents;

    @Data
    public static class Documents {
        private HashMap<String, Object> address;
        private String address_type;
        private Double x;
        private Double y;
        private String address_name;
        private HashMap<String, Object> road_address;
    }
}
