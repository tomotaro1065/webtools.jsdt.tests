/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.core.tests.dom;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.jsdt.core.IJavaScriptUnit;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.JavaScriptModelException;
import org.eclipse.wst.jsdt.core.dom.ASTNode;
import org.eclipse.wst.jsdt.core.dom.IFunctionBinding;
import org.eclipse.wst.jsdt.core.dom.ITypeBinding;
import org.eclipse.wst.jsdt.core.dom.FunctionDeclaration;

import junit.framework.Test;

public class CompatibilityRulesTests extends AbstractASTTests {
	
	public CompatibilityRulesTests(String name) {
		super(name);
	}

	public static Test suite() {
		return buildModelTestSuite(CompatibilityRulesTests.class);
	}
	
	// Use this static initializer to specify subset for tests
	// All specified tests which do not belong to the class are skipped...
	static {
//		TESTS_PREFIX =  "testBug86380";
//		TESTS_NAMES = new String[] { "test032" };
//		TESTS_NUMBERS = new int[] { 83230 };
//		TESTS_RANGE = new int[] { 83304, -1 };
		}
	
	public void setUpSuite() throws Exception {
		super.setUpSuite();
		createJavaProject("P", new String[] {""}, new String[] {"JCL15_LIB"}, "", "1.5");
	}
	
	public void tearDownSuite() throws Exception {
		deleteProject("P");
		super.tearDownSuite();
	}
	
	/*
	 * Ensures that a subtype is subtype compatible with its super type
	 */
	public void test001() throws JavaScriptModelException {
		ITypeBinding[] bindings = createTypeBindings(
			new String[] {
				"/P/p1/X.js",
				"package p1;\n" +
				"public class X {\n" +
				"}",
				"/P/p1/Y.js",
				"package p1;\n" +
				"public class Y extends X {\n" +
				"}",
			},
			new String[] {
				"Lp1/X;",
				"Lp1/Y;"
			});	
		assertTrue("X should be subtype compatible with Y", bindings[1].isSubTypeCompatible(bindings[0]));
	}
	
	/*
	 * Ensures that a type is subtype compatible with itself
	 */
	public void test002() throws JavaScriptModelException {
		ITypeBinding[] bindings = createTypeBindings(
			new String[] {
				"/P/p1/X.js",
				"package p1;\n" +
				"public class X {\n" +
				"}",
			},
			new String[] {
				"Lp1/X;",
			});	
		assertTrue("X should be subtype compatible with itself", bindings[0].isSubTypeCompatible(bindings[0]));
	}
	
	/*
	 * Ensures that a supertype is not subtype compatible with its subtype
	 */
	public void test003() throws JavaScriptModelException {
		ITypeBinding[] bindings = createTypeBindings(
			new String[] {
				"/P/p1/X.js",
				"package p1;\n" +
				"public class X {\n" +
				"}",
				"/P/p1/Y.js",
				"package p1;\n" +
				"public class Y extends X {\n" +
				"}",
			},
			new String[] {
				"Lp1/X;",
				"Lp1/Y;"
			});	
		assertTrue("X should not be subtype compatible with Y", !bindings[0].isSubTypeCompatible(bindings[1]));
	}
	
	/*
	 * Ensures that a type is not subtype compatible with an unrelated type.
	 */
	public void test004() throws JavaScriptModelException {
		ITypeBinding[] bindings = createTypeBindings(
			new String[] {
				"/P/p1/X.js",
				"package p1;\n" +
				"public class X {\n" +
				"}",
				"/P/p1/Y.js",
				"package p1;\n" +
				"public class Y {\n" +
				"}",
			},
			new String[] {
				"Lp1/X;",
				"Lp1/Y;"
			});	
		assertTrue("X should not be subtype compatible with Y", !bindings[0].isSubTypeCompatible(bindings[1]));
	}
	
	/*
	 * Ensures that the int base type is not subtype compatible with the long base type
	 */
	public void test005() throws JavaScriptModelException {
		ITypeBinding[] bindings = createTypeBindings(
			new String[] {},
			new String[] {
				"I",
				"J"
			});	
		assertTrue("int should not be subtype compatible with long", !bindings[0].isSubTypeCompatible(bindings[1]));
	}

