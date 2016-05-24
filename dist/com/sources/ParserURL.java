 /*
* @ParserURL.java 23/04/2014
*
* Copyrigth (C) 2013 Heidy Marisol Marin-Castro.
*
* Centro de Investigación y de Estudios Avanzados
* del Instituto Politécnico Nacional - Tamaulipas
* hmarin@tamps.cinvestav.mx
*
* This program is a helper object to identify WQI from a set of web pages, belonging to a specific domain.
*/


package com.sources; 
  
import net.htmlparser.jericho.*;
import java.util.*;
import java.io.*;
import java.net.*;

public class ParserURL{
   static int urlsParsed = 0;
   static int urlsAccessed = 0;
   static long timeConnection = 0;
   static long timeFormIdentification = 0;
   
/**
 * Proccessing the segments of code delimited by <form> and </form> tags  
 * Receive the segment of code, the url and characteristic vector 
 * Verifying the existence of methods GET and POST in HTML forms
 * @param process the segment
 * @return the ocurrences of the methods GET and POST without missing 
   @the origin url and the number of form 
 */
 
   public static long getTimeConnection(){
      return timeConnection;
   }
 
   public static long getTimeIdentification(){
      return timeFormIdentification;
   }
 
   public static  List<? extends Segment> identifyHTMLForms(String sourceUrlString){
      List<? extends Segment> htmlFormsList = null;           
      timeConnection = 0;
      timeFormIdentification = 0;
      
      if (sourceUrlString.indexOf(':')==-1) 
         sourceUrlString="file:"+sourceUrlString;
      
      MicrosoftTagTypes.register();
      MasonTagTypes.register();
      
      Source source=null;    
      try{
         long startTime = System.nanoTime(); //inicia la cuenta del tiempo
         source = new Source(removeBlankLines(new URL(sourceUrlString)));
         long endTime = System.nanoTime(); //inicia la cuenta del tiempo
          /**
            * Searching of segments of code 
            * delimited by the labels <form..> 
            * and </form> of  HTML code of WQIs
            */     
      		//System.out.println(source);
         timeConnection += endTime - startTime;
         
         startTime = System.nanoTime(); //inicia la cuenta del tiempo   	
         source.getAllTags(); 	 
         htmlFormsList = source.getAllStartTags("form");
         urlsParsed++;     
         endTime = System.nanoTime();
         
         timeFormIdentification += endTime - startTime;
      }
      catch(Exception e){
         System.out.println(e.toString()); 
         //e.printStackTrace();
      }
      return htmlFormsList;  
   }
   
   private static BufferedReader readURL(URL urlcon){ 
      BufferedReader in = null; 
      try { 
         HttpURLConnection myURLConnection =(HttpURLConnection) urlcon.openConnection();
         
         System.setProperty("http.agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; wow64; rv:12.0; Trident/4.0;X11; U; Linux x86_64; en-US; rv:1.9.2.13)"); 
         myURLConnection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1;  wow64; rv:12.0; Trident/4.0;X11; U; Linux x86_64; en-US; rv:1.9.2.13)"); 
      
      
         int status = myURLConnection.getResponseCode();
      
         if (status != HttpURLConnection.HTTP_OK){
            System.out.println("**** NOT CONNECTED *****: " + status );
         }
         else{
         
            urlsAccessed++;
            in = new BufferedReader( 
                                 new InputStreamReader( 
                                     myURLConnection.getInputStream())); 
            System.out.println("****** CONNECTED *************");
         }
      } 
      catch (Exception e) { 
         // TODO Auto-generated catch block 
         System.out.println(e.toString());
         //e.printStackTrace(); 
      } 
   
