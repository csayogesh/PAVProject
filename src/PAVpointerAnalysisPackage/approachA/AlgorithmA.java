package PAVpointerAnalysisPackage.approachA;

import com.ibm.wala.ssa.IR;

public class AlgorithmA {

	public static void performAnalysisOnMethod(IR x) {
		System.out.println(x);
		Graph graph = new Graph();
		graph.CreateFromIR(x);
	}
}
