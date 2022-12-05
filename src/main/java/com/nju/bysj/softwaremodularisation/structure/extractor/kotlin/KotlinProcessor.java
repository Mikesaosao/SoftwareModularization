package com.nju.bysj.softwaremodularisation.structure.extractor.kotlin;

import com.nju.bysj.softwaremodularisation.structure.entity.repo.BuiltInType;
import com.nju.bysj.softwaremodularisation.structure.extractor.AbstractLangProcessor;
import com.nju.bysj.softwaremodularisation.structure.extractor.FileParser;
import com.nju.bysj.softwaremodularisation.structure.extractor.java.JavaBuiltInType;
import com.nju.bysj.softwaremodularisation.structure.extractor.java.JavaImportLookupStrategy;
import com.nju.bysj.softwaremodularisation.structure.relations.ImportLookupStrategy;

import java.util.ArrayList;
import java.util.List;

import static com.nju.bysj.softwaremodularisation.structure.deptypes.DependencyType.*;

public class KotlinProcessor extends AbstractLangProcessor {

	public KotlinProcessor() {
    	super(true);
	}

	@Override
	public String supportedLanguage() {
		return "kotlin[on-going]";
	}

	@Override
	public String[] fileSuffixes() {
		return new String[] {".kt"};
	}

	@Override
	public ImportLookupStrategy getImportLookupStrategy() {
		return new JavaImportLookupStrategy();
	}

	@Override
	public BuiltInType getBuiltInType() {
		return new JavaBuiltInType();
	}

	@Override
	protected FileParser createFileParser(String fileFullPath) {
		return new KotlinFileParser(fileFullPath,entityRepo, inferer);
	}
	
	@Override
	public List<String> supportedRelations() {
		ArrayList<String> depedencyTypes = new ArrayList<>();
		depedencyTypes.add(IMPORT);
		depedencyTypes.add(CONTAIN);
		depedencyTypes.add(IMPLEMENT);
		depedencyTypes.add(INHERIT);
		depedencyTypes.add(CALL);
		depedencyTypes.add(PARAMETER);
		depedencyTypes.add(RETURN);
		depedencyTypes.add(SET);
		depedencyTypes.add(CREATE);
		depedencyTypes.add(USE);
		depedencyTypes.add(CAST);
		depedencyTypes.add(THROW);
		depedencyTypes.add(ANNOTATION);
		return depedencyTypes;
	}	

}
