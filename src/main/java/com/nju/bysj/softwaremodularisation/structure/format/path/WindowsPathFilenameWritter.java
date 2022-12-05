package com.nju.bysj.softwaremodularisation.structure.format.path;

public class WindowsPathFilenameWritter implements FilenameWritter{
	@Override
	public String reWrite(String originalPath) {
		return originalPath.replaceAll("/","\\\\");
	}
}
