package org.grits.toolbox.tools.trap.calculateMass;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.grits.toolbox.tools.trap.om.AminoAcidDatabase;

/**
 * 
 * @author Aravind Kalimurthy
 *
 */

public class ProteinToPeptide {
	
	public static int counter = 0;
	
	/**
	 * Method to get list of peptide sequence from a given protein sequence for
	 * Trypsin enzyme.
	 * 
	 * @param proteinSequence
	 *            sequence of protein in form of a string
	 * @return returns the list of peptide sequence for trypsin enzyme
	 */
	public static ArrayList<String> getPeptideSequenceTrypsin(String proteinSequence) {
		// Get the peptide sequence based on trypsin enzyme.
		String regex = "[RK]";
		int start = 0;
		int end = 0;
		List<String> peptideSequence = new ArrayList<String>();
		
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(proteinSequence);
		
		while (matcher.find()) {
			end = matcher.end();
			String temp = proteinSequence.substring(start, end);
			start = end;
			if (proteinSequence.charAt(end) == 'P') {
				matcher.find();
				end = matcher.end();
				String newTemp = temp + proteinSequence.substring(start, end);
				peptideSequence.add(newTemp);
				start = end;
			} // if
			else
				peptideSequence.add(temp);
		} // While
		end = proteinSequence.length();
		peptideSequence.add(proteinSequence.substring(start, end));
		
		return (ArrayList<String>) peptideSequence;
	} // getPeptideSequenceTrypsin
	
	/**
	 * Method to get list of peptide sequence from a given protein sequence for
	 * Pronase enzyme.
	 * 
	 * @param proteinSequence
	 *            sequence of protein in form of a string
	 * @return returns the list of peptide sequence for pronase enzyme
	 */
	public static ArrayList<String> getPeptideSequencePronase(String proteinSequence, int min, int max) {
		// Get the peptide sequence based on Pronase enzyme.
		List<String> peptideSequence = new ArrayList<String>();
		
		for (int i = min; i <= max; i++) {
			int start = 0;
			// int end = getRandomNumberInRange(min, max);
			int end = i;
			int length = proteinSequence.length();
			
			while (length > end) {
				String temp = proteinSequence.substring(start, end);
				start = start + 1;
				// end = end + getRandomNumberInRange(min, max);
				end = start + i;
				peptideSequence.add(temp);
			}
			peptideSequence.add(proteinSequence.substring(start, length));
		}
		return (ArrayList<String>) peptideSequence;
	} // getpeptideSequencePronase
	
	/**
	 * Method to get the individual mass of the peptide sequence, which is the
	 * total mass of the amino acid present in it.
	 * 
	 * @param peptideSequence
	 *            list of peptide sequence.
	 * @param flag
	 *            indicates if it wants permethylated or normal masses for the
	 *            peptide.
	 * @return returns the individual masses for the peptide sequence in form of
	 *         a list.
	 */
	public static ArrayList<ArrayList<Double>> getAminoPeptideMass(List<String> peptideSequence, int flag) {
		
		double current = 0;
		double mWeight;
		
		if (flag == 0) {
			mWeight = 18.0105646;
		} else {
			double temp = 18.0105646 + 14.01565 + 14.01565; // Check this value
			                                                // if 2 (14.01565)
			                                                // or just 1
			mWeight = 46.04187; // check once if mass error found
		}
		List<ArrayList<Double>> doubleResult = new ArrayList<ArrayList<Double>>();
		
		for (int i = 0; i < peptideSequence.size(); i++) {
			List<ArrayList<Double>> listAminoMass = new ArrayList<ArrayList<Double>>();
			List<Double> resultPeptideMass = new ArrayList<Double>();
			List<Double> result = new ArrayList<Double>();
			
			listAminoMass = aminoAcidPeptide(peptideSequence.get(i), flag);
			
			result = generatePermutations(listAminoMass, resultPeptideMass, 0, current);
			
			for (int j = 0; j < result.size(); j++) {
				result.set(j, (result.get(j) + mWeight));
			}
			doubleResult.add((ArrayList<Double>) result);
			
		}
		
		return (ArrayList<ArrayList<Double>>) doubleResult;
	}
	
