package config.xsd;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class Workflow {

	LinkedList<Task> tasks = new LinkedList<Task>();

	public boolean contains(String exe) {
		for (Task task : tasks) {
			if (task.exeName.equals(exe)) {
				return true;
			}
		}
		return false;
	}

	public void load(String filename) {
		tasks.clear();
		File xmlFile = new File(filename);

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

		DocumentBuilder dBuilder;
		try {
			dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();

			NodeList taskList = doc.getElementsByTagName("task");
			for (int temp = 0; temp < taskList.getLength(); temp++) {
				Node node = taskList.item(temp);
				NamedNodeMap attsMap = node.getAttributes();
				Node execNode = attsMap.getNamedItem("exec");
				Node typeNode = attsMap.getNamedItem("type");
				// String exec = nameNode.getNodeValue();
				// if (name.equals("exec"))
				{
					tasks.add(new Task(execNode.getNodeValue(), typeNode
							.getNodeValue()));
					System.out.println(execNode.getNodeValue() + " "
							+ typeNode.getNodeValue());
					NodeList argsList = node.getChildNodes();
					for (int i = 0; i < argsList.getLength(); i++) {
						Node argNode = argsList.item(i);
						NamedNodeMap argAttsMap = node.getAttributes();
						Node argNameNode = argAttsMap.getNamedItem("name");
						System.out.println(argNameNode);
					}
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void save(String filename) {
		File file = new File(filename);
		StringBuffer xmlFile = new StringBuffer();
		xmlFile.append("<task_sequence>");
		for (Task task : tasks) {
			xmlFile.append(Task.getTaskStr(task.exeName, task.type));
		}

		xmlFile.append("</task_sequence>");
		System.out.println(xmlFile);

		try {
			FileWriter fstream = new FileWriter(file);
			BufferedWriter fout = new BufferedWriter(fstream);
			fout.write(xmlFile.toString());

			fout.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
	}

	private boolean isValid(List<String> inputFiles) {
		if (tasks.isEmpty() || inputFiles.isEmpty()) {
			return false;
		}
		boolean binarization = false, layout = false, paging = false, ocr = false, hierarchy = false;
		for (Task task : tasks) {
			if (task.hasType("binarization")) {
				binarization = true;
			} else if (task.hasType("layout")) {
				layout = true;
			} else if (task.hasType("ocr")) {
				ocr = true;
			} else if (task.hasType("paging")) {
				paging = true;
			} else if (task.hasType("hierarchy")) {
				hierarchy = true;
			}
		}
		if (!tasks.get(0).hasType("binarization")
				&& !tasks.get(0).hasType("layout")) {
			return false;
		}

		if ((paging && !layout) || (ocr && !layout)
				|| (!ocr && hierarchy)) {
			return false;
		}
		return true;
	}

	public void execute(List<String> inputFiles) {

		if (!isValid(inputFiles)) {
			Utility.showError("Secventa nu este valida!");
			return;
		}
		List<String> initialImages = new ArrayList<String>();
		List<String> images = new ArrayList<String>();
		List<String> toDelete = new ArrayList<String>();
		toDelete.add(Constants.TMP_HIERARCHY_FILE);
		toDelete.add(Constants.OCR_PARAM_FILE);
		toDelete.add(Constants.PARAM_FILE);
		toDelete.add(Constants.OCR_OUTPUT_FILE + ".txt");

		for (String image : inputFiles) {
			initialImages.add(image);
			images.add(image);
		}

		// initialImages.add("execs/pic1.tif");
		// initialImages.add("execs/pic2.tif");
		// images.add("execs/pic1.tif");
		// images.add("execs/pic2.tif");
		// images.add("execs/pic2.tif");
		// images.add("execs/pic3.tif");
		// images.add("execs/pic4.tif");

		for (Task task : tasks) {
			if (!task.hasType("hierarchy")) {
				for (int i = 0; i < images.size(); ++i) {
					System.out.println(images.get(i));
					if (!task.hasType(tasks.get(0).type)) {
						toDelete.add(images.get(i));
					}

					String outputFile = initialImages.get(i) + "." + task.type;

					int ret = 1;
					if (!task.hasType("ocr")) {
						ret = task.execute(images.get(i), outputFile);
						{
							images.set(i, outputFile);
						}
					} else if (task.hasType("ocr")) {
						LayoutTask layoutTask = new LayoutTask(task.exeName,
								initialImages.get(i), images.get(i));
						ret = 0;
					}
					if (ret != 0) {
						Utility.showError(task.exeName + " failed!");
						cleanTmpFiles(toDelete);
						return;
					}
				}
			} else {

				try {
					// Create file
					FileWriter fstream = new FileWriter(
							Constants.TMP_HIERARCHY_FILE);
					BufferedWriter fout = new BufferedWriter(fstream);
					StringBuffer xmlFile = new StringBuffer("<task>");
					for (int i = 0; i < images.size(); ++i) {
						xmlFile.append("<inputFile name=\""
								+ initialImages.get(i) + ".ocr" + "\" />");
					}
					xmlFile.append("<outputFile name = \""
							+ Constants.HIERARCHY_FILE + "\" /></task>");
					fout.write(xmlFile.toString());

					fout.close();
				} catch (Exception e) {
					System.err.println("Error: " + e.getMessage());
				}

				int ret = task.runExe(task.exeName,
						Constants.TMP_HIERARCHY_FILE);
				if (ret != 0) {
					Utility.showError(task.exeName + " failed!");
					cleanTmpFiles(toDelete);
					return;
				}
			}
		}

		cleanTmpFiles(toDelete);
		Utility.showMessage("Succes", "Secventa a fost executata cu succes!");
	}

	public void cleanTmpFiles(List<String> toDeleteList) {

		for (String file : toDeleteList) {
			System.out.println(file);
			System.out.println("" + Utility.deleteFile(file));
		}
	}

	public int getNumTasks() {
		return tasks.size();
	}

}
