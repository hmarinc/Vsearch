package com.preprocessing;

import net.didion.jwnl.*;
import net.didion.jwnl.data.*;
import net.didion.jwnl.dictionary.*;
import java.util.ListIterator;
import java.util.Collections;
import java.util.Comparator;
import java.lang.Math.*;
import java.io.*;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.net.*;
import java.util.Vector;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Hashtable;
import com.clustering.*;




public class ConexionWordNet{
   static int MaxWordLength = 50;
   static Dictionary dic;
   static MorphologicalProcessor morph;
   static boolean IsInitialized = false;  
   static HashMap AllWords = null;
   static LinkedList<String> termsWQI= new LinkedList<String>();
   static Set<String> synonyms; 
   static Hashtable<String,Set<String>> synSets;
   static Hashtable<String,Hashtable<String,Set<String>>> allSynonyms;


   public static void process(LinkedList<Clusterizable> CleanedUIrepository){
      AllWords = new HashMap ();
      allSynonyms=new Hashtable();  
      try
      {
         JWNL.initialize(new FileInputStream("file_properties.xml"));
         dic = Dictionary.getInstance();
         morph = dic.getMorphologicalProcessor();
         IsInitialized = true;
      }
      catch ( Exception e ){
         System.out.println("Error initializing Stemmer: " );
         e.printStackTrace();
      } 
   
   
      StemFullCad(CleanedUIrepository);
   
   }

   private static void Unload (){ 
      dic.close();
      Dictionary.uninstall();
      JWNL.shutdown();
   }
 


   private static void StemFullCad(LinkedList<Clusterizable> CleanedUIrepository){
      System.out.println("Entering to WORDNET...");
      String stemmedword="",token="";
   
      for(Clusterizable ei:CleanedUIrepository){
         String nueva = "";
         String label=ei.getLabel().toLowerCase(); 
         StringTokenizer tokens=new StringTokenizer(label); 
      	
         while(tokens.hasMoreTokens()){
            token=tokens.nextToken();
            stemmedword=StemWordWithWordNet(token);
            if(stemmedword != null){
               if(nueva.equals(""))
                  nueva=stemmedword;
               else
                  nueva=nueva+" "+stemmedword;
            }
            else{
               if(nueva.equals(""))
                  nueva=token;
               else
                  nueva=nueva+" "+token;
            
            }
         	 
         }
         ei.setLabel(nueva);
      	 
      }
   
   //return UIrepository;
   }


/* stems a word with wordnet
 * @param word word to stem
 * @return the stemmed word or null if it was not found in WordNet
 */
   private static String StemWordWithWordNet ( String word ){
      if ( !IsInitialized )
         return word;
      if ( word == null ) 
         return null;
      if ( morph == null ) morph = dic.getMorphologicalProcessor();
   
      IndexWord w;
      try
      {
         w = morph.lookupBaseForm( POS.VERB, word );
         if ( w != null )
            return w.getLemma().toString ();
         w = morph.lookupBaseForm( POS.NOUN, word );
         if ( w != null )
            return w.getLemma().toString();
         w = morph.lookupBaseForm( POS.ADJECTIVE, word );
         if ( w != null )
            return w.getLemma().toString();
         w = morph.lookupBaseForm( POS.ADVERB, word );
         if ( w != null )
            return w.getLemma().toString();
      } 
      catch ( JWNLException e )
      {
      }
      return null;
   }





   private static Set<String> SynonWordWordNet(String word ){
      
      Set<String> synonyms = new HashSet<String>();
           
      try{
         IndexWord w = dic.getIndexWord(POS.NOUN,word);
      	
         if (w== null)
            return synonyms;
      
         Synset[] synSets = w.getSenses();
         for (Synset synset : synSets)
         {
            System.out.println(w + ": "+synset);
            
            Word[] words = synset.getWords();
            for (Word wordX : words)
            {
               synonyms.add(wordX.getLemma());
            }
            
         }
      }
      catch(Exception e){}
          
      return synonyms;
   
   }
   
	
      
	
   
   public static void main(String[] args) throws Exception {
   
    /*  LinkedList<String> prueba = new LinkedList<String>();
     
      prueba.add("result");
      prueba.add("publication date");
      complex_synonymous(prueba);
   
     */
     
     
      /*ConexionWordNet ps = new ConexionWordNet();
      String wordX="title publication"; 
      
   	
      Set<String> synonyms =ps.SynonWordWordNet(wordX);
   	
      for(String wo: synonyms)	
         System.out.println(wordX+ "  Synonym: " +wo);
           //PruebaStemming ps = new PruebaStemming();
      
   	//Stem a word
      String word = "authors";
         	 
      //PruebaStemming ps = new PruebaStemming();
      LinkedList<String> keySearch = new LinkedList<String>();
      keySearch.add("authors");
      keySearch.add("children");
      keySearch.add("titles");
      keySearch.add("publication");
      keySearch.add("published");
      keySearch.add("alibris id");
   	  
      String s = ps.StemWordWithWordNet(word);	
      System.out.println("Returned: " + s);
   
    //Stem words in a vector  
      Vector v = new Vector();
      v.add("addresses");
      v.add("systems");
      v.add("publisher");
   
   
      //List wordx= ps.Stem(keySearch);
   	
               ps.Unload();
   	
    */      	
   }
}
	
	
	
   	

