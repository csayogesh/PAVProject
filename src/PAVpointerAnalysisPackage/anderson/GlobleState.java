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
		else merge(state);
	}

	private static void merge(State state) {
		Iterator<State> x = states.iterator();
		while(x.hasNext()) {
			State obj = x.next();
			if(obj.equals(state)) {
				obj.setValues(state.getValues());
				obj.setRhs(state.getRhs());
				break;
			}
		}
	}

	public static void printGlobleStates() {
		Iterator<State> it = states.iterator();
		while(it.hasNext())
			System.out.println(it.next());
	}
}