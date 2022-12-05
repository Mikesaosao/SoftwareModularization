package com.nju.bysj.softwaremodularisation.nsga.objective;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nju.bysj.softwaremodularisation.nsga.Common;
import com.nju.bysj.softwaremodularisation.nsga.ParameterConfig;
import com.nju.bysj.softwaremodularisation.nsga.PreProcessLoadData;
import com.nju.bysj.softwaremodularisation.nsga.datastructure.Chromosome;
import com.nju.bysj.softwaremodularisation.nsga.datastructure.IntegerAllele;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.nju.bysj.softwaremodularisation.common.FileUtils.readFile;

public class ConcernGranularityObjective {

    /**
     * 判断个体是否是关注点过载的
     * @param chromosome
     * @return 过载-true；不过载-false
     */
    public static boolean ifChromosomeOverload(Chromosome chromosome) {
        HashMap<Integer, List<Integer>> serviceFiles = Common.splitChromosomeToFAFileListMap(chromosome);
        HashMap<Integer, List<Integer>> serviceConcernMap = getServiceConcernMap(serviceFiles);
        int maxConcernNum = serviceConcernMap.values().stream().mapToInt(List::size).max().getAsInt();
        return maxConcernNum > ParameterConfig.overload_threshold;
    }

    private static class ServiceProb {
        int serviceId;
        double prob;

        public ServiceProb(int serviceId, double prob) {
            this.serviceId = serviceId;
            this.prob = prob;
        }
    }

    public static HashMap<Integer, List<Integer>> getServiceConcernMap(Map<Integer, List<Integer>> serviceFiles) {
        // 带TC值的过载检测
        // key-concernId；value-List<serviceId, prob>
        HashMap<Integer, List<ServiceProb>> concernServiceMap = new HashMap<>();
        // 1、计算每个服务和所有主题的关联概率
        for(int topic = 0; topic < PreProcessLoadData.model.K; topic++) {
            for (Map.Entry<Integer, List<Integer>> service : serviceFiles.entrySet()) {
                List<Integer> files = service.getValue();
                // 当前微服务包含的主题词数量
                int serviceWordCount = 0;
                double serviceProbability = 0.0;

                // 统计当前服务下的主题词数量
                for (int file : files) {
                    // 每个文件分配的主题词数量
                    int fileWords = PreProcessLoadData.model.z[file].size();
                    serviceWordCount += fileWords;
                }

                // 累加每个代码文件中【主题词占比 * 主题文件关联概率】
                for (int file : files) {
                    double fileProb = PreProcessLoadData.model.theta[file][topic];
                    int fileWords = PreProcessLoadData.model.z[file].size();
//                    System.out.println("  adding " + ((double) fileWords / serviceWordCount * fileProb) + " service: " + service.getKey() +
//                            " ; file: " + file + " - " + refactorServiceFileList.get(file).substring(59) +
//                            " ; serviceWordCount: " + serviceWordCount + " ; fileWords: " + fileWords + " ; fileProb: " + fileProb);
                    serviceProbability += (double) fileWords / serviceWordCount * fileProb;
                }

                // 2、基于service_threshold和已识别关注点进行过滤筛选
//                System.out.println(service.getKey() + " - topic" + topic + " prob: " + serviceProbability);
                if (serviceProbability >= ParameterConfig.service_threshold && PreProcessLoadData.concerns.contains(topic)) {
                    if (!concernServiceMap.containsKey(topic)) {
                        concernServiceMap.put(topic, new ArrayList<>());
                    }
                    concernServiceMap.get(topic).add(new ServiceProb(service.getKey(), serviceProbability));
//                    System.out.println(service.getKey() + " - topic" + topic + " prob: " + serviceProbability);
                }
            }
        }

        // 3、对当前的 key-服务，value-关注点列表进行检测，保证每个关注点关联的服务数量仍使其满足TC值
        HashMap<Integer, List<Integer>> concernServiceListMap = new HashMap<>();
        List<Integer> allServiceFileIdList = PreProcessLoadData.allServiceFileList.stream()
                .map(f -> PreProcessLoadData.allServiceFileList.indexOf(f))
                .collect(Collectors.toList());

        for (Map.Entry<Integer, List<ServiceProb>> entry : concernServiceMap.entrySet()) {
            int concern = entry.getKey();
            Double originTC = PreProcessLoadData.concernTCMap.get(concern);
//            Double originTC = 0.0;
            List<ServiceProb> sortedDescServices = entry.getValue().stream()
                    .sorted((i1, i2) -> (Double.compare(i2.prob, i1.prob)))
                    .collect(Collectors.toList());
            double curTC = 1.0;
            List<Integer> selectedFile = new ArrayList<>();
            List<Integer> restFile = new ArrayList<>(allServiceFileIdList);

            List<Integer> selectedServices = new ArrayList<>();
            int index = 0;
            while (index < sortedDescServices.size() && curTC >= originTC) {
                int curService = sortedDescServices.get(index).serviceId;
                List<Integer> files = serviceFiles.get(curService);
                selectedServices.add(curService);

                for (int fileID : files) {
                    if (PreProcessLoadData.model.theta[fileID][concern] >= ParameterConfig.file_threshold) {
                        selectedFile.add(fileID);
                        restFile.remove((Integer) fileID);
                    }
                }

                curTC = calculateTC(selectedFile, restFile);
                index++;
            }
//            System.out.println("concern: " + concern + " ; before : " + sortedDescServices.size() + " ; after: " + selectedServices.size() + " ; index: " + index);
            concernServiceListMap.put(concern, selectedServices);
        }

        // 4、反推服务和关注点
        HashMap<Integer, List<Integer>> serviceConcernMap = new HashMap<>();
        for (Map.Entry<Integer, List<Integer>> entry : concernServiceListMap.entrySet()) {
            Integer c = entry.getKey();
            for (int s : entry.getValue()) {
                if (!serviceConcernMap.containsKey(s)) {
                    serviceConcernMap.put(s, new ArrayList<>());
                }
                serviceConcernMap.get(s).add(c);
            }
        }

        return serviceConcernMap;

        // 不带TC值的过载检测
//        HashMap<Integer, List<Integer>> serviceConcernMap = new HashMap<>();
//        // 1、计算每个服务和所有主题的关联概率
//        for(int topic = 0; topic < model.K; topic++) {
//            for (Map.Entry<Integer, List<Integer>> service : serviceFiles.entrySet()) {
//                List<Integer> files = service.getValue();
//                // 当前微服务包含的主题词数量
//                int serviceWordCount = 0;
//                double serviceProbability = 0.0;
//
//                // 统计当前服务下的主题词数量
//                for (int file : files) {
//                    // 每个文件分配的主题词数量
//                    int fileWords = model.z[file].size();
//                    serviceWordCount += fileWords;
//                }
//
//                // 累加每个代码文件中【主题词占比 * 主题文件关联概率】
//                for (int file : files) {
//                    double fileProb = model.theta[file][topic];
//                    int fileWords = model.z[file].size();
////                    System.out.println("  adding " + ((double) fileWords / serviceWordCount * fileProb) + " service: " + service.getKey() +
////                            " ; file: " + file + " - " + refactorServiceFileList.get(file).substring(59) +
////                            " ; serviceWordCount: " + serviceWordCount + " ; fileWords: " + fileWords + " ; fileProb: " + fileProb);
//                    serviceProbability += (double) fileWords / serviceWordCount * fileProb;
//                }
//
//                // 2、基于service_threshold和已识别关注点进行过滤筛选
//                if (serviceProbability >= service_threshold && concerns.contains(topic)) {
//                    if (!serviceConcernMap.containsKey(service.getKey())) {
//                        serviceConcernMap.put(service.getKey(), new ArrayList<>());
//                    }
//                    serviceConcernMap.get(service.getKey()).add(topic);
////                    System.out.println(service.getKey() + " - topic" + topic + " prob: " + serviceProbability);
//                }
//            }
//        }
//        return serviceConcernMap;
    }

