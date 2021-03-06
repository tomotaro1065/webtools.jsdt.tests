/*******************************************************************************
 * Copyright (c) 2000, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.core.tests.model;

import java.io.IOException;

import junit.framework.Test;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.wst.jsdt.core.Flags;
import org.eclipse.wst.jsdt.core.IClassFile;
import org.eclipse.wst.jsdt.core.IFunction;
import org.eclipse.wst.jsdt.core.IJavaScriptElement;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.IJavaScriptUnit;
import org.eclipse.wst.jsdt.core.IPackageFragment;
import org.eclipse.wst.jsdt.core.IPackageFragmentRoot;
import org.eclipse.wst.jsdt.core.IType;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.core.JavaScriptModelException;
import org.eclipse.wst.jsdt.core.WorkingCopyOwner;
import org.eclipse.wst.jsdt.core.search.IJavaScriptSearchConstants;
import org.eclipse.wst.jsdt.core.search.IJavaScriptSearchScope;
import org.eclipse.wst.jsdt.core.search.SearchEngine;

public class ClassFileTests extends ModifyingResourceTests {
	
	IPackageFragmentRoot jarRoot;
	IJavaScriptUnit workingCopy;
	IClassFile classFile;
	
public ClassFileTests(String name) {
	super(name);
}

// Use this static initializer to specify subset for tests
// All specified tests which do not belong to the class are skipped...
static {
//	TESTS_PREFIX = "testGetCategories";
//	TESTS_NAMES = new String[] { "testWorkingCopy11"};
//	TESTS_NUMBERS = new int[] { 13 };
//	TESTS_RANGE = new int[] { 16, -1 };
}
public static Test suite() {
	return buildModelTestSuite(ClassFileTests.class);
}

public void setUpSuite() throws Exception {
	super.setUpSuite();
	IJavaScriptProject javaProject = createJavaProject("P");
	String[] pathAndContents = new String[] {
		"nongeneric/A.js", 
		"package nongeneric;\n" +
		"public class A {\n" + 
		"}",			
		"generic/X.js", 
		"package generic;\n" +
		"public class X<T> {\n" + 
		"  <U extends Exception> X<T> foo(X<T> x) throws RuntimeException, U {\n" +
		"    return null;\n" +
		"  }\n" +
		"  <K, V> V foo(K key, V value) throws Exception {\n" +
		"    return value;\n" +
		"  }\n" +
		"}",
		"generic/Y.js", 
		"package generic;\n" +
		"public class Y<K, L> {\n" + 
		"}",
		"generic/Z.js", 
		"package generic;\n" +
		"public class Z<T extends Object & I<? super T>> {\n" + 
		"}",
		"generic/I.js", 
		"package generic;\n" +
		"public interface I<T> {\n" + 
		"}",
		"generic/W.js", 
		"package generic;\n" +
		"public class W<T extends X<T> , U extends T> {\n" + 
		"}",
		"generic/V.js", 
		"package generic;\n" +
		"public class V extends X<Thread> implements I<String> {\n" + 
		"}",
		"varargs/X.js", 
		"package varargs;\n" +
		"public class X {\n" + 
		"  void foo(String s, Object ... others) {\n" +
		"  }\n" +
		"}",
		"workingcopy/X.js", 
		"package workingcopy;\n" +
		"public class X {\n" + 
		"  void foo() {\n" +
		"    System.out.println();\n" +
		"  }\n" +
		"}",
		"workingcopy/Y.js", 
		"package workingcopy;\n" +
		"public class Y<W> {\n" + 
		"  <T> T foo(T t, String... args) {\n" +
		"    return t;\n" +
		"  }\n" +
		"}",
	};
	addLibrary(javaProject, "lib.jar", "libsrc.zip", pathAndContents, JavaScriptCore.VERSION_1_5);
	this.jarRoot = javaProject.getPackageFragmentRoot(getFile("/P/lib.jar"));
}

public void tearDownSuite() throws Exception {
	super.tearDownSuite();
	deleteProject("P");
}

protected void tearDown() throws Exception {
	if (this.workingCopy != null)
		this.workingCopy.discardWorkingCopy();
	if (this.classFile != null) {
		removeLibrary(getJavaProject("P"), "lib2.jar", "src2.zip");
		this.classFile = null;
	}
	super.tearDown();
}

private IClassFile createClassFile(String contents) throws CoreException, IOException {
	IJavaScriptProject project = getJavaProject("P");
	addLibrary(project, "lib2.jar", "src2.zip", new String[] {"p/X.js", contents}, "1.5");
	this.classFile =  project.getPackageFragmentRoot(getFile("/P/lib2.jar")).getPackageFragment("p").getClassFile("X.class");
	return this.classFile;
}

/*
 * Ensures that no exception is thrown for a .class file name with a dot
 * (regression test for bug 114140 assertion failed when opening a class file not not the classpath)
 */
