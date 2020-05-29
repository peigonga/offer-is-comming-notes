package com.peigong.offer.chapter3_concurrent.lock.reentrantlock;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @author: lilei
 * @create: 2020-05-29 10:34
 **/
public class WrapperLock {

    private ReentrantLock readLock = new ReentrantLock();
    private ReentrantLock writeLock = new ReentrantLock();

    public static WrapperLock instance = new WrapperLock();

    public static WrapperLock getInstance(){
        return instance;
    }

    public void readLock(){
        readLock.lock();
    }

    public void writeLock(){
        writeLock.lock();
    }

    public void readUnlock(){
        readLock.unlock();
    }

    public void writeUnlock(){
        writeLock.unlock();
    }

}
