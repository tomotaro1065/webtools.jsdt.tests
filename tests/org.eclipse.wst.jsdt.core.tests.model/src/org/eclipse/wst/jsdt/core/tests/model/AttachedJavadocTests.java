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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Map;

import junit.framework.Test;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.jsdt.core.IClassFile;
import org.eclipse.wst.jsdt.core.IIncludePathAttribute;
import org.eclipse.wst.jsdt.core.IIncludePathEntry;
import org.eclipse.wst.jsdt.core.IJavaScriptUnit;
import org.eclipse.wst.jsdt.core.IField;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.IFunction;
import org.eclipse.wst.jsdt.core.IPackageFragment;
import org.eclipse.wst.jsdt.core.IPackageFragmentRoot;
import org.eclipse.wst.jsdt.core.IType;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.core.JavaScriptModelException;
import org.eclipse.wst.jsdt.core.WorkingCopyOwner;

public class AttachedJavadocTests extends ModifyingResourceTests {
	static {
//		TESTS_NAMES = new String[] { "test010" };
//		TESTS_NUMBERS = new int[] { 20 };
//		TESTS_RANGE = new int[] { 169, 180 };
	}

	public static Test suite() {
		return buildModelTestSuite(AttachedJavadocTests.class);
	}

	private IJavaScriptProject project;
	private IPackageFragmentRoot root;

	public AttachedJavadocTests(String name) {
		super(name);
	}

	/**
	 * Create project and set the jar placeholder.
	 */
	public void setUpSuite() throws Exception {
		super.setUpSuite();

		this.project = setUpJavaProject("AttachedJavadocProject", "1.5"); //$NON-NLS-1$
		Map options = this.project.getOptions(true);
		options.put(JavaScriptCore.TIMEOUT_FOR_PARAMETER_NAME_FROM_ATTACHED_JAVADOC, "2000"); //$NON-NLS-1$
		this.project.setOptions(options);
		IIncludePathEntry[] entries = this.project.getRawIncludepath();
		IResource resource = this.project.getProject().findMember("/doc/"); //$NON-NLS-1$
		assertNotNull("doc folder cannot be null", resource); //$NON-NLS-1$
		URI locationURI = resource.getLocationURI();
		assertNotNull("doc folder cannot be null", locationURI); //$NON-NLS-1$
		URL docUrl = null;
		try {
			docUrl = locationURI.toURL();
		} catch (MalformedURLException e) {
			assertTrue("Should not happen", false); //$NON-NLS-1$
		} catch(IllegalArgumentException e) {
			assertTrue("Should not happen", false); //$NON-NLS-1$
		}
		IIncludePathAttribute attribute = JavaScriptCore.newIncludepathAttribute(IIncludePathAttribute.JSDOC_LOCATION_ATTRIBUTE_NAME, docUrl.toExternalForm());
		for (int i = 0, max = entries.length; i < max; i++) {
			final IIncludePathEntry entry = entries[i];
			if (entry.getEntryKind() == IIncludePathEntry.CPE_LIBRARY
					&& entry.getContentKind() == IPackageFragmentRoot.K_BINARY
					&& "/AttachedJavadocProject/lib/test6.jar".equals(entry.getPath().toString())) { //$NON-NLS-1$
				entries[i] = JavaScriptCore.newLibraryEntry(entry.getPath(), entry.getSourceAttachmentPath(), entry.getSourceAttachmentRootPath(), entry.getAccessRules(), new IIncludePathAttribute[] { attribute}, entry.isExported());
			}
		}
		project.setRawIncludepath(entries, null);

		IPackageFragmentRoot[] roots = this.project.getAllPackageFragmentRoots();
		int count = 0;
		for (int i = 0, max = roots.length; i < max; i++) {
			final IPackageFragmentRoot packageFragmentRoot = roots[i];
			switch(packageFragmentRoot.getKind()) {
				case IPackageFragmentRoot.K_BINARY :
					if (!packageFragmentRoot.isExternal()) {
						count++;
						if (root == null) {
							root = packageFragmentRoot;
						}
					}
			}
		}
		assertEquals("Wrong value", 1, count); //$NON-NLS-1$
		assertNotNull("Should not be null", root); //$NON-NLS-1$
	}

	/**
	 * Reset the jar placeholder and delete project.
	 */
	public void tearDownSuite() throws Exception {
		this.deleteProject("AttachedJavadocProject"); //$NON-NLS-1$
		this.root = null;
		this.project = null;
		super.tearDownSuite();
	}

