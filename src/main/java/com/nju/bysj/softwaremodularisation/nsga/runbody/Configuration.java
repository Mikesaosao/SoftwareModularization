package com.nju.bysj.softwaremodularisation.nsga.runbody;

import com.nju.bysj.softwaremodularisation.nsga.Reporter;
import com.nju.bysj.softwaremodularisation.nsga.crossover.AbstractCrossover;
import com.nju.bysj.softwaremodularisation.nsga.crossover.FileRandomCrossover;
import com.nju.bysj.softwaremodularisation.nsga.mutation.AbstractMutation;
import com.nju.bysj.softwaremodularisation.nsga.mutation.FileRandomMutation;
import com.nju.bysj.softwaremodularisation.nsga.objective.AbstractObjectiveFunction;
import com.nju.bysj.softwaremodularisation.nsga.objective.ObjectiveProvider;
import com.nju.bysj.softwaremodularisation.nsga.plugin.*;
import com.nju.bysj.softwaremodularisation.nsga.termination.TerminatingCriterion;
import com.nju.bysj.softwaremodularisation.nsga.termination.TerminatingCriterionProvider;

import java.util.List;

/**
 * The Configuration class is used to setup the runtime settings of the NSGA-II algorithm.
 * Since a lot of the settings within the algorithm can be changed, this class manages them all.
 * An instance of this class is needed to be setup before creating an instance of
 * NSGA2 class or running the algorithm. Changing settings of an instance of a Configuration class between
 * runs will also reflect immediately on the result. This means that, if required (but almost rarely),
 * the configuration of the algorithm within the same run can be changed dynamically.
 */
public class Configuration {

	public static final String CONFIGURATION_NOT_SETUP = "The NSGA-II configuration object is not setup properly!";
	public static final int DEFAULT_POPULATION_SIZE = 100;
	public static final int DEFAULT_GENERATIONS = 25;
	public static final int DEFAULT_CHROMOSOME_LENGTH = 20;

	public static String FITNESS_CALCULATOR_NULL = "The fitness calculation operation has not been setup. "				+
			"You need to set the AbstractObjectiveFunction#fitnessCalculator "	+
			"with an instance of FitnessCalculator!";

	public List<AbstractObjectiveFunction> objectives;

	private int populationSize;
	private int generations;
	private int chromosomeLength;
	private PopulationProducer populationProducer;
	private ChildPopulationProducer childPopulationProducer;
	private GeneticCodeProducer geneticCodeProducer;
	private AbstractCrossover crossover;
	private AbstractMutation mutation;
	private TerminatingCriterion terminatingCriterion;
	private FitnessCalculator fitnessCalculator;

	public Configuration() {
		this(
				Configuration.DEFAULT_POPULATION_SIZE,
				Configuration.DEFAULT_GENERATIONS,
				Configuration.DEFAULT_CHROMOSOME_LENGTH
		);
	}

	public Configuration(int populationSize, int generations, int chromosomeLength) {
		this(
				populationSize,
				generations,
				chromosomeLength,
				DefaultPluginProvider.fileInitPopulationProducer(),
				DefaultPluginProvider.randomChildrenProducer(0.5f),
				null,
				ObjectiveProvider.provideFileStructuralAndSemanticAndEvolution(),
				new FileRandomCrossover(null, 0.5f),
				new FileRandomMutation(0.5f),
				TerminatingCriterionProvider.refactorTerminatingCriterion(100, 10000),
				false,
				true,
				true
		);
	}

	public Configuration(int populationSize,
						 int generations,
						 int chromosomeLength,
						 PopulationProducer populationProducer,
						 ChildPopulationProducer childPopulationProducer,
						 GeneticCodeProducer geneticCodeProducer,
						 List<AbstractObjectiveFunction> objectives,
						 AbstractCrossover crossover,
						 AbstractMutation mutation,
						 TerminatingCriterion terminatingCriterion,
						 boolean silent,
						 boolean plotGraph,
						 boolean writeToDisk) {

		this.populationSize = populationSize;
		this.generations = generations;
		this.chromosomeLength = chromosomeLength;
		this.populationProducer = populationProducer;
		this.childPopulationProducer = childPopulationProducer;
		this.geneticCodeProducer = geneticCodeProducer;
		this.objectives = objectives;
		this.crossover = crossover;
		this.mutation = mutation;
		this.terminatingCriterion = terminatingCriterion;

		Reporter.silent = silent;
		Reporter.plotGraph = plotGraph;
		Reporter.writeToDisk = writeToDisk;
	}

	public Configuration(int populationSize,
						 int generations,
						 int chromosomeLength,
						 PopulationProducer populationProducer,
						 ChildPopulationProducer childPopulationProducer,
						 GeneticCodeProducer geneticCodeProducer,
						 List<AbstractObjectiveFunction> objectives,
						 AbstractCrossover crossover,
						 AbstractMutation mutation,
						 TerminatingCriterion terminatingCriterion,
						 boolean silent,
						 boolean plotGraph,
						 boolean writeToDisk,
						 FitnessCalculator fitnessCalculator) {

		this(populationSize,
				generations,
				chromosomeLength,
				populationProducer,
				childPopulationProducer,
				geneticCodeProducer,
				objectives,
				crossover,
				mutation,
				terminatingCriterion,
				silent,
				plotGraph,
				writeToDisk);
		this.fitnessCalculator = fitnessCalculator;
	}

