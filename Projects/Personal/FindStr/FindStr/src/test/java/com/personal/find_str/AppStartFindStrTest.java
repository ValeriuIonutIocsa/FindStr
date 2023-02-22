package com.personal.find_str;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

class AppStartFindStrTest {

	@Test
	void main() {

		final Path rootPath =
				Paths.get("D:\\casdev\\td5\\da\\mdc\\000\\DAMDC_0U0_000\\" +
                        "_FS_DAMDC_0U0_NORMAL\\out\\T1Instrumentation\\Results");
		final String stringToFind = "<RunnableMeasurementResult ";
		final String fileNamePatternString = "RuntimeMeasurementResults\\.xml";
		AppStartFindStr.main(rootPath, stringToFind, fileNamePatternString);
	}
}
