package com.nju.bysj.softwaremodularisation.common;

import com.alibaba.fastjson.JSONObject;
import com.nju.bysj.softwaremodularisation.structure.service.Scanner;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

@Scanner
public class FileUtils {

    /**
     * 按行写入一个list，直接覆盖源文件
     * @param dataList
     * @param outputPath
     * @throws IOException
     */
    public static void writeByLine(List<String> dataList, String outputPath) throws IOException {
        File file = new File(outputPath);
        if (file.exists()) {
            file.delete();
        }

        OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
        BufferedWriter bw = new BufferedWriter(osw);

        for (String data : dataList) {
            bw.write(data);
            bw.newLine();
            bw.flush();
        }

        osw.close();
        bw.close();
    }

    /**
     * relation.json写入的对象
     */
    public static class FileDependence {
        public List<String> fileSequence;
        public int[][] dependGraph;

        public FileDependence(List<String> fileSequence, int[][] dependGraph) {
            this.fileSequence = fileSequence;
            this.dependGraph = dependGraph;
        }
    }

    /**
     * 读取指定路径的文件到字符串中
     * @param path
     * @return
     */
    public static String readFile(String path) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileInputStream is = new FileInputStream(path);
            InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            is.close();
            isr.close();
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    /**
     * 读取指定目录下的文件，返回一个list
     * @return
     */
    public static List<String> readFileList(String inputPath) throws IOException {
        List<String> dataList = new ArrayList<>();
        InputStreamReader isr = new InputStreamReader(new FileInputStream(inputPath), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null) {
            dataList.add(line);
        }
        isr.close();
        br.close();
        return dataList;
    }

    /**
     * 将json字符串写入文件
     * @param jsonStr
     * @param outputPath
     */
    public static void writeObjString(String jsonStr, String outputPath) throws IOException {
        Path path = Paths.get(outputPath);
        if (Files.exists(path)) {
            Files.delete(path);
        }
        Files.createFile(path);
        System.out.println(outputPath + " 创建成功");
        try {
            Files.write(path, jsonStr.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(outputPath + " 写入成功");
    }

    /**
     * 基于路径和过滤规则扫描指定目标下的全部代码文件，返回一个list
     * @param file
     * @param filter
     * @return
     */
    public static List<String> scan(File file, FileFilter filter) {
        File[] files = file.listFiles(filter);
        List<String> fileList = new ArrayList<>();
        for (File f : files) {
            if (f.isFile()) {
                fileList.add(f.getAbsolutePath());
            } else {
                fileList.addAll(scan(f, filter));
            }
        }
        return fileList;
    }

    /**
     * cluster.json写入对象
     */
    static class FACluster {
        public List<List<String>> clusters;
    }

    /**
     * 聚类结果写入文件
     * @param outputPath
     * @param clusters
     * @param fileSequence
     */
    public static void writeClusterToJson(String outputPath, List<List<Integer>> clusters, List<String> fileSequence) throws IOException {
        FACluster object = new FACluster();
        List<List<String>> fileClusters = new ArrayList<>();
        for (List<Integer> cluster : clusters) {
            List<String> fileCluster = new ArrayList<>();
            for (int n : cluster) {
                fileCluster.add(fileSequence.get(n));
            }
            fileClusters.add(fileCluster);
        }
        object.clusters = fileClusters;

        String objString = JSONObject.toJSONString(object, true);
        Path path = Paths.get(outputPath);
        if (Files.exists(path)) {
            Files.delete(path);
        }
        Files.createFile(path);
        System.out.println(outputPath + " 创建成功");
        try {
            Files.write(path, objString.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(outputPath + " 写入成功");
    }

    /**
     * 初始化K个簇
     * @param k
     * @param medoides
     * @return
     */
    public static List<List<Integer>> newCluster(int k, List<Integer> medoides) {
        List<List<Integer>> clusters = new ArrayList<>(k);
        for (int i = 0; i < k; i++) {
            List<Integer> item = new ArrayList<>();
            item.add(medoides.get(i));
            clusters.add(item);
        }
        return clusters;
    }

    /**
     * 打印聚类结果
     * @param k
     * @param medoides
     * @param clusters
     * @param fileSequence
     */
    public static void printClusters(int k, List<Integer> medoides, List<List<Integer>> clusters, List<String> fileSequence) {
        for (int i = 0; i < k; i++) {
            List<Integer> cluster = clusters.get(i);
            System.out.println("簇" + (i+1) + " 中心点: " + medoides.get(i) + " 文件数量: " + cluster.size() + " =======================================================");
//            for (Integer n : cluster) {
//                    System.out.print(n + " ");
//                System.out.println(fileSequence.get(n).substring(29));
//            }
//                System.out.println();
        }
    }


    public static void main(String[] args) {
        System.out.println("D:\\Development\\idea_projects\\microservices-platform-master\\".length());
    }
}
