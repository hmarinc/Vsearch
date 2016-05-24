package com.util;
import java.io.*;

public class Files{

   public static boolean storeObject(Object object, String path, String description){
      try{
         FileOutputStream fis = new FileOutputStream(path);
         ObjectOutputStream ois = new ObjectOutputStream(fis); 
         ois.writeObject(object);   
         System.out.println(description + " stored in file " + path);
         ois.close();
         return true;
      }
      catch(Exception e) {
         System.out.println(description + " don't stored, an IO error ocurred: " + e.toString());
         return false;
      } 
   }
   
   public static Object readObject(String path, String description){
      try{
         FileInputStream fis = new FileInputStream(path);
         ObjectInputStream ois = new ObjectInputStream(fis); 
         Object obj = ois.readObject();   
         ois.close();
         System.out.println(description + " recovered from file " + path);   
         return obj;   
      }
      catch(Exception e) {
         System.out.println(description + " don't found in file " + path + ". " + e.toString());
         return null;
            
      }  
   }


}