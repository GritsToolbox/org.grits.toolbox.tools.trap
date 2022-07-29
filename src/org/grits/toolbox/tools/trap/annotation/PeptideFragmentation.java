package org.grits.toolbox.tools.trap.annotation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.eurocarbdb.application.glycanbuilder.Glycan;
import org.eurocarbdb.application.glycanbuilder.GlycanRendererAWT;
import org.eurocarbdb.application.glycanbuilder.IonCloud;
import org.eurocarbdb.application.glycanbuilder.MassOptions;
import org.eurocarbdb.application.glycanbuilder.ResidueDictionary;
import org.eurocarbdb.application.glycoworkbench.GlycanWorkspace;
import org.grits.toolbox.core.dataShare.PropertyHandler;
import org.grits.toolbox.core.datamodel.Entry;
import org.grits.toolbox.core.datamodel.property.ProjectProperty;
import org.grits.toolbox.core.datamodel.util.DataModelSearch;
import org.grits.toolbox.entry.ms.property.MassSpecProperty;
import org.grits.toolbox.entry.ms.property.datamodel.MSPropertyDataFile;
import org.grits.toolbox.entry.ms.property.datamodel.MassSpecMetaData;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.utils.DatabaseUtils;
import org.grits.toolbox.ms.annotation.structure.AnalyteStructure;
import org.grits.toolbox.ms.annotation.structure.GlycanPreDefinedOptions;
import org.grits.toolbox.ms.annotation.structure.GlycanStructure;
import org.grits.toolbox.ms.file.MSFile;
import org.grits.toolbox.ms.file.reader.IMSAnnotationFileReader;
import org.grits.toolbox.ms.om.data.AnalyteSettings;
import org.grits.toolbox.ms.om.data.IonSettings;
import org.grits.toolbox.ms.om.data.Method;
import org.grits.toolbox.ms.om.data.Scan;
import org.grits.toolbox.tools.trap.calculateMass.ProteinToPeptide;
import org.grits.toolbox.tools.trap.data.GlycanObjects;
import org.grits.toolbox.tools.trap.om.AdductInfo;
import org.grits.toolbox.tools.trap.om.AdductSettings;
import org.grits.toolbox.tools.trap.om.AminoAcidMass;
import org.grits.toolbox.tools.trap.om.GlycoPeptideConfiguration;
import org.grits.toolbox.tools.trap.om.IonCombination;
import org.grits.toolbox.tools.trap.om.Peptide;
import org.grits.toolbox.tools.trap.om.PeptideInfo;
import org.grits.toolbox.tools.trap.om.PeptideMass;
import org.grits.toolbox.tools.trap.om.ScanMatches;
import org.grits.toolbox.tools.trap.util.GlycanDatabaseFileHandler;
import org.grits.toolbox.tools.trap.wizard.GlycoProteinAnnotationWizard;
import org.grits.toolbox.util.structure.glycan.util.FilterUtils;
import org.grits.toolbox.widgets.tools.GRITSProcessStatus;

public class PeptideFragmentation {
	
	// log4J Logger
	private static final Logger logger = Logger.getLogger(PeptideFragmentation.class);
	