public void testDotName() throws JavaScriptModelException {
	IType type = getClassFile("/P/X.Y.class").getType();
	assertEquals("X.Y", type.getElementName());
}

/*
 * Ensure that the categories for a class are correct.
 */
public void testGetCategories01() throws CoreException, IOException {
	createClassFile(
		"package p;\n" +
		"/**\n" +
		" * @category test\n" +
		" */\n" +
		"public class X {\n" +
		"}"
	);
	String[] categories = this.classFile.getType().getCategories();
	assertStringsEqual(
		"Unexpected categories",
		"test\n",
		categories);
}
public void testGetCategories02() throws CoreException, IOException {
	createClassFile(
		"package p;\n" +
		"/**\n" +
		" * @category test1 test2 test3 test4 test5 test6 test7 test8 test9 test10\n" +
		" */\n" +
		"public class X {\n" +
		"}"
	);
	String[] categories = this.classFile.getType().getCategories();
	assertStringsEqual(
		"Unexpected categories",
		"test1\ntest2\ntest3\ntest4\ntest5\ntest6\ntest7\ntest8\ntest9\ntest10\n",
		categories);
}

/*
 * Ensure that the categories for a field are correct.
 */
public void testGetCategories03() throws CoreException, IOException {
	createClassFile(
		"package p;\n" +
		"public class X {\n" +
		"  /**\n" +
		"   * @category test\n" +
		"   */\n" +
		"  int field;\n" +
		"}"
	);
	String[] categories = this.classFile.getType().getField("field").getCategories();
	assertStringsEqual(
		"Unexpected categories",
		"test\n",
		categories);
}
public void testGetCategories04() throws CoreException, IOException {
	createClassFile(
		"package p;\n" +
		"public class X {\n" +
		"  /**\n" +
		"   * @category test1 test2\n" +
		"   */\n" +
		"  int field;\n" +
		"}"
	);
	String[] categories = this.classFile.getType().getField("field").getCategories();
	assertStringsEqual(
		"Unexpected categories",
		"test1\ntest2\n",
		categories);
}

/*
 * Ensure that the categories for a method are correct.
 */
public void testGetCategories05() throws CoreException, IOException {
	createClassFile(
		"package p;\n" +
		"public class X {\n" +
		"  /**\n" +
		" * @category test\n" +
		"   */\n" +
		"  void foo() {}\n" +
		"}"
	);
	String[] categories = this.classFile.getType().getFunction("foo", new String[0]).getCategories();
	assertStringsEqual(
		"Unexpected categories",
		"test\n",
		categories);
}
public void testGetCategories06() throws CoreException, IOException {
	createClassFile(
		"package p;\n" +
		"public class X {\n" +
		"  /**\n" +
		" * @category test1 test2 test3 test4 test5\n" +
		"   */\n" +
		"  void foo() {}\n" +
		"}"
	);
	String[] categories = this.classFile.getType().getFunction("foo", new String[0]).getCategories();
	assertStringsEqual(
		"Unexpected categories",
		"test1\ntest2\ntest3\ntest4\ntest5\n",
		categories);
}
// https://bugs.eclipse.org/bugs/show_bug.cgi?id=125676
public void testGetCategories07() throws CoreException, IOException {
	createClassFile(
		"package p;\n" +
		"public class X {\n" +
		"  /**\n" +
		"   * @category " +
		"	 *		test\n" +
		"   */\n" +
		"  void foo() {}\n" +
		"}"
	);
	String[] categories = this.classFile.getType().getFunction("foo", new String[0]).getCategories();
	assertStringsEqual(
		"Unexpected categories",
		"",
		categories);
}
public void testGetCategories08() throws CoreException, IOException {
	createClassFile(
		"package p;\n" +
		"public class X {\n" +
		"  /**\n" +
		"   * @category" +
		"	 *		test\n" +
		"   */\n" +
		"  void foo() {}\n" +
		"}"
	);
	String[] categories = this.classFile.getType().getFunction("foo", new String[0]).getCategories();
	assertStringsEqual(
		"Unexpected categories",
		"",
		categories);
}
public void testGetCategories09() throws CoreException, IOException {
	createClassFile(
		"package p;\n" +
		"public class X {\n" +
		"  /**\n" +
		"   * @category test1" +
		"	 *		test2\n" +
		"   */\n" +
		"  void foo() {}\n" +
		"}"
	);
	String[] categories = this.classFile.getType().getFunction("foo", new String[0]).getCategories();
	assertStringsEqual(
		"Unexpected categories",
		"test1\n",
		categories);
}