	/*
	 * Ensures that the int base type is not subtype compatible with the java.lang.Object type
	 */
	public void test006() throws JavaScriptModelException {
		ITypeBinding[] bindings = createTypeBindings(
			new String[] {},
			new String[] {
				"I",
				"Ljava/lang/Object;"
			});	
		assertTrue("int should not be subtype compatible with Object", !bindings[0].isSubTypeCompatible(bindings[1]));
	}
	
	/*
	 * Ensures that a subtype is assignment compatible with its super type
	 */
	public void test007() throws JavaScriptModelException {
		ITypeBinding[] bindings = createTypeBindings(
			new String[] {
				"/P/p1/X.js",
				"package p1;\n" +
				"public class X {\n" +
				"}",
				"/P/p1/Y.js",
				"package p1;\n" +
				"public class Y extends X {\n" +
				"}",
			},
			new String[] {
				"Lp1/X;",
				"Lp1/Y;"
			});	
		assertTrue("X should be assignment compatible with Y", bindings[1].isAssignmentCompatible(bindings[0]));
	}
	
	/*
	 * Ensures that a type is assignment compatible with itself
	 */
	public void test008() throws JavaScriptModelException {
		ITypeBinding[] bindings = createTypeBindings(
			new String[] {
				"/P/p1/X.js",
				"package p1;\n" +
				"public class X {\n" +
				"}",
			},
			new String[] {
				"Lp1/X;",
			});	
		assertTrue("X should be assignment compatible with itself", bindings[0].isAssignmentCompatible(bindings[0]));
	}
	
	/*
	 * Ensures that a supertype is not assignment compatible with its subtype
	 */
	public void test009() throws JavaScriptModelException {
		ITypeBinding[] bindings = createTypeBindings(
			new String[] {
				"/P/p1/X.js",
				"package p1;\n" +
				"public class X {\n" +
				"}",
				"/P/p1/Y.js",
				"package p1;\n" +
				"public class Y extends X {\n" +
				"}",
			},
			new String[] {
				"Lp1/X;",
				"Lp1/Y;"
			});	
		assertTrue("X should not be assignment compatible with Y", !bindings[0].isAssignmentCompatible(bindings[1]));
	}
	
	/*
	 * Ensures that a type is not assigment compatible with an unrelated type.
	 */
	public void test010() throws JavaScriptModelException {
		ITypeBinding[] bindings = createTypeBindings(
			new String[] {
				"/P/p1/X.js",
				"package p1;\n" +
				"public class X {\n" +
				"}",
				"/P/p1/Y.js",
				"package p1;\n" +
				"public class Y {\n" +
				"}",
			},
			new String[] {
				"Lp1/X;",
				"Lp1/Y;"
			});	
		assertTrue("X should not be assigment compatible with Y", !bindings[0].isAssignmentCompatible(bindings[1]));
	}
	
	/*
	 * Ensures that the int base type is assignment compatible with the long base type
	 */
	public void test011() throws JavaScriptModelException {
		ITypeBinding[] bindings = createTypeBindings(
			new String[] {},
			new String[] {
				"I",
				"J"
			});	
		assertTrue("int should be assignment compatible with long", bindings[0].isAssignmentCompatible(bindings[1]));
	}

	/*
	 * Ensures that the int base type is not assignment compatible with the java.lang.Object type in 1.4 mode.
	 */
	public void test012() throws CoreException {
		try {
			IJavaScriptProject project = createJavaProject("P14", new String[] {""}, new String[] {"JCL_LIB"}, "", "1.4");
			ITypeBinding[] bindings = createTypeBindings(
				new String[] {},
				new String[] {
					"I",
					"Ljava/lang/Object;"
				},
				project);	
			assertTrue("int should not be assignment compatible with Object", !bindings[0].isAssignmentCompatible(bindings[1]));
		} finally {
			deleteProject("P14");
		}
	}
	
