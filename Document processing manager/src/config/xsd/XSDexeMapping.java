package config.xsd;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XSDexeMapping {

	private HashMap<String, ArrayList<Executable>> map;

	public void init() {
		map = new HashMap<String, ArrayList<Executable>>();

		for (String step : Constants.steps) {
			map.put(step, new ArrayList<Executable>());
		}
		File xsdDir = new File(Constants.XSD_PATH);
		File[] fileList = xsdDir.listFiles();

		for (File f : fileList) {
			if (f.getName().endsWith(".xsd")) {
				Executable exec = XSDParser.getExecutable(f);

				if (Utility
						.testPropertyExistsInList(exec.type, Constants.steps)) {
					if (Utility.checkFileExists(Constants.EXECS_PATH + exec.path))
						map.get(exec.type).add(exec);
					else
						System.out.println("Could not find exec associated: "
								+ exec);
				} else {
					System.out.println("File: " + f.getName() + " of type "
							+ exec.type + " is not supported");
				}
			}
		}
	}

	public static void runTest() {
		try {
			String completeString = "";
			String uri = "src/test.xml";
			File fXmlFile = new File(uri);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();

			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			Node root = doc.getDocumentElement();
			root.normalize();

			NodeList nodesList = root.getChildNodes();
			for (int temp = 0; temp < nodesList.getLength(); temp++) {
				Node node = nodesList.item(temp);
				if (node.getNodeName().equals("taskAdditionalParams")) {
					completeString += ParamParser.getAdditionalParamsText(node);
				}
			}
			System.out.println(completeString);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		/*Thing t = new Thing();
		t.init();
		List<Executable> list = t.getOptionsForStep("layout");
		for (Executable e : list) {
			System.out.println(e);
		}
		runTest();*/
		runLayoutTest();
	}

	private static void runLayoutTest() {
		String layoutUri = "src/layout.xml";
		LayoutTask t = new LayoutTask("execPath", "pic1.jpeg", layoutUri);
		
	}

	public List<Executable> getOptionsForStep(String step) {
		return map.get(step);
	}

}
