package com.company;

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

   void writeFile(String fileName, byte[] filePart, int partNumber){
      RandomAccessFile randomAccessFile;
      try {
         randomAccessFile = new RandomAccessFile(fileName, WRITE);
      } catch (FileNotFoundException e) {
         e.printStackTrace();
         throw new FileWriteError();
      }
      try {
         long offset = calculateOffset(fileName, partNumber);
         randomAccessFile.seek(offset);
         randomAccessFile.write(filePart);
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   long calculateOffset(String fileName, int partNumber) throws IOException{
      return partNumber * chunkSize;
   }

   byte[] loadFile(String fileName, int partNumber){
      RandomAccessFile randomAccessFile;
      try {
         randomAccessFile = new RandomAccessFile(fileName, READ);
      } catch (FileNotFoundException e) {
         e.printStackTrace();
          throw new FileWriteError();
      }
      try {
         long offset = calculateOffset(fileName, partNumber);
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
