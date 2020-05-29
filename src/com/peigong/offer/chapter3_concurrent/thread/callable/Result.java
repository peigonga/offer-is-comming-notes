package com.peigong.offer.chapter3_concurrent.thread.callable;

/**
 * @author: lilei
 * @create: 2020-05-28 11:18
 **/
public class Result {

    private String ret;

    private int status;

    public String getRet() {
        return ret;
    }

    public void setRet(String ret) {
        this.ret = ret;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ret:" + ret + "\tstatus:" + status;
    }
}
