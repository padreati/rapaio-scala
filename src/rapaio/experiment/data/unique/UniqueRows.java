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

package rapaio.experiment.data.unique;

import it.unimi.dsi.fastutil.doubles.Double2IntOpenHashMap;
import it.unimi.dsi.fastutil.doubles.DoubleAVLTreeSet;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntAVLTreeSet;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongAVLTreeSet;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import rapaio.core.RandomSource;
import rapaio.data.SolidFrame;
import rapaio.data.Var;
import rapaio.data.VarDouble;
import rapaio.data.VarInt;
import rapaio.data.VarLong;
import rapaio.data.accessor.VarDoubleDataAccessor;
import rapaio.data.filter.frame.FRefSort;

import java.util.TreeSet;

/**
 * Created by <a href="mailto:padreati@yahoo.com">Aurelian Tutuianu</a> on 8/16/18.
 */
public class UniqueRows {

    public static UniqueRows from(Var var) {

        if(var instanceof VarDouble) {
            return fromVarDouble((VarDouble) var);
        }
        if(var instanceof VarInt) {
            return fromVarInt((VarInt)var);
        }
        if(var instanceof VarLong) {
            return fromVarLong((VarLong)var);
        }

        switch (var.type()) {
            case BOOLEAN:
                return fromBoolean(var);
            case INT:
                return fromInt(var);
            case DOUBLE:
                return fromDouble(var);
            case NOMINAL:
                return fromNominal(var);
            default:
                throw new IllegalArgumentException("Not implemented");
        }
    }

    public static UniqueRows fromVarLong(VarLong var) {
        LongAVLTreeSet set = new LongAVLTreeSet();
        long[] data = var.getDataAccessor().getData();
        int rows = var.getDataAccessor().getRowCount();
        for (int i = 0; i < rows; i++) {
            set.add(data[i]);
        }
        int uniqueId = 0;
        Long2IntOpenHashMap uniqueKeys = new Long2IntOpenHashMap();
        for (long key : set) {
            uniqueKeys.put(key, uniqueId);
            uniqueId++;
        }
        Int2ObjectOpenHashMap<IntList> uniqueRowLists = new Int2ObjectOpenHashMap<>();
        for (int i = 0; i < rows; i++) {
            long key = data[i];
            int id = uniqueKeys.get(key);
            if (!uniqueRowLists.containsKey(id)) {
                uniqueRowLists.put(id, new IntArrayList());
            }
            uniqueRowLists.get(id).add(i);
        }
        return new UniqueRows(uniqueRowLists);
    }

    public static UniqueRows fromVarInt(VarInt var) {
        IntAVLTreeSet set = new IntAVLTreeSet();
        int[] data = var.getDataAccessor().getData();
        int rows = var.getDataAccessor().getRowCount();
        for (int i = 0; i < rows; i++) {
            set.add(data[i]);
        }
        int uniqueId = 0;
        Int2IntOpenHashMap uniqueKeys = new Int2IntOpenHashMap();
        for (int key : set) {
            uniqueKeys.put(key, uniqueId);
            uniqueId++;
        }
        Int2ObjectOpenHashMap<IntList> uniqueRowLists = new Int2ObjectOpenHashMap<>();
        for (int i = 0; i < rows; i++) {
            int key = data[i];
            int id = uniqueKeys.get(key);
            if (!uniqueRowLists.containsKey(id)) {
                uniqueRowLists.put(id, new IntArrayList());
            }
            uniqueRowLists.get(id).add(i);
        }
        return new UniqueRows(uniqueRowLists);
    }

    public static UniqueRows fromVarDouble(VarDouble var) {
        VarDoubleDataAccessor accessor = var.getDataAccessor();
        double[] data = accessor.getData();
        int rowCount = accessor.getRowCount();

        DoubleAVLTreeSet set = new DoubleAVLTreeSet();
        for (int i = 0; i < rowCount; i++) {
            set.add(data[i]);
        }
        int uniqueId = 0;
        Double2IntOpenHashMap uniqueKeys = new Double2IntOpenHashMap();
        for (double key : set) {
            uniqueKeys.put(key, uniqueId);
            uniqueId++;
        }
        Int2ObjectOpenHashMap<IntList> uniqueRowLists = new Int2ObjectOpenHashMap<>();
        for (int i = 0; i < rowCount; i++) {
            double key = data[i];
            int id = uniqueKeys.get(key);
            if (!uniqueRowLists.containsKey(id)) {
                uniqueRowLists.put(id, new IntArrayList());
            }
            uniqueRowLists.get(id).add(i);
        }
        return new UniqueRows(uniqueRowLists);
    }

