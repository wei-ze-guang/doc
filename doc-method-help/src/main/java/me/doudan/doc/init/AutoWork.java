package me.doudan.doc.init;

import jakarta.annotation.PostConstruct;
import me.doudan.doc.AnnotationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AutoWork {
    private AnnotationHelper annotationHelper;

    @Autowired
    public AutoWork(AnnotationHelper annotationHelper) {
        this.annotationHelper = annotationHelper;
    }

    @Value("${doc-helper.enable}")
    private boolean enabled;
    @Value("${doc-helper.anto-download-md}")
    private boolean autoDownloadMd;

    @Value("${doc-helper.pack-name}")
    private String packName;

    @PostConstruct
    private void init()  {
        try{
            if(enabled && autoDownloadMd){
                if(packName == null){
                    throw  new IllegalArgumentException("packName is null");
                }
                System.out.println("开始工作了");
                annotationHelper.scan(packName);
                annotationHelper.writeModuleMd("method-module.md");
            }
        }catch (IllegalArgumentException | IOException e){
            throw new RuntimeException("write md failed");
        }

    }
}