	public PeptideFragmentation(GlycoProteinAnnotationWizard gpaWizard, Method method, String annotationFolder)
	        throws JAXBException, IOException {
		
		// System.out.println("Begin Fragmentation");
		
		// Get Peptide Information
		List<Peptide> peptideSequence = this.generatePeptide(method);
		
		for (int i = 0; i < peptideSequence.size(); i++) {
			// System.out.println(peptideSequence.get(i).getSequence());
			// System.out.print(" ");
			// System.out.println(peptideSequence.get(i).getMasses());
			// System.out.print(" ");
			
		}
		
		// Get Adducts Information
		List<AdductSettings> adductSettings = this.generateAdductSettings(method);
		
		for (int i = 0; i < adductSettings.size(); i++) {
			// System.out.print(adductSettings.get(i).getLabel());
			// System.out.print(" ");
			// System.out.print(adductSettings.get(i).getCharge());
			// System.out.println("");
		}
		
		new GlycanWorkspace(null, false, new GlycanRendererAWT());
		
		String derivativeType = method.getAnalyteSettings().get(0).getGlycoProteinSettings().getPerDerivatisationType();
		
		List<GlycanObjects> listGlycanObjects = new ArrayList<GlycanObjects>();
		
		if (derivativeType == "Permethylated")
			derivativeType = "perMe";
		else if (derivativeType == "Normal")
			derivativeType = "None";
		
		updateDatabaseReferences(method);
		
		for (AnalyteSettings t_analyteSettings : method.getAnalyteSettings()) {
			
			org.grits.toolbox.ms.annotation.structure.GlycanDatabase t_glycanDatabase = GlycanDatabaseFileHandler
			        .getGlycanDatabase(t_analyteSettings.getGlycoProteinSettings().getDatabase().getURI());
			
			for (AnalyteStructure t_glycanStructure : t_glycanDatabase.getStructures()) {
				
				if (t_glycanStructure instanceof GlycanStructure) {
					GlycanObjects glycanObjects = new GlycanObjects();
					
					glycanObjects.setGlycanID(t_glycanStructure.getId());
					glycanObjects.setGlycanGWBSequence(((GlycanStructure) t_glycanStructure).getGWBSequence());
					
					Double glycanMass = calculateGlycanMass(((GlycanStructure) t_glycanStructure).getGWBSequence(),
					        true, derivativeType);
					
					glycanObjects.setGlycanMass(glycanMass);
					
					listGlycanObjects.add(glycanObjects);
				}
			}
			
		}
		
		// Get the list of scan Precursers Information
		List<Scan> listScanPrecursers = this.generatePrecursers(gpaWizard);
		
		for (int i = 0; i < listScanPrecursers.size(); i++) {
			// System.out.println(listScanPrecursers.get(i).getPrecursor().getPrecursorMz());
		}
		
		Annotation(method, peptideSequence, adductSettings, listScanPrecursers, listGlycanObjects, annotationFolder);
		
	}
	
	public static double calculateGlycanMass(String a_strGSeq, boolean a_bMono, String t_strPerDerivType) {
		Glycan t_glycan = Glycan.fromString(a_strGSeq);
		setMassOptions(t_glycan, a_bMono, t_strPerDerivType);
		return t_glycan.computeMass();
	}
	
	/**
	 * Set mass options including derivatization, mass type<s>, reducing end</s>
	 * to Glycan
	 * 
	 * @param a_glycan
	 *            - Target glycan to set mass options
	 * @param a_bIsMono
	 *            - boolean, whether mass is monoisotopic or not
	 * @param a_strPerDerivForGRITS
	 *            - String, perderivatization type
	 */
	public static void setMassOptions(Glycan a_glycan, boolean a_bIsMono, String a_strPerDerivForGRITS) {
		MassOptions t_massOptions = new MassOptions();
		// Map the derivitasiation type
		String t_strDerivTypeForMassOption = (a_strPerDerivForGRITS
		        .equals(GlycanPreDefinedOptions.DERIVITIZATION_PERMETHYLATED)) ? MassOptions.PERMETHYLATED
		                : MassOptions.NO_DERIVATIZATION;
		t_massOptions.setDerivatization(t_strDerivTypeForMassOption);
		
		// Map the Monoisotopic
		if (a_bIsMono)
			t_massOptions.setIsotope(MassOptions.ISOTOPE_MONO);
		else
			t_massOptions.setIsotope(MassOptions.ISOTOPE_AVG);
		
		// Set IonCloud to remove default ions
		t_massOptions.ION_CLOUD = new IonCloud();
		t_massOptions.NEUTRAL_EXCHANGES = new IonCloud();
		
		// Set Reducing End as free end
		t_massOptions.setReducingEndType(ResidueDictionary.findResidueType("freeEnd"));
		
		a_glycan.setMassOptions(t_massOptions);
	}
	
