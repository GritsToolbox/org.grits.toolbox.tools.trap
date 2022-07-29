package org.grits.toolbox.tools.trap.om;

import javax.xml.bind.annotation.XmlAttribute;

public class AminoAcidMass {
	
	private String	aminoAcidChar	= null;
	private Double	aminoAcidMass	= null;
	
	public String getAminoAcidChar() {
		return aminoAcidChar;
	}
	
	@XmlAttribute
	public void setAminoAcidChar(String a_aminoAcidChar) {
		this.aminoAcidChar = a_aminoAcidChar;
	}
	
	public Double getMass() {
		return aminoAcidMass;
	}
	
	@XmlAttribute
	public void setMass(Double a_mass) {
		this.aminoAcidMass = a_mass;
	}
	
}
