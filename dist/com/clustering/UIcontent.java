   package com.clustering;

   import com.berico.similarity.*;  
   import java.util.LinkedList;
   import java.util.Hashtable;
   import java.util.Enumeration;
 
   public class UIcontent extends Clusterizable{
   
      String nameField;
      String typeUI;
      LinkedList valueField;
   
      public UIcontent(int numForm, int numNode,String type, String label, String name, LinkedList values){
         super(numForm,label,numNode);         	
         nameField = name;
         typeUI = type;
         valueField = values;         	                    
      }
   
      public String getName(){
         return nameField;
      }
   
      public LinkedList getValue(){
         return valueField;
      }
   		
      public String getType(){
         return typeUI;
      }		
   		
      public double compare(Clusterizable element2){//UI vs UI
         if(element2 instanceof UIcontent){
            return compare((UIcontent)element2);
         }  
         
         else if(element2 instanceof GroupElement){  //UI vs Grupo ********
            return compare((GroupElement)element2);
         }
         else 
            return 0.0;
      }
   
      public double compare(UIcontent element2){    
      
         UIcontent b = (UIcontent)element2;
         UIcontent a = this;      
      //Similitud_linguistica(a,b)=cos(name(a), name(b))+cos(label(a),cos(b))+max{cos(name(a),label(b)), cos(label(a),name(b))}
      //Similitud de dominio(a,b)=typeSim(a,b)+valueSim(a,b)
         double totalSimi=0.0;
         double nameSim=0.0,labelSim=0.0,namelabelSimA=0.0,namelabelSimB=0.0,simiLin=0.0;
         double simiDom=0.0;
      
        	
         String la=a.getLabel().toLowerCase();
         String lb=b.getLabel().toLowerCase();
         String na=a.getName().toLowerCase();
         String nb=b.getName().toLowerCase();
            
      //*************Similaridad linguistica***************/
         ISimilarityCalculator label = new JaroWinklerSimilarity();  
      //ISimilarityCalculator label = new CosineSimilarity(); 
         if((la.equals("label"))|| (lb.equals("label")))
            labelSim=0.0;
         else          
            labelSim=label.calculate(la,lb);
         ISimilarityCalculator name = new JaroWinklerSimilarity();    
      //ISimilarityCalculator name = new CosineSimilarity(); 
         nameSim=name.calculate(na,nb);
      // System.out.println("Name1: "+na+"("+a.getForm()+")"+ "   "+nameSim+ "   Name2: "+nb+"("+b.getForm()+")");
      
         ISimilarityCalculator namelabel = new JaroWinklerSimilarity();
      //ISimilarityCalculator namelabel = new CosineSimilarity(); 
         if(lb.equals("label"))
            totalSimi=0.0;
      	
         namelabelSimA=namelabel.calculate(na,lb);
      
         ISimilarityCalculator labelname = new JaroWinklerSimilarity();
      //ISimilarityCalculator labelname = new CosineSimilarity();
         if(lb.equals("label"))
            namelabelSimB=0.0;
         namelabelSimB=labelname.calculate(la,nb);
      
         if (Double.isNaN(labelSim)) {
            labelSim = 0.0;
         }
         if (Double.isNaN(namelabelSimA)) {
            namelabelSimA = 0.0;
         }
         if (Double.isNaN(namelabelSimB)) {
            namelabelSimB = 0.0;
         }
         if (Double.isNaN(nameSim)) {
            nameSim = 0.0;
         }
      
      
         double maxi=Math.max(namelabelSimA,namelabelSimB);
      //-------------------------------------------------
      //------------------ADICIONAL----------------------
      //Asegurar que se obtiene una similaridad
         if(labelSim==0.0){
            ISimilarityCalculator label2 = new CosineSimilarity(); 
            labelSim=label2.calculate(la,lb);
         
         }
         if(nameSim==0.0){
            ISimilarityCalculator name2 = new CosineSimilarity(); 
            nameSim=name2.calculate(la,lb);
         }

      //------------------------------------------------------- 
      //-------------------------------------------------------  
      
         simiLin=labelSim*0.6+nameSim*0.3+maxi*0.1;
      
             //Los coeficentes de peso deben recaer mas en el nombre de la etiqueta ya que ella es
      //que aporta mayor informaicon sobre el dato que recibira el campo
      	
         simiDom= this.simiDomain(b);  
           
      
      
         totalSimi=simiLin*0.6+simiDom*0.4; 
      // System.out.println("***A:"+ la+"   simiLin:"+simiLin+"    simiDom:"+simiDom+"    simiTotal:"+totalSimi+"   ***B:"+lb+"  TIPO:"+type2);
      //Scanner pauser = new Scanner (System.in); 
      //pauser.nextLine(); 
         if(((this.getLabel().equals("leave from"))&&(this.getForm()==8)) && ((b.getLabel().equals("from"))&&(b.getForm()==0)))
            System.out.println("Label1: "+la+"("+a.getForm()+")"+ "  SimiLin: "+simiLin+ " SimiDom: "+simiDom+"  Simitotal: "+totalSimi+"   Label2: "+lb+"("+b.getForm()+")");
      //}
      
         return totalSimi;     
      
      }
      public double compare(GroupElement b){
         return b.compare(this);
      }
   
   
      public String typeDomain(){
         int major=0;
         int cont;
         Hashtable<String,Integer> typeDomain = new Hashtable<String,Integer>();
         LinkedList valuelist = null;
         valuelist=this.getValue(); 
         String eti=this.getLabel();        
         typeDomain.put("typeTime",0);
         typeDomain.put("typeDate",0);
         typeDomain.put("typeInt",0);
         typeDomain.put("typeMoney",0);
         typeDomain.put("typeString",0);
         typeDomain.put("typeMonth",0);
         typeDomain.put("typeBoolean",0);
      
      
           
      
         for (int i = 0; i < valuelist.size(); i++) { 
            String valor=(String)valuelist.get(i); 
            valor=valor.trim();
            valor=valor.replace("&nbsp;","");
            valor=valor.replace("\u00A0",""); 
            valor=valor.replace(" ",""); 
         
            
            if(valor.matches("([01]?[0-9]|2[0-3])(am|pm)(:|-)[0-5][0-9](\\s)?(?i)(am|pm)")||valor.matches("([01]?[0-9]|2[0-3]|[0-9]):[0-5][0-9](am|pm)")||valor.matches("([01]?[0-9]|2[0-3])[ ]?(am|pm)")||valor.matches("(([01]?[0-9]|2[0-3])(am|pm)|noon)[ ]?(-|to)[ ]?(([01]?[0-9]|2[0-3])(am|pm)|noon)"))       
            {      
            //System.out.println("EMPATO TIME......");
               Integer contador =typeDomain.get("typeTime");
               contador++;
               typeDomain.put("typeTime",contador); 
            }
            
            
            else if(valor.matches("[0-9]?[0-9]/[0-9]?[0-9]/((19|20)\\d\\d)|(mm/dd/yyyy)"))
            {        
            //System.out.println("EMPATO DATE......");
               Integer contador =typeDomain.get("typeDate");
               contador++;
               typeDomain.put("typeDate",contador); 	
            }
            
            
            else if(valor.matches("[0-9]{1,2}|[0-9]{1,2}( |)[A-Za-z0-9-]+")& (!valor.contains("am"))&&(!valor.contains("pm")))
            {
            //System.out.println("EMPATO INTEGER......");
               Integer contador =typeDomain.get("typeInt");
               contador++;
               typeDomain.put("typeInt",contador);
            
            }
            		
            else if(valor.contains("$")||valor.contains("pesos")||valor.contains("mxp")||valor.contains("dollar")||valor.contains("usd")||valor.contains("us"))
            {
            //System.out.println("EMPATO Money......");
               Integer contador =typeDomain.get("typeMoney");
               contador++;
               typeDomain.put("typeMoney",contador); 
            }
            
                    
            else if(valor.matches("[a-z-/]+[0-9-]*")&& !valor.contains("jan") && !valor.contains("feb")&& !valor.contains("mar")&& !valor.contains("abr")&& !valor.contains("may")&& !valor.contains("jun")&& !valor.contains("jul")&& !valor.contains("aug")&& !valor.contains("sep")&& !valor.contains("oct")&& !valor.contains("nov")&& !valor.contains("dec")&& !valor.contains("am")&& !valor.contains("pm")&& !valor.contains("true")&& !valor.contains("false")|| valor.matches("")||valor.matches(" "))
            
                  //if(valor.matches("[A-Za-z-]+[0-9-]*")&& (!valor.contains("am"))&&(!valor.contains("pm"))&&(!valor.contains("$"))&&(!valor.contains("us"))&&(!valor.contains("mxp")))
            {//System.out.println("EMPATO String......");
               Integer contador =typeDomain.get("typeString");
               contador++;
               typeDomain.put("typeString",contador);
            }
            
            else if(valor.matches("(jan|feb|mar|apr|may|jun|jul|aug|sep|oct|nov|dec)"))
            {//System.out.println("EMPATO Month......");
               Integer contador =typeDomain.get("typeMonth");
               contador++;
               typeDomain.put("typeMonth",contador); //update the counter
            }
            else if(valor.matches("true")|| valor.matches("false"))
            {//System.out.println("EMPATO boolean......");
               Integer contador =typeDomain.get("typeBoolean");
               contador++;
               typeDomain.put("typeBoolean",contador); //update the counter
            }
         
         //else
         //  if((this.getLabel().equals("departure city"))&&(this.getForm()==2))
           // System.out.println("Label1: "+la+"("+a.getForm()+")"+ "  SimiLin: "+simiLin+ " SimiDom: "+simiDom+"  Simitotal: "+totalSimi+"   Label2: "+lb+"("+b.getForm()+")");
            //  System.out.println("Label1: "+this.getLabel()+"("+this.getForm()+")-->"+valor+"<--");
         
         
         }
      
         String name,domain="";           
         Enumeration names = typeDomain.keys();      
         while(names.hasMoreElements()){
            name = (String)names.nextElement();
            cont = typeDomain.get(name);
         //System.out.println("contador "+cont);
            if(cont>major){
               major=cont;
               domain=name;
            }
                
         }  
            
         return domain;
      }
   
   
   //*************Similaridad de dominio***************/
      public double simiDomain(UIcontent b){
         double simitypeDom,simivalueDom=0.0,simitypeBlock;
      //-----------Identificación del tipo de dominio que se maneja
         String type1=this.typeDomain();
         String type2=b.typeDomain();
      //if(this.getLabel().equals("adult"))
         //System.out.println("Label1: "+this.getLabel()+"("+this.getForm()+")"+ type1+ "   Label2: "+b.getLabel()+"("+b.getForm()+")"+type2);
      
      //Identificacion de tipos de building blocks
         String typeBlock1=this.getType();
         String typeBlock2=b.getType();
            
      
         if (type1.equals(type2))
            simitypeDom=1.0;
         else
            simitypeDom=0;
      	
      
         if(typeBlock1.equals(typeBlock2))
            simitypeBlock=1.0;
         else
            simitypeBlock=0.0;
      	
      //	if((this.getLabel().equals("departure city"))&&(this.getForm()==2))
      //        System.out.println("Label1: "+this.getLabel()+"("+this.getForm()+")"+"  ->"+simitypeBlock+ "  <-"+" Label2: "+b.getLabel()+"("+b.getForm()+")");
      
      
         
      //---------Identificación del valor del dominio de las UI	
            
         LinkedList listaa = null;
         LinkedList listab = null;
         double umbraltem=0.7;
         double res;          
         int c=0;
         listaa=this.getValue();
         listab=b.getValue();
      
      
         if(simitypeDom==1.0){
                  	
         //System.out.println("UI A:"+ la+" tipo: "+type1+"  value: "+ a.getValue()+  " ********** UI B:"+ lb+" tipo: "+type2+"  value: "+ b.getValue());
         
            for (int i = 0; i < listaa.size(); i++) {
               for (int j = 0; j < listab.size(); j++) {
                  String valorA=(String)listaa.get(i);
                  String valorB=(String)listab.get(j);
               
               
               //condicion para eliminar texto de los tipos enteros
                  if(type1.equals("typeInt")&& type2.equals("typeInt")){
                     String[] valoresA = valorA.split(" ");
                     String[] valoresB = valorB.split(" ");
                     ISimilarityCalculator valuedomain = new JaroWinklerSimilarity(); 
                  // ISimilarityCalculator valuedomain = new CosineSimilarity(); 
                     res =valuedomain.calculate(valoresA[0],valoresB[0]);
                  
                  
                  }
                  else{
                     ISimilarityCalculator valuedomain = new JaroWinklerSimilarity();
                  // ISimilarityCalculator valuedomain = new CosineSimilarity(); 
                     res =valuedomain.calculate(valorA,valorB);
                  }
                  if(res>=umbraltem){
                     c=c+1;
                     break;
                  }
               }
            
            }
            int suma=listaa.size()+listab.size();
            int nume=2*c;
            simivalueDom=(double)nume/suma;
         }
      
         double simiDom=simitypeBlock*0.3+simitypeDom*0.5+simivalueDom*0.2;  
         return simiDom;  	
      
              
      }
   
   
      public String toString(){  
      //imprime numNode, label, values 
         String salida = getNumNode() + ", " + "\"" + getName()  + "\", \"" + getLabel() + "\"";
      //ahora agrega los valores
         if(valueField != null){
            if(valueField.size() > 0){
               salida = salida + ", \"";
               salida += valueField;   
               salida = salida + "\"";
            }
         }
         return salida;
      
      }
      public String show(){
         String salida = "S("+getForm() + "," + getNumNode() + "," + getName() + "," + getLabel() + ")";
         return salida;
      }
   
   
   }