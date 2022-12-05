package com.nju.bysj.softwaremodularisation.nsga.mutation;

import com.nju.bysj.softwaremodularisation.nsga.datastructure.Chromosome;

import static com.nju.bysj.softwaremodularisation.nsga.Common.randomGenerateFileChromosome;

public class FileRandomMutation extends AbstractMutation {

    public FileRandomMutation(float mutationProbability) {
        super(mutationProbability);
    }

    @Override
    public Chromosome perform(Chromosome chromosome) {
        return randomGenerateFileChromosome();
    }
}
