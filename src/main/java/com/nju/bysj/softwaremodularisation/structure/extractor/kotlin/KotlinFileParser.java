package com.nju.bysj.softwaremodularisation.structure.extractor.kotlin;

import com.nju.bysj.softwaremodularisation.structure.entity.repo.EntityRepo;
import com.nju.bysj.softwaremodularisation.structure.extractor.FileParser;
import com.nju.bysj.softwaremodularisation.structure.relations.Inferer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.IOException;

public class KotlinFileParser implements FileParser {

	@Override
	public void parse() throws IOException {
	       CharStream input = CharStreams.fromFileName(fileFullPath);
	        Lexer lexer = new KotlinLexer(input);
	        CommonTokenStream tokens = new CommonTokenStream(lexer);
	        KotlinParser parser = new KotlinParser(tokens);
	        KotlinListener bridge = new KotlinListener(fileFullPath, entityRepo,inferer);
		    ParseTreeWalker walker = new ParseTreeWalker();
		    walker.walk(bridge, parser.kotlinFile());
	}
	
	private String fileFullPath;
	private EntityRepo entityRepo;
	private Inferer inferer;
	public KotlinFileParser(String fileFullPath,EntityRepo entityRepo, Inferer inferer) {
        this.fileFullPath = fileFullPath;
        this.entityRepo = entityRepo;
        this.inferer = inferer;
	}


}
