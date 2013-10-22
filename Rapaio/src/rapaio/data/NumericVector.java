/*
 * Copyright 2013 Aurelian Tutuianu <padreati@yahoo.com>
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

package rapaio.data;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Numeric vector holds continuous or discrete random variables.
 *
 * @author Aurelian Tutuianu
 */
public class NumericVector extends AbstractVector {

    private static final double missingValue = Double.NaN;
    private final double[] values;

    public NumericVector(String name, int size) {
        super(name);
        this.values = new double[size];
        Arrays.fill(values, missingValue);
    }

    public NumericVector(String name, double[] values) {
        super(name);
        this.values = Arrays.copyOf(values, values.length);
    }

    public NumericVector(String name, int[] values) {
        super(name);
        this.values = new double[values.length];
        for (int i = 0; i < values.length; i++) {
            this.values[i] = values[i];
        }
    }

    @Override
    public boolean isNumeric() {
        return true;
    }

    @Override
    public boolean isNominal() {
        return false;
    }

    @Override
    public boolean isMappedVector() {
        return false;
    }

    @Override
    public Vector getSourceVector() {
        return this;
    }

    @Override
    public Mapping getMapping() {
        return null;
    }

    @Override
    public int getRowCount() {
        return values.length;
    }

    @Override
    public int getRowId(int row) {
        return row;
    }

    @Override
    public int getIndex(int row) {
        return (int) Math.rint(getValue(row));
    }

    @Override
    public void setIndex(int row, int value) {
        setValue(row, value);
    }

    @Override
    public double getValue(int row) {
        return values[row];
    }

    @Override
    public void setValue(int row, double value) {
        values[row] = value;
    }

    @Override
    public String getLabel(int row) {
        return "";
    }

    @Override
    public void setLabel(int row, String value) {
        throw new RuntimeException("Operation not available for index vectors.");
    }

    @Override
    public String[] getDictionary() {
        return new String[0];
    }

    @Override
    public boolean isMissing(int row) {
        return getValue(row) != getValue(row);
    }

    @Override
    public void setMissing(int row) {
        setValue(row, missingValue);
    }
}
