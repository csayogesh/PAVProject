package PAVpointerAnalysisPackage.approachA;

import java.util.Iterator;
import java.util.LinkedList;

import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSACFG;

public class Graph {

	public Graph() {
		super();
		nodes = new LinkedList<Node>();
	}

	private LinkedList<Node> nodes;

	public void createFromIR(IR x) {
		SSACFG cfg = x.getControlFlowGraph();
		System.out.println(cfg);
		String[] arr = cfg.toString().split("\n");
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].contains("["))
				createNode(arr[i]);
		}
		Node from = null, to = null;
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].contains("["))
				from = findNode(arr[i].split("\\[")[0]);
			else {
				to = getNodeToLink(arr[i]);
				if (to == null || from == null)
					continue;
				from.setEdge(to);
			}
		}
	}

	private Node getNodeToLink(String arr) {
		return findNode(arr.split(" ")[5]);
	}

	private Node findNode(String string) {
		Node res = null;
		Iterator<Node> it = nodes.iterator();
		Node y = new Node();
		y.setId(string);
		while (it.hasNext()) {
			Node x = it.next();
			if (y.equals(x)) {
				res = x;
				break;
			}
		}
		return res;
	}

	private void createNode(String string) {
		Node node = new Node();
		node.setId(string.split("\\[")[0]);
		setNode(node);
	}

	private void setNode(Node node) {
		if (!nodes.contains(node))
			nodes.add(node);
	}

	public LinkedList<Node> getNodes() {
		return nodes;
	}

	public void setNodes(LinkedList<Node> nodes) {
		this.nodes = nodes;
	}

	@Override
	public String toString() {
		String str = "";
		Iterator<Node> it = nodes.iterator();
		while (it.hasNext())
			str += it.next().toString() + "\n";
		return str;
	}
}