	public static void Annotation(Method method, List<Peptide> peptideSequence, List<AdductSettings> adductSettings,
	        List<Scan> listScanPrecursers, List<GlycanObjects> listGlycanObjects, String annotationFolder)
	        throws JAXBException, IOException {
		
		int peptideId = 0;
		
		for (Peptide peptides : peptideSequence) {
			
			for (PeptideMass peptideMass : peptides.getMasses()) {
				
				// PeptideInfo object
				PeptideInfo peptideInfo = new PeptideInfo();
				
				String regex = "(N)\\w[TS]";
				String peptideSequenceString = peptides.getSequence();
				
				Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
				Matcher matcher = pattern.matcher(peptideSequenceString);
				
				Boolean nLinkFound = false;
				
				List<Integer> nLinkPositions = new ArrayList<Integer>();
				
				while (matcher.find()) {
					
					int position = matcher.end() - 2;
					
					nLinkPositions.add(position);
					
					nLinkFound = true;
					
				}
				
				if (nLinkFound) {
					peptideInfo.setNLinks(nLinkPositions);
				}
				
				List<GlycoPeptideConfiguration> listGlycoPeptide = new ArrayList<GlycoPeptideConfiguration>();
				
				int maxNLink = nLinkPositions.size();
				
				List<int[]> listOfGlycanIndices = nLinkIndices(maxNLink, listGlycanObjects.size() - 1);
				
				for (int k = 0; k < listOfGlycanIndices.size(); k++) {
					
					List<GlycanObjects> lAddtionalGlycans = new ArrayList<>();
					
					List<AdductInfo> listAdductInfo = new ArrayList<AdductInfo>();
					
					int[] singleIndices = listOfGlycanIndices.get(k);
					
					Double totalGlycanMass = 0.0;
					for (int t_i : singleIndices) {
						
						if (t_i == -1)
							continue;
						
						lAddtionalGlycans.add(listGlycanObjects.get(t_i));
						totalGlycanMass += listGlycanObjects.get(t_i).getGlycanMass() - 31.0189;
						
					}
					
					GlycoPeptideConfiguration glycoPeptide = new GlycoPeptideConfiguration();
					
					for (AdductSettings adducts : adductSettings) {
						
						// AdductInfo object
						AdductInfo adductInfo = new AdductInfo();
						
						// Calculate mass and m/z value
						Double totalMass = peptideMass.getPeptideMass() + adducts.getMass() + totalGlycanMass;
						
						Double mzTheoritical = totalMass / adducts.getCharge();
						
						Double accuracyMS = method.getAccuracy();
						Double delta = (mzTheoritical * accuracyMS) / 1000000;
						Double minValue = mzTheoritical - delta;
						Double maxValue = mzTheoritical + delta;
						
						// Match scans
						List<ScanMatches> listScanMatches = new ArrayList<ScanMatches>();
						
						for (Scan precur : listScanPrecursers) {
							
							Double precursorValue = precur.getPrecursor().getPrecursorMz();
							
							Double precursorIntensity = precur.getPrecursor().getPrecursorIntensity();
							
							// If precursor matched
							if (precursorValue > minValue && precursorValue < maxValue) {
								ScanMatches scanMatches = new ScanMatches();
								scanMatches.setScanNumber(precur.getScanNo());
								scanMatches.setMzValue(precursorValue);
								scanMatches.setIntensity(precursorIntensity);
								
								listScanMatches.add(scanMatches);
							}
							
						}
						
						if (!listScanMatches.isEmpty()) {
							adductInfo.setScanMatches(listScanMatches);
							adductInfo.setLabel(adducts.getLabel());
							adductInfo.setIonName(adducts.getIonName());
							adductInfo.setCharge(adducts.getCharge());
							
							adductInfo.setMass(adducts.getMass());
							
							listAdductInfo.add(adductInfo);
						}
						
					} // adducts
					
					if (!listAdductInfo.isEmpty()) {
						
						glycoPeptide.setAdductInfos(listAdductInfo);
						
						glycoPeptide.setGlycanObject(lAddtionalGlycans);
						
						listGlycoPeptide.add(glycoPeptide);
						
					}
					
				} // Glycan combo
				
				if (!listGlycoPeptide.isEmpty()) {
					peptideId = peptideId + 1;
					peptideInfo.setId(peptideId);
					peptideInfo.setSequence(peptides.getSequence());
					peptideInfo.setMass(peptideMass.getPeptideMass());
					
					peptideInfo.setAminoMassess(peptideMass.getAminoAcidMasses());
					
					peptideInfo.setGlycoPeptides(listGlycoPeptide);
					
					String fileName = Integer.toString(peptideId) + "_" + peptides.getSequence();
					
					serialize(peptideInfo, annotationFolder, fileName);
				}
				
			} // Peptide Mass
			
		} // Peptide
		
	} // Method Annotation
	
