package org.grits.toolbox.tools.trap.om;

import java.util.List;

public class PeptideMass {
	
	private Integer				id				= null;
	private Double				peptideMass		= null;
	private List<AminoAcidMass>	aminoAcidMasses	= null;
	
	public Integer getId() {
		return id;
	}
	
	public void setId(Integer id) {
		this.id = id;
	}
	
	public Double getPeptideMass() {
		return peptideMass;
	}
	
	public void setPeptideMass(Double a_peptideMass) {
		this.peptideMass = a_peptideMass;
	}
	
	public List<AminoAcidMass> getAminoAcidMasses() {
		return this.aminoAcidMasses;
	}
	
	public void setAminoAcidMasses(List<AminoAcidMass> a_mass) {
		this.aminoAcidMasses = a_mass;
	}
	
}
