package com.nju.bysj.softwaremodularisation.structure.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nju.bysj.softwaremodularisation.structure.DependsCommand;
import com.nju.bysj.softwaremodularisation.structure.LangRegister;
import com.nju.bysj.softwaremodularisation.structure.ParameterException;
import com.nju.bysj.softwaremodularisation.structure.addons.DV8MappingFileBuilder;
import com.nju.bysj.softwaremodularisation.structure.extractor.AbstractLangProcessor;
import com.nju.bysj.softwaremodularisation.structure.extractor.LangProcessorRegistration;
import com.nju.bysj.softwaremodularisation.structure.extractor.UnsolvedBindings;
import com.nju.bysj.softwaremodularisation.structure.format.DependencyDumper;
import com.nju.bysj.softwaremodularisation.structure.format.detail.UnsolvedSymbolDumper;
import com.nju.bysj.softwaremodularisation.structure.format.path.*;
import com.nju.bysj.softwaremodularisation.structure.generator.DependencyGenerator;
import com.nju.bysj.softwaremodularisation.structure.generator.FileDependencyGenerator;
import com.nju.bysj.softwaremodularisation.structure.generator.FunctionDependencyGenerator;
import com.nju.bysj.softwaremodularisation.structure.matrix.core.DependencyMatrix;
import com.nju.bysj.softwaremodularisation.structure.matrix.transform.MatrixLevelReducer;
import com.nju.bysj.softwaremodularisation.structure.matrix.transform.strip.LeadingNameStripper;
import com.nju.bysj.softwaremodularisation.structure.util.FileUtil;
import com.nju.bysj.softwaremodularisation.structure.util.FolderCollector;
import com.nju.bysj.softwaremodularisation.structure.util.TemporaryFile;
import edu.emory.mathcs.backport.java.util.Arrays;
import net.sf.ehcache.CacheManager;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.stereotype.Service;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static com.nju.bysj.softwaremodularisation.common.FileDirManage.fileNameDir;
import static com.nju.bysj.softwaremodularisation.common.FileUtils.*;
import static com.nju.bysj.softwaremodularisation.common.Utils.javaAndDirectoryFilter;

@Service
public class DependencyExtractorService {

	public static void main(String[] args) throws IOException {
//         静态代码分析方面 ---------------------------------------------------------------------------------------------
//	     step1、代码静态解析，获得dependency.json依赖关系
        String source = "/Users/mike/Desktop/SoftwareModularization/data/spring-petclinic-main";
//		// 输出文件名称，自动加json后缀
		String outputDir = "/Users/mike/Desktop/系统模块化方法/实验数据/output";
        String filesListPath = fileNameDir;
//	    mineParser(lang, source, outputName, outputDir);
        getStructureData(source, filesListPath, outputDir, "Call");
    }

