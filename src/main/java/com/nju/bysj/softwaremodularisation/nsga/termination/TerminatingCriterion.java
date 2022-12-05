package com.nju.bysj.softwaremodularisation.nsga.termination;

import com.nju.bysj.softwaremodularisation.nsga.datastructure.Population;

@FunctionalInterface
public interface TerminatingCriterion {
	boolean shouldRun(Population population, int generationCount, int maxGenerations);
}
