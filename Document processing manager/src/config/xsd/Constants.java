package config.xsd;
public class Constants {

	public static String TMP_WORKFLOW_FILE 	= "lastWorkflowFile.xml";
	public static String TMP_HIERARCHY_FILE = "tmpHierarchyFile.xml";
	public static String HIERARCHY_FILE 	= "hierarchy.xml";
	public final static String PARAM_FILE = "tmp.xml";
	public final static String OCR_PARAM_FILE = "ocrTmp.xml";
	public final static String OCR_OUTPUT_FILE = "wrapper_output.xml";
	
	public static final String EXECS_PATH = "execs\\";
	public static final String XSD_PATH = EXECS_PATH + "xml_schemas\\";
	public static final String OCR_PATH = EXECS_PATH + "Tesseract-ocr\\";

	// TODO add all kind of steps
	public static final String[] steps = { "binarization", "layout", "paging", "ocr",
			"hierarchy", "pdf-exporter" };
}
