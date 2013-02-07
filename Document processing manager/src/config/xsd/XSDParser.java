package config.xsd;
import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XSDParser {

	public static String getXSDStep(File fXmlFile) {

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();

			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			NodeList nodesList = doc.getElementsByTagName("xs:simpleType");
			for (int temp = 0; temp < nodesList.getLength(); temp++) {
				Node node = nodesList.item(temp);
				NamedNodeMap attsMap = node.getAttributes();
				Node nameNode = attsMap.getNamedItem("name");
				String name = nameNode.getNodeValue();
				if (name.equals("execType"))
					return processExecType(node);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String processExecType(Node node) {
		NodeList nodesList = node.getChildNodes();
		for (int temp = 0; temp < nodesList.getLength(); temp++) {
			Node current = nodesList.item(temp);
			if (current.getNodeName().equals("xs:restriction")) {
				NodeList nodesList2 = current.getChildNodes();
				for (int i = 0; i < nodesList2.getLength(); i++) {
					Node current2 = nodesList2.item(i);

					if (current2.getNodeName().equals("xs:pattern")) {
						String value = current2.getAttributes()
								.getNamedItem("value").getNodeValue();

						return value;
					}
				}
			}
		}
		return null;
	}

	public static String getXSDExecPath(File fXmlFile) {

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();

			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			NodeList nodesList = doc.getElementsByTagName("xs:simpleType");
			for (int temp = 0; temp < nodesList.getLength(); temp++) {
				Node node = nodesList.item(temp);
				NamedNodeMap attsMap = node.getAttributes();
				Node nameNode = attsMap.getNamedItem("name");
				String name = nameNode.getNodeValue();
				if (name.equals("execName"))
					return processExecName(node);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String processExecName(Node node) {
		NodeList nodesList = node.getChildNodes();
		for (int temp = 0; temp < nodesList.getLength(); temp++) {
			Node current = nodesList.item(temp);
			if (current.getNodeName().equals("xs:restriction")) {
				NodeList nodesList2 = current.getChildNodes();
				for (int i = 0; i < nodesList2.getLength(); i++) {
					Node current2 = nodesList2.item(i);

					if (current2.getNodeName().equals("xs:pattern")) {
						String value = current2.getAttributes()
								.getNamedItem("value").getNodeValue();

						return value;
					}
				}
			}
		}
		return null;
	}
	
	public static Executable getExecutable(File fXmlFile) {
		String type = getXSDStep(fXmlFile);
		String execPath = getXSDExecPath(fXmlFile);
		return new Executable(type, execPath);
	}
}
