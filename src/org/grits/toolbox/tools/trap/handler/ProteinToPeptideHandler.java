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
import org.grits.toolbox.tools.trap.dialog.ProteinToPeptideDialog;

/**
 * 
 * @author Aravind Kalimurthy
 *
 */

public class ProteinToPeptideHandler {
	
	private static final Logger logger = Logger.getLogger(ProteinToPeptideHandler.class);
	
	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell a_shell, EPartService a_partService,
	        EModelService a_modelService, MApplication a_application) {
		logger.info("Started: Trap Handler for Protein to Peptide Mass Calcualator.");
		try {
			// create the dialog
			ProteinToPeptideDialog newDialog = new ProteinToPeptideDialog(Display.getCurrent().getActiveShell());
			// open the dialog
			if (newDialog.open() == Window.OK) {
				
			}
		} catch (Exception e) {
			logger.fatal("Error starting TrapHandler: " + e.getMessage(), e);
			MessageDialog.openError(a_shell, "Error starting Trap Handler",
			        "Unable to run Trap Handler due to an error:\n\n" + e.getMessage());
		}
		logger.info("Finshed Execution:  Trap handler.");
	}
}
