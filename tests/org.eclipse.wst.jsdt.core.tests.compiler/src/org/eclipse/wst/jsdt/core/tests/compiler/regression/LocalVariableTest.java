/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.core.tests.compiler.regression;

import java.util.Map;

import org.eclipse.wst.jsdt.internal.compiler.impl.CompilerOptions;

import junit.framework.Test;

public class LocalVariableTest extends AbstractRegressionTest {
	
public LocalVariableTest(String name) {
	super(name);
}
public static Test suite() {
	return buildAllCompliancesTestSuite(testClass());
}

public void test001() {
	this.runConformTest(new String[] {
		"p/X.java",
		"package p;\n" + 
		"public class X {\n" + 
		"        int foo(){\n" + 
		"                int i;\n" + 
		"                return 1;\n" + 
		"        }\n" + 
		"}\n",
	});
}
public void test002() {
	this.runConformTest(new String[] {
		"p/X.java",
		"package p;\n" + 
		"public class X {\n" + 
		"  void foo() {\n" + 
		"    String temp;\n" + 
		"    try {\n" + 
		"      return;\n" + 
		"    }\n" + 
		"    catch (Exception e){\n" + 
		"    }\n" + 
		"  }\n" + 
		"}\n",
	});
}
public void test003() {
	this.runConformTest(new String[] {
		"p/X.java",
		"package p;\n" + 
		"public class X {\n" + 
		"  void foo() {\n" + 
		"    String temp;\n" + 
		"    try {\n" + 
		"      return;\n" + 
		"    }\n" + 
		"    catch (Exception e) {\n" + 
		"    }\n" + 
		"  }\n" + 
		"}\n",
	});
}
public void test004() {
	this.runConformTest(new String[] {
		"p/X.java",
		"package p;\n" + 
		"public class X {\n" + 
		"  {\n" + 
		"     int i = 1;\n" + 
		"    System.out.println(i);\n" + 
		"  }\n" + 
		"  X(int j){\n" + 
		"  }\n" + 
		"}\n",
	});
}
public void test005() {
	this.runConformTest(new String[] {
		"p/X.java",
		"package p;\n" + 
		"public class X {\n" + 
		"  int j;\n" + 
		"  void f1() {\n" + 
		"    int l;\n" + 
		"    switch (j) {\n" + 
		"      case 0 :\n" + 
		"        l = 10;\n" + 
		"		 l++;\n" + // at least one read usage
		"        break;\n" + 
		"      case 1 :\n" + 
		"        l = 20;\n" + 
		"        break;\n" + 
		"      case 2 :\n" + 
		"        l = 30;\n" + 
		"        break;\n" + 
		"      default :\n" + 
		"        l = 10;\n" + 
		"        break;\n" + 
		"    }\n" + 
		"  }\n" + 
		"  public static void main(String args[]) {\n" + 
		"  }\n" + 
		"}\n",
	});
}

public void test006() {
	this.runConformTest(new String[] {
		"p/Truc.java",
		"package p;\n" + 
		"public class Truc{\n" + 
		"   void foo(){\n" + 
		"      final int i; \n" +
		"	   i = 1;\n" + 
		"      if (false) i = 2;\n" + 
		"   } \n" + 
		"	public static void main(java.lang.String[] args) {\n" + 
		"  		System.out.println(\"SUCCESS\"); \n" + 
		"	}	\n" +
		"}",
	},
	"SUCCESS");
}

public void test007() {
	this.runConformTest(new String[] {
		"p/A.java",
		"package p;\n" + 
		"import p.helper.Y;\n" + 
		"class A extends Y {\n" + 
		"  class Y {\n" + 
		"    int j = i;// i is a protected member inherited from Y\n" + 
		"  }\n" + 
		"}",

		"p/helper/Y.java",
		"package p.helper;\n" + 
		"public class Y {\n" + 
		"  protected int i = 10;\n" + 
		"  public inner in = new inner();\n" + 
		"    \n" + 
		"  protected class inner {\n" + 
		"    public int  f() {\n" + 
		"      return 20;\n" + 
		"    }\n" + 
		"  }\n" + 
		"}",

	});
}
// https://bugs.eclipse.org/bugs/show_bug.cgi?id=127078
public void test008() {
	this.runNegativeTest(
		new String[] {
			"X.java",
			"class X {\n" + 
			"	class Y {\n" + 
			"		Y innerY;\n" + 
			"\n" + 
			"		int longMemberName;\n" + 
			"	}\n" + 
			"\n" + 
			"	static public void main(String args[]) {\n" + 
			"		Y y;\n" + 
			"		System.out.println(y.innerY.longMemberName);\n" + 
			"	}\n" + 
			"}"},
			"----------\n" + 
			"1. ERROR in X.java (at line 10)\n" + 
			"	System.out.println(y.innerY.longMemberName);\n" + 
			"	                   ^\n" + 
			"The local variable y may not have been initialized\n" + 
			"----------\n");
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=127078
public void test009() {
	this.runNegativeTest(
		new String[] {
			"X.java",
			"class X {\n" + 
			"	class Y {\n" + 
			"		int longMemberName;\n" + 
			"	}\n" + 
			"\n" + 
			"	static public void main(String args[]) {\n" + 
			"		Y y;\n" + 
			"		System.out.println(y.longMemberName);\n" + 
			"	}\n" + 
			"}"},
			"----------\n" + 
			"1. ERROR in X.java (at line 8)\n" + 
			"	System.out.println(y.longMemberName);\n" + 
			"	                   ^\n" + 
			"The local variable y may not have been initialized\n" + 
			"----------\n");
}
public void test010() {
	Map options = getCompilerOptions();
	options.put(
		CompilerOptions.OPTION_DocCommentSupport,
		CompilerOptions.ENABLED);
	this.runConformTest(new String[] {
		"p/X.java",
		"package p;\n" + 
		"/**\n" + 
		" * @see Y\n" + 
		" */\n" + 
		"public class X {\n" + 
		"}",
		"p/Y.java",
		"package p;\n" + 
		"class Z {\n" + 
		"}",
	},
	"",
	null,
	true,
	null,
	options,
	null);
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=144426
public void test011() {
	this.runNegativeTest(
			new String[] {
				"X.java",
				"public class X {\n" + 
				"        public static void main(String[] args) {\n" + 
				"                int x = 2;\n" + 
				"                if (true) {\n" + 
				"                        int x = 4;\n" + 
				"                }\n" + 
				"        }\n" + 
				"}\n",
			},
			"----------\n" + 
			"1. ERROR in X.java (at line 5)\n" + 
			"	int x = 4;\n" + 
			"	    ^\n" + 
			"Duplicate local variable x\n" + 
			"----------\n");
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=144858
public void test012() {
	this.runNegativeTest(
			new String[] {
				"X.java",
				"public class X {\n" + 
				"        public static void main(String[] args) {\n" + 
				"                int x = x = 0;\n" + 
				"                if (true) {\n" + 
				"                        int x = x = 1;\n" + 
				"                }\n" + 
				"        }\n" + 
				"}\n",
			},
			"----------\n" + 
			"1. WARNING in X.java (at line 3)\n" + 
			"	int x = x = 0;\n" + 
			"	    ^^^^^^^^^\n" + 
			"The assignment to variable x has no effect\n" + 
			"----------\n" + 
			"2. ERROR in X.java (at line 5)\n" + 
			"	int x = x = 1;\n" + 
			"	    ^\n" + 
			"Duplicate local variable x\n" + 
			"----------\n" + 
			"3. WARNING in X.java (at line 5)\n" + 
			"	int x = x = 1;\n" + 
			"	    ^^^^^^^^^\n" + 
			"The assignment to variable x has no effect\n" + 
			"----------\n");
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=144858 - variation
//check variable collision resiliance (catch argument)
// variable collision should not interfere with exception collision
public void test013() {
	this.runNegativeTest(
			new String[] {
				"X.java",
				"public class X {\n" + 
				"        public static void main(String[] args) {\n" + 
				"                int x = 2;\n" + 
				"                try {\n" + 
				"                	\n" + 
				"                } catch(Exception x) {\n" + 
				"                } catch(Exception e) {\n" + 
				"                }\n" + 
				"        }\n" + 
				"}\n",
			},
			"----------\n" + 
			"1. ERROR in X.java (at line 6)\n" + 
			"	} catch(Exception x) {\n" + 
			"	                  ^\n" + 
			"Duplicate parameter x\n" + 
			"----------\n" + 
			"2. ERROR in X.java (at line 7)\n" + 
			"	} catch(Exception e) {\n" + 
			"	        ^^^^^^^^^\n" + 
			"Unreachable catch block for Exception. It is already handled by the catch block for Exception\n" + 
			"----------\n");
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=144858 - variation
public void test014() {
	this.runNegativeTest(
			new String[] {
				"X.java",
				"public class X {\n" + 
				"	void foo(){\n" + 
				"		int x = 0;\n" + 
				"		String x = \"\";\n" + 
				"		x.toString();\n" + 
				"	  }\n" +
				"	}\n",
			},
			"----------\n" + 
			"1. ERROR in X.java (at line 4)\n" + 
			"	String x = \"\";\n" + 
			"	       ^\n" + 
			"Duplicate local variable x\n" + 
			"----------\n");
}
//https://bugs.eclipse.org/bugs/show_bug.cgi?id=157379
public void test015() {
	Map options = getCompilerOptions();
	if (this.complianceLevel.equals(COMPLIANCE_1_3)) return;
	options.put(CompilerOptions.OPTION_PreserveUnusedLocal, CompilerOptions.OPTIMIZE_OUT);
	this.runConformTest(
			new String[] {
				"X.java",
				"public class X {\n" + 
				"        public static boolean test() {\n" + 
				"                boolean b = false;\n" + 
				"                assert b = true;\n" + 
				"                return false;\n" + 
				"        }\n" + 
				"        public static void main(String[] args) {\n" + 
				"                test();\n" + 
				"                System.out.println(\"SUCCESS\");\n" + 
				"        }\n" + 
				"}\n",
			},
			"SUCCESS",
			null,
			true,
			null,
			options,
			null);
}
public static Class testClass() {
	return LocalVariableTest.class;
}
}
