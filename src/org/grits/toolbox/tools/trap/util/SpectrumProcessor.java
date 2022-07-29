package org.grits.toolbox.tools.trap.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.grits.toolbox.tools.trap.MzXMLWriter.MzXMLWriter;
import org.grits.toolbox.tools.trap.om.SpectrumFilter;
import org.systemsbiology.jrap.grits.stax.GRITSMSXMLParser;
import org.systemsbiology.jrap.grits.stax.Scan;

public class SpectrumProcessor {
	
	private boolean m_writeParentScan = false;
	
	public void start(SpectrumFilter a_spectrumFilter) throws IOException {
		
		// Create the MXXML writer
		MzXMLWriter writer = new MzXMLWriter();
		
		// Create mzXML file
		writer.createMZXML(a_spectrumFilter.getSaveLocation());
		
		// Write Header
		writer.writeHeader();
		
		// open mzxml file and read scans
		this.processMZXMLFile(a_spectrumFilter, writer);
		
		writer.writeFooter();
		
		// close the file
		writer.closeFile();
		
	}
	
	private void processMZXMLFile(SpectrumFilter a_spectrumFilter, MzXMLWriter a_writer) throws IOException {
		
		String fileName = this.getFileName(a_spectrumFilter.getOpenLocation());
		String pathToFile = this.getPathName(a_spectrumFilter.getOpenLocation());
		
		GRITSMSXMLParser gritsmsxmlParser = new GRITSMSXMLParser(pathToFile + fileName);
		
		List<Integer> listIntscans = gritsmsxmlParser.getMS1ScanList();
		
		for (int i = 0; i < listIntscans.size(); i++) {
			
			if (i < listIntscans.size() - 1 && (listIntscans.get(i) + 1) == listIntscans.get(i + 1))
				continue;
			
			List<Scan> scans = gritsmsxmlParser.getScanAndSubScans(listIntscans.get(i));
			
			// List<Scan> scans = ((IMSAnnotationFileReader)
			// file.getReader()).readMSFile(file, listIntscans.get(i));
			
			// List<Integer> writtenParentScans = new ArrayList<Integer>();
			
			HashMap<Integer, Scan> scanNumber = new HashMap<Integer, Scan>();
			
			if (scans.size() < 2)
				continue;
			
			if (m_writeParentScan == true) {
				
				a_writer.writeScan(scans.get(0));
				
			}
			
			for (int j = 1; j < scans.size(); j++) {
				Scan scan = scans.get(j);
				
				scanNumber.put(scan.getHeader().getNum(), scan);
				
				// Integer parentScan = listIntscans.get(i);
				
				if (scan.getHeader().getMsLevel() != 2) {
					continue;
					
				}
				
				if (this.validScan(scan, a_spectrumFilter)) {
					// Wirte parent scan first - if not written at the first
					// place
					a_writer.writeScan(scan);
				}
				
			}
			
		}
		
	}
	
	private boolean validScan(Scan a_scan, SpectrumFilter a_spectrumFilter) {
		
		// Debugging test operations
		
		// calcualte intervall
		Double accuracyValue = a_spectrumFilter.getAccuracy();
		Double cutOffValue = a_spectrumFilter.getCutOfValue();
		Double mzValue = a_spectrumFilter.getMzValue();
		Double minInterval = mzValue - ((mzValue / 1000000) * accuracyValue);
		Double maxInterval = mzValue + ((mzValue / 1000000) * accuracyValue);
		
		// Double highestPeakIntensityInSpectra =
		// a_scan.getPeaklist().get(0).getIntensity();
		Double highestPeakIntensityInSpectra = a_scan.getMassIntensityList()[1][0];
		/* first vector is masses and second vector is the intensity */
		
		Double rangeIntensity = 0.0;
		
		// iterate over peaks
		for (int i = 1; i < a_scan.getMassIntensityList()[1].length; i++) {
			if (a_scan.getMassIntensityList()[1][i] > highestPeakIntensityInSpectra) {
				highestPeakIntensityInSpectra = a_scan.getMassIntensityList()[1][i];
			}
		}
		
		for (int i = 0; i < a_scan.getMassIntensityList()[0].length; i++) {
			if (a_scan.getMassIntensityList()[0][i] < maxInterval
			        && a_scan.getMassIntensityList()[0][i] > minInterval) {
				// if (a_scan.getPeaklist().get(i).getIntensity() >
				// rangeIntensity) {
				// System.out.print("Found Range mz ");
				// System.out.println(a_scan.getMassIntensityList()[0][i]);
				rangeIntensity = a_scan.getMassIntensityList()[1][i];
			}
		}
		// }
		
		if (rangeIntensity > 0)
		
		{
			if (rangeIntensity > highestPeakIntensityInSpectra * cutOffValue / 100) {
				return true;
			}
		}
		
		return false;
	}
	
	private String getPathName(String a_openLocation) {
		// int index = a_openLocation.lastIndexOf("/");
		int index = a_openLocation.lastIndexOf(File.separator);
		String pathToFile = a_openLocation.substring(0, index + 1);
		return pathToFile;
	}
	
	private String getFileName(String a_openLocation) {
		// int index = a_openLocation.lastIndexOf("/");
		int index = a_openLocation.lastIndexOf(File.separator);
		String fileName = a_openLocation.substring(index + 1);
		return fileName;
	}
	
}
