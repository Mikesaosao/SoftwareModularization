/*
MIT License

Copyright (c) 2018-2019 Gang ZHANG

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package com.nju.bysj.softwaremodularisation.structure.extractor.pom;

import com.nju.bysj.softwaremodularisation.structure.deptypes.DependencyType;
import com.nju.bysj.softwaremodularisation.structure.entity.repo.BuiltInType;
import com.nju.bysj.softwaremodularisation.structure.extractor.AbstractLangProcessor;
import com.nju.bysj.softwaremodularisation.structure.extractor.FileParser;
import com.nju.bysj.softwaremodularisation.structure.extractor.empty.EmptyBuiltInType;
import com.nju.bysj.softwaremodularisation.structure.relations.ImportLookupStrategy;

import java.util.ArrayList;
import java.util.List;

public class PomProcessor extends AbstractLangProcessor {

	public PomProcessor() {
		super(false);
	}
	
	@Override
	public String supportedLanguage() {
		return "pom";
	}

	@Override
	public String[] fileSuffixes() {
		return new String[] {".pom"};
	}

	@Override
	public ImportLookupStrategy getImportLookupStrategy() {
		return new PomImportLookupStategy();
	}

	@Override
	public BuiltInType getBuiltInType() {
		return new EmptyBuiltInType();
	}

	@Override
	protected FileParser createFileParser(String fileFullPath) {
		return new PomFileParser(fileFullPath,entityRepo,includePaths(),this,inferer);
	}
	
	@Override
	public List<String> supportedRelations() {
		ArrayList<String> depedencyTypes = new ArrayList<>();
		depedencyTypes.add(DependencyType.PomParent);
		depedencyTypes.add(DependencyType.PomPlugin);
		depedencyTypes.add(DependencyType.PomDependency);
		return depedencyTypes;
	}			
	
	@Override
	public String getRelationMapping(String relation) {
		if (relation.equals(DependencyType.IMPORT)) return DependencyType.PomParent;
		if (relation.equals(DependencyType.USE)) return DependencyType.PomPlugin;
		if (relation.equals(DependencyType.CONTAIN)) return DependencyType.PomDependency;
		return relation;
	}
}
