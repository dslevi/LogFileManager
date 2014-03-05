package test.java.dslevi.com.github.logfilemanager;

import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.channels.OverlappingFileLockException;
import java.util.Scanner;

import src.java.dslevi.com.github.logfilemanager.LogFileManager;

public class LogFileManagerTest {
	private String fileName = "testLogFile.txt";
	private File testFile = new File(fileName);
	
	@Before public void setup() {
		teardown();
	}
	
	@Test public void testOpenLogFile() {
		assertFalse(testFile.exists());
		LogFileManager manager = new LogFileManager();
		manager.openLogFile(fileName);
		assertTrue(testFile.exists());
		teardown();
	}
	
	@Test public void testWriteToLogFile() throws FileNotFoundException {
		String testLogLine = "This is a test log line.";
		LogFileManager manager = new LogFileManager();
		manager.openLogFile(fileName);
		manager.writeToLogFile(testLogLine);
		String logFileContent = new Scanner(testFile).useDelimiter("\n").next();
		assertEquals(testLogLine, logFileContent);
		manager.closeLogFile(fileName);
		teardown();
	}
	
	@Test public void testCloseLogFile() throws FileNotFoundException {
		assertFalse(testFile.exists());
		LogFileManager manager = new LogFileManager();
		manager.openLogFile(fileName);
		assertTrue(testFile.exists());
		manager.closeLogFile(fileName);
		assertFalse(manager.isLogFileOpen());
		teardown();
	}
	
	@Test public void testIsLogFileOpen() {
		LogFileManager manager = new LogFileManager();
		assertFalse(manager.isLogFileOpen());
		manager.openLogFile(fileName);
		assertTrue(manager.isLogFileOpen());
		manager.closeLogFile(fileName);
		assertFalse(manager.isLogFileOpen());
		teardown();
	}
	
	@Test public void testFinalize() {
		
	}
	
	//create one object of class, do openFIle, writetofile, closeFile, is logfileopen ---> false
	@Test public void functionalTest1() {
		LogFileManager manager = new LogFileManager();
		manager.openLogFile(fileName);
		manager.writeToLogFile("This is a test");
		manager.closeLogFile(fileName);
		assertFalse(manager.isLogFileOpen());
		teardown();
	}
	
	//2 objects, ob1 opens/writes, ob2 tries to open ---> gets exception
	@Test(expected=OverlappingFileLockException.class) public void functionalTest2() {
		LogFileManager manager1 = new LogFileManager();
		LogFileManager manager2 = new LogFileManager();
		manager1.openLogFile(fileName);
		manager1.writeToLogFile("Manager1 writing to test log");
		manager2.openLogFile(fileName);
		manager1.closeLogFile(fileName);
		teardown();
	}
	
	//2 objects, ob1 opens/writes, ob2 checks for is open and writes, ob2 writes and gets exception
	@Test(expected=OverlappingFileLockException.class) public void functionalTest3() {
		LogFileManager manager1 = new LogFileManager();
		LogFileManager manager2 = new LogFileManager();
		manager1.openLogFile(fileName);
		manager1.writeToLogFile("Manager1 writing to test log");
		if (!manager2.isLogFileOpen()) {
			manager2.openLogFile(fileName);
		}
		manager2.writeToLogFile("Manager2 writing to test log");
		manager1.closeLogFile(fileName);
		teardown();
	}
	
	@After public void teardown() {
		if (testFile.exists()) testFile.delete();
	}

}
