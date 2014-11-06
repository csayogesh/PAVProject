package PAVpointerAnalysisPackage.anderson;

import java.util.Iterator;
import java.util.LinkedList;

public class GlobleState {
	public GlobleState() {
		super();
		this.states = new LinkedList<State>();
	}

	public GlobleState(LinkedList<State> states) {
		super();
		this.states = states;
	}

	private LinkedList<State> states;


	public LinkedList<State> getStates() {
		return states;
	}

	public void setState(State state) {
		if (state == null)
			return;
		if (!states.contains(state))
			this.states.add(state);
		else
			merge(state);
	}

	private void merge(State state) {
		Iterator<State> x = states.iterator();
		while (x.hasNext()) {
			State obj = x.next();
			if (obj.equals(state)) {
				obj.setValues(state.getValues());
				obj.setRhs(state.getRhs());
				break;
			}
		}
	}

	public void printGlobleStates() {
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
			if(x.getState() == null)
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
}