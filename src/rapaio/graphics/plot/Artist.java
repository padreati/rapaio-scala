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

package rapaio.graphics.plot;

import rapaio.graphics.opt.GOptions;

import java.awt.*;
import java.io.Serializable;

/**
 * @author Aurelian Tutuianu
 */
public abstract class Artist implements Serializable {

    private static final long serialVersionUID = -797168275849511614L;
    protected final GOptions options = new GOptions();
    protected Axes parent;
    private DataRange range;

    public GOptions getOptions() {
        return options;
    }

    public void bind(Axes parent) {
        if (parent == null) {
            throw new IllegalArgumentException("parent plot reference is null");
        }
        this.parent = parent;
        this.options.setParent(parent.plot.options);
    }

    public double xScale(double x) {
        return parent.xScale(x);
    }

    public double yScale(double y) {
        return parent.yScale(y);
    }

    public abstract void updateDataRange(DataRange dataRange);

    public abstract void paint(Graphics2D g2d);
}