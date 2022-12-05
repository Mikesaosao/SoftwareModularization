package com.nju.bysj.softwaremodularisation.nsga.evaluation;

import com.nju.bysj.softwaremodularisation.nsga.Common;
import com.nju.bysj.softwaremodularisation.nsga.datastructure.Chromosome;

import java.util.HashMap;
import java.util.List;

import static com.nju.bysj.softwaremodularisation.nsga.PreProcessLoadData.classSimMatrix;

public class SemanticCohesion {
    public double getValue(Chromosome chromosome) {
        HashMap<Integer, List<Integer>> srvFilesMap = Common.splitChromosomeToServiceFileIdMap(chromosome);
        return calculateSemanticCohesion(srvFilesMap);
    }

    public double calculateSemanticCohesion(HashMap<Integer, List<Integer>> srvFilesMap) {
        double coh = 0;
        int M = srvFilesMap.size();
        double cohesion;

        for(List<Integer> srvFiles: srvFilesMap.values()) {
            coh += calculateSingleSrvSemanticCohesion(srvFiles);
        }
        cohesion = 1 - coh / M;

        return cohesion;
    }

    public double calculateSingleSrvSemanticCohesion(List<Integer> srvFiles) {
        double coh = 0;
        int m = srvFiles.size();
        double sims = 0;

        if(m == 1) {
            return 0;
        }

        for(int i = 0; i < m; i++) {
            int src = srvFiles.get(i);
            for(int j = i + 1; j < m; j++) {
                int dest = srvFiles.get(j);
                sims += classSimMatrix[src][dest];
            }
        }
        coh = 1 - sims / ((double) m * (m - 1) / 2);
        return coh;
    }

}
