/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 *    Copyright 2013 Aurelian Tutuianu
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package rapaio.ml.classification.tree;

import rapaio.data.Frame;
import rapaio.filters.NominalFilters;

import java.util.List;

import static rapaio.core.MathBase.log;

/**
 * @author <a href="mailto:padreati@yahoo.com">Aurelian Tutuianu</a>
 */
public class TreeMetrics {

	public double entropy(Frame df, List<Double> weights, String classColName) {
		int classIndex = df.colIndex(classColName);
		int[] hits = new int[df.col(classIndex).dictionary().length];
		for (int i = 0; i < df.rowCount(); i++) {
			hits[df.col(classIndex).index(i)] += weights.get(i);
		}
		double entropy = 0.;
		for (int hit : hits) {
			if (hit != 0) {
				double p = hit / (1. * df.rowCount());
				entropy += -p * log(p) / log(2);
			}
		}
		return entropy;
	}

	public double entropy(Frame df, List<Double> weights, String classColName, String splitColName) {
		int splitIndex = df.colIndex(splitColName);
		Frame[] split = NominalFilters.groupByNominal(df, splitIndex);
		double entropy = 0.;
		for (Frame f : split) {
			if (f == null) {
				continue;
			}
			entropy += (1. * f.rowCount() * entropy(f, weights, classColName)) / (1. * df.rowCount());
		}
		return entropy;
	}

	public double infoGain(Frame df, List<Double> weights, String classColName, String splitColName) {
		int splitIndex = df.colIndex(splitColName);
		Frame[] split = NominalFilters.groupByNominal(df, splitIndex);
		double infoGain = entropy(df, weights, classColName);
		for (Frame f : split) {
			if (f == null) {
				continue;
			}
			infoGain -= (f.rowCount() / (1. * df.rowCount())) * entropy(f, weights, classColName);
		}
		return infoGain;
	}
}