	// test javadoc for a package fragment
	public void test001() throws JavaScriptModelException {
		IPackageFragment packageFragment = this.root.getPackageFragment("p1/p2"); //$NON-NLS-1$
		assertNotNull("Should not be null", packageFragment); //$NON-NLS-1$
		String javadoc = packageFragment.getAttachedJavadoc(new NullProgressMonitor()); //$NON-NLS-1$
		assertNotNull("Should have a javadoc", javadoc); //$NON-NLS-1$
	}

	// for a class file
	public void test002() throws JavaScriptModelException {
		IPackageFragment packageFragment = this.root.getPackageFragment("p1/p2"); //$NON-NLS-1$
		assertNotNull("Should not be null", packageFragment); //$NON-NLS-1$
		IClassFile classFile = packageFragment.getClassFile("X.class"); //$NON-NLS-1$
		assertNotNull(classFile);
		String javadoc = classFile.getAttachedJavadoc(new NullProgressMonitor()); //$NON-NLS-1$
		assertNotNull("Should have a javadoc", javadoc); //$NON-NLS-1$
	}

	// for a field
	public void test003() throws JavaScriptModelException {
		IPackageFragment packageFragment = this.root.getPackageFragment("p1/p2"); //$NON-NLS-1$
		assertNotNull("Should not be null", packageFragment); //$NON-NLS-1$
		IClassFile classFile = packageFragment.getClassFile("X.class"); //$NON-NLS-1$
		assertNotNull(classFile);
		IType type = classFile.getType();
		IField field = type.getField("f"); //$NON-NLS-1$
		assertNotNull(field);
		String javadoc = field.getAttachedJavadoc(new NullProgressMonitor()); //$NON-NLS-1$
		assertNotNull("Should have a javadoc", javadoc); //$NON-NLS-1$
	}

