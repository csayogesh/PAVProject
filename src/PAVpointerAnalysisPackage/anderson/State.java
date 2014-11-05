package PAVpointerAnalysisPackage.anderson;

import java.util.Iterator;
import java.util.LinkedList;

public class State {
	public State() {
		super();
		this.lhs = null;
		this.rhs = new LinkedList<Variable>();
		this.values = new LinkedList<String>();
	}

	public State(Variable lhs, LinkedList<Variable> rhs,
			LinkedList<String> values) {
		super();
		this.lhs = lhs;
		this.rhs = rhs;
		this.values = values;
	}

	private Variable lhs;
	private LinkedList<Variable> rhs;
	private LinkedList<String> values;

	public Variable getLhs() {
		return lhs;
	}

	public void setLhs(Variable lhs) {
		this.lhs = lhs;
	}

	public LinkedList<String> getValues() {
		return values;
	}

	public void setValue(String string) {
		// TODO Auto-generated method stub
		if (values.contains(string))
			System.out.println("This string is already in this state");
		else
			values.add(string);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lhs == null) ? 0 : lhs.hashCode());
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
		State other = (State) obj;
		if (lhs == null) {
			if (other.lhs != null)
				return false;
		} else if (!lhs.equals(other.lhs))
			return false;
		return true;
	}

	public void setValues(LinkedList<String> values2) {
		Iterator<String> it = values2.iterator();
		while (it.hasNext()) {
			setValue(it.next());
		}
	}

	public void setRhs(Variable rhs1) {
		if (!rhs.contains(rhs1))
			rhs.add(rhs1);
	}

	public LinkedList<Variable> getRhs() {
		return rhs;
	}

	public void setRhs(LinkedList<Variable> rhs) {
		Iterator<Variable> it = rhs.iterator();
		while (it.hasNext())
			setRhs(it.next());
	}

	@Override
	public String toString() {
		return lhs + " → {" + rhs + ", " + values + "}";
	}
}
