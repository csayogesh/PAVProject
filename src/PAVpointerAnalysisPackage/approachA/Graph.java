package PAVpointerAnalysisPackage.approachA;

import java.util.LinkedList;

import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSACFG;

public class Graph {
	
	private LinkedList<Node> nodes;

	public void CreateFromIR(IR x) {
		SSACFG cfg = x.getControlFlowGraph();
		System.out.println(cfg);
		String[] arr = cfg.toString().split("\n");
		for(int i = 0; i < arr.length; i++) {
			if(arr[i].contains("\\[") && arr[i].contains("\\]"))
				createNode(arr[i]);
		}
	}

	private void createNode(String string) {
		Node node = new Node();
	}
}