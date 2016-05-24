package com.sources;
  
import net.htmlparser.jericho.*;
import java.util.*;
import java.io.*;
import java.net.*;


public class Heuristics{
    //static ArrayList<String> dictionary = new ArrayList<String>();
   static List<LinkedList> sal = new LinkedList();
   static String termsWQI = "";

  /*----------------------------------------------------------------------------------
	    ----------------------------------------------------------------------------------
	  */
	
		/** The heuristic for the identifying of fiels is based in the localization of rows
          % Firts localizate the row where is disponible a fiel. Remember that a 
			 % field is defined by three elements: type, name and value.
			 % Once ubicated the row of each fields is identify the label associated 
			 %	to this field doing a up slipping so much rows as is possible to find 
			 % the label associated,
      **/
      /* ----------------------------------------------------------------------------------
		   -----------------------------------------------------------------------------------
		 */    
   public static LinkedList<String> identifyFields(Segment wqiForm){
      	
      //System.out.println("Iniciando identificacion de etiquetas asociadas a fields");
      FormFields formFields=wqiForm.getFormFields();
      LinkedList<Integer> renglonesCampos = new LinkedList<Integer>();
      	
      FormControl f =null;
      Source src = null;
      int pos = 0;
      int renglon = 0;
   
    	//Obtained web query interface using a new form     
      for (FormField formField : formFields) {
         f = formField.getFormControl();
         src = f.getSource();
         pos = f.getBegin();
         renglon = src.getRow(pos);
         
         renglonesCampos.add(renglon);
         //System.out.println("Element: " + f.getFormControlType() + " Renglon: " + renglon);   
      }
      			
   
      TextExtractor textExtractor= 
         new TextExtractor(wqiForm){
            public boolean excludeElement(StartTag startTag) { 
               return "option".equalsIgnoreCase(startTag.getName());
               	
            }
         };
      
      String extractedText = textExtractor.toString();
      /*
      System.out.println("Extracted text: " + extractedText);
      String[] textWQI = extractedText.split("//"); 
      */
       LinkedList<String> textWQIList = textExtractor.getList();
      
      String[] textWQI = textWQIList.toArray(new String[textWQIList.size()]);
      
      for(String a:textWQI)
      System.out.println("Extracted text: " + a);
      
      /**Elimination of puntuaction symbols and stop words	**/
      removeSymbol(textWQI);
      removeGuion(textWQI);
      removeStopWords(textWQI);
     
    
     for(String cad:textWQI)
        System.out.println("Now: " + cad);    
   	  
   	  
   
   
      LinkedList<String> terminos = new LinkedList<String>();    	   
      renglon = 0;
            
      for(int j = 0; j < textWQI.length;j+=2){
         renglon = Integer.parseInt(textWQI[j+1]);
         
         for(int stored: renglonesCampos){
            //System.out.println("Renglon: " + renglon + " stored: " + stored);
         
            for (FormField formField : formFields) {
               f = formField.getFormControl();
               String c=f.getFormControlType().toString();
               if(c.equals("RADIO"))
               {
                  if (renglon == (stored + 1)|| renglon == (stored + 2)){
                     //System.out.println("si entre...");
                     if (!terminos.contains(textWQI[j])){
                        terminos.add(textWQI[j]);
                     }
                     
                     //else
                        //System.out.println("removing de radio " + cadenas[j]);
                  }
               }
               
               if(c.equals("TEXT")){
                  String ca=f.getValues().toString();
                  if(!ca.equals("[]")){
                     ca= ca.substring(1,ca.length()-1);
                     if (!terminos.contains(ca)){
                        terminos.add(ca);
                     }
                  }
               }
            }
         
            if(renglon == (stored - 1) || renglon == (stored - 2)|| renglon == (stored - 3)|| renglon == (stored - 4) ||renglon == (stored - 5)|| renglon == stored ){
            
            /**Elimination of repeat words into HTML form 
            **/
               if (!terminos.contains(textWQI[j])){
                  terminos.add(textWQI[j]);
               
               }
            }
         }         
      }
   
   
   
   	           //for(String cad:terminos)
      //System.out.println(cad); 
       
   	  
   	  
   
     
     /** Elimination of complete phrases that contain more of four words
      **/	  
      largePhrases(terminos);
      /*Call to elimination of empty words or garbage words*/
      emptyWords(terminos);
   	  
             
      System.out.println("FINALS LABELS ");
     
      for( String cadena:terminos ){
         System.out.print(cadena + "//");
      }
      System.out.println("");
      return  terminos; 
   }

  	
//------------------------------------------------------------------------
//------------------------------------------------------------------------	
/** This method eliminate complete phrases that contain more of 6 words
 **/
//------------------------------------------------------------------------
//------------------------------------------------------------------------
   public static void largePhrases(LinkedList<String> terminos){
      int i = 0;
      while(terminos.size()>0 && i < terminos.size()){
         String s1= terminos.get(i);
         int count=0; 
         StringTokenizer stk=new StringTokenizer(s1," ");        
         while(stk.hasMoreTokens()){
            String token=stk.nextToken();
            count++;
         }
         if(count>=5){
            boolean removido = terminos.remove(s1);
            //System.out.println("Removing more than 4" + s1 );
            while(removido){
               //System.out.println("Removing  more than 4" + s1 ); 		      						
               removido = terminos.remove(s1);
            }
         }
         else
            i++;
      }
   }

