package com.starchain.result;



public class ResultGenerator {
    public static final String DEFAULT_SUCCESS_MESSAGE = "success";

    public static ClientResponse genSuccessResult() {
        return new ClientResponse()
                .setCode(ResultCode.SUCCESS)
                .setMessage(DEFAULT_SUCCESS_MESSAGE);
    }

    public static ClientResponse genSuccessResult(Object data) {
        return new ClientResponse()
                .setCode(ResultCode.SUCCESS)
                .setMessage(DEFAULT_SUCCESS_MESSAGE)
                .setData(data);
    }

    public static ClientResponse genFailResult(String message) {
        return new ClientResponse()
                .setCode(ResultCode.FAIL)
                .setMessage(message);
    }

    public static ClientResponse genFailResult(ResultCode resultCode, String message) {
        return new ClientResponse()
                .setCode(resultCode)
                .setMessage(message);
    }


    public static ClientResponse genResult(ResultCode resultCode, String message) {
        return new ClientResponse()
                .setCode(resultCode)
                .setMessage(message);
    }

}
