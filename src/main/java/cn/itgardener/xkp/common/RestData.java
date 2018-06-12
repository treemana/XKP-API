/*
 * Copyright © 2014-2018 www.itgardener.cn. All rights reserved.
 */

package cn.itgardener.xkp.common;

public class RestData {
    private int code = 0;

    private String message;

    private Object data;

    public RestData(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public RestData(Object data) {
        this.code = 0;
        this.message = "";
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