 //------------------------------------------------------------------------
//------------------------------------------------------------------------		
/** This method eliminate empty words(garbage words)as "search/find/go" in term of WQIs
**/
//------------------------------------------------------------------------
//------------------------------------------------------------------------	
   public static void emptyWords(LinkedList<String> terminos){
      LinkedList<String> keySearch = new LinkedList<String>();
      keySearch.add("search");
      keySearch.add("Search");
      keySearch.add("find");
      keySearch.add("go");
     // keySearch.add("keyword");
      keySearch.add("keywords");
      keySearch.add("format");
      keySearch.add("condition");
      keySearch.add("separate");
      //keySearch.add("city");
      //keySearch.add("airport");
      
      keySearch.add("zip code");
      keySearch.add("advanced");
      keySearch.add("eg");
      keySearch.add("suggestions");
      int i;
      for (String s2: keySearch){
         i = 0;
         while(terminos.size()>0 && i < terminos.size()){
            String s1= terminos.get(i);	
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
      }
   }
 	
 //------------------------------------------------------------------------
//------------------------------------------------------------------------		
/**This method remove all puntuaction symbols and digits incluyed into the terms 
in each WQI exluding the symbol '-'
**/		
 //------------------------------------------------------------------------
//------------------------------------------------------------------------	
   public static void removeSymbol(String [] cadenas){
      for(int m=0;m<cadenas.length;m+=2){
         cadenas[m]=cadenas[m].replaceAll("[\\d]+","");
         cadenas[m]=cadenas[m].replaceAll("[\\p{Punct}&&[^-/]]+","");
      }
      return;
   }

   //------------------------------------------------------------------------
//------------------------------------------------------------------------	
/**This method remove guions into a string
**/		
 //------------------------------------------------------------------------
//------------------------------------------------------------------------	
   public static void removeGuion(String [] cadenas){
      for(int m=0;m<cadenas.length;m+=2){
         cadenas[m]=cadenas[m].replaceAll("[-]+"," ");
      
      }
      return;
   }
   

 //------------------------------------------------------------------------
//------------------------------------------------------------------------		
/**This method remove all stopWord contained in large phrases or terms of WQIs
 **/
 //------------------------------------------------------------------------
//------------------------------------------------------------------------	 
   public static void removeStopWords(String[] cadenas)
   {  
   
      
      String token="";
      List<LinkedList> WQIsVectors = new LinkedList();
      LinkedList<String> salida = new LinkedList<String>();
      String[] stopWords = new String[]{"a","able","about","above","according","accordingly","across","actually","after","afterwards","again","against","all","allow","allows","almost","alone","along","already","also","although","always","am","among","amongst","an","and","another","any","anybody","anyhow","anyone","anything","anyway","anyways","anywhere","apart","appear","appreciate","appropriate","are","around","as","aside","ask","asking","associated","at","available","away","awfully","b","be","became","because","become","becomes","becoming","been","before","beforehand","behind","being","believe","below","beside","besides","best","better","between","beyond","both","brief","but","by","c","came","can","cannot","cant","cause","causes","certain","certainly","changes","clearly","co","com","come","comes","concerning","consequently","consider","considering","contain","containing","contains","corresponding","could","course","currently","d","definitely","described","despite","did","different","do","does","doing","done","down","downwards","during","e","each","edu","eg","eight","either","else","elsewhere","enough","entirely","especially","et","etc","even","ever","every","everybody","everyone","everything","everywhere","ex","exactly","example","except","f","far","few","fifth","first","five","followed","following","follows","for","former","formerly","forth","four","from","further","furthermore","g","get","gets","getting","given","gives","go","goes","going","gone","got","gotten","greetings","h","had","happens","hardly","has","have","having","he","hello","help","hence","her","here","hereafter","hereby","herein","hereupon","hers","herself","hi","him","himself","his","hither","hopefully","how","howbeit","however","i","ie","if","ignored","immediate","in","inasmuch","inc","indeed","indicate","indicated","indicates","inner","insofar","instead","into","inward","is","it","its","itself","j","just","k","keep","keeps","kept","know","knows","known","l","last","lately","later","latter","latterly","least","less","lest","let","like","liked","likely","little","ll",
            "look","looking","looks","ltd","m","mainly","many","may","maybe","me","mean","meanwhile","merely","might","more","moreover","most","mostly","much","must","my","myself","n","name","namely","nd","near","nearly","necessary","need","needs","neither","never","nevertheless","new","next","nine","no","nobody","non","none","noone","nor","normally","not","nothing","novel","now","nowhere","number","o","obviously","of","off","often","oh","ok","okay","old","on","once","one","ones","only","onto","or","other","others","otherwise","ought","our","ours","ourselves","out","outside","over","overall","own","p","particular","particularly","per","perhaps","placed","please","plus","possible","presumably","probably","provides","q","que","quite","qv","r","rather","rd","re","really","reasonably","regarding","regardless","regards","relatively","respectively","right","s","said","same","saw","say","saying","says","second","secondly","see","seeing","seem","seemed","seeming","seems","seen","self","selves","sensible","sent","serious","seriously","seven","several","shall","she","should","since","six","so","some","somebody","somehow","someone","something","sometime","sometimes","somewhat","somewhere","soon","sorry","specified","specify","specifying","still","sub","such","sup","sure","t","take","taken","tell","tends","th","than","thank","thanks","thanx","that","thats","the","their","theirs","them","themselves","then","thence","there","thereafter","thereby","therefore","therein","theres","thereupon","these","they","think","third","this","thorough","thoroughly","those","though","three","through","throughout","thru","thus","to","together","too","took","toward","towards","tried","tries","truly","try","trying","twice","two","u","un","under","unfortunately","unless","unlikely","until","unto","up","upon","us","use","used","useful","uses","using","usually","uucp","v","value","various","ve","very","via","viz","vs","w","want","wants","was","way","we","welcome","well","went","were","what","whatever","when","whence","whenever","where","whereafter","whereas","whereby","wherein","whereupon",
            "wherever","whether","which","while","whither","who","whoever","whole","whom","whose","why","will","willing","wish","with","within","without","wonder","would","would","x","y","yes","yet","you","your","yours","yourself","yourselves","z","zero"}; 
      
      if(cadenas == null)
         return;
        
      LinkedList<String> setStopword;
   	  
      for(int i = 0; i < cadenas.length;i+=2){
         String nueva = "";
         setStopword = new LinkedList<String>();
         StringTokenizer stk=new StringTokenizer(cadenas[i].toLowerCase()," ");        
         boolean esStopWord = false;
         while(stk.hasMoreTokens()){
            token=stk.nextToken();
            esStopWord = false;
            for(String s2:stopWords){
               if((s2.equals(token)&& stk.hasMoreTokens()) || (s2.equals(token)&& (!nueva.equals(""))) ){
                  esStopWord = true;
                  setStopword.add(token);
                  break; 
               }
            }
            if(esStopWord)   
               continue;
            if(nueva.equals(""))   
               nueva += token;
            else
               nueva += " " + token;     
         }
         LinkedList list = new LinkedList();    	
         list.add(cadenas[i]);
         list.add(setStopword);
         list.add(nueva);
         list.add(false);
         WQIsVectors.add(list);
      	             
      }
      
   
      for(int i=0;i<WQIsVectors.size();i++){
         LinkedList list1 = (LinkedList)WQIsVectors.get(i);
         String term1= (String)list1.get(2);
         for(int j=i+1; j<WQIsVectors.size();j++){
            LinkedList list2 = (LinkedList)WQIsVectors.get(j);
            String term2= (String)list2.get(2);
            if(term1.equals(term2)){
               list1.set(3,true);
               list2.set(3,true);
            
            }
         }
       
      
      }
     // System.out.println("*******************************************************************");
           
      for(LinkedList element:WQIsVectors){
         if((boolean)element.get(3)){
            //System.out.println(element.get(0));
            String elemento=(String)element.get(0);
            salida.add(elemento);
         }
         else{
           // System.out.println(element.get(2));
            String elemento=(String)element.get(2);
            salida.add(elemento);
         }
      }
   
         
      int k = 0;
      for(String cad:salida){
         cadenas[k]=cad;
       //System.out.println(cadenas[k]);
         k+=2;
      }
      	 
   	
   
      return;
   	
   }	
	
	   	

   public static void main(String args[]){
      String sal;
      String[] textWQI ={"to", "--","number of adults", "","number of children"," ","--","","a number of to infants"};
      	
      removeStopWords(textWQI);
   	
      for(String cad:textWQI)
         System.out.println(cad);
   }       
}