	/*
	 * Ensures that a subtype is cast compatible with its super type
	 */
	public void test013() throws JavaScriptModelException {
		ITypeBinding[] bindings = createTypeBindings(
			new String[] {
				"/P/p1/X.js",
				"package p1;\n" +
				"public class X {\n" +
				"}",
				"/P/p1/Y.js",
				"package p1;\n" +
				"public class Y extends X {\n" +
				"}",
			},
			new String[] {
				"Lp1/X;",
				"Lp1/Y;"
			});	
		assertTrue("X should be cast compatible with Y", bindings[1].isCastCompatible(bindings[0]));
	}
	
	/*
	 * Ensures that a type is cast compatible with itself
	 */
	public void test014() throws JavaScriptModelException {
		ITypeBinding[] bindings = createTypeBindings(
			new String[] {
				"/P/p1/X.js",
				"package p1;\n" +
				"public class X {\n" +
				"}",
			},
			new String[] {
				"Lp1/X;",
			});	
		assertTrue("X should be cast compatible with itself", bindings[0].isCastCompatible(bindings[0]));
	}
	
	/*
	 * Ensures that a supertype is cast compatible with its subtype
	 */
	public void test015() throws JavaScriptModelException {
		ITypeBinding[] bindings = createTypeBindings(
			new String[] {
				"/P/p1/X.js",
				"package p1;\n" +
				"public class X {\n" +
				"}",
				"/P/p1/Y.js",
				"package p1;\n" +
				"public class Y extends X {\n" +
				"}",
			},
			new String[] {
				"Lp1/X;",
				"Lp1/Y;"
			});	
		assertTrue("X should be cast compatible with Y", bindings[0].isCastCompatible(bindings[1]));
	}
	
	/*
	 * Ensures that a type is not cast compatible with an unrelated type.
	 */
	public void test016() throws JavaScriptModelException {
		ITypeBinding[] bindings = createTypeBindings(
			new String[] {
				"/P/p1/X.js",
				"package p1;\n" +
				"public class X {\n" +
				"}",
				"/P/p1/Y.js",
				"package p1;\n" +
				"public class Y {\n" +
				"}",
			},
			new String[] {
				"Lp1/X;",
				"Lp1/Y;"
			});	
		assertTrue("X should not be cast compatible with Y", !bindings[0].isCastCompatible(bindings[1]));
	}
	
	/*
	 * Ensures that the int base type is cast compatible with the long base type
	 */
	public void test017() throws JavaScriptModelException {
		ITypeBinding[] bindings = createTypeBindings(
			new String[] {},
			new String[] {
				"I",
				"J"
			});	
		assertTrue("int should be cast compatible with long", bindings[0].isCastCompatible(bindings[1]));
	}

	/*
	 * Ensures that the int base type is not cast compatible with the java.lang.Object type
	 */
	public void test018() throws JavaScriptModelException {
		ITypeBinding[] bindings = createTypeBindings(
			new String[] {},
			new String[] {
				"I",
				"Ljava/lang/Object;"
			});	
		assertTrue("int should not be cast compatible with Object", !bindings[0].isCastCompatible(bindings[1]));
	}
	
	/*
	 * Ensures that a method in a subtype overrides the corresponding method in the super type.
	 */
	public void test019() throws JavaScriptModelException {
		IFunctionBinding[] bindings = createMethodBindings(
			new String[] {
				"/P/p1/X.js",
				"package p1;\n" +
				"public class X {\n" +
				"  void foo() {\n" +
				"  }\n" +
				"}",
				"/P/p1/Y.js",
				"package p1;\n" +
				"public class Y extends X {\n" +
				"  void foo() {\n" +
				"  }\n" +
				"}",
			},
			new String[] {
				"Lp1/Y;.foo()V",
				"Lp1/X;.foo()V"
			});	
		assertTrue("Y#foo() should override X#foo()", bindings[0].overrides(bindings[1]));
	}
	
