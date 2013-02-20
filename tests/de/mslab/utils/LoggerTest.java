package de.mslab.utils;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;


public class LoggerTest {
	
	private static Logger logger;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		logger = new Logger();
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		logger = null;
	}
	
	@Test
	public final void testInfoObject() {
		Object foo = 42;
		logger.info("testInfoObject");
		logger.info(foo);
	}
	
	@Test
	public final void testInfoString() {
		logger.info("testInfoString 42");
	}
	
	@Test
	public final void testInfoStringObject() {
		logger.info("testInfoStringObject {0} {0}", 42);
	}
	
	@Test
	public final void testInfoStringObjectObject() {
		logger.info("testInfoStringObjectObject {0} {1} {0} {1}", 42, 43);
	}
	
	@Test
	public final void testInfoStringObjectObjectObject() {
		logger.info("testInfoStringObjectObjectObject {0} {1} {2} {0} {1} {2}", 42, 43, 44);
	}
	
	@Test
	public final void testInfoStringObjectObjectObjectObject() {
		logger.info("testInfoStringObjectObjectObjectObject {0} {1} {2} {3} {0} {1} {2} {3}", 42, 43, 44, 45);
	}
	
	@Test
	public final void testInfoStringObjectArray() {
		logger.info("testInfoStringObjectArray {0} {1} {2} {3} {4} {5} {0} {1} {2} {3} {4} {5}", 
			new Object[]{ 42, 43, 44, 45, 46, 47, 48 });
	}
	
}