    /**
     * 生成指定依赖类型的结构数据
     */
    public static void getStructureData(String source, String filesListPath, String outputDir, String dependencyType) throws IOException {
        String lang = "java";
        String outputName = "dependency";
        mineParser(lang, source, outputName, outputDir);

        String jsonFilePath = outputDir + '/' + outputName + ".json";
        String jsonString = readFile(jsonFilePath);
        JSONObject jsonObject = JSONObject.parseObject(jsonString);

        HashMap<String, Object> structureData = new HashMap<>();
        structureData.put("vertex",new ArrayList<>());
        structureData.put("edge", new ArrayList<>());
        structureData.put("module", new ArrayList<>());

        ArrayList<Object> vertexMap = (ArrayList<Object>) structureData.get("vertex");
        ArrayList<Object> edgeMap = (ArrayList<Object>) structureData.get("edge");
//        // step2、写到文件中
//        writeByLine(srvFileList, outputFilesPath);
        JSONArray fileArray = jsonObject.getJSONArray("variables");
        int index = 0;
        for (Object aFileArray : fileArray) {
//            System.out.println(aFileArray);
            String filePath = aFileArray.toString();
            HashMap<String, Object> entityInfoMap = new HashMap<>();
            entityInfoMap.put("index", index);
            entityInfoMap.put("name", filePath);
            vertexMap.add(entityInfoMap);
            index ++;
        }

        JSONArray depArray = jsonObject.getJSONArray("cells");
        for(Object depInfo: depArray) {
            HashMap<String, Object> edgeInfo = new HashMap<>();
            JSONObject depInfoObject = JSONObject.parseObject(depInfo.toString());
            HashMap<String, Object> depInfoMap = (HashMap<String, Object>) depInfoObject.getInnerMap();
            HashMap<String, Object> depTypeMap = (HashMap<String, Object>) JSONObject.parseObject(depInfoMap.get("values").toString()).getInnerMap();
            String src = depInfoMap.get("src").toString();
            String dest = depInfoMap.get("dest").toString();
            if(!depTypeMap.containsKey(dependencyType)) {
                continue;
            }
            String weight = depTypeMap.get(dependencyType).toString();
            edgeInfo.put("src", src);
            edgeInfo.put("dest", dest);
            edgeInfo.put("weight", weight);
            edgeInfo.put("dependencyType", dependencyType);
            edgeMap.add(edgeInfo);

        }

        JSONObject structureJson = new JSONObject(structureData);
        String callJsonStr = JSONObject.toJSONString(structureJson);
        System.out.println(jsonFilePath);
        // 输出路径
        writeObjString(callJsonStr, outputDir + "/data.json");
        getRelationJson(jsonFilePath, filesListPath,outputDir + "/relation.json", outputDir + "/relationWeight.json", "Call");

    }

    /**
     * 生成dependency.json
     */
	public static void mineParser(String lang, String source, String outputName, String outputDir) {
        try {
            LangRegister langRegister = new LangRegister();
            langRegister.register();
            DependsCommand app = new DependsCommand();
            // 设置要解析的语言
            app.setLang(lang);
            // 要分析的源代码文件目录
            app.setSrc(source);
            // 分析结果的输出文档名；
            app.setOutput(outputName);
            mineExecutor(app, outputDir);//由于DependsCommand是只读模式，无法添加setDir方法，故设置输出文档路径操作直接作为参数传入executeCommand方法中
        } catch (Exception e){
            if (e instanceof CommandLine.PicocliException) {
                CommandLine.usage(new DependsCommand(), System.out);
            } else if (e instanceof ParameterException){
                System.err.println(e.getMessage());
            } else {
                System.err.println("Exception encountered. If it is a design error, please report issue to us." );
                e.printStackTrace();
            }
        }
    }

