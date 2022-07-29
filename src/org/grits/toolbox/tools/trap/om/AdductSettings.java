package org.grits.toolbox.tools.trap.om;

import java.util.ArrayList;
import java.util.List;

public class AdductSettings {
	
	private List<IonCombination>	ionCombinations	= new ArrayList<IonCombination>();
	private String					label			= null;
	private String					ionName			= null;
	private Double					mass			= null;
	private Integer					charge			= null;
	
	public String getLabel() {
		return this.label;
	}
	
	public void setLabel(String a_label) {
		this.label = a_label;
	}
	
	public Double getMass() {
		return this.mass;
	}
	
	public void setMass(Double a_mass) {
		this.mass = a_mass;
	}
	
	public Integer getCharge() {
		return this.charge;
	}
	
	public void setCharge(Integer a_charge) {
		this.charge = a_charge;
	}
	
	public List<IonCombination> getIonCombinations() {
		return ionCombinations;
	}
	
	public void setIonCombinations(List<IonCombination> a_ionCombinations) {
		this.ionCombinations = a_ionCombinations;
	}

	public String getIonName() {
		return ionName;
	}

	public void setIonName(String a_ionName) {
		this.ionName = a_ionName;
	}
	
}
