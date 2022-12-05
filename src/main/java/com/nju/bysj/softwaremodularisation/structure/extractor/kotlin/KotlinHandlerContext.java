package com.nju.bysj.softwaremodularisation.structure.extractor.kotlin;

import com.nju.bysj.softwaremodularisation.structure.entity.repo.EntityRepo;
import com.nju.bysj.softwaremodularisation.structure.extractor.java.JavaHandlerContext;
import com.nju.bysj.softwaremodularisation.structure.relations.Inferer;

public class KotlinHandlerContext extends JavaHandlerContext {

	public KotlinHandlerContext(EntityRepo entityRepo, Inferer inferer) {
		super(entityRepo,inferer);
	}

}
