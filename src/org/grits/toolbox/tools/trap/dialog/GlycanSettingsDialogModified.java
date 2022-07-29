package org.grits.toolbox.tools.trap.dialog;

//package org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.grits.toolbox.core.datamodel.UnsupportedVersionException;
import org.grits.toolbox.core.utilShare.FileSelectionAdapter;
import org.grits.toolbox.core.utilShare.TextFieldUtils;
import org.grits.toolbox.entry.ms.annotation.glycan.preference.MSGlycanFilterCateogoryPreference;
import org.grits.toolbox.entry.ms.annotation.glycan.preference.MSGlycanFilterPreference;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.utils.DatabaseUtils;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.utils.GlycanStructureDatabase;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.utils.GlycanStructureDatabaseIndex;
import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.Filtering;
import org.grits.toolbox.ms.annotation.structure.GlycanPreDefinedOptions;
import org.grits.toolbox.ms.annotation.structure.StructureHandlerFileSystem;
import org.grits.toolbox.ms.om.data.GlycanDatabase;
import org.grits.toolbox.ms.om.data.GlycanFilter;
import org.grits.toolbox.ms.om.data.GlycanSettings;
import org.grits.toolbox.util.structure.glycan.filter.om.Category;
import org.grits.toolbox.util.structure.glycan.filter.om.FiltersLibrary;

public class GlycanSettingsDialogModified extends TitleAreaDialog {
	
	private static final Logger							logger					= Logger
	        .getLogger(GlycanSettingsDialogModified.class);
	private final static int							OTHER_REDEND_MAX_LENGTH	= 15;
	
	private Text										txtRedEndName;
	private Text										txtRedEndMass;
	private Combo										cmbDatabases;
	private Text										txtNewDb;
	private Button										btnBrowse;
	
	private Category									preferredFilterCategory;
	private ArrayList<String>							databases;
	private HashMap<String, GlycanStructureDatabase>	m_databaseIndex			= new HashMap<String, GlycanStructureDatabase>();
	boolean												editingMode				= false;
	private GlycanDatabase								glycanDatabase;
	
	public GlycanSettingsDialogModified(Shell parentShell, FiltersLibrary filterLibrary) {
		super(parentShell);
		loadFilterPreferences();
		new Filtering(getShell(), filterLibrary, preferredFilterCategory, null);
	}
	
	public GlycanSettingsDialogModified(Shell parentShell, FiltersLibrary filterLibrary, GlycanSettings gSettings) {
		super(parentShell);
		loadFilterPreferences();
		new Filtering(getShell(), filterLibrary, preferredFilterCategory, null);
		this.editingMode = true;
	}
	
	@Override
	protected boolean isResizable() {
		return true;
	}
	
	private void loadFilterPreferences() {
		try {
			MSGlycanFilterPreference preferences = MSGlycanFilterPreference
			        .getMSGlycanFilterPreferences(MSGlycanFilterPreference.getPreferenceEntity());
			if (preferences != null)
				preferences.getFilterSettings();
			MSGlycanFilterCateogoryPreference categoryPreferences = MSGlycanFilterCateogoryPreference
			        .getMSGlycanFilterCategoryPreferences(MSGlycanFilterCateogoryPreference.getPreferenceEntity());
			if (categoryPreferences != null)
				preferredFilterCategory = categoryPreferences.getCategoryPreference();
		} catch (UnsupportedVersionException e) {
			logger.error("Cannot load filter preference");
		}
	}
	
	protected Label setMandatoryLabel(Label lable) {
		lable.setText(lable.getText() + "*");
		lable.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
		return lable;
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		setTitle("Database Settings");
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayout(new GridLayout(4, false));
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		addDatabaseControls(container);
		
		return container;
	}
	
	private void addDatabaseControls(Composite container) {
		databases = new ArrayList<String>();
		try {
			GlycanStructureDatabaseIndex t_databaseIndex = DatabaseUtils.getGelatoDatabases();
			for (GlycanStructureDatabase t_db : t_databaseIndex.getDatabase()) {
				String t_nameString = t_db.getName() + " - " + t_db.getNumberOfStructures().toString() + " glycans";
				databases.add(t_nameString);
				this.m_databaseIndex.put(t_nameString, t_db);
			}
			DatabaseUtils.getDatabasePath();
		} catch (IOException e) {
			logger.error("Unable to find GELATO database index", e);
		} catch (JAXBException e) {
			logger.error("XML format problem in GELATO database index", e);
		}
		databases.add(GlycanPreDefinedOptions.OTHER);
		String[] cmbDBs = databases.toArray(new String[databases.size()]);
		int inx = 0;
		Label lblDatabase = new Label(container, SWT.NONE);
		lblDatabase.setText("Database");
		lblDatabase = setMandatoryLabel(lblDatabase);
		
		cmbDatabases = new Combo(container, SWT.NONE);
		
		GridData gd_cmbDatabases = new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1);
		gd_cmbDatabases.widthHint = 131;
		cmbDatabases.setLayoutData(gd_cmbDatabases);
		cmbDatabases.setItems(cmbDBs);
		cmbDatabases.select(inx);
		new Label(container, SWT.NONE);
		
