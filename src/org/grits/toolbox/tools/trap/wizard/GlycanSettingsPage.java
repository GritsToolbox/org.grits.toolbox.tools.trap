package org.grits.toolbox.tools.trap.wizard;

import org.eclipse.swt.widgets.Composite;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.GlycanSettingsForm;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.MSGlycanAnnotationWizard;
import org.grits.toolbox.ms.om.data.Method;
import org.grits.toolbox.util.structure.glycan.filter.om.FiltersLibrary;

public class GlycanSettingsPage extends GlycanSettingsForm {
	
	public GlycanSettingsPage(Method a_method, MSGlycanAnnotationWizard a_wizard, FiltersLibrary a_filterLibrary) {
		super(a_method, a_wizard, a_filterLibrary);
	}
	
	@Override
	protected void addDatabaseSettings(Composite container) {
		
	}
	
	@Override
	protected boolean databaseValid() {
		return true;
	}
	
}
