package com.clustering;

import com.berico.similarity.*;  
import java.util.LinkedList;
import java.util.Hashtable;
import java.util.Enumeration;
 
public class ValSelect extends Clusterizable{
   public ValSelect(int numForm, String value, int numNode){
      super(numForm,value,numNode); 
                      	                    
   }
   		
   public double compare(Clusterizable element2){//select value vs selec value
      if(element2 instanceof ValSelect){
         return compare((ValSelect)element2);
      }  
      else 
         return 0.0;
   }
   	
   public double compare(ValSelect element2){    
      double valueSim = 0.0;
      ValSelect b = (ValSelect)element2;
      ValSelect a = this;      
   
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

   public String toString(){
      String salida =  "("+ getForm() + "," + getNumNode() + "," + getLabel()+")";
      return salida;
   }


}