	/**
	 * Method to get the individual mass of the amino acid for the peptide
	 * sequence.
	 * 
	 * @param peptideSequence
	 *            peptide sequence in form of a string
	 * @param flag
	 *            indicates if it wants permethylated or normal masses of the
	 *            amino acid.
	 * @return returns the individual masses of the amino acid in form of a list
	 *         for the peptide sequence.
	 */
	private static List<ArrayList<Double>> aminoAcidPeptide(String peptideSequence, int flag) {
		
		peptideSequence = peptideSequence.toUpperCase();
		
		char charPeptide[] = peptideSequence.toCharArray();
		
		List<ArrayList<Double>> listAminoMass = new ArrayList<ArrayList<Double>>();
		
		for (int i = 0; i < peptideSequence.length(); i++) {
			char temp = charPeptide[i];
			
			switch (temp) {
				
				case 'A': {
					List<Double> result = new ArrayList<Double>();
					int size;
					if (flag == 1)
						size = AminoAcidDatabase.A.pMass.size() - 1;
					else
						size = AminoAcidDatabase.A.mass.size() - 1;
					for (int j = 0; j <= size; j++) {
						result.add(AminoAcidDatabase.A.mass(flag).get(j));
					}
					listAminoMass.add(new ArrayList<Double>(result));
					break;
				}
				
				case 'R': {
					int size;
					List<Double> result = new ArrayList<Double>();
					if (flag == 1)
						size = AminoAcidDatabase.R.pMass.size() - 1;
					else
						size = AminoAcidDatabase.R.mass.size() - 1;
					for (int j = 0; j <= size; j++) {
						result.add(AminoAcidDatabase.R.mass(flag).get(j));
					}
					listAminoMass.add(new ArrayList<Double>(result));
					break;
				}
				
				case 'N': {
					int size;
					List<Double> result = new ArrayList<Double>();
					if (flag == 1)
						size = AminoAcidDatabase.N.pMass.size() - 1;
					else
						size = AminoAcidDatabase.N.mass.size() - 1;
					for (int j = 0; j <= size; j++) {
						result.add(AminoAcidDatabase.N.mass(flag).get(j));
					}
					listAminoMass.add(new ArrayList<Double>(result));
					break;
				}
				
				case 'D': {
					int size;
					List<Double> result = new ArrayList<Double>();
					if (flag == 1)
						size = AminoAcidDatabase.D.pMass.size() - 1;
					else
						size = AminoAcidDatabase.D.mass.size() - 1;
					for (int j = 0; j <= size; j++) {
						result.add(AminoAcidDatabase.D.mass(flag).get(j));
					}
					listAminoMass.add(new ArrayList<Double>(result));
					break;
				}
				
				case 'C': {
					int size;
					List<Double> result = new ArrayList<Double>();
					if (flag == 1)
						size = AminoAcidDatabase.C.pMass.size() - 1;
					else
						size = AminoAcidDatabase.C.mass.size() - 1;
					for (int j = 0; j <= size; j++) {
						result.add(AminoAcidDatabase.C.mass(flag).get(j));
					}
					listAminoMass.add(new ArrayList<Double>(result));
					break;
				}
				case 'E': {
					int size;
					List<Double> result = new ArrayList<Double>();
					if (flag == 1)
						size = AminoAcidDatabase.E.pMass.size() - 1;
					else
						size = AminoAcidDatabase.E.mass.size() - 1;
					for (int j = 0; j <= size; j++) {
						result.add(AminoAcidDatabase.E.mass(flag).get(j));
					}
					listAminoMass.add(new ArrayList<Double>(result));
					break;
				}
				
				case 'Q': {
					int size;
					List<Double> result = new ArrayList<Double>();
					if (flag == 1)
						size = AminoAcidDatabase.Q.pMass.size() - 1;
					else
						size = AminoAcidDatabase.Q.mass.size() - 1;
					for (int j = 0; j <= size; j++) {
						result.add(AminoAcidDatabase.Q.mass(flag).get(j));
					}
					listAminoMass.add(new ArrayList<Double>(result));
					break;
				}
				
				case 'G': {
					int size;
					List<Double> result = new ArrayList<Double>();
					if (flag == 1)
						size = AminoAcidDatabase.G.pMass.size() - 1;
					else
						size = AminoAcidDatabase.G.mass.size() - 1;
					for (int j = 0; j <= size; j++) {
						result.add(AminoAcidDatabase.G.mass(flag).get(j));
					}
					listAminoMass.add(new ArrayList<Double>(result));
					break;
				}
				
				case 'H': {
					int size;
					List<Double> result = new ArrayList<Double>();
					if (flag == 1)
						size = AminoAcidDatabase.H.pMass.size() - 1;
					else
						size = AminoAcidDatabase.H.mass.size() - 1;
					for (int j = 0; j <= size; j++) {
						result.add(AminoAcidDatabase.H.mass(flag).get(j));
					}
					listAminoMass.add(new ArrayList<Double>(result));
					break;
				}
				
				case 'I': {
					int size;
					List<Double> result = new ArrayList<Double>();
					if (flag == 1)
						size = AminoAcidDatabase.I.pMass.size() - 1;
					else
						size = AminoAcidDatabase.I.mass.size() - 1;
					for (int j = 0; j <= size; j++) {
						result.add(AminoAcidDatabase.I.mass(flag).get(j));
					}
					listAminoMass.add(new ArrayList<Double>(result));
					break;
				}
				
				case 'L': {
					int size;
					List<Double> result = new ArrayList<Double>();
					if (flag == 1)
						size = AminoAcidDatabase.L.pMass.size() - 1;
					else
						size = AminoAcidDatabase.L.mass.size() - 1;
					for (int j = 0; j <= size; j++) {
						result.add(AminoAcidDatabase.L.mass(flag).get(j));
					}
					listAminoMass.add(new ArrayList<Double>(result));
					break;
				}
				
				case 'K': {
					int size;
					List<Double> result = new ArrayList<Double>();
					if (flag == 1)
						size = AminoAcidDatabase.K.pMass.size() - 1;
					else
						size = AminoAcidDatabase.K.mass.size() - 1;
					for (int j = 0; j <= size; j++) {
						result.add(AminoAcidDatabase.K.mass(flag).get(j));
					}
					listAminoMass.add(new ArrayList<Double>(result));
					break;
				}
				
				case 'M': {
					int size;
					List<Double> result = new ArrayList<Double>();
					if (flag == 1)
						size = AminoAcidDatabase.M.pMass.size() - 1;
					else
						size = AminoAcidDatabase.M.mass.size() - 1;
					for (int j = 0; j <= size; j++) {
						result.add(AminoAcidDatabase.M.mass(flag).get(j));
					}
					listAminoMass.add(new ArrayList<Double>(result));
					break;
				}
				
				case 'F': {
					int size;
					List<Double> result = new ArrayList<Double>();
					if (flag == 1)
						size = AminoAcidDatabase.F.pMass.size() - 1;
					else
						size = AminoAcidDatabase.F.mass.size() - 1;
					for (int j = 0; j <= size; j++) {
						result.add(AminoAcidDatabase.F.mass(flag).get(j));
					}
					listAminoMass.add(new ArrayList<Double>(result));
					break;
				}
				
				case 'P': {
					int size;
					List<Double> result = new ArrayList<Double>();
					if (flag == 1)
						size = AminoAcidDatabase.P.pMass.size() - 1;
					else
						size = AminoAcidDatabase.P.mass.size() - 1;
					for (int j = 0; j <= size; j++) {
						result.add(AminoAcidDatabase.P.mass(flag).get(j));
					}
					listAminoMass.add(new ArrayList<Double>(result));
					break;
				}
				
				case 'S': {
					int size;
					List<Double> result = new ArrayList<Double>();
					if (flag == 1)
						size = AminoAcidDatabase.S.pMass.size() - 1;
					else
						size = AminoAcidDatabase.S.mass.size() - 1;
					for (int j = 0; j <= size; j++) {
						result.add(AminoAcidDatabase.S.mass(flag).get(j));
					}
					listAminoMass.add(new ArrayList<Double>(result));
					break;
				}
				
				case 'T': {
					int size;
					List<Double> result = new ArrayList<Double>();
					if (flag == 1)
						size = AminoAcidDatabase.T.pMass.size() - 1;
					else
						size = AminoAcidDatabase.T.mass.size() - 1;
					for (int j = 0; j <= size; j++) {
						result.add(AminoAcidDatabase.T.mass(flag).get(j));
					}
					listAminoMass.add(new ArrayList<Double>(result));
					break;
				}
				
				case 'W': {
					int size;
					List<Double> result = new ArrayList<Double>();
					if (flag == 1)
						size = AminoAcidDatabase.W.pMass.size() - 1;
					else
						size = AminoAcidDatabase.W.mass.size() - 1;
					for (int j = 0; j <= size; j++) {
						result.add(AminoAcidDatabase.W.mass(flag).get(j));
					}
					listAminoMass.add(new ArrayList<Double>(result));
					break;
				}
				
				case 'Y': {
					int size;
					List<Double> result = new ArrayList<Double>();
					if (flag == 1)
						size = AminoAcidDatabase.Y.pMass.size() - 1;
					else
						size = AminoAcidDatabase.Y.mass.size() - 1;
					for (int j = 0; j <= size; j++) {
						result.add(AminoAcidDatabase.Y.mass(flag).get(j));
					}
					listAminoMass.add(new ArrayList<Double>(result));
					break;
				}
				
				case 'V': {
					int size;
					List<Double> result = new ArrayList<Double>();
					if (flag == 1)
						size = AminoAcidDatabase.V.pMass.size() - 1;
					else
						size = AminoAcidDatabase.V.mass.size() - 1;
					for (int j = 0; j <= size; j++) {
						result.add(AminoAcidDatabase.V.mass(flag).get(j));
					}
					listAminoMass.add(new ArrayList<Double>(result));
					break;
				}
				
				default: {
					System.out.println("UNKNOWN CHARACTER FOUND");
				}
			}
		}
		return listAminoMass;
	}
	
