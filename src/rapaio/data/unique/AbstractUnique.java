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

package rapaio.data.unique;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntArrays;
import it.unimi.dsi.fastutil.ints.IntList;

/**
 * Created by <a href="mailto:padreati@yahoo.com">Aurelian Tutuianu</a> on 10/23/18.
 */
public abstract class AbstractUnique implements Unique {

    protected IntArrayList uniqueIds;
    protected Int2ObjectOpenHashMap<IntList> uniqueRowLists;
    protected IntArrayList countSortedIds;
    protected IntArrayList valueSortedIds;
    protected int[] idsByRow;

    protected void updateIdsByRow(int len) {
        idsByRow = new int[len];
        for (Int2ObjectOpenHashMap.Entry<IntList> e : uniqueRowLists.int2ObjectEntrySet()) {
            for (int row : e.getValue()) {
                idsByRow[row] = e.getIntKey();
            }
        }
    }

    @Override
    public IntList countSortedIds() {
        if (countSortedIds == null) {
            int[] counts = new int[uniqueIds.size()];
            int[] ids = new int[uniqueIds.size()];
            for (int i = 0; i < uniqueIds.size(); i++) {
                counts[i] = uniqueRowLists.get(uniqueIds.getInt(i)).size();
                ids[i] = uniqueIds.getInt(i);
            }
            IntArrays.quickSort(ids, (i, j) -> Integer.compare(counts[i], counts[j]));
            countSortedIds = new IntArrayList(ids);
        }
        return countSortedIds;
    }

    @Override
    public int idByRow(int row) {
        return idsByRow[row];
    }
}