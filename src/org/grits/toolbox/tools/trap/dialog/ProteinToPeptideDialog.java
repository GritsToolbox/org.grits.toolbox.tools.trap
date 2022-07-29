package org.grits.toolbox.tools.trap.dialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.grits.toolbox.core.datamodel.Entry;
import org.grits.toolbox.ms.annotation.structure.GlycanPreDefinedOptions;
import org.grits.toolbox.tools.trap.calculateMass.ProteinToPeptide;
import org.grits.toolbox.tools.trap.excel.ExcelFunctions;

/**
 * 
 * @author Aravind Kalimurthy
 *
 */

public class ProteinToPeptideDialog extends TitleAreaDialog {
	
	/**
	 * default constructor used to create the dialog box and initialize the
	 * parent shell
	 * 
	 * @param a_parentShell
	 */
	public ProteinToPeptideDialog(Shell a_parentShell) {
		super(a_parentShell);
		// TODO Auto-generated constructor stub
	}
	
	private static final Logger		logger				= Logger.getLogger(ProteinToPeptideDialog.class);
	
	private Text					txtProteinSequence;
	private Text					txtPronaseMin;
	private Text					txtPronaseMax;
	
	public static final String		ENZYME_LABEL		= "Trypsin";
	// public static final String ENZYME_LABEL2 = "Pronase";
	// public static final String[] ENZYME_TYPES = new String[] { ENZYME_LABEL,
	// ENZYME_LABEL2 };
	public static final String[]	ENZYME_TYPES		= new String[] { ENZYME_LABEL };
	
	private String					proteinSequence;
	private String					saveFileLocation;
	protected Shell					shell				= null;
	protected String				fileName			= null;
	protected Entry					msEntry				= null;
	protected String				sOutputFile			= null;
	
	public static final String		EXTENSION			= ".xlsx";
	private static final String		DEFAULT_FILENAME	= "aminoAcidMasses" + EXTENSION;
	private Text					locationText		= null;
	
	int								pronase				= -1;
	int								others				= -1;
	int								trypsin				= -1;
	int								permethylated		= -1;
	
	@Override
	public void create() {
		super.create();
		setTitle("Protein to Peptide Mass Calculater");
		setMessage("This is a Dialog box to calculate Protein to Peptide Masses", IMessageProvider.INFORMATION);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite area = (Composite) super.createDialogArea(parent);
		Composite container = new Composite(area, SWT.NONE);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		GridLayout layout = new GridLayout(5, false);
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 10;
		area.setSize(100, 100);
		container.setLayout(layout);
		
		createProteinSequence(container);
		
		return area;
	}
	
