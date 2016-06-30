package org.kuleuven.utilities;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

public class ProcessUtilities {
	static class StreamGobbler extends Thread {
		InputStream is;
		String type;

		StreamGobbler(InputStream is, String type) {
			this.is = is;
			this.type = type;
		}

		@Override
		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null) {
					System.out.println(type + ">" + line);
				}
				br.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	static class StreamGobbler2File extends Thread {
		InputStream is;
		String type;
		private String filename;
		BufferedWriter bufferedWrite;

		StreamGobbler2File(InputStream is, String filename, String type) {
			this.is = is;
			this.type = type;
			this.filename = filename;
		}

		@Override
		public void run() {
			try {
				FileOutputStream file = new FileOutputStream(filename, true);
				bufferedWrite = new BufferedWriter(new OutputStreamWriter(file), 1000000);
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null) {
					bufferedWrite.write(type + ">" + line + "\n");
				}
				bufferedWrite.close();
			} catch (IOException ioe) {

				ioe.printStackTrace();
			}
		}

		@Override
		protected void finalize() throws Throwable {
			bufferedWrite.close();
			super.finalize();
		}
	}

	public static void pauseOneSec() {
		try {
			Thread.currentThread();
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static int runProgramLoggingOutput(String command, int maxTime, String logpath,
			String errorFile) {
		return runProgramLoggingOutput(command, null, maxTime, logpath, errorFile);
	}

	public static int runProgramLoggingOutput(String[] commandarray, String[] envs, int maxTime,
			String logFile, String errorFile) {
		try {
			Runtime rt = Runtime.getRuntime();
			// System.out.println(commandarray);
			Process p;

			if (envs != null) {
				p = rt.exec(commandarray, envs);

			} else {
				p = rt.exec(commandarray);
			}

			StreamGobbler2File errorGobbler = new StreamGobbler2File(p.getErrorStream(), errorFile,
					"");
			StreamGobbler2File outputGobbler = new StreamGobbler2File(p.getInputStream(), logFile,
					"");
			errorGobbler.start();
			outputGobbler.start();

			int check = -1;
			int time = 0;
			do {
				try {
					pauseOneSec();
					time++;
					check = p.exitValue();

				} catch (Exception e) {

					check = -1;
				}
			} while (time < maxTime && check == -1);
			/*
			 * BufferedReader br = new BufferedReader(new InputStreamReader(p
			 * .getInputStream())); while (br.ready()) {
			 * System.out.println(br.readLine()); }
			 */
			if (check != 0) {
				p.destroy();
			}
			return check;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public static int runProgram(String command, int maxTime) {

		return runProgram(command, null, maxTime);
	}

	public static int runProgram(String[] commandarray, int maxTime) {
		return runProgram(commandarray, null, maxTime);
	}

	public static int runProgram(String[] commandarray, String[] environmentalVariables, int maxTime) {

		try {
			Runtime rt = Runtime.getRuntime();
			// System.out.println(commandarray);
			Process p;
			if (environmentalVariables != null) {
				p = rt.exec(commandarray, environmentalVariables);
			} else {
				p = rt.exec(commandarray);
			}

			StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "");
			StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT");
			errorGobbler.start();
			outputGobbler.start();

			int check = -1;
			int time = 0;
			do {
				try {
					pauseOneSec();
					time++;
					check = p.exitValue();

				} catch (Exception e) {

					check = -1;
				}
			} while (time < maxTime && check == -1);
			// BufferedReader br = new BufferedReader(new
			// InputStreamReader(p.getInputStream()));
			// while (br.ready()) {
			// System.out.println(br.readLine());
			// }
			if (check != 0) {
				p.destroy();
			}
			return check;
		} catch (Exception e) {
			return -1;
		}
	}

	public static int runProgramPushingStandardInput(String command,
			String[] environmentalVariables, int maxTime, File runningDirectory, String sourceFile) {

		try {
			Runtime rt = Runtime.getRuntime();
			// System.out.println(commandarray);
			Process p;
			p = rt.exec(command, environmentalVariables, runningDirectory);
			OutputStream os = p.getOutputStream();
			List<String> lines = TextFileUtilities.loadFromFile(sourceFile);
			BufferedWriter bufferedWrite = new BufferedWriter(new OutputStreamWriter(os), 1000000);

			for (String line : lines) {
				bufferedWrite.write(line);
				bufferedWrite.newLine();
			}
			bufferedWrite.flush();
			bufferedWrite.close();

			StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "");
			StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT");
			errorGobbler.start();
			outputGobbler.start();

			int check = -1;
			int time = 0;
			do {
				try {
					pauseOneSec();
					time++;
					check = p.exitValue();

				} catch (Exception e) {

					check = -1;
				}
			} while (time < maxTime && check == -1);
			// BufferedReader br = new BufferedReader(new
			// InputStreamReader(p.getInputStream()));
			// while (br.ready()) {
			// System.out.println(br.readLine());
			// }
			if (check != 0) {
				p.destroy();
			}
			return check;
		} catch (Exception e) {
			return -1;
		}

	}
public static String getDirectorySeparator(){
	return System.getProperty("file.separator");
}
	public static int runProgram(String command, String[] environmentalVariables, int maxTime) {

		String[] commandarray = command.split("\\s");
		return runProgram(commandarray, environmentalVariables, maxTime);
	}

	public static int runProgramLoggingOutput(String command, String[] envs, int maxTime,
			String logFile, String errorFile) {
		String[] commandarray = command.split("\\s");
		return runProgramLoggingOutput(commandarray, envs, maxTime, logFile, errorFile);
	}
}
