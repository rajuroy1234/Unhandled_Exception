package com.vc.unhandled_exception.utility;


import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.LoggerFactory;

public class FTPD {
	
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FTPD.class);  

	static String server = "localhost";		
	static int port = 2121;		
	static String user = "admin";		
	static String pass = "admin";
	public static void uploadFile(String json, String path) 
	{			
		FTPClient ftpClient = new FTPClient();		
		try {
		
			ftpClient.connect(server, port);			
			ftpClient.login(user, pass);			
			ftpClient.enterLocalPassiveMode();			
			ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
			
			boolean changed;
			changed = ftpClient.changeWorkingDirectory("dids");			
			ftpClient.makeDirectory(path);			
			changed = ftpClient.changeWorkingDirectory(path);
			
			if(changed)			
				System.out.println("CHanged");			
			else			
				System.out.println("NOt changed");
			
			String fileToUploadName = "did.json";			
			System.out.println("Starting to upload file...");
			
			OutputStream outputStream = ftpClient.storeFileStream(fileToUploadName);			
			try (PrintWriter out = new PrintWriter(outputStream)) {			
				out.write(json.toString());			
			} 
			catch (Exception e) {			
				e.printStackTrace();			
			}
			
			outputStream.close();			
			boolean completed = ftpClient.completePendingCommand();			
			if (completed) 			
				System.out.println("File uploaded successfully!");			
			} 
			catch (IOException ex) {			
				System.out.println("Error: " + ex.getMessage());			
				ex.printStackTrace();			
			} 
			finally {
			
				try {				
					if (ftpClient.isConnected()) {				
						ftpClient.logout();				
					ftpClient.disconnect();				
					}				
				} 
				catch (IOException ex) {				
					ex.printStackTrace();
				}
			}
	}
	
	public static void download(String remoteFile)
	{	
	    FTPClient ftpClient = new FTPClient();
	    try {
	
	        ftpClient.connect(server, port);
	        ftpClient.login(user, pass);
	        ftpClient.enterLocalPassiveMode();
	        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
	
	        File downloadFile1 = new File("D:/Downloads/did.json");
	        OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile1));
	        boolean success = ftpClient.retrieveFile(remoteFile, outputStream1);
	        outputStream1.close();
	
	        if (success) {
	            logger.info("Json file fetched successfully!");
	        }
	
	    } catch (IOException ex) {
	        System.out.println("Error: " + ex.getMessage());
	        ex.printStackTrace();
	    } finally {
	        try {
	            if (ftpClient.isConnected()) {
	                ftpClient.logout();
	                ftpClient.disconnect();
	            }
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
	    }
}
}