	public int getPopulationSize() {
		return populationSize;
	}

	public void setPopulationSize(int populationSize) {
		if(populationSize < 1)
			throw new UnsupportedOperationException("Population size cannot be less than 1.");
		this.populationSize = populationSize;
	}

	public int getGenerations() {
		return generations;
	}

	public void setGenerations(int generations) {
		if(generations < 1)
			throw new UnsupportedOperationException("Generations cannot be less than 1.");
		this.generations = generations;
	}

	public int getChromosomeLength() {
		return chromosomeLength;
	}

	public void setChromosomeLength(int chromosomeLength) {
		if(chromosomeLength < 1)
			throw new UnsupportedOperationException("Chromosome length cannot be less than 1.");
		this.chromosomeLength = chromosomeLength;
	}

	public PopulationProducer getPopulationProducer() {
		return populationProducer;
	}

	public void setPopulationProducer(PopulationProducer populationProducer) {
		this.populationProducer = populationProducer;
	}

	public ChildPopulationProducer getChildPopulationProducer() {
		return childPopulationProducer;
	}

	public void setChildPopulationProducer(ChildPopulationProducer childPopulationProducer) {
		this.childPopulationProducer = childPopulationProducer;
	}

	public GeneticCodeProducer getGeneticCodeProducer() {
		return geneticCodeProducer;
	}

	public void setGeneticCodeProducer(GeneticCodeProducer geneticCodeProducer) {
		this.geneticCodeProducer = geneticCodeProducer;
	}

	public AbstractCrossover getCrossover() {
		return crossover;
	}

	public void setCrossover(AbstractCrossover crossover) {
		this.crossover = crossover;
	}

	public AbstractMutation getMutation() {
		return mutation;
	}

	public void setMutation(AbstractMutation mutation) {
		this.mutation = mutation;
	}

	public TerminatingCriterion getTerminatingCriterion() {
		return terminatingCriterion;
	}

	public void setTerminatingCriterion(TerminatingCriterion terminatingCriterion) {
		this.terminatingCriterion = terminatingCriterion;
	}

	public FitnessCalculator getFitnessCalculator() {

		if(this.fitnessCalculator == null)
			this.fitnessCalculator = FitnessCalculatorProvider.normalizedGeneticCodeValue(
					0,
					Math.pow(2, this.chromosomeLength) - 1,
					0,
					2
			);

		return this.fitnessCalculator;
	}

	public void setFitnessCalculator(FitnessCalculator fitnessCalculator) {
		this.fitnessCalculator = fitnessCalculator;
	}

	public boolean isSetup() {
		return (
				this.populationSize != 0 				&&
						this.generations != 0 					&&
						this.chromosomeLength != 0 				&&
						this.populationProducer != null 		&&
						this.childPopulationProducer != null	&&
						this.crossover != null					&&
						this.mutation != null					&&
						this.terminatingCriterion != null			&&
						this.objectives != null		&&
						!this.objectives.isEmpty()
		);
	}

	public void beSilent() {
		Reporter.silent = true;
	}

	public void plotGraph(boolean value) {
		Reporter.plotGraph = value;
	}

	public void writeToDisk(boolean value) {
		Reporter.writeToDisk = value;
	}

	public void completeSilence() {
		this.beSilent();
		this.plotGraph((false));
		this.writeToDisk(false);
	}

	@Override
	public String toString() {
		return "\nPopulation Size: " 																				+
				this.populationSize 																				+
				" [" 																								+
				(this.populationSize > 0 ? "valid" : "invalid") 													+
				"]" 																								+
				"\nGenerations: " 																					+
				this.generations 																					+
				" [" 																								+
				(this.generations > 0 ? "valid" : "invalid") 														+
				"]" 																								+
				"\nChromosome Length: " 																			+
				this.chromosomeLength				 																+
				" [" 																								+
				(this.chromosomeLength > 0 ? "valid" : "invalid") 													+
				"]" 																								+
				"\nPopulation Producer: " 																			+
				"[" 																								+
				(this.populationProducer != null ? "valid" : "invalid") 											+
				"]" 																								+
				"\nChild Population Producer: " 																	+
				"[" 																								+
				(this.childPopulationProducer != null ? "valid" : "invalid")				 						+
				"]" 																								+
				"\nGenetic Code Producer: " 																		+
				"[" 																								+
				(this.geneticCodeProducer != null ? "valid" : "invalid") 											+
				"]" 																								+
				"\nObjectives: " 																					+
				"[" 																								+
				((this.objectives != null && !this.objectives.isEmpty()) ? "valid" : "invalid")						+
				"]"																									+
				"\nCrossover Operator: " 																			+
				"[" 																								+
				(this.crossover != null ? "provided" : "not provided") 												+
				"]" 																								+
				"\nMutation Operator: " 																			+
				"[" 																								+
				(this.mutation != null ? "provided" : "not provided") 												+
				"]" 																								+
				"\nGeneration Driver: " 																			+
				"[" 																								+
				(this.terminatingCriterion != null ? "provided" : "not provided") 										+
				"]" 																								+
				"\nFitness Calculator: " 																			+
				"[" 																								+
				(this.fitnessCalculator != null ? "provided" : "not provided") 										+
				"]";
	}
}
