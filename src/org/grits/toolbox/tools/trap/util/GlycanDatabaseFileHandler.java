package org.grits.toolbox.tools.trap.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.log4j.Logger;
import org.grits.toolbox.ms.annotation.structure.GlycanDatabase;

public class GlycanDatabaseFileHandler {
	
	private static String			m_strGDB	= "";
	private static GlycanDatabase	m_gDB		= null;
	
	private static final Logger		logger		= Logger.getLogger(GlycanDatabaseFileHandler.class);
	
	public static GlycanDatabase getGlycanDatabase(String a_strGDBFilepath) {
		if (m_strGDB.equals(a_strGDBFilepath))
			return m_gDB;
		m_strGDB = a_strGDBFilepath;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(GlycanDatabase.class);
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			m_gDB = (GlycanDatabase) jaxbUnmarshaller.unmarshal(new FileInputStream(a_strGDBFilepath));
			return m_gDB;
		} catch (JAXBException e) {
			logger.error("An error during deserializing GlycanDatabase object.", e);
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			logger.error("GlycanDatabase XML file not found.", e);
			e.printStackTrace();
		}
		return null;
	}
	
}
