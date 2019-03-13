package utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import database.Database;
import database.Relation;
import database.Schema;
import database.Header;
import database.Tuple;
import database.Attribute;

// Format
	/*<Database>
	 *	<Relation name = "">
	 *
	 *	  <Schema>
	 *		<Header name = "" type = "" max = "" />
	 *		<Header name = "" type = "" max = "" />
	 *		...
	 *	  </Schema>
	 *	  <Tuple>
	 *		<Attribute type = "" value = "" />
	 *		<Attribute type = "" value = "" />
	 *		...
	 *
	 *	  </Tuple>
	 *	  ...
	 * 	</Relation>
	 * </Database>
	 */

public class XMLParser {
	
	// ---------------------------------------- Import ---------------------------------------- //

	// Create Database from XML file
	public static Database importDatabase(String path) {
		
		Database database = new Database();
		
		Document doc = getDoc(path);
		
		Element eDatabase = (Element)doc.getElementsByTagName("Database").item(0);
		
		NodeList nRelations = eDatabase.getElementsByTagName("Relation");
		
		for (int i = 0; i < nRelations.getLength(); i++) {
			Element eRelation = (Element)nRelations.item(i);
			
			database.addRelation(parseRelation(eRelation));
		}
 
		
		return database;
	}
	
	private static Relation parseRelation(Element eRelation) {
		String name = eRelation.getAttribute("name");
		
		// Parse Schema
		Element eSchema = (Element)eRelation.getElementsByTagName("Schema").item(0);
		Schema schema = parseSchema(eSchema);
		
		// Parse Tuples	
		NodeList nTuples = eRelation.getElementsByTagName("Tuple");
		LinkedList<Tuple> tuples = new LinkedList<Tuple>();
		
		for (int i = 0; i < nTuples.getLength(); i++) {
			Element eTuple = (Element)nTuples.item(i);
			
			tuples.add(parseTuple(eTuple));
		}
		
		return new Relation(name, schema, tuples);
		
	}
	
	private static Schema parseSchema(Element eSchema) {
		NodeList nHeaders = eSchema.getElementsByTagName("Header");
		
		LinkedList<Header> headers = new LinkedList<Header>();
		
		for (int i = 0; i < nHeaders.getLength(); i++) {
			Element eHeader = (Element)nHeaders.item(i);
			
			headers.add(parseHeader(eHeader));
		}
		
		return new Schema(headers);
	}
	
	private static Header parseHeader(Element eHeader) {
		String name = eHeader.getAttribute("name");
		String type = eHeader.getAttribute("type");
		int max = Integer.parseInt(eHeader.getAttribute("max"));
		
		return new Header(name, type, max);
	}
	
	private static Tuple parseTuple(Element eTuple) {
		NodeList nAttrs = eTuple.getElementsByTagName("Attribute");
		
		LinkedList<Attribute> attrs = new LinkedList<Attribute>();
		
		for (int i = 0; i < nAttrs.getLength(); i++) {
			Element nAttr = (Element)nAttrs.item(i);
			
			attrs.add(parseAttribute(nAttr));
		}
		
		return new Tuple(attrs);
		
	}
	
	private static Attribute parseAttribute(Element eAttr) {
		String type = eAttr.getAttribute("type");
		String value = eAttr.getAttribute("value");
		
		return new Attribute(type, value);
	}
	
	private static Document getDoc(String path) {
		Document doc = null;
		try {
			File fXmlFile = new File(path);
		
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			
			doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
		} catch (Exception e) {
    		e.printStackTrace();
    	}
		return doc;
	}


	// ---------------------------------------- Export ---------------------------------------- //
	public static void exportDatabase(Database db, String path) {
		Document doc = null;
		
		// Create Document
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder build = factory.newDocumentBuilder();
			doc = build.newDocument();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// XML-ify Database
		Element eDatabase = doc.createElement("Database");
		
		for (Relation relation : db.getRelations()) {
			Element eRelation = createRelationElement(relation, doc);
			eDatabase.appendChild(eRelation);
		}
		
		// Export
		doc.appendChild(eDatabase);
		
		try {
			Transformer trans = TransformerFactory.newInstance().newTransformer();
			
			trans.setOutputProperty(OutputKeys.INDENT,   "yes");
			trans.setOutputProperty(OutputKeys.METHOD,   "xml");
			trans.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			
			trans.transform(new DOMSource(doc), new StreamResult(new FileOutputStream(path)));			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	private static Element createRelationElement(Relation relation, Document doc) {
		// Create Relation Element
		Element eRelation = doc.createElement("Relation");
		
		// Set Name
		eRelation.setAttribute("name", relation.getName());
		
		// Create Schema Element
		Element eSchema = createSchemaElement(relation.getSchema(), doc);
		
		// Append Schema
		eRelation.appendChild(eSchema);
		
		// Create Tuple Elements & Append
		for (Tuple tuple : relation.getTuples()) {
			Element eTuple = createTupleElement(tuple, doc);
			
			eRelation.appendChild(eTuple);
		}
		
		return eRelation;
	}
	
	private static Element createSchemaElement(Schema schema, Document doc) {
		// Create Schema Element
		Element eSchema = doc.createElement("Schema");
		
		// Create & Append Headers
		for (Header header : schema.getHeaders()) {
			Element eHeader = createHeaderElement(header, doc);
			
			eSchema.appendChild(eHeader);
		}
		
		return eSchema;
	}
	
	private static Element createHeaderElement(Header header, Document doc) {
		// Create Header Element
		Element eHeader = doc.createElement("Header");
		
		// Add Attributes
		eHeader.setAttribute("name", header.getName());
		eHeader.setAttribute("type", header.getType());
		eHeader.setAttribute("max", String.valueOf(header.getMax()));
		
		return eHeader;
	}
	
	private static Element createTupleElement(Tuple tuple, Document doc) {
		// Create Tuple Element
		Element eTuple = doc.createElement("Tuple");
		
		// Create & Append Attributes
		for (Attribute attr : tuple.getAttributes()) {
			Element eAttr = createAttrElement(attr, doc);
			
			eTuple.appendChild(eAttr);
		}
		
		return eTuple;
	}
	
	private static Element createAttrElement(Attribute attr, Document doc) {
		// Create Attribute Element
		Element eAttr = doc.createElement("Attribute");
		
		// Add Attributes
		eAttr.setAttribute("type", attr.getType());
		eAttr.setAttribute("value", attr.getValue());
	
		return eAttr;
	}
}
