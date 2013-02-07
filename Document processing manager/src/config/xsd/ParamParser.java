package config.xsd;
import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ParamParser {

	public static String getAdditionalParamsText(Node node) {
		String completeString = "";
		NodeList nodesList2 = node.getChildNodes();
		for (int temp2 = 0; temp2 < nodesList2.getLength(); temp2++) {
			Node node2 = nodesList2.item(temp2);
			completeString += ParamParser.getString(node2);
		}
		return completeString;
	}

	public static String getString(Node node) {
		try {
			TransformerFactory transFactory = TransformerFactory.newInstance();
			Transformer transformer = transFactory.newTransformer();
			StringWriter buffer = new StringWriter();
			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,
					"yes");
			transformer
					.transform(new DOMSource(node), new StreamResult(buffer));
			String str = buffer.toString();
			return str;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
