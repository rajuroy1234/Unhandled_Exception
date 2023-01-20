package com.vc.unhandled_exception.service;

import java.io.IOException;
import com.google.gson.Gson;
import com.vc.unhandled_exception.model.DID_Doc;
import com.vc.unhandled_exception.utility.FTPD;


public class FileEdit {
	
//	public static void cleanUpExistingFiles(String path) {
//	    File targetFile = new File(path);
//	    targetFile.delete();
//	}
//	
//	public static void create(String path, DID_Doc doc) throws IOException
//	{
//		cleanUpExistingFiles(path);
//		FileUtils.touch(new File(path));
//		addAndUploadPublicKey(path, doc);		
//	}
//	
//	public static void upload(MultipartFile file, String path) throws IllegalStateException, IOException
//	{
//		file.transferTo(new File(path));
//		System.out.println("Upload success");
//	}
//	
	public static void uploadFTPD(String path, DID_Doc doc) throws IOException
	{
		 
		Gson gson = new Gson();
		String json = gson.toJson(doc);
        FTPD.uploadFile(json, path);
		
		System.out.println("Added public key to json file");
	}
//	
//	public static void del(String path)
//	{
//		Path path1 = Paths.get(path);
//		try {
//			Files.delete(path1);
//			return "Deleted!";
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return "Can't delete. Check path.";
//	}
}
