package PAVpointerAnalysisPackage.anderson;

import java.util.Iterator;
import java.util.LinkedList;

public class GlobleState {
	private static LinkedList<State> states;

	static {
		states = new LinkedList<State>();
	}

	public static LinkedList<State> getStates() {
		return states;
	}

	public static void setState(State state) {
		if (state == null)
			return;
		if (!states.contains(state))
			GlobleState.states.add(state);
		else
			merge(state);
	}

	private static void merge(State state) {
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

	public static void printGlobleStates() {
		Iterator<State> it = states.iterator();
		while (it.hasNext()) {
			State x = it.next();
			x.displayLHS = true;
			System.out.println(x);
		}
	}

	public static void linkAllStates() {
		Iterator<State> it = states.iterator();
		while (it.hasNext())
			link(it.next());
	}

	private static void link(State next) {
		Iterator<Variable> it = next.getRhs().iterator();
		while (it.hasNext()) {
			Variable x = it.next();
			x.setState(findState(x));
		}
		Variable x = next.getLhs();
		x.setState(findLhsState(x));
	}

	private static State findLhsState(Variable x) {
		if (x.getMember() == null)
			return null;
		Variable y = new Variable();
		y.setName(x.getName());
		return findState(y);
	}

	private static State findState(Variable x) {
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