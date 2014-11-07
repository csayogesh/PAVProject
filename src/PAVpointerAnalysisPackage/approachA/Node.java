package PAVpointerAnalysisPackage.approachA;

import java.util.Iterator;
import java.util.LinkedList;

public class Node {
	public Node() {
		super();
		id = null;
		edges = new LinkedList<DirectedEdge>();
		marked = true;
	}

	private String id;
	private LinkedList<DirectedEdge> edges;
	private boolean marked;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public LinkedList<DirectedEdge> getEdges() {
		return edges;
	}

	public void setEdges(LinkedList<DirectedEdge> edges) {
		this.edges = edges;
	}

	public boolean isMarked() {
		return marked;
	}

	public void setMarked(boolean marked) {
		this.marked = marked;
	}

	@Override
	public String toString() {
		String str = "" + id;
		Iterator<DirectedEdge> it = edges.iterator();
		while (it.hasNext())
			str += "\t"+it.next().toString() + "\n";
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

	public void setEdge(DirectedEdge e) {
		if (!edges.contains(e))
			edges.add(e);
	}
}