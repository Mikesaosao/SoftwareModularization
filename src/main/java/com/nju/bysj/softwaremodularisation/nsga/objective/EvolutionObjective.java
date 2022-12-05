package com.nju.bysj.softwaremodularisation.nsga.objective;

import com.nju.bysj.softwaremodularisation.nsga.datastructure.Chromosome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.nju.bysj.softwaremodularisation.nsga.Common.splitChromosomeToServiceFileIdMap;
import static com.nju.bysj.softwaremodularisation.nsga.PreProcessLoadData.classEvolutionMatrix;

public class EvolutionObjective extends AbstractObjectiveFunction{
    public EvolutionObjective() {
        this.objectiveFunctionTitle = "Evolution Index";
    }
    @Override
    public double getValue(Chromosome chromosome) {
        // 得到的是每个srv - 在overloadServiceFileList文件中的idList
        HashMap<Integer, List<Integer>> srvFilesMap = splitChromosomeToServiceFileIdMap(chromosome);
        double evolutionCohesion = getAllServiceEvolutionCohesion(srvFilesMap);
        double evolutionCoupling = getAllServiceEvolutionCoupling(srvFilesMap);
        return evolutionCohesion -evolutionCoupling;
    }

    public double getAllServiceEvolutionCoupling(HashMap<Integer, List<Integer>> srvFilesMap) {
        double totalCosineSim = 0;
        for (Map.Entry<Integer, List<Integer>> entry : srvFilesMap.entrySet()) {
            totalCosineSim += calculateSingleSrvEvolutionCohesion(entry.getValue());
        }
        return totalCosineSim / srvFilesMap.size();
    }

    public double calculateSingleSrvEvolutionCohesion(List<Integer> srvFiles) {
        int fileNum = srvFiles.size();
        // 当前服务只有一个文件的话，返回比较低的相似度
        if (fileNum == 1) {
            return 0;
        }
        double totalCosineSim = 0;
        for (int i = 0; i < fileNum; i++) {
            for (int j = 0; j < fileNum; j++) {
                Integer f1 = srvFiles.get(i);
                Integer f2 = srvFiles.get(j);
                if (i == j) {
                    totalCosineSim += 1;
                } else if (i > j) {
                    totalCosineSim += classEvolutionMatrix[f2][f1];
                } else {
                    totalCosineSim += classEvolutionMatrix[f1][f2];
                }
            }
        }
        return totalCosineSim / fileNum / fileNum;
    }

    public double getAllServiceEvolutionCohesion(HashMap<Integer, List<Integer>> srvFilesMap) {
        double totalSim = 0;
        List<Integer> srvList = new ArrayList<>(srvFilesMap.keySet());
        int srvNum = srvList.size();
        if (srvNum == 1) {
            return 0;
        }

        List<List<Integer>> srvFilesList = new ArrayList<>(srvFilesMap.values());
        for (int i = 0; i < srvFilesList.size(); i++) {
            List<Integer> files1 = srvFilesList.get(i);
            for (int j = i + 1; j < srvFilesList.size(); j++) {
                List<Integer> files2 = srvFilesList.get(j);
                totalSim += calculatePairSrvEvolutionCoupling(files1, files2);
            }
        }
        return totalSim * 2 / (srvNum - 1) / srvNum;
    }

    public double calculatePairSrvEvolutionCoupling(List<Integer> files1, List<Integer> files2) {
        if (files1.size() <= 1 && files2.size() <= 1) {
            return 0;
        }
        double totalSim = 0;
        for (int i : files1) {
            for (int j : files2) {
                totalSim += classEvolutionMatrix[i][j];
            }
        }
        return totalSim / files1.size() / files2.size() / 2;
    }
}
