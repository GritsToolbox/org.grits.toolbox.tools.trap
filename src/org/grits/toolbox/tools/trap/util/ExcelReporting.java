package org.grits.toolbox.tools.trap.util;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.bind.JAXBContext;
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
import org.grits.toolbox.core.dataShare.PropertyHandler;
import org.grits.toolbox.core.datamodel.Entry;
import org.grits.toolbox.core.datamodel.property.ProjectProperty;
import org.grits.toolbox.core.datamodel.util.DataModelSearch;
import org.grits.toolbox.entry.ms.property.MassSpecProperty;
import org.grits.toolbox.tools.trap.excel.GlycanCartoon;
import org.grits.toolbox.tools.trap.om.PeptideInfo;
import org.grits.toolbox.tools.trap.wizard.GlycoProteinAnnotationWizard;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

public class ExcelReporting {
	
	public ExcelReporting(String annotationFolder, String fileName, GlycoProteinAnnotationWizard gpaWizard) {
		
		File folder = new File(annotationFolder);
		File[] listOfFiles = folder.listFiles();
		
		// System.out.println("Starting write To Excel");
		
		CellStyle style;
		String sheetName = "PrecursorInfo";
		String sheetName2 = "PeptideInfo";
		String sheetName3 = "Settings";
		
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet(sheetName);
		XSSFSheet sheet2 = wb.createSheet(sheetName2);
		XSSFSheet sheet3 = wb.createSheet(sheetName3);
		
		int count = 1;
		
		style = createStyle(wb);
		
		for (int i = 0; i < listOfFiles.length; i++) {
			String filename = listOfFiles[i].getName();
			if (filename.endsWith(".xml") || filename.endsWith(".XML")) {
				// System.out.println("File Name = " + filename);
				
				try {
					String completeFilePath = annotationFolder + filename;
					
					// BufferedReader bf = new BufferedReader(new
					// InputStreamReader(System.in));
					
					// Create a factory
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					
					// Use the factory to create a builder
					DocumentBuilder builder = factory.newDocumentBuilder();
					
					Document doc = builder.parse(annotationFolder + filename);
					
					// creating the JAXB context
					JAXBContext jContext = JAXBContext.newInstance(PeptideInfo.class);
					
					// creating the unmarshall object
					javax.xml.bind.Unmarshaller unmarshallerObj = jContext.createUnmarshaller();
					// calling the unmarshall method
					
					PeptideInfo t_peptideInfo = (PeptideInfo) org.grits.toolbox.core.utilShare.XMLUtils
					        .unmarshalObjectXML(completeFilePath, PeptideInfo.class);
					
					int peptideId = t_peptideInfo.getId();
					
					String peptideSequence = t_peptideInfo.getSequence();
					
					Double peptideMass = t_peptideInfo.getMass();
					
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
					
					cellPI.setCellValue(t_peptideInfo.getId());
					
					cellPS.setCellValue(t_peptideInfo.getSequence());
					
					cellPM.setCellValue(t_peptideInfo.getMass());
					
					// MassList
					// Get a list of all elements in the document
					NodeList mList = doc.getElementsByTagName("AminoMass");
					
					// System.out.println("XML Elements: ");
					for (int j = 0; j < mList.getLength(); j++) {
						
						XSSFCell cellMass = sheet2Row.createCell(3 + j);
						
						int columnIndex2 = cellMass.getColumnIndex();
						
						String tempChar = doc.getElementsByTagName("AminoMass").item(j).getAttributes()
						        .getNamedItem("aminoAcidChar").getNodeValue();
						
						String tempMass = doc.getElementsByTagName("AminoMass").item(j).getAttributes()
						        .getNamedItem("mass").getNodeValue();
						
						String massString = tempChar + "(" + tempMass + ")";
						
						cellMass.setCellValue(massString);
						sheet2.autoSizeColumn(columnIndex2);
					}
					
					int columnIndexPI = cellPI.getColumnIndex();
					sheet2.autoSizeColumn(columnIndexPI);
					
					int columnIndexPS = cellPS.getColumnIndex();
					sheet2.autoSizeColumn(columnIndexPS);
					
					int columnIndexPM = cellPM.getColumnIndex();
					sheet2.autoSizeColumn(columnIndexPM);
					
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
					XSSFCell headingCell10 = headingRow.createCell(10);
					XSSFCell headingCell11 = headingRow.createCell(11);
					
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
					headingCell10.setCellValue("Glycan GWB");
					headingCell10.setCellStyle(style);
					headingCell11.setCellValue("Glycan Cartoon");
					headingCell11.setCellStyle(style);
					
					// System.out.println("Glycopeptide Length = " +
					// t_peptideInfo.getGlycoPeptide().size());
					
					for (int k = 0; k < t_peptideInfo.getGlycoPeptide().size(); k++) {
						
						// System.out.println(
						// "Adduct Length = " +
						// t_peptideInfo.getGlycoPeptide().get(k).getAdductInfos().size());
						
						for (int j = 0; j < t_peptideInfo.getGlycoPeptide().get(k).getAdductInfos().size(); j++) {
							
							XSSFRow row = sheet.createRow(count + 1);
							
							count = count + 1;
							
							// System.out.println("ScanMatches Length = " +
							// t_peptideInfo.getGlycoPeptide().get(k)
							// .getAdductInfos().get(j).getScanMatches().size());
							
							for (int z = 0; z < t_peptideInfo.getGlycoPeptide().get(k).getAdductInfos().get(j)
							        .getScanMatches().size(); z++) {
								
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
								
								XSSFCell cell10 = row.createCell(10);
								
								XSSFCell cell11 = row.createCell(11);
								
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
								
								int columnIndex10 = cell10.getColumnIndex();
								sheet.autoSizeColumn(columnIndex10);
								
								int columnIndex11 = cell11.getColumnIndex();
								sheet.autoSizeColumn(columnIndex11);
								
								// String glycanGWB =
								// doc.getElementsByTagName("Glycans").item(k).getAttributes()
								// .getNamedItem("GlycanGWBSequence").getNodeValue();
								
								String glycanGWB = t_peptideInfo.getGlycoPeptide().get(k).getGlycanObjects().get(0)
								        .getGlycanGWBSequence();
								
								cell0.setCellValue(t_peptideInfo.getGlycoPeptide().get(k).getAdductInfos().get(j)
								        .getScanMatches().get(z).getScanNumber());
								
								cell1.setCellValue(t_peptideInfo.getGlycoPeptide().get(k).getAdductInfos().get(j)
								        .getScanMatches().get(z).getMzValue());
								
								cell2.setCellValue(t_peptideInfo.getGlycoPeptide().get(k).getAdductInfos().get(j)
								        .getScanMatches().get(z).getIntensity());
								
								cell3.setCellValue(t_peptideInfo.getId());
								
								cell4.setCellValue(t_peptideInfo.getSequence());
								
								cell5.setCellValue(t_peptideInfo.getMass());
								
								cell6.setCellValue(
								        t_peptideInfo.getGlycoPeptide().get(k).getAdductInfos().get(j).getIonName());
								
								cell7.setCellValue(
								        t_peptideInfo.getGlycoPeptide().get(k).getAdductInfos().get(j).getCharge());
								
								Double calculatedMZ = ((peptideMass
								        + t_peptideInfo.getGlycoPeptide().get(k).getAdductInfos().get(j).getMass())
								        / t_peptideInfo.getGlycoPeptide().get(k).getAdductInfos().get(j).getCharge());
								
								cell8.setCellValue(calculatedMZ);
								
								Double absoluteDeivation = calculatedMZ - t_peptideInfo.getGlycoPeptide().get(k)
								        .getAdductInfos().get(j).getScanMatches().get(z).getMzValue();
								
								Double deivation = (absoluteDeivation * 1000000) / calculatedMZ;
								
								cell9.setCellValue(deivation);
								
								cell10.setCellValue(glycanGWB);
								
								GlycanCartoon gCartoon = new GlycanCartoon(glycanGWB);
								
								Boolean glycanImageSet = gCartoon.setGWBSequenceImage(wb, sheet, count, 11, glycanGWB);
								
							}
						}
						
					}
					
					Entry msEntry = gpaWizard.getInitial().getMsEntryList().get(0);
					
					String workspaceLocation = PropertyHandler.getVariable("workspace_location");
					String projectName = DataModelSearch.findParentByType(msEntry, ProjectProperty.TYPE)
					        .getDisplayName();
					
					String pathToFile = workspaceLocation + projectName + File.separator
					        + MassSpecProperty.getFoldername();
					
					XSSFRow sheet3Row0 = sheet3.createRow(0);
					
					XSSFCell sheet3Cell0 = sheet3Row0.createCell(0);
					sheet3Cell0.setCellValue("mzXML file location");
					sheet3.autoSizeColumn(0);
					XSSFCell sheet3Cell1 = sheet3Row0.createCell(1);
					sheet3Cell1.setCellValue(pathToFile);
					sheet3.autoSizeColumn(1);
					
					XSSFRow sheet3Row1 = sheet3.createRow(1);
					
					XSSFCell sheet3Row1Cell0 = sheet3Row1.createCell(0);
					sheet3Row1Cell0.setCellValue("Accuracy MS");
					XSSFCell sheet3Row1Cell1 = sheet3Row1.createCell(1);
					String accuracy = gpaWizard.getMethod().getAccuracy().toString();
					sheet3Row1Cell1.setCellValue(accuracy);
					
					XSSFRow sheet3Row2 = sheet3.createRow(2);
					
					XSSFCell sheet3Row2Cell0 = sheet3Row2.createCell(0);
					sheet3Row2Cell0.setCellValue("Precursor Intensity");
					XSSFCell sheet3Row2Cell1 = sheet3Row2.createCell(1);
					String precursorIntensity = gpaWizard.getMethod().getPrecursorIntensityCutoff().toString();
					sheet3Row2Cell1.setCellValue(precursorIntensity);
					
					XSSFRow sheet3Row3 = sheet3.createRow(3);
					
					XSSFCell sheet3Row3Cell0 = sheet3Row3.createCell(0);
					sheet3Row3Cell0.setCellValue("Protein Sequence");
					XSSFCell sheet3Row3Cell1 = sheet3Row3.createCell(1);
					String proteinSequence = gpaWizard.getMethod().getAnalyteSettings().get(0).getGlycoProteinSettings()
					        .getProteinSequence();
					sheet3Row3Cell1.setCellValue(proteinSequence);
					
					XSSFRow sheet3Row4 = sheet3.createRow(4);
					
					XSSFCell sheet3Row4Cell0 = sheet3Row4.createCell(0);
					sheet3Row4Cell0.setCellValue("Enzyme");
					XSSFCell sheet3Row4Cell1 = sheet3Row4.createCell(1);
					String enzyme = gpaWizard.getMethod().getAnalyteSettings().get(0).getGlycoProteinSettings()
					        .getEnzyme();
					sheet3Row4Cell1.setCellValue(enzyme);
					
					XSSFRow sheet3Row5 = sheet3.createRow(5);
					
					XSSFCell sheet3Row5Cell0 = sheet3Row5.createCell(0);
					sheet3Row5Cell0.setCellValue("Derivative");
					XSSFCell sheet3Row5Cell1 = sheet3Row5.createCell(1);
					String derivative = gpaWizard.getMethod().getAnalyteSettings().get(0).getGlycoProteinSettings()
					        .getPerDerivatisationType();
					sheet3Row5Cell1.setCellValue(derivative);
					
					XSSFRow sheet3Row6 = sheet3.createRow(6);
					
					XSSFCell sheet3Row6Cell0 = sheet3Row6.createCell(0);
					sheet3Row6Cell0.setCellValue("Database Name");
					XSSFCell sheet3Row6Cell1 = sheet3Row6.createCell(1);
					String databaseName = gpaWizard.getMethod().getAnalyteSettings().get(0).getGlycoProteinSettings()
					        .getDatabase().getDatabase();
					sheet3Row6Cell1.setCellValue(databaseName);
					
					XSSFRow sheet3Row7 = sheet3.createRow(7);
					
					XSSFCell sheet3Row7Cell0 = sheet3Row7.createCell(0);
					sheet3Row7Cell0.setCellValue("Adduct name");
					XSSFCell sheet3Row7Cell1 = sheet3Row7.createCell(1);
					String adductName = gpaWizard.getMethod().getIons().get(0).getName();
					sheet3Row7Cell1.setCellValue(adductName);
					
					XSSFRow sheet3Row8 = sheet3.createRow(8);
					
					XSSFCell sheet3Row8Cell0 = sheet3Row8.createCell(0);
					sheet3Row8Cell0.setCellValue("Adduct Number");
					XSSFCell sheet3Row8Cell1 = sheet3Row8.createCell(1);
					String adductNumber = gpaWizard.getMethod().getIons().get(0).getCounts().get(0).toString();
					sheet3Row8Cell1.setCellValue(adductNumber);
					
					XSSFRow sheet3Row9 = sheet3.createRow(9);
					
					XSSFCell sheet3Row9Cell0 = sheet3Row9.createCell(0);
					sheet3Row9Cell0.setCellValue("Adduct Charge");
					XSSFCell sheet3Row9Cell1 = sheet3Row9.createCell(1);
					String adductCharge = gpaWizard.getMethod().getIons().get(0).getCharge().toString();
					sheet3Row9Cell1.setCellValue(adductCharge);
					
					XSSFRow sheet3Row10 = sheet3.createRow(10);
					
					XSSFCell sheet3Row10Cell0 = sheet3Row10.createCell(0);
					sheet3Row10Cell0.setCellValue("Adduct Mass");
					XSSFCell sheet3Row10Cell1 = sheet3Row10.createCell(1);
					String adductMass = gpaWizard.getMethod().getIons().get(0).getMass().toString();
					sheet3Row10Cell1.setCellValue(adductMass);
					
					FileOutputStream fileOut = new FileOutputStream(annotationFolder + "excelReporting.xlsx");
					
					wb.write(fileOut);
					fileOut.flush();
					fileOut.close();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
			}
		}
		
		// System.out.println("Finished Writing to Excel");
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
	
}
