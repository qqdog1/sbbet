package name.qd.sbbet.response;

public class ErrorResponse {
    private String msg;

    public ErrorResponse(String msg) {
        this.msg = msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
