package rapaio.ml.simple;

import rapaio.core.ColRange;
import rapaio.data.Frame;
import rapaio.data.Numeric;
import rapaio.data.SolidFrame;
import rapaio.data.Vector;
import rapaio.ml.AbstractRegressor;
import rapaio.ml.Regressor;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Aurelian Tutuianu <padreati@yahoo.com>
 */
public class CustomConstantRegressor extends AbstractRegressor {

	List<String> targets;
	double customValue;
	List<Vector> fitValues;

	@Override
	public Regressor newInstance() {
		return new L2ConstantRegressor();
	}

	public double getCustomValue() {
		return customValue;
	}

	public CustomConstantRegressor setCustomValue(double customValue) {
		this.customValue = customValue;
		return this;
	}

	@Override
	public void learn(Frame df, List<Double> weights, String targetColName) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public void learn(Frame df, String targetColNames) {
		ColRange colRange = new ColRange(targetColNames);
		List<Integer> colIndexes = colRange.parseColumnIndexes(df);

		targets = new ArrayList<>();
		for (int i = 0; i < colIndexes.size(); i++) {
			targets.add(df.getColNames()[colIndexes.get(i)]);
		}

		fitValues = new ArrayList<>();
		for (String target : targets) {
			fitValues.add(new Numeric(df.getCol(target).getRowCount(), df.getCol(target).getRowCount(), customValue));
		}
	}

	@Override
	public void predict(Frame df) {
		fitValues = new ArrayList<>();
		for (int i = 0; i < targets.size(); i++) {
			fitValues.add(new Numeric(df.getRowCount(), df.getRowCount(), customValue));
		}
	}

	@Override
	public Numeric getFitValues() {
		return (Numeric) fitValues.get(0);
	}

	@Override
	public Frame getAllFitValues() {
		return new SolidFrame(fitValues.get(0).getRowCount(), fitValues, targets);
	}
}