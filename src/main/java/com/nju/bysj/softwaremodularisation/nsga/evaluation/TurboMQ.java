package com.nju.bysj.softwaremodularisation.nsga.evaluation;

import com.nju.bysj.softwaremodularisation.nsga.Common;
import com.nju.bysj.softwaremodularisation.nsga.datastructure.Chromosome;

import java.util.HashMap;
import java.util.List;

import static com.nju.bysj.softwaremodularisation.nsga.PreProcessLoadData.classCallGraph;

public class TurboMQ {
    public double getValue(Chromosome chromosome) {
        HashMap<Integer, List<Integer>> srvFilesMap = Common.splitChromosomeToServiceFileIdMap(chromosome);
        double val = calculateTurboMQ(srvFilesMap);
        return val;
    }

    public double calculateTurboMQ(HashMap<Integer, List<Integer>> srvFilesMap) {
        double turboMQ = 0.0;

        for(int id: srvFilesMap.keySet()) {
            turboMQ += calculateMF(id,srvFilesMap);
        }
        return turboMQ;
    }

    public double calculateMF(int srvId, HashMap<Integer, List<Integer>> srvFilesMap) {
        List<Integer> srvFiles = srvFilesMap.get(srvId);
        int innerCalls = getInnerDependency(srvFiles);
        int intraCalls = 0;
        if(innerCalls == 0) {
            return 0;
        }

        for(int id: srvFilesMap.keySet()) {
            if(id == srvId) continue;
            List<Integer> otherSrvFiles = srvFilesMap.get(id);
            innerCalls += getIntraDependency(srvFiles,otherSrvFiles) + getIntraDependency(otherSrvFiles,srvFiles);
        }

        double mf = (double) 2 * innerCalls / (2 * innerCalls + intraCalls);
        return mf;
    }

    public int getInnerDependency(List<Integer> srvFiles) {
        int innerCalls = 0;
        for(int i = 0; i < srvFiles.size(); i++) {
            int src = srvFiles.get(i);
            for(int j = 0; j < srvFiles.size(); j++) {
                int dest = srvFiles.get(j);
                if (i == j) continue;
                innerCalls += classCallGraph[src][dest];
            }
        }
        return innerCalls;
    }

    public int getIntraDependency(List<Integer> srvFilesI, List<Integer> srvFilesJ) {
        int intraCalls = 0;
        for(int i = 0; i < srvFilesI.size(); i++) {
            for(int j = 0; j < srvFilesJ.size();j ++) {
                intraCalls += classCallGraph[i][j];
            }
        }
        return intraCalls;
    }

}