	public static List<int[]> nLinkIndices(int maxNLinks, int glycansSIZE) {
		
		if (maxNLinks == 0)
			return new ArrayList<int[]>();
		
		List<int[]> listofListOfIndex = new ArrayList<int[]>();
		
		int[] listIndices = new int[maxNLinks];
		
		for (int i = 0; i < maxNLinks; i++) {
			listIndices[i] = -1;
		}
		
		while (listIndices[maxNLinks - 1] < glycansSIZE) {
			for (int i = 0; i < maxNLinks; i++) {
				
				// Reset lower indices when the number reached to max
				
				if (listIndices[i] == glycansSIZE && i != maxNLinks - 1) {
					
					for (int j = 0; j < i + 1; j++)
						listIndices[j] = listIndices[i + 1] + 1;
					continue;
				}
				// Increment an index
				
				listIndices[i]++;
				
				// Break after increment
				
				break;
				
			}
			
			int[] listNewIndices = new int[maxNLinks];
			for (int i = 0; i < maxNLinks; i++) {
				listNewIndices[i] = listIndices[i];
			}
			listofListOfIndex.add(listNewIndices);
			
		}
		return listofListOfIndex;
	}
	
	public static int annotateSubFunction(Method method, List<Peptide> peptideSequence,
	        List<AdductSettings> adductSettings, List<Scan> listScanPrecursers, List<GlycanObjects> listGlycanObjects,
	        String annotationFolder, int peptideId, Peptide peptides, PeptideMass peptideMass, PeptideInfo peptideInfo,
	        List<GlycoPeptideConfiguration> listGlycoPeptide, List<AdductInfo> listAdductInfo,
	        List<GlycanObjects> listGlycanObjectsInfo) throws JAXBException, IOException {
		
		for (GlycanObjects glycanObjects : listGlycanObjects) {
			
			GlycoPeptideConfiguration glycoPeptide = new GlycoPeptideConfiguration();
			
			int flag = 0;
			
			for (AdductSettings adducts : adductSettings) {
				
				flag = 0;
				
				// AdductInfo object
				AdductInfo adductInfo = new AdductInfo();
				
				GlycanObjects glycanInfo = new GlycanObjects();
				
				Double mzTheoritical = (peptideMass.getPeptideMass() + adducts.getMass()) / adducts.getCharge();
				Double accuracyMS = method.getAccuracy();
				Double delta = (mzTheoritical * accuracyMS) / 1000000;
				Double minValue = mzTheoritical - delta;
				Double maxValue = mzTheoritical + delta;
				
				List<ScanMatches> listScanMatches = new ArrayList<ScanMatches>();
				
				for (Scan precur : listScanPrecursers) {
					
					Double precursorValue = precur.getPrecursor().getPrecursorMz();
					
					Double precursorIntensity = precur.getPrecursor().getPrecursorIntensity();
					
					if (precursorValue > minValue && precursorValue < maxValue) {
						
						flag = 1;
						
						ScanMatches scanMatches = new ScanMatches();
						scanMatches.setScanNumber(precur.getScanNo());
						scanMatches.setMzValue(precursorValue);
						scanMatches.setIntensity(precursorIntensity);
						
						listScanMatches.add(scanMatches);
					}
				}
				
				if (flag == 1) {
					adductInfo.setScanMatches(listScanMatches);
					adductInfo.setLabel(adducts.getLabel());
					adductInfo.setIonName(adducts.getIonName());
					adductInfo.setCharge(adducts.getCharge());
					
					adductInfo.setMass(adducts.getMass());
					
					listAdductInfo.add(adductInfo);
					
					glycanInfo.setGlycanID(glycanObjects.getGlycanID());
					glycanInfo.setGlycanGWBSequence(glycanObjects.getGlycanGWBSequence());
					glycanInfo.setGlycanMass(glycanObjects.getGlycanMass());
					// glycanInfo.setDelta(glycanObjects.getDelta());
					// glycanInfo.setAsparaginePosition(glycanObjects.getAsparaginePosition());
					
					listGlycanObjectsInfo.add(glycanInfo);
				}
			}
			
			if (flag == 1) {
				peptideId = peptideId + 1;
				peptideInfo.setId(peptideId);
				peptideInfo.setSequence(peptides.getSequence());
				peptideInfo.setMass(peptideMass.getPeptideMass());
				
				peptideInfo.setAminoMassess(peptideMass.getAminoAcidMasses());
				
				glycoPeptide.setAdductInfos(listAdductInfo);
				
				glycoPeptide.setGlycanObject(listGlycanObjectsInfo);
				
				listGlycoPeptide.add(glycoPeptide);
				
			}
			
			peptideInfo.setGlycoPeptides(listGlycoPeptide);
			
			String fileName = Integer.toString(peptideId) + "_" + peptides.getSequence();
			
			// System.out.println("Begin Serialize");
			
			serialize(peptideInfo, annotationFolder, fileName);
			
			// System.out.println("Finish Serialize");
			
		}
		
		return 1;
		
	}
	