	/**
	 * Function to calculate and return the combinations of the individual
	 * permethylated masses for the amino acids in the peptide sequence
	 * 
	 * @param peptideSequence
	 *            peptide sequence in form of a string
	 * @param flag
	 *            indicates if it wants permethylated or normal masses of the
	 *            amino acid.
	 * @return returns the list of permethylated masses for the amino acid in
	 *         the given peptide sequence.
	 */
	public static ArrayList<ArrayList<ArrayList<Double>>> getMultipleListResult(List<String> peptideSequence,
	        int flag) {
		
		double current = 0;
		ArrayList<ArrayList<ArrayList<Double>>> MultipleFinalResult = new ArrayList<ArrayList<ArrayList<Double>>>();
		
		// if (flag == 1) {
		for (int i = 0; i < peptideSequence.size(); i++) {
			List<Double> result = new ArrayList<Double>();
			List<Double> resultPeptideMass = new ArrayList<Double>();
			List<ArrayList<Double>> listAminoMass = new ArrayList<ArrayList<Double>>();
			ArrayList<ArrayList<Double>> finalListResult = new ArrayList<ArrayList<Double>>();
			
			listAminoMass = aminoAcidPeptide(peptideSequence.get(i), flag);
			
			result = generatePermutations(listAminoMass, resultPeptideMass, 0, current);
			
			ArrayList<Double> tempListResult = new ArrayList<Double>();
			
			for (int l = 0; l < peptideSequence.get(i).length(); l++) {
				tempListResult.add(l, 0.00);
			}
			
			for (int m = 0; m < result.size(); m++) {
				finalListResult.add(m, tempListResult);
			}
			
			finalListResult = calculatePermutationsValue(listAminoMass, 0, tempListResult, finalListResult);
			MultipleFinalResult.add(finalListResult);
		}
		// }
		
		return MultipleFinalResult;
	}
	
