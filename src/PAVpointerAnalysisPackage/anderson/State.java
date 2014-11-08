package PAVpointerAnalysisPackage.anderson;

import java.util.Iterator;
import java.util.LinkedList;

public class State {
	public State() {
		super();
		this.lhs = null;
		this.rhs = new LinkedList<Variable>();
		this.values = new LinkedList<String>();
		this.gs = null;
		this.displayLHS = true;
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
	private GlobalState gs;
	public boolean displayLHS;

	@Override
	protected Object clone() throws CloneNotSupportedException {
		State state = new State();
		state.lhs = (Variable) lhs.clone();
		state.lhs.setcState(state);
		Iterator<Variable> it = rhs.iterator();
		while (it.hasNext()) {
			Variable x = (Variable) it.next().clone();
			x.setcState(state);
			state.setRhs(x);
		}
		Iterator<String> i = this.values.iterator();
		while(i.hasNext())
			state.setValue(i.next());
		state.gs = null;
		state.displayLHS = this.displayLHS;
		return state;
	}

	public Variable getLhs() {
		return lhs;
	}

	public void setLhs(Variable lhs) {
		this.lhs = lhs;
	}

	public LinkedList<String> getValues() {
		return values;
	}

	public boolean setValue(String string) {
		boolean res = false;
		if (values.contains(string))
			;
		else {
			res = true;
			values.add(string);
		}
		return res;
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

	public boolean setValues(LinkedList<String> values2) {
		Iterator<String> it = values2.iterator();
		boolean res = false;
		while (it.hasNext()) {
			res = setValue(it.next());
		}
		return res;
	}

	public boolean setRhs(Variable rhs1) {
		if (!rhs.contains(rhs1)) {
			rhs.add(rhs1);
			return true;
		}
		return false;
	}

	public LinkedList<Variable> getRhs() {
		return rhs;
	}

	public boolean setRhs(LinkedList<Variable> rhs) {
		Iterator<Variable> it = rhs.iterator();
		boolean res = false;
		while (it.hasNext())
			res = setRhs(it.next());
		return res;
	}

	@Override
	public String toString() {
		String lhs = "", end = "";
		String[] lhsArr = null;
		if (displayLHS) {
			lhsArr = this.lhs.getArray();
			if (lhsArr != null)
				for (int i = 0; i < lhsArr.length; i++)
					lhsArr[i] += " → {";

			lhs = this.lhs.toString() + " → {";
			end = "}";
		}
		String rhs = "";
		Iterator<Variable> it = this.rhs.iterator();
		while (it.hasNext()) {
			Variable x = it.next();
			if (x.getState() == null) {
				rhs += x.toString();
				if (it.hasNext())
					rhs += ", ";
				continue;
			}
			x.getState().displayLHS = false;
			if (x.getMember() != null && x.displayMember) {
				String[] arr = x.getArray();
				if (arr != null)
					for (int i = 0; i < arr.length; i++)
						rhs += arr[i];
				else
					rhs += "." + x.getMember();
			} else
				rhs += x.getState().toString();
			if (it.hasNext())
				rhs += ", ";
		}
		String values = "";
		if (this.values.size() > 0) {
			Iterator<String> i = this.values.iterator();
			while (i.hasNext()) {
				values += "[" + i.next() + "]";
				if (i.hasNext())
					values += ", ";
			}
		}
		String res = "";
		String comma = "";
		if (!rhs.equals("") && !values.equals(""))
			comma = ", ";
		if (lhsArr != null) {
			for (int i = 0; i < lhsArr.length; i++) {
				String tmp = lhsArr[i] + rhs + comma + values + end;
				if (i < lhsArr.length - 1)
					tmp += "\n";
				res += tmp;
			}
			return res;
		}

		return (lhs + rhs + comma + values + end);
	}

	public static void main(String[] args) {
		new State().toString();
	}

	public GlobalState getGs() {
		return gs;
	}

	public void setGs(GlobalState gs) {
		this.gs = gs;
	}

}
