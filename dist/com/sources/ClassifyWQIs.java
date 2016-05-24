   package com.sources;
    //package com.temesoft.google.pr;
/*
 *Esta clase se encarga de clasificar nuevas WQIs a un dominio dado
 * a través del diccionario de dominios construido previamente
 * El almacenamiento de los terminos encontrados es a través de 
   una  hashtable, dicha estructura es comparada contra el diccionario 
	dominios y alquel synset de dominio con el mayor peso se considera como el dominio
	de pertencia
 */ 
   import java.util.ArrayList;
   import java.util.ListIterator;
   import java.util.LinkedList;
   import java.util.Collections;
   import java.util.Comparator;
   import java.io.*;
   import java.util.*;
  
	
		
   public class ClassifyWQIs{
      static  Hashtable<String,ArrayList> dictionaries = null;
      static  Hashtable<String,Double> finalSimi = new Hashtable<String,Double>();
      static  int insta_correct, url_noIdentify,insta_incorrect;
           
      
      public static String computeSimi(LinkedList<String> termsInWQI,String sourceUrlString,int numForms, String action)
      {
         //File file = new File("DOMAINS.txt");
         String domainFinal="";
         double similitudMayor = 0.0;
      	
         if(dictionaries == null)
            dictionaries = TermsDictionary.getDictionaries();
         if(dictionaries == null)	{
            System.out.println("!!!!!!!!!UNABLE TO CLASIFY, DICTIONARIES UNAVAILABLE!!!!!!!!!!!!!");
            return null;
         }	
      
         TermsDictionary.printHash();
      
         System.out.println("--------------------------------------------------------------");     
         System.out.println("Classifying the given WQI according to the domain dictionary");     
         
         Enumeration e = dictionaries.keys();
         String domain;
         String name;
         double weight;
        
      
         while (e.hasMoreElements()) {
            //nombre de la key 
            domain = (String)e.nextElement();
            double score = 0.0;
            //elementos de la key
            ArrayList<Term> list= (ArrayList<Term>)dictionaries.get(domain);
            int i=0;   
            for (String newTerm: termsInWQI){         //recorre cada termino del vector de entrada
               for(Term term: list){
                  name=term.getName();
                  weight= term.getWeight();
                  if(name.equals(newTerm)){
                     score+= weight; 
                     break;                
                  }
                  for(String synonym: term.getSynonyms()){
                     if(synonym.equals(newTerm)){
                        score+= weight/2; 
                        break; 
                     }
                  
                  }
                  	
               }
                
            }
            //Normalization of score
            score=score/(double)(termsInWQI.size());
         	
            System.out.println("\t Similarity in the domain \""+domain+"\" is: "+ score);
            
            if((score>similitudMayor)&&(score>=0.1)){
               similitudMayor = score;
               domainFinal = domain;
            }
         	
         }	
         System.out.println("SOURCE:"+sourceUrlString+"\t FORM:"+numForms+"\t DOMAIN: "+domainFinal+"\t SIMILARITY: "+ similitudMayor+"\t ACTION: "+action);
         
         if (domainFinal.equals("Autos"))
            insta_correct=insta_correct+1;
         else
            if(domainFinal.equals(""))
               url_noIdentify=url_noIdentify+1;
               
            else
               insta_incorrect=insta_incorrect+1;
        /* try{
            PrintWriter ap = null;
            ap = new PrintWriter(new FileWriter(file,true));
            if (domainFinal!=""){
               ap.printf("Source:%100s  Form:%d   Domain:%10s   Simi:%f  Action:%50s",sourceUrlString,numForms,domainFinal,similitudMayor,action);
               ap.println("");
               ap.close();
            }
         }
            catch(Exception E2){
               System.out.println(e.toString());
            }*/
         System.out.println("--------------------------------------------------------------");
			return domainFinal;  
      }
      
      public static void main(String args[]){
         LinkedList<String> termsInWQI = new LinkedList<String>();
        
         termsInWQI.add("author");
         termsInWQI.add("isbn");
         termsInWQI.add("price");
         termsInWQI.add("title");
         //carga el diccionario  
        // ClassifyWQIs.computeSimi(termsInWQI);
        
      }
   
   }
