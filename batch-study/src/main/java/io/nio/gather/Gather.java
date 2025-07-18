package io.nio.gather;

import lombok.Data;
import org.junit.Test;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

/**
 * 把多个缓冲区内容写入一个channel
 */
public class Gather {

    @Test
    public void test() {
        ByteBuffer[] buffer = {null,null,null} ;

        String inputFile = "out_flie_fixed_size.txt";

        File intPutFile = new File(inputFile);  //文件

        ByteBuffer fileHeader = ByteBuffer.wrap(getFileInfo(intPutFile).getBytes());  //存储文件的信息

        buffer[0] = fileHeader;

        FileChannel channel = null;

        MappedByteBuffer map = null;

        try {
            channel = new FileInputStream(inputFile).getChannel();
            //映射一些
            map = channel.map(FileChannel.MapMode.READ_ONLY, 0, inputFile.length());

            buffer[1] = map;

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            if (channel != null || map != null) try {
                //如果为空的话把错误信息存入缓冲区
                String s = "出现了错误";
                byte[] bytes = s.getBytes();
                ByteBuffer wrap = ByteBuffer.wrap(bytes);
                buffer[2] = wrap;
            }catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        File outFile = new File("out_gather.txt");

        try {
            FileOutputStream fos = new FileOutputStream(outFile);
            FileChannel channel1 = fos.getChannel();

            while (channel1.write(buffer) > 0);

            channel1.close();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getFileInfo(File file) {
        String absolutePath = file.getAbsolutePath();  //文件绝对路径

        long l = file.lastModified();  //文件最后修改时间

        Date date = new Date(l);  //转为时间

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        String format = sdf.format(date);

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append("文件绝对路径:"+absolutePath+"\n");
        stringBuilder.append("文件最后修改时间:"+format+"\n");

        return stringBuilder.toString();
    }
}