/*
 * Ensure that the categories for a member that has no categories when another member defines some are correct.
 */
public void testGetCategories10() throws CoreException, IOException {
	createClassFile(
		"package p;\n" +
		"public class X {\n" +
		"  int field1;\n" +
		"  /**\n" +
		"   * @category test\n" +
		"   */\n" +
		"  int field2;\n" +
		"}"
	);
	String[] categories = this.classFile.getType().getField("field1").getCategories();
	assertStringsEqual(
		"Unexpected categories",
		"",
		categories);
}

/*
 * Ensures that the children of a type for a given category are correct.
 */
public void testGetChildrenForCategory01() throws CoreException, IOException {
	createClassFile(
		"package p;\n" +
		"public class X {\n" +
		"  /**\n" +
		"   * @category test\n" +
		"   */\n" +
		"  int field;\n" +
		"  /**\n" +
		"   * @category test\n" +
		"   */\n" +
		"  void foo1() {}\n" +
		"  /**\n" +
		"   * @category test\n" +
		"   */\n" +
		"  void foo2() {}\n" +
		"  /**\n" +
		"   * @category other\n" +
		"   */\n" +
		"  void foo3() {}\n" +
		"}"
	);
	IJavaScriptElement[] children = this.classFile.getType().getChildrenForCategory("test");
	assertElementsEqual(
		"Unexpected children",
		"field [in X [in X.class [in p [in lib2.jar [in P]]]]]\n" + 
		"foo1() [in X [in X.class [in p [in lib2.jar [in P]]]]]\n" + 
		"foo2() [in X [in X.class [in p [in lib2.jar [in P]]]]]",
		children);
}
public void testGetChildrenForCategory02() throws CoreException, IOException {
	createClassFile(
		"package p;\n" +
		"public class X {\n" +
		"  /**\n" +
		"   * @category fields test all\n" +
		"   */\n" +
		"  int field;\n" +
		"  /**\n" +
		"   * @category methods test all\n" +
		"   */\n" +
		"  void foo1() {}\n" +
		"  /**\n" +
		"   * @category methods test all\n" +
		"   */\n" +
		"  void foo2() {}\n" +
		"  /**\n" +
		"   * @category methods other all\n" +
		"   */\n" +
		"  void foo3() {}\n" +
		"}"
	);
	IJavaScriptElement[] tests  = this.classFile.getType().getChildrenForCategory("test");
	assertElementsEqual(
		"Unexpected children",
		"field [in X [in X.class [in p [in lib2.jar [in P]]]]]\n" + 
		"foo1() [in X [in X.class [in p [in lib2.jar [in P]]]]]\n" + 
		"foo2() [in X [in X.class [in p [in lib2.jar [in P]]]]]",
		tests);
	IJavaScriptElement[] methods = this.classFile.getType().getChildrenForCategory("methods");
	assertElementsEqual(
		"Unexpected children",
		"foo1() [in X [in X.class [in p [in lib2.jar [in P]]]]]\n" + 
		"foo2() [in X [in X.class [in p [in lib2.jar [in P]]]]]\n" + 
		"foo3() [in X [in X.class [in p [in lib2.jar [in P]]]]]",
		methods);
	IJavaScriptElement[] others = this.classFile.getType().getChildrenForCategory("other");
	assertElementsEqual(
		"Unexpected children",
		"foo3() [in X [in X.class [in p [in lib2.jar [in P]]]]]",
		others);
	IJavaScriptElement[] all = this.classFile.getType().getChildrenForCategory("all");
	assertElementsEqual(
		"Unexpected children",
		"field [in X [in X.class [in p [in lib2.jar [in P]]]]]\n" + 
		"foo1() [in X [in X.class [in p [in lib2.jar [in P]]]]]\n" + 
		"foo2() [in X [in X.class [in p [in lib2.jar [in P]]]]]\n" + 
		"foo3() [in X [in X.class [in p [in lib2.jar [in P]]]]]",
		all);
}