	/*
	 * Ensures that a method in a super type doesn't override the corresponding method in a subtype.
	 */
	public void test020() throws JavaScriptModelException {
		IFunctionBinding[] bindings = createMethodBindings(
			new String[] {
				"/P/p1/X.js",
				"package p1;\n" +
				"public class X {\n" +
				"  void foo() {\n" +
				"  }\n" +
				"}",
				"/P/p1/Y.js",
				"package p1;\n" +
				"public class Y extends X {\n" +
				"  void foo() {\n" +
				"  }\n" +
				"}",
			},
			new String[] {
				"Lp1/X;.foo()V",
				"Lp1/Y;.foo()V"
			});	
		assertTrue("X#foo() should not override Y#foo()", !bindings[0].overrides(bindings[1]));
	}

	/*
	 * Ensures that a method doesn't override the corresponding method in an unrelated type.
	 */
	public void test021() throws JavaScriptModelException {
		IFunctionBinding[] bindings = createMethodBindings(
			new String[] {
				"/P/p1/X.js",
				"package p1;\n" +
				"public class X {\n" +
				"  void foo() {\n" +
				"  }\n" +
				"}",
				"/P/p1/Y.js",
				"package p1;\n" +
				"public class Y {\n" +
				"  void foo() {\n" +
				"  }\n" +
				"}",
			},
			new String[] {
				"Lp1/X;.foo()V",
				"Lp1/Y;.foo()V"
			});	
		assertTrue("X#foo() should not override Y#foo()", !bindings[0].overrides(bindings[1]));
	}

	/*
	 * Ensures that IFunctionBinding#ovverides(IFunctionBinding) doesn't throw a NullPointerException if
	 * the method was not built in a batch.
	 * (regression test for bug 79635 NPE when asking an IFunctionBinding whether it overrides itself)
	 */
	public void test022() throws JavaScriptModelException {
		IJavaScriptUnit workingCopy = null;
		try {
			workingCopy = getWorkingCopy("/P/p1/X.js", true/*compute problems to get bindings*/);
			ASTNode node = buildAST(
				"package p1;\n" +
				"public class X {\n" +
				"  /*start*/void foo() {\n" +
				"  }/*end*/\n" +
				"}",
				workingCopy);
			IFunctionBinding methodBinding = ((FunctionDeclaration) node).resolveBinding();
			assertTrue("X#foo() should not override itself", !methodBinding.overrides(methodBinding));
		} finally {
			if (workingCopy != null)
				workingCopy.discardWorkingCopy();
		}
	}

	/*
	 * Ensures that a base type is assignment compatible with its wrapper type
	 * (regression test for bug 80455 [5.0] ITypeBinding.canAssign not aware of type boxing)
	 */
	public void test023() throws JavaScriptModelException {
		ITypeBinding[] bindings = createTypeBindings(
			new String[] {
				"/P/java/lang/Integer.js",
				"package java.lang;\n" +
				"public class Integer {\n" +
				"}",
			},
			new String[] {
				"I",
				"Ljava/lang/Integer;",
			});	
		assertTrue("int should be assignment compatible with Integer", bindings[0].isAssignmentCompatible(bindings[1]));
	}
	
	/*
	 * Ensures that a base type is assignment compatible with Object
	 */
	public void test024() throws JavaScriptModelException {
		ITypeBinding[] bindings = createTypeBindings(
			new String[] {},
			new String[] {
				"I",
				"Ljava/lang/Object;",
			});	
		assertTrue("int should be assignment compatible with Object", bindings[0].isAssignmentCompatible(bindings[1]));
	}
	
	/*
	 * Ensures that a method is subsignature of itself.
	 */
	public void test025() throws JavaScriptModelException {
		IFunctionBinding[] bindings = createMethodBindings(
			new String[] {
				"/P/p1/X.js",
				"package p1;\n" +
				"public class X {\n" +
				"  void foo() {\n" +
				"  }\n" +
				"}",
			},
			new String[] {
				"Lp1/X;.foo()V"
			});	
		assertTrue("X#foo() should be a subsignature of X#foo()", bindings[0].isSubsignature(bindings[0]));
	}
	
