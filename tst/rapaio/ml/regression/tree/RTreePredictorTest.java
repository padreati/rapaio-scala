/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 *    Copyright 2013 Aurelian Tutuianu
 *    Copyright 2014 Aurelian Tutuianu
 *    Copyright 2015 Aurelian Tutuianu
 *    Copyright 2016 Aurelian Tutuianu
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

package rapaio.ml.regression.tree;

import org.junit.Test;
import rapaio.data.Frame;
import rapaio.data.NumVar;
import rapaio.data.SolidFrame;
import rapaio.data.stream.FSpot;
import rapaio.util.Pair;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;

public class RTreePredictorTest {

    private static final double TOL = 1e-20;

    @Test
    public void standardPredictorTest() throws IOException, URISyntaxException {

        RTreePredictor pred = RTreePredictor.STANDARD;
        assertEquals("STANDARD", pred.name());

        // nodes

        RTreeNode root = new RTreeNode(null,null, "root", (row, frame) -> true);
        root.setLeaf(false);

        RTreeNode left = new RTreeNode(null, root, "x < 10", (row, frame) -> frame.value(row, "x") < 10);
        left.setLeaf(false);

        RTreeNode right = new RTreeNode(null, root, "x >= 10", (row, frame) -> frame.value(row, "x") >= 10);
        right.setLeaf(false);

        RTreeNode left_left = new RTreeNode(null, left, "x < 5", (row, frame) -> frame.value(row, "x") < 5);
        left_left.setLeaf(true);
        left_left.setValue(0);
        left_left.setWeight(1);

        RTreeNode left_right = new RTreeNode(null, left, "x >= 5", (row, frame) -> frame.value(row, "x") >= 5);
        left_right.setLeaf(true);
        left_right.setValue(1);
        left_right.setWeight(2);

        RTreeNode right_left = new RTreeNode(null, right, "x < 15", (row, frame) -> frame.value(row, "x") < 15);
        right_left.setLeaf(true);
        right_left.setValue(2);
        right_left.setWeight(3);

        RTreeNode right_right = new RTreeNode(null, right, "x >= 15", (row, frame) -> frame.value(row, "x") >= 15);
        right_right.setLeaf(true);
        right_right.setValue(3);
        right_right.setWeight(4);

        // links

        root.getChildren().add(left);
        root.getChildren().add(right);

        left.getChildren().add(left_left);
        left.getChildren().add(left_right);

        right.getChildren().add(right_left);
        right.getChildren().add(right_right);

        Pair<Double, Double> fit_0 = pred.predict(0, getFrame(0), root);
        assertEquals(0, fit_0._1, TOL);
        assertEquals(1, fit_0._2, TOL);

        Pair<Double, Double> fit_7 = pred.predict(0, getFrame(7), root);
        assertEquals(1, fit_7._1, TOL);
        assertEquals(2, fit_7._2, TOL);

        Pair<Double, Double> fit_11 = pred.predict(0, getFrame(11), root);
        assertEquals(2, fit_11._1, TOL);
        assertEquals(3, fit_11._2, TOL);

        Pair<Double, Double> fit_20 = pred.predict(0, getFrame(20), root);
        assertEquals(3, fit_20._1, TOL);
        assertEquals(4, fit_20._2, TOL);


        // missing

        Pair<Double, Double> fit_na = pred.predict(0, getFrame(Double.NaN), left);
        assertEquals((0.0 * 1.0 + 1.0 * 2.0) / 3.0, fit_na._1, TOL);
        assertEquals((1.0 + 2.0) / 2.0, fit_na._2, TOL);

        Pair<Double, Double> fit_na_root = pred.predict(0, getFrame(Double.NaN), root);
        assertEquals((0.0 * 1.0 + 1.0 * 2.0 + 2.0 * 3.0 + 3.0 * 4.0) / (1.0 + 2.0 + 3.0 + 4.0), fit_na_root._1, TOL);
        assertEquals((1.0 + 2.0 + 3.0 + 4.0) / 4.0, fit_na_root._2, TOL);


    }

    Frame getFrame(double value) {
        return SolidFrame.byVars(NumVar.wrap(value).withName("x"));
    }
}
