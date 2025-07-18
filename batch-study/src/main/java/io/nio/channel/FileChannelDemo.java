package io.nio.channel;

import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 测试单向的
 */
public class FileChannelDemo {

    @Test
    public void testFileChannel() {
        //这里是单向的
        String path = "src/main/java/io/nio/channel/FileChannelDemo.java";

        String outPath = "out_file.txt";

        Path path1 = Paths.get(path);

        FileInputStream inputStream = null;

        FileOutputStream outputStream = null;

        try {
            inputStream = new FileInputStream(path1.toFile());
            outputStream = new FileOutputStream(outPath);

            FileChannel fileInputChannel = inputStream.getChannel();  //获取channel 单向的
            FileChannel fileOutPutChannel = outputStream.getChannel();  //获取channel  单向的

            //映射到缓冲区
            MappedByteBuffer map = fileInputChannel.map(FileChannel.MapMode.READ_ONLY, 0, path1.toFile().length() );//

            //通过channel 把缓冲区的数据写入文件
            fileOutPutChannel.write(map);


        }catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            //映射失败的话走这里
            throw new RuntimeException(e);
        }

    }

    @Test
    public void testFileChannel2()  {
        //测试双向的
        String path = "out_file.txt";
        File file = new File(path);

        FileChannel channel = null;
        //在这个文件上追加信息

        RandomAccessFile randomAccessFile = null;
        try {
            if(!file.exists()) throw new  RuntimeException();

            RandomAccessFile rw = new RandomAccessFile(file, "rw");
            channel= rw.getChannel();  //双向的
            MappedByteBuffer mappedByteBuffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, file.length());  //映射到内存了
            //channel他也有一个position，需要在文件后面追截内容，就需要先把他的position移动到文件的最后面
            channel.position(file.length());  //移动到最后面
            channel.write(mappedByteBuffer);  //再把缓冲区的内容重复给他写一遍
            channel.force(true);  //强制刷盘

        } catch (RuntimeException e) {
            System.out.println("文件不存在");
            System.out.println(e.getMessage());
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            if(randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                }catch (IOException e) {
                    e.printStackTrace();
                }

            }

            if(channel != null) {
                try {
                    channel.close();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Test
    public void testFileChannel3()  {
        //测试固定缓冲区大小
        String path = "out_file.txt";

        String outPath = "out_flie_fixed_size.txt";

        File outFile = new File(outPath);   //输出文件

        File file = new File(path);  //源文件

        FileChannel channel = null;
        //在这个文件上追加信息

        FileOutputStream outputStream = null;
        FileInputStream inputStream = null;

        FileChannel outChannel = null;
        FileChannel inChannel = null;

        try {
            if(!file.exists() ) throw new  RuntimeException();
            outputStream = new FileOutputStream(outFile);  //输出文件流
            inputStream = new FileInputStream(file);  //输入文件流
            inChannel = inputStream.getChannel();  //输入通道
            outChannel= outputStream.getChannel(); //输出通道

            ByteBuffer byteBuffer = ByteBuffer.allocateDirect(32);  //创建固定大小的缓冲区
            //把源文件一次一次读进来缓冲区

            int bytes = inChannel.read(byteBuffer); //读取，返回-1说明全部读取完了
            while (bytes != -1) {
                byteBuffer.flip();  //切换为读模式
                outChannel.write(byteBuffer);  //读取
                byteBuffer.clear();  //清理一下
                bytes = inChannel.read(byteBuffer); //再读取
            }

        }catch (RuntimeException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    /**
     * 通道直接输出文件到通道，不需要经过缓冲区  channel到channel
     * 只有FileChannel支持 ，其他的不可以
     */
    @Test
    public void testFileChannel4()  {
        String path = "out_file.txt";
        File inFile = new File(path);
        String outPath = "channel_to_channel.txt";
        File outFile = new File(outPath);
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        RandomAccessFile randomAccessFile = null;
        FileInputStream inputStream = null;

        try {
            if(!inFile.exists() ) throw new  RuntimeException();  //源文件不存在直接抛出异常

            RandomAccessFile raf = new RandomAccessFile(inFile, "rw");

            inChannel = raf.getChannel();

            outChannel = new FileOutputStream(outFile).getChannel();

            //然后直接通过通道到通道进行，不需要通过缓冲区

            //inChannel.transferTo(0,inFile.length(),outChannel);  //这样可以，下面页可以
            outChannel.transferFrom(inChannel, 0, inChannel.size());

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            if(randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
