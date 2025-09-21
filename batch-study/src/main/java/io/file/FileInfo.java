package io.file;

import org.junit.Test;

import java.io.File;
import java.text.SimpleDateFormat;

/**
 * File 对象
 * 常用构造方法
 * public File(String pathname)                      // 单路径
 * public File(String parent, String child)          // 父 + 子路径
 * public File(File parent, String child)            // File 对象 + 子路径
 */
public class FileInfo {

    @Test
    public void test() {
        File file = new File("out.txt");
        System.out.println(file.getAbsolutePath());
        printInfo(file);
    }

    public static void printInfo(File file) {
        StringBuilder sb = new StringBuilder();

        sb.append("文件或者目录是否存在"+file.exists()+"\n");

        if(!file.exists()) return;

        boolean exists = file.exists();

        int resultToInt = exists ? 1 : 0;

        boolean result = switch (resultToInt){
            case 1 -> true;
            case 2 -> false;
            default -> false;
        };

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        sb.append(file.isFile() ? "是文件": "是目录");
        sb.append("\n");
        sb.append(file.canRead() ? "可读":"不可读");
        sb.append("\n");
        sb.append(file.canWrite()? "可写":"不可写");
        sb.append("\n");
        sb.append("文件大小(字节):"+file.length()+"\n");
        sb.append("上次修改时间："+sdf.format(file.lastModified())+"\n");

        sb.append("文件名:"+file.getName()+"\n");
        sb.append("绝对路径:"+file.getAbsolutePath()+"\n");
        sb.append("父路径:"+file.getParent()+"\n");
        sb.append("原始路径:"+file.getPath()+"\n");

        System.out.println(sb);

    }
}

/**
 * 文件属性查询
 * | 方法                           | 作用         |
 * | ---------------------------- | ---------- |
 * | `exists()`                   | 是否存在       |
 * | `isFile()` / `isDirectory()` | 是文件 / 是目录  |
 * | `canRead()` / `canWrite()`   | 是否可读 / 可写  |
 * | `length()`                   | 文件大小（字节）   |
 * | `lastModified()`             | 上次修改时间（毫秒） |
 *
 * 文件路径相关
 * | 方法                   | 说明                 |
 * | -------------------- | ------------------ |
 * | `getName()`          | 文件名                |
 * | `getParent()`        | 父路径（字符串）           |
 * | `getParentFile()`    | 父路径（File 对象）       |
 * | `getPath()`          | 原始路径               |
 * | `getAbsolutePath()`  | 绝对路径               |
 * | `getCanonicalPath()` | 去除 `..` 和符号链接的规范路径 |
 *   文件操作
 * | 方法                       | 作用            |
 * | ------------------------ | ------------- |
 * | `mkdir()` / `mkdirs()`   | 创建目录 / 连父目录一起 |
 * | `createNewFile()`        | 创建新文件         |
 * | `delete()`               | 删除            |
 * | `renameTo(File dest)`    | 重命名 / 移动      |
 * | `list()` / `listFiles()` | 列出目录内容        |
 */
