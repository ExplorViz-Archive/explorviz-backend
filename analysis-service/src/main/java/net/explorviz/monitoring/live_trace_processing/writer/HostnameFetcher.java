package net.explorviz.monitoring.live_trace_processing.writer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class HostnameFetcher {

	public static String getHostname() {
		String result = null;
		try {
			final List<String> output = executeCommand("hostname");
			result = output.get(0).trim();
		} catch (final Exception e) {
		}
		return result;
	}

	private static List<String> executeCommand(final String command) throws Exception {
		final List<String> output = new ArrayList<String>();

		BufferedReader in = null;
		BufferedReader err = null;
		try {
			final Process process = Runtime.getRuntime().exec(command);
			in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			err = new BufferedReader(new InputStreamReader(process.getErrorStream()));

			final int exitCode = process.waitFor();
			if (exitCode != 0) {
				String error = "Command didn't exit cleanly! Command exited with code: " + exitCode
						+ "\n Command was: " + command + "\n";
				while (err.ready()) {
					error += err.readLine() + "\n";
				}
				throw new Exception(error);
			}
			while (in.ready()) {
				output.add(in.readLine());
			}
		} finally {
			closeOpenIO(in, err);
		}
		return output;
	}

	private static void closeOpenIO(final BufferedReader in, final BufferedReader err) {
		try {
			if ((in != null) && in.ready()) {
				in.close();
			}
			if ((err != null) && err.ready()) {
				err.close();
			}
		} catch (final IOException e) {
		}
	}
}
