package com.preprocessing;

import java.util.*;
import com.clustering.*;
  	
public class Preprocessing{
	 
   public static void clean(LinkedList<Clusterizable> UIrepository){
      System.out.println("Iniciando preprocesamiento (eliminacion de stopwords)");
      removeStopWords(UIrepository);
      removeSegment(UIrepository);
      removeSymbol(UIrepository);
      removeRepetedTermInWQI(UIrepository);
   }
      

   private static void removeRepetedTermInWQI(LinkedList<Clusterizable> repository){
   //------------------------------------------------------------------------------------------------
   	//Quitar aquellos terminos que tienen etiquetas similares y se encuentran en la misma WQI, ejemplo:
   	//to: y to:
   	
      Clusterizable elementi;
      Clusterizable elementj;
      for(int i = 0; i < repository.size()-1; i++ ){
         elementi = repository.get(i);
      
         for(int j = i+1; j < repository.size(); j++){
            elementj = repository.get(j);
            if((elementi.getLabel().equals(elementj.getLabel())) && (elementi.getForm()==elementj.getForm())&& ((!elementi.getLabel().equals("label"))&&(!elementj.getLabel().equals("label")))){
               //System.out.println("elem1: "+elementi.getLabel()+"("+elementi.getForm()+")"+"  elem2: "+elementj.getLabel()+"("+elementj.getForm()+")");
               repository.remove(elementj);
            }
         }
      } 
     //------------------------------------------------------------------------------------------------
   	
   
   }
        
