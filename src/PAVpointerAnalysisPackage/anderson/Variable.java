package PAVpointerAnalysisPackage.anderson;

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
		this.displayMember = false;
	}
	private String name;
	private String member;
	private LinkedList<String> values;
	private State state;
	public boolean displayMember;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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
		String str = "";
		if(member != null)
			str = "." + member;
		
		String name = this.name;
		if(this.state != null) {
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
	 * @param state the state to set
	 */
	public void setState(State state) {
		this.state = state;
	}
	public String[] getArray() {
		String arr[] = null;
		if(this.member != null && this.state != null) {
			this.state.displayLHS = false;
			String str = this.state.toString();
			arr = str.split("]");
			for(int i = 0; i < arr.length; i++)
				arr[i] += "]." + member;
		}
		return arr;
	}
}
