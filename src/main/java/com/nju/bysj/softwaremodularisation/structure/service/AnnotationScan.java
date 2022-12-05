package com.nju.bysj.softwaremodularisation.structure.service;//package depends.refactoring;
//
//import com.alibaba.fastjson.JSONArray;
//import com.alibaba.fastjson.JSONObject;
//import org.springframework.cloud.openfeign.FeignClient;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.File;
//import java.io.FileFilter;
//import java.io.IOException;
//import java.lang.annotation.Annotation;
//import java.lang.reflect.Method;
//import java.util.*;
//import java.util.stream.Collectors;
//
//import static depends.refactoring.FileUtils.readFile;
//import static depends.refactoring.FileUtils.readFileList;
//
//@Scanner
//public class AnnotationScan {
//
//    /**
//     * 总结一下加载class的方式
//     * 1、使用URLClassLoader，构造传入一个URL数组，URL的构造使用目标class文件路径的字符串构造的File对象的toURL方法生成
//     * 2、使用URLClassLoader，构造传入一个URL数组，URL的构造使用"file:\\" + 目标class文件路径的字符串生成，注意末尾需要加\\，表示一个目录
//     * 3、使用自定义的类加载器MyClassLoader，重写findClass方法【可以动态调整加载的目录】
//     */
//
//    static MyClassLoader classLoader;
//    static List<String> targetPathList;
//    static String targetDirectory = "target\\classes";
//    static String targetSuffix = ".class";
//    static String controllerSuffix = "controller.class";
//    static String targetClassPath = "D:\\Desktop\\targetClassFile.txt";
//
//    static {
//        try {
//            targetPathList = FileUtils.readFileList(targetClassPath);
//            classLoader = new MyClassLoader(targetPathList.toArray(new String[0]));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    /**
//     * 从指定路径加载.class文件
//     * @param path
//     * @return
//     * @throws ClassNotFoundException
//     */
//    public static Class<?> loadClassFileFrom(String path) throws ClassNotFoundException {
//        int mainIndex = path.indexOf(targetDirectory);
//        int suffixLen = targetSuffix.length();
////                System.out.println("absPath: " + className);
//        // 构造完整的classPackageName
//        String classPackageName = path.substring(mainIndex + 15, path.length() - suffixLen);
//        classPackageName = classPackageName.replace(File.separatorChar, '.');
////                System.out.println("classPackageName: " + classPackageName);
//        // 加载Class类
//        return classLoader.loadClass(classPackageName);
//    }
//
//    /**
//     * 扫描某个路径下，带有目标注解的全部文件列表
//     * @param file 根目录文件
//     * @param filter 文件过滤规则：java、目录，且不以test结尾的文件
//     * @param annotation 目标注解
//     * @return
//     * @throws ClassNotFoundException
//     */
//    public static List<String> scanFileWithAnnotation(
//            File file, FileFilter filter, Class<? extends Annotation> annotation,
//            List<Method> rpcInterfaces) throws ClassNotFoundException {
//        List<String> fileList = new ArrayList<>();
//        for (File f : file.listFiles(filter)) {
//            if (f.isFile()) {
//                Class<?> aClass = loadClassFileFrom(f.getAbsolutePath());
////                System.out.println("load finish");
//                if (aClass.getAnnotation(annotation) != null) {
////                    System.out.println(aClass.getAnnotation(annotation).toString());
////                    System.out.println("absPath: " + className);
//                    Method[] methods = aClass.getMethods();
//                    rpcInterfaces.addAll(Arrays.stream(methods).collect(Collectors.toList()));
////                    for (Method method : methods) {
////                        System.out.println(method.getName() + "; " + Arrays.toString(method.getDeclaredAnnotations()));
////                    }
//                    fileList.add(f.getAbsolutePath());
//                }
//            } else {
//                fileList.addAll(scanFileWithAnnotation(f, filter, annotation, rpcInterfaces));
//            }
//        }
//        return fileList;
//    }
//
//    static int c = 0;
//    /**
//     * 从controller中搜索client对应的producer
//     * @param file
//     * @param filter
//     * @param rpcInterfaces
//     * @param rpcToProducer
//     * @throws Exception
//     */
//    public static void scanForProducer(File file, FileFilter filter,
//                                       List<Method> rpcInterfaces, Map<Method, String> rpcToProducer) throws Exception {
//        for (File f : file.listFiles(filter)) {
//            if (f.isFile()) {
//                String className = f.getAbsolutePath();
//                System.out.println((++c) + ": " + className);
//                Class<?> aClass = loadClassFileFrom(className);
////                System.out.println(Arrays.toString(aClass.getDeclaredAnnotations()));
//                for (Method method : aClass.getMethods()) {
//                    for (Method rpc : rpcInterfaces) {
//                        if (judgeRPCInterfaceWithControllerMethod(rpc, method, aClass)) {
//                            if (rpcToProducer.get(rpc) != null) {
//                                throw new Exception("当前的rpc接口已经添加过controller方法");
//                            } else {
//                                String producer = convertClassPathToJavaPath(className);
//                                rpcToProducer.put(rpc, producer);
//                                System.out.println("Add new relation **********************");
//                                System.out.println(rpc.getName() + " ; " + producer);
//                            }
//                        }
//                    }
//                }
//            } else {
//                // dir
//                scanForProducer(f, filter, rpcInterfaces, rpcToProducer);
//            }
//        }
//    }
//
//    /**
//     * 根据.class文件的abs路径，生成对应的java文件的abs路径
//     * @param classpath
//     * @return
//     */
//    public static String convertClassPathToJavaPath(String classpath) {
//        int dirIndex = classpath.indexOf("target\\classes");
//        return classpath.substring(0, dirIndex)
//                .concat("src\\main\\java\\")
//                .concat(classpath.substring(dirIndex + 15, classpath.length() - 6))
//                .concat(".java");
//    }
//
//    /**
//     * 判断两个接口注解的param，String数组是否相等
//     * @param params1
//     * @param params2
//     * @return
//     */
//    public static boolean judgeMethodParams(String[] params1, String[] params2) {
//        if (params1.length != params2.length) {
//            return false;
//        }
//        Arrays.sort(params1);
//        Arrays.sort(params2);
//        for (int i = 0; i < params1.length; i++) {
//            if (!params1[i].equals(params2[i])) {
//                return false;
//            }
//        }
//        return true;
//    }
//
//    /**
//     * 判断controller的方法是否包含了某个rpc接口的全部注解
//     * @param rpc
//     * @param cm
//     * @return
//     */
//    public static boolean judgeRPCInterfaceWithControllerMethod(Method rpc, Method cm, Class<?> controller) throws Exception {
//        for (Annotation annotation : rpc.getDeclaredAnnotations()) {
//            // 判断注解类型
//            if (cm.getAnnotation(annotation.annotationType()) == null) {
//                return false;
//            }
//            // 之后需要判断路径和参数是否一致
//            String cm_urlPrefix = "";
//            if (controller.getAnnotation(RequestMapping.class) != null) {
//                cm_urlPrefix = controller.getAnnotation(RequestMapping.class).value()[0];
//            }
//
//            // 目前仅考虑：GetMapping、PostMapping、PutMapping、DeleteMapping、PatchMapping
//            if (annotation.annotationType() == GetMapping.class) {
//                String rpc_url = rpc.getAnnotation(GetMapping.class).value()[0];
//                String cm_url = cm_urlPrefix.concat(cm.getAnnotation(GetMapping.class).value()[0]);
//                if (!rpc_url.equals(cm_url) || !judgeMethodParams(
//                        rpc.getAnnotation(GetMapping.class).params(), cm.getAnnotation(GetMapping.class).params())) {
//                    return false;
//                }
//            }
//            else if (annotation.annotationType() == PostMapping.class) {
//                String rpc_url = rpc.getAnnotation(PostMapping.class).value()[0];
//                String cm_url = cm_urlPrefix.concat(cm.getAnnotation(PostMapping.class).value()[0]);
//                if (!rpc_url.equals(cm_url) || !judgeMethodParams(
//                        rpc.getAnnotation(PostMapping.class).params(), cm.getAnnotation(PostMapping.class).params())) {
//                    return false;
//                }
//            }
//            else if (annotation.annotationType() == PutMapping.class) {
//                String rpc_url = rpc.getAnnotation(PutMapping.class).value()[0];
//                String cm_url = cm_urlPrefix.concat(cm.getAnnotation(PutMapping.class).value()[0]);
//                if (!rpc_url.equals(cm_url) || !judgeMethodParams(
//                        rpc.getAnnotation(PutMapping.class).params(), cm.getAnnotation(PutMapping.class).params())) {
//                    return false;
//                }
//            }
//            else if (annotation.annotationType() == DeleteMapping.class) {
//                String rpc_url = rpc.getAnnotation(DeleteMapping.class).value()[0];
//                String cm_url = cm_urlPrefix.concat(cm.getAnnotation(DeleteMapping.class).value()[0]);
//                if (!rpc_url.equals(cm_url) || !judgeMethodParams(
//                        rpc.getAnnotation(DeleteMapping.class).params(), cm.getAnnotation(DeleteMapping.class).params())) {
//                    return false;
//                }
//            }
//            else if (annotation.annotationType() == PatchMapping.class) {
//                String rpc_url = rpc.getAnnotation(PatchMapping.class).value()[0];
//                String cm_url = cm_urlPrefix.concat(cm.getAnnotation(PatchMapping.class).value()[0]);
//                if (!rpc_url.equals(cm_url) || !judgeMethodParams(
//                        rpc.getAnnotation(PatchMapping.class).params(), cm.getAnnotation(PatchMapping.class).params())) {
//                    return false;
//                }
//            }
//            else {
//                throw new Exception("未作处理的方法request类型 ");
//            }
//        }
//        return true;
//    }
//
//
//    public static void main(String[] args) throws Exception {
//        // 1、扫描client类
//        String path = "D:\\Development\\idea_projects\\microservices-platform-master";
//        FileFilter filter = pathname ->
//                (pathname.getName().endsWith(".class") || pathname.isDirectory())
//                && !pathname.getName().toLowerCase(Locale.ROOT).endsWith("test.class")
//                && !pathname.getName().toLowerCase(Locale.ROOT).endsWith("tests.class")
//                && !pathname.getName().contains("zlt-demo");
//
//        List<Method> rpcInterfaces = new ArrayList<>();
//        List<String> clientClasses = scanFileWithAnnotation(new File(path), filter, FeignClient.class, rpcInterfaces);
////        System.out.println(clientClasses.size());
//        List<String> clientList = new ArrayList<>(clientClasses.size());
//        for (String clientClass : clientClasses) {
//            String client = convertClassPathToJavaPath(clientClass);
////            System.out.println(client);
//            clientList.add(client);
//        }
////
////        // 2、根据client的method寻找producer
//////        for (Method method : rpcInterfaces) {
//////            System.out.println(method.getName() + " -----------------------------------------------------------------");
//////            System.out.println(Arrays.toString(method.getDeclaredAnnotations()));
//////        }
//        FileFilter controllerFilter = pathname ->
//                (pathname.getName().toLowerCase(Locale.ROOT).endsWith("controller.class") || pathname.isDirectory())
//                        && !pathname.getName().contains("zlt-demo");
//        Map<Method, String> rpcToProducer = new HashMap<>();
//        scanForProducer(new File(path), controllerFilter, rpcInterfaces, rpcToProducer);
//
//        // 3、读取dependency.json，寻找与rpc文件有依赖的代码文件
//        String jsonString = readFile("D:\\Desktop\\dependency.json");
//        System.out.println("读取：dependency 成功");
//        JSONObject jsonObject = JSONObject.parseObject(jsonString);
//        JSONArray fileArray = jsonObject.getJSONArray("variables");
//        // 读取全部文件列表
//        List<String> fullFiles = new ArrayList<>();
//        for (Object aFileArray : fileArray) {
//            fullFiles.add(aFileArray.toString());
//        }
//        System.out.println("获取项目全部文件列表成功");
//        // 读取目标文件列表
//        List<String> targetFiles = readFileList("D:\\Development\\idea_projects\\codeTopics\\src\\test\\example\\files.flist");
//        JSONArray cellArray = jsonObject.getJSONArray("cells");
//        List<String> targetDependencies = new ArrayList<String>() {{
//            add("Call");
////            add("Cast");
////            add("Contain");
////            add("Create");
////            add("Import");
////            add("Include");
////            add("Parameter");
//            add("Use");
//        }};
//
//        for (int i = 0; i < cellArray.size(); i++){
//            JSONObject subObj = cellArray.getJSONObject(i);
//            int srcId = subObj.getIntValue("src");
//            int destId = subObj.getIntValue("dest");
//            String srcFile = fullFiles.get(srcId), destFile = fullFiles.get(destId);
//            if (clientList.contains(destFile)) {
//                JSONObject values = subObj.getJSONObject("values");
//                for (String dt : targetDependencies) {
//                    int weight = values.getIntValue(dt);
//                    if (weight > 0) {
//                        System.out.println("scan dependency: " + dt + "-" + weight);
//                        System.out.println(srcFile.substring(59) + " to " + destFile.substring(59));
//                    }
//                }
//            }
//        }
//
//    }
//}