		txtNewDb = new Text(container, SWT.BORDER);
		
		txtNewDb.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		btnBrowse = new Button(container, SWT.PUSH);
		if (!cmbDatabases.getItem(cmbDatabases.getSelectionIndex()).equals(GlycanPreDefinedOptions.OTHER)) {
			btnBrowse.setEnabled(false);
			txtNewDb.setEnabled(false);
		}
		btnBrowse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		btnBrowse.setText("Browse");
		FileSelectionAdapter rawFileBrowserSelectionAdapter = new FileSelectionAdapter();
		rawFileBrowserSelectionAdapter.setShell(container.getShell());
		rawFileBrowserSelectionAdapter.setText(txtNewDb);
		btnBrowse.addSelectionListener(rawFileBrowserSelectionAdapter);
		
		new Label(container, SWT.NONE);
		
		txtNewDb.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				getButton(OK).setEnabled(validateInput());
			}
		});
		
		cmbDatabases.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (cmbDatabases.getItem(cmbDatabases.getSelectionIndex()).equals("other")) {
					txtNewDb.setEnabled(true);
					btnBrowse.setEnabled(true);
					getButton(OK).setEnabled(validateInput());
				} else {
					txtNewDb.setEnabled(false);
					btnBrowse.setEnabled(false);
					getButton(OK).setEnabled(validateInput());
				}
			}
			
		});
	}
	
	public boolean validateInput() {
		if (txtRedEndName != null && txtRedEndName.getEnabled()) {
			if (TextFieldUtils.isEmpty(txtRedEndName)) {
				setErrorMessage("Please enter a valid name for the reducing end.");
				return false;
			} else if (txtRedEndName.getText().trim().length() > OTHER_REDEND_MAX_LENGTH) {
				setErrorMessage("The maximum length for \"Other\" reducing end is " + OTHER_REDEND_MAX_LENGTH + ".");
				return false;
			}
		}
		if (txtRedEndMass != null && txtRedEndMass.getEnabled()) {
			if (TextFieldUtils.isEmpty(txtRedEndMass) || !TextFieldUtils.isDouble(txtRedEndMass)) {
				setErrorMessage("Please enter a valid mass for the reducing end ");
				return false;
			}
		}
		if (txtNewDb != null && txtNewDb.getEnabled()) {
			if (TextFieldUtils.isEmpty(txtNewDb) || !isValidDatabase(txtNewDb.getText())) {
				setErrorMessage("Please select a valid database");
				return false;
			}
		}
		
		setErrorMessage(null);
		return true;
		
	}
	
	private boolean isValidDatabase(String dbName) {
		try {
			GlycanFilter filter = new GlycanFilter();
			if (dbName.indexOf(File.separator) == -1) { // does not have the
			                                            // full path
				try {
					filter.setDatabase(DatabaseUtils.getDatabasePath() + File.separator + dbName);
				} catch (IOException e) {
					logger.error("Database path cannot be determined", e);
				}
			} else
				filter.setDatabase(dbName); // if "other" is selected, the
				                            // dbName will contain the full path
				                            // to the file
			StructureHandlerFileSystem handler = new StructureHandlerFileSystem();
			if (handler.getStructures(filter) == null)
				return false;
			else {
				filter.getVersion();
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}
	
	@Override
	protected void okPressed() {
		if (validateInput()) {
			saveGlycanDatabases();
			super.okPressed();
		}
	}
	
	private void saveGlycanDatabases() {
		
		String sSelectedDb = cmbDatabases.getText();
		
		GlycanStructureDatabase selectedDatabase = this.m_databaseIndex.get(sSelectedDb);
		
		GlycanDatabase database = new GlycanDatabase();
		
		if (selectedDatabase == null) {
			
			String glycanDatabaseName = txtNewDb.getText();
			
			int startIndex = glycanDatabaseName.lastIndexOf(File.separator);
			int endIndex = glycanDatabaseName.lastIndexOf(".");
			
			String databaseName = glycanDatabaseName.substring(startIndex + 1, endIndex);
			
			database.setDatabase(databaseName);
			database.setURI(txtNewDb.getText());
		} else {
			database.setDatabase(selectedDatabase.getName());
			database.setURI(selectedDatabase.getFileName());
		}
		
		this.glycanDatabase = database;
	}
	
	public GlycanDatabase getGlycanDatabse() {
		return glycanDatabase;
	}
}
