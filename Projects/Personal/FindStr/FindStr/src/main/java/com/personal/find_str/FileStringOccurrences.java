package com.personal.find_str;

import java.nio.file.Path;

class FileStringOccurrences {

	private final Path relativeFilePath;
	private final int occurrenceCount;

	FileStringOccurrences(
			final Path relativeFilePath,
			final int occurrenceCount) {

		this.relativeFilePath = relativeFilePath;
		this.occurrenceCount = occurrenceCount;
	}

	public void print(
            final int i) {

		System.out.println(i + ". " + relativeFilePath + "   " + occurrenceCount + " occurrences");
	}
}