	private int[] NSequence(String sequence) {
		int position[] = null;
		int j = 0;
		
		for (int i = 0; i < sequence.length(); i++) {
			if (sequence.charAt(i) == 'N') {
				position[j] = i;
				j++;
			}
			
		}
		
		return null;
		
	}
	
	public static void serialize(PeptideInfo peptideInfo, String annotationFolder, String fileName)
	        throws JAXBException, IOException {
		final Logger logger = Logger.getLogger(PeptideFragmentation.class);
		
		try {
			// Add context list for Filter classes
			List<Class> contextList = new ArrayList<>(Arrays.asList(FilterUtils.filterClassContext));
			contextList.add(PeptideInfo.class);
			// Create JAXB context and instantiate marshallar
			JAXBContext context = JAXBContext.newInstance(contextList.toArray(new Class[contextList.size()]));
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			String path = annotationFolder + fileName + ".xml";
			
			// Write to File
			m.marshal(peptideInfo, new File(path));
			
		} catch (JAXBException e) {
			logger.error("An error during serializing method object.", e);
		}
		
	}
	
	private List<Scan> generatePrecursers(GlycoProteinAnnotationWizard gpaWizard) {
		List<Double> precursers = new ArrayList<Double>();
		List<Scan> listScanPrecursers = new ArrayList<Scan>();
		
		int iStatus = GRITSProcessStatus.OK;
		
		for (int i = 0; i < gpaWizard.getInitial().getMsEntryList().size() && iStatus == GRITSProcessStatus.OK; i++) {
			// get the current MS Entry
			Entry msEntry = gpaWizard.getInitial().getMsEntryList().get(i);
			
			final MassSpecProperty prop = (MassSpecProperty) msEntry.getProperty();
			
			MassSpecMetaData metaData = prop.getMassSpecMetaData();
			
			MSPropertyDataFile dataFile = gpaWizard.getInitial().getFileMap().get(msEntry.getDisplayName());
			
			String workspaceLocation = PropertyHandler.getVariable("workspace_location");
			String projectName = DataModelSearch.findParentByType(msEntry, ProjectProperty.TYPE).getDisplayName();
			String pathToFile = workspaceLocation + projectName + File.separator + MassSpecProperty.getFoldername();
			
			MSFile file = dataFile.getMSFileWithReader(pathToFile, metaData.getMsExperimentType());
			
			if (file.getReader() instanceof IMSAnnotationFileReader) {
				List<Scan> scans = ((IMSAnnotationFileReader) file.getReader()).readMSFile(file);
				
				for (int j = 0; j < scans.size(); j++) {
					Scan scan = scans.get(j);
					
					if (scan.getMsLevel() != 2) {
						continue;
						
					}
					
					// scan.getPrecursor().getMz();
					
					if (scan.getPrecursor() != null && scan.getPrecursor().getIsPrecursor()) {
						// precursers.add(scan.getPrecursor().getPrecursorMz());
						
						listScanPrecursers.add(scan);
						
						// System.out.println("getPrecursorMz = " +
						// scan.getPrecursor().getPrecursorMz() + " "
						// + "getMZ = " + scan.getPrecursor().getMz());
					}
					
				}
				
			}
		}
		
		return listScanPrecursers;
	}
	
