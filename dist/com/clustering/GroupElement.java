package com.clustering;
 
import java.util.*;
import com.berico.similarity.*; 
import com.renderTree.*;
	
public class GroupElement extends Clusterizable{
   
   LinkedList<UIcontent> childs;
	   
   public GroupElement(int numForm, String label, int numNode, LinkedList<UIcontent> childs){
      super(numForm,label,numNode);
      this.childs=childs;      	    	                    
   }
	   
   public LinkedList<UIcontent> getChilds(){
      return childs;      
   }
	  
   public double compare(Clusterizable element2){
   
      if(element2 instanceof UIcontent)
         if(element2 instanceof UIcompuesto)
            return compare((UIcompuesto)element2);
         else 
            return compare((UIcontent)element2);    //UI vs UI
      else if(element2 instanceof GroupElement)  //compuesto vs Grupo
         return compare((GroupElement)element2);
      else 
         return 0;
   }

    
   public double compare(UIcontent b){
   
   //Comparacion entre Elemento de grupo y un campo No compuesto
      LinkedList domain=null;
      LinkedList<UIcontent> UIchilds=null;
      LinkedList valorChilds=null;
   	                      
      double simivalueDom=0.0,simiTotal=0.0; 			
      
      String label1, label2;
      double simi = 0.0;
      double umbral=0.85;
      double umbralDomain=0.65;
      String valorA="";
                  
      //------------------------------------------------------------
     //******Similitud entre etiqueta de grupo y campo no compuesto
     //------------------------------------------------------------
   
      label1=this.getLabel().toLowerCase();
      label2=b.getLabel().toLowerCase();
		ISimilarityCalculator label = new JaroWinklerSimilarity(); 
      //ISimilarityCalculator label = new CosineSimilarity(); 
      simi=label.calculate(label1,label2);
      //System.out.println("Label1: "+label1+" simi: "+simi+"   Compuesto: "+label2);           	
        
                  	
      if (simi>umbral){
      //------------------------------------------------------------
      //******Similitud entre dominios de uis del grupo
      //------------------------------------------------------------
         UIchilds= this.getChilds();   //Obtener uis del grupo
         UIcontent ui1= UIchilds.get(0);
         String tipoui1=ui1.typeDomain();
         String tipoNocomField=b.typeDomain();
         
         for(int j=1;j<UIchilds.size();j++) 
         {
            UIcontent ui= UIchilds.get(j);
            String tipouix=ui.typeDomain();
            
            if((!tipoui1.equals(tipoNocomField))||(!tipouix.equals(tipoNocomField)))
               return 0;
         }
        
         for(int j=1;j<UIchilds.size();j++) 
         {
            UIcontent ui= UIchilds.get(j);
            simivalueDom=ui.simiDomain(b); 
            if(simivalueDom  > simiTotal)
               simiTotal = simivalueDom;
             	
         }
      
      
      	
      }  
   
      return simiTotal;
   }

   //Comparacion de un grupo con un campo compuesto
   public double compare(UIcompuesto b){
   
      LinkedList domain=null;
      LinkedList<UIcontent> UIchilds=null;
      LinkedList valorChilds=null;
   	                      
      double simivalueDom=0.0,simiTotal=0.0; 			
      
      String label1, label2;
      double simi = 0.0;
      double umbral=0.89;
      double umbralDomain=0.65;
      String valorA="";
                  
      int c=0;
   
   
   
      label1=this.getLabel();
      label2=b.getLabel();
		ISimilarityCalculator label = new JaroWinklerSimilarity(); 
      //ISimilarityCalculator label = new CosineSimilarity(); 
      simi=label.calculate(label1,label2);
      //  System.out.println("Label1: "+label1+" simi: "+simi+"   Compuesto: "+label2);           	
                  	
         	 //Si la similitud entre la etiqueta de grupo y el campo compuesto E es mayor que umbral obtener similitud de subdominios de E y las UI del grupo
      if (simi>umbral){
            
      
         domain  =  b.getValue();
         UIchilds= this.getChilds();   
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
            simiTotal=simivalueDom*0.4+simi*0.6;
            
         	
         		
         }
      }
      else
         simiTotal=0.0;
      return simiTotal;	
   }

   public double compare(GroupElement element2){
   
      GroupElement b = element2;
      GroupElement a = this;      
       
   	 
      LinkedList<UIcontent> UIchildsA=null;
      LinkedList<UIcontent> UIchildsB=null;
      String label1, label2;
      LinkedList valorChilds=null;
      LinkedList domain=null;                      
      double simivalueDom=0.0,simiTotal=0.0; 			
      
      
      double simiLabel = 0.0;
      double umbraltem=0.65;
                   
            
     //------------------------------------------------------------
     //******Similitud entre etiquetas de grupo        //------------------------------------------------------------
   
      label1=a.getLabel().toLowerCase();
      label2=b.getLabel().toLowerCase();
		ISimilarityCalculator label = new JaroWinklerSimilarity();
      //ISimilarityCalculator label = new CosineSimilarity(); 
      simiLabel=label.calculate(label1,label2);
     
                  	
      if (simiLabel>umbraltem){
      //------------------------------------------------------------
      //******Similitud entre dominios de uis del grupo
      //------------------------------------------------------------
         UIchildsA= a.getChilds();   //Obtener uis del 1er. grupo
         UIchildsB= b.getChilds();
         int tamaA=UIchildsA.size();
         int tamaB=UIchildsB.size();
                    
         for(int i=0;i<UIchildsA.size();i++) 
         {
         
            for(int j=0;j<UIchildsB.size();j++){
               UIcontent ui1= UIchildsA.get(i);
               UIcontent ui2= UIchildsB.get(j);
             
               simivalueDom=simivalueDom+ui1.simiDomain(ui2); 
            
            
                            	
            }
         
         }
      
         int totalchilds=tamaA*tamaB;
         simivalueDom=(double)simivalueDom/totalchilds;  //Similitud promedio del dominio entre uis de los dos grupos
      	
      }  
     
      simiTotal = simiLabel*0.6+simivalueDom*0.4;
   
      return simiTotal; 
   
   }
	
   
   public String toString(){
   //imprime cada uno de los hijos de este grupo
      String salida = getNumNode() + "-G, " + "\"" + getLabel() + "\"";
      if(childs != null)
         if(childs.size() > 0)
            for(int i = 0; i < childs.size(); i++)
               salida = salida + "\n\t" + childs.get(i);
      return salida;
   }
   
   public String show(){
      String salida = "G("+ getForm() + "," + getNumNode() + "," + getLabel() + ":" + childs.size() + ")";
      return salida;
   }


}