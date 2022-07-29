package org.grits.toolbox.tools.trap.dialog;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.grits.toolbox.ms.om.data.GlycanDatabase;

public class DatabaseSettingTableSingleColumn extends Composite {
	
	List<GlycanDatabase>	listGlycanDatabase;
	TableViewer				settingsTableViewer;
	private Table			settingsTable;
	
	/**
	 * 
	 * @param parent
	 * @param style
	 */
	public DatabaseSettingTableSingleColumn(Composite parent, int style) {
		super(parent, style);
	}
	
	/**
	 * sets the GlycanDatabase to be displayed in the table
	 * 
	 * @param GlycanDatabase
	 *            list of GlycanDatabase
	 */
	public void setGlycanDatabase(List<GlycanDatabase> listGlycanDatabase) {
		this.listGlycanDatabase = listGlycanDatabase;
		if (settingsTableViewer != null) {
			settingsTableViewer.setInput(listGlycanDatabase);
			settingsTableViewer.refresh();
		}
	}
	
	/**
	 * return the GlycanDatabase in the table
	 * 
	 * @return the list of GlycanDatabase in the table
	 */
	public List<GlycanDatabase> getGlycanDatabase() {
		return listGlycanDatabase;
	}
	
	public TableViewer getSettingsTableViewer() {
		return settingsTableViewer;
	}
	
	/**
	 * adds the table viewer to the composite
	 */
	public void createTable() {
		GridLayout layout = new GridLayout(1, true);
		layout.marginWidth = 1;
		layout.marginHeight = 1;
		this.setLayout(layout);
		
		settingsTableViewer = new TableViewer(this, SWT.BORDER | SWT.FULL_SELECTION);
		settingsTable = settingsTableViewer.getTable();
		GridData gd_table_2 = new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1);
		gd_table_2.heightHint = 80;
		settingsTable.setLayoutData(gd_table_2);
		settingsTable.setHeaderVisible(true);
		settingsTable.setLinesVisible(true);
		
		TableViewerColumn databaseNameColumn = new TableViewerColumn(settingsTableViewer, SWT.NONE);
		TableColumn dColumn = databaseNameColumn.getColumn();
		dColumn.setText("Database Name");
		dColumn.setWidth(200);
		databaseNameColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof GlycanDatabase) {
					GlycanDatabase glycanDatabase = ((GlycanDatabase) element);
					
					return (glycanDatabase.getDatabase());
				}
				return "";
			}
		});
		
		settingsTableViewer.setContentProvider(new ArrayContentProvider());
		settingsTableViewer.setInput(listGlycanDatabase);
	}
}
