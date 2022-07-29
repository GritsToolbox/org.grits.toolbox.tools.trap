package org.grits.toolbox.tools.trap.handler;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Named;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.grits.toolbox.core.utilShare.ErrorUtils;
import org.grits.toolbox.ms.om.data.Method;
import org.grits.toolbox.tools.trap.annotation.PeptideFragmentation;
import org.grits.toolbox.tools.trap.util.ExcelReporting;
import org.grits.toolbox.tools.trap.wizard.GlycoProteinAnnotationWizard;
import org.grits.toolbox.util.structure.glycan.util.FilterUtils;
import org.grits.toolbox.widgets.tools.GRITSProcessStatus;

/**
 * 
 * @author Aravind Kalimurthy
 *
 */

public class GlycoProteinHandler {
	
	private static final Logger logger = Logger.getLogger(GlycoProteinHandler.class);
	
	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell) {
		logger.debug("BEGIN GlycoProteinWizardHandler");
		
		String annotationFolder = "/Users/aravind/Desktop/annotation/";
		
		GlycoProteinAnnotationWizard gpaWizard = new GlycoProteinAnnotationWizard();
		WizardDialog dialog = new WizardDialog(shell, gpaWizard);
		
		int iStatus = GRITSProcessStatus.OK;
		
		if (iStatus == 1) {
			
			try {
				int dialogstatis = dialog.open();
				
				if (dialogstatis == 0) {
					
					Instant start = Instant.now();
					
					serialize(gpaWizard.getMethod());
					
					Method classMethod = gpaWizard.getMethod();
					
					// Creation of XML reporting class
					new PeptideFragmentation(gpaWizard, classMethod, annotationFolder);
					
					Instant end = Instant.now();
					
					Duration timeElapsed = Duration.between(start, end);
					
					System.out
					        .println("Time taken for XML Reporting Class: " + timeElapsed.toMillis() + " milliseconds");
					
					Instant startExcel = Instant.now();
					
					// Creation of Excel reporting class
					new ExcelReporting(annotationFolder, "annotationReport", gpaWizard);
					
					Instant endExcel = Instant.now();
					
					Duration timeElapsedExcel = Duration.between(startExcel, endExcel);
					
					System.out.println(
					        "Time taken for Excel Reporting Class: " + timeElapsedExcel.toMillis() + " milliseconds");
					
				}
				
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				ErrorUtils.createErrorMessageBox(shell, "Exception", e);
			}
		}
		logger.debug("END GlycoProteinWizardHandler");
	}
	
	private void serialize(Method method) {
		// TODO: Serialize the Method to XML
		try {
			// Add context list for Filter classes
			List<Class> contextList = new ArrayList<>(Arrays.asList(FilterUtils.filterClassContext));
			contextList.add(Method.class);
			// Create JAXB context and instantiate marshallar
			JAXBContext context = JAXBContext.newInstance(contextList.toArray(new Class[contextList.size()]));
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			
			// Write to File
			m.marshal(method, new File("/Users/aravind/Desktop/method.xml"));
			
		} catch (JAXBException e) {
			logger.error("An error during serializing method object.", e);
		}
		
	}
	
}
