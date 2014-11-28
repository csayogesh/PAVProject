package PAVpointerAnalysisPackage.anderson;

import java.util.Iterator;
import java.util.LinkedList;

public class Variable {
	public Variable(String name, String member, LinkedList<String> values,
			State state) {
		super();
		this.name = name;
		this.member = member;
		this.values = values;
		this.state = state;
		this.displayMember = false;
	}

	public Variable() {
		super();
		this.name = null;
		this.member = null;
		this.values = new LinkedList<String>();
		this.state = null;
		this.cState = null;
		this.displayMember = false;
	}

	private String name;
	private String member;
	private LinkedList<String> values;
	private State state, cState;
	public boolean displayMember;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name.contains(":#null")) {
			this.name = name.split(":")[0];
			this.setValue("null");
			return;
		}
		this.name = name;
	}

	private void setValue(String string) {
		if (values.contains(string))
			return;
		values.add(string);
	}

	public String getMember() {
		return member;
	}

	public void setMember(String member) {
		this.member = member;
	}

	public LinkedList<String> getValues() {
		return values;
	}

	@Override
	public String toString() {
		if (values.size() > 0)
			return values.toString();
		String str = "";
		if (member != null)
			str = "." + member;

		String name = this.name;
		if (this.state != null) {
			state.displayLHS = false;
			name = state.toString();
		}
		return name + str;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((member == null) ? 0 : member.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Variable other = (Variable) obj;

		if (member == null) {
			if (other.member != null)
				return false;
		} else if (!member.equals(other.member))
			return false;
		if (member != null && member.equals(other.member)) {
			Variable x = new Variable();
			x.setName(other.getName());
			boolean old = this.displayMember;
			// if (other.getState() != null && other.getState().getGs() != null)
			// {
			State state = cState.getGs().findLhsState(this);
			this.displayMember = old;
			Iterator<Variable> it = null;
			if (state != null)
				it = state.getRhs().iterator();
			if (it != null)
				while (it.hasNext())
					if (it.next().equals(x))
						return true;
		}
		// }
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	/**
	 * @return the state
	 */
	public State getState() {
		return state;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(State state) {
		this.state = state;
	}

	public String[] getArray() {
		String arr[] = null;
		if (this.member != null && this.state != null) {
			this.state.displayLHS = false;
			String str = this.state.toString();
			arr = str.split(", ");
			for (int i = 0; i < arr.length; i++)
				// if (!arr[i].equals("[null]"))
				arr[i] += "." + member;
		} else if (this.member != null) {
			arr = new String[] { "" + name + "." + member };
		}
		return arr;
	}

	public State getcState() {
		return cState;
	}

	public void setcState(State cState) {
		this.cState = cState;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		Variable v = new Variable();
		v.name = this.name;
		v.member = this.member;
		Iterator<String> it = this.values.iterator();
		while (it.hasNext())
			v.setValue(it.next());
		v.displayMember = this.displayMember;
		v.state = null;
		v.cState = null;
		return v;
	}
}