   private static void removeStopWords(LinkedList<Clusterizable> UIrepository){  
      String token="";
      List<LinkedList> WQIsVectors = new LinkedList();
      boolean esStopWord;
         /*String[] stopWords = new String[]{"a","able","about","according","accordingly","across","actually","after","afterwards","again","against","all","allow","allows","almost","alone","along","already","also","although","always","am","among","amongst","an","and","another","any","anybody","anyhow","anyone","anything","anyway","anyways","anywhere","apart","appear","appreciate","appropriate","are","around","as","aside","ask","asking","associated","at","available","away","awfully","b","be","became","because","become","becomes","becoming","been","before","beforehand","behind","being","believe","below","beside","besides","best","better","between","beyond","both","brief","but","by","c","came","can","cannot","cant","cause","causes","certain","certainly","changes","clearly","co","com","come","comes","concerning","consequently","consider","considering","contain","containing","contains","corresponding","could","course","currently","d","definitely","described","despite","did","different","do","does","doing","done","down","downwards","during","e","each","edu","eg","eight","either","else","elsewhere","enough","entirely","especially","et","etc","even","ever","every","everybody","everyone","everything","everywhere","ex","exactly","example","except","f","far","few","fifth","first","five","followed","following","follows","for","former","formerly","forth","four","further","furthermore","g","get","gets","getting","given","gives","go","goes","gone","got","gotten","greetings","h","had","happens","hardly","has","have","having","he","hello","help","hence","her","here","hereafter","hereby","herein","hereupon","hers","herself","hi","him","himself","his","hither","hopefully","how","howbeit","however","i","ie","if","ignored","immediate","in","inasmuch","inc","indeed","indicate","indicated","indicates","inner","insofar","instead","into","inward","is","it","its","itself","j","just","k","keep","keeps","kept","know","knows","known","l","last","lately","later","latter","latterly","least","less","lest","let","like","liked","likely","little","ll",
               "look","looking","looks","ltd","m","mainly","many","may","maybe","me","mean","meanwhile","merely","might","more","moreover","most","mostly","much","must","my","myself","n","name","namely","nd","near","nearly","necessary","need","needs","neither","never","nevertheless","new","next","nine","no","nobody","non","none","noone","nor","normally","not","nothing","novel","now","nowhere","number","o","obviously","of","off","often","oh","ok","okay","old","on","once","ones","only","onto","or","other","others","otherwise","ought","our","ours","ourselves","out","outside","over","overall","own","p","particular","particularly","per","perhaps","placed","please","plus","possible","presumably","probably","provides","q","que","quite","qv","r","rather","rd","re","really","reasonably","regarding","regardless","regards","relatively","respectively","right","s","said","same","saw","say","saying","says","second","secondly","see","seeing","seem","seemed","seeming","seems","seen","self","selves","sensible","sent","serious","seriously","seven","several","shall","she","should","since","six","so","some","somebody","somehow","someone","something","sometime","sometimes","somewhat","somewhere","soon","sorry","specified","specify","specifying","still","sub","such","sup","sure","t","take","taken","tell","tends","th","than","thank","thanks","thanx","that","thats","the","their","theirs","them","themselves","then","thence","there","thereafter","thereby","therefore","therein","theres","thereupon","these","they","think","third","this","thorough","thoroughly","those","though","three","through","throughout","thru","thus","together","too","took","toward","towards","tried","tries","truly","try","trying","twice","two","u","un","under","unfortunately","unless","unlikely","until","unto","up","upon","us","use","used","useful","uses","using","usually","uucp","v","value","various","ve","very","via","viz","vs","w","want","wants","was","we","welcome","well","went","were","what","whatever","when","whence","whenever","where","whereafter","whereas","whereby","wherein","whereupon",
               "wherever","whether","which","while","whither","who","whoever","whole","whom","whose","why","will","willing","wish","with","within","without","wonder","would","would","x","y","yes","yet","you","your","yours","yourself","yourselves","z","zero"}; */
      String[] stopWords = new String[]{"a","able","about","above","according","accordingly","across","actually","after","afterwards","again","against","all","allow","allows","almost","alone","along","already","also","although","always","am","among","amongst","an","and","another","any","anybody","anyhow","anyone","anything","anyway","anyways","anywhere","apart","appear","appreciate","appropriate","are","around","as","aside","ask","asking","associated","at","available","away","awfully","b","be","became","because","become","becomes","becoming","been","before","beforehand","behind","being","believe","below","beside","besides","best","better","between","beyond","both","brief","but","by","c","came","can","cannot","cant","cause","causes","certain","certainly","changes","clearly","co","com","come","comes","concerning","consequently","consider","considering","contain","containing","contains","corresponding","could","course","currently","d","definitely","described","despite","did","different","do","does","doing","done","down","downwards","during","e","each","edu","eg","eight","either","else","elsewhere","enough","entirely","especially","et","etc","even","ever","every","everybody","everyone","everything","everywhere","ex","exactly","example","except","f","far","few","fifth","first","five","followed","following","follows","for","former","formerly","forth","four","further","furthermore","g","get","gets","getting","given","gives","go","goes","going","gone","got","gotten","greetings","h","had","happens","hardly","has","have","having","he","hello","help","hence","her","here","hereafter","hereby","herein","hereupon","hers","herself","hi","him","himself","his","hither","hopefully","how","howbeit","however","i","ie","if","ignored","immediate","in","inasmuch","inc","indeed","indicate","indicated","indicates","inner","insofar","instead","into","inward","is","it","its","itself","j","just","k","keep","keeps","kept","know","knows","known","l","last","lately","later","latter","latterly","least","less","lest","let","like","liked","likely","little","ll",
               "look","looking","looks","ltd","m","mainly","many","may","maybe","me","mean","meanwhile","merely","might","more","moreover","most","mostly","much","must","my","myself","n","name","namely","nd","near","nearly","necessary","need","needs","neither","never","nevertheless","new","next","nine","no","nobody","non","none","noone","nor","normally","not","nothing","novel","now","nowhere","number","o","obviously","of","off","often","oh","ok","okay","old","on","once","one","ones","only","onto","or","other","others","otherwise","ought","our","ours","ourselves","out","outside","over","overall","own","p","particular","particularly","per","perhaps","placed","please","plus","possible","presumably","probably","provides","q","que","quite","qv","r","rather","rd","re","really","reasonably","regarding","regardless","regards","relatively","respectively","right","s","said","same","saw","say","saying","says","second","secondly","see","seeing","seem","seemed","seeming","seems","seen","self","selves","sensible","sent","serious","seriously","seven","several","shall","she","should","since","six","so","some","somebody","somehow","someone","something","sometime","sometimes","somewhat","somewhere","soon","sorry","specified","specify","specifying","still","sub","such","sup","sure","t","take","taken","tell","tends","th","than","thank","thanks","thanx","that","thats","the","their","theirs","them","themselves","then","thence","there","thereafter","thereby","therefore","therein","theres","thereupon","these","they","think","third","this","thorough","thoroughly","those","though","three","through","throughout","thru","thus","together","too","took","toward","towards","tried","tries","truly","try","trying","twice","two","u","un","under","unfortunately","unless","unlikely","until","unto","up","upon","us","use","used","useful","uses","using","usually","uucp","v","value","various","ve","very","via","viz","vs","w","want","wants","was","way","we","welcome","well","went","were","what","whatever","when","whence","whenever","where","whereafter","whereas","whereby","wherein","whereupon",
               "wherever","whether","which","while","whither","who","whoever","whole","whom","whose","why","will","willing","wish","with","within","without","wonder","would","would","x","y","yes","yet","you","your","yours","yourself","yourselves","z","zero"}; 
         
   
      	   
      LinkedList<String> setStopword;
      for(Clusterizable ei:UIrepository){
         String nueva = "";
         setStopword = new LinkedList<String>();
         String label=ei.getLabel().toLowerCase(); 
         StringTokenizer tokens=new StringTokenizer(label); 
         while(tokens.hasMoreTokens()){
            token=tokens.nextToken();
            esStopWord = false;
            for(String s2:stopWords){
               if( (s2.equals(token) && tokens.hasMoreTokens())|| (s2.equals(token)&& (!nueva.equals(""))) ){
                  esStopWord = true;
                  setStopword.add(token);
                     //token=tokens.nextToken();
                  break; 
               }
            }
            if(esStopWord)   
               continue; 
            if(nueva.equals(""))
               nueva+=token;
            else
               nueva=nueva+" "+token; 
         }
      		
         ei.setLabel(nueva);
         	//System.out.println("cadena: "+nueva);
      		
      	 
      	
      
      }
   } 
		  
