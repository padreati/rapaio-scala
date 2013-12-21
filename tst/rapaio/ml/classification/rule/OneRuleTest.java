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

package rapaio.ml.classification.rule;

import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import rapaio.data.Frame;
import rapaio.data.NominalVector;
import rapaio.data.NumericVector;
import rapaio.data.SolidFrame;
import rapaio.data.Vector;
import rapaio.datasets.Datasets;
import rapaio.graphics.BarChart;
import rapaio.ml.classification.Classifier;
import rapaio.ml.classification.ModelEvaluation;
import rapaio.ml.classification.boost.AdaBoostM1;
import rapaio.ml.classification.tree.DecisionStump;
import rapaio.ml.classification.tree.RandomForest;
import rapaio.printer.LocalPrinter;
import rapaio.workspace.Summary;
import static rapaio.workspace.Workspace.*;

/**
 * User: Aurelian Tutuianu <paderati@yahoo.com>
 */
public class OneRuleTest {

    private static final int SIZE = 6;

    private final Vector classVector;
    private final Vector heightVector;

    public OneRuleTest() {
        classVector = new NominalVector(SIZE, new String[]{"False", "True"});
        classVector.setLabel(0, "True");
        classVector.setLabel(1, "True");
        classVector.setLabel(2, "True");
        classVector.setLabel(3, "False");
        classVector.setLabel(4, "False");
        classVector.setLabel(5, "False");

        heightVector = new NumericVector(new double[]{0.1, 0.3, 0.5, 10, 10.3, 10.5});
    }

    @Test
    public void testSimpleNumeric() {
        Frame df = new SolidFrame(SIZE, new Vector[]{heightVector, classVector}, new String[]{"height", "class"});

        String[] labels;
        OneRule oneRule = new OneRule();

        oneRule = oneRule.setMinCount(1);
        oneRule.learn(df, "class");
        oneRule.predict(df);
        labels = new String[]{"True", "True", "True", "False", "False", "False"};
        for (int i = 0; i < SIZE; i++) {
            Assert.assertEquals(labels[i], oneRule.getPrediction().getLabel(i));
        }

        oneRule.setMinCount(2);
        oneRule.learn(df, "class");
        oneRule.predict(df);
        labels = new String[]{"True", "True", "TrueFalse", "TrueFalse", "False", "False"};
        for (int i = 0; i < SIZE; i++) {
            Assert.assertTrue(labels[i].contains(oneRule.getPrediction().getLabel(i)));
        }

        oneRule.setMinCount(3);
        oneRule.learn(df, "class");
        oneRule.predict(df);
        labels = new String[]{"True", "True", "True", "False", "False", "False"};
        for (int i = 0; i < SIZE; i++) {
            Assert.assertTrue(labels[i].equals(oneRule.getPrediction().getLabel(i)));
        }

        oneRule.setMinCount(4);
        oneRule.learn(df, "class");
        oneRule.predict(df);
        for (int i = 1; i < SIZE; i++) {
            Assert.assertTrue(oneRule.getPrediction().getLabel(i).equals(oneRule.getPrediction().getLabel(0)));
        }
    }
    
    
    @Test
    public void testSimpleNominal() throws Exception {
        setPrinter(new LocalPrinter());
        Frame df = Datasets.loadMushrooms();
        Summary.names(df);
        
        draw(new BarChart(df.getCol("odor"), df.getCol("classes")));
        
        OneRule onerule = new OneRule();
        ModelEvaluation eval = new ModelEvaluation();
        List<Classifier> cs = new ArrayList<>();
//        cs.add(onerule);
        cs.add(new AdaBoostM1(onerule, 40));
        cs.add(new AdaBoostM1(new DecisionStump(), 40));
//        cs.add(new RandomForest().setMcols(4));
        eval.multiCv(df, "classes", cs, 10);
    }
}