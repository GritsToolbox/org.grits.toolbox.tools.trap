package org.grits.toolbox.tools.trap.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.grits.toolbox.ms.om.data.AnalyteSettings;
import org.grits.toolbox.ms.om.data.GlycanDatabase;
import org.grits.toolbox.ms.om.data.GlycoProteinSettings;
import org.grits.toolbox.ms.om.data.Method;
import org.grits.toolbox.util.structure.glycan.util.FilterUtils;

public class TestGlycoProtein {
	
	public static void main(String args[]) {
		
		GlycanDatabase t_glycanDatabase = new GlycanDatabase();
		t_glycanDatabase.setDatabase("GlycanDatabaseName");
		t_glycanDatabase.setURI("GlycanDatabaseURI");
		
		GlycoProteinSettings t_glycoProteinSettings = new GlycoProteinSettings();
		t_glycoProteinSettings.setProteinSequence("aaa");
		t_glycoProteinSettings.setPerDerivatisationType("Permethylated");
		t_glycoProteinSettings.setEnzyme("Pronase");
		t_glycoProteinSettings.setPronaseMax(null);
		t_glycoProteinSettings.setPronaseMin(null);
		t_glycoProteinSettings.setDatabase(t_glycanDatabase);
		
		AnalyteSettings t_analyteSettings = new AnalyteSettings();
		t_analyteSettings.setGlycoProteinSettings(t_glycoProteinSettings);
		
		Method t_method = new Method();
		t_method.getAnalyteSettings().add(t_analyteSettings);
		
		serialize(t_method);
	}
	
	private static void serialize(Method method) {
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
			e.printStackTrace();
		}
		
	}
	
}
