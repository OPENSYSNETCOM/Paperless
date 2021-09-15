package kr.co.opensysnet.paperless.utils;

public class SignBodyUtil {
    static public String getReqParamFromData(int seq, String type) {
        return String.format("{\"seq\":%d, \"type\":\"%s\"}", seq, type);
    }
}
