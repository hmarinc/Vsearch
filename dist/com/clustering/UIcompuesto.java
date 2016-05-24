package com.clustering;

import com.berico.similarity.*;  
import java.util.LinkedList;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.*;
 
import com.renderTree.*;
	
public class UIcompuesto extends UIcontent{

   public UIcompuesto(int numForm, int numNode, String type, String label, String name, LinkedList values){
      super(numForm, numNode,type,label, name, values);
   }



   public double compare(Clusterizable element2){
      if(element2 instanceof UIcontent)
         return compare((UIcontent)element2);    //UI vs UI    
      else if(element2 instanceof GroupElement){  //UI vs Grupo
         return compare((GroupElement)element2);}
      else 
         return 0;
   }
   
   public double compare(GroupElement element2){
      UIcontent    a = this; 
      GroupElement b = (GroupElement)element2;
   
      LinkedList domain=null;
      LinkedList<UIcontent> UIchilds=null;
      LinkedList valorChilds=null;
   	                      
      double simivalueDom=0.0,simiTotal=0.0; 			
      
      String label1="", label2="";
      double simi = 0.0;
      double umbral=0.85;
      double umbralDomain=0.65;
      String valorA="";
                  
      int c=0;
   
      label1=a.getLabel();
      label2=b.getLabel();
		ISimilarityCalculator label = new JaroWinklerSimilarity();
      //ISimilarityCalculator label = new CosineSimilarity(); 
      simi=label.calculate(label1,label2);
		//-------------------------------------------------
      //------------------ADICIONAL----------------------
      //Asegurar que se obtiene una similaridad
      if (simi==0){
      ISimilarityCalculator labelx = new CosineSimilarity(); 
      simi=labelx.calculate(label1,label2);

      }
       //------------------------------------------------------- 
      //-------------------------------------------------------  

      if (simi>umbral){
         domain  =  a.getValue();
         UIchilds= b.getChilds();   
         String token="";
               	 
         for (int i = 0; i < domain.size(); i++) {
            String value=(String)domain.get(i);
            StringTokenizer tokens=new StringTokenizer(value,",&&/ "); 
            while(tokens.hasMoreTokens()){
               token=tokens.nextToken();
                     // System.out.println("valor:"+value+"   Tokem:"+token);  
               for (int j = 0; j < UIchilds.size(); j++) {
                  UIcontent ui= UIchilds.get(j);
                  valorChilds=ui.getValue();
                        
                  
                     	
                  for (int m = 0; m < valorChilds.size(); m++) {
                        
                        //System.out.println("*****ValorChilds:"+valorChilds.size());
                     valorA=(String)valorChilds.get(m);
							ISimilarityCalculator valuedomain = new JaroWinklerSimilarity(); 

                    //ISimilarityCalculator valuedomain = new CosineSimilarity(); 
                     double res =valuedomain.calculate(valorA,token);
                        
                            
                     if(res>=umbralDomain){
                        c=c+1;
                                                      
                        break;
                     }
                  }
               }
                  
            }
            int suma=valorChilds.size()+domain.size();
            int nume=2*c;
            simivalueDom=(double)nume/suma;
            //simiDom=simivalueDom*0.4+simiType*0.6;
            simiTotal=simivalueDom*0.2+simi*0.8;
         }
      }
      else
         simiTotal=0.0;
      return simiTotal;	
   }   
   
   public String toString(){   
      //imprime numNode, label, values 
      String salida = getNumNode() + ", " + "\"" + getName()  + "\", \"" + getLabel() + "\"";
   //ahora agrega los valores
      if(valueField != null){
         if(valueField.size() > 0){
            salida = salida + " , \"";
            for(int i = 0; i < valueField.size(); i++){
               String value = (String)valueField.get(i);
               salida = salida + value;
               if((i+1) < valueField.size() )//si hay un valor mas
                  salida = salida + ", ";
            }
            salida = salida + " \"";
         }
      }
      return salida;
   }
   
   public String show(){
      String salida = "C("+ getForm() + "," + getNumNode() + "," + getName() + "," + getLabel() + ")";
      return salida;
   }
	   
}