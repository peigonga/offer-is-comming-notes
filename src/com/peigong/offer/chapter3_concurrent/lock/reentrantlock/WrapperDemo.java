package com.peigong.offer.chapter3_concurrent.lock.reentrantlock;

/**
 * @author: lilei
 * @create: 2020-05-29 10:34
 **/
public class WrapperDemo {

    private int a;

    public void read(){
        System.out.println("read:a="+a);
    }

    public void write(){
        a++;
        System.out.println("write:a="+a);
    }

    public static void main(String[] args) {
        WrapperDemo demo = new WrapperDemo();

        new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        try {
                            WrapperLock.getInstance().readLock();
                            demo.read();
                            Thread.sleep(300);
                            try{
                                WrapperLock.getInstance().writeLock();
                                demo.write();
                            }finally {
                                WrapperLock.getInstance().writeUnlock();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }finally {
                            WrapperLock.getInstance().readUnlock();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        try {
                            WrapperLock.getInstance().readLock();
                            demo.read();
                            Thread.sleep(300);
                            try{
                                WrapperLock.getInstance().writeLock();
                                demo.write();
                            }finally {
                                WrapperLock.getInstance().writeUnlock();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }finally {
                            WrapperLock.getInstance().readUnlock();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
