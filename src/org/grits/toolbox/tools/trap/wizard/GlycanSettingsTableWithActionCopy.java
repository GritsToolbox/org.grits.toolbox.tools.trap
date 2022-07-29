package org.grits.toolbox.tools.trap.wizard;

import java.util.ArrayList;

//package org.grits.toolbox.importer.ms.annotation.glycan.simiansearch.wizard;

import java.util.List;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.grits.toolbox.entry.ms.annotation.glycan.preference.MSGlycanAnnotationPreference;
import org.grits.toolbox.ms.om.data.AnalyteSettings;
import org.grits.toolbox.ms.om.data.GlycanDatabase;
//import org.grits.toolbox.entry.ms.annotation.glycan.dialog.DatabaseSettingsTableComposite;
import org.grits.toolbox.tools.trap.dialog.DatabaseSettingTableSingleColumn;
import org.grits.toolbox.tools.trap.dialog.GlycanSettingsDialogModified;
import org.grits.toolbox.util.structure.glycan.filter.om.FiltersLibrary;

public class GlycanSettingsTableWithActionCopy extends Composite {
	
	protected FiltersLibrary					filterLibrary;
	protected MSGlycanAnnotationPreference		preferences;
	private DatabaseSettingTableSingleColumn	table;
	protected TableViewer						settingsTableViewer;
	protected Table								settingsTable;
	protected IPropertyChangeListener			listener;
	
	public GlycanSettingsTableWithActionCopy(Composite parent, int style, IPropertyChangeListener listener) {
		super(parent, style);
		this.listener = listener;
	}
	
	/**
	 * This method sets the layout for this composite, adds the table components
	 * and also the "add/delete" buttons
	 * 
	 */
	public void initComponents() {
		initLayout();
		createTable();
		addButtons();
	}
	
	protected void createTable() {
		table = new DatabaseSettingTableSingleColumn(this, SWT.NONE);
		GridData gd2 = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		
		if (table.getGlycanDatabase() == null) {
			table.setGlycanDatabase(new ArrayList<GlycanDatabase>());
		}
		table.setLayoutData(gd2);
		table.createTable();
		settingsTableViewer = table.getSettingsTableViewer();
		settingsTable = settingsTableViewer.getTable();
	}
	
	/**
	 * sets the grid layout to the composite
	 * 
	 */
	public void initLayout() {
		GridLayout layout2 = new GridLayout(4, false);
		layout2.marginWidth = 2;
		layout2.marginHeight = 2;
		this.setLayout(layout2);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		this.setLayoutData(gd);
	}
	
	/**
	 * adds "Add" and "Delete" buttons and their actions "Add" and "Edit"
	 * actions require the "preferences" and "filterLibrary" to be set in this
	 * composite
	 * 
	 */
	public void addButtons() {
		GridData gd1 = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd1.widthHint = 200;
		new Label(this, SWT.NONE).setLayoutData(gd1);
		
		addAddButton();
		
		addDeleteButton();
	}
	
	protected void addDeleteButton() {
		Button deleteButton = new Button(this, SWT.PUSH);
		deleteButton.setText("Delete");
		deleteButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] items = settingsTable.getSelection();
				if (items.length > 0) {
					GlycanDatabase gDatabase = (GlycanDatabase) items[0].getData();
					if (gDatabase != null) {
						boolean wasEmpty = getGlycanDatabase().isEmpty();
						getGlycanDatabase().remove(gDatabase);
						settingsTableViewer.refresh();
						PropertyChangeEvent ce = new PropertyChangeEvent(this, "Page Complete", !wasEmpty,
		                        !getGlycanDatabase().isEmpty());
						listener.propertyChange(ce);
					}
				}
			}
		});
	}
	
	protected void addEditButton() {
		Button editButton = new Button(this, SWT.PUSH);
		editButton.setText("Edit");
		
		editButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] items = settingsTable.getSelection();
				if (items.length > 0) {
					AnalyteSettings setting = (AnalyteSettings) items[0].getData();
					if (setting != null) {
						GlycanSettingsDialogModified dialog = new GlycanSettingsDialogModified(getShell(),
		                        filterLibrary, setting.getGlycanSettings());
						if (dialog.open() == Window.OK) {
							settingsTableViewer.refresh();
						}
					}
				}
			}
		});
	}
	
	protected void addAddButton() {
		Button addButton = new Button(this, SWT.PUSH);
		addButton.setText("Add");
		addButton.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				GlycanSettingsDialogModified dialog = new GlycanSettingsDialogModified(getShell(), filterLibrary);
				if (dialog.open() == Window.OK) {
					PropertyChangeEvent ce = new PropertyChangeEvent(this, "Page Complete",
		                    !getGlycanDatabase().isEmpty(), true);
					
					GlycanDatabase gDatabase = dialog.getGlycanDatabse();
					getGlycanDatabase().add(gDatabase);
					
					settingsTableViewer.refresh();
					listener.propertyChange(ce);
				}
			}
		});
	}
	
	public List<GlycanDatabase> getGlycanDatabase() {
		return table.getGlycanDatabase();
	}
	
	public void setGlycanDatabase(List<GlycanDatabase> database) {
		table.setGlycanDatabase(database);
	}
	
	/**
	 * sets the filterLibrary to be used in "adding" analyteSettings
	 * 
	 * @param filterLibrary
	 *            library of the filters
	 */
	public void setFilterLibrary(FiltersLibrary filterLibrary) {
		this.filterLibrary = filterLibrary;
	}
	
	/**
	 * sets the glycan annotation preferences to be used in "adding"
	 * analyteSettings
	 * 
	 * @param preferences
	 *            preference settings
	 */
	public void setPreferences(MSGlycanAnnotationPreference preferences) {
		this.preferences = preferences;
	}
}
