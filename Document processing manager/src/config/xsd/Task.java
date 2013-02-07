package config.xsd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

public class Task {

	String exeName;
	String type;

	public Task(String exe, String tip) {
		exeName = new String(exe);
		type = new String(tip);

	}

	boolean hasType(String execType) {
		return type.equalsIgnoreCase(execType);
	}

	public static String getTaskStr(String execPath, String type) {
		return "<task exec=\"" + execPath + "\" type=\"" + type + "\"></task>";
	}

	public static String getOCR(String execPath, String in, String direction,
			int top, int right, int bottom, int left) {
		try {
			// Create file
			FileWriter fstream = new FileWriter(Constants.OCR_PARAM_FILE);
			BufferedWriter fout = new BufferedWriter(fstream);
			fout.write("<task><inputFile name=\"" + in + "\" />"
					+ "<outputFile name=\"" + Constants.OCR_OUTPUT_FILE
					+ "\" />" + "<processRectangle direction=\"" + "descending"
					+ "\" bottom=\"" + bottom + "\" left=\"" + left
					+ "\" right=\"" + right + "\" top=\"" + top
					+ "\" /></task>");

			// Close the output stream
			fout.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}

		ProcessBuilder builder = new ProcessBuilder(execPath,
				Constants.OCR_PARAM_FILE);
		String ocrPath = new File(".").getAbsolutePath() + "\\"
				+ Constants.OCR_PATH;
		System.out.println(ocrPath);
		builder.environment()
				.put("TESSDATA_PREFIX",// ocrPath);
						"C:\\Users\\ilarele\\Desktop\\mps\\mps\\proj\\Document processing manager\\execs\\Tesseract-ocr\\");
		// "C:\\Users\\Bogdan\\Desktop\\mps\\proj\\Document processing manager\\execs\\Tesseract-ocr\\");

		try {
			Process notepad = builder.start();
			notepad.waitFor();
			System.out.println("Notepad returned: " + notepad.exitValue());

			if (notepad.exitValue() != 0) {
				System.out.println("CRASH!!!");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		String decodedText = null;
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(new File(Constants.OCR_OUTPUT_FILE
					+ ".txt"));
			FileChannel fc = stream.getChannel();
			MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0,
					fc.size());

			decodedText = Charset.defaultCharset().decode(bb).toString();

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println(decodedText);
		return decodedText;
	}

	public int execute(String in, String out) {
		// File x = new File

		if (!hasType("ocr")) {
			try {
				// Create file
				FileWriter fstream = new FileWriter(Constants.PARAM_FILE);
				BufferedWriter fout = new BufferedWriter(fstream);
				fout.write("<task><inputFile name=\"" + in
						+ "\"/><outputFile name=\"" + out + "\"/></task>");
				// Close the output stream
				fout.close();
			} catch (Exception e) {// Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}
		}
		return runExe(exeName, Constants.PARAM_FILE);
	}

	public int runExe(String exeName, String filename) {
		System.out.println(exeName);

		ProcessBuilder builder = new ProcessBuilder(exeName, filename);
		try {
			Process notepad = builder.start();
			notepad.waitFor();
			int ret = notepad.exitValue();
			notepad.destroy();
			return ret;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return 1;
	}
}
