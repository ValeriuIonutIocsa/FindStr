package com.personal.scripts.gen.find_str;

import org.junit.jupiter.api.Test;

class AppStartFindStrTest {

	@Test
	void main() {

		final String rootPathString;
		final String stringToFind;
		final String filePathPatternString;
		final int input = Integer.parseInt("1");
		if (input == 1) {

			rootPathString = "D:\\casdev\\td5\\da\\mdc\\000\\DAMDC_0U0_000\\" +
					"_FS_DAMDC_0U0_NORMAL\\out\\T1Instrumentation\\Results";
			stringToFind = "<RunnableMeasurementResult ";
			filePathPatternString = ".*\\\\RuntimeMeasurementResults\\.xml";

		} else if (input == 11) {

			rootPathString = "C:\\IVI\\Prog\\JavaGradle\\Scripts\\General";
			stringToFind = "";
			filePathPatternString = ".*\\\\io-utils\\\\io-utils\\\\build.gradle";

		} else {
			throw new RuntimeException();
		}

		AppStartFindStr.main(rootPathString, stringToFind, filePathPatternString);
	}
}
