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

package com.nju.bysj.softwaremodularisation.structure;

import com.nju.bysj.softwaremodularisation.structure.extractor.AbstractLangProcessor;
import com.nju.bysj.softwaremodularisation.structure.extractor.LangProcessorRegistration;
import com.nju.bysj.softwaremodularisation.structure.extractor.java.JavaProcessor;
import com.nju.bysj.softwaremodularisation.structure.extractor.kotlin.KotlinProcessor;
import com.nju.bysj.softwaremodularisation.structure.extractor.pom.PomProcessor;

public class LangRegister {


	public LangRegister() {
		add (new JavaProcessor());
//		add (new com.nju.rjxy.refactorbackend.depends.extractor.cpp.CppProcessor());
//		add (new com.nju.rjxy.refactorbackend.depends.extractor.ruby.RubyProcessor());
		add (new PomProcessor());
		add (new KotlinProcessor());
//		add (new com.nju.rjxy.refactorbackend.depends.extractor.python.union.PythonProcessor());
	}

	public void register() {

	}
	
	private void add(AbstractLangProcessor langProcessor) {
		LangProcessorRegistration.getRegistry().register(langProcessor);
	}
}


