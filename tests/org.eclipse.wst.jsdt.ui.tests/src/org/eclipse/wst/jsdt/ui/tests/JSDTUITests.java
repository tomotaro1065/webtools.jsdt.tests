/*******************************************************************************
 * Copyright (c) 2009, 2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.wst.jsdt.ui.tests;

import java.util.ArrayList;
import java.util.Iterator;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.eclipse.wst.jsdt.ui.tests.contentassist.ContentAssistTests;
import org.eclipse.wst.jsdt.ui.tests.format.FormattingTests;

/**
 * @author nitin
 *
 */
public class JSDTUITests extends TestSuite {

	/**
	 * @param name
	 */
	public JSDTUITests(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	static {
		JavaScriptCore.getPlugin().getPluginPreferences().setValue("semanticValidation", true);
	}

	public JSDTUITests() {
		this("JSDT UI Tests");
	}

	public static Test suite() {
		ArrayList standardTests = new ArrayList();
		
		// $JUnit-BEGIN$
		standardTests.add(EditorTests.class);
		// $JUnit-END$

		TestSuite all = new TestSuite("JSDT UI Tests");
		for (Iterator iter = standardTests.iterator(); iter.hasNext();) {
			Class test = (Class) iter.next();
			all.addTestSuite(test); 
		}
		
		all.addTest(ContentAssistTests.suite());
		
		// $JUnit-BEGIN$
		all.addTest(FormattingTests.suite());
		// $JUnit-END$
		
		return all;
	} 
	}