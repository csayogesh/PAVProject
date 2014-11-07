package PAVpointerAnalysisPackage.approachA;

import PAVpointerAnalysisPackage.anderson.GlobalState;

public class DirectedEdge {
	public DirectedEdge() {
		super();
		node = null;
		es = new GlobalState();
	}

	private Node node;
	private GlobalState es;

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public GlobalState getEs() {
		return es;
	}

	public void setEs(GlobalState es) {
		this.es = es;
	}

	@Override
	public String toString() {
		return node.getId();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((node == null) ? 0 : node.hashCode());
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
		DirectedEdge other = (DirectedEdge) obj;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		return true;
	}
}
