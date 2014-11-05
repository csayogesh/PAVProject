package PAVpointerAnalysisPackage.anderson;

import java.util.Iterator;

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
		String str = ir.toString();
		String[] lines = str.split("\n");
		boolean istart = false;
		for (int i = 0; i < lines.length; i++) {
			if (istart)
				GlobleState.setState(fetchState(lines[i]));
			if (lines[i].equals("Instructions:"))
				istart = true;
		}
		GlobleState.printGlobleStates();
	}

	private static State fetchState(String string) {
		State state = null;
		if (string.contains(" = new <"))
			state = extractNew(string);
		else if (string.contains(" putfield v"))
			state = extractPutfield(string);
		else if (string.contains(" getfield <"))
			state = extractGetfield(string);
		else if (string.contains(" = phi ") && string.contains(":#null,"))
			state = extractPhiNull(string);
		else if (string.contains(" = phi ") && !string.contains(":#"))
			state = extractPhi(string);
		return state;
	}

	private static State extractPhi(String string) {
		State state = new State();
		String[] arr = string.split(" ");
		Variable lhs = new Variable();
		lhs.setName(arr[11]);
		state.setLhs(lhs );
		Variable rhs1 = new Variable();
		rhs1.setName(arr[15].split(",")[1]);
		state.setRhs(rhs1 );
		rhs1 = new Variable();
		rhs1.setName(arr[15].split(",")[0]);
		state.setRhs(rhs1 );
		return state;
	}

	private static State extractPhiNull(String string) {
		State state = new State();
		String[] arr = string.split(" ");
		Variable lhs = new Variable();
		lhs.setName(arr[11]);
		state.setLhs(lhs );
		Variable rhs1 = new Variable();
		rhs1.setName(arr[15].split(":#null,")[1]);
		state.setRhs(rhs1 );
		return state;
	}

	private static State extractGetfield(String string) {
		State state = new State();
		String[] arr = string.split(" ");
		Variable lhs = new Variable();
		lhs.setName(arr[3]);
		state.setLhs(lhs );
		Variable rhs1 = new Variable();
		rhs1.setName(arr[12].split("\\(")[0]);
		rhs1.setMember(arr[9].split(",")[0]);
		state.setRhs(rhs1);
		return state;
	}

	private static State extractPutfield(String string) {
		State state = new State();
		String[] arr = string.split(" ");
		Variable lhs = new Variable();
		lhs.setName(arr[4]);
		lhs.setMember(arr[10].split(",")[0]);
		state.setLhs(lhs);
		Variable rhs1 = new Variable();
		rhs1.setName(arr[6]);
		state.setRhs(rhs1);
		return state;
	}

	private static State extractNew(String string) {
		State state = new State();
		String[] arr = string.split(" ");
		Variable lhs = new Variable();
		lhs.setName(arr[3]);
		state.setLhs(lhs);
		state.setValue("new " + arr[0]);
		return state;
	}
}