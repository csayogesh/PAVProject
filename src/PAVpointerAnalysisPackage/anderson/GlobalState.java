package PAVpointerAnalysisPackage.anderson;

import java.util.Iterator;
import java.util.LinkedList;

public class GlobalState {
	public GlobalState() {
		super();
		this.states = new LinkedList<State>();
	}

	public boolean add(GlobalState gs) {
		boolean res = false;
		Iterator<State> it = gs.states.iterator();
		while (it.hasNext())
			try {
				State state = (State) it.next().clone();
				state.setGs(this);
				res = this.setState(state);
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		return res;
	}

	public GlobalState(LinkedList<State> states) {
		super();
		this.states = states;
	}

	private LinkedList<State> states;

	public LinkedList<State> getStates() {
		return states;
	}

	public boolean setState(State state) {
		if (state == null)
			return false;
		boolean res = false;
		if (!states.contains(state)) {
			state.setGs(this);
			this.states.add(state);
			res = true;
		} else {
			state.setGs(this);
			res = merge(state);
		}
		return res;
	}

	private boolean merge(State state) {
		Iterator<State> x = states.iterator();
		boolean res = false;
		while (x.hasNext()) {
			State obj = x.next();
			if (obj.equals(state)) {
				res = obj.setValues(state.getValues());
				if (res)
					obj.setRhs(state.getRhs());
				else
					res = obj.setRhs(state.getRhs());
				break;
			}
		}
		return res;
	}

	public void printGlobalStates() {
		Iterator<State> it = states.iterator();
		while (it.hasNext()) {
			State x = it.next();
			x.displayLHS = true;
			System.out.println(x);
		}
	}

	public void linkAllStates() {
		Iterator<State> it = states.iterator();
		while (it.hasNext())
			link(it.next());
	}

	private void link(State next) {
		Iterator<Variable> it = next.getRhs().iterator();
		while (it.hasNext()) {
			Variable x = it.next();
			x.setState(findState(x));
			if (x.getState() == null)
				x.setState(findLhsState(x));
		}
		Variable x = next.getLhs();
		x.setState(findLhsState(x));
	}

	public State findLhsState(Variable x) {
		if (x.getMember() == null)
			return null;
		Variable y = new Variable();
		y.setName(x.getName());
		x.displayMember = true;
		return findState(y);
	}

	public State findState(Variable x) {
		State state = new State();
		state.setLhs(x);
		Iterator<State> it = states.iterator();
		while (it.hasNext()) {
			State res = it.next();
			if (res.equals(state))
				return res;
		}
		return null;
	}

	@Override
	public String toString() {
		Iterator<State> it = states.iterator();
		String str = "";
		while (it.hasNext()) {
			State st = it.next();
			st.displayLHS = true;
			str += "" + st.toString() + "\n";
		}
		return str;
	}
}