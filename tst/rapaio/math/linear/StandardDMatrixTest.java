/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 *    Copyright 2013 - 2021 Aurelian Tutuianu
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

package rapaio.math.linear;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rapaio.core.RandomSource;
import rapaio.math.linear.base.DMatrixBase;
import rapaio.math.linear.dense.DMatrixStripe;
import rapaio.math.linear.dense.DVectorDense;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by <a href="mailto:padreati@yahoo.com">Aurelian Tutuianu</a> on 1/14/20.
 */
public abstract class StandardDMatrixTest {

    protected static final double TOL = 1e-15;

    @BeforeEach
    void beforeEach() {
        RandomSource.setSeed(123);
    }

    protected abstract SOrder order();

    protected abstract DMatrix generateSequential(int n, int m);

    protected abstract DMatrix generateIdentity(int n);

    protected abstract DMatrix generateFill(int n, int m, double fill);

    protected abstract DMatrix generateCopy(double[][] values);

    protected abstract String className();

    @Test
    void typeTest() {
        assertEquals(order(), generateSequential(10, 3).order());
    }

    @Test
    void testMapRow() {
        DMatrix m = generateSequential(10, 11);
        DVector vector1 = m.mapRow(3);
        DVector vector2 = m.mapRowCopy(3);
        for (int i = 0; i < vector1.size(); i++) {
            assertEquals(33.0 + i, vector1.get(i), TOL);
            assertEquals(33.0 + i, vector2.get(i), TOL);
        }
    }

    @Test
    void testMapRows() {
        DMatrix m = generateSequential(10, 11);

        DMatrix view = m.mapRows(3, 4);
        DMatrix copy = m.mapRowsCopy(3, 4);

        for (int i = 0; i < view.rowCount(); i++) {
            for (int j = 0; j < view.colCount(); j++) {
                assertEquals(view.get(i, j), copy.get(i, j), TOL);
            }
        }

        view.set(0, 0, 100);
        copy.set(0, 0, 200);

        assertEquals(m.get(3, 0), view.get(0, 0), TOL);
        assertEquals(100, m.get(3, 0), TOL);
        assertEquals(200, copy.get(0, 0), TOL);
    }

    @Test
    void testRangeRows() {
        DMatrix m = generateSequential(10, 11);

        DMatrix view = m.rangeRows(3, 5);
        DMatrix copy = m.rangeRowsCopy(3, 5);

        for (int i = 0; i < view.rowCount(); i++) {
            for (int j = 0; j < view.colCount(); j++) {
                assertEquals(view.get(i, j), copy.get(i, j), TOL);
            }
        }

        view.set(0, 0, 100);
        copy.set(0, 0, 200);

        assertEquals(m.get(3, 0), view.get(0, 0), TOL);
        assertEquals(100, m.get(3, 0), TOL);
        assertEquals(200, copy.get(0, 0), TOL);
    }

    @Test
    void testRemoveRows() {
        DMatrix m = generateSequential(10, 11);

        DMatrix view = m.removeRows(0, 1, 2, 4, 6, 7, 8, 9, 10);
        DMatrix copy = m.removeRowsCopy(0, 1, 2, 4, 6, 7, 8, 9, 10);

        for (int i = 0; i < view.rowCount(); i++) {
            for (int j = 0; j < view.colCount(); j++) {
                assertEquals(view.get(i, j), copy.get(i, j), TOL);
            }
        }

        view.set(0, 0, 100);
        copy.set(0, 0, 200);

        assertEquals(m.get(3, 0), view.get(0, 0), TOL);
        assertEquals(100, m.get(3, 0), TOL);
        assertEquals(200, copy.get(0, 0), TOL);
    }

    @Test
    void testMapCol() {
        DMatrix m = generateSequential(10, 11);
        DVector vector1 = m.mapCol(3);
        DVector vector2 = m.mapColCopy(3);
        for (int i = 0; i < vector1.size(); i++) {
            assertEquals(3.0 + i * 11, vector1.get(i), TOL);
            assertEquals(3.0 + i * 11, vector2.get(i), TOL);
        }
    }

