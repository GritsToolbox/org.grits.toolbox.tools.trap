package org.grits.toolbox.tools.trap.handler;

import javax.inject.Named;

import org.apache.log4j.Logger;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.MApplication;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.e4.ui.workbench.modeling.EModelService;
import org.eclipse.e4.ui.workbench.modeling.EPartService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.grits.toolbox.tools.trap.dialog.SpectraFilteringDialog;
import org.grits.toolbox.tools.trap.om.SpectrumFilter;
import org.grits.toolbox.tools.trap.util.SpectrumProcessor;

public class SpectraFilteringHandler {
	
	private static final Logger logger = Logger.getLogger(SpectraFilteringHandler.class);
	
	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell a_shell, EPartService a_partService,
	        EModelService a_modelService, MApplication a_application) {
		logger.info("Started: Spectra Filtering options for mzXML file.");
		try {
			// create the dialog
			SpectraFilteringDialog newDialog = new SpectraFilteringDialog(Display.getCurrent().getActiveShell());
			// open the dialog
			if (newDialog.open() == Window.OK) {
				
				SpectrumFilter spectrumFilter = newDialog.getFiler();
				
				SpectrumProcessor processor = new SpectrumProcessor();
				
				processor.start(spectrumFilter);
				
			}
		} catch (Exception e) {
			logger.fatal("Error starting Spectra Filtering options: " + e.getMessage(), e);
			MessageDialog.openError(a_shell, "Error starting Spectra Filtering options",
			        "Unable to run Spectra Filtering Handler due to an error:\n\n" + e.getMessage());
		}
		logger.info("Finshed Execution:  Spectra Filtering options.");
	}
	
}