	private List<AdductSettings> generateAdductSettings(Method method) {
		
		List<AdductSettings> adductSettings = new ArrayList<AdductSettings>();
		
		List<IonSettings> listMolecules = new ArrayList<IonSettings>();
		
		int maxCharge = method.getMaxIonCount();
		
		List<ArrayList<AdductSettings>> listAdductSettings = new ArrayList<ArrayList<AdductSettings>>();
		
		for (IonSettings ionSettings : method.getIons()) {
			// String name = ionSettings.getName();
			// String label = ionSettings.getLabel();
			// Double mass = ionSettings.getMass();
			// int charge = ionSettings.getCharge();
			// Boolean polarity = ionSettings.getPolarity();
			
			listMolecules.add(ionSettings);
			
			int ionSize = ionSettings.getCounts().get(ionSettings.getCounts().size() - 1);
			
			// System.out.println("Ion size = " + ionSize);
			
			// int ionsize3 = method.getIons().size();
			// System.out.println("method get ion elements = " +
			// ionSettings.getCounts());
			
			List<AdductSettings> listTempAdductSettings = new ArrayList<AdductSettings>();
			
			for (int j = ionSettings.getCharge(); j <= ionSize; j = j + ionSettings.getCharge()) {
				
				int tempJ = j / ionSettings.getCharge();
				
				if (j <= maxCharge) {
					
					AdductSettings singleAdductSettings = new AdductSettings();
					
					List<IonCombination> listIonCombinations = new ArrayList<IonCombination>();
					
					IonCombination singleIonCombination = new IonCombination(ionSettings);
					singleIonCombination.setCount(j);
					
					listIonCombinations.add(singleIonCombination);
					
					String tempLabel = "";
					if (tempJ == 1) {
						tempLabel = ionSettings.getLabel();
					} else {
						tempLabel = Integer.toString(tempJ) + ionSettings.getLabel();
					}
					
					singleAdductSettings.setLabel(tempLabel);
					singleAdductSettings.setIonName(ionSettings.getLabel());
					singleAdductSettings.setCharge(j);
					singleAdductSettings.setMass(tempJ * ionSettings.getMass());
					singleAdductSettings.setIonCombinations(listIonCombinations);
					
					adductSettings.add(singleAdductSettings);
					
					listTempAdductSettings.add(singleAdductSettings);
				}
			}
			
			listAdductSettings.add((ArrayList<AdductSettings>) listTempAdductSettings);
			
		}
		
		if (listMolecules.size() >= 2) {
			
			List<AdductSettings> listTempAdductSettings = new ArrayList<AdductSettings>();
			
			int listLength = listAdductSettings.size();
			
			// I'th Loop to run over list of Adduct Settings
			for (int i = 0; i < listLength - 1; i++) {
				
				// J'th Loop to run over list of Adduct Settings
				for (int j = i + 1; j < listLength; j++) {
					
					int iLength = listAdductSettings.get(i).size();
					int jLength = listAdductSettings.get(j).size();
					
					// M'th Loop to run over ions in I'th element
					for (int m = 0; m < iLength; m++) {
						
						// Nth Loop to run over ions in J'th element
						for (int n = 0; n < jLength; n++) {
							
							int totalCount = listAdductSettings.get(i).get(m).getIonCombinations().get(0).getCount()
							        + listAdductSettings.get(j).get(n).getIonCombinations().get(0).getCount();
							
							if (totalCount <= maxCharge) {
								
								AdductSettings singleAdductSettings = new AdductSettings();
								
								List<IonCombination> listIonCombinations = new ArrayList<IonCombination>();
								
								IonCombination iSingleIonCombination = new IonCombination(listMolecules.get(i));
								iSingleIonCombination.setCount(
								        listAdductSettings.get(i).get(m).getIonCombinations().get(0).getCount());
								IonCombination jSingleIonCombination = new IonCombination(listMolecules.get(i));
								jSingleIonCombination.setCount(
								        listAdductSettings.get(j).get(n).getIonCombinations().get(0).getCount());
								
								listIonCombinations.add(iSingleIonCombination);
								listIonCombinations.add(jSingleIonCombination);
								
								int tempIonCharge = listAdductSettings.get(i).get(m).getCharge()
								        + listAdductSettings.get(j).get(n).getCharge();
								
								String tempIonLabel = listAdductSettings.get(i).get(m).getLabel() + "+";
								tempIonLabel = tempIonLabel + listAdductSettings.get(j).get(n).getLabel();
								
								String tempIonName = listAdductSettings.get(i).get(m).getIonName() + "+";
								tempIonName = tempIonName + listAdductSettings.get(j).get(n).getIonName();
								
								Double tempIonMass = listAdductSettings.get(i).get(m).getMass()
								        + listAdductSettings.get(j).get(n).getMass();
								
								singleAdductSettings.setLabel(tempIonLabel);
								singleAdductSettings.setIonName(tempIonName);
								singleAdductSettings.setCharge(tempIonCharge);
								singleAdductSettings.setMass(tempIonMass);
								singleAdductSettings.setIonCombinations(listIonCombinations);
								
								// System.out.print(listAdductSettings.get(i).get(m).getLabel());
								// System.out.print(" ");
								// System.out
								// .print(listAdductSettings.get(i).get(m).getIonCombinations().get(0).getCount());
								// System.out.print(" ");
								// System.out.print(listAdductSettings.get(j).get(n).getLabel());
								// System.out.print(" ");
								// System.out
								// .print(listAdductSettings.get(j).get(n).getIonCombinations().get(0).getCount());
								// System.out.println();
								
								adductSettings.add(singleAdductSettings);
								
								listTempAdductSettings.add(singleAdductSettings);
								
							}
							
						}
						
					}
				}
			}
			
			listAdductSettings.add((ArrayList<AdductSettings>) listTempAdductSettings);
			
		}
		
		return adductSettings;
	}
	
