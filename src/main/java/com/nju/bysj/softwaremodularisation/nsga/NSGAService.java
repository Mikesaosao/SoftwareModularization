package com.nju.bysj.softwaremodularisation.nsga;

import com.nju.bysj.softwaremodularisation.nsga.crossover.FileRandomCrossover;
import com.nju.bysj.softwaremodularisation.nsga.crossover.FileSingleCrossover;
import com.nju.bysj.softwaremodularisation.nsga.mutation.FileModularMutation;
import com.nju.bysj.softwaremodularisation.nsga.mutation.FileRandomMutation;
import com.nju.bysj.softwaremodularisation.nsga.objective.ObjectiveProvider;
import com.nju.bysj.softwaremodularisation.nsga.plugin.DefaultPluginProvider;
import com.nju.bysj.softwaremodularisation.nsga.runbody.Configuration;
import com.nju.bysj.softwaremodularisation.nsga.runbody.FileNsga2;
import com.nju.bysj.softwaremodularisation.nsga.termination.TerminatingCriterionProvider;
import org.springframework.stereotype.Service;

import static com.nju.bysj.softwaremodularisation.nsga.Common.appendStringToFile;
import static com.nju.bysj.softwaremodularisation.nsga.Common.getFourBitsDoubleString;
import static com.nju.bysj.softwaremodularisation.nsga.ParameterConfig.*;

@Service
public class NSGAService {

    public void initParamConfig(int generation, int population, int maxRecord, float overloadRemainThreshold,
                                float crossoverProb, float mutationProb, float breakProb, double modelService,
                                double modelFile, int overloadThreshold) {
        // 前端只运行一次
        experimentTimes = 1;
//        ParameterConfig.overloadRemainThreshold = overloadRemainThreshold;
        ParameterConfig.crossoverProb = crossoverProb;
        ParameterConfig.mutationProb = mutationProb;
        ParameterConfig.breakProb = breakProb;
        ParameterConfig.service_threshold = modelService;
        ParameterConfig.file_threshold = modelFile;
        overload_threshold = overloadThreshold;

        faMaxGeneration = generation;
        fileMaxGeneration = generation;
        faMaxPopulationSize = population;
        fileMaxPopulationSize = population;
        faMaxRecord = maxRecord;
        fileMaxRecord = maxRecord;
    }

    public void fileRefactorTest(Configuration configuration, int startIter) {
        // 代码文件粒度重构，初始化种群
        configuration.setPopulationProducer(DefaultPluginProvider.fileInitPopulationProducer());
        configuration.objectives = ObjectiveProvider.provideFileStructuralAndSemanticAndEvolution();
        // 交叉操作
        configuration.setChromosomeLength(PreProcessLoadData.classFileList.size());
        configuration.setCrossover(new FileSingleCrossover(null, crossoverProb));
        // 变异操作 - mutationProbability是在refactorGenerateChildrenProducerEncode方法中用的
        configuration.setMutation(new FileModularMutation(mutationProb, breakProb));
        // 子代更新方法
        configuration.setChildPopulationProducer(DefaultPluginProvider.childrenProducer(mutationProb));
        // 最大代数，与终止条件有关
        configuration.setGenerations(fileMaxGeneration);
        // 原意是种群的固定数量，但在本研究中，种群数量是从1开始的，次值应该是种群上限
        configuration.setPopulationSize(fileMaxPopulationSize);
        // 遗传终止方法
        configuration.setTerminatingCriterion(TerminatingCriterionProvider.refactorTerminatingCriterion(
                fileMaxPopulationSize, fileMaxRecord));

        FileNsga2 fileNsga2 = new FileNsga2(configuration);
        for (int i = startIter; i <= experimentTimes; i++) {
            System.out.println("experiment - " + i + " -------------------------------------------------------------------");
            PreProcessLoadData.experimentInitDataStructure("file");

            long start = System.currentTimeMillis();
            fileNsga2.run(i, PostProcessShowData.outputFileProgramJson, PostProcessShowData.outputFileObjectiveTxt, PostProcessShowData.outputFileFrontTxt, PostProcessShowData.outputFileCostTxt);
            long end = System.currentTimeMillis();

            appendStringToFile("\ntime:" + (end - start) / 1000.0, PostProcessShowData.outputFileCostTxt + i + ".txt", false);
            System.out.println("运行时间：" + getFourBitsDoubleString((end - start) / 1000.0) + " s");
        }
    }

    public void fileRandomSearchTest(Configuration configuration, int startIter) {
        // 代码文件粒度的随机搜索，初始化种群
        configuration.setPopulationProducer(DefaultPluginProvider.fileInitPopulationProducer());
        configuration.objectives = ObjectiveProvider.provideFileStructuralAndSemanticAndEvolution();
        // 交叉操作
        configuration.setChromosomeLength(PreProcessLoadData.classFileList.size());
        configuration.setCrossover(new FileRandomCrossover(null, crossoverProb));
        // 变异操作 - mutationProbability是在refactorGenerateChildrenProducerEncode方法中用的
        configuration.setMutation(new FileRandomMutation(mutationProb));
        // 子代更新方法，参数暂时没用到
        configuration.setChildPopulationProducer(DefaultPluginProvider.randomChildrenProducer(mutationProb));
        // 最大代数，与终止条件有关
        configuration.setGenerations(fileMaxGeneration);
        // 原意是种群的固定数量，但在本研究中，种群数量是从1开始的，次值应该是种群上限
        configuration.setPopulationSize(fileMaxPopulationSize);
        // 遗传终止方法
        configuration.setTerminatingCriterion(TerminatingCriterionProvider.refactorTerminatingCriterion(
                fileMaxPopulationSize, fileMaxRecord));

        FileNsga2 rs = new FileNsga2(configuration);
        for (int i = 1; i <= experimentTimes; i++) {
            PreProcessLoadData.experimentInitDataStructure("file");

            long start = System.currentTimeMillis();
            rs.run(i, PostProcessShowData.outputFileRandomProgramJson, PostProcessShowData.outputFileRandomObjectiveTxt, PostProcessShowData.outputFileRandomFrontTxt, PostProcessShowData.outputFileRandomCostTxt);
            long end = System.currentTimeMillis();

            appendStringToFile("\ntime:" + (end - start) / 1000.0, PostProcessShowData.outputFileRandomCostTxt + i + ".txt", false);
            System.out.println("运行时间：" + getFourBitsDoubleString((end - start) / 1000.0) + " s");
        }
    }

    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        NSGAService service = new NSGAService();
//
        long start = System.currentTimeMillis();
////        faRefactorTest(configuration, 1);
////        faRandomSearchTest(configuration, 1);
        service.fileRefactorTest(configuration, 1);
//        fileRandomSearchTest(configuration, 1);
//
        long end = System.currentTimeMillis();
        System.out.println("任务总耗时：" + getFourBitsDoubleString((end - start) / 1000.0));
    }

}
