package org.grits.toolbox.tools.trap.om;

import javax.xml.bind.annotation.XmlAttribute;

public class ScanMatches {
	
	private Integer	scanNumber	= null;
	private Double	mzValue		= null;
	private Double	intensity	= null;
	
	public Integer getScanNumber() {
		return scanNumber;
	}
	
	@XmlAttribute
	public void setScanNumber(Integer a_scanNumber) {
		this.scanNumber = a_scanNumber;
	}
	
	public Double getMzValue() {
		return mzValue;
	}
	
	@XmlAttribute
	public void setMzValue(Double a_mzValue) {
		this.mzValue = a_mzValue;
	}
	
	public Double getIntensity() {
		return intensity;
	}
	
	@XmlAttribute
	public void setIntensity(Double a_intensity) {
		this.intensity = a_intensity;
	}
	
}