    /**
     * 参考executeCommand写的自定义执行入口
     * @param app
     * @param outputDir
     * @throws ParameterException
     */
    private static void mineExecutor(DependsCommand app, String outputDir) throws ParameterException {
        String lang = app.getLang();
        String inputDir = app.getSrc();
        String[] includeDir = app.getIncludes();
        String outputName = app.getOutputName();

        // 未设置输出路径给一个默认值
        outputDir = (outputDir == null) ? "D:\\" : outputDir;
        String[] outputFormat = app.getFormat();

        inputDir = FileUtil.uniqFilePath(inputDir);
        boolean supportImplLink = false;
        if (app.getLang().equals("cpp") || app.getLang().equals("python")) supportImplLink = true;

        if (app.isAutoInclude()) {
            FolderCollector includePathCollector = new FolderCollector();
            List<String> additionalIncludePaths = includePathCollector.getFolders(inputDir);
            additionalIncludePaths.addAll(Arrays.asList(includeDir));
            includeDir = additionalIncludePaths.toArray(new String[] {});
        }

        AbstractLangProcessor langProcessor = LangProcessorRegistration.getRegistry().getProcessorOf(lang);
        if (langProcessor == null) {
            System.err.println("Not support this language: " + lang);
            return;
        }

        if (app.isDv8map()) {
            DV8MappingFileBuilder dv8MapfileBuilder = new DV8MappingFileBuilder(langProcessor.supportedRelations());
            dv8MapfileBuilder.create(outputDir+File.separator+"depends-dv8map.mapping");
        }

        long startTime = System.currentTimeMillis();

        FilenameWritter filenameWritter = new EmptyFilenameWritter();
        if (!StringUtils.isEmpty(app.getNamePathPattern())) {
            if (app.getNamePathPattern().equals("dot")||
                    app.getNamePathPattern().equals(".")) {
                filenameWritter = new DotPathFilenameWritter();
            } else if (app.getNamePathPattern().equals("unix")||
                    app.getNamePathPattern().equals("/")) {
                filenameWritter = new UnixPathFilenameWritter();
            } else if (app.getNamePathPattern().equals("windows")||
                    app.getNamePathPattern().equals("\\")) {
                filenameWritter = new WindowsPathFilenameWritter();
            } else{
                throw new ParameterException("Unknown name pattern paremater:" + app.getNamePathPattern());
            }
        }

        /* by default use file dependency generator */
        DependencyGenerator dependencyGenerator = new FileDependencyGenerator();
        if (!StringUtils.isEmpty(app.getGranularity())) {
            /* method parameter means use method generator */
            if (app.getGranularity().equals("method"))
                dependencyGenerator = new FunctionDependencyGenerator();
            else if (app.getGranularity().equals("file"))
                /*no action*/;
            else if (app.getGranularity().startsWith("L"))
                /*no action*/;
            else
                throw new ParameterException("Unknown granularity parameter:" + app.getGranularity());
        }

        if (app.isStripLeadingPath() || app.getStrippedPaths().length > 0) {
            dependencyGenerator.setLeadingStripper(new LeadingNameStripper(app.isStripLeadingPath(),inputDir,app.getStrippedPaths()));
        }

        if (app.isDetail()) {
            dependencyGenerator.setGenerateDetail(true);
        }

        dependencyGenerator.setFilenameRewritter(filenameWritter);
        langProcessor.setDependencyGenerator(dependencyGenerator);

        langProcessor.buildDependencies(inputDir, includeDir, app.getTypeFilter(), supportImplLink, app.isOutputExternalDependencies());
        DependencyMatrix matrix = langProcessor.getDependencies();

        if (app.getGranularity().startsWith("L")) {
            matrix = new MatrixLevelReducer(matrix,app.getGranularity().substring(1)).shrinkToLevel();
        }
        DependencyDumper output = new DependencyDumper(matrix);
        output.outputResult(outputName,outputDir,outputFormat);
        if (app.isOutputExternalDependencies()) {
            Set<UnsolvedBindings> unsolved = langProcessor.getExternalDependencies();
            UnsolvedSymbolDumper unsolvedSymbolDumper = new UnsolvedSymbolDumper(unsolved,app.getOutputName(),app.getOutputDir(),
                    new LeadingNameStripper(app.isStripLeadingPath(),inputDir,app.getStrippedPaths()));
            unsolvedSymbolDumper.output();
        }
        long endTime = System.currentTimeMillis();
        TemporaryFile.getInstance().delete();
        CacheManager.create().shutdown();
        System.out.println("Consumed time: " + (float) ((endTime - startTime) / 1000.00) + " s,  or "
                + (float) ((endTime - startTime) / 60000.00) + " min.");
    }

