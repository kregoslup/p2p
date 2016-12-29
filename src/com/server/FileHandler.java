package com.server;

import java.io.*;

/**
 * Created by krego on 27.12.2016.
 */
class FileHandler {
   private static final String WRITE = "w";
   private static final String READ = "r";
   private int chunkSize;

   FileHandler(int chunkSize){
      this.chunkSize = chunkSize;
   }

   void writeFilePart(String fileName, byte[] filePart, int partNumber){
      RandomAccessFile randomAccessFile;
      try {
         randomAccessFile = new RandomAccessFile(fileName, WRITE);
      } catch (FileNotFoundException e) {
         e.printStackTrace();
         throw new FileWriteError();
      }
      try {
         long offset = calculateOffset(partNumber);
         randomAccessFile.seek(offset);
         randomAccessFile.write(filePart);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   private long calculateOffset(int partNumber) throws IOException{
      return partNumber * chunkSize;
   }

   byte[] loadFilePart(String fileName, int partNumber){
      RandomAccessFile randomAccessFile;
      try {
         randomAccessFile = new RandomAccessFile(fileName, READ);
      } catch (FileNotFoundException e) {
         e.printStackTrace();
          throw new FileWriteError();
      }
      try {
         long offset = calculateOffset(partNumber);
         byte[] filePart = new byte[chunkSize];
         randomAccessFile.seek(offset);
         randomAccessFile.read(filePart);
         return filePart;
      } catch (IOException e) {
         e.printStackTrace();
         throw new FileWriteError();
      }
   }
}
