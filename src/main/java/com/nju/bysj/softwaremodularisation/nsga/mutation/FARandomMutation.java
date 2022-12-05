package com.nju.bysj.softwaremodularisation.nsga.mutation;

import com.nju.bysj.softwaremodularisation.nsga.Common;
import com.nju.bysj.softwaremodularisation.nsga.datastructure.Chromosome;

public class FARandomMutation extends AbstractMutation {

    public FARandomMutation(float mutationProbability) {
        super(mutationProbability);
    }

    @Override
    public Chromosome perform(Chromosome chromosome) {
        return Common.randomGenerateFAChromosome();
    }
}
