package com.peigong.offer.chapter1_jvm.nio;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @author: lilei
 * @create: 2020-06-19 16:14
 **/
public class FileChannelTest {

    public static void main(String[] args) throws Exception{
        String path = FileChannelTest.class.getResource("/").getPath();
        System.out.println(path);
        FileOutputStream fos = new FileOutputStream(path + "test.txt");
        FileChannel channel = fos.getChannel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
        byteBuffer.put("哈哈哈哈哈哈哈哈".getBytes());
        byteBuffer.flip();
        channel.write(byteBuffer);
        FileChannel channel1 = new RandomAccessFile(path + "test.txt","r").getChannel();
        byteBuffer.clear();
        byte[] temp = new byte[0];
        while (channel1.read(byteBuffer) > -1) {
            byte[] bs = new byte[byteBuffer.position()];
            byteBuffer.flip();
            byteBuffer.get(bs);
            byteBuffer.clear();
            byte[] toTemp = new byte[temp.length + bs.length];
            System.arraycopy(temp, 0, toTemp, 0, temp.length);
            System.arraycopy(bs, 0, toTemp, temp.length, bs.length);
            temp = toTemp;
        }
        if (temp.length > 0) {
            System.out.println(new String(temp));
        }
    }

}
