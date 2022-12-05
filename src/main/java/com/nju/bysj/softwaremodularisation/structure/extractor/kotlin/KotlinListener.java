package com.nju.bysj.softwaremodularisation.structure.extractor.kotlin;

import com.nju.bysj.softwaremodularisation.structure.entity.repo.EntityRepo;
import com.nju.bysj.softwaremodularisation.structure.importtypes.ExactMatchImport;
import com.nju.bysj.softwaremodularisation.structure.relations.Inferer;

public class KotlinListener extends KotlinParserBaseListener {

	private KotlinHandlerContext context;

	public KotlinListener(String fileFullPath, EntityRepo entityRepo, Inferer inferer) {
		this.context = new KotlinHandlerContext(entityRepo,inferer);
		context.startFile(fileFullPath);
	}

	@Override
	public void enterPackageHeader(KotlinParser.PackageHeaderContext ctx) {
		if (ctx.identifier()!=null) {
			context.foundNewPackage(ContextHelper.getName(ctx.identifier()));
		}
		super.enterPackageHeader(ctx);
	}

	@Override
	public void enterImportHeader(KotlinParser.ImportHeaderContext ctx) {
		context.foundNewImport(new ExactMatchImport(ContextHelper.getName(ctx.identifier())));
		//TODO: alias of import
		if (ctx.importAlias()!=null) {
			
		}
		super.enterImportHeader(ctx);
	}
	

}
