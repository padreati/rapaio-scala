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

package rapaio.ml

import rapaio.data._
import rapaio.printer.Printable
import rapaio.core.RandomSource

/**
 * Trait for all classifiers. For now the number of target features is 1.
 *
 * @author <a href="email:padreati@yahoo.com>Aurelian Tutuianu</a>
 */
trait Classifier extends Printable {

  protected var _prediction: Nominal = _
  protected var _distribution: Frame = _

  protected var _target: String = _
  protected var _dictionary: Array[String] = _
  protected var _rowCount: Int = _

  /**
   * Name of the classification algorithm used for informative messages
   * @return short name of the implemented classifier
   */
  def name: String

  /**
   * Algorithm name with the eventual parameter values used.
   * @return algorithm name and parameter values
   */
  def description: String

  /**
   * Creates a new classifier instance with the same parameters as the original.
   * The fitted model and other artifacts are not replicated.
   *
   * @return new parametrized instance
   */
  def newInstance(): Classifier

  protected def collectLearningInfo(df: Frame, weights: Value, targetName: String): Unit = {
    _rowCount = df.rowCount
    _dictionary = df.col(targetName).labels.dictionary
    _target = targetName
  }

  /**
   * Fit a classifier on instances specified by frame, with row weights
   * and target as classColName.
   *
   * @param df           data set instances
   * @param weights      row weights
   * @param targetName target column name
   */
  def learn(df: Frame, weights: Value, targetName: String)

  /**
   * Fit a classifier on instances specified by frame, with row weights
   * equal to 1 and target as classColName.
   *
   * @param df           data set instances
   * @param targetName target column name
   */
  def learn(df: Frame, targetName: String) {
    learn(df, new Value(df.rowCount, df.rowCount, 1.0), targetName)
  }

  /**
   * Builds a new classifier using artifacts from a previous classifier.
   *
   * @param df           data set instances
   * @param weights      row weights
   * @param targetName target column name
   * @param classifier   previous classifier
   */
  def learnFurther(df: Frame, weights: Value, targetName: String, classifier: Classifier) {
    sys.error(f"learnFurther not implemented for ${name}")
  }

  /**
   * Builds a new classifier using artifacts from a previous classifier.
   *
   * @param df           data set instances
   * @param targetName target column name
   * @param classifier   previous classifier
   */
  def learnFurther(df: Frame, targetName: String, classifier: Classifier) {
    learnFurther(df, new Value(df.rowCount, df.rowCount, 1.0), targetName, classifier)
  }

  /**
   * Predicts classes for new data set instances
   *
   * @param df data set instances
   */
  def predict(df: Frame): Unit

  /**
   * Predict further classes for new data set instances, using
   * as much as possible fitted artifacts from previous classifier.
   * <p/>
   * The frame df is supposed to be the same, otherwise
   * the result is unpredictable
   *
   * @param df data set instances
   */
  def predictFurther(df: Frame, classifier: Classifier) {
    sys.error(f"predictFurther not implemented for ${name}")
  }

  /**
   * Returns predicted classes
   *
   * @return nominal vector with predicted classes
   */
  def prediction: Nominal = _prediction

  /**
   * Returns predicted class distribution.
   *
   * @return predicted class distribution (frame with one
   *         column for each target class, including the missing
   *         label column in first position)
   */
  def distribution: Frame = _distribution

  override def buildSummary(sb: StringBuilder): Unit = {
    sb.append("\n>> classification model summary\n")
    sb.append("* model: " + name + "\n")

    sb.append("* parameters:");
    buildParameterSummary(sb)

    sb.append("* structure:")
    buildModelSummary(sb)

    if (_prediction != null) {
      sb.append("* prediction:")


    }
  }

  def buildParameterSummary(sb: StringBuilder): Unit = sb.append("none\n")

  def buildModelSummary(sb: StringBuilder): Unit = sb.append("no information\n")

  /**
   * Build [[_prediction]] from the previously computed [[_distribution]]
   * and specified [[_dictionary]].
   * It is mainly used by various classifier as the last step for prediction,
   * before the classifier selected [[_dictionary]] and computed [[_distribution]]
   */
  protected def buildPrediction(): Unit = {
    _prediction = new Nominal(_distribution.rowCount, _dictionary)

    def modeNext(row: Int, i: Int, n: Double, max: Double, lastLabel: String): String = {
      if (i >= _dictionary.length) lastLabel
      else if (_distribution.values(row, i) < max) modeNext(row, i + 1, n, max, lastLabel)
      else if (_distribution.values(row, i) == max)
        if (RandomSource.nextDouble >= n / (n + 1)) modeNext(row, i + 1, 1, max, _dictionary(i))
        else modeNext(row, i + 1, n + 1, max, lastLabel)
      else modeNext(row, i + 1, 1, _distribution.values(row, i), _dictionary(i))
    }
    (0 until _distribution.rowCount).foreach(row => {
      _prediction.labels(row) = modeNext(row, 0, 1, -1, "")
    })
  }
}