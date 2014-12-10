package PAVpointerAnalysisPackage.approachA;

import java.util.Iterator;
import java.util.LinkedList;

import PAVpointerAnalysisPackage.SetUpAnalysis;
import PAVpointerAnalysisPackage.anderson.*;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSACFG;

public class Graph {

	public Graph() {
		super();
		ir = null;
		nodes = new LinkedList<Node>();
	}

	static {
		methodGraphs = new LinkedList<Graph>();
	}

	private LinkedList<Node> nodes;
	private String method;
	private IR ir;
	private static LinkedList<Graph> methodGraphs;

	public static void printMethods() {
		Iterator<Graph> it = methodGraphs.iterator();
		while (it.hasNext())
			System.out.println(it.next());
	}

	public void createIPGFromIR(SetUpAnalysis setup) {
		IR main = setup.getTargetNode(setup.getAnalysisMethod(),
				setup.getAnalysisClass()).getIR();
		addNewMethod(main, setup.getAnalysisMethod());
		String[] arr = main.toString().split("\n");
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].contains("invokestatic")) {
				String arr1[] = arr[i].split(" ");
				String method = arr1[9].split("\\(")[0];
				CGNode t = setup
						.getTargetNode(method, setup.getAnalysisClass());
				addNewMethod(t.getIR(), method);
			}
		}
	}

	private void setMethod(String method) {
		this.method = method;
	}

	private void addNewMethod(IR ir, String method) {
		Graph graph = new Graph();
		graph.setIr(ir);
		graph.setMethod(method);
		if (!methodGraphs.contains(graph)) {
			graph.createFromIR(ir);
			methodGraphs.add(graph);
		}
		return;
	}

	private void setIr(IR ir) {
		this.ir = ir;
	}

	public void createFromIR(IR x) {
		SSACFG cfg = x.getControlFlowGraph();
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
		String str = "" + method + "\n";
		Iterator<Node> it = nodes.iterator();
		while (it.hasNext())
			str += it.next().toString() + "\n";
		return str;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((method == null) ? 0 : method.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Graph other = (Graph) obj;
		if (method == null) {
			if (other.method != null)
				return false;
		} else if (!method.equals(other.method))
			return false;
		return true;
	}

	public void initializeStates(IR ir) {
		String[] lines = ir.toString().split("\n");
		boolean istart = false;
		Node cur = null;
		for (int i = 0; i < lines.length; i++) {
			if (istart) {
				if (lines[i].matches("BB[0-9]*")) {
					cur = findNode(lines[i]);
				} else {
					cur.getGs().setState(
							AndersonAnalysis.fetchState(lines[i], cur.getGs()));
				}
			}
			if (lines[i].equals("Instructions:"))
				istart = true;
		}
	}

	public boolean kildallIterateOnce(boolean b) {
		boolean change = false;
		Iterator<Node> it = this.nodes.iterator();
		while (it.hasNext()) {
			Node node = it.next();
			if (node == null)
				continue;
			if (node.isMarked()) {
				Iterator<Node> edges = node.getEdges().iterator();
				while (edges.hasNext()) {
					Node child = edges.next();
					if (child == null)
						continue;
					if (child.getGs().add(node.getGs())) {
						change = true;
						child.setMarked(true);
						Iterator<Node> i = child.getEdges().iterator();
						while (i.hasNext())
							i.next().setMarked(true);
					}
					if (b)
						child.getGs().removeUnrelatedStates();
				}
				node.setMarked(false);
			}
		}
		return change;
	}

	public void runKildall() {
		boolean change = true;
		setAllNodesMarked();
		while (change) {
			change = kildallIterateOnce(false);
		}
	}

	private void setAllNodesMarked() {
		Iterator<Node> it = nodes.iterator();
		while (it.hasNext())
			it.next().setMarked(true);
	}

	public void linkStates() {
		Iterator<Node> it = nodes.iterator();
		while (it.hasNext())
			it.next().getGs().linkAllStates();
	}

	public void removeUnelatedStates() {
		Iterator<Node> it = nodes.iterator();
		while (it.hasNext()) {
			State x;
			while ((x = it.next().getGs().removeUnrelatedStates()) != null) {
				this.removeState(x);
			}
		}
	}

	private void removeState(State x) {
		Iterator<Node> it = nodes.iterator();
		while (it.hasNext()) {
			Node y = it.next();
			y.getGs().getStates().remove(x);
			Iterator<State> i = y.getGs().getStates().iterator();
			while (i.hasNext())
				i.next().getRhs().remove(x.getLhs());
		}
	}

	public static void initializeMethodGraphs() {
		Iterator<Graph> it = methodGraphs.iterator();
		while (it.hasNext()) {
			Graph x = it.next();
			x.initializeStates(x.ir);
		}
	}

}