package org.grits.toolbox.tools.trap.wizard;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Composite;
import org.grits.toolbox.entry.ms.annotation.glycan.preference.MSGlycanAnnotationPreference;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.AddAdductsForm;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.AddNeutralLossForm;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.GeneralInformationMulti;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.GlycanSettingsTableWithActions;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.MSGlycanAnnotationWizard;
import org.grits.toolbox.ms.om.data.Method;
import org.grits.toolbox.util.structure.glycan.util.FilterUtils;

/**
 * 
 * @author Aravind Kalimurthy
 *
 */

public class GlycoProteinAnnotationWizard extends MSGlycanAnnotationWizard {
	
	// log4J Logger
	private static final Logger		logger					= Logger.getLogger(GlycoProteinAnnotationWizard.class);
	
	protected ProteinToPeptideForm	ProteinToPeptideForm	= null;
	
	public GlycoProteinAnnotationWizard() {
		setWindowTitle("MS Annotation");
		method = new Method();
		try {
			filterLibrary = FilterUtils.readFilters(getDefaultFiltersPath());
		} catch (UnsupportedEncodingException e) {
			logger.error("Error getting the filters", e);
		} catch (FileNotFoundException e) {
			logger.error("Error getting the filters", e);
		} catch (JAXBException e) {
			logger.error("Error getting the filters", e);
		}
	}
	
	@Override
	public void addPages() {
		
		this.initial = new GeneralInformationMulti(this.msEntries, "New MS GlycoProtein Annotation",
		        "Identify GlycoProtein.", "DANGO");
		
		this.glycanSettingsForm = new GlycanSettingsPage(this.method, this, this.filterLibrary);
		
		this.ProteinToPeptideForm = new ProteinToPeptideForm(this.method, this, this.filterLibrary);
		
		this.ionSettingsForm = new AddAdductsForm(this.method);
		
		this.neutralLossForm = new AddNeutralLossForm(this.method);
		
		addPage(this.initial);
		
		addPage(this.glycanSettingsForm);
		
		addPage(this.ProteinToPeptideForm);
		
		addPage(this.ionSettingsForm);
		
		addPage(this.neutralLossForm);
	}
	
	// @Override
	protected GlycanSettingsTableWithActions getSettingsTable(Composite container) {
		return null;
	}
	
	@Override
	public void setPreferences(MSGlycanAnnotationPreference preferences) {
		this.preferences = preferences;
		
		glycanSettingsForm.updateControlsFromPreferences();
		
		ionSettingsForm.updateControlsFromPreferences();
		
		neutralLossForm.updateControlsFromPreferences();
		
	}
	
	@Override
	public boolean canFinish() {
		if (this.glycanSettingsForm.canFlipToNextPage())
			this.glycanSettingsForm.save();
		else
			return false;
		
		if (this.ProteinToPeptideForm.canFlipToNextPage())
			this.ProteinToPeptideForm.save();
		else
			return false;
		
		if (this.ionSettingsForm.canFlipToNextPage())
			this.ionSettingsForm.save();
		else
			return false;
		
		if (this.neutralLossForm.isPageComplete())
			this.neutralLossForm.save();
		else
			return false;
		
		return this.ionSettingsForm.isPageComplete() && this.initial.canFlipToNextPage()
		        && this.glycanSettingsForm.canFlipToNextPage() && this.neutralLossForm.isPageComplete();
	}
	
	@Override
	public boolean performFinish() {
		boolean bCanFinish = canFinish();
		if (bCanFinish) { // update prefs
		}
		return bCanFinish;
	}
}
