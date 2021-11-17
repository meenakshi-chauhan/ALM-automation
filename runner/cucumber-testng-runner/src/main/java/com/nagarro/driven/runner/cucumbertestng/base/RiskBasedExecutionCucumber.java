package com.nagarro.driven.runner.cucumbertestng.base;

import com.nagarro.driven.runner.testng.base.RiskBasedExecutionTestng;
import com.nagarro.driven.runner.testng.base.config.ExecutionConfig;
import com.nagarro.driven.runner.testng.base.model.TestCaseDetails;
import java.util.List;

public class RiskBasedExecutionCucumber {

  private static final boolean SMART_EXECUTION_FLAG =
      ExecutionConfig.getInstance().smartExecutionStatus();
  private final List<TestCaseDetails> ratio;

  /** Constructor to get the fail ratio data of features. */
  public RiskBasedExecutionCucumber() {
    ratio = new RiskBasedExecutionTestng().getRatio();
  }

  /**
   * This method arranges the features according to their fail ratio
   *
   * @return arrangedFeatures
   */
  public Object[][] arrangeFeatures(Object[][] features) {
    if (SMART_EXECUTION_FLAG) {
      // Cloning of features into arrangedFeatures
      Object[][] arrangedFeatures = features.clone();
      for (int i = 0; i < features.length; ++i) {
        arrangedFeatures[i] = features[i].clone();
      }
      int iterator3 = 0;

      for (int iterator = 0; iterator < ratio.size(); iterator++) {
        for (Object[] feature : features) {
          if (ratio.get(0).getTestName().equals(feature[0].toString())) {
            arrangedFeatures[iterator3++][0] = feature[0];
            break;
          }
        }
      }
      return arrangedFeatures;
    }
    return features;
  }
}
