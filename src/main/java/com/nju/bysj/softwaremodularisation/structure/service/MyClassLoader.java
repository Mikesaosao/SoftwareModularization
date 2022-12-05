package com.nju.bysj.softwaremodularisation.structure.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MyClassLoader extends ClassLoader {
    // 编译后的class路径
    private String[] targetPath;

    public MyClassLoader() {
        super();
    }

    public MyClassLoader(String[] targetPath){
        this.targetPath = targetPath;
    }

    /**
     * 重写findClass方法
     * @param fullClassName 是我们这个类的全路径
     * @return 目标类的class对象
     * @throws ClassNotFoundException
     */
    @Override
    protected Class<?> findClass(String fullClassName) throws ClassNotFoundException {
        Class<?> aClass = null;
        String fullClassFile = fullClassName.replace('.', '\\').concat(".class");
        // 获取该class文件字节码数组
        byte[] classData = null;
        for (int i = 0; i < targetPath.length; i++) {
//            System.out.println(targetPath[i] + fullClassFile);
            if ((classData = getData(targetPath[i] + fullClassFile)) != null) {
                break;
            }
        }

        if (classData == null) {
            throw new ClassNotFoundException("lack class: " + fullClassName);
        }
        // 将class的字节码数组转换成Class类的实例
        aClass = defineClass(fullClassName, classData, 0, classData.length);
        return aClass;
    }

    /**
     * 将class文件转化为字节码数组
     * @return
     */
    private byte[] getData(String path) {
        File file = new File(path);
        if (file.exists()){
            FileInputStream in = null;
            ByteArrayOutputStream out = null;
            try {
                in = new FileInputStream(file);
                out = new ByteArrayOutputStream();

                byte[] buffer = new byte[1024];
                int size;
                while ((size = in.read(buffer)) != -1) {
                    out.write(buffer, 0, size);
                }

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (IOException e) {

                    e.printStackTrace();
                }
            }
            return out.toByteArray();
        }else{
            return null;
        }
    }
}