/*
 * Ensures that IType#getSuperclassTypeSignature() is correct for a binary type.
 * (regression test for bug 78520 [model] IType#getSuperInterfaceTypeSignatures() doesn't include type arguments)
 */
public void testGetSuperclassTypeSignature() throws JavaScriptModelException {
	IType type = this.jarRoot.getPackageFragment("generic").getClassFile("V.class").getType();
	assertEquals(
		"Unexpected signature", 
		"Lgeneric.X<Ljava.lang.Thread;>;",
		type.getSuperclassTypeSignature());
}

/*
 * Ensures that the parameter names of a binary method with source attached are correct.
 */
public void testParameterNames01() throws CoreException {
	IFunction method = this.jarRoot.getPackageFragment("generic").getClassFile("X.class").getType().getFunction("foo", new String[] {"TK;", "TV;"});
	String[] parameterNames = method.getParameterNames();
	assertStringsEqual(
		"Unexpected parameter names", 
		"key\n" + 
		"value\n",
		parameterNames);
}

/*
 * Ensures that the parameter names of a binary method without source attached are correct.
 */
public void testParameterNames02() throws CoreException {
	IPath sourceAttachmentPath = this.jarRoot.getSourceAttachmentPath();
	try {
		attachSource(this.jarRoot, null, null);
		IFunction method = this.jarRoot.getPackageFragment("generic").getClassFile("X.class").getType().getFunction("foo", new String[] {"TK;", "TV;"});
		String[] parameterNames = method.getParameterNames();
		assertStringsEqual(
			"Unexpected parameter names", 
			"arg0\n" + 
			"arg1\n",
			parameterNames);
	} finally {
		attachSource(this.jarRoot, sourceAttachmentPath.toString(), null);
	}
}

/*
 * Ensures that the raw parameter names of a binary method with source attached are correct.
 */
public void testRawParameterNames01() throws CoreException {
	IFunction method = this.jarRoot.getPackageFragment("generic").getClassFile("X.class").getType().getFunction("foo", new String[] {"TK;", "TV;"});
	String[] parameterNames = method.getRawParameterNames();
	assertStringsEqual(
		"Unexpected parameter names", 
		"arg0\n" + 
		"arg1\n",
		parameterNames);
}

/*
 * Ensures that the raw parameter names of a binary method without source attached are correct.
 */
public void testRawParameterNames02() throws CoreException {
	IPath sourceAttachmentPath = this.jarRoot.getSourceAttachmentPath();
	try {
		attachSource(this.jarRoot, null, null);
		IFunction method = this.jarRoot.getPackageFragment("generic").getClassFile("X.class").getType().getFunction("foo", new String[] {"TK;", "TV;"});
		String[] parameterNames = method.getParameterNames();
		assertStringsEqual(
			"Unexpected parameter names", 
			"arg0\n" + 
			"arg1\n",
			parameterNames);
	} finally {
		attachSource(this.jarRoot, sourceAttachmentPath.toString(), null);
	}
}

/*
 * Ensure that the return type of a binary method is correct.
 */
public void testReturnType1() throws JavaScriptModelException {
	IType type = this.jarRoot.getPackageFragment("generic").getClassFile("X.class").getType();
	IFunction method = type.getFunction("foo", new String[] {"TK;", "TV;"});
	assertEquals(
		"Unexpected return type",
		"TV;",
		method.getReturnType());
}