    /**
     * 计算TC值
     * @param selected
     * @param rest
     * @return
     */
    private static double calculateTC(List<Integer> selected, List<Integer> rest) {
        // 当前主题已选文件的依赖数量
        int r_int = 0;
        // 当前主题已选文件和未选文件间的依赖数量
        int r_ext = 0;
        for (int i : selected) {
            for (int j : selected) {
                if (i != j && PreProcessLoadData.relationGraph[i][j] == 1) {
                    r_int += 1;
                }
            }
        }
        for (int i : selected) {
            for (int j : rest) {
                if (PreProcessLoadData.relationGraph[i][j] == 1) {
                    r_ext += 1;
                }
            }
        }
        return r_int / (r_int + r_ext + 0.000000000000001);
    }

    public static void main(String[] args) throws IOException {
        String jsonString = readFile("D:\\Development\\idea_projects\\NSGA-II\\output\\faPrograms.json");
        JSONArray programs = JSONArray.parseArray(jsonString);

        for (int i = 1; i < programs.size(); i++) {
            JSONObject program = programs.getJSONObject(i);
            System.out.println(program.getString("name") + " -------------------------------------------------------------");
            HashMap<Integer, List<Integer>> moduleFAMap = new HashMap<>();
            JSONArray services = program.getJSONArray("children");
            List<IntegerAllele> faIndexList = new ArrayList<>(PreProcessLoadData.faNums);
            for (int j = 0; j < PreProcessLoadData.faNums; j++) {
                faIndexList.add(new IntegerAllele(-1));
            }

            // 解析每个服务
            for (int j = 0; j < services.size(); j++) {
                JSONObject service = services.getJSONObject(j);
                String serviceName = service.getString("name");
                int serviceId = Integer.parseInt(serviceName.substring("服务".length()));
                JSONArray fas = service.getJSONArray("children");
                moduleFAMap.put(j + 1, new ArrayList<>());
                // 解析每个FA
                for (int k = 0; k < fas.size(); k++) {
                    String faName = fas.getJSONObject(k).getString("name");
                    int faId = Integer.parseInt(faName.substring("FA - ".length()));
                    moduleFAMap.get(j + 1).add(faId);
                    faIndexList.set(faId, new IntegerAllele(serviceId));
                }
            }

            Chromosome chromosome = new Chromosome(faIndexList);
            HashMap<Integer, List<Integer>> serviceFiles = Common.splitChromosomeToFAFileListMap(chromosome);
            System.out.println(ifChromosomeOverload(chromosome));

            HashMap<Integer, List<Integer>> serviceConcernMap = getServiceConcernMap(serviceFiles);
            for (Map.Entry<Integer, List<Integer>> entry : moduleFAMap.entrySet()) {
                int sid = entry.getKey();
                System.out.println("service: " + sid);
                System.out.println("  fa list: " + moduleFAMap.get(sid) + " ; files: " + serviceFiles.get(sid).size());
                System.out.println("  concerns: " + serviceConcernMap.getOrDefault(sid, new ArrayList<>()) +
                        "  sum: " + serviceConcernMap.getOrDefault(sid, new ArrayList<>()).size());
            }
        }
    }
}
