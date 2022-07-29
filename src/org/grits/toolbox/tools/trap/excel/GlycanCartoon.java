package org.grits.toolbox.tools.trap.excel;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.eurocarbdb.application.glycanbuilder.Glycan;
import org.eurocarbdb.application.glycanbuilder.GlycanRendererAWT;
import org.eurocarbdb.application.glycanbuilder.GraphicOptions;
import org.eurocarbdb.application.glycanbuilder.ResidueType;
import org.eurocarbdb.application.glycanbuilder.Union;
import org.eurocarbdb.application.glycoworkbench.GlycanWorkspace;
import org.grits.toolbox.utils.io.ExcelWriterHelper;

public class GlycanCartoon {
	
	public GlycanCartoon(String a_strOutputFile) throws IOException {
		
		this.m_mapGWBSequenceToImage = new HashMap<>();
		
	}
	
	private static final Logger			logger		= Logger.getLogger(GlycanCartoon.class);
	
	private ExcelWriterHelper			m_helper	= new ExcelWriterHelper();
	private Map<String, BufferedImage>	m_mapGWBSequenceToImage;
	
	public void convertGWBSequenceToImage(Workbook wb, Sheet a_sheet, int a_iRow, int a_iCol) {
		Cell t_cell = a_sheet.getRow(a_iRow).getCell(a_iCol);
		if (t_cell == null || t_cell.getStringCellValue().isEmpty())
			return;
		// Remove text after the image generation
		if (this.setGWBSequenceImage(wb, a_sheet, a_iRow, a_iCol, t_cell.getStringCellValue()))
			t_cell.setCellValue("");
	}
	
	public boolean setGWBSequenceImage(Workbook wb, Sheet a_sheet, int a_iRow, int a_iCol, String a_strGWBSeq) {
		try {
			// System.out.println("Generate image of " + a_strGWBSeq);
			BufferedImage t_img = this.getGWBSequenceImage(a_strGWBSeq);
			if (t_img == null)
				return false;
			
			this.m_helper.writeCellImage(wb, a_sheet, a_iRow, a_iCol, t_img, new ArrayList<>());
			
		} catch (Exception e) {
			logger.error("An error in converting GWB sequence in the cell to image", e);
			return false;
		}
		return true;
	}
	
	private BufferedImage getGWBSequenceImage(String a_strGWBSeq) {
		
		if (this.m_mapGWBSequenceToImage.containsKey(a_strGWBSeq))
			return this.m_mapGWBSequenceToImage.get(a_strGWBSeq);
		
		BufferedImage t_img = null;
		
		t_img = createGlycanImage(a_strGWBSeq);
		
		if (t_img == null)
			return null;
		this.m_mapGWBSequenceToImage.put(a_strGWBSeq, t_img);
		return t_img;
	}
	
	private static GlycanWorkspace	glycanWorkspase		= new GlycanWorkspace(null, false, new GlycanRendererAWT());
	private static boolean			isInitializedGWS	= false;
	
	/**
	 * Initializes graphic options of GlycanWorkspace.
	 */
	private static void initGraphicOptions() {
		if (isInitializedGWS)
			return;
		// Set orientation of glycan: RL - right to left, LR - left to right, TB
		// - top to bottom, BT - bottom to top
		glycanWorkspase.getGraphicOptions().ORIENTATION = GraphicOptions.RL;
		// Set flag to show information such as linkage positions and anomers
		glycanWorkspase.getGraphicOptions().SHOW_INFO = true;
		// Set flag to show mass
		glycanWorkspase.getGraphicOptions().SHOW_MASSES = false;
		// Set flag to show reducing end
		glycanWorkspase.getGraphicOptions().SHOW_REDEND = true;
		
		// glycanWorkspase.setDisplay(GraphicOptions.DISPLAY_NORMAL);
		// glycanWorkspase.setNotation(GraphicOptions.NOTATION_CFG);
		
		isInitializedGWS = true;
	}
	
	/**
	 * Creates glycan image from a GWB sequence without reducing end
	 * 
	 * @param a_strGWBSeq
	 *            String of GWB sequence
	 * @return BufferedImage of the glycan
	 * @see #createGlycanImage(String, String)
	 */
	public static BufferedImage createGlycanImage(String a_strGWBSeq) {
		return createGlycanImage(a_strGWBSeq, null);
	}
	
	/**
	 * Crates glycan image from a GWB sequence and a String of reducing end
	 * 
	 * @param a_strGWBSeq
	 *            String of GWB sequence
	 * @param a_strRedEnd
	 *            String of reducing end
	 * @return BufferedImage of the glycan with reducing end
	 */
	public static BufferedImage createGlycanImage(String a_strGWBSeq, String a_strRedEnd) {
		// Initialize GWB
		initGraphicOptions();
		/*
		 * // Prepare GWB sequence containing reducing end if (
		 * a_strGWBSeq.indexOf("freeEnd") == -1 ) {
		 * 
		 * // if ( Residue )
		 * ResidueDictionary.getReducingEnds().add(ResidueType.
		 * createOtherReducing(name, mass)) }
		 */
		Glycan t_convertedGlycan = null;
		// Return null if conversion failed
		try {
			t_convertedGlycan = Glycan.fromString(a_strGWBSeq);
		} catch (Exception e) {
			logger.error("Glycan image conversion from GWB sequece was failed", e);
			return null;
		}
		
		if (a_strRedEnd != null) {
			t_convertedGlycan.setReducingEndType(ResidueType.createOtherResidue(a_strRedEnd, -1.0));
		}
		// GlycanRenderer().getImage(Collection<Glycan> glycans, boolean
		// showInfo, boolean showMasses, boolean showReducingEnd)
		BufferedImage t_image = glycanWorkspase.getGlycanRenderer().getImage(new Union<Glycan>(t_convertedGlycan), true,
		        false, true, 0.5d);
		// Return null if conversion is failed
		if (t_image.getWidth() == 1 && t_image.getHeight() == 1)
			return null;
		return t_image;
	}
	
}
