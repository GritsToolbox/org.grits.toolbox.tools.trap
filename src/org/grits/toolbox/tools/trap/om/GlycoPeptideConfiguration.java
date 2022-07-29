package org.grits.toolbox.tools.trap.om;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import org.grits.toolbox.tools.trap.data.GlycanObjects;

public class GlycoPeptideConfiguration {
	
	@XmlAttribute(name = "mass")
	private Double				mass			= null;
	
	// @XmlElementWrapper(name = "GlycanObjects")
	@XmlElement(name = "Glycans")
	private List<GlycanObjects>	glycanObjects	= new ArrayList<GlycanObjects>();
	
	// @XmlElementWrapper(name = "AdductList")
	@XmlElement(name = "Adduct")
	private List<AdductInfo>	adductInfos		= new ArrayList<AdductInfo>();
	
	@XmlTransient
	public Double getMass() {
		return this.mass;
	}
	
	public void setMass(Double mass) {
		this.mass = mass;
	}
	
	public List<GlycanObjects> getGlycanObjects() {
		return this.glycanObjects;
	}
	
	// @XmlAttribute
	public void setGlycanObject(List<GlycanObjects> a_glycanObjects) {
		this.glycanObjects = a_glycanObjects;
	}
	
	@XmlTransient
	public List<AdductInfo> getAdductInfos() {
		return adductInfos;
	}
	
	// @XmlAttribute
	public void setAdductInfos(List<AdductInfo> a_adductInfos) {
		this.adductInfos = a_adductInfos;
	}
	
}
