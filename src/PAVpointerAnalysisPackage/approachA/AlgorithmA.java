package PAVpointerAnalysisPackage.approachA;

import com.ibm.wala.ssa.IR;

public class AlgorithmA {

	public static void performAnalysisOnMethod(IR x) {
		Graph graph = new Graph();
		graph.createFromIR(x);
		graph.initializeStates(x);
		graph.runKildall();
		graph.removeUnelatedStates();
		graph.linkStates();
		System.out.println("Pointer Analysis Using Algorithm A");
		System.out.println(graph);
	}
}
