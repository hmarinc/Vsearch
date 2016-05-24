package com.clustering;
import java.io.Serializable;
import java.util.*;

/*
 * Clase generica que permite modelar un nodo que se ingresa al cluster. La informacion
 * de interes del nodo del arbol es su etiqueta, numero de formulario, id que es el id dentro del arbol

*/
public class  FiniteUI implements Serializable{

   TreeMap<String,LinkedList<Clusterizable>> finito;
   String type;
         
   public FiniteUI(String type){
      finito = new TreeMap<String,LinkedList<Clusterizable>>();
      this.type = type;
   }

   public void set(LinkedList<LinkedList<Clusterizable>> clusters){
      for(LinkedList<Clusterizable> cluster : clusters)
         add(cluster);
   }
   public void add(LinkedList<Clusterizable> clusterVals){
   //encuentra la etiqueta más frecuente en este cluster
      if(clusterVals == null) 
         return;
      Map<String, Integer> mpStr = new HashMap<String, Integer>();
      String mostFreq = null;
      int maxCount = -1;
      for(Clusterizable val : clusterVals){
         String label = ((ValSelect)val).getLabel();
         Integer counter = mpStr.get(label);
         if (counter == null){
            counter = 0;   
         }
         counter ++;
         mpStr.put(label,counter);
         if(counter > maxCount){
            mostFreq = label;
            maxCount = counter;
         }
      }
      
      finito.put(mostFreq,clusterVals);
   }
	 
   public Set<String> getValues(){	
      return finito.keySet();
   }
	   	
   public LinkedList<Clusterizable> getCluster(String label){
      return finito.get(label);
   }
	      
   public String getType(){
      return this.type;
   }
      
   public String toString(){
      String salida = "";
   
      salida = salida + "\"" + type + "\"";
   
      Set<Map.Entry<String,LinkedList<Clusterizable>>> entries = finito.entrySet();
      if(entries.size() == 0)
         return salida;
      
      Iterator it = entries.iterator();
      while(it.hasNext()){
         Map.Entry<String,LinkedList<Clusterizable>> entry = (Map.Entry<String,LinkedList<Clusterizable>>)it.next();
      
      salida = salida + "\n\"" + entry.getKey() + "\" " + entry.getValue();
      }
      return salida;
   }
}