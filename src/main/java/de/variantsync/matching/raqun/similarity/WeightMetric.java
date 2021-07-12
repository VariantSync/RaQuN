package de.variantsync.matching.raqun.similarity;

import de.variantsync.matching.raqun.data.RElement;
import de.variantsync.matching.raqun.data.RMatch;
import de.variantsync.matching.nwm.domain.Element;

import java.util.*;

/**
 * Re-implementation of the weight metric proposed by Rubin and Chechik, ESEC/FSE 2013
 * https://dl.acm.org/doi/10.1145/2491411.2491446
 */
public class WeightMetric implements SimilarityFunction
{
	protected int numberOfModels;

	/**
	 * Initialize the WeightMetric with the number of models which are used for normalization
	 * @param numberOfModels The number of input models that are considered
	 */
	public WeightMetric(int numberOfModels) {
		this.numberOfModels = numberOfModels;
	}

	public WeightMetric() {
		// Set the number of models to 1, so that no normalization is done, if the number is not given explicitly
		this.numberOfModels = 1;
	}

	/**
	 * Weight calculation used by the classes implemented by Rubin et al. We changed the method
	 * that is called by these classes to this one, because we improved the runtime of the weight calculation
	 * and did not want to give RaQuN an unfair advantage here.
	 */
	public static double weightForElements(Collection<Element> tuple, int numberOfModels) {
		HashMap<String, List<Boolean>> allDistinctProperties = new HashMap<>();
		long numerator = 0;
		for (Element element : tuple) {
			for (String name : element.getProperties()) {
				numerator += addProperty(allDistinctProperties, name);
			}
		}

		int numberOfDistinctProperties = allDistinctProperties.size(); // |pi(t)|
		return ((double)numerator) / (numberOfDistinctProperties * numberOfModels * numberOfModels);
	}

	@Override
	public double getMatchConfidence(RElement elementA, RElement elementB) {
		Set<RElement> elements = new HashSet<>();
		elements.add(elementA);
		elements.add(elementB);

		return weightForElements(elements);
	}

	@Override
	public boolean shouldMatch(Set<RMatch> tuples) {
		Set<RElement> mergedRElements = new HashSet<>();
		double weightSum = 0.0d;
		for (RMatch tuple : tuples) {
			mergedRElements.addAll(tuple.getElements());
			weightSum += weightForElements(tuple.getElements());
		}

		return weightForElements(mergedRElements) > weightSum;
	}

	@Override
	public void setNumberOfModels(int numberOfModels) {
		this.numberOfModels = numberOfModels;
	}

	public double getQualityOfMatching(Set<RMatch> set) {
		double weight = 0.0d;
		for (RMatch tuple : set) {
			weight += weightForElements(tuple.getElements());
		}
		return weight;
	}
	// New  implementation

	public double weightForElements(Collection<RElement> match)
	{
		HashMap<String, List<Boolean>> allDistinctProperties = new HashMap<>();
		long numerator = 0;
		for (RElement node : match) {
			for (String propertyName : node.getProperties()) {
				numerator += addProperty(allDistinctProperties, propertyName);
			}
		}

		int numberOfDistinctProperties = allDistinctProperties.size(); // |pi(t)|
		return ((double)numerator) / (numberOfDistinctProperties * numberOfModels * numberOfModels);
	}

	private static long addProperty(Map<String, List<Boolean>> properties, String property) {
		long value = 0;
		if (properties.containsKey(property)) {
			List<Boolean> flags = properties.get(property);
			flags.add(true);
			// Calculate the j^2 value of the NwMWeight
			value = (long) flags.size() * flags.size();
			if (flags.size() > 2) {
				// Subtract (j-1)^2 because only the highest "j" should be considered
				value -= (long) (flags.size() - 1) * (flags.size() - 1);
			}
		} else {
			List<Boolean> flags = new ArrayList<>();
			flags.add(true);
			properties.put(property, flags);
		}
		return value;
	}

}