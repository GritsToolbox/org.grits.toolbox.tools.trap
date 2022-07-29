package org.grits.toolbox.tools.trap.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Combinations {
	private StringBuilder	output	= new StringBuilder();
	List<String>			inputstring;
	
	public Combinations(final String str) {
		inputstring = Arrays.asList("Na", "H", "Ca");
		System.out.println("The input string  is  : " + inputstring);
	}
	
	public static void main(String args[]) {
		Combinations combobj = new Combinations("wxyz");
		System.out.println("All possible combinations are :  ");
		combobj.combine();
	}
	
	public void combine() {
		List<String> result = new ArrayList<>();
		List<String> finalResult = combine(0, output, result);
		for (int i = 0; i < finalResult.size(); i++) {
			System.out.println(finalResult.get(i));
		}
	}
	
	private List<String> combine(int start, StringBuilder output, List<String> result) {
		for (int i = start; i < inputstring.size(); ++i) {
			output.append(inputstring.get(i));
			result.add(output.toString());
			// System.out.println(output);
			if (i < inputstring.size())
				combine(i + 1, output, result);
			output.setLength(output.length() - 1);
			output = new StringBuilder();
		}
		
		return result;
	}
}