/*
 * Ensure that the return type of a binary method is correct.
 */
public void testReturnType2() throws JavaScriptModelException {
	IType type = this.jarRoot.getPackageFragment("generic").getClassFile("X.class").getType();
	IFunction method = type.getFunction("foo", new String[] {"Lgeneric.X<TT;>;"});
	assertEquals(
		"Unexpected return type",
		"Lgeneric.X<TT;>;",
		method.getReturnType());
}

/*
 * Ensures that asking for the source range of a IClassFile in a non-Java project throws a JavaScriptModelException
 * (regression test for bug 132494 JavaScriptModelException opening up class file in non java project)
 */
public void testSourceRangeNonJavaProject() throws CoreException {
	try {
		createProject("Simple");
		createFile("/Simple/X.class", "");
		IClassFile classX = getClassFile("/Simple/X.class");
		JavaScriptModelException exception = null;
		try {
			classX.getSourceRange();
		} catch (JavaScriptModelException e) {
			exception = e;
		}
		assertExceptionEquals("Unexpected exception", "Simple does not exist", exception);
	} finally {
		deleteProject("Simple");
	}
}

/*
 * Ensures that asking for the source range of a IClassFile not on the classpath of a Java project doesn't throw a JavaScriptModelException
 * (regression test for bug 138507 exception in .class file editor for classes imported via plug-in import)
 */
public void testSourceRangeNotOnClasspath() throws CoreException {
	try {
		createJavaProject("P2", new String[] {"src"});
		createFile("/P2/bin/X.class", "");
		IClassFile classX = getClassFile("/P2/bin/X.class");
		assertNull("Unxepected source range", classX.getSourceRange());
	} finally {
		deleteProject("P2");
	}
}

/*
 * Ensure that a method with varargs has the AccVarargs flag set.
 */
public void testVarargs() throws JavaScriptModelException {
	IType type = this.jarRoot.getPackageFragment("varargs").getClassFile("X.class").getType();
	IFunction method = type.getFunction("foo", new String[]{"Ljava.lang.String;", "[Ljava.lang.Object;"});
	assertTrue("Should have the AccVarargs flag set", Flags.isVarargs(method.getFlags()));
}

/*
 * Ensures that a class file can be turned into a working copy and that its children are correct.
 */
public void testWorkingCopy01() throws CoreException {
	IClassFile clazz = this.jarRoot.getPackageFragment("workingcopy").getClassFile("X.class");
	this.workingCopy = clazz.getWorkingCopy(null, null);
	assertElementDescendants(
		"Unexpected children", 
		"[Working copy] X.class\n" + 
		"  package workingcopy\n" + 
		"  class X\n" + 
		"    void foo()",
		this.workingCopy);
}

/*
 * Ensures that a class file without source attached can be turned into a working copy and that its children are correct.
 */
public void testWorkingCopy02() throws CoreException {
	IPath sourceAttachmentPath = this.jarRoot.getSourceAttachmentPath();
	try {
		attachSource(this.jarRoot, null, null);
		IClassFile clazz = this.jarRoot.getPackageFragment("workingcopy").getClassFile("X.class");
		assertNull("Should not have source attached", clazz.getSource());
		this.workingCopy = clazz.getWorkingCopy(null, null);
		assertElementDescendants(
			"Unexpected children", 
			"[Working copy] X.class\n" + 
			"  package workingcopy\n" + 
			"  class X\n" + 
			"    X()\n" + 
			"    void foo()",
			this.workingCopy);
	} finally {
		attachSource(this.jarRoot, sourceAttachmentPath.toString(), null);
	}
}

/*
 * Ensures that a class file can be turned into a working copy, modified and that its children are correct.
 */
public void testWorkingCopy03() throws CoreException {
	IClassFile clazz = this.jarRoot.getPackageFragment("workingcopy").getClassFile("X.class");
	this.workingCopy = clazz.getWorkingCopy(null, null);
	this.workingCopy.getBuffer().setContents(
		"package workingcopy;\n" +
		"public class X {\n" + 
		"  void bar() {\n" +
		"  }\n" +
		"}"
	);
	this.workingCopy.reconcile(IJavaScriptUnit.NO_AST, false/*don't force problems*/, null/*primary owner*/, null/*no progress*/);
	assertElementDescendants(
		"Unexpected children", 
		"[Working copy] X.class\n" + 
		"  package workingcopy\n" + 
		"  class X\n" + 
		"    void bar()",
		this.workingCopy);
}

