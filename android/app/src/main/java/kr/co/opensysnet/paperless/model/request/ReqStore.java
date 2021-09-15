package kr.co.opensysnet.paperless.model.request;

public class ReqStore {
    private String latitude;
    private String longitude;
    private int scale;

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setScale(int scale) {
        this.scale = scale;
    }
}