	private List<Peptide> generatePeptide(Method method) {
		
		List<Peptide> peptideSequence = new ArrayList<Peptide>();
		
		List<String> stringPeptideSequence = new ArrayList<String>();
		
		String proteinSequence = method.getAnalyteSettings().get(0).getGlycoProteinSettings().getProteinSequence();
		
		String derivativeType = method.getAnalyteSettings().get(0).getGlycoProteinSettings().getPerDerivatisationType();
		String nonPermethylated = "Normal";
		String permethylated = "Permethylated";
		boolean nTerminalElimination = method.getAnalyteSettings().get(0).getGlycoProteinSettings()
		        .getNTerminalElimination();
		
		String enzymeName = method.getAnalyteSettings().get(0).getGlycoProteinSettings().getEnzyme();
		String trypsin = "Trypsin";
		String pronase = "Pronase";
		int flag;
		
		proteinSequence = proteinSequence.replaceAll("\\n", "");
		proteinSequence = proteinSequence.replaceAll("\\s", "");
		
		proteinSequence = proteinSequence.toUpperCase();
		
		// System.out.println("Protein Sequence : " + proteinSequence);
		
		// System.out.println("Derivative Type : " + derivativeType);
		
		// System.out.println("Enzyme Name : " + enzymeName);
		
		if (enzymeName.equals(trypsin)) {
			// System.out.println("Its Trypsin");
			stringPeptideSequence = ProteinToPeptide.getPeptideSequenceTrypsin(proteinSequence);
		}
		
		if (enzymeName.equals(pronase)) {
			int min = method.getAnalyteSettings().get(0).getGlycoProteinSettings().getPronaseMin();
			int max = method.getAnalyteSettings().get(0).getGlycoProteinSettings().getPronaseMax();
			// System.out.println("Its Pronase");
			stringPeptideSequence = ProteinToPeptide.getPeptideSequencePronase(proteinSequence, min, max);
		}
		
		ArrayList<ArrayList<ArrayList<Double>>> multipleArrayList = new ArrayList<ArrayList<ArrayList<Double>>>();
		
		if (derivativeType.equals(permethylated)) {
			flag = 1;
		} else {
			flag = 0;
		}
		
		multipleArrayList = ProteinToPeptide.getMultipleListResult(stringPeptideSequence, flag);
		
		// For each Peptide sequence add all the list of masses
		for (int i = 0; i < stringPeptideSequence.size(); i++) {
			
			Peptide singlePeptide = new Peptide();
			
			List<PeptideMass> listPeptideMass = new ArrayList<PeptideMass>();
			
			// System.out.println(peptideSequence.get(i));
			
			for (int j = 0; j < multipleArrayList.get(i).size(); j++) {
				
				PeptideMass singlePeptideMass = new PeptideMass();
				
				List<AminoAcidMass> listAcidMass = new ArrayList<AminoAcidMass>();
				
				Double totalMassOfPeptide = 0.00;
				
				for (int k = 0; k < multipleArrayList.get(i).get(j).size(); k++) {
					
					AminoAcidMass acidMass = new AminoAcidMass();
					
					String aCharacter = String.valueOf(stringPeptideSequence.get(i).charAt(k));
					
					// System.out.println(aCharacter + " " +
					// multipleArrayList.get(i).get(j).get(k));
					
					acidMass.setMass(multipleArrayList.get(i).get(j).get(k));
					acidMass.setAminoAcidChar(aCharacter);
					
					listAcidMass.add(acidMass);
					
					totalMassOfPeptide += multipleArrayList.get(i).get(j).get(k);
					
				}
				
				if (flag == 0) {
					totalMassOfPeptide = totalMassOfPeptide + 18.0105646;
				} else if (flag == 1) {
					if (nTerminalElimination) {
						totalMassOfPeptide = totalMassOfPeptide + 18.0105646 + 14.01565 - 1.00794 - 45.05785;
					} else {
						totalMassOfPeptide = totalMassOfPeptide + 18.0105646 + 14.01565 - 1.00794;
					}
				}
				
				singlePeptideMass.setId(j);
				singlePeptideMass.setPeptideMass(totalMassOfPeptide);
				singlePeptideMass.setAminoAcidMasses(listAcidMass);
				
				// listPeptideMass.add(j, singlePeptideMass);
				listPeptideMass.add(singlePeptideMass);
				
				// System.out.println(singlePeptideMass);
				
			}
			
			singlePeptide.setId(i);
			singlePeptide.setSequence(stringPeptideSequence.get(i));
			singlePeptide.setMasses(listPeptideMass);
			
			peptideSequence.add(singlePeptide);
			
		}
		
		return peptideSequence;
	}
	
	private void updateDatabaseReferences(Method method) {
		for (AnalyteSettings aSettings : method.getAnalyteSettings()) {
			org.grits.toolbox.ms.om.data.GlycanDatabase glycanDatabase = aSettings.getGlycoProteinSettings()
			        .getDatabase();
			if (glycanDatabase.getURI().indexOf(File.separator) == -1)
				try {
					glycanDatabase.setURI(DatabaseUtils.getDatabasePath() + File.separator + glycanDatabase.getURI());
				} catch (IOException e) {
					logger.error("Database path cannot be determined", e);
				}
		}
	}
	
}