      return in;
   } 
   
   public static String removeBlankLines(URL sourceUrl){
      String htmlText = "";     
      try{
         BufferedReader bf = readURL(sourceUrl);
         //System.out.println("Si entre..");
      
         String linea = null;
         int i = 0;
         while((linea = bf.readLine()) != null){
            
            if(linea.matches("[\\r\\t]*")){
               i++;
               continue;
            }
            htmlText+=linea+"\n";
         }
         bf.close();
      }
      catch(Exception e){
         System.out.println("Exception when REMOVING EXTRA LINES \n" + e.toString());
      }
      return htmlText;
   }
         
	/**
 * Searching number of ocurrences of HTML components("tags") in segments of HTML forms
 * Receive the segment of code, hashtable that contains the keys of html components 
 * and its number of ocurrence, URL, number of form
 * 
 * 
 * @return the number of ocurrencees of HTML components and 
   @the asignation of class "SEARCHABLE" or "NOT-SEARCHABLE"
 */
   public static boolean isSearchable(Segment form, StringBuffer characVect){
      StartTag tagForm=(StartTag)form;
      Element HTMLForm = tagForm.getElement(); 
      
      boolean investigable=true;
   	
      Hashtable<String,Integer> tablax = new Hashtable<String,Integer>();
      tablax.put("get",0);
      tablax.put("post",0);
      
      String atributo=null;
      atributo= tagForm.getAttributeValue("method");
      if(atributo!=null){
         atributo=atributo.toLowerCase();
         if(tablax.containsKey(atributo)){
            Integer contador = tablax.get(atributo);
            contador++;
            tablax.put(atributo,contador);
         }
      }
     
   
      List<? extends Segment> htmlComponents = HTMLForm.getAllStartTags();
   	
   	
      Element e1;      
      int di=0; 
      int i=0;
      int asig;
      
      boolean val1=false,val2=false,val3=false,val4=false;
      boolean valSearch=false,valFind=false,valGo=false, valBuy=false, valNext=false;
      boolean val11=false,val21=false,val31=false,val41=false,val51=false;
      boolean v21=false, v22=false, v23=false,v24=false, v25=false;
      boolean vid1=false, vid2=false, vid3=false, vid4=false, vid5=false;
      boolean vid6=false, vid7=false, vid8=false, vid9=false, vid10=false;
      Integer cont=0, cont2=0;
      String valueAttribute=null, altAttribute=null,nameAttribute=null, classAttribute=null, idAttribute=null, titleAttribute=null;
   
      FormControlType var=null;
      boolean pasw = false;
      Hashtable<String,Integer> tabla = new Hashtable<String,Integer>();
      Hashtable<String,Integer> tablaControlForm = new Hashtable<String,Integer>();
      
    
      //  ******* Tags******
      tabla.put("button",0);
      tabla.put("img",0);
      tabla.put("select",0);
      tabla.put("caption",0);
      tabla.put("legend",0);
      tabla.put("area",0);
      tabla.put("fieldset",0);
      tabla.put("optgroup",0);
      tabla.put("table",0);
      tabla.put("option",0);
      tabla.put("search/find/go/buy",0);
   	
   	//  *******Control Elements********
      tablaControlForm.put("submit",0);
      tablaControlForm.put("checkbox",0);
      tablaControlForm.put("hidden", 0);
      tablaControlForm.put("radio", 0);
      tablaControlForm.put("text",0);
      tablaControlForm.put("password",0);	
      tablaControlForm.put("file",0);
      tablaControlForm.put("image",0);   
      
   	 
      for (Segment htmlComponent : htmlComponents) {
         /** Identification of Tag name in the segment**/
         StartTag tag = (StartTag)htmlComponent;
         String nameTag = tag.getName();
         nameTag=nameTag.toLowerCase(); 
      			         
         if(tabla.containsKey(nameTag)){
            Integer contador = tabla.get(nameTag);
            contador++;
            tabla.put(nameTag,contador); //update the counter	
         }
          						       
         try{
            var = tag.getFormControl().getFormControlType() ;    
            String varStr = ""+var;
         	//System.out.println("Tag: "+tag);
         	//System.out.println("Type: "+varStr);
            varStr=varStr.toLowerCase(); 
         	
         	// * Identifying if the html component type 
            if(varStr.equals("password"))
               investigable=false;
         		
            if(tablaControlForm.containsKey(varStr)){
               Integer contador = tablaControlForm.get(varStr);
               contador++;
               tablaControlForm.put(varStr,contador);
            }	
         /**
         * Searching keywords that given evidence that the form is a searchable form  
         **/
            if((nameTag.equals("input")|| nameTag.equals("button")|| nameTag.equals("a")) && (varStr.equals("image")|| varStr.equals("submit")|| varStr.equals("button")|| varStr.equals("id"))) {
             
               valueAttribute= tag.getAttributeValue("value");
               altAttribute=tag.getAttributeValue("alt");
               nameAttribute=tag.getAttributeValue("name");
               classAttribute=tag.getAttributeValue("class");
               idAttribute=tag.getAttributeValue("id");
               titleAttribute=tag.getAttributeValue("title");
            	//System.out.println("Value: "+valueAttribute); 
                 		
               if(valueAttribute!=null){ 
                  val1=valueAttribute.matches("(?i).*search.*");
                  val2=valueAttribute.matches("(?i).*find.*");
                  val3=valueAttribute.matches("(?i).*go.*");
                  val4=valueAttribute.matches("(?i).*buy.*");  
                 	
               }
               if(altAttribute!=null){	
                  valSearch=altAttribute.matches("(?i).*search.*");
                  valFind=altAttribute.matches("(?i).*find.*");
                  valGo=altAttribute.matches("(?i).*go.*");
                  valBuy=altAttribute.matches("(?i).*buy.*");
                  valNext=altAttribute.matches("(?i).*Next.*");
               }
               if(nameAttribute!=null){ 
                  val11=nameAttribute.matches("(?i).*search.*");
                  val21=nameAttribute.matches("(?i).*find.*");
                  val31=nameAttribute.matches("(?i).*go.*");
                  val41=nameAttribute.matches("(?i).*buy.*");
                  val51=nameAttribute.matches("(?i).*buscar.*");
               }
               if(classAttribute!=null){ 
                  v21=classAttribute.matches("(?i).*search.*");
                  v22=classAttribute.matches("(?i).*find.*");
                  v23=classAttribute.matches("(?i).*go.*");
                  v24=classAttribute.matches("(?i).*buy.*");
                  v25=classAttribute.matches("(?i).*next.*");
               }
               if(idAttribute!=null){
                  vid1=idAttribute.matches("(?i).*search.*");
                  vid2=idAttribute.matches("(?i).*find.*");
                  vid3=idAttribute.matches("(?i).*go.*");
                  vid4=idAttribute.matches("(?i).*buy.*");
                  vid5=idAttribute.matches("(?i).*reservacion.*");
               }
               if(titleAttribute!=null){
                  vid6=titleAttribute.matches("(?i).*search.*");
                  vid7=titleAttribute.matches("(?i).*find.*");
                  vid8=titleAttribute.matches("(?i).*go.*");
                  vid9=titleAttribute.matches("(?i).*buy.*");
                  vid10=titleAttribute.matches("(?i).*reservacion.*");
               }
            
            	
            	
            	
            
               if(val1 || val3 || val2 || val4 || valSearch || valFind || valGo || valBuy || valNext || val11 || val31 || val21 || val41 || 	val51 || v21 || v22 || v23 || v24 || v25 || vid1 || vid2 || vid3 || vid4 || vid5  || vid6 || vid7 || vid8 || vid9 || vid10)
                  cont2++;
            }
         	
         /** Scaling the number of frecuences of "search" word 
          ** by a Umbral to not dispared the frecuency or aucence the keywords
         **/
            if(cont2==0) 
               asig=0;
            else if ((cont2==2)||(cont2==1))		 
               asig=1;
            else
               asig=2;  
         /*Storing the frecuences of keywords in hastable*/
            tabla.put("search/find/go/buy",asig);         		 
                        
         } 
         catch(Exception e){}
      }
      
      //System.out.println("\n-----------------------------------------------------------------------------\n");
      String name;           
      Enumeration names = tabla.keys();      
      
      while(names.hasMoreElements()){
         name = (String)names.nextElement();
         cont = tabla.get(name);
         if(name.equals("search/find/go/buy") && cont2 == 0)//the form does not generate a query to the DW
            investigable=false; 
         if(characVect != null)     
            characVect.append(cont+",");    
      }  
      
      if(characVect != null){
         Enumeration nameControls  = tablaControlForm.keys();
         Integer contador;
         String nameControl;
      
         while(nameControls.hasMoreElements()){
            nameControl = (String)nameControls.nextElement();
            contador = tablaControlForm.get(nameControl);   
            characVect.append(contador+",");		
         }		
      
         Enumeration namesPG  = tablax.keys();
         Integer c;
         String namePG;
            	
         while(namesPG.hasMoreElements()){
            namePG = (String)namesPG.nextElement();
            c = (Integer)tablax.get(namePG);
            characVect.append(c+",");
         }
      }  
      if(investigable){
         if(characVect != null)
            characVect.append("SEARCHABLE");
         return true;
      }
      else{
         if(characVect != null)
            characVect.append("NO-SEARCHABLE"); 
         return false;
      }          		      
      
   }
   
  		
   public static   Segment segSource(Segment form){
      StartTag tagForm=(StartTag)form;
      Element HTMLForm = tagForm.getElement(); 
      Segment htmlF= HTMLForm.getFirstElement();
   
                    // Source segmento=form.getSource();      
      return htmlF;
   }
}