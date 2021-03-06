/*******************************************************************************
 * Copyright (c) 2006, 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.wst.jsdt.core.tests.dom;

import java.util.List;

import junit.framework.Test;

import org.eclipse.wst.jsdt.core.IJavaScriptUnit;
import org.eclipse.wst.jsdt.core.JavaScriptModelException;
import org.eclipse.wst.jsdt.core.dom.AST;
import org.eclipse.wst.jsdt.core.dom.ASTNode;
import org.eclipse.wst.jsdt.core.dom.ArrayInitializer;
import org.eclipse.wst.jsdt.core.dom.Assignment;
import org.eclipse.wst.jsdt.core.dom.Block;
import org.eclipse.wst.jsdt.core.dom.EmptyStatement;
import org.eclipse.wst.jsdt.core.dom.Expression;
import org.eclipse.wst.jsdt.core.dom.ExpressionStatement;
import org.eclipse.wst.jsdt.core.dom.ForStatement;
import org.eclipse.wst.jsdt.core.dom.FunctionDeclaration;
import org.eclipse.wst.jsdt.core.dom.FunctionInvocation;
import org.eclipse.wst.jsdt.core.dom.ITypeBinding;
import org.eclipse.wst.jsdt.core.dom.JavaScriptUnit;
import org.eclipse.wst.jsdt.core.dom.NumberLiteral;
import org.eclipse.wst.jsdt.core.dom.SimpleName;
import org.eclipse.wst.jsdt.core.dom.Statement;
import org.eclipse.wst.jsdt.core.dom.StringLiteral;
import org.eclipse.wst.jsdt.core.dom.VariableDeclarationExpression;
import org.eclipse.wst.jsdt.core.dom.VariableDeclarationFragment;
import org.eclipse.wst.jsdt.core.dom.VariableDeclarationStatement;

public class ASTConverterRecoveryTest extends ConverterTestSetup {
	public ASTConverterRecoveryTest(String name) {
		super(name);
	}

	static {
//		TESTS_NAMES = new String[] {"test0003"};
//		TESTS_NUMBERS =  new int[] { 624 };
	}
	public static Test suite() {
		return buildModelTestSuite(ASTConverterRecoveryTest.class);
	}
	
	public void setUpSuite() throws Exception {
		super.setUpSuite();
		this.ast = AST.newAST(AST.JLS3);
	}
	
	public void test0001() throws JavaScriptModelException {
		this.workingCopies = new IJavaScriptUnit[1];
		this.workingCopies[0] = getWorkingCopy(
			"/Converter/src/test/X.js",
			"package test;\n"+
			"\n"+
			"public class X {\n"+
			"	void foo() {\n"+
			"	    bar(0)\n"+
			"	    baz(1);\n"+
			"	}\n"+
			"}\n");
		
		char[] source = this.workingCopies[0].getSource().toCharArray();
		ASTNode result = runConversion(AST.JLS3, this.workingCopies[0], true, true);
		
		assertASTNodeEquals(
			"package test;\n" + 
			"public class X {\n" + 
			"  void foo(){\n" + 
			"    bar(0);\n" + 
			"    baz(1);\n" + 
			"  }\n" + 
			"}\n",
			result);
		
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0);
		assertNotNull(node);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		Block block = methodDeclaration.getBody();
		List statements = block.statements();
		assertEquals("wrong size", 2, statements.size()); //$NON-NLS-1$
		Statement statement = (Statement) statements.get(0);
		assertTrue("Not an expression statement", statement.getNodeType() == ASTNode.EXPRESSION_STATEMENT); //$NON-NLS-1$
		ExpressionStatement expressionStatement = (ExpressionStatement) statement;
		checkSourceRange(expressionStatement, "bar(0)", source); //$NON-NLS-1$
		Expression expression = expressionStatement.getExpression();
		assertTrue("Not a method invocation", expression.getNodeType() == ASTNode.FUNCTION_INVOCATION); //$NON-NLS-1$
		FunctionInvocation methodInvocation = (FunctionInvocation) expression;
		checkSourceRange(methodInvocation, "bar(0)", source); //$NON-NLS-1$
		List list = methodInvocation.arguments();
		assertTrue("Parameter list is empty", list.size() == 1); //$NON-NLS-1$
		Expression parameter = (Expression) list.get(0);
		assertTrue("Not a number", parameter instanceof NumberLiteral); //$NON-NLS-1$
		ITypeBinding typeBinding = parameter.resolveTypeBinding();
		assertNotNull("No binding", typeBinding); //$NON-NLS-1$
		assertEquals("Not int", "int", typeBinding.getName()); //$NON-NLS-1$ //$NON-NLS-2$
		checkSourceRange(parameter, "0", source); //$NON-NLS-1$
		Statement statement2 = (Statement) statements.get(1);
		assertTrue("Not an expression statement", statement2.getNodeType() == ASTNode.EXPRESSION_STATEMENT); //$NON-NLS-1$
		ExpressionStatement expressionStatement2 = (ExpressionStatement) statement2;
		checkSourceRange(expressionStatement2, "baz(1);", source); //$NON-NLS-1$
		Expression expression2 = expressionStatement2.getExpression();
		assertTrue("Not a method invocation", expression2.getNodeType() == ASTNode.FUNCTION_INVOCATION); //$NON-NLS-1$
		FunctionInvocation methodInvocation2 = (FunctionInvocation) expression2;
		checkSourceRange(methodInvocation2, "baz(1)", source); //$NON-NLS-1$
		List list2 = methodInvocation2.arguments();
		assertTrue("Parameter list is empty", list2.size() == 1); //$NON-NLS-1$
		Expression parameter2 = (Expression) list2.get(0);
		assertTrue("Not a number", parameter2 instanceof NumberLiteral); //$NON-NLS-1$
		ITypeBinding typeBinding2 = parameter2.resolveTypeBinding();
		assertNotNull("No binding", typeBinding2); //$NON-NLS-1$
		assertEquals("Not int", "int", typeBinding2.getName()); //$NON-NLS-1$ //$NON-NLS-2$
		checkSourceRange(parameter2, "1", source); //$NON-NLS-1$
	}
	
	public void test0002() throws JavaScriptModelException {
		this.workingCopies = new IJavaScriptUnit[1];
		this.workingCopies[0] = getWorkingCopy(
			"/Converter/src/test/X.js",
			"package test;\n"+
			"\n"+
			"public class X {\n"+
			"	void foo() {\n"+
			"	    baz(0);\n"+
			"	    bar(1,\n"+
			"	}\n"+
			"}\n");
		
		char[] source = this.workingCopies[0].getSource().toCharArray();
		ASTNode result = runConversion(AST.JLS3, this.workingCopies[0], true, true);
		
		assertASTNodeEquals(
			"package test;\n" + 
			"public class X {\n" + 
			"  void foo(){\n" + 
			"    baz(0);\n" + 
			"    bar(1);\n" + 
			"  }\n" + 
			"}\n",
			result);
		
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0);
		assertNotNull(node);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		Block block = methodDeclaration.getBody();
		List statements = block.statements();
		assertEquals("wrong size", 2, statements.size()); //$NON-NLS-1$
		Statement statement = (Statement) statements.get(0);
		assertTrue("Not an expression statement", statement.getNodeType() == ASTNode.EXPRESSION_STATEMENT); //$NON-NLS-1$
		ExpressionStatement expressionStatement = (ExpressionStatement) statement;
		checkSourceRange(expressionStatement, "baz(0);", source); //$NON-NLS-1$
		Expression expression = expressionStatement.getExpression();
		assertTrue("Not a method invocation", expression.getNodeType() == ASTNode.FUNCTION_INVOCATION); //$NON-NLS-1$
		FunctionInvocation methodInvocation = (FunctionInvocation) expression;
		checkSourceRange(methodInvocation, "baz(0)", source); //$NON-NLS-1$
		List list = methodInvocation.arguments();
		assertTrue("Parameter list is empty", list.size() == 1); //$NON-NLS-1$
		Expression parameter = (Expression) list.get(0);
		assertTrue("Not a number", parameter instanceof NumberLiteral); //$NON-NLS-1$
		ITypeBinding typeBinding = parameter.resolveTypeBinding();
		assertNotNull("No binding", typeBinding); //$NON-NLS-1$
		assertEquals("Not int", "int", typeBinding.getName()); //$NON-NLS-1$ //$NON-NLS-2$
		checkSourceRange(parameter, "0", source); //$NON-NLS-1$
		Statement statement2 = (Statement) statements.get(1);
		assertTrue("Not an expression statement", statement2.getNodeType() == ASTNode.EXPRESSION_STATEMENT); //$NON-NLS-1$
		ExpressionStatement expressionStatement2 = (ExpressionStatement) statement2;
		checkSourceRange(expressionStatement2, "bar(1", source); //$NON-NLS-1$
		Expression expression2 = expressionStatement2.getExpression();
		assertTrue("Not a method invocation", expression2.getNodeType() == ASTNode.FUNCTION_INVOCATION); //$NON-NLS-1$
		FunctionInvocation methodInvocation2 = (FunctionInvocation) expression2;
		checkSourceRange(methodInvocation2, "bar(1", source); //$NON-NLS-1$
		List list2 = methodInvocation2.arguments();
		assertTrue("Parameter list is empty", list2.size() == 1); //$NON-NLS-1$
		Expression parameter2 = (Expression) list2.get(0);
		assertTrue("Not a number", parameter2 instanceof NumberLiteral); //$NON-NLS-1$
		ITypeBinding typeBinding2 = parameter2.resolveTypeBinding();
		assertNotNull("No binding", typeBinding2); //$NON-NLS-1$
		assertEquals("Not int", "int", typeBinding2.getName()); //$NON-NLS-1$ //$NON-NLS-2$
		checkSourceRange(parameter2, "1", source); //$NON-NLS-1$
	}
	
	public void test0003() throws JavaScriptModelException {
		this.workingCopies = new IJavaScriptUnit[1];
		this.workingCopies[0] = getWorkingCopy(
			"/Converter/src/test/X.js",
			"package test;\n"+
			"\n"+
			"public class X {\n"+
			"	void foo() {\n"+
			"	    baz(0);\n"+
			"	    bar(1,\n"+
			"	    foo(3);\n"+
			"	}\n"+
			"}\n");
		
		char[] source = this.workingCopies[0].getSource().toCharArray();
		ASTNode result = runConversion(AST.JLS3, this.workingCopies[0], true, true);
		
		assertASTNodeEquals(
			"package test;\n" + 
			"public class X {\n" + 
			"  void foo(){\n" + 
			"    baz(0);\n" + 
			"    bar(1,foo(3));\n" + 
			"  }\n" + 
			"}\n",
			result);
		
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0);
		assertNotNull(node);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		Block block = methodDeclaration.getBody();
		List statements = block.statements();
		assertEquals("wrong size", 2, statements.size()); //$NON-NLS-1$
		Statement statement = (Statement) statements.get(0);
		assertTrue("Not an expression statement", statement.getNodeType() == ASTNode.EXPRESSION_STATEMENT); //$NON-NLS-1$
		ExpressionStatement expressionStatement = (ExpressionStatement) statement;
		checkSourceRange(expressionStatement, "baz(0);", source); //$NON-NLS-1$
		Expression expression = expressionStatement.getExpression();
		assertTrue("Not a method invocation", expression.getNodeType() == ASTNode.FUNCTION_INVOCATION); //$NON-NLS-1$
		FunctionInvocation methodInvocation = (FunctionInvocation) expression;
		checkSourceRange(methodInvocation, "baz(0)", source); //$NON-NLS-1$
		List list = methodInvocation.arguments();
		assertTrue("Parameter list is empty", list.size() == 1); //$NON-NLS-1$
		Expression parameter = (Expression) list.get(0);
		assertTrue("Not a number", parameter instanceof NumberLiteral); //$NON-NLS-1$
		ITypeBinding typeBinding = parameter.resolveTypeBinding();
		assertNotNull("No binding", typeBinding); //$NON-NLS-1$
		assertEquals("Not int", "int", typeBinding.getName()); //$NON-NLS-1$ //$NON-NLS-2$
		checkSourceRange(parameter, "0", source); //$NON-NLS-1$
		Statement statement2 = (Statement) statements.get(1);
		assertTrue("Not an expression statement", statement2.getNodeType() == ASTNode.EXPRESSION_STATEMENT); //$NON-NLS-1$
		ExpressionStatement expressionStatement2 = (ExpressionStatement) statement2;
		checkSourceRange(expressionStatement2, "bar(1,\n\t    foo(3);", source); //$NON-NLS-1$
		Expression expression2 = expressionStatement2.getExpression();
		assertTrue("Not a method invocation", expression2.getNodeType() == ASTNode.FUNCTION_INVOCATION); //$NON-NLS-1$
		FunctionInvocation methodInvocation2 = (FunctionInvocation) expression2;
		checkSourceRange(methodInvocation2, "bar(1,\n\t    foo(3)", source); //$NON-NLS-1$
		List list2 = methodInvocation2.arguments();
		assertTrue("Parameter list is empty", list2.size() == 2); //$NON-NLS-1$
		Expression parameter2 = (Expression) list2.get(0);
		assertTrue("Not a Number", parameter2 instanceof NumberLiteral); //$NON-NLS-1$
		parameter2 = (Expression) list2.get(1);
		assertTrue("Not a method invocation", parameter2 instanceof FunctionInvocation); //$NON-NLS-1$
		FunctionInvocation methodInvocation3 = (FunctionInvocation) parameter2;
		checkSourceRange(methodInvocation3, "foo(3)", source); //$NON-NLS-1$
	}
	
	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=124296
	public void test0004() throws JavaScriptModelException {
		this.workingCopies = new IJavaScriptUnit[1];
		this.workingCopies[0] = getWorkingCopy(
			"/Converter/src/test/X.js",
			"package test;\n"+
			"\n"+
			"public class X {\n"+
			"	void foo() {\n"+
			"	    int var= 123\n"+
			"	    System.out.println(var);\n"+
			"	}\n"+
			"}\n");
		
		char[] source = this.workingCopies[0].getSource().toCharArray();
		ASTNode result = runConversion(AST.JLS3, this.workingCopies[0], true, true);
		
		assertASTNodeEquals(
			"package test;\n" + 
			"public class X {\n" + 
			"  void foo(){\n" + 
			"    int var=123;\n" + 
			"    System.out.println(var);\n" + 
			"  }\n" + 
			"}\n",
			result);
		
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0);
		assertNotNull(node);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		Block block = methodDeclaration.getBody();
		List statements = block.statements();
		assertEquals("wrong size", 2, statements.size()); //$NON-NLS-1$
		Statement statement1 = (Statement) statements.get(0);
		assertTrue("Not an expression statement", statement1.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT); //$NON-NLS-1$
		VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) statement1;
		checkSourceRange(variableDeclarationStatement, "int var= 123", source); //$NON-NLS-1$
		List fragments = variableDeclarationStatement.fragments();
		assertEquals("wrong size", 1, fragments.size()); //$NON-NLS-1$
		VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment)fragments.get(0);
		checkSourceRange(variableDeclarationFragment, "var= 123", source); //$NON-NLS-1$
	}
	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=126148
	public void test0005() throws JavaScriptModelException {
		this.workingCopies = new IJavaScriptUnit[1];
		this.workingCopies[0] = getWorkingCopy(
			"/Converter/src/test/X.js",
			"package test;\n"+
			"\n"+
			"public class X {\n"+
			"	void foo() {\n"+
			"	    String[] s =  {\"\",,,};\n"+
			"	}\n"+
			"}\n");
		
		char[] source = this.workingCopies[0].getSource().toCharArray();
		ASTNode result = runConversion(AST.JLS3, this.workingCopies[0], true, true);
		
		assertASTNodeEquals(
			"package test;\n" + 
			"public class X {\n" + 
			"  void foo(){\n" + 
			"    String[] s={\"\",$missing$};\n" + 
			"  }\n" + 
			"}\n",
			result);
		
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0);
		assertNotNull(node);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		Block block = methodDeclaration.getBody();
		List statements = block.statements();
		assertEquals("wrong size", 1, statements.size()); //$NON-NLS-1$
		Statement statement1 = (Statement) statements.get(0);
		assertTrue("Not an expression variable declaration statement", statement1.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT); //$NON-NLS-1$
		VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) statement1;
		checkSourceRange(variableDeclarationStatement, "String[] s =  {\"\",,,};", source); //$NON-NLS-1$
		List fragments = variableDeclarationStatement.fragments();
		assertEquals("wrong size", 1, fragments.size()); //$NON-NLS-1$
		VariableDeclarationFragment variableDeclarationFragment = (VariableDeclarationFragment)fragments.get(0);
		checkSourceRange(variableDeclarationFragment, "s =  {\"\",,,}", source); //$NON-NLS-1$
		Expression expression = variableDeclarationFragment.getInitializer();
		assertTrue("Not an array initializer", expression.getNodeType() == ASTNode.ARRAY_INITIALIZER); //$NON-NLS-1$
		ArrayInitializer arrayInitializer = (ArrayInitializer) expression;
		checkSourceRange(arrayInitializer, "{\"\",,,}", source); //$NON-NLS-1$
		List expressions = arrayInitializer.expressions();
		assertEquals("wrong size", 2, expressions.size()); //$NON-NLS-1$
		Expression expression1 = (Expression) expressions.get(0);
		assertTrue("Not a string literal", expression1.getNodeType() == ASTNode.STRING_LITERAL); //$NON-NLS-1$
		StringLiteral stringLiteral = (StringLiteral) expression1;
		checkSourceRange(stringLiteral, "\"\"", source); //$NON-NLS-1$
		Expression expression2 = (Expression) expressions.get(1);
		assertTrue("Not a string literal", expression2.getNodeType() == ASTNode.SIMPLE_NAME); //$NON-NLS-1$
		SimpleName simpleName = (SimpleName) expression2;
		checkSourceRange(simpleName, ",", source); //$NON-NLS-1$
		
	}
		
	// check RECOVERED flag (insert tokens)
	public void test0006() throws JavaScriptModelException {
		this.workingCopies = new IJavaScriptUnit[1];
		this.workingCopies[0] = getWorkingCopy(
			"/Converter/src/test/X.js",
			"package test;\n"+
			"\n"+
			"public class X {\n"+
			"	void foo() {\n"+
			"	    bar()\n"+
			"	}\n"+
			"}\n");
		
		char[] source = this.workingCopies[0].getSource().toCharArray();
		ASTNode result = runConversion(AST.JLS3, this.workingCopies[0], true, true);
		
		assertASTNodeEquals(
			"package test;\n" + 
			"public class X {\n" + 
			"  void foo(){\n" + 
			"    bar();\n" + 
			"  }\n" + 
			"}\n",
			result);
		
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0);
		assertNotNull(node);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		assertTrue("Flag as RECOVERED", (methodDeclaration.getFlags() & ASTNode.RECOVERED) == 0);
		Block block = methodDeclaration.getBody();
		assertTrue("Flag as RECOVERED", (block.getFlags() & ASTNode.RECOVERED) == 0);
		List statements = block.statements();
		assertEquals("wrong size", 1, statements.size()); //$NON-NLS-1$
		Statement statement = (Statement) statements.get(0);
		assertTrue("Not an expression statement", statement.getNodeType() == ASTNode.EXPRESSION_STATEMENT); //$NON-NLS-1$
		ExpressionStatement expressionStatement = (ExpressionStatement) statement;
		checkSourceRange(expressionStatement, "bar()", source); //$NON-NLS-1$
		assertTrue("Not flag as RECOVERED", (expressionStatement.getFlags() & ASTNode.RECOVERED) != 0);
		Expression expression = expressionStatement.getExpression();
		assertTrue("Not a method invocation", expression.getNodeType() == ASTNode.FUNCTION_INVOCATION); //$NON-NLS-1$
		FunctionInvocation methodInvocation = (FunctionInvocation)expression;
		checkSourceRange(methodInvocation, "bar()", source); //$NON-NLS-1$
		assertTrue("Flag as RECOVERED", (methodInvocation.getFlags() & ASTNode.RECOVERED) == 0);
	}
	
	// check RECOVERED flag (insert tokens)
	public void test0007() throws JavaScriptModelException {
		this.workingCopies = new IJavaScriptUnit[1];
		this.workingCopies[0] = getWorkingCopy(
			"/Converter/src/test/X.js",
			"package test;\n"+
			"\n"+
			"public class X {\n"+
			"	void foo() {\n"+
			"	    bar(baz()\n"+
			"	}\n"+
			"}\n");
		
		char[] source = this.workingCopies[0].getSource().toCharArray();
		ASTNode result = runConversion(AST.JLS3, this.workingCopies[0], true, true);
		
		assertASTNodeEquals(
			"package test;\n" + 
			"public class X {\n" + 
			"  void foo(){\n" + 
			"    bar(baz());\n" + 
			"  }\n" + 
			"}\n",
			result);
		
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0);
		assertNotNull(node);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		assertTrue("Flag as RECOVERED", (methodDeclaration.getFlags() & ASTNode.RECOVERED) == 0);
		Block block = methodDeclaration.getBody();
		assertTrue("Flag as RECOVERED", (block.getFlags() & ASTNode.RECOVERED) == 0);
		List statements = block.statements();
		assertEquals("wrong size", 1, statements.size()); //$NON-NLS-1$
		Statement statement = (Statement) statements.get(0);
		assertTrue("Not an expression statement", statement.getNodeType() == ASTNode.EXPRESSION_STATEMENT); //$NON-NLS-1$
		ExpressionStatement expressionStatement = (ExpressionStatement) statement;
		checkSourceRange(expressionStatement, "bar(baz()", source); //$NON-NLS-1$
		assertTrue("Not flag as RECOVERED", (expressionStatement.getFlags() & ASTNode.RECOVERED) != 0);
		Expression expression = expressionStatement.getExpression();
		assertTrue("Not a method invocation", expression.getNodeType() == ASTNode.FUNCTION_INVOCATION); //$NON-NLS-1$
		FunctionInvocation methodInvocation = (FunctionInvocation)expression;
		checkSourceRange(methodInvocation, "bar(baz()", source); //$NON-NLS-1$
		assertTrue("Not flag as RECOVERED", (methodInvocation.getFlags() & ASTNode.RECOVERED) != 0);
		List arguments = methodInvocation.arguments();
		assertEquals("wrong size", 1, arguments.size()); //$NON-NLS-1$
		Expression argument = (Expression) arguments.get(0);
		assertTrue("Not a method invocation", argument.getNodeType() == ASTNode.FUNCTION_INVOCATION); //$NON-NLS-1$
		FunctionInvocation methodInvocation2 = (FunctionInvocation) argument;
		checkSourceRange(methodInvocation2, "baz()", source); //$NON-NLS-1$
		assertTrue("Flag as RECOVERED", (methodInvocation2.getFlags() & ASTNode.RECOVERED) == 0);
	}
	
	// check RECOVERED flag (insert tokens)
	public void test0008() throws JavaScriptModelException {
		this.workingCopies = new IJavaScriptUnit[1];
		this.workingCopies[0] = getWorkingCopy(
			"/Converter/src/test/X.js",
			"package test;\n"+
			"\n"+
			"public class X {\n"+
			"	void foo() {\n"+
			"	    for(int i\n"+
			"	}\n"+
			"}\n");
		
		char[] source = this.workingCopies[0].getSource().toCharArray();
		ASTNode result = runConversion(AST.JLS3, this.workingCopies[0], true, true);
		
		assertASTNodeEquals(
			"package test;\n" + 
			"public class X {\n" + 
			"  void foo(){\n" + 
			"    for (int i; ; )     ;\n" + 
			"  }\n" + 
			"}\n",
			result);
		
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0);
		assertNotNull(node);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		assertTrue("Flag as RECOVERED", (methodDeclaration.getFlags() & ASTNode.RECOVERED) == 0);
		Block block = methodDeclaration.getBody();
		assertTrue("Not flag as RECOVERED", (block.getFlags() & ASTNode.RECOVERED) != 0);
		List statements = block.statements();
		assertEquals("wrong size", 1, statements.size()); //$NON-NLS-1$
		Statement statement = (Statement) statements.get(0);
		assertTrue("Not a for statement", statement.getNodeType() == ASTNode.FOR_STATEMENT); //$NON-NLS-1$
		ForStatement forStatement = (ForStatement) statement;
		checkSourceRange(forStatement, "for(int i", source); //$NON-NLS-1$
		assertTrue("Not flag as RECOVERED", (forStatement.getFlags() & ASTNode.RECOVERED) != 0);
		List initializers = forStatement.initializers();
		assertEquals("wrong size", 1, statements.size()); //$NON-NLS-1$
		Expression expression = (Expression)initializers.get(0);
		assertTrue("Not a method invocation", expression.getNodeType() == ASTNode.VARIABLE_DECLARATION_EXPRESSION); //$NON-NLS-1$
		VariableDeclarationExpression variableDeclarationExpression = (VariableDeclarationExpression)expression;
		checkSourceRange(variableDeclarationExpression, "int i", source); //$NON-NLS-1$
		assertTrue("Not flag as RECOVERED", (variableDeclarationExpression.getFlags() & ASTNode.RECOVERED) != 0);
		List fragments = variableDeclarationExpression.fragments();
		assertEquals("wrong size", 1, fragments.size()); //$NON-NLS-1$
		VariableDeclarationFragment fragment = (VariableDeclarationFragment)fragments.get(0);
		checkSourceRange(fragment, "i", source); //$NON-NLS-1$
		assertTrue("Not flag as RECOVERED", (fragment.getFlags() & ASTNode.RECOVERED) != 0);
		SimpleName name = fragment.getName();
		checkSourceRange(name, "i", source); //$NON-NLS-1$
		assertTrue("Flag as RECOVERED", (name.getFlags() & ASTNode.RECOVERED) == 0);
		Statement statement2 = forStatement.getBody();
		assertTrue("Not an empty statement", statement2.getNodeType() == ASTNode.EMPTY_STATEMENT); //$NON-NLS-1$
		EmptyStatement emptyStatement = (EmptyStatement)statement2;
		checkSourceRange(emptyStatement, "i", source); //$NON-NLS-1$
		assertTrue("Not flag as RECOVERED", (emptyStatement.getFlags() & ASTNode.RECOVERED) != 0);
	}
	
	// check RECOVERED flag (remove tokens)
	public void test0009() throws JavaScriptModelException {
		this.workingCopies = new IJavaScriptUnit[1];
		this.workingCopies[0] = getWorkingCopy(
			"/Converter/src/test/X.js",
			"package test;\n"+
			"\n"+
			"public class X {\n"+
			"	void foo() {\n"+
			"	    bar(baz());#\n"+
			"	}\n"+
			"}\n");
		
		char[] source = this.workingCopies[0].getSource().toCharArray();
		ASTNode result = runConversion(AST.JLS3, this.workingCopies[0], true, true);
		
		assertASTNodeEquals(
			"package test;\n" + 
			"public class X {\n" + 
			"  void foo(){\n" + 
			"    bar(baz());\n" + 
			"  }\n" + 
			"}\n",
			result);
		
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0);
		assertNotNull(node);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		assertTrue("Flag as RECOVERED", (methodDeclaration.getFlags() & ASTNode.RECOVERED) == 0);
		Block block = methodDeclaration.getBody();
		assertTrue("Not flag as RECOVERED", (block.getFlags() & ASTNode.RECOVERED) != 0);
		List statements = block.statements();
		assertEquals("wrong size", 1, statements.size()); //$NON-NLS-1$
		Statement statement = (Statement) statements.get(0);
		assertTrue("Not an expression statement", statement.getNodeType() == ASTNode.EXPRESSION_STATEMENT); //$NON-NLS-1$
		ExpressionStatement expressionStatement = (ExpressionStatement) statement;
		checkSourceRange(expressionStatement, "bar(baz());", source); //$NON-NLS-1$
		assertTrue("Flag as RECOVERED", (expressionStatement.getFlags() & ASTNode.RECOVERED) == 0);
		Expression expression = expressionStatement.getExpression();
		assertTrue("Not a method invocation", expression.getNodeType() == ASTNode.FUNCTION_INVOCATION); //$NON-NLS-1$
		FunctionInvocation methodInvocation = (FunctionInvocation)expression;
		checkSourceRange(methodInvocation, "bar(baz())", source); //$NON-NLS-1$
		assertTrue("Flag as RECOVERED", (methodInvocation.getFlags() & ASTNode.RECOVERED) == 0);
		List arguments = methodInvocation.arguments();
		assertEquals("wrong size", 1, arguments.size()); //$NON-NLS-1$
		Expression argument = (Expression) arguments.get(0);
		assertTrue("Not a method invocation", argument.getNodeType() == ASTNode.FUNCTION_INVOCATION); //$NON-NLS-1$
		FunctionInvocation methodInvocation2 = (FunctionInvocation) argument;
		checkSourceRange(methodInvocation2, "baz()", source); //$NON-NLS-1$
		assertTrue("Flag as RECOVERED", (methodInvocation2.getFlags() & ASTNode.RECOVERED) == 0);
	}
	
	// check RECOVERED flag (remove tokens)
	public void test0010() throws JavaScriptModelException {
		this.workingCopies = new IJavaScriptUnit[1];
		this.workingCopies[0] = getWorkingCopy(
			"/Converter/src/test/X.js",
			"package test;\n"+
			"\n"+
			"public class X {\n"+
			"	void foo() {\n"+
			"	    bar(baz())#;\n"+
			"	}\n"+
			"}\n");
		
		char[] source = this.workingCopies[0].getSource().toCharArray();
		ASTNode result = runConversion(AST.JLS3, this.workingCopies[0], true, true);
		
		assertASTNodeEquals(
			"package test;\n" + 
			"public class X {\n" + 
			"  void foo(){\n" + 
			"    bar(baz());\n" + 
			"  }\n" + 
			"}\n",
			result);
		
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0);
		assertNotNull(node);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		assertTrue("Flag as RECOVERED", (methodDeclaration.getFlags() & ASTNode.RECOVERED) == 0);
		Block block = methodDeclaration.getBody();
		assertTrue("Flag as RECOVERED", (block.getFlags() & ASTNode.RECOVERED) == 0);
		List statements = block.statements();
		assertEquals("wrong size", 1, statements.size()); //$NON-NLS-1$
		Statement statement = (Statement) statements.get(0);
		assertTrue("Not an expression statement", statement.getNodeType() == ASTNode.EXPRESSION_STATEMENT); //$NON-NLS-1$
		ExpressionStatement expressionStatement = (ExpressionStatement) statement;
		checkSourceRange(expressionStatement, "bar(baz())#;", source); //$NON-NLS-1$
		assertTrue("Not flag as RECOVERED", (expressionStatement.getFlags() & ASTNode.RECOVERED) != 0);
		Expression expression = expressionStatement.getExpression();
		assertTrue("Not a method invocation", expression.getNodeType() == ASTNode.FUNCTION_INVOCATION); //$NON-NLS-1$
		FunctionInvocation methodInvocation = (FunctionInvocation)expression;
		checkSourceRange(methodInvocation, "bar(baz())", source); //$NON-NLS-1$
		assertTrue("Flag as RECOVERED", (methodInvocation.getFlags() & ASTNode.RECOVERED) == 0);
		List arguments = methodInvocation.arguments();
		assertEquals("wrong size", 1, arguments.size()); //$NON-NLS-1$
		Expression argument = (Expression) arguments.get(0);
		assertTrue("Not a method invocation", argument.getNodeType() == ASTNode.FUNCTION_INVOCATION); //$NON-NLS-1$
		FunctionInvocation methodInvocation2 = (FunctionInvocation) argument;
		checkSourceRange(methodInvocation2, "baz()", source); //$NON-NLS-1$
		assertTrue("Flag as RECOVERED", (methodInvocation2.getFlags() & ASTNode.RECOVERED) == 0);
	}
	
	// check RECOVERED flag (remove tokens)
	public void test0011() throws JavaScriptModelException {
		this.workingCopies = new IJavaScriptUnit[1];
		this.workingCopies[0] = getWorkingCopy(
			"/Converter/src/test/X.js",
			"package test;\n"+
			"\n"+
			"public class X {\n"+
			"	void foo() {\n"+
			"	    bar(baz()#);\n"+
			"	}\n"+
			"}\n");
		
		char[] source = this.workingCopies[0].getSource().toCharArray();
		ASTNode result = runConversion(AST.JLS3, this.workingCopies[0], true, true);
		
		assertASTNodeEquals(
			"package test;\n" + 
			"public class X {\n" + 
			"  void foo(){\n" + 
			"    bar(baz());\n" + 
			"  }\n" + 
			"}\n",
			result);
		
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0);
		assertNotNull(node);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		assertTrue("Flag as RECOVERED", (methodDeclaration.getFlags() & ASTNode.RECOVERED) == 0);
		Block block = methodDeclaration.getBody();
		assertTrue("Flag as RECOVERED", (block.getFlags() & ASTNode.RECOVERED) == 0);
		List statements = block.statements();
		assertEquals("wrong size", 1, statements.size()); //$NON-NLS-1$
		Statement statement = (Statement) statements.get(0);
		assertTrue("Not an expression statement", statement.getNodeType() == ASTNode.EXPRESSION_STATEMENT); //$NON-NLS-1$
		ExpressionStatement expressionStatement = (ExpressionStatement) statement;
		checkSourceRange(expressionStatement, "bar(baz()#);", source); //$NON-NLS-1$
		assertTrue("Flag as RECOVERED", (expressionStatement.getFlags() & ASTNode.RECOVERED) == 0);
		Expression expression = expressionStatement.getExpression();
		assertTrue("Not a method invocation", expression.getNodeType() == ASTNode.FUNCTION_INVOCATION); //$NON-NLS-1$
		FunctionInvocation methodInvocation = (FunctionInvocation)expression;
		checkSourceRange(methodInvocation, "bar(baz()#)", source); //$NON-NLS-1$
		assertTrue("Not flag as RECOVERED", (methodInvocation.getFlags() & ASTNode.RECOVERED) != 0);
		List arguments = methodInvocation.arguments();
		assertEquals("wrong size", 1, arguments.size()); //$NON-NLS-1$
		Expression argument = (Expression) arguments.get(0);
		assertTrue("Not a method invocation", argument.getNodeType() == ASTNode.FUNCTION_INVOCATION); //$NON-NLS-1$
		FunctionInvocation methodInvocation2 = (FunctionInvocation) argument;
		checkSourceRange(methodInvocation2, "baz()", source); //$NON-NLS-1$
		assertTrue("Flag as RECOVERED", (methodInvocation2.getFlags() & ASTNode.RECOVERED) == 0);
	}
	
	// check RECOVERED flag (insert tokens)
	public void test0012() throws JavaScriptModelException {
		this.workingCopies = new IJavaScriptUnit[1];
		this.workingCopies[0] = getWorkingCopy(
			"/Converter/src/test/X.js",
			"package test;\n"+
			"\n"+
			"public class X {\n"+
			"	void foo() {\n"+
			"	    bar()#\n"+
			"	}\n"+
			"}\n");
		
		char[] source = this.workingCopies[0].getSource().toCharArray();
		ASTNode result = runConversion(AST.JLS3, this.workingCopies[0], true, true);
		
		assertASTNodeEquals(
			"package test;\n" + 
			"public class X {\n" + 
			"  void foo(){\n" + 
			"    bar();\n" + 
			"  }\n" + 
			"}\n",
			result);
		
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0);
		assertNotNull(node);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		assertTrue("Flag as RECOVERED", (methodDeclaration.getFlags() & ASTNode.RECOVERED) == 0);
		Block block = methodDeclaration.getBody();
		assertTrue("Flag as RECOVERED", (block.getFlags() & ASTNode.RECOVERED) == 0);
		List statements = block.statements();
		assertEquals("wrong size", 1, statements.size()); //$NON-NLS-1$
		Statement statement = (Statement) statements.get(0);
		assertTrue("Not an expression statement", statement.getNodeType() == ASTNode.EXPRESSION_STATEMENT); //$NON-NLS-1$
		ExpressionStatement expressionStatement = (ExpressionStatement) statement;
		checkSourceRange(expressionStatement, "bar()#", source); //$NON-NLS-1$
		assertTrue("Not flag as RECOVERED", (expressionStatement.getFlags() & ASTNode.RECOVERED) != 0);
		Expression expression = expressionStatement.getExpression();
		assertTrue("Not a method invocation", expression.getNodeType() == ASTNode.FUNCTION_INVOCATION); //$NON-NLS-1$
		FunctionInvocation methodInvocation = (FunctionInvocation)expression;
		checkSourceRange(methodInvocation, "bar()", source); //$NON-NLS-1$
		assertTrue("Flag as RECOVERED", (methodInvocation.getFlags() & ASTNode.RECOVERED) == 0);
	}
	
	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=129555
	public void test0013() throws JavaScriptModelException {
		this.workingCopies = new IJavaScriptUnit[1];
		this.workingCopies[0] = getWorkingCopy(
			"/Converter/src/test/X.js",
			"package test;\n"+
			"\n"+
			"public class X {\n"+
			"	void foo() {\n"+
			"	    a[0]\n"+
			"	}\n"+
			"}\n");
		
		char[] source = this.workingCopies[0].getSource().toCharArray();
		ASTNode result = runConversion(AST.JLS3, this.workingCopies[0], true, true);
		
		assertASTNodeEquals(
			"package test;\n" + 
			"public class X {\n" + 
			"  void foo(){\n" + 
			"    a[0]=$missing$;\n" + 
			"  }\n" + 
			"}\n",
			result);
		
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0);
		assertNotNull(node);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		assertTrue("Flag as RECOVERED", (methodDeclaration.getFlags() & ASTNode.RECOVERED) == 0);
		Block block = methodDeclaration.getBody();
		assertTrue("Flag as RECOVERED", (block.getFlags() & ASTNode.RECOVERED) != 0);
		List statements = block.statements();
		assertEquals("wrong size", 1, statements.size()); //$NON-NLS-1$
		Statement statement = (Statement) statements.get(0);
		assertTrue("Not an expression statement", statement.getNodeType() == ASTNode.EXPRESSION_STATEMENT); //$NON-NLS-1$
		ExpressionStatement expressionStatement = (ExpressionStatement) statement;
		checkSourceRange(expressionStatement, "a[0]", source); //$NON-NLS-1$
		assertTrue("Not flag as RECOVERED", (expressionStatement.getFlags() & ASTNode.RECOVERED) != 0);
		Expression expression = expressionStatement.getExpression();
		assertTrue("Not an assigment", expression.getNodeType() == ASTNode.ASSIGNMENT); //$NON-NLS-1$
		Assignment assignment = (Assignment)expression;
		checkSourceRange(assignment, "a[0]", source); //$NON-NLS-1$
		assertTrue("Flag as RECOVERED", (assignment.getFlags() & ASTNode.RECOVERED) != 0);
		Expression rhs = assignment.getRightHandSide();
		assertTrue("Not a simple name", rhs.getNodeType() == ASTNode.SIMPLE_NAME); //$NON-NLS-1$
		SimpleName simpleName = (SimpleName) rhs;
		assertEquals("Not length isn't correct", 0, simpleName.getLength()); //$NON-NLS-1$
	}
	
	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=129909
	public void test0014() throws JavaScriptModelException {
		this.workingCopies = new IJavaScriptUnit[1];
		this.workingCopies[0] = getWorkingCopy(
			"/Converter/src/test/X.js",
			"package test;\n"+
			"\n"+
			"public class X {\n"+
			"	void foo() {\n"+
			"	    int[] = a[0];\n"+
			"	}\n"+
			"}\n");
		
		char[] source = this.workingCopies[0].getSource().toCharArray();
		ASTNode result = runConversion(AST.JLS3, this.workingCopies[0], true, true);
		
		assertASTNodeEquals(
			"package test;\n" + 
			"public class X {\n" + 
			"  void foo(){\n" + 
			"    int[] $missing$=a[0];\n" + 
			"  }\n" + 
			"}\n",
			result);
		
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0);
		assertNotNull(node);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		assertTrue("Flag as RECOVERED", (methodDeclaration.getFlags() & ASTNode.RECOVERED) == 0);
		Block block = methodDeclaration.getBody();
		assertTrue("Flag as RECOVERED", (block.getFlags() & ASTNode.RECOVERED) == 0);
		List statements = block.statements();
		assertEquals("wrong size", 1, statements.size()); //$NON-NLS-1$
		Statement statement = (Statement) statements.get(0);
		assertTrue("Not a variable declaration statement", statement.getNodeType() == ASTNode.VARIABLE_DECLARATION_STATEMENT); //$NON-NLS-1$
		VariableDeclarationStatement variableDeclarationStatement = (VariableDeclarationStatement) statement;
		checkSourceRange(variableDeclarationStatement, "int[] = a[0];", source); //$NON-NLS-1$
		assertTrue("Not flag as RECOVERED", (variableDeclarationStatement.getFlags() & ASTNode.RECOVERED) != 0);
		List fragments = variableDeclarationStatement.fragments();
		VariableDeclarationFragment fragment = (VariableDeclarationFragment) fragments.get(0);
		SimpleName simpleName = fragment.getName();
		assertEquals("Not length isn't correct", 0, simpleName.getLength()); //$NON-NLS-1$
	}
	
	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=143212
	public void test0015() throws JavaScriptModelException {
		this.workingCopies = new IJavaScriptUnit[1];
		this.workingCopies[0] = getWorkingCopy(
			"/Converter/src/test/X.js",
			"package test;\n"+
			"\n"+
			"public class X {\n"+
			"	void foo() {\n"+
			"	    assert 0 == 0 : a[0;\n"+
			"	}\n"+
			"}\n");
		
		ASTNode result = runConversion(AST.JLS3, this.workingCopies[0], true, true);
		
		assertASTNodeEquals(
			"package test;\n" + 
			"public class X {\n" + 
			"  void foo(){\n" + 
			"    assert 0 == 0 : a[0];\n" + 
			"  }\n" + 
			"}\n",
			result);
		
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0);
		assertNotNull(node);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		assertTrue("Flag as RECOVERED", (methodDeclaration.getFlags() & ASTNode.RECOVERED) == 0);
		Block block = methodDeclaration.getBody();
		assertTrue("Flag as RECOVERED", (block.getFlags() & ASTNode.RECOVERED) == 0);
		List statements = block.statements();
		assertEquals("wrong size", 1, statements.size()); //$NON-NLS-1$
	}
	
	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=143212
	public void test0016() throws JavaScriptModelException {
		this.workingCopies = new IJavaScriptUnit[1];
		this.workingCopies[0] = getWorkingCopy(
			"/Converter/src/test/X.js",
			"package test;\n"+
			"\n"+
			"public class X {\n"+
			"	void foo() {\n"+
			"	    assert 0 == 0 : foo(;\n"+
			"	}\n"+
			"}\n");
		
		ASTNode result = runConversion(AST.JLS3, this.workingCopies[0], true, true);
		
		assertASTNodeEquals(
			"package test;\n" + 
			"public class X {\n" + 
			"  void foo(){\n" + 
			"    assert 0 == 0 : foo();\n" + 
			"  }\n" + 
			"}\n",
			result);
		
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0);
		assertNotNull(node);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		assertTrue("Flag as RECOVERED", (methodDeclaration.getFlags() & ASTNode.RECOVERED) == 0);
		Block block = methodDeclaration.getBody();
		assertTrue("Flag as RECOVERED", (block.getFlags() & ASTNode.RECOVERED) == 0);
		List statements = block.statements();
		assertEquals("wrong size", 1, statements.size()); //$NON-NLS-1$
	}
	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=143212
	public void test0017() throws JavaScriptModelException {
		this.workingCopies = new IJavaScriptUnit[1];
		this.workingCopies[0] = getWorkingCopy(
			"/Converter/src/test/X.js",
			"package test;\n"+
			"\n"+
			"public class X {\n"+
			"	void foo() {\n"+
			"	    assert 0 == 0 : (\"aa\";\n"+
			"	}\n"+
			"}\n");
		
		ASTNode result = runConversion(AST.JLS3, this.workingCopies[0], true, true);
		
		assertASTNodeEquals(
			"package test;\n" + 
			"public class X {\n" + 
			"  void foo(){\n" + 
			"    assert 0 == 0 : (\"aa\");\n" + 
			"  }\n" + 
			"}\n",
			result);
		
		ASTNode node = getASTNode((JavaScriptUnit) result, 0, 0);
		assertNotNull(node);
		assertTrue("Not a method declaration", node.getNodeType() == ASTNode.FUNCTION_DECLARATION); //$NON-NLS-1$
		FunctionDeclaration methodDeclaration = (FunctionDeclaration) node;
		assertTrue("Flag as RECOVERED", (methodDeclaration.getFlags() & ASTNode.RECOVERED) == 0);
		Block block = methodDeclaration.getBody();
		assertTrue("Flag as RECOVERED", (block.getFlags() & ASTNode.RECOVERED) == 0);
		List statements = block.statements();
		assertEquals("wrong size", 1, statements.size()); //$NON-NLS-1$
	}
}
