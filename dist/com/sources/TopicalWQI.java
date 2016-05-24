/*
* @TopicalWQI.java 23/04/2014
*
* Copyrigth (C) 2013 Heidy Marisol Marin-Castro.
*
* Centro de Investigación y de Estudios Avanzados
* del Instituto Politécnico Nacional - Tamaulipas
* hmarin@tamps.cinvestav.mx
*
* This program identify WQI from a set of web pages, belonging to a specific domain.
*/

package com.sources; 
import net.htmlparser.jericho.*;
import java.util.*;
import java.io.*;

import com.util.Files;


import javax.swing.*;

  
public class TopicalWQI{
   
   public static  java.util.List<LinkedList> mainProcess(boolean training, JProgressBar progressBar, JTextArea taskOutput){
   
      String domainInterest = null;
      String mainDir = null;
      
      long connectionTime = 0;
      long formIdentificationTime = 0;
      long WQIIdentificationTime = 0;   
      PrintWriter outFileTime = null;
     
      File file = null;
      String line = null;
      BufferedReader input = null;
      String db = null;
   //1. Verify if a datasource of html forms is available in the settings.txt file 
      try{
         file = new File("settings.txt");
         if(!file.exists()){
            System.out.println("Required file 'settings.txt' not found.");
            return null;
         }
      //open the setting file and look for the path pointing to the list of URls where HTML forms will be extracted from.
         input = new BufferedReader( new FileReader( file ) ); 
         line = null;	
         while (( line = input.readLine()) != null) {        
            if(line.startsWith("#htmlForms_source"))//the next no empty line is the path to the URLs list
               db = input.readLine();
               
            if(line.startsWith("#formsDomain_name"))
               domainInterest = input.readLine();
            
            if(line.startsWith("#main_dir"))
               mainDir = input.readLine();   
               
         }
         
         if(db == null){
            System.out.println("No any valid path found in 'settings.txt'.");
            return null;
         }
         else{
            System.out.println("Path found: '" + db + "'");
            System.out.println("Domain given: '" + domainInterest + "'");
            System.out.println("Working directory: '" + mainDir + "'");
         }
      
      }
      catch(Exception e){
         System.out.println("Error ocurred while reading 'settings.txt'.");
         return null;
      }
      
      //2. Process the list of given URLs
      java.util.List<LinkedList> WQIsVectors = null;
      
      int totalURLs = 0;
      int urlsWithForms = 0;
      int urlsWithOutForms = 0;
      int urlsWithWQIs = 0;
      int urlsWQIsInDomain = 0;
      
      int pageRank =- 1;           
      try{
         if(training){
            file = new File("urls.txt");
            if(file.exists())
               file.delete();
               
            file = new File("characteristic_vector.txt");
            if(file.exists())
               file.delete();
         }
         
         file = new File(domainInterest + "/DOMAINS.txt");
         if(file.exists())
            file.delete();
      
      //Si existe una carpeta de trabajo con el mismo nombre se borra
         File carpeta = new File(mainDir);
         if(!carpeta.exists()){
            try{
               carpeta.mkdir();
            }
            catch(Exception e){
               System.out.println("Folder " + mainDir + " cannot be created.");
               return null;
            }
         }
         else{
            File lista[];
            int num = 0;
            try{
               lista = carpeta.listFiles();
               num = lista.length;
               for(int i = 0; i < num; i++){ 
                  lista[i].delete();
               }
            }
            catch(Exception e){
               System.out.println("Folder " + mainDir + " exists, but its content cannot be deleted.");
               return null;
            }
         }
                  
         int numValidWQI = 0;
         File database = new File(db);
         String sourceUrl= ""; 
         
         //crea el archivo de salida de reporte de tiempo, si existe lo destruye, solo lo crea, mas adelante se abre en modo append para irle agregando los tiempos
         /*
         try{
            outFileTime = new PrintWriter(mainDir + "/timing.txt"); 
            outFileTime.println("URL $ NF $ NFWQI $ TC $ TIF $ TIFWQI ");
            outFileTime.close();    
         }
         catch(Exception e){
         }
           */
                 
         BufferedReader entrada = new BufferedReader( new FileReader( database ) ); 	
         while (( line = entrada.readLine()) != null) { 
            //Identificar la URL raiz  
            if(line.startsWith("<srcurl>")&& line.endsWith("</srcurl>")){
               int indice = line.indexOf('>');
               int indice2 = line.indexOf("</");
               //solo se usa para el pagerank
               sourceUrl = line.substring(indice+1,indice2);
            }
              
            if(line.startsWith("<srcinterface>")&& line.endsWith("</srcinterface>")){
               int indice = line.indexOf('>');
               int indice2 = line.indexOf("</");
               String sourceUrlString = line.substring(indice+1,indice2);
               totalURLs++;
               progressBar.setValue(totalURLs);
               /*Recovers all HTML forms in the current URL */
               System.out.println("\n*******************************************************************************\n");			
               System.out.println("URL "+ totalURLs + ": \""+sourceUrlString+'"');
               taskOutput.append("\nURL "+ totalURLs + ": \""+sourceUrlString+'"');
               taskOutput.setCaretPosition(taskOutput.getDocument().getLength());  
               
               System.out.println("Getting HTML forms list");
                  
               java.util.List<? extends Segment> htmlFormsList = ParserURL.identifyHTMLForms(sourceUrlString);
               
               int numForms = 1;
               
               connectionTime = ParserURL.getTimeConnection();
               formIdentificationTime = ParserURL.getTimeIdentification();
               
               if(htmlFormsList == null)
                  continue;
                  
               if(htmlFormsList.size() == 0){
                  /*
                  try{
                     outFileTime = new PrintWriter(new FileWriter(mainDir + "/timing.txt", true));   
                     outFileTime.println(sourceUrlString + "$0$0$" + (double)connectionTime/1000000000.0 + "$" + (double)formIdentificationTime/1000000000.0 + "$" + 0);
                     outFileTime.close();    
                  }
                  catch(Exception e){
                  }
                  */
                  urlsWithOutForms++;
                  continue;
               }
            
               System.out.println("HTML forms list obtained");
               urlsWithForms++;
               
               String action = null;
                
               long startTime = System.nanoTime(); //inicia la cuenta del tiempo de WQI Indent  
             
               
               StringBuffer characVect = null;
               int numWQIs = 0;
               
               boolean isWQI = false;
               boolean WQIInDomain = false;
               
               for (Segment form : htmlFormsList){
                  if(training)
                     characVect = new StringBuffer();    
                  if(ParserURL.isSearchable(form,characVect)){
                     System.out.println("FORM number " + numForms + " is SEARCHABLE");
                     
                     isWQI = true; //bandera que indica que esta URL al menos tiene una WQI
                     
                     numWQIs++;
                     StartTag tag=(StartTag)form;
                     Element wqi = tag.getElement(); 
                     Segment wqiContent = wqi.getContent();
                     action=tag.getAttributeValue("action");
                     
                   /*Appying heuristic identification of Fields to extract labels asociated with the fields identified
                  with the objetive of determinate the domain of the WQI*/  
                     LinkedList<String> wqiLabels = Heuristics.identifyFields(wqiContent);
                      
                  
                     if(training){  
                        if(wqiLabels.size() !=0 && wqiLabels.size() > 2){
                        
                        //update the dictionary with new terms
                           System.out.println("Label:" + wqiLabels);
                          // setTraining.comparTerms(wqiLabels);
                          
                        //Processing of steamming and synonymous  
                           ConexionWordNet.steamming(wqiLabels);
                        	
                         //Load of Dictionaries of domains , updating of terms of actual domain
                           TermsDictionary.addTerms(wqiLabels); 
                        //Classification of new WQIs
                                                  
                        }
                     	                         
                     }
                     else{//No esta entrenando, por tanto, clasificar el conjunto de etiquetas obtenidas
                        String domainFinal = "";
                        if(wqiLabels.size() != 0){
                        //Processing of steamming and synonymous  
                           System.out.println("Changing to root words.." );
                           ConexionWordNet.steamming(wqiLabels);
                           //Classification of WQIs
                           System.out.println("Classifying this WQI: " + wqiLabels);
                           domainFinal = ClassifyWQIs.computeSimi(wqiLabels,sourceUrlString,numForms,action);
                           System.out.println("Domain found for this WQI: " + domainFinal);
                                                           
                           if ((domainFinal != "") && (domainFinal.equals(domainInterest))){   
                              //Reputation of WQIs  	
                              PageRankService prService = new PageRankService();
                              pageRank = prService.getPR(sourceUrl);
                              System.out.println("Google PageRank for this WQI: " + prService.getPR(sourceUrl));	
                           
                              if( WQIsVectors == null)
                                 WQIsVectors = new LinkedList();
                              
                              //new register to save:
                              String id = domainFinal + "_" + Integer.toString(numValidWQI++);
                              try{
                                 Segment fuente= ParserURL.segSource(form);
                                 File fileSource = new File(mainDir + "/" + id + ".html");
                                 PrintWriter write = new PrintWriter(new FileWriter(fileSource,true));
                                 write.println(fuente);   	
                                 write.close();
                              }
                              catch(Exception e){
                                 System.out.println("An error has occurred while identifying WQIs.");
                                 return null;
                              }
                              
                              WQIInDomain = true;
                              
                              LinkedList list = new LinkedList();    	
                              list.add(sourceUrlString);	
                              list.add(id);
                              list.add(action);
                              list.add(pageRank);  
                              WQIsVectors.add(list);
                              /*                              
                              try{
                                 PrintWriter ap = new PrintWriter(new FileWriter(new File(domainInterest + "/DOMAINS.txt"),true));	
                                 ap.printf("Source:%75s , ID:%10s ,  Action:%50s  PageRank:%d",sourceUrlString, id, action,pageRank);
                                 ap.println("");
                                 ap.close();		
                              }
                              catch(Exception e){
                                 System.out.println("An error has occurred while identifying WQIs.");
                                 return null;
                              }
                              */
                           }
                           else
                              System.out.println("Domain '" + domainFinal + "' no reconocido");   
                        }   
                     }
                  }
                  else
                     System.out.println("FORM number " + numForms + " is NO-SEARCHABLE");   	
                  
                  if(training){
                     //store URL,numForm,vect
                    // store(sourceUrlString,numForms,characVect); 
                  }
                  
                  numForms++;
               }
               
               if(isWQI) urlsWithWQIs++;    
               if(WQIInDomain) urlsWQIsInDomain++;
                              
               //termina de analizar cada formulario
               long endTime = System.nanoTime(); //inicia la cuenta del tiempo de WQI Indent  
               WQIIdentificationTime = endTime - startTime;
               /*   
               try{
                  outFileTime = new PrintWriter(new FileWriter(mainDir + "/timing.txt", true));   
                  outFileTime.println(sourceUrlString + "$" + (numForms - 1) + "$" + numWQIs + "$"+ (double)connectionTime/1000000000.0 + "$" + (double)formIdentificationTime/1000000000.0 + "$" + (double)WQIIdentificationTime/1000000000.0);
                  outFileTime.close();    
               }
               catch(Exception e){
               }
               */
            }
         }
         entrada.close();
         
         if(training){
            //Set of terms is available	
            TermsDictionary.computeSynonyms("");
            TermsDictionary.removeTerms();
            TermsDictionary.weights();
            TermsDictionary.storeDictionary("");
            TermsDictionary.readHash();
            TermsDictionary.printHash();
            TermsDictionary.weights();				
         }
      }
      catch(Exception e){
         System.out.println(e.toString());
         return null;
      }
         
      System.out.println("TOTAL URLs in database: " + totalURLs); 
      System.out.println("TOTAL URLs accessed     : " + ParserURL.urlsAccessed);
   	
      try{
         outFileTime = new PrintWriter(new FileWriter(mainDir + "/timing.txt", true));   
         outFileTime.println("\n\n Total URLs:$" + totalURLs);
         outFileTime.println("URLs with forms:$" + urlsWithForms);
         outFileTime.println("URLs without forms:$" + urlsWithOutForms );
         outFileTime.println("URLs with WQI forms:$" + urlsWithWQIs);
         outFileTime.println("URLs with WQI in Domain:$" + urlsWQIsInDomain);
         
         outFileTime.close();    
      }
      catch(Exception e){
      }
               
      try{
         Files.storeObject(WQIsVectors, mainDir + "/domains.dat", "Domains info"); 
         PrintWriter ap = new PrintWriter(mainDir + "/DOMAINS_VIEW.txt");	
        
         for(LinkedList list:WQIsVectors){
            String sourceUrlString = (String)list.get(0);
            sourceUrlString = sourceUrlString.trim();
            String id = (String)list.get(1);
            String action = (String)list.get(2);
            int pR = (Integer)list.get(3);
            ap.printf("Source:%75s , ID:%10s ,  Action:%50s  PageRank:%d",sourceUrlString, id, action,pR);
            ap.println("");
         }      
         ap.close();
      }
      catch(Exception e){
         System.out.println("An error has occurred while storing DOMAINS.dat.");
         return null;
      }
                         
      return WQIsVectors;
   }
  
