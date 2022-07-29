package org.grits.toolbox.tools.trap.om;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 
 * @author Aravind Kalimurthy
 *
 */

/**
 * List of all amino acids characters and their masses
 */
public enum AminoAcidDatabase {
//	@formatter:off
	G('G', "Glycine", Arrays.asList(57.02146),  Arrays.asList(71.03711)), 
	A('A', "Alanine", Arrays.asList(71.03711),  Arrays.asList(85.05276)), 
	S('S', "Serine", Arrays.asList(87.03203),  Arrays.asList(115.0633, 83.0371, 81.03405)),      // 95.0497 - 14.01565
	T('T', "Threonine", Arrays.asList(101.04768), Arrays.asList(129.0790, 97.0528)), 
	C('C', "Cysteine", Arrays.asList(103.00919), Arrays.asList(202.0776,83.0371)), 
	V('V', "Valine", Arrays.asList(99.06841),  Arrays.asList(113.0841)), 
	L('L', "Leucine", Arrays.asList(113.08406), Arrays.asList(127.0997)), 
	I('I', "Isoleucine", Arrays.asList(113.08406), Arrays.asList(127.0997)), 
	M('M', "Methionine", Arrays.asList(131.04049), Arrays.asList(97.0528)), 
	P('P', "Proline", Arrays.asList(97.05276),  Arrays.asList(112.07624)), 
	F('F', "Phenyl alanine", Arrays.asList(147.06841), Arrays.asList(161.0841)), 
	Y('Y', "Tyrosine", Arrays.asList(163.06333), Arrays.asList(191.0946)), 
	W('W', "Tryptophan", Arrays.asList(186.07931), Arrays.asList(216.1263)), 
	D('D', "Aspartic acid", Arrays.asList(115.02694), Arrays.asList(143.0582)), 
	E('E', "Glutamic acid", Arrays.asList(129.04259), Arrays.asList(157.0739)), 
	N('N', "Asparagine", Arrays.asList(114.04293), Arrays.asList(156.0899)), 
	Q('Q', "Glutamine", Arrays.asList(128.05858), Arrays.asList( 170.1055)), 
	H('H', "Histidine", Arrays.asList(137.05891), Arrays.asList(165.0902)), 
	K('K', "Lysine", Arrays.asList(128.09496), Arrays.asList(185.1648)), 
	R('R', "Arginine", Arrays.asList(156.10111), Arrays.asList(226.1794));
// @formatter:on
	
	public final char	letter;
	public final String	aminoAcidName;
	public List<Double>	mass	= new ArrayList<Double>();
	public List<Double>	pMass	= new ArrayList<Double>();
	
	/**
	 * List of all amino acids characters and their masses
	 * 
	 * @param letter
	 *            the character of the amino acid
	 * @param aminoAcidName
	 *            the string name for the amino acid
	 * @param mass
	 *            normal individual mass of the amino acid
	 * @param pMass
	 *            permethylated mass of the amino acid
	 */
	AminoAcidDatabase(char letter, String aminoAcidName, List<Double> mass, List<Double> pMass) {
		this.letter = letter;
		this.aminoAcidName = aminoAcidName;
		this.mass = mass;
		this.pMass = pMass;
	}
	
	/**
	 * 
	 * @return the character of the amino acid
	 */
	public char letter() {
		return letter;
	}
	
	/**
	 * 
	 * @return the string name for the amino acid
	 */
	public String aminoAcidName() {
		return aminoAcidName;
	}
	
	/**
	 * 
	 * @param flag
	 *            indicates whether to return permethylated or normal mass of
	 *            the amino acid
	 * @return individual mass of the amino acid
	 */
	public List<Double> mass(int flag) {
		if (flag == 1)
			return pMass;
		else
			return mass;
	}
	
}