	/**
	 * Recursive function to calculate the sum of all the combinations of the
	 * permethylated masses for the amino acids for the peptide sequence.
	 * 
	 * @param listAminoMass
	 *            list of all the permethylated masses for the amino acid in the
	 *            peptide sequence.
	 * @param result
	 *            list of sum of permethylated masses for the peptide.
	 * @param depth
	 *            number of permethylated masses for the amino acid.
	 * @param current
	 *            current value of the prmethylated mass of the amino acid
	 * @return returns the list of permethylated mass for the peptide
	 */
	private static List<Double> generatePermutations(List<ArrayList<Double>> listAminoMass, List<Double> result,
	        int depth, double current) {
		if (depth == listAminoMass.size()) {
			result.add(current);
			return result;
		}
		
		for (int i = 0; i < listAminoMass.get(depth).size(); ++i) {
			generatePermutations(listAminoMass, result, depth + 1, current + listAminoMass.get(depth).get(i));
		}
		return result;
	}
	
	/**
	 * Recursive function to calculate the individual permethylated mass of all
	 * the combinations for the amino acids in the peptide sequence in a
	 * sequence.
	 * 
	 * @param listAminoMass
	 *            list of all the permethylated masses for the amino acid in the
	 *            peptide sequence
	 * @param depth
	 *            number of permethylated masses for the amino acid.
	 * @param tempListResult
	 *            temporary list of permethylated masses of the amino acid
	 * @param finalListResult
	 *            list of individual of permethylated mass of all the amino
	 *            acids in the give peptide sequence
	 * @return returns the individual permethylated mass of all the amino acids
	 *         in the give peptide sequence.
	 */
	private static ArrayList<ArrayList<Double>> calculatePermutationsValue(List<ArrayList<Double>> listAminoMass,
	        int depth, ArrayList<Double> tempListResult, ArrayList<ArrayList<Double>> finalListResult) {
		
		if (depth == listAminoMass.size()) {
			ArrayList<Double> tempListResult2 = new ArrayList<Double>();
			ArrayList<Double> t_clone = (ArrayList<Double>) tempListResult.clone();
			tempListResult2 = t_clone;
			finalListResult.set(counter, tempListResult2);
			counter = counter + 1;
			if (counter >= finalListResult.size()) {
				counter = 0;
			}
			return finalListResult;
		}
		
		for (int i = 0; i < listAminoMass.get(depth).size(); ++i) {
			Double tempPermethylatedValue = listAminoMass.get(depth).get(i);
			tempListResult.set(depth, tempPermethylatedValue);
			calculatePermutationsValue(listAminoMass, depth + 1, tempListResult, finalListResult);
		}
		return finalListResult;
	}
	
}
