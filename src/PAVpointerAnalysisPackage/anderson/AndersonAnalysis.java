package PAVpointerAnalysisPackage.anderson;

import java.util.Iterator;

import PAVpointerAnalysisPackage.approachA.InvokeMethod;

import com.ibm.wala.classLoader.IClass;
import com.ibm.wala.classLoader.IField;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.core.tests.callGraph.CallGraphTestUtil;
import com.ibm.wala.examples.drivers.PDFWalaIR;
import com.ibm.wala.ipa.callgraph.AnalysisCache;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.AnalysisScope;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.callgraph.CallGraph;
import com.ibm.wala.ipa.callgraph.CallGraphBuilder;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.cha.ClassHierarchy;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ssa.IR;
import com.ibm.wala.ssa.SSACFG;
import com.ibm.wala.ssa.SSAGetInstruction;
import com.ibm.wala.ssa.SSAIndirectionData;
import com.ibm.wala.ssa.SSAInstruction;
import com.ibm.wala.ssa.SSAPutInstruction;
import com.ibm.wala.ssa.SymbolTable;
import com.ibm.wala.types.FieldReference;
import com.ibm.wala.util.CancelException;
import com.ibm.wala.util.config.AnalysisScopeReader;
import com.ibm.wala.util.io.FileProvider;
import com.ibm.wala.ssa.analysis.ExplodedControlFlowGraph;

@SuppressWarnings("unused")
public class AndersonAnalysis {

	public static void performAnalysisOnMethod(IR ir) {
		if (ir == null)
			return;
		String str = ir.toString();
		String[] lines = str.split("\n");
		boolean istart = false;
		GlobalState gs = new GlobalState();
		for (int i = 0; i < lines.length; i++) {
			if (istart)
				gs.setState(fetchState(lines[i], gs));
			if (lines[i].equals("Instructions:"))
				istart = true;
		}
		System.out.println("Analysis Using Anderson's " + "Algorithm:");
		gs.linkAllStates();
		gs.printGlobalStates();
	}

	public static State fetchState(String string, GlobalState gs) {
		State state = null;
		if (string.contains(" = new <"))
			state = extractNew(string, gs);
		else if (string.contains(" putfield v"))
			state = extractPutfield(string, gs);
		else if (string.contains(" getfield <"))
			state = extractGetfield(string, gs);
		else if (string.contains(" = phi ") && string.contains(":#null,"))
			state = extractPhi(string, gs);
		else if (string.contains(" = phi ") && !string.contains(":#"))
			state = extractPhi(string, gs);
		else if (string.contains("arraystore"))
			state = extractArrayStore(string, gs);
		else if (string.contains("arrayload"))
			state = extractArrayload(string, gs);
		else if (string.contains("invokestatic"))
			state = extractInvokeStatic(string, gs);
		// else if (string.contains("return"))
		// state = extractReturn(string, gs);
		return state;
	}

	private static State extractReturn(String string, GlobalState gs) {
		System.out.println(string);
		return new State();
	}

	private static State extractInvokeStatic(String string, GlobalState gs) {
		InvokeMethod state = new InvokeMethod();
		state.setGs(gs);
		int arg, m, lhs;
		String arr1[] = string.split(" ");
		Variable v = new Variable();
		state.setLhs(v);
		if (string.contains(" = invokestatic <")) {
			m = 9;
			arg = 11;
			v.setName(arr1[3]);
		} else {
			v.setName(" ");
			arg = 9;
			m = 7;
		}
		state.setArguments(arr1[arg].split(","));
		state.setMethod(arr1[m].split("\\(")[0]);
		state.setLineNo(arr1[0]);
		return state;
	}

	public static State extractArrayload(String string, GlobalState gs) {
		State state = new State();
		state.setGs(gs);
		Variable lhs = new Variable();
		lhs.setcState(state);
		String[] arr = string.split(" ");
		lhs.setName(arr[3]);
		state.setLhs(lhs);
		Variable rhs1 = new Variable();
		rhs1.setcState(state);
		rhs1.setName(arr[6].split("\\[")[0]);
		state.setRhs(rhs1);
		return state;
	}

	public static State extractArrayStore(String string, GlobalState gs) {
		State state = new State();
		state.setGs(gs);
		Variable lhs = new Variable();
		lhs.setcState(state);
		String[] arr = string.split(" ");
		lhs.setName(arr[4].split("\\[")[0]);
		state.setLhs(lhs);
		Variable rhs1 = new Variable();
		rhs1.setcState(state);
		rhs1.setName(arr[6]);
		state.setRhs(rhs1);
		return state;
	}

	public static State extractPhi(String string, GlobalState gs) {
		State state = new State();
		state.setGs(gs);
		String[] arr = string.split(" ");
		Variable lhs = new Variable();
		lhs.setcState(state);
		lhs.setName(arr[11]);
		state.setLhs(lhs);

		String[] arr2 = arr[15].split(",");
		for (int i = 0; i < arr2.length; i++) {
			if (arr2[i].contains("#"))
				continue;
			Variable rhs1 = new Variable();
			rhs1.setcState(state);
			rhs1.setName(arr2[i]);
			state.setRhs(rhs1);
		}

		return state;
	}

	public static State extractGetfield(String string, GlobalState gs) {
		State state = new State();
		state.setGs(gs);
		String[] arr = string.split(" ");
		Variable lhs = new Variable();
		lhs.setcState(state);
		lhs.setName(arr[3]);
		state.setLhs(lhs);
		Variable rhs1 = new Variable();
		rhs1.setcState(state);
		rhs1.setName(arr[12].split("\\(")[0]);
		rhs1.setMember(arr[9].split(",")[0]);
		state.setRhs(rhs1);
		return state;
	}

	public static State extractPutfield(String string, GlobalState gs) {
		State state = new State();
		state.setGs(gs);
		String[] arr = string.split(" ");
		Variable lhs = new Variable();
		lhs.setcState(state);
		lhs.setName(arr[4]);
		lhs.setMember(arr[10].split(",")[0]);
		state.setLhs(lhs);
		Variable rhs1 = new Variable();
		rhs1.setcState(state);
		rhs1.setName(arr[6]);
		state.setRhs(rhs1);
		return state;
	}

	public static State extractNew(String string, GlobalState gs) {
		State state = new State();
		state.setGs(gs);
		String[] arr = string.split(" ");
		Variable lhs = new Variable();
		lhs.setcState(state);
		lhs.setName(arr[3]);
		state.setLhs(lhs);
		state.setValue("new" + arr[0] + "_" + gs.getCallString());
		return state;
	}
}