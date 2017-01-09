package com.server;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by krego on 27.12.2016.
 */
class FileHandler {
   private static final String WRITE = "rw";
   private static final String READ = "r";
   private int chunkSize;

   FileHandler(int chunkSize){
      this.chunkSize = chunkSize;
   }

   FileHandler() {
      chunkSize = RequestConfig.getInstance().getChunkSize();
   }

   void writeFilePart(String fileName, byte[] filePart, long partNumber){
      System.out.print(fileName);
      RandomAccessFile randomAccessFile;
      try {
         randomAccessFile = new RandomAccessFile(fileName, WRITE);
      } catch (FileNotFoundException e) {
         e.printStackTrace();
         throw new FileWriteError("Error creating file");
      }
      try {
         long offset = calculateOffset(partNumber);
         randomAccessFile.seek(offset);
         randomAccessFile.write(filePart);
         randomAccessFile.close();
      } catch (IOException e) {
         e.printStackTrace();
         throw new FileWriteError("Error writing random access file");
      }
   }

   String getFullFileName(String fileName){
      return Paths.get(RequestConfig.getInstance().getDownloadPath().toString(), fileName).toString();
   }

   private long calculateOffset(long partNumber) throws IOException{
      return partNumber * chunkSize;
   }

   byte[] loadFilePart(String fileName, long partNumber){
      RandomAccessFile randomAccessFile;
      try {
         randomAccessFile = new RandomAccessFile(fileName, READ);
      } catch (FileNotFoundException e) {
         e.printStackTrace();
         throw new FileWriteError("Error opening file");
      }
      try {
         long offset = calculateOffset(partNumber);
         randomAccessFile.seek(offset);
         byte[] filePart = new byte[calculatePartLength(fileName, offset, RequestConfig.getInstance().getChunkSize())];
         randomAccessFile.read(filePart);
         randomAccessFile.close();
         return filePart;
      } catch (IOException e) {
         e.printStackTrace();
         throw new FileWriteError("Error reading file");
      }
   }

   private int calculatePartLength(String fileName, long offset, int chunkSize) {
        File file = new File(fileName);
        long fileLength = file.length();
        if (fileLength - offset > chunkSize){
           return chunkSize;
        }
        // Mozna bezpiecznie rzutowac bo chunk size jest intem a ta wartosc jest mniejsza
        return (int)(fileLength - offset);
   }

   long calculateFileParts(File file, long chunkSize){
      long length = file.length();
      return (length / chunkSize) + 1;
   }

   static byte[] countMD5(String file) throws IOException {
      try {
         MessageDigest md = MessageDigest.getInstance("MD5");
         InputStream inputStream = Files.newInputStream(Paths.get(file));
         DigestInputStream dis = new DigestInputStream(inputStream, md);
         byte[] buf = new byte[1024];
         int bytesRead;
         while ((bytesRead = dis.read(buf)) != -1){
            md.update(buf, 0, bytesRead);
         }
         dis.close();
         return md.digest();
      } catch (NoSuchAlgorithmException e) {
         e.printStackTrace();
      }
      return new byte[0];
   }
}
