package com.nju.bysj.softwaremodularisation.nsga.evaluation;

import com.nju.bysj.softwaremodularisation.nsga.Common;
import com.nju.bysj.softwaremodularisation.nsga.datastructure.Chromosome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.nju.bysj.softwaremodularisation.nsga.PreProcessLoadData.classCoChangeMatrix;

public class REI {
    public double getValue(Chromosome chromosome) {
        HashMap<Integer, List<Integer>> srvFilesMap = Common.splitChromosomeToServiceFileIdMap(chromosome);
        return calculateREI(srvFilesMap);
    }

    public double calculateREI(HashMap<Integer, List<Integer>> srvFilesMap) {
        double ECF = calculateECF(srvFilesMap);
        double ICF = calculateICF(srvFilesMap);
        return ECF / ICF;
    }

    public double calculateICF(HashMap<Integer, List<Integer>> srvFilesMap) {
        double ICF = 0;
        int N = srvFilesMap.size();
        for(List<Integer> srvFiles: srvFilesMap.values()) {
            ICF += calculateSingleSrvIcf(srvFiles);
        }
        return ICF / N;
    }

    public double calculateSingleSrvIcf(List<Integer> srvFiles) {
        int len = srvFiles.size();
        double icf;
        double fmt = 0;
        for(int i = 0; i < len; i++) {
            int src = srvFiles.get(i);
            int tmp = 0;
            for(int j = i + 1; j < len; j++) {
                int dest = srvFiles.get(j);
                tmp += classCoChangeMatrix[src][dest];
            }

            fmt += (double) tmp / len;
        }
        icf = fmt / len;
        return icf;
    }

    public double calculateECF(HashMap<Integer, List<Integer>> srvFilesMap) {
        double ECF = 0;
        int N = srvFilesMap.size();
        for(int id: srvFilesMap.keySet()) {
            ECF += calculateSingleSrvEcf(id, srvFilesMap);
        }

        return ECF / N;
    }

    public double calculateSingleSrvEcf(int srvId, HashMap<Integer, List<Integer>> srvFilesMap) {
        List<Integer> srvFiles = srvFilesMap.get(srvId);
        List<Integer> otherFiles = new ArrayList<>();
        for(int id: srvFilesMap.keySet()) {
            if(id == srvId) continue;
            otherFiles.addAll(srvFilesMap.get(id));
        }
        int srvLen = srvFiles.size();
        int otherSrvLen = otherFiles.size();
        double fmt = 0;
        double ecf;

        for(int i = 0; i < srvLen; i++) {
            int src = srvFiles.get(i);
            int tmp = 0;
            for(int j = 0; j < otherSrvLen; j++) {
                int dest = otherFiles.get(j);
                tmp += classCoChangeMatrix[src][dest];
            }

            fmt += (double) tmp / otherSrvLen;
        }

        ecf = fmt / srvLen;

        return ecf;
    }
}
