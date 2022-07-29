package org.grits.toolbox.tools.trap.data;

import javax.xml.bind.annotation.XmlAttribute;

public class GlycanObjects {
	
	private String	glycanID			= null;
	private String	glycanGWBSequence	= null;
	private Double	glycanMass			= 0.00;
	// private Double nonPMeSubstract = 18.0105546;
	// private Double PMeSubstract = 46.04186;
	// private Integer numberOfAsparagine = 0;
	// private Integer asparaginePosition = 0;
	
	public String getGlycanID() {
		return this.glycanID;
	}
	
	@XmlAttribute(name = "GlycanID")
	public void setGlycanID(String a_glycanID) {
		this.glycanID = a_glycanID;
	}
	
	public String getGlycanGWBSequence() {
		return this.glycanGWBSequence;
	}
	
	@XmlAttribute(name = "GlycanGWBSequence")
	public void setGlycanGWBSequence(String a_glycanGWB) {
		this.glycanGWBSequence = a_glycanGWB;
	}
	
	public Double getGlycanMass() {
		return glycanMass;
	}
	
	@XmlAttribute(name = "GlycanMass")
	public void setGlycanMass(Double a_glycanMass) {
		this.glycanMass = a_glycanMass;
	}
	
	// public Integer getNumberOfAsparagine() {
	// return numberOfAsparagine;
	// }
	
	// public void setNumberOfAsparagine(Integer a_numberOfAsparagine) {
	// this.numberOfAsparagine = a_numberOfAsparagine;
	// }
	
	/*
	 * public Integer getAsparaginePosition() { return asparaginePosition; }
	 * 
	 * @XmlAttribute(name = "Position") public void
	 * setAsparaginePosition(Integer a_asparaginePosition) {
	 * this.asparaginePosition = a_asparaginePosition; }
	 */
}
