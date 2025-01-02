package com.starchain.result;




import java.io.Serializable;

public class ClientResponse implements Serializable {

    private int code;
    private String message;
    private Object data;


    @Override
    public String toString() {
        return "BaseData{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

    public ClientResponse setCode(ResultCode resultCode) {
        this.code = resultCode.code;
        return this;
    }

    public int getCode() {
        return code;
    }

    public ClientResponse setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ClientResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getData() {
        return data;
    }

    public ClientResponse setData(Object data) {
        this.data = data;
        return this;
    }

}