   private static void removeSegment(LinkedList<Clusterizable> UIrepository){
   	
   
      for(Clusterizable ei:UIrepository){
         String nueva = "";
         String cadena=ei.getLabel().toLowerCase(); 
         if(cadena.contains("["))
            cadena=cadena.substring(0,cadena.indexOf("["));
         if(cadena.contains("("))
            cadena=cadena.substring(0,cadena.indexOf("("));				         
         ei.setLabel(cadena);
      }
   }
   
   	  
   private static void removeSymbol(LinkedList<Clusterizable> UIrepository){
      for(Clusterizable ei:UIrepository){
         String nueva = "";
         String cadena=ei.getLabel(); 
         
         cadena=cadena.replaceAll("[\\d]+","");
         cadena=cadena.replaceAll("[\\p{Punct}&&[^-./]]+","");
         
           
           // cadena=cadena.replaceAll("[-]+"," ");
         	//  cadena=cadena.replaceAll("[ \t\r\n]{2,}"," ");
         String tt="\u00A0";
         cadena=cadena.replaceAll(tt,""); 
         cadena=cadena.trim();
         ei.setLabel(cadena);
      }
   		
   		
   }
      
   	   		
   public static void main(String[] args){
    /*  LinkedList<String> fields=new LinkedList<String>();
   		fields.add("number of adults");
   		fields.add("number of children");
   		fields.add("a number of to infants");
   		Preprocessing uno=new Preprocessing();
   		String sal=uno.removeStopWords22("number of adults");
   */
   }
}