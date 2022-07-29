package org.grits.toolbox.tools.trap.wizard;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.wizard.WizardPage;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.grits.toolbox.core.datamodel.Entry;
import org.grits.toolbox.core.utilShare.TextFieldUtils;
import org.grits.toolbox.ms.annotation.structure.GlycanPreDefinedOptions;
import org.grits.toolbox.ms.om.data.AnalyteSettings;
import org.grits.toolbox.ms.om.data.GlycanDatabase;
import org.grits.toolbox.ms.om.data.GlycoProteinSettings;
//import org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard.GlycanSettingsTableWithActions;
import org.grits.toolbox.ms.om.data.Method;
import org.grits.toolbox.util.structure.glycan.filter.om.FiltersLibrary;

public class ProteinToPeptideForm extends WizardPage implements IPropertyChangeListener {
	private static final Logger					logger					= Logger.getLogger(ProteinToPeptideForm.class);
	Method										method;
	private Text								txtProteinSequence;
	private Text								txtPronaseMin;
	private Text								txtPronaseMax;
	
	public static final String					ENZYME_LABEL			= "Trypsin";
	// public static final String ENZYME_LABEL2 = "Pronase";
	// public static final String[] ENZYME_TYPES = new String[] { ENZYME_LABEL,
	// ENZYME_LABEL2 };
	public static final String[]				ENZYME_TYPES			= new String[] { ENZYME_LABEL };
	
	protected Shell								shell					= null;
	protected String							fileName				= null;
	protected Entry								msEntry					= null;
	protected String							sOutputFile				= null;
	
	private boolean								nTerminalElimination	= false;
	
	private Combo								cmbEnzyme;
	private Combo								cmbDerivative;
	
	int											pronase					= -1;
	int											others					= -1;
	int											trypsin					= -1;
	int											permethylated			= -1;
	
	private GlycanSettingsTableWithActionCopy	databaseSettings;
	
	private FiltersLibrary						filterLibrary;
	
	private GlycoProteinAnnotationWizard		myWizard;
	
	private boolean								readyToFinish			= false;
	
	private ModifyListener						txtProteinSequenceListner;
	
	ListViewer									listViewer;
	
	private String								sProteinSequence;
	private String								sEnzyme;
	private String								sDerivativeType;
	private Integer								iMin;
	private Integer								iMax;
	
	protected ProteinToPeptideForm(Method a_method, GlycoProteinAnnotationWizard a_glycoProteinAnnotationWizard,
	        FiltersLibrary a_filterLibrary) {
		super("ProteinToPeptideAnnotation");
		this.method = a_method;
		this.myWizard = a_glycoProteinAnnotationWizard;
		this.filterLibrary = a_filterLibrary;
		setTitle("Protein To Peptide Mass");
		setDescription("Describe general protein settings");
	}
	
	@Override
	public void createControl(Composite parent) {
		
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(5, false);
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 10;
		parent.setSize(100, 100);
		container.setLayout(layout);
		
		Label proteinSequenceLabel = new Label(container, SWT.NONE);
		proteinSequenceLabel.setText("Enter Protein Sequence");
		
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		GridData dataProteinSequence = new GridData(GridData.FILL_BOTH);
		dataProteinSequence.minimumHeight = 80;
		dataProteinSequence.grabExcessHorizontalSpace = true;
		dataProteinSequence.horizontalSpan = 5;
		txtProteinSequence = new Text(container, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		txtProteinSequence.setLayoutData(dataProteinSequence);
		
		txtProteinSequenceListner = new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (validateInput()) {
					canFlipToNextPage();
					getWizard().getContainer().updateButtons();
				} else {
					readyToFinish = false;
					canFlipToNextPage();
					getWizard().getContainer().updateButtons();
				}
				
			}
		};
		txtProteinSequence.addModifyListener(txtProteinSequenceListner);
		
