/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.core.tests.model;

import org.eclipse.wst.jsdt.core.*;
import org.eclipse.wst.jsdt.internal.codeassist.RelevanceConstants;

import junit.framework.*;

public class CompletionWithMissingTypesTests_1_5 extends AbstractJavaModelCompletionTests implements RelevanceConstants {
	static {
//		TESTS_NAMES = new String[]{"test0040"};
	}
public CompletionWithMissingTypesTests_1_5(String name) {
	super(name);
}
public void setUpSuite() throws Exception {
	if (COMPLETION_PROJECT == null)  {
		COMPLETION_PROJECT = setUpJavaProject("Completion", "1.5");
	} else {
		setUpProjectCompliance(COMPLETION_PROJECT, "1.5");
	}
	super.setUpSuite();
}

public void tearDownSuite() throws Exception {
	super.tearDownSuite();
}
public static Test suite() {
	return buildModelTestSuite(CompletionWithMissingTypesTests_1_5.class);
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=44984
public void test0001() throws JavaScriptModelException {
	this.workingCopies = new IJavaScriptUnit[2];
	this.workingCopies[0] = getWorkingCopy(
		"/Completion/src/test/Test.js",
		"package test;"+
		"public class Test {\n" + 
		"  void foo() {\n" + 
 		"    MissingType<Object> m = null;\n" + 
 		"    m.b\n" + 
		"  }\n" + 
		"}\n");
	
	this.workingCopies[1] = getWorkingCopy(
		"/Completion/src/missing/MissingType.js",
		"package missing;"+
		"public class MissingType<T> {\n" + 
		"  public void bar() {};\n" + 
		"}\n");

	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true, false, true, false, true);
	requestor.allowAllRequiredProposals();
	String str = this.workingCopies[0].getSource();
	String completeBehind = "m.b";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.workingCopies[0].codeComplete(cursorLocation, requestor, this.wcOwner);

	int relevance1 = R_DEFAULT + R_INTERESTING + R_CASE + R_NON_STATIC + R_NON_RESTRICTED + R_NO_PROBLEMS;
	int start1 = str.lastIndexOf("m.b") + "m.".length();
	int end1 = start1 + "b".length();
	int start2 = str.lastIndexOf("MissingType");
	int end2 = start2 + "MissingType".length();
	assertResults(
			"bar[FUNCTION_REF]{bar(), Lmissing.MissingType<Ljava.lang.Object;>;, ()V, bar, null, ["+start1+", "+end1+"], " + (relevance1) + "}\n" +
			"   MissingType<T>[TYPE_REF]{missing.MissingType, missing, Lmissing.MissingType<TT;>;, null, null, ["+start2+", "+end2+"], " + (relevance1) + "}",
			requestor.getResults());
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=44984
public void test0002() throws JavaScriptModelException {
	this.workingCopies = new IJavaScriptUnit[3];
	this.workingCopies[0] = getWorkingCopy(
		"/Completion/src/test/Test.js",
		"package test;"+
		"public class Test {\n" + 
		"  void foo() {\n" + 
 		"    MissingType<MissingType2> m = null;\n" + 
 		"    m.b\n" + 
		"  }\n" + 
		"}\n");
	
	this.workingCopies[1] = getWorkingCopy(
		"/Completion/src/missing/MissingType.js",
		"package missing;"+
		"public class MissingType<T> {\n" + 
		"  public void bar() {};\n" + 
		"  public void bar(T t) {};\n" + 
		"}\n");
	
	this.workingCopies[2] = getWorkingCopy(
		"/Completion/src/missing/MissingType2.js",
		"package missing;"+
		"public class MissingType2 {\n" + 
		"}\n");

	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true, false, true, false, true);
	requestor.allowAllRequiredProposals();
	String str = this.workingCopies[0].getSource();
	String completeBehind = "m.b";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.workingCopies[0].codeComplete(cursorLocation, requestor, this.wcOwner);

	int relevance1 = R_DEFAULT + R_INTERESTING + R_CASE + R_NON_STATIC + R_NON_RESTRICTED + R_NO_PROBLEMS;
	int start1 = str.lastIndexOf("m.b") + "m.".length();
	int end1 = start1 + "b".length();
	int start2 = str.lastIndexOf("MissingType2");
	int end2 = start2 + "MissingType2".length();
	int start3 = str.lastIndexOf("MissingType<");
	int end3 = start3 + "MissingType".length();
	assertResults(
			"bar[FUNCTION_REF]{bar(), Lmissing.MissingType<Lmissing.MissingType2;>;, ()V, bar, null, ["+start1+", "+end1+"], " + (relevance1) + "}\n" +
			"   MissingType2[TYPE_REF]{missing.MissingType2, missing, Lmissing.MissingType2;, null, null, ["+start2+", "+end2+"], " + (relevance1) + "}\n" +
			"   MissingType<T>[TYPE_REF]{missing.MissingType, missing, Lmissing.MissingType<TT;>;, null, null, ["+start3+", "+end3+"], " + (relevance1) + "}\n" +
			"bar[FUNCTION_REF]{bar(), Lmissing.MissingType<Lmissing.MissingType2;>;, (Lmissing.MissingType2;)V, bar, (t), ["+start1+", "+end1+"], " + (relevance1) + "}\n" +
			"   MissingType2[TYPE_REF]{missing.MissingType2, missing, Lmissing.MissingType2;, null, null, ["+start2+", "+end2+"], " + (relevance1) + "}\n" +
			"   MissingType<T>[TYPE_REF]{missing.MissingType, missing, Lmissing.MissingType<TT;>;, null, null, ["+start3+", "+end3+"], " + (relevance1) + "}",
			requestor.getResults());
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=44984
public void test0003() throws JavaScriptModelException {
	this.workingCopies = new IJavaScriptUnit[3];
	this.workingCopies[0] = getWorkingCopy(
		"/Completion/src/test/Test.js",
		"package test;"+
		"import missing.MissingType;"+
		"public class Test {\n" + 
		"  void foo() {\n" + 
 		"    MissingType<MissingType2> m = null;\n" + 
 		"    m.b\n" + 
		"  }\n" + 
		"}\n");
	
	this.workingCopies[1] = getWorkingCopy(
		"/Completion/src/missing/MissingType.js",
		"package missing;"+
		"public class MissingType<T> {\n" + 
		"  public void bar() {};\n" + 
		"  public void bar(T t) {};\n" + 
		"}\n");
	
	this.workingCopies[2] = getWorkingCopy(
		"/Completion/src/missing/MissingType2.js",
		"package missing;"+
		"public class MissingType2 {\n" + 
		"}\n");

	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true, false, true, false, true);
	requestor.allowAllRequiredProposals();
	String str = this.workingCopies[0].getSource();
	String completeBehind = "m.b";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.workingCopies[0].codeComplete(cursorLocation, requestor, this.wcOwner);

	int relevance1 = R_DEFAULT + R_INTERESTING + R_CASE + R_NON_STATIC + R_NON_RESTRICTED + R_NO_PROBLEMS;
	int start1 = str.lastIndexOf("m.b") + "m.".length();
	int end1 = start1 + "b".length();
	int start2 = str.lastIndexOf("MissingType2");
	int end2 = start2 + "MissingType2".length();
	assertResults(
			"bar[FUNCTION_REF]{bar(), Lmissing.MissingType<Lmissing.MissingType2;>;, ()V, bar, null, ["+start1+", "+end1+"], " + (relevance1) + "}\n" +
			"   MissingType2[TYPE_REF]{missing.MissingType2, missing, Lmissing.MissingType2;, null, null, ["+start2+", "+end2+"], " + (relevance1) + "}\n" +
			"bar[FUNCTION_REF]{bar(), Lmissing.MissingType<Lmissing.MissingType2;>;, (Lmissing.MissingType2;)V, bar, (t), ["+start1+", "+end1+"], " + (relevance1) + "}\n" +
			"   MissingType2[TYPE_REF]{missing.MissingType2, missing, Lmissing.MissingType2;, null, null, ["+start2+", "+end2+"], " + (relevance1) + "}",
			requestor.getResults());
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=44984
public void test0004() throws JavaScriptModelException {
	this.workingCopies = new IJavaScriptUnit[3];
	this.workingCopies[0] = getWorkingCopy(
		"/Completion/src/test/Test.js",
		"package test;"+
		"import missing.MissingType2;"+
		"public class Test {\n" + 
		"  void foo() {\n" + 
 		"    MissingType<MissingType2> m = null;\n" + 
 		"    m.b\n" + 
		"  }\n" + 
		"}\n");
	
	this.workingCopies[1] = getWorkingCopy(
		"/Completion/src/missing/MissingType.js",
		"package missing;"+
		"public class MissingType<T> {\n" + 
		"  public void bar() {};\n" + 
		"  public void bar(T t) {};\n" + 
		"}\n");
	
	this.workingCopies[2] = getWorkingCopy(
		"/Completion/src/missing/MissingType2.js",
		"package missing;"+
		"public class MissingType2 {\n" + 
		"}\n");

	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true, false, true, false, true);
	requestor.allowAllRequiredProposals();
	String str = this.workingCopies[0].getSource();
	String completeBehind = "m.b";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.workingCopies[0].codeComplete(cursorLocation, requestor, this.wcOwner);

	int relevance1 = R_DEFAULT + R_INTERESTING + R_CASE + R_NON_STATIC + R_NON_RESTRICTED + R_NO_PROBLEMS;
	int start1 = str.lastIndexOf("m.b") + "m.".length();
	int end1 = start1 + "b".length();
	int start2 = str.lastIndexOf("MissingType<");
	int end2 = start2 + "MissingType".length();
	assertResults(
			"bar[FUNCTION_REF]{bar(), Lmissing.MissingType<Lmissing.MissingType2;>;, ()V, bar, null, ["+start1+", "+end1+"], " + (relevance1) + "}\n" +
			"   MissingType<T>[TYPE_REF]{missing.MissingType, missing, Lmissing.MissingType<TT;>;, null, null, ["+start2+", "+end2+"], " + (relevance1) + "}\n" +
			"bar[FUNCTION_REF]{bar(), Lmissing.MissingType<Lmissing.MissingType2;>;, (Lmissing.MissingType2;)V, bar, (t), ["+start1+", "+end1+"], " + (relevance1) + "}\n" +
			"   MissingType<T>[TYPE_REF]{missing.MissingType, missing, Lmissing.MissingType<TT;>;, null, null, ["+start2+", "+end2+"], " + (relevance1) + "}",
			requestor.getResults());
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=44984
public void test0005() throws JavaScriptModelException {
	this.workingCopies = new IJavaScriptUnit[6];
	this.workingCopies[0] = getWorkingCopy(
		"/Completion/src/test/Test.js",
		"package test;"+
		"public class Test {\n" + 
		"  void foo() {\n" + 
 		"    MissingType<MissingType1, MissingType2> m = null;\n" + 
 		"    m.b\n" + 
		"  }\n" + 
		"}\n");
	
	this.workingCopies[1] = getWorkingCopy(
		"/Completion/src/missing/MissingType.js",
		"package missing;"+
		"public class MissingType<T, U> {\n" + 
		"  public void bar(T t, U u) {};\n" + 
		"}\n");
	
	this.workingCopies[2] = getWorkingCopy(
		"/Completion/src/missing/MissingType1.js",
		"package missing;"+
		"public class MissingType1 {\n" + 
		"}\n");
	
	this.workingCopies[3] = getWorkingCopy(
		"/Completion/src/missing/MissingType2.js",
		"package missing;"+
		"public class MissingType2 {\n" + 
		"}\n");
	
	this.workingCopies[4] = getWorkingCopy(
		"/Completion/src/missing2/MissingType1.js",
		"package missing2;"+
		"public class MissingType1 {\n" + 
		"}\n");
	
	this.workingCopies[5] = getWorkingCopy(
		"/Completion/src/missing2/MissingType2.js",
		"package missing2;"+
		"public class MissingType2 {\n" + 
		"}\n");

	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true, false, true, false, true);
	requestor.allowAllRequiredProposals();
	String str = this.workingCopies[0].getSource();
	String completeBehind = "m.b";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.workingCopies[0].codeComplete(cursorLocation, requestor, this.wcOwner);

	int relevance1 = R_DEFAULT + R_INTERESTING + R_CASE + R_NON_STATIC + R_NON_RESTRICTED + R_NO_PROBLEMS;
	int start1 = str.lastIndexOf("m.b") + "m.".length();
	int end1 = start1 + "b".length();
	int start2 = str.lastIndexOf("MissingType1");
	int end2 = start2 + "MissingType1".length();
	int start3 = str.lastIndexOf("MissingType2");
	int end3 = start3 + "MissingType2".length();
	int start4 = str.lastIndexOf("MissingType<");
	int end4 = start4 + "MissingType".length();
	assertResults(
			"bar[FUNCTION_REF]{bar(), Lmissing.MissingType<Lmissing.MissingType1;Lmissing.MissingType2;>;, (Lmissing.MissingType1;Lmissing.MissingType2;)V, bar, (t, u), ["+start1+", "+end1+"], " + (relevance1) + "}\n" +
			"   MissingType1[TYPE_REF]{missing.MissingType1, missing, Lmissing.MissingType1;, null, null, ["+start2+", "+end2+"], " + (relevance1) + "}\n" +
			"   MissingType2[TYPE_REF]{missing.MissingType2, missing, Lmissing.MissingType2;, null, null, ["+start3+", "+end3+"], " + (relevance1) + "}\n" +
			"   MissingType<T,U>[TYPE_REF]{missing.MissingType, missing, Lmissing.MissingType<TT;TU;>;, null, null, ["+start4+", "+end4+"], " + (relevance1) + "}\n" +
			"bar[FUNCTION_REF]{bar(), Lmissing.MissingType<Lmissing.MissingType1;Lmissing2.MissingType2;>;, (Lmissing.MissingType1;Lmissing2.MissingType2;)V, bar, (t, u), ["+start1+", "+end1+"], " + (relevance1) + "}\n" +
			"   MissingType1[TYPE_REF]{missing.MissingType1, missing, Lmissing.MissingType1;, null, null, ["+start2+", "+end2+"], " + (relevance1) + "}\n" +
			"   MissingType2[TYPE_REF]{missing2.MissingType2, missing2, Lmissing2.MissingType2;, null, null, ["+start3+", "+end3+"], " + (relevance1) + "}\n" +
			"   MissingType<T,U>[TYPE_REF]{missing.MissingType, missing, Lmissing.MissingType<TT;TU;>;, null, null, ["+start4+", "+end4+"], " + (relevance1) + "}\n" +
			"bar[FUNCTION_REF]{bar(), Lmissing.MissingType<Lmissing2.MissingType1;Lmissing.MissingType2;>;, (Lmissing2.MissingType1;Lmissing.MissingType2;)V, bar, (t, u), ["+start1+", "+end1+"], " + (relevance1) + "}\n" +
			"   MissingType1[TYPE_REF]{missing2.MissingType1, missing2, Lmissing2.MissingType1;, null, null, ["+start2+", "+end2+"], " + (relevance1) + "}\n" +
			"   MissingType2[TYPE_REF]{missing.MissingType2, missing, Lmissing.MissingType2;, null, null, ["+start3+", "+end3+"], " + (relevance1) + "}\n" +
			"   MissingType<T,U>[TYPE_REF]{missing.MissingType, missing, Lmissing.MissingType<TT;TU;>;, null, null, ["+start4+", "+end4+"], " + (relevance1) + "}\n" +
			"bar[FUNCTION_REF]{bar(), Lmissing.MissingType<Lmissing2.MissingType1;Lmissing2.MissingType2;>;, (Lmissing2.MissingType1;Lmissing2.MissingType2;)V, bar, (t, u), ["+start1+", "+end1+"], " + (relevance1) + "}\n" +
			"   MissingType1[TYPE_REF]{missing2.MissingType1, missing2, Lmissing2.MissingType1;, null, null, ["+start2+", "+end2+"], " + (relevance1) + "}\n" +
			"   MissingType2[TYPE_REF]{missing2.MissingType2, missing2, Lmissing2.MissingType2;, null, null, ["+start3+", "+end3+"], " + (relevance1) + "}\n" +
			"   MissingType<T,U>[TYPE_REF]{missing.MissingType, missing, Lmissing.MissingType<TT;TU;>;, null, null, ["+start4+", "+end4+"], " + (relevance1) + "}",
			requestor.getResults());
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=44984
public void test0006() throws JavaScriptModelException {
	this.workingCopies = new IJavaScriptUnit[2];
	this.workingCopies[0] = getWorkingCopy(
		"/Completion/src/test/Test.js",
		"package test;"+
		"public class Test {\n" + 
		"  MissingType m(int ... i) {return null;}\n" + 
		"  void foo() {\n" + 
 		"    m(0, 0).b\n" + 
		"  }\n" + 
		"}\n");
	
	this.workingCopies[1] = getWorkingCopy(
		"/Completion/src/missing/MissingType.js",
		"package missing;"+
		"public class MissingType {\n" + 
		"  public void bar() {}\n" + 
		"}\n");

	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true, false, true, false, true);
	requestor.allowAllRequiredProposals();
	String str = this.workingCopies[0].getSource();
	String completeBehind = ".b";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.workingCopies[0].codeComplete(cursorLocation, requestor, this.wcOwner);

	int relevance1 = R_DEFAULT + R_INTERESTING + R_CASE + R_NON_STATIC + R_NON_RESTRICTED + R_NO_PROBLEMS;
	int start1 = str.lastIndexOf("m(0, 0).b") + "m(0, 0).".length();
	int end1 = start1 + "b".length();
	int start2 = str.lastIndexOf("MissingType");
	int end2 = start2 + "MissingType".length();
	assertResults(
			"bar[FUNCTION_REF]{bar(), Lmissing.MissingType;, ()V, bar, null, ["+start1+", "+end1+"], " + (relevance1) + "}\n" +
			"   MissingType[TYPE_REF]{missing.MissingType, missing, Lmissing.MissingType;, null, null, ["+start2+", "+end2+"], " + (relevance1) + "}",
			requestor.getResults());
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=44984
public void test0007() throws JavaScriptModelException {
	this.workingCopies = new IJavaScriptUnit[3];
	this.workingCopies[0] = getWorkingCopy(
		"/Completion/src/test/Test.js",
		"package test;"+
		"public class Test {\n" + 
		"  void foo() {\n" + 
 		"    AType<? extends MissingType> m = null;\n" + 
 		"    m.b\n" + 
		"  }\n" + 
		"}\n");
	
	this.workingCopies[1] = getWorkingCopy(
		"/Completion/src/test/AType.js",
		"package tezt;"+
		"public class AType<T> {\n" + 
		"  public void bar(T t) {};\n" + 
		"}\n");
	
	this.workingCopies[2] = getWorkingCopy(
		"/Completion/src/missing/MissingType.js",
		"package missing;"+
		"public class MissingType {\n" + 
		"}\n");
	
	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true, false, true, false, true);
	requestor.allowAllRequiredProposals();
	String str = this.workingCopies[0].getSource();
	String completeBehind = "m.b";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.workingCopies[0].codeComplete(cursorLocation, requestor, this.wcOwner);

	int relevance1 = R_DEFAULT + R_INTERESTING + R_CASE + R_NON_STATIC + R_NON_RESTRICTED + R_NO_PROBLEMS;
	int start1 = str.lastIndexOf("m.b") + "m.".length();
	int end1 = start1 + "b".length();
	int start2 = str.lastIndexOf("MissingType");
	int end2 = start2 + "MissingType".length();
	assertResults(
			"bar[FUNCTION_REF]{bar(), Ltest.AType<!+Lmissing.MissingType;>;, (!+Lmissing.MissingType;)V, bar, (t), ["+start1+", "+end1+"], " + (relevance1) + "}\n" +
			"   MissingType[TYPE_REF]{missing.MissingType, missing, Lmissing.MissingType;, null, null, ["+start2+", "+end2+"], " + (relevance1) + "}",
			requestor.getResults());
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=44984
public void test0008() throws JavaScriptModelException {
	this.workingCopies = new IJavaScriptUnit[2];
	this.workingCopies[0] = getWorkingCopy(
		"/Completion/src/test/Test.js",
		"package test;"+
		"public class Test {\n" + 
		"  void foo() {\n" + 
 		"    MissingType.MissingMemberType<Object> m = null;\n" + 
 		"    m.b\n" + 
		"  }\n" + 
		"}\n");
	
	this.workingCopies[1] = getWorkingCopy(
		"/Completion/src/missing/MissingType.js",
		"package missing;"+
		"public class MissingType {\n" + 
		"  public class MissingMemberType<T> {\n" + 
		"    public void bar() {};\n" + 
		"  }\n" + 
		"}\n");

	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true, false, true, false, true);
	requestor.allowAllRequiredProposals();
	String str = this.workingCopies[0].getSource();
	String completeBehind = "m.b";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.workingCopies[0].codeComplete(cursorLocation, requestor, this.wcOwner);

	int relevance1 = R_DEFAULT + R_INTERESTING + R_CASE + R_NON_STATIC + R_NON_RESTRICTED + R_NO_PROBLEMS;
	int start1 = str.lastIndexOf("m.b") + "m.".length();
	int end1 = start1 + "b".length();
	int start2 = str.lastIndexOf("MissingType");
	int end2 = start2 + "MissingType".length();
	assertResults(
			"bar[FUNCTION_REF]{bar(), Lmissing.MissingType$MissingMemberType<Ljava.lang.Object;>;, ()V, bar, null, ["+start1+", "+end1+"], " + (relevance1) + "}\n" +
			"   MissingType[TYPE_REF]{missing.MissingType, missing, Lmissing.MissingType;, null, null, ["+start2+", "+end2+"], " + (relevance1) + "}",
			requestor.getResults());
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=44984
public void test0009() throws JavaScriptModelException {
	this.workingCopies = new IJavaScriptUnit[2];
	this.workingCopies[0] = getWorkingCopy(
		"/Completion/src/test/Test.js",
		"package test;"+
		"public class Test {\n" + 
		"  void foo() {\n" + 
 		"    missing2.MissingType<Object> m = null;\n" + 
 		"    m.b\n" + 
		"  }\n" + 
		"}\n");
	
	this.workingCopies[1] = getWorkingCopy(
		"/Completion/src/missing1/missing2/MissingType.js",
		"package missing1.missing2;"+
		"public class MissingType<T> {\n" + 
		"  public void bar() {};\n" + 
		"}\n");

	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true, false, true, false, true);
	requestor.allowAllRequiredProposals();
	String str = this.workingCopies[0].getSource();
	String completeBehind = "m.b";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.workingCopies[0].codeComplete(cursorLocation, requestor, this.wcOwner);

	assertResults(
			"",
			requestor.getResults());
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=44984
public void test0010() throws JavaScriptModelException {
	this.workingCopies = new IJavaScriptUnit[2];
	this.workingCopies[0] = getWorkingCopy(
		"/Completion/src/test/Test.js",
		"package test;"+
		"public class Test {\n" + 
		"  void foo() {\n" + 
 		"    MissingType<Object>.MissingMemberType<Object> m = null;\n" + 
 		"    m.b\n" + 
		"  }\n" + 
		"}\n");
	
	this.workingCopies[1] = getWorkingCopy(
		"/Completion/src/missing/MissingType.js",
		"package missing;"+
		"public class MissingType<T> {\n" + 
		"  public class MissingMemberType<T> {\n" + 
		"    public void bar() {};\n" + 
		"  }\n" + 
		"}\n");

	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true, false, true, false, true);
	requestor.allowAllRequiredProposals();
	String str = this.workingCopies[0].getSource();
	String completeBehind = "m.b";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.workingCopies[0].codeComplete(cursorLocation, requestor, this.wcOwner);

	int relevance1 = R_DEFAULT + R_INTERESTING + R_CASE + R_NON_STATIC + R_NON_RESTRICTED + R_NO_PROBLEMS;
	int start1 = str.lastIndexOf("m.b") + "m.".length();
	int end1 = start1 + "b".length();
	int start2 = str.lastIndexOf("MissingType");
	int end2 = start2 + "MissingType".length();
	assertResults(
			"bar[FUNCTION_REF]{bar(), Lmissing.MissingType<Ljava.lang.Object;>.MissingMemberType<Ljava.lang.Object;>;, ()V, bar, null, ["+start1+", "+end1+"], " + (relevance1) + "}\n" +
			"   MissingType<T>[TYPE_REF]{missing.MissingType, missing, Lmissing.MissingType<TT;>;, null, null, ["+start2+", "+end2+"], " + (relevance1) + "}",
			requestor.getResults());
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=44984
public void test0011() throws JavaScriptModelException {
	this.workingCopies = new IJavaScriptUnit[2];
	this.workingCopies[0] = getWorkingCopy(
		"/Completion/src/test/Test.js",
		"package test;"+
		"public class Test {\n" + 
		"  void foo() {\n" + 
 		"    MissingMemberType<Object> m = null;\n" + 
 		"    m.b\n" + 
		"  }\n" + 
		"}\n");
	
	this.workingCopies[1] = getWorkingCopy(
		"/Completion/src/missing/MissingType.js",
		"package missing;"+
		"public class MissingType<T> {\n" + 
		"  public class MissingMemberType<T> {\n" + 
		"    public void bar() {};\n" + 
		"  }\n" + 
		"}\n");

	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true, false, true, false, true);
	requestor.allowAllRequiredProposals();
	String str = this.workingCopies[0].getSource();
	String completeBehind = "m.b";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.workingCopies[0].codeComplete(cursorLocation, requestor, this.wcOwner);

	assertResults(
			"",
			requestor.getResults());
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=161204
public void test0012() throws JavaScriptModelException {
	this.workingCopies = new IJavaScriptUnit[3];
	this.workingCopies[0] = getWorkingCopy(
		"/Completion/src/test/Test.js",
		"package test;"+
		"public class Test {\n" + 
		"  void foo() {\n" + 
 		"    MissingType m = null;\n" + 
 		"    m.b\n" + 
		"  }\n" + 
		"}\n");
	
	this.workingCopies[1] = getWorkingCopy(
		"/Completion/src/missing1/MissingType.js",
		"package missing1;"+
		"public class MissingType<T> {\n" + 
		"  public void bar() {};\n" + 
		"}\n");
	
	this.workingCopies[2] = getWorkingCopy(
		"/Completion/src/missing2/MissingType.js",
		"package missing2;"+
		"public class MissingType {\n" + 
		"  public void bar() {};\n" + 
		"}\n");

	CompletionTestsRequestor2 requestor = new CompletionTestsRequestor2(true, false, true, false, true);
	requestor.allowAllRequiredProposals();
	String str = this.workingCopies[0].getSource();
	String completeBehind = "m.b";
	int cursorLocation = str.lastIndexOf(completeBehind) + completeBehind.length();
	this.workingCopies[0].codeComplete(cursorLocation, requestor, this.wcOwner);

	int relevance1 = R_DEFAULT + R_INTERESTING + R_CASE + R_NON_STATIC + R_NON_RESTRICTED;
	int relevance2 = R_DEFAULT + R_INTERESTING + R_CASE + R_NON_STATIC + R_NON_RESTRICTED + R_NO_PROBLEMS;
	int start1 = str.lastIndexOf("m.b") + "m.".length();
	int end1 = start1 + "b".length();
	int start2 = str.lastIndexOf("MissingType");
	int end2 = start2 + "MissingType".length();
	assertResults(
			"bar[FUNCTION_REF]{bar(), Lmissing1.MissingType;, ()V, bar, null, ["+start1+", "+end1+"], " + (relevance1) + "}\n" +
			"   MissingType<T>[TYPE_REF]{missing1.MissingType, missing1, Lmissing1.MissingType<TT;>;, null, null, ["+start2+", "+end2+"], " + (relevance1) + "}\n" +
			"bar[FUNCTION_REF]{bar(), Lmissing2.MissingType;, ()V, bar, null, ["+start1+", "+end1+"], " + (relevance2) + "}\n" +
			"   MissingType[TYPE_REF]{missing2.MissingType, missing2, Lmissing2.MissingType;, null, null, ["+start2+", "+end2+"], " + (relevance2) + "}",
			requestor.getResults());
}
}
