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

package com.nju.bysj.softwaremodularisation.structure.format.detail;

import com.nju.bysj.softwaremodularisation.structure.format.AbstractFormatDependencyDumper;
import com.nju.bysj.softwaremodularisation.structure.matrix.core.DependencyDetail;
import com.nju.bysj.softwaremodularisation.structure.matrix.core.DependencyMatrix;
import com.nju.bysj.softwaremodularisation.structure.matrix.core.DependencyPair;
import com.nju.bysj.softwaremodularisation.structure.matrix.core.DependencyValue;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;

public class DetailTextFormatDependencyDumper extends AbstractFormatDependencyDumper{
	ArrayList<String> files;
	@Override
	public String getFormatName() {
		return "detail";
	}
	public DetailTextFormatDependencyDumper(DependencyMatrix matrix, String name, String outputDir) {
		super(matrix,name,outputDir);
	}
	@Override
	public boolean output() {
		PrintWriter writer;
		try {
			files = matrix.getNodes();
			writer = new PrintWriter(composeFilename() +".txt");
	        Collection<DependencyPair> dependencyPairs = matrix.getDependencyPairs();
	        addRelations(writer,dependencyPairs); 
			writer.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}

	private void addRelations(PrintWriter writer, Collection<DependencyPair> dependencyPairs) {
		for (DependencyPair dependencyPair:dependencyPairs) {
            int src = dependencyPair.getFrom();
            int dst = dependencyPair.getTo();
        	writer.println("======="+files.get(src) + " -> " + files.get(dst) + "=========");
        	for (DependencyValue dependency:dependencyPair.getDependencies()) {
        		for (DependencyDetail item:dependency.getDetails()) {
                	writer.println("["+dependency.getType()+"]"+item);
        		}
        	}
        }		
	}
}
