package kr.co.opensysnet.paperless.model.request;

public class ReqBase<T> {
    private T req_param;

    public void setReq_param(T req_param) {
        this.req_param = req_param;
    }
}
