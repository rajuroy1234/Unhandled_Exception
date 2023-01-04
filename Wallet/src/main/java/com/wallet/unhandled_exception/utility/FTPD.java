package com.wallet.unhandled_exception.utility;


import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.LoggerFactory;

public class FTPD {
	
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(FTPD.class);  

	
	public static void UploadFile(String json, String path) {
	
		String server = "localhost";		
		int port = 2121;		
		String user = "admin";		
		String pass = "admin";
		
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
}