		Label enzymeLabel = new Label(container, SWT.BORDER);
		enzymeLabel.setText("Select the Enzyme"); // create new layout data
		GridData dataEnzyme = new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1);
		enzymeLabel.setLayoutData(dataEnzyme);
		
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Button radioButtons = new Button(container, SWT.RADIO);
		radioButtons.setText("Pronase");
		
		GridData dataradioButton0 = new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1);
		dataradioButton0.horizontalSpan = 1;
		radioButtons.setLayoutData(dataradioButton0);
		
		Label minLabel = new Label(container, SWT.BORDER);
		minLabel.setText("Min");
		GridData dataMin = new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1);
		minLabel.setLayoutData(dataMin);
		minLabel.setVisible(false);
		
		txtPronaseMin = new Text(container, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		txtPronaseMin.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1));
		txtPronaseMin.setVisible(false);
		
		Label maxLabel = new Label(container, SWT.RIGHT);
		maxLabel.setText("Max");
		GridData dataMax = new GridData(SWT.RIGHT, SWT.TOP, true, true, 1, 1);
		maxLabel.setLayoutData(dataMax);
		maxLabel.setVisible(false);
		
		txtPronaseMax = new Text(container, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		txtPronaseMax.setLayoutData(new GridData(SWT.MULTI, SWT.TOP, true, true, 1, 1));
		txtPronaseMax.setVisible(false);
		
		Button radioButtons2 = new Button(container, SWT.RADIO);
		radioButtons2.setText("Others");
		radioButtons2.setSelection(true); // Default selection
		
		GridData dataradioButton1 = new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1);
		dataradioButton0.horizontalSpan = 1;
		radioButtons2.setLayoutData(dataradioButton1);
		
		cmbEnzyme = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
		String itemList[] = { "Trypsin" };
		cmbEnzyme.setItems(itemList);
		GridData radioEnzyme = new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1);
		radioEnzyme.horizontalSpan = 1;
		cmbEnzyme.setLayoutData(radioEnzyme);
		cmbEnzyme.setVisible(false);
		cmbEnzyme.select(0); // Select default first item.
		trypsin = 1;
		cmbEnzyme.setVisible(true);
		
		cmbEnzyme.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (cmbEnzyme.getText().equals("Trypsin")) {
					trypsin = 1;
				}
			}
		});
		
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		radioButtons.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button source = (Button) e.getSource();
				if (source.getSelection()) {
					pronase = 1;
					others = -1;
					trypsin = -1;
					minLabel.setVisible(true);
					maxLabel.setVisible(true);
					txtPronaseMin.setVisible(true);
					txtPronaseMax.setVisible(true);
					cmbEnzyme.setVisible(false);
				}
			}
			
		});
		
		radioButtons2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Button source = (Button) e.getSource();
				if (source.getSelection()) {
					pronase = -1;
					others = 1;
					trypsin = 1;
					minLabel.setVisible(false);
					maxLabel.setVisible(false);
					txtPronaseMin.setVisible(false);
					txtPronaseMax.setVisible(false);
					cmbEnzyme.setVisible(true);
				}
			}
		});
		
		new Label(container, SWT.NONE);
		
		Label permethylatedLabel = new Label(container, SWT.BORDER);
		permethylatedLabel.setText("Select Permethylated or Normal Mass");
		GridData dataPermethylated = new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1);
		enzymeLabel.setLayoutData(dataPermethylated);
		
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		cmbDerivative = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
		
		String items[] = { "Normal", "Permethylated" };
		
		// changes check this
		
		String items2[] = { GlycanPreDefinedOptions.DERIVITIZATION_NO_DERIVATIZATION,
		        GlycanPreDefinedOptions.DERIVITIZATION_PERMETHYLATED };
		
		cmbDerivative.setItems(items2);
		GridData comboPermethylated = new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1);
		comboPermethylated.horizontalSpan = 1;
		cmbDerivative.setLayoutData(comboPermethylated);
		cmbDerivative.select(1);
		permethylated = 1;
		
		cmbDerivative.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (cmbDerivative.getText().equals(GlycanPreDefinedOptions.DERIVITIZATION_NO_DERIVATIZATION)) {
					permethylated = 0;
					System.out.println("Normal");
				} else if (cmbDerivative.getText().equals(GlycanPreDefinedOptions.DERIVITIZATION_PERMETHYLATED)) {
					permethylated = 1;
					System.out.println("Permethylated");
				}
			}
		});
		
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		Boolean check = false;
		Button checkBox = new Button(container, SWT.CHECK);
		checkBox.setText("N-Terminal elimination");
		
		checkBox.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent event) {
				Button btn = (Button) event.getSource();
				nTerminalElimination = btn.getSelection();
			}
		});
		
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		new Label(container, SWT.NONE);
		
		addDatabaseSettings(container);
		
		setControl(container);
		
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		setPageComplete(false);
		
		if (validateInput()) {
			canFlipToNextPage();
		} else {
			readyToFinish = false;
			canFlipToNextPage();
		}
		
	}
	
	protected void addDatabaseSettings(Composite container) {
		databaseSettings = getSettingsTable(container);
		databaseSettings.setFilterLibrary(this.filterLibrary);
		databaseSettings.setPreferences(myWizard.getPreferences());
		databaseSettings.initComponents();
		
	}
	
	protected GlycanSettingsTableWithActionCopy getSettingsTable(Composite container) {
		return new GlycanSettingsTableWithActionCopy(container, SWT.NONE, this);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent a_event) {
		if (validateInput()) {
			canFlipToNextPage();
			getWizard().getContainer().updateButtons();
		} else {
			readyToFinish = false;
			canFlipToNextPage();
			getWizard().getContainer().updateButtons();
		}
		
	}
	
	public boolean validateInput() {
		if (TextFieldUtils.isEmpty(txtProteinSequence)) {
			setErrorMessage("Enter Protein Sequence");
			return false;
		}
		
		if (!databaseValid()) {
			setErrorMessage("Please add at least one valid database");
			return false;
		}
		
		setErrorMessage(null);
		readyToFinish = true;
		return true;
		
	}
	
	@Override
	public boolean canFlipToNextPage() {
		if (readyToFinish) {
			return true;
		} else
			return false;
	}
	
	public boolean canFinish() {
		return readyToFinish;
	}
	
	protected boolean databaseValid() {
		if (databaseSettings == null || databaseSettings.getGlycanDatabase() == null
		        || databaseSettings.getGlycanDatabase().isEmpty())
			return false;
		return true;
	}
	
	public void save() {
		
		sProteinSequence = txtProteinSequence.getText();
		
		sEnzyme = cmbEnzyme.getText();
		
		sDerivativeType = cmbDerivative.getText();
		
		if (sDerivativeType.equals(GlycanPreDefinedOptions.DERIVITIZATION_NO_DERIVATIZATION))
			sDerivativeType = "Normal";
		
		else if (sDerivativeType.equals(GlycanPreDefinedOptions.DERIVITIZATION_PERMETHYLATED))
			sDerivativeType = "Permethylated";
		
		/*
		 * if (pronase == 1) { sEnzyme = "Pronase"; }
		 * 
		 * if (others == 1 || trypsin == 1) sEnzyme = "Trypsin";
		 * 
		 * if (txtPronaseMin != null && !txtPronaseMin.getText().isEmpty()) {
		 * iMin = Integer.parseInt(txtPronaseMin.getText()); }
		 * 
		 * if (txtPronaseMax != null && !txtPronaseMax.getText().isEmpty()) {
		 * iMax = Integer.parseInt(txtPronaseMax.getText()); }
		 */
		
		List<String> glycanDatabaseNames = new ArrayList<>();
		List<String> glycanDatabaseURIs = new ArrayList<>();
		String glycanDatabaseStringName = new String();
		String glycanDatabaseStringURI = new String();
		
		for (int l = 0; l < databaseSettings.getGlycanDatabase().size(); l++) {
			GlycanDatabase gdb = databaseSettings.getGlycanDatabase().get(l);
			glycanDatabaseNames.add(l, gdb.getDatabase());
			glycanDatabaseURIs.add(l, gdb.getURI());
			if (l > 0) {
				glycanDatabaseStringName = glycanDatabaseStringName + " , " + glycanDatabaseNames.get(l);
			} else
				glycanDatabaseStringName = glycanDatabaseNames.get(l);
			if (l > 0) {
				glycanDatabaseStringURI = glycanDatabaseStringURI + " , " + glycanDatabaseURIs.get(l);
			} else
				glycanDatabaseStringURI = glycanDatabaseURIs.get(l);
		}
		
		GlycanDatabase t_glycanDatabase = new GlycanDatabase();
		t_glycanDatabase.setDatabase(glycanDatabaseStringName);
		t_glycanDatabase.setURI(glycanDatabaseStringURI);
		
		GlycoProteinSettings settings = new GlycoProteinSettings();
		
		settings.setProteinSequence(sProteinSequence);
		settings.setEnzyme(sEnzyme);
		settings.setPerDerivatisationType(sDerivativeType);
		settings.setPronaseMin(iMin);
		settings.setPronaseMax(iMax);
		settings.setNTerminalTermination(nTerminalElimination);
		
		settings.setDatabase(t_glycanDatabase);
		
		AnalyteSettings analyteSettings = new AnalyteSettings();
		analyteSettings.setGlycoProteinSettings(settings);
		List<AnalyteSettings> lAnalyteSettings = new ArrayList<>();
		lAnalyteSettings.add(analyteSettings);
		
		method.setAnalyteSettings(lAnalyteSettings);
		
	}
	
}
