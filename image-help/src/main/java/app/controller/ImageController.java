package app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ImageController {

    // 图片资源的绝对路径
    private static final String IMAGE_DIRECTORY = "C:/Users/86199/Desktop/CAS/image-help/public/image/";

    @GetMapping("/api/images")
    public List<String> getImages() {
        File folder = new File(IMAGE_DIRECTORY);
        File[] files = folder.listFiles();
        List<String> imagePaths = new ArrayList<>();

        if (files != null) {
            int index = 1; // 用来生成新的文件名
            for (File file : files) {
                // 判断是否是图片文件（jpg, png, jpeg）
                if (file.isFile() && (file.getName().endsWith(".jpg") || file.getName().endsWith(".png") || file.getName().endsWith(".jpeg"))) {
                    // 生成新的文件名，例如：image1.jpg, image2.jpg
                    String newFileName = "image" + index + file.getName().substring(file.getName().lastIndexOf('.'));

                    // 创建新的文件对象
                    File newFile = new File(IMAGE_DIRECTORY + newFileName);

                    // 修改文件名
                    if (file.renameTo(newFile)) {
                        // 如果重命名成功，将新文件名添加到返回的列表
                        imagePaths.add(newFileName);
                    } else {
                        // 如果重命名失败
                        System.out.println("Failed to rename file: " + file.getName());
                    }

                    index++;
                }
            }
        }
        return imagePaths; // 返回图片路径列表
    }
}

