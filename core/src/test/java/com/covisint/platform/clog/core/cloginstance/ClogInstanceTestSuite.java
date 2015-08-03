/* Copyright (C) 2014 Covisint. All Rights Reserved. */

package com.covisint.platform.clog.core.cloginstance;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.covisint.platform.clog.core.cloginstance.io.json.ClogInstanceReaderTest;
import com.covisint.platform.clog.core.cloginstance.io.json.ClogInstanceWriterTest;

/**
 * Suite covering all tests for {@link ClogInstance} and Associated Readers and Writers.
 * @since Jul 13, 2015
 *
 */
@RunWith(Suite.class)
@SuiteClasses({ ClogInstanceTest.class, ClogInstanceWriterTest.class, ClogInstanceReaderTest.class })
public class ClogInstanceTestSuite {
}
