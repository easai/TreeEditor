package com.github.easai.utils.TreeEditor;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TreeXML {

	Document doc;

	public void readXML(String xmlFile) {
		try {
			File inputFile = new File(xmlFile);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();
			parseXMLTree(doc.getDocumentElement(), 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseXMLTree(Node node, int level) {
		if (node.getNodeName().equals("value")) {
			for (int i = 0; i < level - 1; i++) {
				System.out.print("-");
			}
			System.out.println(node.getTextContent());
		} else {

			NodeList nodeList = node.getChildNodes();
			for (int i = 0; i < nodeList.getLength(); i++) {
				parseXMLTree(nodeList.item(i), level + 1);
			}
		}
	}

	public void writeXML(String xmlFile) {
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(xmlFile));
			transformer.transform(source, result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		TreeXML treeXML = new TreeXML();
		treeXML.readXML("a.xml");
		treeXML.writeXML("b.xml");
	}
}