/*
 * Ensures that a class file working copy cannot be commited
 */
public void testWorkingCopy04() throws CoreException {
	IClassFile clazz = this.jarRoot.getPackageFragment("workingcopy").getClassFile("X.class");
	this.workingCopy = clazz.getWorkingCopy(null, null);
	this.workingCopy.getBuffer().setContents(
		"package workingcopy;\n" +
		"public class X {\n" + 
		"  void bar() {\n" +
		"  }\n" +
		"}"
	);
	JavaScriptModelException exception = null;
	try {
		this.workingCopy.commitWorkingCopy(false/*don't force*/, null);
	} catch (JavaScriptModelException e) {
		exception = e;
	}
	assertEquals(
		"Unxepected JavaScriptModelException", 
		"JavaScript Model Exception: JavaScript Model Status [Operation not supported for specified element type(s):[Working copy] X.class [in workingcopy [in lib.jar [in P]]]]", 
		exception.toString());
}

/*
 * Ensures that a type can be created in class file working copy.
 */
public void testWorkingCopy05() throws CoreException {
	IClassFile clazz = this.jarRoot.getPackageFragment("workingcopy").getClassFile("X.class");
	this.workingCopy = clazz.getWorkingCopy(null, null);
	this.workingCopy.createType(
		"class Y {\n" + 
		"}",
		null,
		false/*don't force*/,
		null);
	assertElementDescendants(
		"Unexpected children", 
		"[Working copy] X.class\n" + 
		"  package workingcopy\n" + 
		"  class X\n" + 
		"    void foo()\n" + 
		"  class Y",
		this.workingCopy);
}

/*
 * Ensures that the primary compilation unit of class file working copy is correct.
 */
public void testWorkingCopy06() throws CoreException {
	IClassFile clazz = this.jarRoot.getPackageFragment("workingcopy").getClassFile("X.class");
	WorkingCopyOwner owner = new WorkingCopyOwner() {};
	this.workingCopy = clazz.getWorkingCopy(owner, null);
	IJavaScriptUnit primary = this.workingCopy.getPrimary();
	assertEquals("Unexpected owner of primary working copy", null, primary.getOwner());
}

/*
 * Ensures that a class file working copy can be restored from the original source.
 */
public void testWorkingCopy07() throws CoreException {
	IClassFile clazz = this.jarRoot.getPackageFragment("workingcopy").getClassFile("X.class");
	WorkingCopyOwner owner = new WorkingCopyOwner() {};
	this.workingCopy = clazz.getWorkingCopy(owner, null);
	this.workingCopy.getBuffer().setContents(
		"package workingcopy;\n" +
		"public class X {\n" + 
		"  void bar() {\n" +
		"  }\n" +
		"}"
	);
	this.workingCopy.reconcile(IJavaScriptUnit.NO_AST, false/*don't force problems*/, null/*primary owner*/, null/*no progress*/);
	this.workingCopy.restore();
	assertElementDescendants(
		"Unexpected children", 
		"[Working copy] X.class\n" + 
		"  package workingcopy\n" + 
		"  class X\n" + 
		"    void foo()",
		this.workingCopy);
}

/*
 * Ensures that a class file working copy can be reconciled against.
 */