    /**
     * 读取dependency.json，输出relation.json
     * @param dependsPath
     * @param outputPath1, outputPath1
     */
    public static void getRelationJson(String dependsPath, String filesListPath,String outputPath1, String outputPath2, String dependencyType) throws IOException {
        String jsonString = readFile(dependsPath);
        System.out.println("读取：dependency 成功");
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        JSONArray fileArray = jsonObject.getJSONArray("variables");
        HashMap<Integer, String> allFilesIndexMap = new HashMap<>();

        Object[] allFilesList = fileArray.toArray();

        for(int i = 0; i < allFilesList.length; i++) {
            allFilesIndexMap.put(i, allFilesList[i].toString());
        }

        List<String> filesList = readFileList(fileNameDir);
        HashMap<String, Integer> filesIndexMap = new HashMap<>();

        for(int i = 0; i < filesList.size(); i++) {
            filesIndexMap.put(filesList.get(i), i);
        }

        // 依赖图矩阵
        int len = filesList.size();
        int[][] dependGraph = new int[len][len];
        int[][] dependWeightGraph = new int[len][len];

        // 读取依赖关系，只要存在依赖关系，对应矩阵值就设置为1
        // 这里只获取目标文件范围内的依赖关系
        JSONArray cellArray = jsonObject.getJSONArray("cells");
        for (int i = 0; i < cellArray.size(); i++){
            JSONObject subObj = cellArray.getJSONObject(i);
            int srcId = subObj.getIntValue("src");
            int destId = subObj.getIntValue("dest");

            String srcFilePath = allFilesIndexMap.get(srcId);
            String destFilePath = allFilesIndexMap.get(destId);

            if(!filesIndexMap.containsKey(srcFilePath) || !filesIndexMap.containsKey(destFilePath)) {
                continue;
            }
            srcId = filesIndexMap.get(srcFilePath);
            destId = filesIndexMap.get(destFilePath);

            JSONObject deps = subObj.getJSONObject("values");
            if(deps.containsKey(dependencyType)){
                dependGraph[srcId][destId] = 1;
                dependWeightGraph[srcId][destId] = deps.getIntValue(dependencyType);
            }
        }

        // 写入relation.json
        FileDependence object1 = new FileDependence(filesList, dependGraph);
        FileDependence object2 = new FileDependence(filesList, dependWeightGraph);
        String relationString1 = JSONObject.toJSONString(object1, true);
        String relationString2 = JSONObject.toJSONString(object2, true);
        writeObjString(relationString1, outputPath1);
        writeObjString(relationString2, outputPath2);
    }

    /**
     * 生成目标服务下的 callGraph.json
     * @throws IOException
     */
    public void getServiceCallGraph(String servicePath, String dependsPath, String outputFilesPath,  String outputGraphPath) throws IOException {
        // 目标服务下的文件列表
        List<String> srvFileList = scan(new File(servicePath), javaAndDirectoryFilter);
        int len = srvFileList.size();

        // 项目所有文件列表
        String jsonString = readFile(dependsPath);
        System.out.println("读取：" + dependsPath + " 成功");
        JSONObject jsonObject = JSONObject.parseObject(jsonString);
        JSONArray fileArray = jsonObject.getJSONArray("variables");
        List<String> fileSequence = new ArrayList<>();
        for (Object aFileArray : fileArray) {
            fileSequence.add(aFileArray.toString());
        }

        // 初始化call依赖图矩阵
        int[][] callGraph = new int[len][len];
        JSONArray cellArray = jsonObject.getJSONArray("cells");
        // step1、读取依赖关系
        for (int i = 0; i < cellArray.size(); i++){
            JSONObject subObj = cellArray.getJSONObject(i);
            int srcId = subObj.getIntValue("src");
            int destId = subObj.getIntValue("dest");
            String srcFile = fileSequence.get(srcId), destFile = fileSequence.get(destId);
            if (srvFileList.contains(srcFile) && srvFileList.contains(destFile)) {
                int srcIndex = srvFileList.indexOf(srcFile), destIndex = srvFileList.indexOf(destFile);
                JSONObject dependencyArr = subObj.getJSONObject("values");
                // 没有call时，返回的calldouble会是0
                double callDouble = dependencyArr.getDoubleValue("Call");
                int weight = (int) callDouble;
                if (weight > 0) {
                    callGraph[srcIndex][destIndex] = weight;
                }
            }
        }

        // step2、写到文件中
        writeByLine(srvFileList, outputFilesPath);
        String callJsonStr = JSONObject.toJSONString(callGraph);
        writeObjString(callJsonStr, outputGraphPath);
    }

}
