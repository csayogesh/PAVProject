package PAVpointerAnalysisPackage.approachA;

import java.util.Iterator;
import java.util.LinkedList;

import PAVpointerAnalysisPackage.anderson.*;

public class Node {
	public Node() {
		super();
		id = null;
		edges = new LinkedList<Node>();
		cs = new LinkedList<GlobalState>();
		gs = new GlobalState();
		marked = true;
		returnVar = null;
	}

	private String id, returnVar;
	private LinkedList<Node> edges;
	private GlobalState gs;
	private LinkedList<GlobalState> cs;
	private boolean marked;

	public String getId() {
		return id;
	}

	public LinkedList<GlobalState> getCs() {
		return cs;
	}

	public void setCs(GlobalState cs) {
		if (!this.cs.contains(cs)) {
			cs.add(gs);
			this.cs.add(cs);
		}
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isMarked() {
		return marked;
	}

	public void setMarked(boolean marked) {
		this.marked = marked;
	}

	@Override
	public String toString() {
		String str = "";
		Iterator<Node> it = edges.iterator();
		while (it.hasNext()) {
			str += this.id + " - " + it.next().id + "\n";
			Iterator<GlobalState> it1 = cs.iterator();
			while (it1.hasNext()) {
				str += it1.next().toString() + "\n";
			}
		}
		return str;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Node other = (Node) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public void setEdge(Node e) {
		if (!edges.contains(e))
			edges.add(e);
	}

	public GlobalState getGs() {
		return gs;
	}

	public String getReturnVar() {
		return returnVar;
	}

	public void setGs(GlobalState gs) {
		this.gs = gs;
	}

	public LinkedList<Node> getEdges() {
		return edges;
	}

	public void setEdges(LinkedList<Node> edges) {
		this.edges = edges;
	}

	public void setReturnVar(String string) {
		returnVar = string;
	}

	public GlobalState getGs(String callString) {
		GlobalState gs = new GlobalState();
		gs.setCallString(callString);
		Iterator<GlobalState> it = cs.iterator();
		while (it.hasNext()) {
			GlobalState x = it.next();
			if (x.equals(gs))
				return x;
		}
		gs.setCallString(callString);
		gs.add(this.gs);
		cs.add(gs);
		return gs;
	}

	public void makeProcedureCall(String callString) {
		Iterator<State> it = gs.getStates().iterator();
		while (it.hasNext()) {
			State state = it.next();
			if (state.getClass() == InvokeMethod.class) {
				InvokeMethod i = (InvokeMethod) state;
				Graph g = Graph.getMethodG(i.getMethod());
				int j = 0;
				for (String str : i.arguments) {
					GlobalState x = this.getGs(callString);
					State st = x.getState(str);
					g.passArgument(
							callString + "$" + i.getMethod() + i.getLineNo(),
							st, j + 1);
					j++;
				}
				g.runKildallCallString(callString + "$" + i.getMethod()
						+ i.getLineNo());
				g.retrieveParameters(
						callString + "$" + i.getMethod() + i.getLineNo(), i,
						this, callString);
			}
		}
	}

	public State getState(String string, String string2) {
		return this.getGs(string).getState(string2);
	}
}