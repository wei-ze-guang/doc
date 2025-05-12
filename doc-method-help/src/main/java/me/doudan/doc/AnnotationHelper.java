package me.doudan.doc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import java.lang.reflect.Field;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


@Component
public class AnnotationHelper {
    //这个String 存的是注解的集合的那个描述
    private final Map<String, List<MethodDetail>> MethodDetails = new LinkedHashMap<>();
    private final Map<String, Map<String, List<String>>> chartModule = new LinkedHashMap<>();  //处理md格式

    @Value("${doc-helper.md-full-name:false}")  //是否让md文件展示方法全名
    private boolean mdFullName ;

    private List<String> orderList = new  LinkedList();  //把这个主机map的值存进来，弄成有序的

    public AnnotationHelper() {
    }

    @Autowired
    public AnnotationHelper(Map<Class<?>, String> methodAnnotation) {
        this.annotationTypeMap = methodAnnotation;
    }

    //    @Resource
    Map<Class<?>, String> annotationTypeMap;  //这个map现在是有序的了

    private Map<String, List<MethodDetail>> scanMethodDetails(String basePackage) throws IOException {

        for(Map.Entry<Class<?>, String> entry : annotationTypeMap.entrySet()){
            String value = entry.getValue();
            orderList.add(value);
        }
        orderList.forEach(System.out::println);  //这里变为有序的了

        MethodDetails.clear();
        chartModule.clear();
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forPackage(basePackage))
                .setScanners(new MethodAnnotationsScanner())
                .addClassLoaders(Thread.currentThread().getContextClassLoader()));

        //初始化一下 List，因为表格他固定顺序

        for (Map.Entry<Class<?>, String> entry : annotationTypeMap.entrySet()) {
            Class<?> annotationClass = entry.getKey();   //注解名字
            String annotationName = entry.getValue();  //别名

            Set<Method> methods = reflections.getMethodsAnnotatedWith((Class<? extends Annotation>) annotationClass);
            List<MethodDetail> methodList = MethodDetails.computeIfAbsent(annotationName, k -> new ArrayList<>());

            for (Method method : methods) {
                String methodSignature = method.getDeclaringClass().getName() + "#" + method.getName();  //方法全名
                String methodOnlyClassNameAndSingleName  = "";  //只有类名中间加一个#
                String[] arr = methodSignature.split("\\.");
                methodOnlyClassNameAndSingleName = arr[arr.length - 1];
                String returnType = method.getReturnType().getSimpleName();  //返回值

                StringBuilder params = new StringBuilder();
                for (Class<?> paramType : method.getParameterTypes()) {
                    params.append(paramType.getSimpleName()).append(" ");
                }

                String description = "";
                String module = "";
                try {
                    Object annotation = method.getAnnotation((Class) annotationClass);
                    Method valueMethod = annotationClass.getMethod("value");
                    Method moduleMethod = annotationClass.getMethod("module");
                    description = (String) valueMethod.invoke(annotation);
                    module = (String) moduleMethod.invoke(annotation);

                } catch (Exception e) {

                }
                if (!module.isEmpty()) {
                    //初始化一下 List，因为表格他固定顺序

                    //第一步：获取 module 对应的内层 Map（如果没有就新建一个）
                    Map<String, List<String>> innerMap = chartModule.computeIfAbsent(module, k -> new LinkedHashMap<>());
                    // 第二步：在 innerMap 中找 annotationName 对应的列表（如果没有就新建）

                    for(String str : orderList) {
                        innerMap.computeIfAbsent(str, k -> new ArrayList<>());
                    }
                    List<String> descList = innerMap.computeIfAbsent(annotationName, k -> new ArrayList<>());

                    String chatMethodName = methodSignature;
                    if(mdFullName){
                        //如果不展示全名的话就是使用简名
//                        String[] arr = chatMethodName.split("\\.");
//                        chatMethodName = arr[arr.length - 1];
                        chatMethodName = methodOnlyClassNameAndSingleName;
                        methodSignature = methodOnlyClassNameAndSingleName;
                    }
                    descList.add(chatMethodName);
                }

                MethodDetail methodDetail = new MethodDetail(methodSignature, returnType, params.toString().trim(), description, module);
                methodList.add(methodDetail);
            }
        }
        String jsonChatModule = new ObjectMapper().writeValueAsString(chartModule);
        System.out.println("md-模型最初结果" + jsonChatModule);
