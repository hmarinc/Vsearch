package com.clustering;

import com.berico.similarity.*;  
import java.util.LinkedList;
import java.util.Hashtable;
import java.util.Enumeration;
 
public class SelectUI extends Clusterizable{
   public SelectUI(int numForm, String value, int numNode){
      super(numForm,value,numNode); 
                      	                    
   }
   		
   public double compare(Clusterizable element2){//select value vs selec value
      if(element2 instanceof SelectUI){
         return compare((SelectUI)element2);
      }  
      else 
         return 0.0;
   }
   	
   public double compare(SelectUI element2){    
      double valueSim = 0.0;
      SelectUI b = (SelectUI)element2;
      SelectUI a = this;      
   
      String la = a.getLabel().toLowerCase();
      String lb = b.getLabel().toLowerCase();
		ISimilarityCalculator simi = new JaroWinklerSimilarity();
      //ISimilarityCalculator simi = new CosineSimilarity(); 
      valueSim = simi.calculate(la,lb);
      return valueSim;
   }
	
   public String show(){
      String salida = "SelectVal("+ getForm() + "," + getNumNode() + "," + getLabel()+")";
      return salida;
   }



}