	private Button addAButton(Composite container, String label, int horizontalAlignment) {
		Button button = new Button(container, SWT.PUSH);
		button.setText(label);
		GridData buttonGridData = new GridData(horizontalAlignment);
		buttonGridData.horizontalSpan = 1;
		buttonGridData.widthHint = 150;
		button.setLayoutData(buttonGridData);
		
		return button;
	}
	
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		
		createButton(parent, IDialogConstants.OK_ID, "Export", true);
		getButton(IDialogConstants.OK_ID).addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent a_e) {
				
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent a_e) {
				// TODO Auto-generated method stub
				
			}
		});
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}
	
	/**
	 * This method creates user interface dialog box to accept the input from
	 * the user.
	 * 
	 * @param container
	 *            composite to which we add the new UI and make changes.
	 */
	private void createProteinSequence(Composite container) {
		
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
		
		final Combo r = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
		String itemList[] = { "Trypsin" };
		r.setItems(itemList);
		GridData radioEnzyme = new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1);
		radioEnzyme.horizontalSpan = 2;
		r.setLayoutData(radioEnzyme);
		r.setVisible(false);
		r.select(0); // Select default first item.
		trypsin = 1;
		r.setVisible(true);
		
		r.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (r.getText().equals("Trypsin")) {
					trypsin = 1;
				}
			}
		});
		
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
					r.setVisible(false);
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
					trypsin = -1;
					minLabel.setVisible(false);
					maxLabel.setVisible(false);
					txtPronaseMin.setVisible(false);
					txtPronaseMax.setVisible(false);
					r.setVisible(true);
				}
			}
		});
		
		new Label(container, SWT.NONE);
		
		Label permethylatedLabel = new Label(container, SWT.BORDER);
		permethylatedLabel.setText("Select Permethylated Masses or Normal Masses");
		GridData dataPermethylated = new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1);
		enzymeLabel.setLayoutData(dataPermethylated);
		
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		final Combo c = new Combo(container, SWT.DROP_DOWN | SWT.READ_ONLY);
		
		String items[] = { GlycanPreDefinedOptions.DERIVITIZATION_NO_DERIVATIZATION,
		        GlycanPreDefinedOptions.DERIVITIZATION_PERMETHYLATED };
		c.setItems(items);
		GridData comboPermethylated = new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1);
		comboPermethylated.horizontalSpan = 2;
		c.setLayoutData(comboPermethylated);
		c.select(1);
		permethylated = 1;
		
		c.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (c.getText().equals(GlycanPreDefinedOptions.DERIVITIZATION_NO_DERIVATIZATION)) {
					permethylated = 0;
				} else if (c.getText().equals(GlycanPreDefinedOptions.DERIVITIZATION_PERMETHYLATED)) {
					permethylated = 1;
				}
			}
		});
		
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		// Save Button
		
		Label saveLabel = new Label(container, SWT.BORDER);
		saveLabel.setText("Select the folder to save excel sheet");
		GridData dataSave = new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1);
		saveLabel.setLayoutData(dataSave);
		
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		new Label(container, SWT.NONE);
		
		locationText = new Text(container, SWT.BORDER);
		GridData textData = new GridData(SWT.FILL, SWT.TOP, true, true, 1, 1);
		textData.horizontalSpan = 4;
		locationText.setLayoutData(textData);
		locationText.setText(System.getProperty("user.home") + File.separator + DEFAULT_FILENAME);
		locationText.setEnabled(false);
		locationText.setLayoutData(textData);
		
		Button browseButton = addAButton(container, "Save...", GridData.HORIZONTAL_ALIGN_END);
		
		browseButton.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setErrorMessage(null);
				FileDialog fileDialog = new FileDialog(getShell(), SWT.SAVE);
				fileDialog.setText("Select File");
				fileDialog.setFilterExtensions(new String[] { EXTENSION });
				fileDialog.setFilterNames(new String[] { "Grits Export (" + EXTENSION + ")" });
				fileDialog.setFileName(DEFAULT_FILENAME);
				fileDialog.setOverwrite(true);
				String selected = fileDialog.open();
				if (selected != null) {
					locationText.setText(selected);
				}
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				
			}
		});
		browseButton.setFocus();
		
		container.setLayoutData(new GridData(GridData.FILL_BOTH));
	}
	
	@Override
	protected void okPressed() {
		
		List<String> peptideSequence = new ArrayList<String>();
		
		proteinSequence = txtProteinSequence.getText();
		
		proteinSequence = proteinSequence.replaceAll("\\n", "");
		proteinSequence = proteinSequence.replaceAll("\\s", "");
		
		proteinSequence = proteinSequence.toUpperCase();
		
		if (txtProteinSequence.getText().equals("")) {
			setErrorMessage("Protein Sequence cannot be Empty");
			return;
		}
		
		String stringRegex = "[BJOUXZ]";
		String numberRegex = "[0-9]";
		
		Pattern sPattern = Pattern.compile(stringRegex, Pattern.CASE_INSENSITIVE);
		Matcher sMatcher = sPattern.matcher(proteinSequence);
		
		Pattern nPattern = Pattern.compile(numberRegex, Pattern.CASE_INSENSITIVE);
		Matcher nMatcher = nPattern.matcher(proteinSequence);
		
		Pattern whitespacePattern = Pattern.compile("\\s");
		Matcher whitespaceMatcher = whitespacePattern.matcher(proteinSequence);
		
		if (sMatcher.find()) {
			setErrorMessage("INVALID Protein Character Found");
			return;
		}
		
		if (nMatcher.find()) {
			setErrorMessage("Numbers not allowed in Protein Sequence");
			return;
		}
		
		/*
		 * if (whitespaceMatcher.find()) { setErrorMessage(
		 * "WhiteSpace not allowed in Protein Sequence"); return; }
		 */
		
		if (pronase == -1 && trypsin == -1 && others == -1) {
			setErrorMessage("Please select the Enzyme");
			return;
		}
		
		if (permethylated == -1) {
			setErrorMessage("Select Normal or Permethylated Masses to be considered");
			return;
		}
		
		if (pronase == 1) {
			
			if (txtPronaseMin.getText().equals("")) {
				setErrorMessage("Min Value cannot be Empty");
				return;
			}
			
			if (txtPronaseMax.getText().equals("")) {
				setErrorMessage("Max Value cannot be Empty");
				return;
			}
			
			if (txtPronaseMin.getText().matches("[a-zA-z]+")) {
				setErrorMessage("Min Value cannot be character, should be integer");
				return;
			}
			
			if (txtPronaseMax.getText().matches("[a-zA-z+]")) {
				setErrorMessage("Max Value cannot be character, should be integer");
				return;
			}
			
			String minString = txtPronaseMin.getText();
			String maxString = txtPronaseMax.getText();
			
			try {
				int min = Integer.parseInt(minString);
			} catch (Exception e) {
				setErrorMessage("Min Value should be integer");
				return;
			}
			
			try {
				int max = Integer.parseInt(maxString);
			} catch (Exception e) {
				setErrorMessage("Max Value should be integer");
				return;
			}
			
			int min = Integer.parseInt(minString);
			int max = Integer.parseInt(maxString);
			
			if (min > max) {
				setErrorMessage("Min Value cannot be greater than Max value");
				return;
			}
			
			peptideSequence = ProteinToPeptide.getPeptideSequencePronase(proteinSequence, min, max);
		}
		
		if (others == 1 && trypsin == -1) {
			setErrorMessage("Select the Other type of Enzyme from drop-down menu");
			return;
		}
		
		saveFileLocation = locationText.getText();
		
		if (trypsin == 1)
			peptideSequence = ProteinToPeptide.getPeptideSequenceTrypsin(proteinSequence);
		
		ExcelFunctions.writeToExcel(peptideSequence, permethylated, saveFileLocation);
		
		// saveInput();
		super.okPressed();
	}
}
