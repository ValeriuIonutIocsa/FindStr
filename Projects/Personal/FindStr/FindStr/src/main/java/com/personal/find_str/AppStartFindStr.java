package com.personal.find_str;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Stream;

final class AppStartFindStr {

	private AppStartFindStr() {
	}

	public static void main(
			final String[] args) {

		if (args.length < 2) {

			System.err.println("ERROR - insufficient arguments" +
					System.lineSeparator() + System.lineSeparator() +
					"usage: find_str STRING_TO_FIND FILE_NAME_PATTERN");
			System.exit(-1);
		}

		final Path rootPath = Paths.get("");
		final String stringToFind = args[0];
		final String fileNamePatternString = args[1];

		main(rootPath, stringToFind, fileNamePatternString);
	}

	static void main(
			final Path rootPath,
			final String stringToFind,
			final String fileNamePatternString) {

		try {
			final Pattern fileNamePattern = Pattern.compile(fileNamePatternString);

			final List<FileStringOccurrences> fileStringOccurrencesList =
					Collections.synchronizedList(new ArrayList<>());

			final List<Runnable> runnableList = new ArrayList<>();
			try (final Stream<Path> pathStream = Files.list(rootPath)) {

				pathStream.forEach(filePath -> runnableList.add(() -> searchInFile(
						stringToFind, fileNamePattern, filePath, rootPath, fileStringOccurrencesList)));
			}

			final ExecutorService executorService = Executors.newFixedThreadPool(12);
			for (final Runnable runnable : runnableList) {
				executorService.execute(runnable);
			}
			executorService.shutdown();
			final boolean success = executorService.awaitTermination(10, TimeUnit.SECONDS);
			if (!success) {
				System.err.println("ERROR - failed to terminate all threads");
			}

			for (int i = 0; i < fileStringOccurrencesList.size(); i++) {

				final FileStringOccurrences fileStringOccurrences = fileStringOccurrencesList.get(i);
				fileStringOccurrences.print(i);
			}

		} catch (final Throwable thr) {
			thr.printStackTrace();
		}
	}

	private static void searchInFile(
			final String stringToFind,
			final Pattern fileNamePattern,
			final Path filePath,
			final Path rootPath,
			final List<FileStringOccurrences> fileStringOccurrencesList) {

		final Path relativeFilePath = rootPath.relativize(filePath);
		final String relativeFilePathString = relativeFilePath.toString();
		if (fileNamePattern.matcher(relativeFilePathString).matches()) {

			int occurrenceCount = 0;
			try (final BufferedReader bufferedReader = Files.newBufferedReader(filePath)) {

				String line;
				while ((line = bufferedReader.readLine()) != null) {

					occurrenceCount += countOccurrences(line, stringToFind);
				}

			} catch (final Throwable thr) {
				System.err.println("ERROR - error occurred while reading file:" +
						System.lineSeparator() + filePath);
				thr.printStackTrace();
			}

			if (occurrenceCount > 0) {

				final FileStringOccurrences fileStringOccurrences =
						new FileStringOccurrences(relativeFilePath, occurrenceCount);
				fileStringOccurrencesList.add(fileStringOccurrences);
			}
		}
	}

	private static int countOccurrences(
			final String str,
			final String findStr) {

		int count = 0;
		int lastIndex = 0;
		while (lastIndex != -1) {

			lastIndex = str.indexOf(findStr, lastIndex);
			if (lastIndex != -1) {

				count++;
				lastIndex += findStr.length();
			}
		}
		return count;
	}
}
