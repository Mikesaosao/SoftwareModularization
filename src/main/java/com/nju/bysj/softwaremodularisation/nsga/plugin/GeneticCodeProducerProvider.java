package com.nju.bysj.softwaremodularisation.nsga.plugin;

import com.nju.bysj.softwaremodularisation.nsga.Common;
import com.nju.bysj.softwaremodularisation.nsga.datastructure.BooleanAllele;
import com.nju.bysj.softwaremodularisation.nsga.datastructure.IntegerAllele;
import com.nju.bysj.softwaremodularisation.nsga.datastructure.ValueAllele;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

public class GeneticCodeProducerProvider {

	public static GeneticCodeProducer binaryGeneticCodeProducer() {
		return (length) -> {

			List<BooleanAllele> geneticCode = new ArrayList<>();

			for(int i = 0; i < length; i++)
				geneticCode.add(i, new BooleanAllele(ThreadLocalRandom.current().nextBoolean()));

			return geneticCode;
		};
	}

	public static GeneticCodeProducer valueEncodedGeneticCodeProducer(double origin, double bound, boolean unique) {
		return length -> {
			int count = -1;
			List<ValueAllele> geneticCode = new ArrayList<>();
			while(count < (length - 1)) {
				double value = ThreadLocalRandom.current().nextDouble(origin, (bound + 0.1));
				if(value > bound)
					value = bound;
				value = Common.roundOff(value, 4);
				if(unique && Common.isInGeneticCode(geneticCode, value))
					continue;
				geneticCode.add(
					++count,
					new ValueAllele(value)
				);
			}
			return geneticCode;
		};
	}

	public static GeneticCodeProducer permutationEncodingGeneticCodeProducer() {
		return length -> {
			List<ValueAllele> valueEncodedGeneticCode = GeneticCodeProducerProvider
															.valueEncodedGeneticCodeProducer(0, 1, true)
															.produce(length)
															.stream()
															.map(e -> (ValueAllele)e)
															.collect(Collectors.toList());

			List<ValueAllele> originalGeneticCode = new ArrayList<>(valueEncodedGeneticCode);
			List<IntegerAllele> permutationEncodedGeneticCode = new ArrayList<>();
			valueEncodedGeneticCode.sort(Comparator.comparingDouble(ValueAllele::getGene));
			originalGeneticCode.forEach(e -> {
				valueEncodedGeneticCode.forEach(v -> {
					if(e.getGene().equals(v.getGene()))
						permutationEncodedGeneticCode.add(
							new IntegerAllele(
								valueEncodedGeneticCode.indexOf(v) + 1
							)
						);
				});
			});

			return permutationEncodedGeneticCode;
		};
	}
}
