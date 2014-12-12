package PAVpointerAnalysisPackage.approachA;

import java.util.Iterator;
import java.util.LinkedList;

import PAVpointerAnalysisPackage.SetUpAnalysis;
import PAVpointerAnalysisPackage.anderson.*;

import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSACFG;
import com.ibm.wala.ssa.SSAInstruction;

public class Graph {

	public Graph() {
		super();
		ir = null;
		nodes = new LinkedList<Node>();
	}

	static {
		methodGraphs = new LinkedList<Graph>();
	}

	public String getMethod() {
		return method;
	}

	private LinkedList<Node> nodes;
	private String method;
	private IR ir;
	private static LinkedList<Graph> methodGraphs;

	public static void printMethods() {
		Iterator<Graph> it = methodGraphs.iterator();
		Graph x;
		while (it.hasNext()) {
			x = it.next();
			x.linkStates();
			System.out.println(x);
		}
	}

	public void createIPGFromIR(SetUpAnalysis setup) {
		LinkedList<String> q = new LinkedList<String>();
		q.addLast(setup.getAnalysisMethod());
		while (!q.isEmpty()) {
			String str = q.removeFirst();
			IR main = setup.getTargetNode(str, setup.getAnalysisClass())
					.getIR();
			addNewMethod(main, str);
			String[] arr = main.toString().split("\n");
			for (int i = 0; i < arr.length; i++) {
				if (arr[i].contains("invokestatic")) {
					int m;
					if (arr[i].contains(" = invokestatic <"))
						m = 9;
					else
						m = 7;
					String arr1[] = arr[i].split(" ");
					String method = arr1[m].split("\\(")[0];
					CGNode t = setup.getTargetNode(method,
							setup.getAnalysisClass());
					addNewMethod(t.getIR(), method);
					q.addLast(method);
				}
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
					if (lines[i].contains("return")) {
						String[] arr = lines[i].split(" ");
						cur.setReturnVar(arr[4]);
						continue;
					}
					cur.getGs().setState(
							AndersonAnalysis.fetchState(lines[i], cur.getGs()));
				}
			}
			if (lines[i].equals("Instructions:"))
				istart = true;
		}
	}

	public boolean kildallIterateOnce(boolean b, String callString) {
		boolean change = false;
		Iterator<Node> it = this.nodes.iterator();
		while (it.hasNext()) {
			Node node = it.next();
			if (node == null)
				continue;
			if (node.isMarked()) {
				Iterator<Node> edges = node.getEdges().iterator();
				node.makeProcedureCall(callString);
				while (edges.hasNext()) {
					Node child = edges.next();
					if (child == null)
						continue;
					boolean res = child.getGs(callString).add(
							node.getGs(callString));
					if (res) {
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

	public void runKildallCallString(String callString) {
		boolean change = true;
		setAllNodesMarked();
		while (change) {
			change = kildallIterateOnce(false, callString);
		}
	}

	public void runKildall() {
		boolean change = true;
		setAllNodesMarked();
		while (change) {
			change = kildallIterateOnce(false, "");
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
			x.createReturnVars();
		}
	}

	private void createReturnVars() {
		String[] lines = ir.toString().split("\n");
		boolean istart = false;
		Node cur = null;
		for (int i = 0; i < lines.length; i++) {
			if (istart) {
				if (lines[i].matches("BB[0-9]*")) {
					cur = findNode(lines[i]);
				} else {
					if (lines[i].contains("return"))
						cur.setReturnVar(lines[i].split(" ")[4]);
				}
			}
			if (lines[i].equals("Instructions:"))
				istart = true;
		}
	}

	public static void initializeCallLinks() {
		Iterator<Graph> it = methodGraphs.iterator();
		while (it.hasNext()) {
			Graph x = it.next();
			Iterator<SSAInstruction> it1 = x.ir.iterateAllInstructions();
			while (it1.hasNext())
				System.out.println();
		}
	}

	public static void runKildall(String analysisMethod) {
		Iterator<Graph> it = methodGraphs.iterator();
		while (it.hasNext()) {
			Graph g = it.next();
			if (g.getMethod().equals(analysisMethod))
				g.runKildallCallString("main0");
		}
	}

	public static Graph getMethodG(String method) {
		Iterator<Graph> it = methodGraphs.iterator();
		while (it.hasNext()) {
			Graph g = it.next();
			if (g.getMethod().equals(method))
				return g;
		}
		System.out.println(method);
		try {
			throw new Exception("Should not reach here");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void passArgument(String callString, State st, int i) {
		Node node = this.getNode("BB0");
		GlobalState gs = node.getGs(callString);
		State y = (State) st.clone();
		y.getLhs().setName("v" + i);
		gs.setState(y);
	}

	private Node getNode(String string) {
		Iterator<Node> it = nodes.iterator();
		while (it.hasNext()) {
			Node x = it.next();
			if (x.getId().equals(string))
				return x;
		}
		return null;
	}

	public void retrieveParameters(String string, InvokeMethod i, Node node,
			String callString) {
		Iterator<Node> it = this.nodes.iterator();
		Node x;
		while (it.hasNext()) {
			x = it.next();
			if (x.getReturnVar() != null) {
				int j = 0;
				for (String str : i.getArguments()) {
					State s1 = x.getState(string, "v" + (j + 1));
					node.getState(callString, str).setRhs(s1.getRhs());
					node.getState(callString, str).setValues(s1.getValues());
					j++;
				}
				if (i.getLhs().getName().equals(" ")) {
					node.getState(callString, i.getLhs().getName()).setRhs(
							x.getState(string, x.getReturnVar()).getRhs());
					node.getState(callString, i.getLhs().getName()).setValues(
							x.getState(string, x.getReturnVar()).getValues());
				}
			}
		}
	}
}