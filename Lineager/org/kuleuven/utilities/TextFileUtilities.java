package org.kuleuven.utilities;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class TextFileUtilities {

	public static List<String> loadFromFile(String filename) {
		List<String> result = new ArrayList<String>();
		try {
			FileInputStream file = new FileInputStream(filename);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file), 1000000);
			try {
				while (bufferedReader.ready()) {
					result.add(bufferedReader.readLine());
				}
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void saveToFileThrowsExceptioin(List<String> lines, String filename)
			throws Exception {
		FileOutputStream file = new FileOutputStream(filename);
		BufferedWriter bufferedWrite = new BufferedWriter(new OutputStreamWriter(file), 1000000);
		for (String line : lines) {
			bufferedWrite.write(line);
			bufferedWrite.newLine();
		}
		bufferedWrite.flush();
		bufferedWrite.close();
	}

	public static void saveToFile(List<String> lines, String filename) {
		try {
			FileOutputStream file = new FileOutputStream(filename);
			BufferedWriter bufferedWrite = new BufferedWriter(new OutputStreamWriter(file), 1000000);
			try {
				for (String line : lines) {
					bufferedWrite.write(line);
					bufferedWrite.newLine();
				}
				bufferedWrite.flush();
				bufferedWrite.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void saveToFile(String text, String filename) {
		try {
			FileOutputStream file = new FileOutputStream(filename);
			BufferedWriter bufferedWrite = new BufferedWriter(new OutputStreamWriter(file), 1000000);
			try {
				bufferedWrite.write(text);
				bufferedWrite.newLine();
				bufferedWrite.flush();
				bufferedWrite.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static void appendToFile(List<String> text, String fileName) {
		try {
			FileOutputStream file = new FileOutputStream(fileName, true);
			BufferedWriter bufferedWrite = new BufferedWriter(new OutputStreamWriter(file), 1000000);
			try {
				for (String line : text) {
					bufferedWrite.write(line);
					bufferedWrite.newLine();
				}
				bufferedWrite.flush();
				bufferedWrite.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}

	public static void appendToFile(String text, String fileName) {
		try {
			FileOutputStream file = new FileOutputStream(fileName, true);
			BufferedWriter bufferedWrite = new BufferedWriter(new OutputStreamWriter(file), 1000000);
			try {
				bufferedWrite.write(text);
				bufferedWrite.newLine();
				bufferedWrite.flush();
				bufferedWrite.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}
}
