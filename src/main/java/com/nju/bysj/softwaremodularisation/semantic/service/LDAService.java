package com.nju.bysj.softwaremodularisation.semantic.service;

import com.nju.bysj.softwaremodularisation.semantic.lda.Estimator;
import com.nju.bysj.softwaremodularisation.semantic.lda.LDAOption;
import com.nju.bysj.softwaremodularisation.semantic.lda.Model;
import com.nju.bysj.softwaremodularisation.semantic.preprocess.Corpus;
import com.nju.bysj.softwaremodularisation.semantic.preprocess.Document;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import static com.nju.bysj.softwaremodularisation.common.FileDirManage.*;
import static com.nju.bysj.softwaremodularisation.common.Utils.javaAndDirectoryFilter;
import static com.nju.bysj.softwaremodularisation.semantic.preprocess.PreProcessMethods.removeStopWords;
import static com.nju.bysj.softwaremodularisation.semantic.preprocess.PreProcessMethods.splitIdentifier;

import static com.nju.bysj.softwaremodularisation.semantic.preprocess.CommonStopWordList.myStopWords;
import static com.nju.bysj.softwaremodularisation.semantic.preprocess.PreProcessMethods.*;


import static com.nju.bysj.softwaremodularisation.common.Utils.javaAndDirectoryFilter;

@Service
public class LDAService {

    public void nlpPreprocess(String[] srvPaths) {
        Corpus corpus = preByService(srvPaths);
        // content1存储所有文件的words列表通过空格拼接而成的字符串
        StringBuilder content1 = new StringBuilder();
        content1.append(corpus.documents.size()).append("\n");
        for (Document doc : corpus.documents) {
            String line = String.join(" ", doc.words);
            line += "\n\n";
            content1.append(line);
        }

        // content2存储所有文件的绝对路径名
        StringBuilder content2 = new StringBuilder();
        for (String filename : corpus.fileNames) {
            content2.append(filename).append("\n");
        }

        try {
            File file = new File(wordsDir);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
                if (!file.exists()) {
                    file.createNewFile();
                }
            }

            FileWriter fileWriter = new FileWriter(file.getAbsolutePath(), false);
            fileWriter.write(content1.toString());
            fileWriter.close();

            File file2 = new File(filenameDir);
            if (!file2.getParentFile().exists()) {
                file2.getParentFile().mkdirs();
                if (!file2.exists()) {
                    file2.createNewFile();
                }
            }

            FileWriter fileWriter2 = new FileWriter(file2.getAbsolutePath(), false);
            fileWriter2.write(content2.toString());
            fileWriter2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        LDAService ldaService = new LDAService();
        ldaService.nlpPreprocess(new String[]{"/Users/mike/Desktop/SoftwareModularization/data/spring-petclinic-main"});
    }

    private Corpus preByService(String[] srvPaths) {
        //总的词袋
        Corpus allCorpus = new Corpus();
        allCorpus.init(new ArrayList<>(), new ArrayList<>());
        // 扫描目标路径下的所有代码文件（文件树的DFS遍历）
        for(String mpath : srvPaths){
            Corpus corpus = new Corpus();
            //将target_projects目录下的案例项目文件夹用自定义过滤器过滤出来，所有符合要求的文件绝对路径名
            corpus.init(mpath, javaAndDirectoryFilter);

            splitIdentifier(corpus);
            toLowerCase(corpus);
            removeStopWords(corpus, myStopWords);
            filtering(corpus);
            tf_idf(corpus);
            stemming(corpus);

            allCorpus.fileNames.addAll(corpus.fileNames);
            allCorpus.documents.addAll(corpus.documents);
        }
        Map<String, Integer> wordfrequency = new HashMap<>();
        for (Document document : allCorpus.documents) {
            for (String w : document.words) {
                if (!wordfrequency.containsKey(w)) {
                    wordfrequency.put(w, 0);
                }
                wordfrequency.put(w, wordfrequency.get(w) + 1);
            }
        }
        return allCorpus;
    }
}
