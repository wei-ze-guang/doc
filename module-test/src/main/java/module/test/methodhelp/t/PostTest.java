package module.test.methodhelp.t;

import jakarta.annotation.PostConstruct;
import me.doudan.doc.AnnotationHelper;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class PostTest {

    @PostConstruct
    public void init() throws IOException {
        AnnotationHelper helper = new AnnotationHelper();
        helper.scan("module");
        helper.writeModuleMd("module.md");
    }
}
