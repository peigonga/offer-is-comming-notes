package com.peigong.offer.chapter3_concurrent.thread.test;

/**
 * @author: lilei
 * @create: 2020-05-28 11:50
 **/
public class ExecuteResult {

    private String executeThreadId;
    private String executeThreadName;

    public ExecuteResult(String executeThreadId, String executeThreadName) {
        this.executeThreadId = executeThreadId;
        this.executeThreadName = executeThreadName;
    }

    public String getExecuteThreadId() {
        return executeThreadId;
    }

    public void setExecuteThreadId(String executeThreadId) {
        this.executeThreadId = executeThreadId;
    }

    public String getExecuteThreadName() {
        return executeThreadName;
    }

    public void setExecuteThreadName(String executeThreadName) {
        this.executeThreadName = executeThreadName;
    }

    @Override
    public String toString() {
        return "threadId:" + executeThreadId + "\tthreadName:" + executeThreadName;
    }
}
