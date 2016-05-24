   package com.sources;
   import net.htmlparser.jericho.*;
   import java.net.URLConnection;
   import java.net.URL;
   import java.io.InputStream;
   import java.util.*;
   import java.io.*;
   import java.net.*;

/**
 * <b>PageRankService provides simple API to Google PageRank Technology</b>
 * <br>
 * PageRankService queries google toolbar webservice and returns a
 * google page rank retrieved from one of the available datacenters.
 * Each new connection created by PageRankService will be made to a ip on the list
 * this was we will not get google security ringing their bells... ;-) ...as fast...
 */
   public class PageRankService {
   
      static private int dataCenterIdx = 0;
      static String WQI,fuente,sourceUrlString = "",sourceWQIString = "",nameWQI = "";
      static int umbral=5;
      //static  int num = 0;
    
   
    /**
     * List of available google datacenter IPs and addresses
     */
      static final public String [] GOOGLE_PR_DATACENTER_IPS = new String[]{
         //                "www.google.com",
         //                "64.233.161.101",
         //                "64.233.177.17",
         //                "64.233.183.91",
         //                "64.233.185.19",
         //                "64.233.189.44",
         //                "66.102.1.103",
         //                "66.102.9.115",
         //                "66.249.81.101",
         //                "66.249.89.83",
         //                "66.249.91.99",
         //                "66.249.93.190",
         //                "72.14.203.107",
         //                "72.14.205.113",
         //                "72.14.255.107",
                "toolbarqueries.google.com",
                };
   
    /**
     * Default constructor
     */
      public PageRankService() {
      
      }
   
    /**
     * Must receive a domain in form of: "http://www.domain.com"
     * @param domain - (String)
     * @return PR rating (int) or -1 if unavailable or internal error happened.
     */
      public int getPR(String domain) {
      
         int result = -1;
         JenkinsHash jHash = new JenkinsHash();
      
         String googlePrResult = "";
      
         long hash = jHash.hash(("info:" + domain).getBytes());
      
         String url = "http://"+GOOGLE_PR_DATACENTER_IPS[dataCenterIdx]+"/tbr?client=navclient-auto&hl=en&"+
                "ch=6"+hash+"&ie=UTF-8&oe=UTF-8&features=Rank&q=info:" + domain;
      
         try {
            URLConnection con = new URL(url).openConnection();
            InputStream is = con.getInputStream();
            byte [] buff = new byte[1024];
            int read = is.read(buff);
            while (read > 0) {
               googlePrResult = new String(buff, 0, read);
               read = is.read(buff);
            }
            googlePrResult = googlePrResult.split(":")[2].trim();
            result = new Long(googlePrResult).intValue();
         } 
            catch (Exception e) {
               System.out.println(e.getMessage());
            }
      
         dataCenterIdx++;
         if (dataCenterIdx == GOOGLE_PR_DATACENTER_IPS.length) {
            dataCenterIdx = 0;
         }
      
         return result;
      }
   
      public static void main(String [] args) throws Exception {
         String estado;
         String[] cad;
         SortedSet sortedEntries=null;
         TreeMap<String, Integer> treemap = new TreeMap<String, Integer>();
         //List<String> urls = new LinkedList();
         HashMap urls = new HashMap();
         int pageRank=-1;
         PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("PageRange.txt")));
      
         File database = new File( "C:/phD/Datasets/TEL-8/interface_downloadable/browse/Automobiles/Automobiles.xml" );
         BufferedReader entrada = new BufferedReader( new FileReader( database ) ); 	
         while (( fuente = entrada.readLine()) != null) { 
            if(fuente.startsWith("<srcurl>")&& fuente.endsWith("</srcurl>")){
              //num++;
               int indice=fuente.indexOf('>');
               int indice2=fuente.indexOf("</");
               fuente=fuente.substring(indice+1,indice2);
               sourceUrlString=fuente;
              
               System.out.println("\n*******************************************************************************\n");			
               long start = System.currentTimeMillis();
               PageRankService prService = new PageRankService();
               //String domain = "http://www.alaskaair.com/";
               if (args.length > 0) {
                  sourceUrlString = args[0];
               }
               pageRank=prService.getPR(sourceUrlString);
               System.out.println("Checking " + sourceUrlString);
               System.out.println("Google PageRank: " + prService.getPR(sourceUrlString));
               System.out.println("Took: " + (System.currentTimeMillis() - start) + "ms");
            
            
               if(pageRank >= umbral){
                  if(( WQI = entrada.readLine()) != null){
                     if(WQI.startsWith("<srcinterface>")&& WQI.endsWith("</srcinterface>")){
                     
                        int indWQI=WQI.indexOf('>');
                        int ind2WQI=WQI.indexOf("</");
                        WQI=WQI.substring(indWQI+1,ind2WQI);
                        sourceWQIString=WQI;
                      if (args.length > 0) 
                           sourceWQIString = args[0];
                          
                        urls.put(sourceUrlString, sourceWQIString); 
                        System.out.println("WQI:"+sourceWQIString);
                        treemap.put(sourceUrlString,pageRank);
                     	
                        sortedEntries=entriesSortedByValues(treemap); 
                     }
                  }
               }
              
            }
           
         }
        

         //System.out.println("Numero de sitios web:"+num);
         try{
            
            Iterator j = sortedEntries.iterator();
         // Display elements
            while(j.hasNext()) {
               Map.Entry entry = (Map.Entry)j.next();
               String sitio=(String)entry.getKey();
               Integer pageR=(Integer)entry.getValue();
               String c=sitio+",  ";
               out.print(c);
               out.println(pageR);
              
                         
            }
            out.close();
         }
         
            catch(Exception e){
               System.out.println(e.toString());
            }	
            
      	
         Set set = urls.entrySet();
     
         Iterator i = set.iterator();
      // Display elements
         while(i.hasNext()) {
            Map.Entry fue = (Map.Entry)i.next();
            String f=(String)fue.getKey();
            String v=(String)fue.getValue();
                  
            //for(String f : urls ) {    
            System.err.println("URL : \"" + f + "\"");
            
            int ind1=f.indexOf("www.");
            int ind2=f.indexOf(".",ind1+4);
            nameWQI =f.substring(ind1+4,ind2);
            System.out.println("Name " + nameWQI);
            
               
            if (f.indexOf(':') == -1) 
               f = "file:" + f;
            
            MicrosoftTagTypes.register();
            MasonTagTypes.register();
               
            Source source = null;
            
            try{
               source = new Source(new URL(v));          
               System.out.println("\n Buscando <FORMs> en " + f); 
               identifyAndSaveFORMS(source.getAllStartTags("form"),nameWQI);
            }
               catch(Exception e){
                  System.out.println(e.toString());
                  System.out.println("Sucedio un error al acceder al sitio:" + f); 
               }
            //}
         }
         	
      }
      
   	
      static <K,V extends Comparable<? super V>> SortedSet<Map.Entry<K,V>> entriesSortedByValues(Map<K,V> map) {
         SortedSet<Map.Entry<K,V>> sortedEntries = new TreeSet<Map.Entry<K,V>>(
               new Comparator<Map.Entry<K,V>>() {
                  @Override public int compare(Map.Entry<K,V> e1, Map.Entry<K,V> e2) {
                     int res = e1.getValue().compareTo(e2.getValue());
                     return res != 0 ? res : 1; // Special fix to preserve items with equal values
                  }
               }
            );
         sortedEntries.addAll(map.entrySet());
         return sortedEntries;
      
      }
   
   	
   	
      
   	 // Procedure to search forms
      private static void identifyAndSaveFORMS(List<? extends Segment> segments, String nameWQI){
         //se recibe lista con segmentos representando a cada FORM identificado en la URL
         int numForms=0;
         System.out.println("Se identificaron " + segments.size() + " <FORMs>");
           
         for (Segment segment : segments) { //recupera cada segmento, que es un <form>
            numForms = numForms + 1;
            StartTag tag=(StartTag)segment;
            Element elem = tag.getElement();
            System.out.println("Form[" + numForms + "]\n" + elem.getContent());
            
            try{
               PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(nameWQI+"_form"+ numForms +".html")));
               out.println("<html><form>");
               out.println(elem.getContent());
               out.println("</form></html>");
               out.close();
            }
               catch(Exception e){
                  System.out.println(e.toString());
               }	   
         }
      }
   	
   	
   }