    @Test
    void testMapCols() {
        DMatrix m = generateSequential(10, 11);

        DMatrix view = m.mapCols(3, 4);
        DMatrix copy = m.mapColsCopy(3, 4);

        for (int i = 0; i < view.rowCount(); i++) {
            for (int j = 0; j < view.colCount(); j++) {
                assertEquals(view.get(i, j), copy.get(i, j), TOL);
            }
        }

        view.set(0, 0, 100);
        copy.set(0, 0, 200);

        assertEquals(m.get(0, 3), view.get(0, 0), TOL);
        assertEquals(100, m.get(0, 3), TOL);
        assertEquals(200, copy.get(0, 0), TOL);
    }

    @Test
    void testRangeCols() {
        DMatrix m = generateSequential(10, 11);

        DMatrix view = m.rangeCols(3, 5);
        DMatrix copy = m.rangeColsCopy(3, 5);

        for (int i = 0; i < view.rowCount(); i++) {
            for (int j = 0; j < view.colCount(); j++) {
                assertEquals(view.get(i, j), copy.get(i, j), TOL);
            }
        }

        view.set(0, 0, 100);
        copy.set(0, 0, 200);

        assertEquals(m.get(0, 3), view.get(0, 0), TOL);
        assertEquals(100, m.get(0, 3), TOL);
        assertEquals(200, copy.get(0, 0), TOL);
    }

    @Test
    void testRemoveCols() {
        DMatrix m = generateSequential(10, 11);

        DMatrix view = m.removeCols(0, 1, 2, 4, 6, 7, 8, 9, 10);
        DMatrix copy = m.removeColsCopy(0, 1, 2, 4, 6, 7, 8, 9, 10);

        for (int i = 0; i < view.rowCount(); i++) {
            for (int j = 0; j < view.colCount(); j++) {
                assertEquals(view.get(i, j), copy.get(i, j), TOL);
            }
        }

        view.set(0, 0, 100);
        copy.set(0, 0, 200);

        assertEquals(m.get(0, 3), view.get(0, 0), TOL);
        assertEquals(100, m.get(0, 3), TOL);
        assertEquals(200, copy.get(0, 0), TOL);
    }

