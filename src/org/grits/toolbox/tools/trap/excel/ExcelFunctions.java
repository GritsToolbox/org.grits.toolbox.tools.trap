package org.grits.toolbox.tools.trap.excel;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.grits.toolbox.tools.trap.calculateMass.ProteinToPeptide;
import org.grits.toolbox.tools.trap.om.AminoAcidDatabase;

/**
 * 
 * @author Aravind Kalimurthy
 *
 */

public class ExcelFunctions {
	
	/**
	 * Writes the Peptide Sequence and its Permethylated or Non-Permethylated
	 * Masses to Excel for the given Protein Sequence.
	 * 
	 * @param peptideSequence
	 *            List of peptide sequence obtained from protein sequence based
	 *            on the given enzyme
	 * @param permethylated
	 *            tells whether to use permethylated or normal mass of the amino
	 *            acid (1 for permethylated mass and 0 for non-permethylated
	 *            mass)
	 * @param fileName
	 *            name of the excel sheet to be saved along with the path
	 */
	public static void writeToExcel(List<String> peptideSequence, int permethylated, String fileName) {
		
		try {
			
			CellStyle style;
			String sheetName = "PeptideMasses";
			String sheetName2 = "AminoAcidMasses";
			
			List<ArrayList<Double>> aminoPeptideMass = new ArrayList<ArrayList<Double>>();
			List<ArrayList<Double>> permethylatedPeptideMass = new ArrayList<ArrayList<Double>>();
			List<ArrayList<ArrayList<Double>>> MultipleFinalResult = new ArrayList<ArrayList<ArrayList<Double>>>();
			
			aminoPeptideMass = ProteinToPeptide.getAminoPeptideMass(peptideSequence, 0);
			permethylatedPeptideMass = ProteinToPeptide.getAminoPeptideMass(peptideSequence, 1);
			MultipleFinalResult = ProteinToPeptide.getMultipleListResult(peptideSequence, 1);
			
			System.out.println("Starting write To Excel");
			
			XSSFWorkbook wb = new XSSFWorkbook();
			XSSFSheet sheet = wb.createSheet(sheetName);
			XSSFSheet sheet2 = wb.createSheet(sheetName2);
			
			style = createStyle(wb);
			// Writing for Sheet1, iterating r number of rows
			for (int r = 0; r < peptideSequence.size(); r++) {
				
				XSSFRow row = sheet.createRow(r);
				XSSFCell cellP = row.createCell(0);
				
				cellP.setCellValue(peptideSequence.get(r));
				
				int columnIndex = cellP.getColumnIndex();
				sheet.autoSizeColumn(columnIndex);
				
				// For Non-Permethylated Masses
				List<Double> tempAminoMass = new ArrayList<Double>();
				tempAminoMass.addAll(aminoPeptideMass.get(r));
				
				// For Permethylated Masses
				List<Double> tempPermethylatedMass = new ArrayList<Double>();
				tempPermethylatedMass.addAll(permethylatedPeptideMass.get(r));
				
				if (permethylated == 1) {
					// iterating c number of columns
					for (int c = 0; c < tempPermethylatedMass.size(); c++) {
						XSSFCell cell = row.createCell(c + 1);
						cell.setCellValue(tempPermethylatedMass.get(c));
					}
				}
				
				if (permethylated == 0) {
					for (int c = 0; c < tempAminoMass.size(); c++) {
						XSSFCell cell = row.createCell(c + 1);
						cell.setCellValue(tempAminoMass.get(c));
					}
				}
			}
			
			int count = 0;
			
			// Writing For Sheet2
			for (int r = 0; r < peptideSequence.size(); r++) {
				
				// For Non-Permethylated Masses
				List<Double> tempAminoMass = new ArrayList<Double>();
				tempAminoMass.addAll(aminoPeptideMass.get(r));
				
				// For Permethylated Masses
				List<Double> tempPermethylatedMass = new ArrayList<Double>();
				tempPermethylatedMass.addAll(permethylatedPeptideMass.get(r));
				
				List<ArrayList<Double>> permethylatedListValue = new ArrayList<ArrayList<Double>>();
				permethylatedListValue.addAll(MultipleFinalResult.get(r));
				
				if (permethylated == 1) {
					// iterating c number of columns
					for (int c = 0; c < tempPermethylatedMass.size(); c++) {
						
						Double rowTotal = 18.0105646;
						
						XSSFRow row = sheet2.createRow(count);
						XSSFCell cellP = row.createCell(0);
						
						int columnIndex = cellP.getColumnIndex();
						sheet2.autoSizeColumn(columnIndex);
						
						cellP.setCellValue(peptideSequence.get(r));
						
						XSSFCell cell = row.createCell(1);
						cell.setCellValue(tempPermethylatedMass.get(c));
						
						XSSFFont font = wb.createFont();
						font.setBold(true);
						
						cell.setCellStyle(style);
						
						char charPeptide[] = (peptideSequence.get(r)).toCharArray();
						
						List<Double> rowPermethylatedValue = new ArrayList<Double>();
						rowPermethylatedValue.addAll(permethylatedListValue.get(c));
						
						for (int i = 0; i < (peptideSequence.get(r)).length(); i++) {
							char temp = charPeptide[i];
							Double cellPermethylatedValue = rowPermethylatedValue.get(i);
							String massString = temp + " ( " + cellPermethylatedValue + " ) ";
							
							rowTotal = rowTotal + cellPermethylatedValue;
							
							XSSFCell cellMass = row.createCell(2 + i);
							
							cellMass.setCellValue(massString);
							
							int columnIndex2 = cellMass.getColumnIndex();
							sheet2.autoSizeColumn(columnIndex2);
						}
						
						// XSSFCell cellR = row.createCell(2);
						// cellR.setCellValue(rowTotal);
						
						count = count + 1;
					}
				}
				
				if (permethylated == 0) {
					for (int c = 0; c < tempAminoMass.size(); c++) {
						
						Double massValue = 0.0;
						
						XSSFRow row = sheet2.createRow(count);
						XSSFCell cellP = row.createCell(0);
						
						int columnIndex = cellP.getColumnIndex();
						sheet2.autoSizeColumn(columnIndex);
						
						cellP.setCellValue(peptideSequence.get(r));
						
						XSSFCell cell = row.createCell(1);
						cell.setCellValue(tempAminoMass.get(c));
						
						XSSFFont font = wb.createFont();
						font.setBold(true);
						
						cell.setCellStyle(style);
						
						char charPeptide[] = (peptideSequence.get(r)).toCharArray();
						
						for (int i = 0; i < (peptideSequence.get(r)).length(); i++) {
							char temp = charPeptide[i];
							
							if (temp == 'A')
								massValue = AminoAcidDatabase.A.mass(0).get(0);
							if (temp == 'R')
								massValue = AminoAcidDatabase.R.mass(0).get(0);
							if (temp == 'N')
								massValue = AminoAcidDatabase.N.mass(0).get(0);
							if (temp == 'D')
								massValue = AminoAcidDatabase.D.mass(0).get(0);
							if (temp == 'C')
								massValue = AminoAcidDatabase.C.mass(0).get(0);
							if (temp == 'E')
								massValue = AminoAcidDatabase.E.mass(0).get(0);
							if (temp == 'Q')
								massValue = AminoAcidDatabase.Q.mass(0).get(0);
							if (temp == 'G')
								massValue = AminoAcidDatabase.G.mass(0).get(0);
							if (temp == 'H')
								massValue = AminoAcidDatabase.H.mass(0).get(0);
							if (temp == 'I')
								massValue = AminoAcidDatabase.I.mass(0).get(0);
							if (temp == 'L')
								massValue = AminoAcidDatabase.L.mass(0).get(0);
							if (temp == 'K')
								massValue = AminoAcidDatabase.K.mass(0).get(0);
							if (temp == 'M')
								massValue = AminoAcidDatabase.M.mass(0).get(0);
							if (temp == 'F')
								massValue = AminoAcidDatabase.F.mass(0).get(0);
							if (temp == 'P')
								massValue = AminoAcidDatabase.P.mass(0).get(0);
							if (temp == 'S')
								massValue = AminoAcidDatabase.S.mass(0).get(0);
							if (temp == 'T')
								massValue = AminoAcidDatabase.T.mass(0).get(0);
							if (temp == 'W')
								massValue = AminoAcidDatabase.W.mass(0).get(0);
							if (temp == 'Y')
								massValue = AminoAcidDatabase.Y.mass(0).get(0);
							if (temp == 'V')
								massValue = AminoAcidDatabase.V.mass(0).get(0);
							
							String massString = temp + " ( " + massValue + " ) ";
							
							XSSFCell cellMass = row.createCell(2 + i);
							
							cellMass.setCellValue(massString);
							
							int columnIndex2 = cellMass.getColumnIndex();
							sheet2.autoSizeColumn(columnIndex2);
							
						}
						
						count = count + 1;
					}
				}
				
			}
			
			FileOutputStream fileOut = new FileOutputStream(fileName);
			
			wb.write(fileOut);
			fileOut.flush();
			fileOut.close();
			
		} catch (Exception e) {
			e.printStackTrace();
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
	
	/***
	 * Creates a cell style for the rows of the excel
	 * 
	 * @param wb
	 *            the excel workbook
	 * @return returns the cell style
	 */
	private static CellStyle createRowStyle(Workbook wb) {
		CellStyle rowStyle = wb.createCellStyle();
		rowStyle.setWrapText(true);
		return rowStyle;
	}
	
}
