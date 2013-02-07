package config.xsd;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.w3c.dom.Node;

public class LayoutTask {

	public String execPath;

	public String inputPicturePath;

	public String layoutXmlPath;

	private String textDirection;

	public LayoutTask(String execPath, String inputPicturePath, String layoutXml) {
		this.execPath = execPath;
		this.inputPicturePath = inputPicturePath;
		this.layoutXmlPath = layoutXml;
		this.textDirection = getTextDirection();
		System.out.println(textDirection);
		try {

			SAXBuilder builder = new SAXBuilder();
			File xmlFile = new File(this.layoutXmlPath);

			Document doc = (Document) builder.build(xmlFile);
			Element rootNode = doc.getRootElement();
			run(rootNode);
			XMLOutputter xmlOutput = new XMLOutputter();
			xmlOutput.setFormat(Format.getPrettyFormat());
			FileWriter fw = new FileWriter(inputPicturePath + ".ocr");
			xmlOutput.output(doc, fw);
			fw.close();
			System.out.println("File updated!");
		} catch (IOException io) {
			io.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		} finally {

		}
	}

	private void executeOCR(Element textLine) {
		textLine.removeChild("String");
		int top = Integer.parseInt(textLine.getAttributeValue("top"));
		int right = Integer.parseInt(textLine.getAttributeValue("right"));
		int bottom = Integer.parseInt(textLine.getAttributeValue("bottom"));
		int left = Integer.parseInt(textLine.getAttributeValue("left"));
		String ocr = Task.getOCR(execPath, inputPicturePath, textDirection,
				top, right, bottom, left);
		ocr = ocr.trim();
		String words[] = ocr.split(" ");
		for (String word : words) {
			Element string = new Element("String").setText(word);
			textLine.addContent(string);
		}
	}

	private void run(Element x) {

		if (x.getName().equals("TextLine")) {
			executeOCR(x);
			return;
		}

		List<Element> list = x.getChildren();
		for (Element e : list) {
			run(e);
		}
	}

	private String getTextDirection() {
		try {
			File fXmlFile = new File(layoutXmlPath);
			javax.xml.parsers.DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();

			javax.xml.parsers.DocumentBuilder dBuilder = dbFactory
					.newDocumentBuilder();
			org.w3c.dom.Document doc = dBuilder.parse(fXmlFile);
			Node root = doc.getDocumentElement();
			root.normalize();
			String direction = root.getAttributes().getNamedItem("direction")
					.getNodeValue();
			return direction;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
