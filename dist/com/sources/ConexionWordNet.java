   package com.sources;
   
   import net.didion.jwnl.*;
   import net.didion.jwnl.data.*;
   import net.didion.jwnl.dictionary.*;
   
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



   public class ConexionWordNet{
      private int MaxWordLength = 50;
      public Dictionary dic;
      private MorphologicalProcessor morph;
      private boolean IsInitialized = false;  
      public HashMap AllWords = null;
      static LinkedList<String> termsWQI= new LinkedList<String>();
      static Set<String> synonyms; 
      static Hashtable<String,Set<String>> synSets;
      static Hashtable<String,Hashtable<String,Set<String>>> allSynonyms;
   
      static ArrayList<Term> setSynonim=new ArrayList<Term>();
         /** 
   
    * establishes connection to the WordNet database
    */
    
    
      public ConexionWordNet()
      {
         AllWords = new HashMap ();
         allSynonyms=new Hashtable();  
         try
         {
            JWNL.initialize(new FileInputStream
               ("file_properties.xml"));
            dic = Dictionary.getInstance();
            morph = dic.getMorphologicalProcessor();
         // ((AbstractCachingDictionary)dic).
         //	setCacheCapacity (10000);
            IsInitialized = true;
         }
            catch ( Exception e ){
               System.out.println("Error initializing Stemmer: " );
               e.printStackTrace();
            } 
      
      }
   
      public void Unload ()
      { 
         dic.close();
         Dictionary.uninstall();
         JWNL.shutdown();
      }
      
   	/**
    * Stem a single word
    * tries to look up the word in the AllWords HashMap
    * If the word is not found it is stemmed with WordNet
    * and put into AllWords
    * 
    * @param word word to be stemmed
    * @return stemmed word
    */
      public String Stem( String word )
      {
      // check if we already know the word
         String stemmedword =(String) AllWords.get( word );
         if ( stemmedword != null )
            return stemmedword; // return it if we already know it
      
      // don't check words with digits in them
         //if ( containsNumbers (word) == true )
            //stemmedword = null;
         //else	// unknown word: try to stem it
         stemmedword = StemWordWithWordNet (word);
      
         if ( stemmedword != null )
         {
         // word was recognized and stemmed with wordnet:
         // add it to hashmap and return the stemmed word
            AllWords.put( word, stemmedword );
            return stemmedword;
         }
      // word could not be stemmed by wordnet, 
      // thus it is no correct english word
      // just add it to the list of known words so 
      // we won't have to look it up again
         AllWords.put( word, word );
         return word;
      }
   
   /**
    * performs Stem on each element in the given Vector
    * 
    */
      public List <String> Stem ( List <String> words )
      {
         if ( !IsInitialized )
            return words;
      
         for ( int i = 0; i < words.size(); i++ )
         {
            words.set( i, Stem( (String)words.get( i ) ) );
         }
         return words;		
      }
   
   /* stems a word with wordnet
    * @param word word to stem
    * @return the stemmed word or null if it was not found in WordNet
    */
      public String StemWordWithWordNet ( String word )
      {
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
   
   
   
   
   
      public Set<String> SynonWordWordNet(String word )
      {
         
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
      
   	
      public static void steamming(LinkedList<String> termsInWQI){
       
       //obtiene el steamming o palabra raiz de cada uno de los terminos de la WQI
       // y en algunos terminos verifica si el termino esta contenido como keyword y keywords 
       // donde no es posible obtener su steamming 
       
         System.out.println("--------------------------------------------------------------");     
         System.out.println("Steamming (Root words) of terms in a WQI");     
         ConexionWordNet ps = new ConexionWordNet();
         int i=0;  
         String steamWord="";
         for(String s1:termsInWQI){
            String com="";
            int count=0; 
            StringTokenizer stk=new StringTokenizer(s1," ");        
            while(stk.hasMoreTokens()){
               String token=stk.nextToken();
               steamWord=ps.StemWordWithWordNet(token);
               if(steamWord!=null){	
                  com = com.concat(steamWord);
                  com= com.concat(" ");}
               else{
                  com = com.concat(token);
                  com= com.concat(" ");}
            }
            int lon=com.length();
            String sal=com.substring(0,lon-1);
            
            termsInWQI.set(i,sal);
            i=i+1;          
         }
         ps.Unload();           
      }
   	
      public static void steammingV2(Set<String> syno, LinkedList<String>termsInWQI){
       
       //obtiene el steamming o palabra raiz de cada uno de los terminos de la WQI
       // y en algunos terminos verifica si el termino esta contenido como keyword y keywords 
       // donde no es posible obtener su steamming 
       
         System.out.println("--------------------------------------------------------------");     
         System.out.println("Steamming (Root words) of terms in a WQI");     
         ConexionWordNet ps = new ConexionWordNet();
         int i=0;
         String steamWord="";
         for(String s1:syno){
            steamWord=ps.StemWordWithWordNet(s1);
             //System.out.println("Entro conexion..."+steamWord); 
            termsInWQI.set(i,steamWord);
            i++;
         }
         ps.Unload();           
      }
   	
   	
      public static void synonymous(String domain, LinkedList<String> termsInWQI)
      {
         System.out.println("--------------------------------------------------------------");     
         System.out.println("Synonymous for " + domain);     
         ConexionWordNet ps = new ConexionWordNet();
      	
         synSets = new Hashtable<String,Set<String>>();
         
         for (String newTerm: termsInWQI){
            Set<String> synonyms =ps.SynonWordWordNet(newTerm);
            synSets.put(newTerm,synonyms);
         }
         ps.Unload();  
       
         allSynonyms.put(domain,synSets);
      } 	       	
   	
      public static void printSynomyms(){
      
         
         System.out.println("-----------------  SYNONIMS ----------------------");
      	
         Enumeration synKeys = allSynonyms.keys();
         String domain;
         while(synKeys.hasMoreElements()){
            domain = (String) synKeys.nextElement();
            synSets = allSynonyms.get(domain);
            System.out.println("Domain: "+domain);
         	
            Enumeration termsList = synSets.keys();
            String term;
         	
            while(termsList.hasMoreElements()){
               term = (String) termsList.nextElement();
               Set<String> list= synSets.get(term);
               System.out.println("\tTerm: "+term);
               for(String word:list){
                  System.out.format("\t\t %18s \n", word );
               }
            }
         	             
         }   
      }
      
   	
      public static void complex_synonymous(LinkedList<String> termsInWQI){
      
         System.out.println("--------------------------------------------------------------");     
         System.out.println("Complex Synonymous of terms");     
         ConexionWordNet ps = new ConexionWordNet();
         	
         for(String newTerm:termsInWQI){
            int index = 0;
            StringTokenizer stk=new StringTokenizer(newTerm," ");        
            while(stk.hasMoreTokens()){
               String token=stk.nextToken();
               Set<String> list =ps.SynonWordWordNet(token);
               
               //setSynonim.add(index,new Term(token,list));
            }
            index++; 
         }	
        
            
      
      	
         for(int i=0;i<setSynonim.size();i++)
            System.out.println("Word["+i+"]"+setSynonim.get(i));
      	 		
      }
            
      public static void storesSynomyms(){
      	
      	      	//Escribir la hash ya actualizadaEscritura al archivo 
         try{
            File file=new File ("SynomymsDictionary.dic");
         
         
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(allSynonyms);
            oos.close();
         }
            catch(Exception e){System.out.println(e.toString());
            }
      	
      }
   
      public static boolean readSynomyms(){
      
         
         try{
            FileInputStream fis = new FileInputStream("SynomymsDictionary.dic");
            ObjectInputStream iis = new ObjectInputStream(fis);
         
            allSynonyms = (Hashtable)iis.readObject();
            iis.close();
            return true;
         }
            catch(Exception e){
               System.out.println(e.toString());
               allSynonyms=new Hashtable();
               return false;
            }
      
      }
   	
         
   
   	
      
      public static void main(String[] args) throws Exception {
      
       /*  LinkedList<String> prueba = new LinkedList<String>();
        
         prueba.add("result");
         prueba.add("publication date");
         complex_synonymous(prueba);
      
        */
		  
        //String sino=SynonWordWordNet("author");
		  
        
         ConexionWordNet ps = new ConexionWordNet();
         String wordX="number adult"; 
         
      	
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
         keySearch.add("nearby");
         keySearch.add("include");
         keySearch.add("published");
         keySearch.add("alibris id");
      	  
         String s = ps.StemWordWithWordNet(word);	
         System.out.println("Returned: " + s);
      
       //Stem words in a vector  
         Vector v = new Vector();
         v.add("addresses");
         v.add("systems");
         v.add("publisher");
      
      
         List wordx= ps.Stem(keySearch);
      	
         for(int i = 0; i < wordx.size(); i++)
            System.out.println("wordx("+i+") = " + wordx.get(i));
         
         ps.Unload();
      	
             	
      }
   }
   	
		
		
      	
   
