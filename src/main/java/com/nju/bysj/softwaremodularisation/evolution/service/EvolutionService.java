package com.nju.bysj.softwaremodularisation.evolution.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nju.bysj.softwaremodularisation.evolution.entity.Commit;

import java.io.IOException;
import java.util.*;

import static com.nju.bysj.softwaremodularisation.common.FileDirManage.fileNameDir;
import static com.nju.bysj.softwaremodularisation.common.FileUtils.*;
import static com.nju.bysj.softwaremodularisation.nsga.PreProcessLoadData.*;

public class EvolutionService {
    public static HashMap<String, Commit> getEvolutionData(String filePath) throws IOException {
        HashMap<String, Commit> evolutionMap = new HashMap<>();
        String jsonString = readFile(filePath);
        System.out.println("读取：evolution 成功");
        JSONObject jsonObject = JSONObject.parseObject(jsonString);

        List<String> filesList = readFileList(fileNameDir);
        for(String commitID: jsonObject.keySet()) {

            JSONObject data = jsonObject.getJSONObject(commitID);
            JSONArray fileList = data.getJSONArray("files");
            String developer = data.getString("committer");

            for(Object file: fileList.toArray()) {
                String fileName = file.toString();
                if (!fileName.endsWith(".java") || !filesList.contains(fileName)) {
                    continue;
                }
                Commit commit;
                if(evolutionMap.containsKey(fileName)) {
                    commit = evolutionMap.get(fileName);
                } else {
                    commit = new Commit(fileName, new HashSet<>(), new HashSet<>());
                    evolutionMap.put(fileName, commit);
                }

                commit.addCommit(commitID);
                commit.addDevelopers(developer);
            }
        }
        return evolutionMap;
    }


    public static void calculateEvolutionMatrix(HashMap<String, Commit> evolutionMap) throws IOException {
//List<String> classFileList;
        int len = classFileList.size();
        classEvolutionMatrix = new int[len][len];
        classCoChangeMatrix = new int[len][len];

        for(int i = 0; i < len; i++) {
            for(int j = i + 1; j < len; j++) {
                if(i == j) {
                    continue;
                }

                String file1 = classFileList.get(i);
                String file2 = classFileList.get(j);

                Commit commitI = evolutionMap.get(file1);
                Commit commitJ = evolutionMap.get(file2);

                Set<String> commitIdsI = commitI.getCommits();
                Set<String> commitIdsJ = commitJ.getCommits();

                Set<String> devI = commitI.getDevelopers();
                Set<String> devJ = commitJ.getDevelopers();
                if(isEvoCoupling(commitIdsI, commitIdsJ) && isDevCoupling(devI, devJ)) {
//                    System.out.println(file1 + ":" + file2);
                    classEvolutionMatrix[i][j] = 2;
                    classEvolutionMatrix[j][i] = 2;
                } else if (isEvoCoupling(commitIdsI, commitIdsJ) || isDevCoupling(devI, devJ)) {
                    classEvolutionMatrix[i][j] = 1;
                    classEvolutionMatrix[j][i] = 1;
                }

                int coChangeNums = coChange(commitIdsI, commitIdsJ);
                classCoChangeMatrix[i][j] = coChangeNums;
                classCoChangeMatrix[j][i] = coChangeNums;
            }
        }
        FileDependence object1 = new FileDependence(new ArrayList<>(evolutionMap.keySet()), classCoChangeMatrix);
        String relationString1 = JSONObject.toJSONString(object1, true);
        writeObjString(relationString1, "//Users/mike/Desktop/系统模块化方法/实验数据/test.json");

    }

    public static int coChange(Set<String> commitIdsI, Set<String> commitIdsJ) {
        int nums = 0;

        for(String commitId: commitIdsJ) {
            if(commitIdsI.contains(commitId)) {
                nums += 1;
            }
        }

        return nums;
    }

    public static boolean isEvoCoupling(Set<String> commitIdsI, Set<String> commitIdsJ) {
        int molecular = 0;
        int denominator = commitIdsI.size();

        for(String commitID: commitIdsJ) {
            if(commitIdsI.contains(commitID)) {
                molecular++;
            } else {
                denominator++;
            }
        }

        double val = (double) molecular / denominator;
        return val >= commitThreshold;
    }

    public static boolean isDevCoupling(Set<String> devI, Set<String> devJ) {
        int molecular = 0;
        int denominator = devI.size();

        for(String commitID: devJ) {
            if(devI.contains(commitID)) {
                molecular++;
            } else {
                denominator++;
            }
        }

        double val = (double) molecular / denominator;
        return val >= devThreshold;
    }

    public static void main(String[] args) throws IOException {
        getEvolutionData("/Users/mike/PycharmProjects/Test/evolution.json");
    }
}