	/*
	 * Ensures that a method is subsignature of its super method.
	 */
	public void test026() throws JavaScriptModelException {
		IFunctionBinding[] bindings = createMethodBindings(
			new String[] {
				"/P/p1/X.js",
				"package p1;\n" +
				"public class X {\n" +
				"  String foo(Object o) {\n" +
				"  }\n" +
				"}",
				"/P/p1/Y.js",
				"package p1;\n" +
				"public class Y extends X {\n" +
				"  String foo(Object o) {\n" +
				"  }\n" +
				"}",
			},
			new String[] {
				"Lp1/X;.foo(Ljava/lang/Object;)Ljava/lang/String;",
				"Lp1/Y;.foo(Ljava/lang/Object;)Ljava/lang/String;",
			});	
		assertTrue("Y#foo(Object) should be a subsignature of X#foo(Object)", bindings[1].isSubsignature(bindings[0]));
	}
		
	/*
	 * Ensures that a method is subsignature of its super generic method.
	 */
	public void test027() throws JavaScriptModelException {
		IFunctionBinding[] bindings = createMethodBindings(
			new String[] {
				"/P/p1/X.js",
				"package p1;\n" +
				"public class X<T>  {\n" +
				"  Z<T> foo(Z<T> o) {\n" +
				"  }\n" +
				"}",
				"/P/p1/Y.js",
				"package p1;\n" +
				"public class Y extends X {\n" +
				"  Z foo(Z o) {\n" +
				"  }\n" +
				"}",
				"/P/p1/Z.js",
				"package p1;\n" +
				"public class Z<T> {\n" +
				"}",
			},
			new String[] {
				"Lp1/X;.foo(Lp1/Z<TT;>;)Lp1/Z<TT;>;",
				"Lp1/Y;.foo(Lp1/Z;)Lp1/Z;",
			});	
		assertTrue("Y#foo(Z) should be a subsignature of X#foo(Z<T>)", bindings[1].isSubsignature(bindings[0]));
	}
		
	/*
	 * Ensures that a method is not the subsignature of an unrelated method.
	 */
	public void test028() throws JavaScriptModelException {
		IFunctionBinding[] bindings = createMethodBindings(
			new String[] {
				"/P/p1/X.js",
				"package p1;\n" +
				"public class X {\n" +
				"  void foo() {\n" +
				"  }\n" +
				"}",
				"/P/p1/Y.js",
				"package p1;\n" +
				"public class Y {\n" +
				"  void bar() {\n" +
				"  }\n" +
				"}",
			},
			new String[] {
				"Lp1/X;.foo()V",
				"Lp1/Y;.bar()V",
			});	
		assertTrue("Y#bar() should not be a subsignature of X#foo()", !bindings[1].isSubsignature(bindings[0]));
	}
		
	/*
	 * Ensures that a method in a subtype doesn't override the a method with same parameters but with different name in the super type.
	 */
	public void test029() throws JavaScriptModelException {
		IFunctionBinding[] bindings = createMethodBindings(
			new String[] {
				"/P/p1/X.js",
				"package p1;\n" +
				"public class X {\n" +
				"  void foo() {\n" +
				"  }\n" +
				"}",
				"/P/p1/Y.js",
				"package p1;\n" +
				"public class Y extends X {\n" +
				"  void bar() {\n" +
				"  }\n" +
				"}",
			},
			new String[] {
				"Lp1/Y;.bar()V",
				"Lp1/X;.foo()V"
			});	
		assertTrue("Y#bar() should not override X#foo()", !bindings[0].overrides(bindings[1]));
	}

	/*
	 * Ensures that a method in a subtype overrides a method in the super parameterized type.
	 * (regression test for bug 99608 IFunctionBinding#overrides returns false on overridden method)
	 */
	public void test030() throws JavaScriptModelException {
		IFunctionBinding[] bindings = createMethodBindings(
			new String[] {
				"/P/p1/X.js",
				"package p1;\n" +
				"public class X<T> {\n" +
				"  void foo(T t) {\n" +
				"  }\n" +
				"}",
				"/P/p1/Y.js",
				"package p1;\n" +
				"public class Y extends X<String> {\n" +
				"  void foo(String s) {\n" +
				"  }\n" +
				"}",
			},
			new String[] {
				"Lp1/Y;.foo(Ljava/lang/String;)V",
				"Lp1/X;.foo(TT;)V"
			});	
		assertTrue("Y#foo(String) should override X#foo(T)", bindings[0].overrides(bindings[1]));
	}
	
