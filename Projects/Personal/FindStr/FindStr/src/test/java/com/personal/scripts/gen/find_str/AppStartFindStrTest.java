package com.personal.scripts.gen.find_str;

import org.junit.jupiter.api.Test;

class AppStartFindStrTest {

	@Test
	void testWork() {

		final String rootPathString;
		final String filePathPatternString;
		final String stringToFind;
		final String stringToReplace;
		final int input = Integer.parseInt("21");
		if (input == 1) {

			rootPathString = "D:\\casdev\\td5\\da\\mdc\\000\\DAMDC_0U0_000\\" +
					"_FS_DAMDC_0U0_NORMAL\\out\\T1Instrumentation\\Results";
			filePathPatternString = ".*\\\\RuntimeMeasurementResults\\.xml";
			stringToFind = "<RunnableMeasurementResult ";
			stringToReplace = "<RunnableMeasurementResult  ";

		} else if (input == 11) {

			rootPathString = "C:\\IVI\\Prog\\JavaGradle\\Scripts\\General";
			filePathPatternString = ".*\\\\io-utils\\\\io-utils\\\\build.gradle";
			stringToFind = "";
			stringToReplace = "";

		} else if (input == 21) {

			rootPathString = "C:\\Users\\uid39522";
			filePathPatternString = ".*\\\\AllocCtrl.xlsx";
			stringToFind = "";
			stringToReplace = "";

		} else {
			throw new RuntimeException();
		}

		AppStartFindStr.work(rootPathString, filePathPatternString, stringToFind, stringToReplace);
	}
}
