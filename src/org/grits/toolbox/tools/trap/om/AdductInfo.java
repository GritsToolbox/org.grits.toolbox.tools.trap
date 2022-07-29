package org.grits.toolbox.tools.trap.om;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;

public class AdductInfo {
	
	private String				label		= null;
	private String				ionName		= null;
	private Double				mass		= null;
	private Integer				charge		= null;
	private List<ScanMatches>	scanMatches	= new ArrayList<ScanMatches>();
	
	public String getLabel() {
		return this.label;
	}
	
	@XmlAttribute
	public void setLabel(String a_label) {
		this.label = a_label;
	}
	
	public Double getMass() {
		return this.mass;
	}
	
	@XmlAttribute
	public void setMass(Double a_mass) {
		this.mass = a_mass;
	}
	
	public Integer getCharge() {
		return this.charge;
	}
	
	@XmlAttribute
	public void setCharge(Integer a_charge) {
		this.charge = a_charge;
	}
	
	public List<ScanMatches> getScanMatches() {
		return scanMatches;
	}
	
	public void setScanMatches(List<ScanMatches> a_scanMatches) {
		this.scanMatches = a_scanMatches;
	}
	
	public String getIonName() {
		return ionName;
	}
	
	@XmlAttribute
	public void setIonName(String a_ionName) {
		this.ionName = a_ionName;
	}
	
}
