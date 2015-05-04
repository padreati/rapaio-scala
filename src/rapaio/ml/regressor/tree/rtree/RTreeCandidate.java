/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 *    Copyright 2013 Aurelian Tutuianu
 *    Copyright 2014 Aurelian Tutuianu
 *    Copyright 2015 Aurelian Tutuianu
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

package rapaio.ml.regressor.tree.rtree;

import rapaio.data.stream.FSpot;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * RTree split candidate.
 * <p>
 * Created by <a href="mailto:padreati@yahoo.com>Aurelian Tutuianu</a> on 11/24/14.
 */
@Deprecated
public class RTreeCandidate implements Comparable<RTreeCandidate>, Serializable {

    private final double score;
    private final String testName;
    private final List<String> groupNames = new ArrayList<>();
    private final List<Predicate<FSpot>> groupPredicates = new ArrayList<>();

    public RTreeCandidate(double score, String testName) {
        this.score = score;
        this.testName = testName;
    }

    public void addGroup(String name, Predicate<FSpot> predicate) {
        if (groupNames.contains(name)) {
            throw new IllegalArgumentException("group name already defined");
        }
        groupNames.add(name);
        groupPredicates.add(predicate);
    }

    public List<String> getGroupNames() {
        return groupNames;
    }

    public List<Predicate<FSpot>> getGroupPredicates() {
        return groupPredicates;
    }

    public double getScore() {
        return score;
    }

    public String getTestName() {
        return testName;
    }

    @Override
    public int compareTo(RTreeCandidate o) {
        if (o == null) return -1;
        return new Double(score).compareTo(o.score);
    }
}