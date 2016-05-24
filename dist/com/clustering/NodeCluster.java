package com.clustering;

import com.renderTree.*;
import com.util.Files;
import com.preprocessing.*;

import java.io.* ;
import java.util.*;
import java.lang.Math.*;

//Este nodo contiene la información representativa de un cluster, como lo es La etiqueta dominate del cluster, el UI dominante del cluster y el tipo de UI, o si este es un grupo
public class NodeCluster implements Serializable{
   String label;       //la etiqueta representativa de este cluster
   String infinito;   //indica si en este cluster hay un textfield o checkbox
   FiniteUI finiteUI; //modela un UI infinito, el cual contine una lista 
                          //del clustering de los valores de esos UI y la cadena representativa que representa a ese conjunto de valores
   
   int indexNode;   //si es distinto de -1, entonces el nodo representativo del cluster es un campo compuesto o un grupo
   

   public NodeCluster(String label, String inf, FiniteUI fin, int index){
      this.label = label;
      infinito = inf;
      finiteUI = fin;
      indexNode = index;
   }

   public int hashCode(){
      return label.hashCode();
   }     
   
   public boolean compareTo(NodeCluster n){
      String val = n.getLabel();
      return label.equals(val);
   } 
   public boolean compareTo(Object obj){
      if(obj instanceof NodeCluster){   
         String val = ((NodeCluster)obj).getLabel();
         return label.equals(val);
      }
      else
      return false;
   } 
   
   public boolean equals(Object obj){
      if(obj instanceof NodeCluster){   
         String val = ((NodeCluster)obj).getLabel();
         return label.equals(val);
      }
      else
      return false;
   } 


   public String getLabel(){
      return label;
   } 	
   public String infinito(){
      return infinito;
   }
   public FiniteUI getFinite(){
      return finiteUI;
   } 	
   
   public int getIndexNode (){
      return indexNode;
   }
   
   public String toString(){
      return " " + label + " " + infinito;
   }
}