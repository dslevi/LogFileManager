package src.java.dslevi.com.github.logfilemanager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;

public class LogFileManager {
	
	private String fileName = null;
	private FileLock lock;
	private FileOutputStream fos;
	
	public void openLogFile(String fileName) {
		if (this.fileName==null) {
			try {
				fos = new FileOutputStream(new File(fileName), true);
				lock = fos.getChannel().lock();
				this.fileName = fileName;
				
			} catch (IOException e) {
				System.out.println("Could not open file");
			}
		}
	}
	
	public void writeToLogFile(String logLine) {
		try {

			fos.write(logLine.getBytes());
			fos.write("\n".getBytes());
			
		} catch (IOException e) {
			System.out.println("The stream is not open");
		}
	}
	
	public void closeLogFile(String fileName) {
		if (fileName.equals(this.fileName)) {
			try {
				lock.release();
				fos.close();
				this.fileName = null;
			} catch (IOException e) {
				System.out.println("Invalid file.");
			}
		}
	}
	
	public Boolean isLogFileOpen(){
		return fileName != null;
	}
	
	@Override protected void finalize() throws Throwable {
		lock.release();
		fos.close();
		fileName = null;
		fos = null;
		lock = null;
	}
	
	public static void main(String[] args) {
		LogFileManager manager = new LogFileManager();
		LogFileManager manager2 = new LogFileManager();
		String fileName = "testLogFile.txt";
		if (!manager.isLogFileOpen()) {
			manager.openLogFile(fileName);
			manager.writeToLogFile("Manager1 writing to the log");
			manager.closeLogFile(fileName);
		}
		if (!manager2.isLogFileOpen()) {
			manager2.openLogFile(fileName);
			manager2.writeToLogFile("Manager2 writing to the log");
			manager2.closeLogFile(fileName);
		}
	}
}
