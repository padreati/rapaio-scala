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

package rapaio.data

import rapaio.data.mapping._
import java.io.Serializable
import scala.annotation.tailrec
import rapaio.printer.Printable

/**
 * Random access list of observed values for a specific variable.
 *
 * @author Aurelian Tutuianu
 */
trait Feature extends Serializable with Printable {

  def typeName: String

  /**
   * @return true is the vector can be used as a nominal variable, false otherwise
   */
  def isNominal: Boolean

  /**
   * @return true if the vector can be treated as a numeric variable, false otherwise
   */
  def isNumeric: Boolean

  /**
   * @return true if the vector is mapped (is a mapping over an original vector),
   *         false otherwise
   */
  def isMappedFeature: Boolean

  /**
   * @return the source vector if is a mapping vector, otherwise
   *         the same instance is returned
   */
  def source: Feature

  /**
   * @return mapping which consists of all rowId for all the available rows, null if
   *         the vector is not mapped
   */
  def mapping: Mapping

  /**
   * @return number of observations contained by the vector
   */
  def rowCount: Int

  /**
   * Returns observation identifier which is an integer.
   * <p>
   * When a vector or frame is created from scratch as a solid vector/frame then
   * row identifiers are the row numbers. When the vector/frame wraps other
   * vector/frame then row identifier is the wrapped row identifier.
   * <p>
   * This is mostly used to keep track of the original row numbers even after a series
   * of transformations which use wrapped vectors/frames.
   *
   * @param row row for which row identifier is returned
   * @return row identifier
   */
  def rowId(row: Int): Int

  def remove(row: Int)

  def removeRange(from: Int, to: Int)

  def clear(): Unit

  def trimToSize(): Unit

  def ensureCapacity(minCapacity: Int)

  def missing: Missing

  def values: Values

  def indexes: Indexes

  def labels: Labels

  abstract class Missing {
    def apply(row: Int): Boolean

    def update(row: Int, value: Boolean): Unit

    def ++(): Unit

    def count: Double = {
      def count(i: Int, cnt: Int): Int = {
        if (i < rowCount)
          if (missing(i)) count(i + 1, cnt + 1)
          else count(i + 1, cnt)
        else cnt
      }
      count(0, 0)
    }
  }

  abstract class Values {
    def apply(row: Int): Double

    def update(row: Int, value: Double): Unit

    def ++(value: Double): Unit

    def foreach[U](f: Double => U) {
      for (i <- 0 until rowCount) {
        f(source.values(rowId(i)))
      }
    }

    def filterComplete(p: (Double) => Boolean): Array[Double] = {
      def compose(x: Double): Boolean = {
        if (x.isNaN) false
        else p(x)
      }
      filter(compose)
    }

    def filter(p: (Double) => Boolean): Array[Double] = {

      @tailrec
      def filter(i: Int, list: List[Double]): List[Double] = {
        if (i >= rowCount) list
        else {
          if (p(values(i))) filter(i + 1, list ::: List(values(i)))
          else filter(i + 1, list)
        }
      }
      filter(0, List.empty).toArray
    }

    def count(p: (Double) => Boolean): Int = {

      @tailrec
      def filter(i: Int, count: Int): Int = {
        if (i >= rowCount) count
        else {
          if (p(values(i))) filter(i + 1, count + 1)
          else filter(i + 1, count)
        }
      }
      filter(0, 0)
    }

    def transform(f: (Double) => Double): Unit = {
      def transform(i: Int) {
        if (i >= rowCount) Unit
        else {
          values(i) = f(values(i))
          transform(i + 1)
        }
      }
      transform(0)
    }

    def map[B](f: Double => B)(implicit t: scala.reflect.ClassTag[B]): Array[B] = {
      val b = {
        val build = Array.newBuilder[B]
        build.sizeHint(0)
        build.result()
      }
      for (i <- 0 until rowCount) b(i) = f(values(i))
      b
    }

    def mapComplete[B](f: Double => B)(implicit t: scala.reflect.ClassTag[B]): Array[B] = {
      val b = new Array[B](rowCount - missing.count.toInt)
      var pos = 0
      for (i <- 0 until rowCount) {
        if (!missing(i)) {
          b(pos) = f(values(i))
          pos += 1
        }
      }
      b
    }

    def fill(value: Double) = (0 until rowCount).foreach(i => values(i) = value)
  }

  abstract class Indexes {

    def apply(row: Int): Int

    def update(row: Int, value: Int): Unit

    def ++(value: Int): Unit
  }

  abstract class Labels {

    def apply(row: Int): String

    def update(row: Int, value: String): Unit

    def ++(value: String): Unit

    /**
     * Returns the term getDictionary used by the nominal values.
     * <p>
     * Term getDictionary contains all the nominal labels used by
     * observations and might contain also additional nominal labels.
     * Term getDictionary defines the domain of the definition for the nominal vector.
     * <p>
     * The term getDictionary contains nominal labels sorted in lexicografical order,
     * so binary search techniques may be used on this vector.
     * <p>
     * For other vector types like numerical ones this method returns nothing.
     *
     * @return term getDictionary defined by the nominal vector.
     */
    def dictionary: Array[String]

    def dictionary_=(dict: Array[String]): Unit

    def indexOf(label: String): Option[Int]

    def filter(p: (String) => Boolean): Array[String] = {

      @tailrec
      def filter(i: Int, list: List[String]): List[String] = {
        if (i >= rowCount) list
        else {
          if (p(labels(i))) filter(i + 1, list ::: List(labels(i)))
          else filter(i + 1, list)
        }
      }
      filter(0, List.empty).toArray
    }

    def forall(p: (String) => Boolean): Boolean = {
      @tailrec
      def forOne(i: Int): Boolean = {
        if (i >= rowCount) true
        else if (p(labels(i))) forOne(i + 1)
        else false
      }
      forOne(0)
    }

    def foreach[U](f: String => U) {
      for (i <- 0 until rowCount) {
        f(source.labels(rowId(i)))
      }
    }

    def transform(f: (String) => String): Unit = {
      def transform(i: Int) {
        if (i >= rowCount) Unit
        else {
          labels(i) = f(labels(i))
          transform(i + 1)
        }
      }
      transform(0)
    }

    def fill(label: String) = (0 until rowCount).foreach(i => labels(i) = label)
  }

  def apply(f: (Feature, Int) => Boolean): MappedFeature = {
    val m = new Mapping
    if (isMappedFeature) {
      mapping.foreach(row => {
        if (f(this, row)) m.add(rowId(row))
      })
    } else {
      for (row <- 0 until rowCount) {
        if (f(this, row)) m.add(rowId(row))
      }
    }
    new MappedFeature(source, m)
  }

  def solidCopy: Feature

  override def toString: String = {
    "Vector{ size='" + rowCount + "\'}"
  }
}



