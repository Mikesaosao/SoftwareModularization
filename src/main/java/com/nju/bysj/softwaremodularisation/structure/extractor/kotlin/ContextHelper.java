package com.nju.bysj.softwaremodularisation.structure.extractor.kotlin;

public class ContextHelper {

	public static String getName(KotlinParser.IdentifierContext identifier) {
		StringBuffer sb = new StringBuffer();
		for (KotlinParser.SimpleIdentifierContext id:identifier.simpleIdentifier()) {
			if (sb.length()>0) {
				sb.append(".");
			}
			sb.append(id.getText());
		}
		return sb.toString();
	}

}
