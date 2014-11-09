package PAVpointerAnalysisPackage.approachA;

import java.util.Iterator;
import java.util.LinkedList;

import PAVpointerAnalysisPackage.anderson.*;

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
		while (it.hasNext())
			it.next().getGs().getStates().remove(x);
	}
}