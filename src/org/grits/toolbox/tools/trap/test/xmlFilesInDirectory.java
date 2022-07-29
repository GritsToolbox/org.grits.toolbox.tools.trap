package org.grits.toolbox.tools.trap.test;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;

public class xmlFilesInDirectory {
	
	public static void main(String[] args) {
		
		String annotationFolder = "/Users/aravind/Desktop/annotation/";
		
		File folder = new File(annotationFolder);
		File[] listOfFiles = folder.listFiles();
		
		System.out.println("Starting write To Excel");
		
		CellStyle style;
		String sheetName = "PrecursorInfo";
		String sheetName2 = "PeptideInfo";
		
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet(sheetName);
		XSSFSheet sheet2 = wb.createSheet(sheetName2);
		
		int count = 1;
		
		style = createStyle(wb);
		
		for (int i = 0; i < listOfFiles.length; i++) {
			String filename = listOfFiles[i].getName();
			if (filename.endsWith(".xml") || filename.endsWith(".XML")) {
				// System.out.println("File Name = " + filename);
				
				try {
					// BufferedReader bf = new BufferedReader(new
					// InputStreamReader(System.in));
					
					// Create a factory
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					
					// Use the factory to create a builder
					DocumentBuilder builder = factory.newDocumentBuilder();
					
					Document doc = builder.parse(annotationFolder + filename);
					
					String StringPeptideId = doc.getElementsByTagName("peptideInfo").item(0).getAttributes()
					        .getNamedItem("id").getNodeValue();
					int peptideId = Integer.parseInt(StringPeptideId);
					
					String peptideSequence = doc.getElementsByTagName("peptideInfo").item(0).getAttributes()
					        .getNamedItem("sequence").getNodeValue();
					
					String stringPeptideMass = doc.getElementsByTagName("peptideInfo").item(0).getAttributes()
					        .getNamedItem("mass").getNodeValue();
					
					Double peptideMass = Double.parseDouble(stringPeptideMass);
					
					String stringAdductMass = doc.getElementsByTagName("Adduct").item(0).getAttributes()
					        .getNamedItem("mass").getNodeValue();
					
					Double adductMass = Double.parseDouble(stringAdductMass);
					
					String stringAdductCharge = doc.getElementsByTagName("Adduct").item(0).getAttributes()
					        .getNamedItem("charge").getNodeValue();
					
					String adductLabel = doc.getElementsByTagName("Adduct").item(0).getAttributes()
					        .getNamedItem("label").getNodeValue();
					
					String adductName = doc.getElementsByTagName("Adduct").item(0).getAttributes()
					        .getNamedItem("ionName").getNodeValue();
					
					int adductCharge = Integer.parseInt(stringAdductCharge);
					
					XSSFRow headingRowSheet2 = sheet2.createRow(0);
					
					XSSFCell headingCell0Sheet2 = headingRowSheet2.createCell(0);
					XSSFCell headingCell1Sheet2 = headingRowSheet2.createCell(1);
					XSSFCell headingCell2Sheet2 = headingRowSheet2.createCell(2);
					
					headingCell0Sheet2.setCellValue("Peptide ID");
					headingCell0Sheet2.setCellStyle(style);
					headingCell1Sheet2.setCellValue("Peptide Sequence");
					headingCell1Sheet2.setCellStyle(style);
					headingCell2Sheet2.setCellValue("Peptide Mass");
					headingCell2Sheet2.setCellStyle(style);
					
					XSSFRow sheet2Row = sheet2.createRow(i + 1);
					XSSFCell cellPI = sheet2Row.createCell(0);
					XSSFCell cellPS = sheet2Row.createCell(1);
					XSSFCell cellPM = sheet2Row.createCell(2);
					
					int columnIndexPI = cellPI.getColumnIndex();
					sheet2.autoSizeColumn(columnIndexPI);
					
					int columnIndexPS = cellPS.getColumnIndex();
					sheet2.autoSizeColumn(columnIndexPS);
					
					int columnIndexPM = cellPM.getColumnIndex();
					sheet2.autoSizeColumn(columnIndexPM);
					
					cellPI.setCellValue(peptideId);
					
					cellPS.setCellValue(peptideSequence);
					
					cellPM.setCellValue(peptideMass);
					
					// MassList
					// Get a list of all elements in the document
					NodeList mList = doc.getElementsByTagName("AminoMass");
					
					// System.out.println("XML Elements: ");
					for (int j = 0; j < mList.getLength(); j++) {
						
						XSSFCell cellMass = sheet2Row.createCell(3 + j);
						
						int columnIndex2 = cellMass.getColumnIndex();
						sheet2.autoSizeColumn(columnIndex2);
						
						String tempChar = doc.getElementsByTagName("AminoMass").item(j).getAttributes()
						        .getNamedItem("aminoAcidChar").getNodeValue();
						
						String tempMass = doc.getElementsByTagName("AminoMass").item(j).getAttributes()
						        .getNamedItem("mass").getNodeValue();
						
						String massString = tempChar + "(" + tempMass + ")";
						
						cellMass.setCellValue(massString);
					}
					
					XSSFRow headingRow = sheet.createRow(0);
					
					XSSFCell headingCell0 = headingRow.createCell(0);
					XSSFCell headingCell1 = headingRow.createCell(1);
					XSSFCell headingCell2 = headingRow.createCell(2);
					XSSFCell headingCell3 = headingRow.createCell(3);
					XSSFCell headingCell4 = headingRow.createCell(4);
					XSSFCell headingCell5 = headingRow.createCell(5);
					XSSFCell headingCell6 = headingRow.createCell(6);
					XSSFCell headingCell7 = headingRow.createCell(7);
					XSSFCell headingCell8 = headingRow.createCell(8);
					XSSFCell headingCell9 = headingRow.createCell(9);
					
					headingCell0.setCellValue("Scan No.");
					headingCell0.setCellStyle(style);
					headingCell1.setCellValue("mz value");
					headingCell1.setCellStyle(style);
					headingCell2.setCellValue("Intensity");
					headingCell2.setCellStyle(style);
					headingCell3.setCellValue("Peptide ID");
					headingCell3.setCellStyle(style);
					headingCell4.setCellValue("Peptide Sequence");
					headingCell4.setCellStyle(style);
					headingCell5.setCellValue("Peptide Mass");
					headingCell5.setCellStyle(style);
					headingCell6.setCellValue("Adduct Label");
					headingCell6.setCellStyle(style);
					headingCell7.setCellValue("Adduct Charge");
					headingCell7.setCellStyle(style);
					headingCell8.setCellValue("Calculated m/z");
					headingCell8.setCellStyle(style);
					headingCell9.setCellValue("Deivation");
					headingCell9.setCellStyle(style);
					
					// Adduct List
					// Get a list of all elements in the document
					NodeList aList = doc.getElementsByTagName("scanMatches");
					
					// System.out.println("XML Elements: ");
					for (int j = 0; j < aList.getLength(); j++) {
						
						XSSFRow row = sheet.createRow(count + 1);
						
						count = count + 1;
						
						XSSFCell cell0 = row.createCell(0);
						// cell0.setCellType(Cell.CELL_TYPE_NUMERIC);
						
						XSSFCell cell1 = row.createCell(1);
						// cell1.setCellType(Cell.CELL_TYPE_NUMERIC);
						
						XSSFCell cell2 = row.createCell(2);
						// cell1.setCellType(Cell.CELL_TYPE_NUMERIC);
						
						XSSFCell cell3 = row.createCell(3);
						
						XSSFCell cell4 = row.createCell(4);
						
						XSSFCell cell5 = row.createCell(5);
						
						XSSFCell cell6 = row.createCell(6);
						
						XSSFCell cell7 = row.createCell(7);
						
						XSSFCell cell8 = row.createCell(8);
						
						XSSFCell cell9 = row.createCell(9);
						
						int columnIndex0 = cell0.getColumnIndex();
						sheet.autoSizeColumn(columnIndex0);
						
						int columnIndex1 = cell1.getColumnIndex();
						sheet.autoSizeColumn(columnIndex1);
						
						int columnIndex2 = cell2.getColumnIndex();
						sheet.autoSizeColumn(columnIndex2);
						
						int columnIndex3 = cell3.getColumnIndex();
						sheet.autoSizeColumn(columnIndex3);
						
						int columnIndex4 = cell4.getColumnIndex();
						sheet.autoSizeColumn(columnIndex3);
						
						int columnIndex5 = cell5.getColumnIndex();
						sheet.autoSizeColumn(columnIndex5);
						
						int columnIndex6 = cell6.getColumnIndex();
						sheet.autoSizeColumn(columnIndex6);
						
						int columnIndex7 = cell7.getColumnIndex();
						sheet.autoSizeColumn(columnIndex7);
						
						int columnIndex8 = cell8.getColumnIndex();
						sheet.autoSizeColumn(columnIndex8);
						
						int columnIndex9 = cell9.getColumnIndex();
						sheet.autoSizeColumn(columnIndex9);
						
						String tempIntensity = doc.getElementsByTagName("scanMatches").item(j).getAttributes()
						        .getNamedItem("intensity").getNodeValue();
						
						String tempMzValue = doc.getElementsByTagName("scanMatches").item(j).getAttributes()
						        .getNamedItem("mzValue").getNodeValue();
						
						Double mzvalue = Double.parseDouble(tempMzValue);
						
						String scanNumber = doc.getElementsByTagName("scanMatches").item(j).getAttributes()
						        .getNamedItem("scanNumber").getNodeValue();
						
						cell0.setCellValue(Double.parseDouble(scanNumber));
						
						cell1.setCellValue(Double.parseDouble(tempMzValue));
						
						cell2.setCellValue(Double.parseDouble(tempIntensity));
						
						cell3.setCellValue(peptideId);
						
						cell4.setCellValue(peptideSequence);
						
						cell5.setCellValue(peptideMass);
						
						cell6.setCellValue(adductName);
						
						cell7.setCellValue(adductCharge);
						
						Double calculatedMZ = ((peptideMass + adductMass) / adductCharge);
						
						cell8.setCellValue(calculatedMZ);
						
						Double deivation = calculatedMZ - mzvalue;
						
						cell9.setCellValue(deivation);
						
					}
					
					FileOutputStream fileOut = new FileOutputStream(annotationFolder + "excelReporting.xlsx");
					
					wb.write(fileOut);
					fileOut.flush();
					fileOut.close();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}
		System.out.println("Finished Writing to Excel");
	}
	
	/***
	 * Creates a cell style for the header of the excel
	 * 
	 * @param wb
	 *            the excel workbook
	 * @return returns the cell style
	 */
	private static CellStyle createStyle(Workbook wb) {
		CellStyle style = wb.createCellStyle();
		Font font = wb.createFont();
		((XSSFFont) font).setBold(true);
		style.setFont(font);
		return style;
	}
	
	public static void listAllAttributes(Element element) {
		
		NamedNodeMap attributes = element.getAttributes();
		
		int numAttrs = attributes.getLength();
		
		for (int i = 0; i < numAttrs; i++) {
			
			Attr attr = (Attr) attributes.item(i);
			
			String attrName = attr.getNodeName();
			
			String attrValue = attr.getNodeValue();
			
			// System.out.println(attrName + " == " + attrValue);
			
		}
		
	}
	
}
