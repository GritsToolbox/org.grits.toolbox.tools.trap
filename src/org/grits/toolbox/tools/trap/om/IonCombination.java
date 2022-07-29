package org.grits.toolbox.tools.trap.om;

import org.grits.toolbox.ms.om.data.Ion;

public class IonCombination extends Ion {
	
	public IonCombination(Ion ion) {
		this.setName(ion.getName());
		this.setCharge(ion.getCharge());
		this.setLabel(ion.getLabel());
		this.setMass(ion.getMass());
		this.setPolarity(ion.getPolarity());
	}
	
	private Integer count = null;
	
	public Integer getCount() {
		return count;
	}
	
	public void setCount(Integer a_count) {
		this.count = a_count;
	}
	
}