    public static UniqueRows fromBoolean(Var var) {
        IntList naList = new IntArrayList();
        IntList zeroList = new IntArrayList();
        IntList oneList = new IntArrayList();

        for (int i = 0; i < var.rowCount(); i++) {
            if (var.isMissing(i)) {
                naList.add(i);
                continue;
            }
            if (var.getInt(i) == 0) {
                zeroList.add(i);
            } else {
                oneList.add(i);
            }
        }
        Int2ObjectOpenHashMap<IntList> uniqueRowLists = new Int2ObjectOpenHashMap<>();
        int uniqueId = 0;
        if (!naList.isEmpty()) {
            uniqueRowLists.put(uniqueId, naList);
            uniqueId++;
        }
        if (!zeroList.isEmpty()) {
            uniqueRowLists.put(uniqueId, zeroList);
            uniqueId++;
        }
        if (!oneList.isEmpty()) {
            uniqueRowLists.put(uniqueId, oneList);
        }
        return new UniqueRows(uniqueRowLists);
    }

    public static UniqueRows fromInt(Var var) {
        IntAVLTreeSet set = new IntAVLTreeSet();
        for (int i = 0; i < var.rowCount(); i++) {
            set.add(var.getInt(i));
        }
        int uniqueId = 0;
        Int2IntOpenHashMap uniqueKeys = new Int2IntOpenHashMap();
        for (int key : set) {
            uniqueKeys.put(key, uniqueId);
            uniqueId++;
        }
        Int2ObjectOpenHashMap<IntList> uniqueRowLists = new Int2ObjectOpenHashMap<>();
        for (int i = 0; i < var.rowCount(); i++) {
            int key = var.getInt(i);
            int id = uniqueKeys.get(key);
            if (!uniqueRowLists.containsKey(id)) {
                uniqueRowLists.put(id, new IntArrayList());
            }
            uniqueRowLists.get(id).add(i);
        }
        return new UniqueRows(uniqueRowLists);
    }

    public static UniqueRows fromDouble(Var var) {
        DoubleAVLTreeSet set = new DoubleAVLTreeSet();
        for (int i = 0; i < var.rowCount(); i++) {
            set.add(var.getDouble(i));
        }
        int uniqueId = 0;
        Double2IntOpenHashMap uniqueKeys = new Double2IntOpenHashMap();
        for (double key : set) {
            uniqueKeys.put(key, uniqueId);
            uniqueId++;
        }
        Int2ObjectOpenHashMap<IntList> uniqueRowLists = new Int2ObjectOpenHashMap<>();
        for (int i = 0; i < var.rowCount(); i++) {
            double key = var.getDouble(i);
            int id = uniqueKeys.get(key);
            if (!uniqueRowLists.containsKey(id)) {
                uniqueRowLists.put(id, new IntArrayList());
            }
            uniqueRowLists.get(id).add(i);
        }
        return new UniqueRows(uniqueRowLists);
    }

    public static UniqueRows fromNominal(Var var) {
        TreeSet<String> set = new TreeSet<>();
        for (int i = 0; i < var.rowCount(); i++) {
            set.add(var.getLabel(i));
        }
        int uniqueId = 0;
        Object2IntOpenHashMap<String> uniqueKeys = new Object2IntOpenHashMap<>();
        for (String key : set) {
            uniqueKeys.put(key, uniqueId);
            uniqueId++;
        }
        Int2ObjectOpenHashMap<IntList> uniqueRowLists = new Int2ObjectOpenHashMap<>();
        for (int i = 0; i < var.rowCount(); i++) {
            String key = var.getLabel(i);
            int id = uniqueKeys.get(key);
            if (!uniqueRowLists.containsKey(id)) {
                uniqueRowLists.put(id, new IntArrayList());
            }
            uniqueRowLists.get(id).add(i);
        }
        return new UniqueRows(uniqueRowLists);
    }

    private final int[] uniqueIndex;

    private final Int2ObjectOpenHashMap<IntList> uniqueRowLists;
    public UniqueRows(Int2ObjectOpenHashMap<IntList> uniqueRowLists) {
        this.uniqueRowLists = uniqueRowLists;
        int count = 0;
        for (IntList list : uniqueRowLists.values()) {
            count += list.size();
        }
        uniqueIndex = new int[count];
        for (int key : uniqueRowLists.keySet()) {
            for (int row : uniqueRowLists.get(key)) {
                uniqueIndex[row] = key;
            }
        }
    }

    public int getUniqueCount() {
        return uniqueRowLists.size();
    }

    public int getUniqueId(int row) {
        return uniqueIndex[row];
    }

    public IntList getRowList(int uniqueId) {
        return uniqueRowLists.get(uniqueId);
    }

    public static void main(String[] args) {
        RandomSource.setSeed(123);
        Var var = VarInt.empty().withName("var");
        for (int i = 0; i < 100; i++) {
            double p = RandomSource.nextDouble();
            if(p<0.1) {
                var.addMissing();
                continue;
            }
            double val = RandomSource.nextDouble();
            val = Math.round((val-0.5) * 100);
            var.addDouble(val);
        }
        UniqueRows uniqueRows = UniqueRows.from(var);
        Var uniques = VarInt.empty().withName("id");
        for (int i = 0; i < var.rowCount(); i++) {
            uniques.addInt(uniqueRows.getUniqueId(i));
        }
        SolidFrame.byVars(var, uniques).fapply(FRefSort.by(var.refComparator())).printLines();
    }
}
