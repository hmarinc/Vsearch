package com.integration;

import java.io.* ;
import com.clustering.*;
import com.renderTree.*;
import java.util.*;
import com.util.*;


public class MapaGlobal{
   public static Map<String,String> mapaSymtoLabel;
   public static Map<String,String> mapaLabeltoSym;
   public static Map<String,NodeCluster> mapaLabeltoNodeCluster;

   
   static int idCluster = 97;
   static {
      mapaSymtoLabel = new TreeMap<String,String>();   
      mapaLabeltoSym = new TreeMap<String,String>();  
      mapaLabeltoNodeCluster = new HashMap<String,NodeCluster>();  
   }

   public static void storeStatus(String ruta){
      Files.storeObject(mapaSymtoLabel, ruta + "/mapa1.mp", "Map1"); 
      Files.storeObject(mapaLabeltoSym, ruta + "/mapa2.mp", "Map2"); 
      Files.storeObject(mapaLabeltoNodeCluster, ruta + "/mapa3.mp", "Map3");  	
   }
   
   public static boolean restore(String ruta){
      mapaSymtoLabel = (Map<String,String>)Files.readObject(ruta + "/mapa1.mp", "Map1");
      mapaLabeltoSym = (Map<String,String>)Files.readObject(ruta + "/mapa2.mp", "Map2");
      mapaLabeltoNodeCluster = (Map<String,NodeCluster>)Files.readObject(ruta + "/mapa3.mp", "Map3");
      if( mapaSymtoLabel == null || mapaLabeltoSym == null || mapaLabeltoNodeCluster == null)
         return false;
      else 
         return true;
   }
   
   public static void putNodeMap(String str, NodeCluster n){
      mapaLabeltoNodeCluster.put(str,n);
   }
   
   public static NodeCluster getNodeMap(String str){
      return mapaLabeltoNodeCluster.get(str);
   }


   public static String newEntre(String labelCluster){
   
      if(!mapaLabeltoSym.containsKey(labelCluster)){
       //System.out.println("etiqueta: "+s+"  code: "+idCluster);
         String sal=""+(char)idCluster;
         mapaSymtoLabel.put(sal,labelCluster);
         mapaLabeltoSym.put(labelCluster,sal);
         idCluster++;
         return sal;
      }
      else
         return mapaLabeltoSym.get(labelCluster);         
         
   }

   public static String getSymbol(String label){
   //la etiqueta es la llave, el simbolo es el contenido
      return mapaLabeltoSym.get(label);
   }
   public static String getLabel(String symb){
      return mapaSymtoLabel.get(symb);
   }

   public static void main(String []args){
      newEntre("to");
      newEntre("to");
      newEntre("to");
      newEntre("from");
      newEntre("to");
      Collection <String> c = mapaSymtoLabel.values();
      System.out.println(c);
      c = mapaLabeltoSym.values();
      System.out.println(c);
   } 

   public static LinkedList<String> getValues(){
      Collection <String> c = mapaLabeltoSym.values();
      LinkedList<String> list = new LinkedList(c);
   
      Collections.sort(list);
      return list;
   }
   
   public static void show(){
   //Despliegue del mapa global
      
      for(Map.Entry<String,String> entry : mapaSymtoLabel.entrySet()) {
         String key = entry.getKey();
         String value = entry.getValue();
         System.out.println(key + " => " + value);
      }
   }    
}          
