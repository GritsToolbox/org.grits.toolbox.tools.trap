package org.grits.toolbox.tools.trap.om;

import java.util.ArrayList;
import java.util.List;

public class Peptide {
	
	private Integer				id			= null;
	private String				sequence	= null;
	private List<PeptideMass>	masses		= new ArrayList<PeptideMass>();
	
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
	
	public List<PeptideMass> getMasses() {
		return this.masses;
	}
	
	public void setMasses(List<PeptideMass> a_masses) {
		this.masses = a_masses;
	}
	
}
