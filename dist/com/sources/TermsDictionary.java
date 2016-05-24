   package com.sources;

   import java.util.ArrayList;
   import java.util.ListIterator;
   import java.util.LinkedList;
   import java.util.Collections;
   import java.util.Comparator;
   import java.io.*;
   import java.util.*;
   import java.lang.Math.*;
  
	
		
   public class TermsDictionary{
      static ArrayList<Term> dictionary = new ArrayList<Term>();   
      static Hashtable<String,ArrayList> domainsDictionary;
           
   	
      public static void printTerms(String domain, ArrayList<Term> list){
         Integer freq;
         String name;
         double weight;
         Set<String>synonyms;
         System.out.println("**Domain: "+domain);
        //System.out.println("\tTerm name \t\t Freq. \t Weight");
         for(Term term:list){
            name = term.getName();
            freq = term.getFreq();
            weight=term.getWeight();
            synonyms=term.getSynonyms();
            System.out.format(" %20s : %d \t %f", name, freq, weight);
            System.out.println("\t");
            //System.out.println("\t"+synonyms);
         
         } 
         
      	
      	 
      
      }
       	
      public static void printHash(){
      
      
         System.out.println("Reading dictionaries from file.");
         domainsDictionary = TermsDictionary.getDictionaries();
         if(domainsDictionary == null)	{
            System.out.println("!!!!!!!!!UNABLE TO PRINT, DICTIONARIES UNAVAILABLE!!!!!!!!!!!!!");
            return;
         }	
         
         //System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
         //System.out.println("Printing dictionaries");
         //System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^"); 
         
         
         Enumeration domains = domainsDictionary.keys();
         String domain;
         while (domains.hasMoreElements()) {
            domain = (String) domains.nextElement();
            ArrayList<Term> list= domainsDictionary.get(domain);
				//--------Impresion de diccionarios de dominio
            //printTerms(domain, list);
            
         } 	     	
      
      }
   
      public static void addTerms(LinkedList<String> termsInWQI){
       //extraer los terminos de la hastable y comparar contra los nuevos
       //terminos que llegan.
       //Hacer una comparación por similaridad, no una comparación estricta.
       //Para cada termino de entrada se revisa toda la Hashtable
         System.out.println("--------------------------------------------------------------");     
         System.out.println("Updating the dictionary with previous identified labels");     
         for (String newTerm: termsInWQI){
          
            boolean inserted = false;
            int index = 0;
            
            while (index < dictionary.size()) {
               Term element = dictionary.get(index);
               String termStored = element.getName();
              // if(termStored.lastIndexOf(newTerm)>=0 || newTerm.lastIndexOf(termStored) >= 0){
               if(termStored.equals(newTerm)){
                  int counter = element.getFreq();
                  counter++;
                  dictionary.set(index,new Term(termStored,counter));
                  //System.out.println("Updating " + termStored);
                  inserted = true;
                  break;               
               }
              
               index++; 
            }  
            
            if(!inserted){
               dictionary.add(new Term(newTerm,1));	
               //System.out.println("New term added " + newTerm);    
            }
            
         }
      	
      	
                 
      	
         System.out.println("--------------------------------------------------------------");          
      }
   	
   	
   	
      
      public static void weights(){  
      
         Term max=dictionary.get(0);
         int maxFrequen = max.getFreq();
         for(Term number: dictionary)
         {
            if (number.getFreq()>maxFrequen)
               maxFrequen = number.getFreq();
         }
        
         int index = 0;
         while (index < dictionary.size()) {
            Term element = dictionary.get(index);
            String termStored = element.getName();
            int frecuency =element.getFreq();
            double tf1=(double)frecuency/maxFrequen;
            double idf1=(double)Math.log10((double) 10/frecuency);
            double weight1=tf1*idf1;
            if(frecuency!=0){
               double tf2=1+Math.log10((double)1+Math.log10(frecuency));
            	double idf2=(double)Math.log10((double)(1+19)/frecuency);
            	double weight2=tf2*idf2;
            }
            element.setWeight(weight1);   
            dictionary.set(index,element);
            index++; 
         }  
      }
       
        	
      public static void read(String domainName){
      
               
      
      
         dictionary = null;
         try{
            FileInputStream fis = new FileInputStream("Dictionary.dic");
            ObjectInputStream iis = new ObjectInputStream(fis);
         
            System.out.println("Reading the domain --> " + iis.readUTF());
            dictionary = (ArrayList)iis.readObject();
            iis.close();
         }
            catch(Exception e){System.out.println(e.toString());
            }
      
      }
   	
      public static boolean readHash(){
      
         domainsDictionary = null;
         try{
            FileInputStream fis = new FileInputStream("domainsDictionary.dic");
            ObjectInputStream iis = new ObjectInputStream(fis);
         
            domainsDictionary = (Hashtable)iis.readObject();
            iis.close();
            return true;
         }
            catch(Exception e){
               System.out.println(e.toString());
               return false;
            }
      
      }
   
      public static Hashtable<String,ArrayList> getDictionaries(){
         try{
            FileInputStream fis = new FileInputStream("domainsDictionary.dic");
            ObjectInputStream iis = new ObjectInputStream(fis);
         
            Hashtable<String,ArrayList> dictionaries = (Hashtable<String,ArrayList>)iis.readObject();
            iis.close();
            return dictionaries;
         }
            catch(Exception e){
               System.out.println(e.toString());
               return null;
            }
      
      }
          
   	
      public static void storeDictionary(String domainName){
      
         Collections.sort(dictionary, 
               new Comparator(){
               
                  public int compare(Object o1, Object o2) {
                     Term p1 = (Term) o1;
                     Term p2 = (Term) o2;
                     if (p1.getFreq() < p2.getFreq())
                        return 1;
                     else if  (p1.getFreq() > p2.getFreq())
                        return -1;
                     else
                        return 0;
                  }
               });
      
      
      
      //Intentar abrir la hash
         readHash();
         
         if(domainsDictionary == null){
            domainsDictionary = new Hashtable<String,ArrayList>();
         }
         domainsDictionary.put(domainName,dictionary);
      	
      	      	//Escribir la hash ya actualizadaEscritura al archivo 
         try{
            File file=new File ("domainsDictionary.dic");
         
         
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(domainsDictionary);
            oos.close();
         }
            catch(Exception e){System.out.println(e.toString());
            }
      	
      }
   
   
   
      public static  LinkedList<String> getTerms(String domainName){
         LinkedList<String> dictionaryLink= new LinkedList();
      
      
         System.out.println("Loading terms of all WQIs");
         domainsDictionary = TermsDictionary.getDictionaries();
         if(domainsDictionary == null)	{
            System.out.println("Nothing");
            return null;
         }	
            
         ArrayList<Term> list= domainsDictionary.get(domainName);
         String name;
         for(Term element:list)
         {
            name= element.getName();
            System.out.println("Term:"+name);	
         //LinkedList=(LinkedList)domainsDictionary.get(domainName);
            dictionaryLink.add(name);
            
         }
         return dictionaryLink;
      } 	     	
   
   
      
      public static void computeSynonyms(String domain){
         ConexionWordNet ps = new ConexionWordNet();
         System.out.println("Synonymous for " + domain);     
         String newTerm;
         for(Term term:dictionary)
         {	
            newTerm=term.getName();
         
            Set<String> synonyms =ps.SynonWordWordNet(newTerm);
            term.setSynonyms(synonyms);
         }
         ps.Unload();  
       
      }
      
   	
      public static void removeTerms()
      {
      
         System.out.println("--------------------------------------------------------------");     
         System.out.println("Removing terms...."); 
         String newTerm;
         int index=1;
         int index2=0;
         boolean elim=false;
         int freq;
         int actualFreq,freqsal=0;
         Term term;
         while (index2 < dictionary.size()-1) {
            term=dictionary.get(index2);
            index=index2+1;
            newTerm=term.getName();
            actualFreq=term.getFreq();
            while (index < dictionary.size()) {
               Term element = dictionary.get(index);
               freq=element.getFreq();
               Set<String> synonyms =element.getSynonyms();
               elim=false;
              // System.out.println("Comparing " + newTerm + " vs " + element.getName());
               for(String synonym:synonyms){
                 // System.out.println("\t Syn of  " + element.getName() + " is: " + synonym);
               
                  if(newTerm.equals(synonym)){
                     dictionary.remove(index);
                     elim=true;
                     term.setFreq(term.getFreq()+freq);
                     freqsal=term.getFreq();
                     System.out.println("Term: "+newTerm+" Freq:"+actualFreq+"  Term2: "+synonym+ "  freq: "+freq+ "Actual: "+ term.getFreq());
                  
                     break; 
                  }
               }  
               if(!elim)		
                  index++; 
               //System.out.println("Indice:"+index);  
            }
            index2++;
         }    	
         	
      }
       
   
   
   
   
   	/*
                  if(s1.lastIndexOf(s2)>=0 || s2.lastIndexOf(s1) >= 0){
                  boolean removido = terminos.remove(s1);
                  //System.out.println("Removing " + s1 + " usando " + s2);
                  while(removido){ 		      						
                     //System.out.println("Removing " + s1 + " usando " + s2);
                     removido = terminos.remove(s1);
                  }
               }
               else
                  i++;    
            }
   
   
   
   */
   
   	
   	
      public static void main(String args[]){
         String domainName = "Books";
      
         LinkedList<String> termsInWQI = new LinkedList<String>();
         String domain="Books";
         termsInWQI.add("author");
         termsInWQI.add("author");
         termsInWQI.add("depart");
         termsInWQI.add("author");
         termsInWQI.add("author");
         termsInWQI.add("isbn");
         termsInWQI.add("editorial");
         termsInWQI.add("editorial");
         termsInWQI.add("editorials");
         termsInWQI.add("isbn");
         termsInWQI.add("isbn");
         termsInWQI.add("isbn");
         termsInWQI.add("authors");
         termsInWQI.add("prueba");
         termsInWQI.add("au");
         TermsDictionary.addTerms(termsInWQI);
         TermsDictionary.weights();
            
                           	
         TermsDictionary.storeDictionary(domainName);
         TermsDictionary.readHash();
         TermsDictionary.printHash();         
      
      }
   }
	
   class Term implements Serializable{
   
      private String name;
      private int freq;
      private double weight;
      private Set<String> synonyms;
      private boolean remove;
     
      
   
      Term(String name,int freq){
         this.name = name;
         this.freq = freq;
         this.synonyms=null;
      
            
      }
      
      
      
      public double setWeight(double value){
         weight=value;
         return weight;
      }
      public String getName(){
         return name;
      }
   	
      public int getFreq(){
         return freq;
      }
      public void setFreq(int freq){
         this.freq=freq;
      }
      public double getWeight(){
         return weight;
      } 
      public Set<String> getSynonyms(){
         return synonyms;
      }
      public void setSynonyms(Set<String> synonyms){
         this.synonyms=synonyms;
      }
   
   	
   }