    @Test
    void testAdd() {
        DMatrix m1 = generateSequential(20, 10);
        DMatrix m2 = generateSequential(20, 10);

        DMatrix t1 = m1.copy().add(m2);
        DMatrix t2 = m1.copy().add(1);
        DMatrix t3 = m1.add(1).add(m2);

        assertEquals(20, t1.rowCount());
        assertEquals(10, t1.colCount());
        assertEquals(20, t2.rowCount());
        assertEquals(10, t2.colCount());
        assertEquals(20, t3.rowCount());
        assertEquals(10, t3.colCount());

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                assertEquals(2 * (i * 10 + j), t1.get(i, j), TOL);
                assertEquals(i * 10 + j + 1, t2.get(i, j), TOL);
                assertEquals(2 * (i * 10 + j) + 1, t3.get(i, j));
            }
        }

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> generateSequential(10, 10).add(generateSequential(11, 11)));
        assertEquals("Matrices are not conform with this operation.", ex.getMessage());
    }

    @Test
    void testAddAxis() {
        DMatrix m1 = generateFill(20, 10, 1);

        DVector v1 = DVectorDense.fill(10, 1);
        DVector v2 = DVectorDense.fill(20, 1);

        assertTrue(generateFill(20, 10, 2).deepEquals(m1.copy().add(v1, 0)));
        assertTrue(generateFill(20, 10, 2).deepEquals(m1.copy().add(v2, 1)));
    }

    @Test
    void testSubtract() {
        DMatrix m1 = generateSequential(20, 10);
        DMatrix m2 = generateSequential(20, 10);

        DMatrix t1 = m1.copy().sub(m2);
        DMatrix t2 = m1.copy().sub(1);
        DMatrix t3 = m1.sub(1).sub(m2);

        assertEquals(20, t1.rowCount());
        assertEquals(10, t1.colCount());
        assertEquals(20, t2.rowCount());
        assertEquals(10, t2.colCount());
        assertEquals(20, t3.rowCount());
        assertEquals(10, t3.colCount());

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                assertEquals(0, t1.get(i, j), TOL);
                assertEquals(i * 10 + j - 1, t2.get(i, j), TOL);
                assertEquals(-1, t3.get(i, j));
            }
        }

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> generateSequential(10, 10).sub(generateSequential(9, 10)));
        assertEquals("Matrices are not conform with this operation.", ex.getMessage());
    }

    @Test
    void testSubtractAxis() {
        DMatrix m1 = generateFill(20, 10, 1);

        DVector v1 = DVectorDense.fill(10, 1);
        DVector v2 = DVectorDense.fill(20, 1);

        assertTrue(generateFill(20, 10, 0).deepEquals(m1.copy().sub(v1, 0)));
        assertTrue(generateFill(20, 10, 0).deepEquals(m1.copy().sub(v2, 1)));
    }

    @Test
    void testMultiply() {
        DMatrix m1 = generateSequential(20, 10);
        DMatrix m2 = generateSequential(20, 10);

        DMatrix t1 = m1.copy().mult(m2);
        DMatrix t2 = m1.copy().mult(2);
        DMatrix t3 = m1.mult(2).mult(m2);

        assertEquals(20, t1.rowCount());
        assertEquals(10, t1.colCount());
        assertEquals(20, t2.rowCount());
        assertEquals(10, t2.colCount());
        assertEquals(20, t3.rowCount());
        assertEquals(10, t3.colCount());

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                assertEquals(Math.pow(i * 10 + j, 2), t1.get(i, j), TOL);
                assertEquals(2 * (i * 10 + j), t2.get(i, j), TOL);
                assertEquals(2 * Math.pow(i * 10 + j, 2), t3.get(i, j));
            }
        }

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> generateSequential(10, 10).mult(generateSequential(10, 9)));
        assertEquals("Matrices are not conform with this operation.", ex.getMessage());

    }

    @Test
    void testDivide() {
        DMatrix m1 = generateSequential(20, 10);
        DMatrix m2 = generateSequential(20, 10);

        DMatrix t1 = m1.copy().div(m2);
        DMatrix t2 = m1.copy().div(2);
        DMatrix t3 = m1.div(2).div(m2);

        assertEquals(20, t1.rowCount());
        assertEquals(10, t1.colCount());
        assertEquals(20, t2.rowCount());
        assertEquals(10, t2.colCount());
        assertEquals(20, t3.rowCount());
        assertEquals(10, t3.colCount());

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                if (i == 0 && j == 0) {
                    assertEquals(Double.NaN, t1.get(i, j), TOL);
                    assertEquals(0, t2.get(i, j), TOL);
                    assertEquals(Double.NaN, t3.get(i, j));
                } else {
                    assertEquals(1, t1.get(i, j), TOL);
                    assertEquals((i * 10. + j) / 2, t2.get(i, j), TOL);
                    assertEquals(1 / 2., t3.get(i, j));
                }
            }
        }

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> generateSequential(10, 10).div(generateSequential(9, 9)));
        assertEquals("Matrices are not conform with this operation.", ex.getMessage());
    }

    @Test
    void testSum() {
        var m1 = generateSequential(5, 3);
        assertEquals(105, m1.sum());
        assertTrue(DVectorDense.wrap(30, 35, 40).deepEquals(m1.sum(0)));
        assertTrue(DVectorDense.wrap(3, 12, 21, 30, 39).deepEquals(m1.sum(1)));
    }

    @Test
    void testApply() {
        var m = generateSequential(10, 10);
        var copy = m.copy();
        m.apply(x -> x - 10).apply(x -> x + 10);
        assertTrue(m.deepEquals(m));
    }

    @Test
    void testDot() {

        DVector v = DVectorDense.ones(10);
        DMatrix m = generateSequential(10, 10);

        var m1 = m.dot(v);
        for (int i = 0; i < m1.size(); i++) {
            assertEquals(45 + 100 * i, m1.get(i), TOL);
        }

        DMatrix n1 = generateSequential(10, 20);
        DMatrix n2 = generateSequential(20, 30);

        var m2 = n1.dot(n2);
        assertEquals(10, m2.rowCount());
        assertEquals(30, m2.colCount());

        for (int i = 0; i < m2.rowCount(); i++) {
            for (int j = 0; j < m2.colCount(); j++) {
                assertEquals(n1.mapRow(i).dot(n2.mapCol(j)), m2.get(i, j), TOL);
            }
        }
    }

    @Test
    void dotDiagTest() {
        var m = generateFill(10, 3, 1);
        var v1 = DVectorDense.wrap(1, 2, 3);

        var d = DMatrixStripe.wrap(new double[][]{
                {1, 0, 0},
                {0, 2, 0},
                {0, 0, 3}
        });
        var r1 = m.copy().dot(d);


        assertTrue(r1.deepEquals(m.copy().dotDiag(v1)));
    }

    @Test
    void testTrace() {
        for (int i = 1; i < 10; i++) {
            int len = i + 1;
            var m = generateSequential(len, len);
            assertEquals((len + 1) * (len) * (len - 1) / 2., m.trace(), TOL);
        }

        var ex = assertThrows(IllegalArgumentException.class, () -> generateSequential(2, 3).trace());
        assertEquals("Matrix is not squared, trace of the matrix is not defined.", ex.getMessage());
    }

    @Test
    void testDiag() {
        var m = generateSequential(10, 10);
        var d = m.diag();
        for (int i = 1; i < 10; i++) {
            assertEquals(i * 11, d.get(i), TOL);
        }
    }

    @Test
    void testScatter() {
        for (int i = 1; i < 10; i++) {
            var x = generateSequential(i, i);
            var s = x.scatter();

            var c = generateIdentity(i).sub(generateFill(i, i, 1.0 / i));
            var s2 = x.t().dot(c).dot(x);

            assertTrue(s2.deepEquals(s, 1e-11));
        }
    }

    @Test
    void testRank() {

        assertEquals(1, generateFill(10, 10, 1).rank());
        assertEquals(2, generateSequential(7, 3).rank());
        assertEquals(1, generateSequential(2, 1).rank());
        assertEquals(3, generateCopy(new double[][]{
                {1, 1, 1},
                {1, 2, 4},
                {3, 2, 1}
        }).rank());
    }

    @Test
    void maxValues() {
        var m = generateCopy(new double[][]{
                {1, 2, 3, 4},
                {5, 6, 7, 8}
        });
        var max0 = m.amax(0);
        assertArrayEquals(new double[]{5, 6, 7, 8}, m.amax(0).asVarDouble().stream().mapToDouble().toArray());
        assertArrayEquals(new double[]{4, 8}, m.amax(1).asVarDouble().stream().mapToDouble().toArray());

        assertArrayEquals(new double[]{1, 2, 3, 4}, m.amin(0).asVarDouble().stream().mapToDouble().toArray());
        assertArrayEquals(new double[]{1, 5}, m.amin(1).asVarDouble().stream().mapToDouble().toArray());
    }

    @Test
    void testArgMaxArgMin() {
        double[][] m = new double[][]{
                {1, -1, 2},
                {2, 1, 1},
                {1, 2, 2},
                {2, 2, 1}
        };

        assertArrayEquals(new int[]{1, 2, 0}, generateCopy(m).argmax(0));
        assertArrayEquals(new int[]{2, 0, 1, 0}, generateCopy(m).argmax(1));

        assertArrayEquals(new int[]{0, 0, 1}, generateCopy(m).argmin(0));
        assertArrayEquals(new int[]{1, 1, 0, 2}, generateCopy(m).argmin(1));
    }

    @Test
    void deepEqualsTest() {
        DMatrix m1 = generateIdentity(2);

        DMatrix m2 = DMatrixBase.wrap(new double[][]{{1, 0}, {0, 1}});
        DMatrix m3 = DMatrixStripe.identity(2);

        assertTrue(m1.deepEquals(m2));
        assertTrue(m2.deepEquals(m3));

        m2.inc(1, 1, 1);
        m3.inc(0, 1, 2);

        assertFalse(m1.deepEquals(m2));
        assertFalse(m1.deepEquals(m3));

        var m4 = DMatrixStripe.fill(2, 3, 0);
        var m5 = DMatrixStripe.fill(3, 2, 0);

        assertFalse(m4.deepEquals(m1));
        assertFalse(m5.deepEquals(m1));

        m1 = generateCopy(new double[][]{
                {1, 1, 2},
                {1, 2, 4}
        });
        m2 = generateCopy(new double[][]{
                {1d + 0x1.0p-47, 1, 2},
                {1, 2, 4}
        });

        assertTrue(m1.deepEquals(m2));
        assertTrue(m2.deepEquals(m1));
        assertTrue(m1.deepEquals(m2, 1e-14));
        assertFalse(m1.deepEquals(m2, 1e-30));

        assertFalse(m1.deepEquals(generateSequential(2, 2)));
        assertFalse(m1.deepEquals(generateSequential(3, 3)));
    }

    @Test
    void testPrintable() {

        var id3 = generateIdentity(3).add(0x.1p-22);

        assertEquals(className() + "{rowCount:3, colCount:3, values:\n" +
                "[\n" +
                " [ 1 0 0 ], \n" +
                " [ 0 1 0 ], \n" +
                " [ 0 0 1 ], \n" +
                "]}", id3.toString());

        assertEquals("                           [0]                        [1]                        [2] \n" +
                "[0] 1.0000000149011612         0.000000014901161193847656 0.000000014901161193847656 \n" +
                "[1] 0.000000014901161193847656 1.0000000149011612         0.000000014901161193847656 \n" +
                "[2] 0.000000014901161193847656 0.000000014901161193847656 1.0000000149011612         \n", id3.toSummary());

        assertEquals("                           [0]                        [1]                        [2] \n" +
                "[0] 1.0000000149011612         0.000000014901161193847656 0.000000014901161193847656 \n" +
                "[1] 0.000000014901161193847656 1.0000000149011612         0.000000014901161193847656 \n" +
                "[2] 0.000000014901161193847656 0.000000014901161193847656 1.0000000149011612         \n", id3.toContent());

        assertEquals("                           [0]                        [1]                        [2] \n" +
                "[0] 1.0000000149011612         0.000000014901161193847656 0.000000014901161193847656 \n" +
                "[1] 0.000000014901161193847656 1.0000000149011612         0.000000014901161193847656 \n" +
                "[2] 0.000000014901161193847656 0.000000014901161193847656 1.0000000149011612         \n", id3.toFullContent());


        var id25 = generateIdentity(25);

        assertEquals(className() + "{rowCount:25, colCount:25, values:\n" +
                "[\n" +
                " [ 1  0  0  0  0  0  0  0  0  0  .. ], \n" +
                " [ 0  1  0  0  0  0  0  0  0  0  .. ], \n" +
                " [ 0  0  1  0  0  0  0  0  0  0  .. ], \n" +
                " [ 0  0  0  1  0  0  0  0  0  0  .. ], \n" +
                " [ 0  0  0  0  1  0  0  0  0  0  .. ], \n" +
                " [ 0  0  0  0  0  1  0  0  0  0  .. ], \n" +
                " [ 0  0  0  0  0  0  1  0  0  0  .. ], \n" +
                " [ 0  0  0  0  0  0  0  1  0  0  .. ], \n" +
                " [ 0  0  0  0  0  0  0  0  1  0  .. ], \n" +
                " [ 0  0  0  0  0  0  0  0  0  1  .. ], \n" +
                " [ 0  0  0  0  0  0  0  0  0  0  .. ], \n" +
                " [ 0  0  0  0  0  0  0  0  0  0  .. ], \n" +
                " [ 0  0  0  0  0  0  0  0  0  0  .. ], \n" +
                " [ 0  0  0  0  0  0  0  0  0  0  .. ], \n" +
                " [ 0  0  0  0  0  0  0  0  0  0  .. ], \n" +
                " [ 0  0  0  0  0  0  0  0  0  0  .. ], \n" +
                " [ 0  0  0  0  0  0  0  0  0  0  .. ], \n" +
                " [ 0  0  0  0  0  0  0  0  0  0  .. ], \n" +
                " [ 0  0  0  0  0  0  0  0  0  0  .. ], \n" +
                " [ 0  0  0  0  0  0  0  0  0  0  .. ], \n" +
                " [ 0  0  0  0  0  0  0  0  0  0  .. ], \n" +
                " [ 0  0  0  0  0  0  0  0  0  0  .. ], \n" +
                " [ 0  0  0  0  0  0  0  0  0  0  .. ], \n" +
                " [ 0  0  0  0  0  0  0  0  0  0  .. ], \n" +
                " [ .. .. .. .. .. .. .. .. .. .. .. ], \n" +
                "]}", id25.toString());

        assertEquals("     [0] [1] [2] [3] [4] [5] [6] [7] [8] [9] [10] [11] [12] [13] [14] [15] [16] [17] [18] [19] ... [23] [24] \n" +
                        " [0]  1   0   0   0   0   0   0   0   0   0   0    0    0    0    0    0    0    0    0    0   ...  0    0   \n" +
                        " [1]  0   1   0   0   0   0   0   0   0   0   0    0    0    0    0    0    0    0    0    0   ...  0    0   \n" +
                        " [2]  0   0   1   0   0   0   0   0   0   0   0    0    0    0    0    0    0    0    0    0   ...  0    0   \n" +
                        " [3]  0   0   0   1   0   0   0   0   0   0   0    0    0    0    0    0    0    0    0    0   ...  0    0   \n" +
                        " [4]  0   0   0   0   1   0   0   0   0   0   0    0    0    0    0    0    0    0    0    0   ...  0    0   \n" +
                        " [5]  0   0   0   0   0   1   0   0   0   0   0    0    0    0    0    0    0    0    0    0   ...  0    0   \n" +
                        " [6]  0   0   0   0   0   0   1   0   0   0   0    0    0    0    0    0    0    0    0    0   ...  0    0   \n" +
                        " [7]  0   0   0   0   0   0   0   1   0   0   0    0    0    0    0    0    0    0    0    0   ...  0    0   \n" +
                        " [8]  0   0   0   0   0   0   0   0   1   0   0    0    0    0    0    0    0    0    0    0   ...  0    0   \n" +
                        " [9]  0   0   0   0   0   0   0   0   0   1   0    0    0    0    0    0    0    0    0    0   ...  0    0   \n" +
                        "[10]  0   0   0   0   0   0   0   0   0   0   1    0    0    0    0    0    0    0    0    0   ...  0    0   \n" +
                        "[11]  0   0   0   0   0   0   0   0   0   0   0    1    0    0    0    0    0    0    0    0   ...  0    0   \n" +
                        "[12]  0   0   0   0   0   0   0   0   0   0   0    0    1    0    0    0    0    0    0    0   ...  0    0   \n" +
                        "[13]  0   0   0   0   0   0   0   0   0   0   0    0    0    1    0    0    0    0    0    0   ...  0    0   \n" +
                        "[14]  0   0   0   0   0   0   0   0   0   0   0    0    0    0    1    0    0    0    0    0   ...  0    0   \n" +
                        "[15]  0   0   0   0   0   0   0   0   0   0   0    0    0    0    0    1    0    0    0    0   ...  0    0   \n" +
                        "[16]  0   0   0   0   0   0   0   0   0   0   0    0    0    0    0    0    1    0    0    0   ...  0    0   \n" +
                        "[17]  0   0   0   0   0   0   0   0   0   0   0    0    0    0    0    0    0    1    0    0   ...  0    0   \n" +
                        "[18]  0   0   0   0   0   0   0   0   0   0   0    0    0    0    0    0    0    0    1    0   ...  0    0   \n" +
                        "[19]  0   0   0   0   0   0   0   0   0   0   0    0    0    0    0    0    0    0    0    1   ...  0    0   \n" +
                        "...  ... ... ... ... ... ... ... ... ... ... ...  ...  ...  ...  ...  ...  ...  ...  ...  ...  ... ...  ...  \n" +
                        "[23]  0   0   0   0   0   0   0   0   0   0   0    0    0    0    0    0    0    0    0    0   ...  1    0   \n" +
                        "[24]  0   0   0   0   0   0   0   0   0   0   0    0    0    0    0    0    0    0    0    0   ...  0    1   \n",
                id25.toSummary());

        assertEquals("     [0] [1] [2] [3] [4] [5] [6] [7] [8] [9] [10] [11] [12] [13] [14] [15] [16] [17] [18] [19] ... [23] [24] \n" +
                        " [0]  1   0   0   0   0   0   0   0   0   0   0    0    0    0    0    0    0    0    0    0   ...  0    0   \n" +
                        " [1]  0   1   0   0   0   0   0   0   0   0   0    0    0    0    0    0    0    0    0    0   ...  0    0   \n" +
                        " [2]  0   0   1   0   0   0   0   0   0   0   0    0    0    0    0    0    0    0    0    0   ...  0    0   \n" +
                        " [3]  0   0   0   1   0   0   0   0   0   0   0    0    0    0    0    0    0    0    0    0   ...  0    0   \n" +
                        " [4]  0   0   0   0   1   0   0   0   0   0   0    0    0    0    0    0    0    0    0    0   ...  0    0   \n" +
                        " [5]  0   0   0   0   0   1   0   0   0   0   0    0    0    0    0    0    0    0    0    0   ...  0    0   \n" +
                        " [6]  0   0   0   0   0   0   1   0   0   0   0    0    0    0    0    0    0    0    0    0   ...  0    0   \n" +
                        " [7]  0   0   0   0   0   0   0   1   0   0   0    0    0    0    0    0    0    0    0    0   ...  0    0   \n" +
                        " [8]  0   0   0   0   0   0   0   0   1   0   0    0    0    0    0    0    0    0    0    0   ...  0    0   \n" +
                        " [9]  0   0   0   0   0   0   0   0   0   1   0    0    0    0    0    0    0    0    0    0   ...  0    0   \n" +
                        "[10]  0   0   0   0   0   0   0   0   0   0   1    0    0    0    0    0    0    0    0    0   ...  0    0   \n" +
                        "[11]  0   0   0   0   0   0   0   0   0   0   0    1    0    0    0    0    0    0    0    0   ...  0    0   \n" +
                        "[12]  0   0   0   0   0   0   0   0   0   0   0    0    1    0    0    0    0    0    0    0   ...  0    0   \n" +
                        "[13]  0   0   0   0   0   0   0   0   0   0   0    0    0    1    0    0    0    0    0    0   ...  0    0   \n" +
                        "[14]  0   0   0   0   0   0   0   0   0   0   0    0    0    0    1    0    0    0    0    0   ...  0    0   \n" +
                        "[15]  0   0   0   0   0   0   0   0   0   0   0    0    0    0    0    1    0    0    0    0   ...  0    0   \n" +
                        "[16]  0   0   0   0   0   0   0   0   0   0   0    0    0    0    0    0    1    0    0    0   ...  0    0   \n" +
                        "[17]  0   0   0   0   0   0   0   0   0   0   0    0    0    0    0    0    0    1    0    0   ...  0    0   \n" +
                        "[18]  0   0   0   0   0   0   0   0   0   0   0    0    0    0    0    0    0    0    1    0   ...  0    0   \n" +
                        "[19]  0   0   0   0   0   0   0   0   0   0   0    0    0    0    0    0    0    0    0    1   ...  0    0   \n" +
                        "...  ... ... ... ... ... ... ... ... ... ... ...  ...  ...  ...  ...  ...  ...  ...  ...  ...  ... ...  ...  \n" +
                        "[23]  0   0   0   0   0   0   0   0   0   0   0    0    0    0    0    0    0    0    0    0   ...  1    0   \n" +
                        "[24]  0   0   0   0   0   0   0   0   0   0   0    0    0    0    0    0    0    0    0    0   ...  0    1   \n",
                id25.toContent());

        assertEquals("     [0] [1] [2] [3] [4] [5] [6] [7] [8] [9] [10] [11] [12] [13] [14] [15] [16] [17] [18] [19] [20] [21] [22] [23] [24] \n" +
                        " [0]  1   0   0   0   0   0   0   0   0   0   0    0    0    0    0    0    0    0    0    0    0    0    0    0    0   \n" +
                        " [1]  0   1   0   0   0   0   0   0   0   0   0    0    0    0    0    0    0    0    0    0    0    0    0    0    0   \n" +
                        " [2]  0   0   1   0   0   0   0   0   0   0   0    0    0    0    0    0    0    0    0    0    0    0    0    0    0   \n" +
                        " [3]  0   0   0   1   0   0   0   0   0   0   0    0    0    0    0    0    0    0    0    0    0    0    0    0    0   \n" +
                        " [4]  0   0   0   0   1   0   0   0   0   0   0    0    0    0    0    0    0    0    0    0    0    0    0    0    0   \n" +
                        " [5]  0   0   0   0   0   1   0   0   0   0   0    0    0    0    0    0    0    0    0    0    0    0    0    0    0   \n" +
                        " [6]  0   0   0   0   0   0   1   0   0   0   0    0    0    0    0    0    0    0    0    0    0    0    0    0    0   \n" +
                        " [7]  0   0   0   0   0   0   0   1   0   0   0    0    0    0    0    0    0    0    0    0    0    0    0    0    0   \n" +
                        " [8]  0   0   0   0   0   0   0   0   1   0   0    0    0    0    0    0    0    0    0    0    0    0    0    0    0   \n" +
                        " [9]  0   0   0   0   0   0   0   0   0   1   0    0    0    0    0    0    0    0    0    0    0    0    0    0    0   \n" +
                        "[10]  0   0   0   0   0   0   0   0   0   0   1    0    0    0    0    0    0    0    0    0    0    0    0    0    0   \n" +
                        "[11]  0   0   0   0   0   0   0   0   0   0   0    1    0    0    0    0    0    0    0    0    0    0    0    0    0   \n" +
                        "[12]  0   0   0   0   0   0   0   0   0   0   0    0    1    0    0    0    0    0    0    0    0    0    0    0    0   \n" +
                        "[13]  0   0   0   0   0   0   0   0   0   0   0    0    0    1    0    0    0    0    0    0    0    0    0    0    0   \n" +
                        "[14]  0   0   0   0   0   0   0   0   0   0   0    0    0    0    1    0    0    0    0    0    0    0    0    0    0   \n" +
                        "[15]  0   0   0   0   0   0   0   0   0   0   0    0    0    0    0    1    0    0    0    0    0    0    0    0    0   \n" +
                        "[16]  0   0   0   0   0   0   0   0   0   0   0    0    0    0    0    0    1    0    0    0    0    0    0    0    0   \n" +
                        "[17]  0   0   0   0   0   0   0   0   0   0   0    0    0    0    0    0    0    1    0    0    0    0    0    0    0   \n" +
                        "[18]  0   0   0   0   0   0   0   0   0   0   0    0    0    0    0    0    0    0    1    0    0    0    0    0    0   \n" +
                        "[19]  0   0   0   0   0   0   0   0   0   0   0    0    0    0    0    0    0    0    0    1    0    0    0    0    0   \n" +
                        "[20]  0   0   0   0   0   0   0   0   0   0   0    0    0    0    0    0    0    0    0    0    1    0    0    0    0   \n" +
                        "[21]  0   0   0   0   0   0   0   0   0   0   0    0    0    0    0    0    0    0    0    0    0    1    0    0    0   \n" +
                        "[22]  0   0   0   0   0   0   0   0   0   0   0    0    0    0    0    0    0    0    0    0    0    0    1    0    0   \n" +
                        "[23]  0   0   0   0   0   0   0   0   0   0   0    0    0    0    0    0    0    0    0    0    0    0    0    1    0   \n" +
                        "[24]  0   0   0   0   0   0   0   0   0   0   0    0    0    0    0    0    0    0    0    0    0    0    0    0    1   \n",
                id25.toFullContent());


    }
}