	/*
	 * Ensures that a method with the same parameter types but with different type parameters is not a subsignature of its super method.
	 * (regression test for bug 107110 IFunctionBinding.isSubsignature not yet correctly implemented)
	 */
	public void test031() throws JavaScriptModelException {
		IFunctionBinding[] bindings = createMethodBindings(
			new String[] {
				"/P/p1/X.js",
				"package p1;\n" +
				"public class X {\n" +
				"  void foo() {\n" +
				"  }\n" +
				"}\n" +
				"class Y extends X {\n" +
				"  <T> void foo() {\n" +
				"  }\n" +
				"}",
			},
			new String[] {
				"Lp1/X;.foo()V",
				"Lp1/Y;.foo<T:Ljava/lang/Object;>()V"
			});	
		assertFalse("Y#foo() should not be a subsignature of X#foo()", bindings[1].isSubsignature(bindings[0]));
	}
	
	/*
	 * Ensures that a method in a subtype overrides the corresponding method in the super type
	 * even if the two methods have different return types.
	 * (regression test for bug 105808 [1.5][dom] FunctionBinding#overrides(..) should not consider return types)
	 */
	public void test032() throws CoreException {
		try {
			IJavaScriptProject project = createJavaProject("P2", new String[] {""}, new String[] {"JCL_LIB"}, "", "1.4");
			IFunctionBinding[] bindings = createMethodBindings(
				new String[] {
					"/P/p1/X.js",
					"package p1;\n" +
					"public class X {\n" +
					"  Object foo() {\n" +
					"  }\n" +
					"}",
					"/P/p1/Y.js",
					"package p1;\n" +
					"public class Y extends X {\n" +
					"  String foo() {\n" +
					"  }\n" +
					"}",
				},
				new String[] {
					"Lp1/Y;.foo()Ljava/lang/String;",
					"Lp1/X;.foo()Ljava/lang/Object;"
				},
				project);	
			assertTrue("Y#foo() should override X#foo()", bindings[0].overrides(bindings[1]));
		} finally {
			deleteProject("P2");
		}
	}
	
	/*
	 * Ensures that a method in a subtype doesn't override the corresponding private method in the super type.
	 * (regression test for bug 132191 IFunctionBinding.overrides(IFunctionBinding) returns true even if the given argument is private.)
	 */
	public void test033() throws JavaScriptModelException {
		IFunctionBinding[] bindings = createMethodBindings(
			new String[] {
				"/P/p1/X.js",
				"package p1;\n" +
				"public class X {\n" +
				"  private void foo() {\n" +
				"  }\n" +
				"}",
				"/P/p1/Y.js",
				"package p1;\n" +
				"public class Y extends X {\n" +
				"  void foo() {\n" +
				"  }\n" +
				"}",
			},
			new String[] {
				"Lp1/Y;.foo()V",
				"Lp1/X;.foo()V"
			});	
		assertTrue("Y#foo() should not override X#foo()", !bindings[0].overrides(bindings[1]));
	}
	
	/*
	 * Ensures that a method in a subtype doesn't override the corresponding default method in the super type in a different package.
	 * (regression test for bug 132191 IFunctionBinding.overrides(IFunctionBinding) returns true even if the given argument is private.)
	 */
	public void test034() throws JavaScriptModelException {
		IFunctionBinding[] bindings = createMethodBindings(
			new String[] {
				"/P/p1/X.js",
				"package p1;\n" +
				"public class X {\n" +
				"  void foo() {\n" +
				"  }\n" +
				"}",
				"/P/p2/Y.js",
				"package p2;\n" +
				"public class Y extends p1.X {\n" +
				"  void foo() {\n" +
				"  }\n" +
				"}",
			},
			new String[] {
				"Lp2/Y;.foo()V",
				"Lp1/X;.foo()V"
			});	
		assertTrue("Y#foo() should not override X#foo()", !bindings[0].overrides(bindings[1]));
	}
	
}