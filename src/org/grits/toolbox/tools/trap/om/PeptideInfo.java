package org.grits.toolbox.tools.trap.om;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "peptideInfo")
public class PeptideInfo {
	
	@XmlAttribute(name = "id")
	private Integer							id							= null;
	
	@XmlAttribute(name = "sequence")
	private String							sequence					= null;
	
	@XmlAttribute(name = "mass")
	private Double							mass						= null;
	
	@XmlElementWrapper(name = "MassList")
	@XmlElement(name = "AminoMass")
	private List<AminoAcidMass>				aminoMasses					= new ArrayList<AminoAcidMass>();
	
	// @XmlElementWrapper(name = "nLinks List")
	@XmlElement(name = "nLinkPositions")
	private List<Integer>					nLinks						= new ArrayList<Integer>();
	
	// @XmlElementWrapper(name = "GlycoPeptide")
	@XmlElement(name = "GlycoPeptideConfiguration")
	private List<GlycoPeptideConfiguration>	glycoPeptideConfiguration	= new ArrayList<GlycoPeptideConfiguration>();
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public String getSequence() {
		return this.sequence;
	}
	
	public void setSequence(String a_sequence) {
		this.sequence = a_sequence;
	}
	
	public Double getMass() {
		return this.mass;
	}
	
	public void setMass(Double a_mass) {
		this.mass = a_mass;
	}
	
	public List<AminoAcidMass> getAminoMasses() {
		return this.aminoMasses;
	}
	
	public void setAminoMassess(List<AminoAcidMass> a_masses) {
		this.aminoMasses = a_masses;
	}
	
	public List<Integer> getNLinks() {
		return this.nLinks;
	}
	
	public void setNLinks(List<Integer> nLinkPosition) {
		this.nLinks = nLinkPosition;
	}
	
	public List<GlycoPeptideConfiguration> getGlycoPeptide() {
		return this.glycoPeptideConfiguration;
	}
	
	public void setGlycoPeptides(List<GlycoPeptideConfiguration> glycoPeptides) {
		this.glycoPeptideConfiguration = glycoPeptides;
	}
	
}