   public static  java.util.List<LinkedList> mainProcess(boolean training){
   
      String domainInterest = null;
   
      File file = null;
      String line = null;
      BufferedReader input = null;
      String db = null;
   //1. Verify if a datasource of html forms is available in the settings.txt file 
      try{
         file = new File("settings.txt");
         if(!file.exists()){
            System.out.println("Required file 'settings.txt' not found.");
            return null;
         }
      //open the setting file and look for the path pointing to the list of URls where HTML forms will be extracted from.
         input = new BufferedReader( new FileReader( file ) ); 
         line = null;	
         while (( line = input.readLine()) != null) {        
            if(line.startsWith("#htmlForms_source"))//the next no empty line is the path to the URLs list
               db = input.readLine();
               
            if(line.startsWith("#formsDomain_name"))
               domainInterest = input.readLine();
         }
         
         if(db == null){
            System.out.println("No any valid path found in 'settings.txt'.");
            return null;
         }
         else{
            System.out.println("Path found: '" + db + "'");
            System.out.println("Domain given: '" + domainInterest + "'");
         }
      
      }
      catch(Exception e){
         System.out.println("Error ocurred while reading 'settings.txt'.");
         return null;
      }
      
      //2. Process the list of given URLs
      java.util.List<LinkedList> WQIsVectors = null;
      int totalURLs = 0;
      int pageRank =- 1;           
      try{
         if(training){
            file = new File("urls.txt");
            if(file.exists())
               file.delete();
               
            file = new File("characteristic_vector.txt");
            if(file.exists())
               file.delete();
         }
         
         file = new File(domainInterest + "/DOMAINS.txt");
         if(file.exists())
            file.delete();
      
      //Si existe una carpeta con el mismo nombre del dominio que se está analizando, se elimina su contenido
         File carpeta = new File(domainInterest);
         if(!carpeta.exists()){
            try{
               carpeta.mkdir();
            }
            catch(Exception e){
               System.out.println("Folder " + domainInterest + " cannot be created.");
               return null;
            }
         }
         else{
            File lista[];
            int num = 0;
            try{
               lista = carpeta.listFiles();
               num = lista.length;
               for(int i = 0; i < num; i++){ 
                  lista[i].delete();
               }
            }
            catch(Exception e){
               System.out.println("Folder " + domainInterest + " exists, but its content cannot be deleted.");
               return null;
            }
         }
                  
         int numValidWQI = 0;
         File database = new File(db);
         String sourceUrl= ""; 
         BufferedReader entrada = new BufferedReader( new FileReader( database ) ); 	
         while (( line = entrada.readLine()) != null) { 
            //Identificar la URL raiz  
            if(line.startsWith("<srcurl>")&& line.endsWith("</srcurl>")){
               int indice = line.indexOf('>');
               int indice2 = line.indexOf("</");
               sourceUrl = line.substring(indice+1,indice2);
            }
              
            if(line.startsWith("<srcinterface>")&& line.endsWith("</srcinterface>")){
               int indice = line.indexOf('>');
               int indice2 = line.indexOf("</");
               String sourceUrlString = line.substring(indice+1,indice2);
               totalURLs++;
            
               /*Recovers all HTML forms in the current URL */
               System.out.println("\n*******************************************************************************\n");			
               System.out.println("URL "+ totalURLs + ": \""+sourceUrlString+'"');
            
               System.out.println("Getting HTML forms list");
               java.util.List<? extends Segment> htmlFormsList = ParserURL.identifyHTMLForms(sourceUrlString);
               if(htmlFormsList == null)
                  continue;
                  
               System.out.println("HTML forms list obtained");
               String action = null;
             
               int numForms = 1;
               StringBuffer characVect = null;
               for (Segment form : htmlFormsList){
                  if(training)
                     characVect = new StringBuffer();    
                  if(ParserURL.isSearchable(form,characVect)){
                     System.out.println("FORM number " + numForms + " is SEARCHABLE");
                     StartTag tag=(StartTag)form;
                     Element wqi = tag.getElement(); 
                     Segment wqiContent = wqi.getContent();
                     action=tag.getAttributeValue("action");
                     
                   /*Appying heuristic identification of Fields to extract labels asociated with the fields identified
                  with the objetive of determinate the domain of the WQI*/  
                     LinkedList<String> wqiLabels = Heuristics.identifyFields(wqiContent);
                      
                  
                     if(training){  
                        if(wqiLabels.size() !=0 && wqiLabels.size() > 2){
                        
                        //update the dictionary with new terms
                           System.out.println("Label:" + wqiLabels);
                          // setTraining.comparTerms(wqiLabels);
                          
                        //Processing of steamming and synonymous  
                           ConexionWordNet.steamming(wqiLabels);
                        	
                         //Load of Dictionaries of domains , updating of terms of actual domain
                           TermsDictionary.addTerms(wqiLabels); 
                        //Classification of new WQIs
                                                  
                        }
                     	                         
                     }
                     else{//No esta entrenando, por tanto, clasificar el conjunto de etiquetas obtenidas
                        String domainFinal = "";
                        if(wqiLabels.size() != 0){
                        //Processing of steamming and synonymous  
                           System.out.println("Changing to root words.." );
                           ConexionWordNet.steamming(wqiLabels);
                           //Classification of WQIs
                           System.out.println("Classifying this WQI: " + wqiLabels);
                           domainFinal = ClassifyWQIs.computeSimi(wqiLabels,sourceUrlString,numForms,action);
                           System.out.println("Domain found for this WQI: " + domainFinal);
                                                           
                           if ((domainFinal != "") && (domainFinal.equals(domainInterest))){   
                              //Reputation of WQIs  	
                              PageRankService prService = new PageRankService();
                              pageRank = prService.getPR(sourceUrl);
                              System.out.println("Google PageRank for this WQI: " + prService.getPR(sourceUrl));	
                           
                              if( WQIsVectors == null)
                                 WQIsVectors = new LinkedList();
                              
                              //new register to save:
                              String id = domainFinal + "_" + Integer.toString(numValidWQI++);
                              try{
                                 Segment fuente= ParserURL.segSource(form);
                                 File fileSource = new File(domainInterest + "/" + id + ".html");
                                 PrintWriter write = new PrintWriter(new FileWriter(fileSource,true));
                                 write.println(fuente);   	
                                 write.close();
                              }
                              catch(Exception e){
                                 System.out.println("An error has occurred while identifying WQIs.");
                                 return null;
                              }
                           
                              LinkedList list = new LinkedList();    	
                              list.add(sourceUrlString);	
                              list.add(id);
                              list.add(action);
                              list.add(pageRank);  
                              WQIsVectors.add(list);
                              /*                              
                              try{
                                 PrintWriter ap = new PrintWriter(new FileWriter(new File(domainInterest + "/DOMAINS.txt"),true));	
                                 ap.printf("Source:%75s , ID:%10s ,  Action:%50s  PageRank:%d",sourceUrlString, id, action,pageRank);
                                 ap.println("");
                                 ap.close();		
                              }
                              catch(Exception e){
                                 System.out.println("An error has occurred while identifying WQIs.");
                                 return null;
                              }
                              */
                           }
                           else
                              System.out.println("Domain '" + domainFinal + "' no reconocido");   
                        }   
                     }
                  }
                  else
                     System.out.println("FORM number " + numForms + " is NO-SEARCHABLE");   	
                  
                  if(training){
                     //store URL,numForm,vect
                    // store(sourceUrlString,numForms,characVect); 
                  }
                  
                  numForms++;
               }	
            }
         }
         entrada.close();
         
         if(training){
            //Set of terms is available	
            TermsDictionary.computeSynonyms("");
            TermsDictionary.removeTerms();
            TermsDictionary.weights();
            TermsDictionary.storeDictionary("");
            TermsDictionary.readHash();
            TermsDictionary.printHash();
            TermsDictionary.weights();				
         }
      }
      catch(Exception e){
         System.out.println(e.toString());
         return null;
      }
         
      System.out.println("TOTAL URLs in database: " + totalURLs); 
      System.out.println("TOTAL URLs accessed     : " + ParserURL.urlsAccessed);
   	
      try{
         Files.storeObject(WQIsVectors, domainInterest + "/domains.dat", "Domains info"); 
         PrintWriter ap = new PrintWriter(domainInterest + "/DOMAINS_VIEW.txt");	
        
         for(LinkedList list:WQIsVectors){
            String sourceUrlString = (String)list.get(0);
            sourceUrlString = sourceUrlString.trim();
            String id = (String)list.get(1);
            String action = (String)list.get(2);
            int pR = (Integer)list.get(3);
            ap.printf("Source:%75s , ID:%10s ,  Action:%50s  PageRank:%d",sourceUrlString, id, action,pR);
            ap.println("");
         }      
         ap.close();
      }
      catch(Exception e){
         System.out.println("An error has occurred while storing DOMAINS.dat.");
         return null;
      }
      
      return WQIsVectors;
   } 
	
   public static void store(String url, int numForm, StringBuffer str){
    
      try{
       /**
        ** file to store the urls and number of forms 
        ** prevously  classified as searchables forms(WQIs) 
        **/
        
         File file = new File("urls.txt");
         PrintWriter ap = null;
         ap = new PrintWriter(new FileWriter(file,true));
         ap.println(url + " FORM " + numForm);
         ap.close();
         /**
            * file to store the number of ocurrences of
            * html components(i.e. texbox, radio button, images, etc..) 
            * of each WQI
            * In the same manner this file store 
            * the class assigned(searchable or not-searchable)
            */
      		
      		
         file = new File("characteristic_vector.txt");
         ap = null;
         ap = new PrintWriter(new FileWriter(file,true));	 
         ap.println(str);		 
         ap.close();	
      }
      catch(Exception e){
         System.out.println(e.toString());
      }
   		
   }
   
   public static void main(String[] args) throws Exception {
   
      boolean training = false;        
      java.util.List<LinkedList> WQIsVectors = mainProcess(training);
   }

}