/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 *    Copyright 2013 Aurelian Tutuianu
 *    Copyright 2014 Aurelian Tutuianu
 *    Copyright 2015 Aurelian Tutuianu
 *    Copyright 2016 Aurelian Tutuianu
 *    Copyright 2017 Aurelian Tutuianu
 *    Copyright 2018 Aurelian Tutuianu
 *    Copyright 2019 Aurelian Tutuianu
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
 *
 */

package rapaio.data.filter;

import rapaio.core.stat.Quantiles;
import rapaio.data.Var;
import rapaio.data.VarNominal;
import rapaio.printer.Format;
import rapaio.printer.Printer;
import rapaio.printer.opt.POption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Make a numerical variable a nominal one with intervals specified by quantiles.
 * <p>
 * Created by <a href="mailto:padreati@yahoo.com">Aurelian Tutuianu</a> on 1/18/16.
 */
public class VQuantileDiscrete implements VFilter {

    public static VQuantileDiscrete split(int k) {
        if (k <= 1) {
            throw new IllegalArgumentException("Number of parts k: " + k + " of the split " +
                    "must be greater than 1.");
        }
        double[] qp = new double[k - 1];
        double step = 1.0 / k;
        for (int i = 0; i < qp.length; i++) {
            qp[i] = step * (i + 1);
        }
        return new VQuantileDiscrete(qp);
    }

    public static VQuantileDiscrete with(double... qp) {
        if (qp.length < 1) {
            throw new IllegalArgumentException("Number of quantiles must be positive.");
        }
        return new VQuantileDiscrete(qp);
    }


    private static final long serialVersionUID = -6702714518094848749L;

    private List<String> dict = new ArrayList<>();
    private Map<String, Predicate<Double>> predicates = new HashMap<>();
    private final double[] qp;
    private double[] qv;

    private VQuantileDiscrete(double... qp) {
        this.qp = qp;
    }

    @Override
    public VQuantileDiscrete fit(Var var) {
        if (!var.type().isNumeric()) {
            return this;
        }
        qv = Quantiles.of(var, qp).values();

        // first interval

        dict.add("-Inf~" + Format.floatFlexShort(qv[0]));
        predicates.put("-Inf~" + Format.floatFlexShort(qv[0]), x -> x <= qv[0]);

        // mid intervals

        for (int i = 1; i < qv.length; i++) {
            int index = i;
            dict.add(Format.floatFlexShort(qv[i - 1]) + "~" + Format.floatFlexShort(qv[i]));
            predicates.put(Format.floatFlexShort(qv[i - 1]) + "~" + Format.floatFlexShort(qv[i]), x -> x > qv[index - 1] && x <= qv[index]);
        }

        // last interval

        dict.add(Format.floatFlexShort(qv[qv.length - 1]) + "~Inf");
        predicates.put(Format.floatFlexShort(qv[qv.length - 1]) + "~Inf", x -> x > qv[qv.length - 1]);
        return this;
    }

    @Override
    public Var apply(Var var) {
        if (!var.type().isNumeric()) {
            return var;
        }
        VarNominal result = VarNominal.empty(0, dict).withName(var.name());
        for (int i = 0; i < var.rowCount(); i++) {
            if (var.isMissing(i)) {
                result.addMissing();
                continue;
            }
            for (Map.Entry<String, Predicate<Double>> e : predicates.entrySet()) {
                if (e.getValue().test(var.getDouble(i))) {
                    result.addLabel(e.getKey());
                }
            }
        }
        return result;
    }

    @Override
    public String toContent(Printer printer, POption... options) {
        return toString();
    }

    @Override
    public String toString() {
        return "VQuantileDiscrete(q=[" + String.join(",", Arrays.stream(qp).mapToObj(Format::floatFlex).toArray(String[]::new)) + "])";
    }
}
