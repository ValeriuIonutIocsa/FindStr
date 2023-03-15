package com.personal.scripts.gen.find_str;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
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

		final Instant start = Instant.now();

		if (args.length >= 1 && "-help".equals(args[0])) {

			final String helpMessage = createHelpMessage();
			System.out.println(helpMessage);
			System.exit(0);
		}

		if (args.length < 3) {

			final String helpMessage = createHelpMessage();
			System.err.println("ERROR - insufficient arguments" +
					System.lineSeparator() + helpMessage);
			System.exit(-1);
		}

		final String rootPathString = args[0];
		final String stringToFind = args[1];
		final String filePathPatternString = args[2];

		main(rootPathString, stringToFind, filePathPatternString);

		final Duration executionTime = Duration.between(start, Instant.now());
		System.out.println("done; execution time: " + durationToString(executionTime));
	}

	private static String createHelpMessage() {

		return "usage: find_str <dir_to_search_in> <string_to_find> <file_path_pattern>";
	}

	static void main(
			final String rootPathString,
			final String stringToFind,
			final String filePathPatternString) {

		try {
			final Path rootPath = Paths.get(rootPathString).toAbsolutePath().normalize();
			System.out.println("path to search in:" + System.lineSeparator() + rootPath);

			System.out.println("string to find: " + stringToFind);

			final Pattern filePathPattern = Pattern.compile(filePathPatternString);
			System.out.println("file path pattern: " + filePathPatternString);

			final List<FileStringOccurrences> fileStringOccurrencesList =
					Collections.synchronizedList(new ArrayList<>());

			final List<Runnable> runnableList = new ArrayList<>();
			try (Stream<Path> filePathStream = Files.walk(rootPath)) {

				filePathStream.forEach(filePath -> runnableList.add(() -> searchInFile(
						stringToFind, filePathPattern, filePath, fileStringOccurrencesList)));
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
			final Pattern filePathPattern,
			final Path filePath,
			final List<FileStringOccurrences> fileStringOccurrencesList) {

		if (Files.isRegularFile(filePath)) {

			final String filePathString = filePath.toString();
			if (filePathPattern.matcher(filePathString).matches()) {

				if (stringToFind.isEmpty()) {

					final FileStringOccurrences fileStringOccurrences =
							new FileStringOccurrences(filePath, 1);
					fileStringOccurrencesList.add(fileStringOccurrences);

				} else {
					int occurrenceCount = 0;
					try (BufferedReader bufferedReader = Files.newBufferedReader(filePath)) {

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
								new FileStringOccurrences(filePath, occurrenceCount);
						fileStringOccurrencesList.add(fileStringOccurrences);
					}
				}
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

	private static String durationToString(
			final Duration duration) {

		final StringBuilder stringBuilder = new StringBuilder();
		final long allSeconds = duration.get(ChronoUnit.SECONDS);
		final long hours = allSeconds / 3600;
		if (hours > 0) {
			stringBuilder.append(hours).append("h ");
		}

		final long minutes = (allSeconds - hours * 3600) / 60;
		if (minutes > 0) {
			stringBuilder.append(minutes).append("m ");
		}

		final long nanoseconds = duration.get(ChronoUnit.NANOS);
		final double seconds = allSeconds - hours * 3600 - minutes * 60 +
				nanoseconds / 1_000_000_000.0;
		stringBuilder.append(doubleToString(seconds)).append('s');

		return stringBuilder.toString();
	}

	private static String doubleToString(
			final double d) {

		final String str;
		if (Double.isNaN(d)) {
			str = "";

		} else {
			final String format;
			format = "0.000";
			final DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.US);
			final DecimalFormat decimalFormat = new DecimalFormat(format, decimalFormatSymbols);
			str = decimalFormat.format(d);
		}
		return str;
	}
}