//        writeMethodDetailsToJson(filePath);
        return MethodDetails;
    }

    /**
     * 开始扫描，扫描完之后的所有结果，之后根据情况自己提取
     *
     * @param packName
     * @throws IOException
     */
    public void scan(@NonNull String packName) throws IOException {
        scanMethodDetails(packName);
    }

    public void writeModuleMd(@Nullable String fileName) {
        StringBuilder sb  = new StringBuilder();
        String content = handleChartModule();
        String details = handleMethodDetails();
        String filePath = "method.md";
        if (fileName != null) {
            filePath = fileName ;
        }
        try {
            Path path = Paths.get(filePath);
            Files.writeString(path, content);
            System.out.println("功能表格已完成：" + path.toAbsolutePath());

            Path pathDetail = Paths.get("detail-"+filePath);
            Files.writeString(pathDetail,details);
            System.out.println("方法详细表：" + path.toAbsolutePath());

            sb.append(content).append("\n").append(details);

            Path pathAll = Paths.get("all-"+filePath);
            Files.writeString(pathAll,sb.toString());
            System.out.println("打印在一张表：" + pathAll.toAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String handleChartModule() {
        System.out.println("开始把模型转为md格式字符串");
        StringBuilder sb = new StringBuilder();
        sb.append("## 模块资源表\n\n");
        sb.append("|module|");
        for (Map.Entry<Class<?>, String> entry : annotationTypeMap.entrySet()) {
            //先写表格头
            String value = entry.getValue();
            sb.append(value).append("|");
        }
        sb.append("\n").append("|");
        for (int i = 0; i < annotationTypeMap.size(); i++) {
            sb.append("-----------------|");
        }
        sb.append("--------------|");  //换行加一个|  准备扫描chartModule，加入内容
        System.out.println(sb);

        for (Map.Entry<String, Map<String, List<String>>> entry : chartModule.entrySet()) {
            sb.append("\n");
            String module = entry.getKey();
            Map<String, List<String>> innerMap = entry.getValue();

            sb.append("|").append(module).append("|");  //这里加入了模块，准备只处理数组

//            for(Map.Entry<Class<?>,String> entry1 : annotationTypeMap.entrySet()){
//                String value = entry1.getValue();
//                for (Map.Entry<String, List<String>> innerEntry : innerMap.entrySet()) {
//                    //由于表头有序的，但是列表是无序的
//                    String listString = String.join(",", innerEntry.getValue());   //这里顺序是 乱的
//                    String md = wrapWithBr(listString,30);
//                    sb.append(md).append("|");
//                }
//            }
            for (Map.Entry<String, List<String>> innerEntry : innerMap.entrySet()) {
                //由于表头有序的，但是列表是无序的
                String listString = String.join(",", innerEntry.getValue());   //这里顺序是 乱的
                String md = wrapWithBr(listString,30);
                sb.append(md).append("|");
            }


        }
        System.out.println(sb);
        return sb.toString();
    }

    private String handleMethodDetails(){
        String distant = "-------------";
        //这里动态获取类字段名
        List<String> fieldNames = Arrays.stream(MethodDetail.class.getDeclaredFields())
                .map(Field::getName)
                .collect(Collectors.toList());

        System.out.println("开始写方法详细功能表");
        StringBuilder sb = new StringBuilder();
        sb.append("## 方法详细表\n\n");
        sb.append("\n");
        sb.append("|");
        for(String fieldName : fieldNames){
            sb.append(fieldName).append("|");
        }  //加表格标题
        sb.append("annoType|").append("\n");
        sb.append("|");
        for(int i = 0; i < fieldNames.size()+1; i++){  //因为多加了一个 annoType ，所以加1
            sb.append(distant).append("|");
        }  //加-------------
        sb.append("\n");

        for(Map.Entry<String,List<MethodDetail>> entry : MethodDetails.entrySet()){
            String annotationName = entry.getKey();
            List<MethodDetail> methodDetails = entry.getValue();
            for(MethodDetail methodDetail : methodDetails){
                sb.append("|");
                sb.append(methodDetail.getMethod()).append("|");
                sb.append(methodDetail.getReturnType()).append("|");
                sb.append(methodDetail.getParameters()).append("|");
                sb.append(methodDetail.getDescription()).append("|");
                sb.append(methodDetail.getModule()).append("|");
                sb.append(annotationName);
                sb.append("|").append("\n");
            }
        }
        System.out.println(sb);
        return sb.toString();
    }

    /**
     * 处理方法太长的话换行
     *
     * @param input
     * @param maxLineLength
     * @return
     */
    private String wrapWithBr(String input, int maxLineLength) {
        if (input == null) return "";

        StringBuilder sb = new StringBuilder();
        int len = input.length();
        for (int i = 0; i < len; i += maxLineLength) {
            int end = Math.min(i + maxLineLength, len);
            sb.append(input, i, end);
            if (end < len) {
                sb.append("<br>");
            }
        }
        return sb.toString();
    }


    private void writeMethodDetailsToJson(String filePath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.writeValue(new File(filePath), MethodDetails);
    }

    public static class MethodDetail {
        private String method;
        private String returnType;
        private String parameters;
        private String description;
        private String module;

        public MethodDetail(String method, String returnType, String parameters, String description, String module) {
            this.method = method;
            this.returnType = returnType;
            this.parameters = parameters;
            this.description = description;
            this.module = module;
        }

        public String getModule() {
            return module;
        }

        public void setModule(String module) {
            this.module = module;
        }

        public String getMethod() {
            return method;
        }

        public void setMethod(String method) {
            this.method = method;
        }

        public String getReturnType() {
            return returnType;
        }

        public void setReturnType(String returnType) {
            this.returnType = returnType;
        }

        public String getParameters() {
            return parameters;
        }

        public void setParameters(String parameters) {
            this.parameters = parameters;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
