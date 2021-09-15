package kr.co.opensysnet.paperless.model.request;

public class ReqLogin {
    private String user_id;
    private String passwd;

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }
}