	// for a method
	public void test004() throws JavaScriptModelException {
		IPackageFragment packageFragment = this.root.getPackageFragment("p1/p2"); //$NON-NLS-1$
		assertNotNull("Should not be null", packageFragment); //$NON-NLS-1$
		IClassFile classFile = packageFragment.getClassFile("X.class"); //$NON-NLS-1$
		assertNotNull(classFile);
		IType type = classFile.getType();
		IFunction method = type.getFunction("foo", new String[] {"I", "J", "Ljava.lang.String;"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		assertTrue(method.exists());
		String javadoc = method.getAttachedJavadoc(new NullProgressMonitor()); //$NON-NLS-1$
		assertNotNull("Should have a javadoc", javadoc); //$NON-NLS-1$
		String[] paramNames = method.getParameterNames();
		assertNotNull(paramNames);
		assertEquals("Wrong size", 3, paramNames.length); //$NON-NLS-1$
		assertEquals("Wrong name for first param", "i", paramNames[0]); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("Wrong name for second param", "l", paramNames[1]); //$NON-NLS-1$ //$NON-NLS-2$
		assertEquals("Wrong name for third param", "s", paramNames[2]); //$NON-NLS-1$ //$NON-NLS-2$
	}

	// for a constructor
	public void test005() throws JavaScriptModelException {
		IPackageFragment packageFragment = this.root.getPackageFragment("p1/p2"); //$NON-NLS-1$
		assertNotNull("Should not be null", packageFragment); //$NON-NLS-1$
		IClassFile classFile = packageFragment.getClassFile("X.class"); //$NON-NLS-1$
		assertNotNull(classFile);
		IType type = classFile.getType();
		IFunction method = type.getFunction("X", new String[] {"I"}); //$NON-NLS-1$ //$NON-NLS-2$
		assertTrue(method.exists());
		String javadoc = method.getAttachedJavadoc(new NullProgressMonitor()); //$NON-NLS-1$
		assertNotNull("Should have a javadoc", javadoc); //$NON-NLS-1$
		String[] paramNames = method.getParameterNames();
		assertNotNull(paramNames);
		assertEquals("Wrong size", 1, paramNames.length); //$NON-NLS-1$
		assertEquals("Wrong name for first param", "i", paramNames[0]);		 //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	// for a member type
	public void test006() throws JavaScriptModelException {
		IPackageFragment packageFragment = this.root.getPackageFragment("p1/p2"); //$NON-NLS-1$
		assertNotNull("Should not be null", packageFragment); //$NON-NLS-1$
		IClassFile classFile = packageFragment.getClassFile("X$A.class"); //$NON-NLS-1$
		assertNotNull(classFile);
		String javadoc = classFile.getAttachedJavadoc(new NullProgressMonitor()); //$NON-NLS-1$
		assertNotNull("Should have a javadoc", javadoc); //$NON-NLS-1$
	}
	
	// for a constructor
	public void test007() throws JavaScriptModelException {
		IPackageFragment packageFragment = this.root.getPackageFragment("p1/p2"); //$NON-NLS-1$
		assertNotNull("Should not be null", packageFragment); //$NON-NLS-1$
		IClassFile classFile = packageFragment.getClassFile("X$A.class"); //$NON-NLS-1$
		assertNotNull(classFile);
		IType type = classFile.getType();
		IFunction method = type.getFunction("A", new String[] {"Lp1.p2.X;", "F"}); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		assertTrue(method.exists());
		String javadoc = method.getAttachedJavadoc(new NullProgressMonitor()); //$NON-NLS-1$
		assertNotNull("Should have a javadoc", javadoc); //$NON-NLS-1$
		String[] paramNames = method.getParameterNames();
		assertNotNull(paramNames);
		assertEquals("Wrong size", 1, paramNames.length); //$NON-NLS-1$
		assertEquals("Wrong name for first param", "f", paramNames[0]); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	// for a method foo2
	public void test008() throws JavaScriptModelException {
		IPackageFragment packageFragment = this.root.getPackageFragment("p1/p2"); //$NON-NLS-1$
		assertNotNull("Should not be null", packageFragment); //$NON-NLS-1$
		IClassFile classFile = packageFragment.getClassFile("X.class"); //$NON-NLS-1$
		assertNotNull(classFile);
		IType type = classFile.getType();
		IFunction method = type.getFunction("foo2", new String[0]); //$NON-NLS-1$
		assertTrue(method.exists());
		String javadoc = method.getAttachedJavadoc(new NullProgressMonitor()); //$NON-NLS-1$
		assertNotNull("Should have a javadoc", javadoc); //$NON-NLS-1$
		String[] paramNames = method.getParameterNames();
		assertNotNull(paramNames);
		assertEquals("Wrong size", 0, paramNames.length); //$NON-NLS-1$
	}
	
	// for a field f2
	public void test009() throws JavaScriptModelException {
		IPackageFragment packageFragment = this.root.getPackageFragment("p1/p2"); //$NON-NLS-1$
		assertNotNull("Should not be null", packageFragment); //$NON-NLS-1$
		IClassFile classFile = packageFragment.getClassFile("X.class"); //$NON-NLS-1$
		assertNotNull(classFile);
		IType type = classFile.getType();
		IField field = type.getField("f2"); //$NON-NLS-1$
		assertNotNull(field);
		String javadoc = field.getAttachedJavadoc(new NullProgressMonitor()); //$NON-NLS-1$
		assertNotNull("Should have a javadoc", javadoc); //$NON-NLS-1$
	}
	
	// test archive doc
	public void test010() throws JavaScriptModelException {
		IIncludePathEntry[] savedEntries = null;
		try {
			IIncludePathEntry[] entries = this.project.getRawIncludepath();
			savedEntries = (IIncludePathEntry[]) entries.clone();
			IResource resource = this.project.getProject().findMember("/doc.zip"); //$NON-NLS-1$
			assertNotNull("doc folder cannot be null", resource); //$NON-NLS-1$
			URI locationURI = resource.getLocationURI();
			assertNotNull("doc folder cannot be null", locationURI); //$NON-NLS-1$
			URL docUrl = null;
			try {
				docUrl = locationURI.toURL();
			} catch (MalformedURLException e) {
				assertTrue("Should not happen", false); //$NON-NLS-1$
			} catch(IllegalArgumentException e) {
				assertTrue("Should not happen", false); //$NON-NLS-1$
			}
			final String path = "jar:" + docUrl.toExternalForm() + "!/doc"; //$NON-NLS-1$ //$NON-NLS-2$
			//final String path = "jar:" + "platform:/resource/AttachedJavadocProject/doc.zip" + "!/doc";
			IIncludePathAttribute attribute = JavaScriptCore.newIncludepathAttribute(IIncludePathAttribute.JSDOC_LOCATION_ATTRIBUTE_NAME, path);
			for (int i = 0, max = entries.length; i < max; i++) {
				final IIncludePathEntry entry = entries[i];
				if (entry.getEntryKind() == IIncludePathEntry.CPE_LIBRARY
						&& entry.getContentKind() == IPackageFragmentRoot.K_BINARY
						&& "/AttachedJavadocProject/lib/test6.jar".equals(entry.getPath().toString())) { //$NON-NLS-1$
					entries[i] = JavaScriptCore.newLibraryEntry(entry.getPath(), entry.getSourceAttachmentPath(), entry.getSourceAttachmentRootPath(), entry.getAccessRules(), new IIncludePathAttribute[] { attribute }, entry.isExported());
				}
			}
			this.project.setRawIncludepath(entries, null);
			IPackageFragment packageFragment = this.root.getPackageFragment("p1/p2"); //$NON-NLS-1$
			assertNotNull("Should not be null", packageFragment); //$NON-NLS-1$
			IClassFile classFile = packageFragment.getClassFile("X.class"); //$NON-NLS-1$
			assertNotNull(classFile);
			IType type = classFile.getType();
			IField field = type.getField("f"); //$NON-NLS-1$
			assertNotNull(field);
			String javadoc = field.getAttachedJavadoc(new NullProgressMonitor()); //$NON-NLS-1$
			assertNotNull("Should have a javadoc", javadoc); //$NON-NLS-1$
		} finally {
			// restore classpath
			if (savedEntries != null) {
				this.project.setRawIncludepath(savedEntries, null);
			}
		}
	}
	
	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=120597
	public void test011() throws JavaScriptModelException {
		IPackageFragment packageFragment = this.root.getPackageFragment("p1/p2"); //$NON-NLS-1$
		assertNotNull("Should not be null", packageFragment); //$NON-NLS-1$
		IClassFile classFile = packageFragment.getClassFile("Z.class"); //$NON-NLS-1$
		assertNotNull(classFile);
		IType type = classFile.getType();
		IField field = type.getField("out"); //$NON-NLS-1$
		assertNotNull(field);
		String javadoc = field.getAttachedJavadoc(new NullProgressMonitor()); //$NON-NLS-1$
		assertNotNull("Should have a javadoc", javadoc); //$NON-NLS-1$
	}
	
	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=120637
	public void test012() throws JavaScriptModelException {
		IPackageFragment packageFragment = this.root.getPackageFragment("p1/p2"); //$NON-NLS-1$
		assertNotNull("Should not be null", packageFragment); //$NON-NLS-1$
		IClassFile classFile = packageFragment.getClassFile("Z.class"); //$NON-NLS-1$
		assertNotNull(classFile);
		String javadoc = classFile.getAttachedJavadoc(new NullProgressMonitor()); //$NON-NLS-1$
		assertNotNull("Should have a javadoc", javadoc); //$NON-NLS-1$
		assertTrue("Should not contain reference to out", javadoc.indexOf("out") == -1);
	}
	
	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=120559
	public void test013() throws JavaScriptModelException {
		IPackageFragment packageFragment = this.root.getPackageFragment("p1/p2"); //$NON-NLS-1$
		assertNotNull("Should not be null", packageFragment); //$NON-NLS-1$
		IClassFile classFile = packageFragment.getClassFile("W.class"); //$NON-NLS-1$
		assertNotNull(classFile);
		String javadoc = classFile.getAttachedJavadoc(new NullProgressMonitor()); //$NON-NLS-1$
		assertNull("Should not have a javadoc", javadoc); //$NON-NLS-1$
	}
	
	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=120637
	public void test014() throws JavaScriptModelException {
		IPackageFragment packageFragment = this.root.getPackageFragment("p1/p2"); //$NON-NLS-1$
		assertNotNull("Should not be null", packageFragment); //$NON-NLS-1$
		IClassFile classFile = packageFragment.getClassFile("E.class"); //$NON-NLS-1$
		assertNotNull(classFile);
		String javadoc = classFile.getAttachedJavadoc(new NullProgressMonitor()); //$NON-NLS-1$
		assertNotNull("Should have a javadoc", javadoc); //$NON-NLS-1$
		assertTrue("Should not contain reference to Constant C", javadoc.indexOf("Constant C") == -1);
	}
	
	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=120637
	public void test015() throws JavaScriptModelException {
		IPackageFragment packageFragment = this.root.getPackageFragment("p1/p2"); //$NON-NLS-1$
		assertNotNull("Should not be null", packageFragment); //$NON-NLS-1$
		IClassFile classFile = packageFragment.getClassFile("Annot.class"); //$NON-NLS-1$
		assertNotNull(classFile);
		String javadoc = classFile.getAttachedJavadoc(new NullProgressMonitor()); //$NON-NLS-1$
		assertNotNull("Should have a javadoc", javadoc); //$NON-NLS-1$
		assertTrue("Should not contain reference to name", javadoc.indexOf("name") == -1);
	}
	
	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=120847
	public void test016() throws JavaScriptModelException {
		IIncludePathEntry[] savedEntries = null;
		try {
			IIncludePathEntry[] entries = this.project.getRawIncludepath();
			savedEntries = (IIncludePathEntry[]) entries.clone();
			IIncludePathAttribute attribute = JavaScriptCore.newIncludepathAttribute(IIncludePathAttribute.JSDOC_LOCATION_ATTRIBUTE_NAME, "invalid_path");
			for (int i = 0, max = entries.length; i < max; i++) {
				final IIncludePathEntry entry = entries[i];
				if (entry.getEntryKind() == IIncludePathEntry.CPE_LIBRARY
						&& entry.getContentKind() == IPackageFragmentRoot.K_BINARY
						&& "/AttachedJavadocProject/lib/test6.jar".equals(entry.getPath().toString())) { //$NON-NLS-1$
					entries[i] = JavaScriptCore.newLibraryEntry(entry.getPath(), entry.getSourceAttachmentPath(), entry.getSourceAttachmentRootPath(), entry.getAccessRules(), new IIncludePathAttribute[] { attribute }, entry.isExported());
				}
			}
			this.project.setRawIncludepath(entries, null);
			IPackageFragment packageFragment = this.root.getPackageFragment("p1/p2"); //$NON-NLS-1$
			assertNotNull("Should not be null", packageFragment); //$NON-NLS-1$
			IClassFile classFile = packageFragment.getClassFile("X.class"); //$NON-NLS-1$
			assertNotNull(classFile);
			IType type = classFile.getType();
			IField field = type.getField("f"); //$NON-NLS-1$
			assertNotNull(field);
			field.getAttachedJavadoc(new NullProgressMonitor()); //$NON-NLS-1$
			assertFalse("Should be unreachable", true);
		} catch(JavaScriptModelException e) {
			assertTrue("Must occur", true);
			assertEquals("Wrong error message", "Cannot retrieve the attached javadoc for invalid_path", e.getMessage());
		} finally {
			// restore classpath
			if (savedEntries != null) {
				this.project.setRawIncludepath(savedEntries, null);
			}
		}
	}
	
	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=120875
	public void test017() throws JavaScriptModelException {
		IPackageFragment packageFragment = this.root.getPackageFragment("p1/p2"); //$NON-NLS-1$
		assertNotNull("Should not be null", packageFragment); //$NON-NLS-1$
		IClassFile classFile = packageFragment.getClassFile("Annot2.class"); //$NON-NLS-1$
		assertNotNull(classFile);
		String javadoc = classFile.getAttachedJavadoc(new NullProgressMonitor()); //$NON-NLS-1$
		assertNotNull("Should have a javadoc", javadoc); //$NON-NLS-1$
		assertTrue("Should not contain reference to name2", javadoc.indexOf("name2") == -1);
	}
	
	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=138167
	public void test018() throws JavaScriptModelException {
		IPackageFragment packageFragment = this.root.getPackageFragment("p1/p2/p3"); //$NON-NLS-1$
		assertNotNull("Should not be null", packageFragment); //$NON-NLS-1$
		IClassFile classFile = packageFragment.getClassFile("C.class"); //$NON-NLS-1$
		assertNotNull(classFile);
		IType type = classFile.getType();
		IFunction[] methods = type.getFunctions();
		NullProgressMonitor monitor = new NullProgressMonitor();
		for (int i = 0, max = methods.length; i < max; i++) {
			IFunction method = methods[i];
			String javadoc = method.getAttachedJavadoc(monitor);
			assertNotNull("Should have a javadoc", javadoc);
			final String selector = method.getElementName();
			assertTrue("Wrong doc", javadoc.indexOf(selector) != -1);
		}
	}
	
	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=138167
	public void test019() throws JavaScriptModelException {
		IPackageFragment packageFragment = this.root.getPackageFragment("p1/p2/p3"); //$NON-NLS-1$
		assertNotNull("Should not be null", packageFragment); //$NON-NLS-1$
		IClassFile classFile = packageFragment.getClassFile("C.class"); //$NON-NLS-1$
		assertNotNull(classFile);
		IType type = classFile.getType();
		IFunction method = type.getFunction("bar5", new String[] {"Ljava.util.Map<TK;TV;>;", "I", "Ljava.util.Map<TK;TV;>;"}); //$NON-NLS-1$
		assertTrue(method.exists());
		String javadoc = method.getAttachedJavadoc(new NullProgressMonitor()); //$NON-NLS-1$
		assertNotNull("Should have a javadoc", javadoc); //$NON-NLS-1$
		String[] names = method.getParameterNames();
		assertNotNull("No names", names);
		assertEquals("Wrong size", 3, names.length);
		assertEquals("Wrong parameter name", "m", names[0]);
		assertEquals("Wrong parameter name", "j", names[1]);
		assertEquals("Wrong parameter name", "m2", names[2]);
	}
	
	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=139160
	public void test020() throws JavaScriptModelException {
		IPackageFragment packageFragment = this.root.getPackageFragment("p1/p2"); //$NON-NLS-1$
		assertNotNull("Should not be null", packageFragment); //$NON-NLS-1$
		IClassFile classFile = packageFragment.getClassFile("Z.class"); //$NON-NLS-1$
		assertNotNull(classFile);
		IType type = classFile.getType();
		IFunction method = type.getFunction("foo", new String[] {"I", "I"}); //$NON-NLS-1$
		assertTrue(method.exists());
		String javadoc = null;
		try {
			javadoc = method.getAttachedJavadoc(new NullProgressMonitor()); //$NON-NLS-1$
		} catch(JavaScriptModelException e) {
			assertTrue("Should not happen", false);
		}
		assertNull("Should not have a javadoc", javadoc); //$NON-NLS-1$
		String[] paramNames = method.getParameterNames();
		assertNotNull(paramNames);
		assertEquals("Wrong size", 2, paramNames.length); //$NON-NLS-1$
		assertEquals("Wrong name", "arg0", paramNames[0]); //$NON-NLS-1$
		assertEquals("Wrong name", "arg1", paramNames[1]); //$NON-NLS-1$
	}
	
	/*
	 * Ensures that calling getAttachedJavadoc(...) on a binary method
	 * has no side-effect on the underlying Java model cache.
	 * (regression test for bug 140879 Spontaneous error "java.util.Set cannot be resolved...")
	 */
	public void test021() throws CoreException, IOException {
		IJavaScriptUnit workingCopy = null;
		try {
			IPackageFragment p = this.root.getPackageFragment("p2");
			IType type = p.getClassFile("X.class").getType();
			IFunction method = type.getFunction("foo", new String[0]);
			
			// the following call should have no side-effect
			method.getAttachedJavadoc(null);
			
			// ensure no side-effect
			ProblemRequestor problemRequestor = new ProblemRequestor();
			workingCopy = getWorkingCopy(
				"/AttachedJavadocProject/src/Test.js", 
				"import p2.Y;\n" +
				"public class Test extends Y { }",
				new WorkingCopyOwner() {},
				problemRequestor
			);
			assertProblems(
				"Unexpected problems", 
				"----------\n" + 
				"----------\n",
				problemRequestor);
		} finally {
			if (workingCopy != null)
				workingCopy.discardWorkingCopy();
			deleteProject("P");
		}
	}

	// https://bugs.eclipse.org/bugs/show_bug.cgi?id=149154
	public void test022() throws JavaScriptModelException {
		IPackageFragment packageFragment = this.root.getPackageFragment("p1/p2"); //$NON-NLS-1$
		assertNotNull("Should not be null", packageFragment); //$NON-NLS-1$
		IClassFile classFile = packageFragment.getClassFile("X.class"); //$NON-NLS-1$
		assertNotNull(classFile);
		IType type = classFile.getType();
		IFunction method = type.getFunction("access$1", new String[] {"Lp1.p2.X;", "I"}); //$NON-NLS-1$
		assertTrue(method.exists());
		String javadoc = null;
		try {
			javadoc = method.getAttachedJavadoc(new NullProgressMonitor()); //$NON-NLS-1$
		} catch(JavaScriptModelException e) {
			assertTrue("Should not happen", false);
		}
		assertNull("Should not have a javadoc", javadoc); //$NON-NLS-1$
		String[] paramNames = method.getParameterNames();
		assertNotNull(paramNames);
		assertEquals("Wrong size", 2, paramNames.length); //$NON-NLS-1$
		assertEquals("Wrong name", "arg0", paramNames[0]); //$NON-NLS-1$
		assertEquals("Wrong name", "arg1", paramNames[1]); //$NON-NLS-1$
	}
}
