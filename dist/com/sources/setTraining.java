   package com.sources;
/*
 *Esta clase construye el conjunto de entrenamiento de WQIs de un 
 *dominio dado
 */
   import net.htmlparser.jericho.*;
   import java.util.*;
   import java.io.*;
   import java.net.*;
    public class setTraining{
      	           
   public static void comparTerms(LinkedList<String> wqiLabels){
       //static ArrayList<Term> dictionary = new ArrayList<Term>();
         
             
      
         StringBuffer vectorTerms = new StringBuffer();
         List<StringBuffer> vectors = new LinkedList();
      
         Hashtable<String,Integer> terminos = new Hashtable<String,Integer>();
			
      	// //Terms associates with book domain 
         terminos.put("author",0);
			//terminos.put("title",0);
			terminos.put("titleBook",0);
         terminos.put("keyword",0);
         terminos.put("isbn",0);
         terminos.put("sort",0);
         //terminos.put("price",0);
			terminos.put("priceBook",0);
         terminos.put("publisher",0);
         terminos.put("subject",0);
         //terminos.put("binding",0);
         //terminos.put("display",0);
			//terminos.put("print demand results",0);
			terminos.put("language",0);
         terminos.put("publication year",0);
			terminos.put("edition",0);
			terminos.put("books",0);
      	
      	//Terms associates with job domain
			terminos.put("to",0);
			terminos.put("location",0);
			terminos.put("temp full time jobs",0);
			terminos.put("job title",0);
			terminos.put("show jobs job description",0);
         terminos.put("keywords",0);
         terminos.put("am looking job",0);
         terminos.put("chief technology officer jobs",0);
         terminos.put("select state metro area",0);
         terminos.put("select job type",0);
         terminos.put("chief technology officer jobs",0);
         terminos.put("both temp full time jobs",0);
         terminos.put("temp to hire jobs",0);
               	
						
      	//Terms associates with hotels domain
         terminos.put("rooms",0);
         terminos.put("check in",0);
			//terminos.put("check inHotels",0);
         terminos.put("25 miles from Destination",0);
         terminos.put("check out",0);
			//terminos.put("check outHotels",0);
         terminos.put("adult",0);
         terminos.put("corporate code",0);
         terminos.put("address",0);
         terminos.put("child",0);
		   terminos.put("distance",0);
         terminos.put("departure date",0);
			//terminos.put("departure date_Hotels",0);
         terminos.put("first name",0);
         terminos.put("last name",0);
         terminos.put("confirmation",0);
         terminos.put("0.0",0);
         terminos.put("ratings",0);
         terminos.put("privacy policy",0);
         terminos.put("email preferences",0);
         terminos.put("please enter destination city",0);
         terminos.put("city airport",0);
         terminos.put("refrigerator",0);
         terminos.put("hotel chain",0);
         terminos.put("hotel name",0);
         terminos.put("arrival date",0);
         terminos.put("landmark ex empire state building",0);
         terminos.put("country",0);
         terminos.put("swimming pool",0);
         terminos.put("tennis",0);
         terminos.put("use flexible dates",0);
         terminos.put("hotels",0);
         terminos.put("need",0);
         terminos.put("minors",0);
         terminos.put("minor ages",0);
         terminos.put("ages time travel",0);
         terminos.put("return",0);
      	
      	
        //Terms associates with movies domain					
        terminos.put("title",0);
		  //terminos.put("titleMovies",0);
         terminos.put("actor",0);
         terminos.put("director",0);
         terminos.put("price",0);
			//terminos.put("priceMovies",0);
         terminos.put("keywords",0);
			//terminos.put("keywordsMovies",0);
         terminos.put("vhs",0);
         terminos.put("pal",0);
         terminos.put("secam",0);
         terminos.put("genre",0);
         terminos.put("back to school sale up to off",0);
         terminos.put("up to off summer reads",0);
         terminos.put("whsmith pack oil pastels various colours",0);
      	
			
			
      	 //AIRFARES Terms associates 
        // terminos.put("departure date",0);
			terminos.put("departure date_Airfares",0);
         terminos.put("return date",0);
         terminos.put("children",0);
         terminos.put("from city",0);
         //terminos.put("adults",0);
			terminos.put("adultsAirfares",0);
         terminos.put("drop off",0);
         terminos.put("one way",0);
         terminos.put("departure time",0);
         terminos.put("destination",0);
         terminos.put("lap infants",0);
         terminos.put("round trip",0);
         terminos.put("airports",0);
         terminos.put("prefer non stop flights",0);
         terminos.put("select cabin/class",0);
         terminos.put("age",0);
         terminos.put("multi city",0);
         terminos.put("use miles",0);
         terminos.put("fare type",0);
         terminos.put("leave",0);
         terminos.put("roundtrip",0);
         terminos.put("room",0);
         //terminos.put("check in",0);
			terminos.put("check inAirfares",0);
         //terminos.put("check out",0);
			terminos.put("check outAirfares",0);
        // terminos.put("pick up date",0);
			terminos.put("pick up dateAirfares",0);
         terminos.put("ticket",0);
         terminos.put("teens",0);
         terminos.put("compare prices other sites",0);
      				
						
						
						
						
      	//Terms associates with carRental domain
         terminos.put("airport",0);
         terminos.put("car rental",0);
         terminos.put("pick up from",0);
         terminos.put("city",0);
         terminos.put("pick up time",0);
         terminos.put("corporate",0);
         terminos.put("mm/dd/yyyy",0);
         terminos.put("address",0);
         terminos.put("state",0);
         terminos.put("drop off to",0);
         terminos.put("rental car discount code",0);
         terminos.put("rooms do",0);
        terminos.put("pick up date",0);
			//	terminos.put("pick up dateCarRental",0);
         terminos.put("drop off date",0);
         terminos.put("drop off time",0);
         terminos.put("car type",0);
         terminos.put("transmission",0);
         terminos.put("country",0);
         terminos.put("choose car",0);
         terminos.put("select dates",0);
         terminos.put("car pick up",0);
         terminos.put("car drop off",0);
        // terminos.put("check in",0);
				terminos.put("check inCarRetal",0);
         //terminos.put("check out",0);
				terminos.put("check outCarRental",0);
         terminos.put("adults",0);
				//terminos.put("adultsCARrEENTAL",0);
         terminos.put("minor ages",0);
      	
         for (String nameTerm: wqiLabels ) {
            nameTerm=nameTerm.toLowerCase(); 
         			         
            if(terminos.containsKey(nameTerm)){
               Integer contador = terminos.get(nameTerm);
               contador++;
               terminos.put(nameTerm,contador); //update the counter	
            }
             		
         }
       
         Enumeration names  = terminos.keys();
         Integer contador;
         String name;
      	
         while(names.hasMoreElements()){
            name = (String)names.nextElement();
            contador = terminos.get(name);
            System.out.println(name + " \t : " + contador);
				//System.out.println("@attribute "+name+" INTEGER");
            vectorTerms.append(contador+",");		
         }		
         
      	 vectorTerms.append("Movies");	
         
         vectors.add(vectorTerms); 
         try{
            File file = new File("Trainning11.txt");
            PrintWriter ap = null;
            ap = new PrintWriter(new FileWriter(file,true));
            for(StringBuffer s : vectors)
               ap.println(s);
         		 
            ap.close();
         }
             catch(Exception e){
               System.out.println(e.toString());
            }
      
      
         //ListIterator itr = dictionary.listIterator();
         System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
         System.out.println("Printing dictionary");
         System.out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^"); 
      }
       public static void main(String args[]){
      
      }
   }