public void testWorkingCopy08() throws CoreException {
	IClassFile clazz = this.jarRoot.getPackageFragment("workingcopy").getClassFile("X.class");
	WorkingCopyOwner owner = new WorkingCopyOwner() {};
	this.workingCopy = clazz.getWorkingCopy(owner, null);
	this.workingCopy.getBuffer().setContents(
		"package workingcopy;\n" +
		"public class X {\n" + 
		"  public void bar() {\n" +
		"  }\n" +
		"}"
	);
	this.workingCopy.makeConsistent(null);
	
	IJavaScriptUnit cu = getCompilationUnit("/P/Y.js");
	IJavaScriptUnit copy = null;
	try {
		ProblemRequestor problemRequestor = new ProblemRequestor();
		copy = cu.getWorkingCopy(owner, null/*no prpgress*/);
		copy.getBuffer().setContents(
			"public class Y {\n" +
			"  void foo(workingcopy.X x) {\n" +
			"    x.bar();\n" +
			"  }\n" +
			"}"
		);
		problemRequestor.problems = new StringBuffer();
		copy.reconcile(IJavaScriptUnit.NO_AST, false/*don't force problems*/, owner, null/*no progress*/);
		assertProblems(
			"Unexpected problems", 
			"----------\n" + 
			"----------\n",
			problemRequestor);
	} finally {
		if (copy != null)
			copy.discardWorkingCopy();
	}
}

/*
 * Ensures that types in a class file are hidden when reconciling against if the class file working copy is empty.
 */
public void testWorkingCopy09() throws CoreException {
	IClassFile clazz = this.jarRoot.getPackageFragment("workingcopy").getClassFile("X.class");
	WorkingCopyOwner owner = new WorkingCopyOwner() {};
	this.workingCopy = clazz.getWorkingCopy(owner, null);
	this.workingCopy.getBuffer().setContents(	"");
	this.workingCopy.makeConsistent(null);
	
	IJavaScriptUnit cu = getCompilationUnit("/P/Y.js");
	IJavaScriptUnit copy = null;
	try {
		ProblemRequestor problemRequestor = new ProblemRequestor();
		copy = cu.getWorkingCopy(owner, null/*no prpgress*/);
		copy.getBuffer().setContents(
			"public class Y {\n" +
			"  workingcopy.X x;\n" +
			"}"
		);
		problemRequestor.problems = new StringBuffer();
		copy.reconcile(IJavaScriptUnit.NO_AST, false/*don't force problems*/, owner, null/*no progress*/);
		assertProblems(
			"Unexpected problems", 
			"----------\n" + 
			"1. ERROR in /P/Y.java\n" + 
			"workingcopy.X cannot be resolved to a type\n" + 
			"----------\n",
			problemRequestor);
	} finally {
		if (copy != null)
			copy.discardWorkingCopy();
	}
}

/*
 * Ensures that a 1.5 class file without source attached can be turned into a working copy and that its source is correct.
 */
public void testWorkingCopy10() throws CoreException {
	IPath sourceAttachmentPath = this.jarRoot.getSourceAttachmentPath();
	try {
		attachSource(this.jarRoot, null, null);
		IClassFile clazz = this.jarRoot.getPackageFragment("workingcopy").getClassFile("Y.class");
		assertNull("Should not have source attached", clazz.getSource());
		this.workingCopy = clazz.getWorkingCopy(null, null);
		assertSourceEquals(
			"Unexpected source", 
			"package workingcopy;\n" + 
			"public class Y<W> {\n" + 
			"  \n" + 
			"  public Y() {\n" + 
			"  }\n" + 
			"  \n" + 
			"  <T> T foo(T t, java.lang.String... args) {\n" + 
			"    return null;\n" + 
			"  }\n" + 
			"}",
			this.workingCopy.getSource());
	} finally {
		attachSource(this.jarRoot, sourceAttachmentPath.toString(), null);
	}
}

/*
 * Ensures that types in a class file are not found by a search if the class file working copy is empty.
 */
public void testWorkingCopy11() throws CoreException {
	IPackageFragment pkg = this.jarRoot.getPackageFragment("workingcopy");
	IClassFile clazz = pkg.getClassFile("X.class");
	this.workingCopy = clazz.getWorkingCopy(null, null);
	this.workingCopy.getBuffer().setContents(	"");
	this.workingCopy.makeConsistent(null);
	
	IJavaScriptSearchScope scope = SearchEngine.createJavaSearchScope(new IJavaScriptElement[] {pkg});
	AbstractJavaSearchTests.JavaSearchResultCollector requestor = new AbstractJavaSearchTests.JavaSearchResultCollector();
	search("*", IJavaScriptSearchConstants.TYPE, IJavaScriptSearchConstants.DECLARATIONS, scope, requestor);
	assertSearchResults(
		"lib.jar workingcopy.Y",
		requestor);
}
}
