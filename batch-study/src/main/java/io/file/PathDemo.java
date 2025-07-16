package io.file;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 较新的Path
 * | 类名      | 所属包                   | 引入版本    | 所属模块  |
 * | ------- | --------------------- | ------- | ----- |
 * | `Path`  | `java.nio.file.Path`  | Java 7+ | NIO 2 |
 * | `Files` | `java.nio.file.Files` | Java 7+ | NIO 2 |
 */
public class PathDemo {

    @Test
    public void test() {
        Path path = Paths.get("/path_test");
        try {
            if (!Files.exists(path)) {
                Files.createDirectory(path);
            }
            System.out.println(path.getFileName());
//            Path path1 = Files.createFile(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
/**
 * | 特性     | `File`（老）      | `Path + Files`（新）              |
 * | ------ | -------------- | ------------------------------ |
 * | 所属包    | `java.io.File` | `java.nio.file.Path/Files`     |
 * | 引入版本   | Java 1.0       | Java 7                         |
 * | 面向     | 面向字符串（不安全）     | 面向路径对象（强类型）                    |
 * | 文件操作方式 | 通过 File 对象方法   | 通过 `Files` 工具类操作               |
 * | 读取内容   | 不支持            | `Files.readAllBytes/readLines` |
 * | 遍历目录   | 手动递归           | `Files.walk()`、`Files.find()`  |
 * | 可读性    | 一般             | 现代风格，更函数式                      |
 */