   package com.integration.tree;

   import com.renderTree.*;
   import com.clustering.*;
   import com.integration.*;
   import java.io.* ;
   import java.util.*;  

   public class AVGMat{
   
      double[][] avgMat;    
      int[][] unifiedMat;
   
      double[] promVector;
      int[] cuenta;
      LinkedList<String> lista             = MapaGlobal.getValues(); 
      LinkedList<String> sortedAttrib      = null;
      LinkedList<Vector<Double>> avgVector = null;
      LinkedList<String> sortedAVGVector = null;
       	
      public  AVGMat(LinkedList<int[][]> allMatrices, LinkedList<Vector<String>> allVectors){
      
         avgMat       =  new double[lista.size()][lista.size()]; 
         promVector   =  new double[lista.size()];
         cuenta       =  new int[lista.size()];   
         sortedAttrib =  new LinkedList<String>(); 
      	
         for(int[][] mi:allMatrices)
         {
            for (int i = 0; i < lista.size(); i++){
               for(int j = 0; j < lista.size(); j++){
                  avgMat[i][j]=avgMat[i][j]+(double)mi[i][j];
               
               }   
            }
         }
      
         for (int i = 0; i < lista.size(); i++){
            for(int j = 0; j < lista.size(); j++){
               if(i==j){
               
               //int cifras=(int) Math.pow(10,0);
               //avgMat[i][j]=Math.rint((avgMat[i][j]/allMatrices.size())*cifras)/cifras;
                  double dato =avgMat[i][j]/allMatrices.size();
                  avgMat[i][j]= roundElements(dato);
               }
               else
               {
                  int cifras=(int) Math.pow(10,0);
                  avgMat[i][j]=Math.rint((avgMat[i][j]/allMatrices.size())*cifras)/cifras;
               
               }
            }   
         }
         roundGroupElements();
      //calculo del vector promedio de precedencia de los nodos
         sortedAttrib=avgVect(allVectors);
         showAVGMatriz();
      //tempoMatriz();
      //showAVGMatriz();
         resortedAVgMat(sortedAttrib);
         showUnifiedMat();
         showAVGVect();
                   
      }
   
      public LinkedList<LinkedList<Double>> getAVGMatrix(){
         LinkedList<LinkedList<Double>> matrix = new LinkedList<LinkedList<Double>>();
      
         for (int i = 0; i < lista.size(); i++){
            LinkedList<Double> row = new LinkedList<Double>();
         
            for(int j = 0; j < lista.size(); j++){
               row.add(avgMat[i][j]);
            }
            matrix.add(row);
         }
      
         return matrix;
      }
   
      public LinkedList<LinkedList<Integer>> getUnifiedAVG(){
         LinkedList<LinkedList<Integer>> matrix = new LinkedList<LinkedList<Integer>>();
      
         for (int i = 0; i < lista.size(); i++){
            LinkedList<Integer> row = new LinkedList<Integer>();
         
            for(int j = 0; j < lista.size(); j++){
               row.add(unifiedMat[i][j]);
            }
            matrix.add(row);
         }
      
         return matrix;
      }
   
   
      public LinkedList<String> getAVGVector(){
         return sortedAttrib;
      }
      
           
   
   
   
      public int roundElements(double dato)
      {
         double bound=0.5;
      
         int roundnum=(int)(dato + 0.999 - bound);
         return roundnum;
      
      }
      public void roundGroupElements(){
      
         for (int i = 0; i < lista.size(); i++){
            for(int j = 0; j < lista.size(); j++){
            
            
               if(i!=j){	 
                  int dato=(int)avgMat[i][j];
                  if(dato == 1.0)
                     avgMat[i][j]= dato+2;
               }
            }   
         }
      }
   
      public LinkedList<String> avgVect(LinkedList<Vector<String>> allVectors){
         Vector<Double> vector; 
         avgVector                       = new LinkedList<Vector<Double>>();
         Map<String,Double> map          = new TreeMap<String,Double>();
         sortedAVGVector = new LinkedList<String>(); 
         
      //cuenta el numero de varibles que son diferentes de 0 
         double dato;
         for(Vector<String> vi:allVectors)
         { 
            vector = new Vector<Double>();
            int s=vi.size();
            
            for(int j = 0; j < lista.size(); j++){
               int valor=0;
               int cifras=(int) Math.pow(10,2);
               int index = vi.indexOf(lista.get(j))+1;
               if(index==-1)
                  dato=0.0;
               else{
                  dato= (double)index/s;
                  dato=Math.rint(dato*cifras)/cifras;}
               vector.add(dato);
            //*******Quitar los elementos 0 en el promedio
               
               promVector[j]=promVector[j]+vector.get(j);
               if(vector.get(j)!=0){
                  valor++;                   
                  cuenta[j]=cuenta[j]+valor;
                        }
            }
            avgVector.add(vector);
         }
      
         for(int i = 0; i < lista.size(); i++)
         {
            int cifras=(int) Math.pow(10,2);
            //promVector[i]=Math.rint((promVector[i]/avgVector.size())*cifras)/cifras;
            promVector[i]=Math.rint((promVector[i]/cuenta[i])*cifras)/cifras;
            map.put(""+lista.get(i),promVector[i]);
         }
         SortedSet sortedEntries=entriesSortedByValues(map); 
      
         for(Object entry:sortedEntries){
            String cad=entry.toString();
            sortedAVGVector.add(""+cad.charAt(0));
         }
         return sortedAVGVector;
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
   
      public void resortedAVgMat(LinkedList<String> sortedAVGVector){
         int[][] tempo               =  new int[lista.size()][lista.size()]; 
         unifiedMat               =  new int[lista.size()][lista.size()]; 
         LinkedList<Integer> index = new LinkedList<Integer>();
      
         for(String attrib:sortedAVGVector){
            for(int j = 0; j < lista.size(); j++){
               if(attrib.equals(lista.get(j))){
                  index.add(lista.indexOf(attrib));
                  break;
               }
            }
         }
      
         if(index.size() == 0) 
            return;
                      
         for (int i = 0; i < lista.size(); i++){
            int indice=index.get(i);
            for(int j = 0; j < lista.size(); j++){
               tempo[i][j]=(int)avgMat[indice][j];
            }   
         
         }
      
         for (int i = 0; i < lista.size(); i++){
            int indice=index.get(i);
            for(int j = 0; j < lista.size(); j++){
               unifiedMat[i][j]=(int)tempo[j][indice];
            }   
         
         }
      
      
         
      }
   
      public void showUnifiedMat(){
         if(unifiedMat==null){
            System.out.println("No hay vector AVG");
            return;
         }
      
         System.out.println("**********  MATRIZ AVG ORDENADA ***********");
      
         for (String n : sortedAVGVector)
            System.out.print("\t"+n);
      
         for (int i = 0; i < sortedAVGVector.size(); i++){
            System.out.println("");
            String n = sortedAVGVector.get(i);
            System.out.print("   "+n+"\t");
            for(int j = 0; j < lista.size(); j++){
               System.out.print(unifiedMat[i][j]+"\t");
            }   
         }
         System.out.println("");
      
      
      }
     				
      public void showAVGMatriz(){
      
         if(avgMat==null){
            System.out.println("No hay matriz AVG");
            return;
         }
        
         System.out.println("**********  MATRIZ AVG ***********");
      
         for (String n : lista)
            System.out.print("\t"+n);
         for (int i = 0; i < lista.size(); i++){
            System.out.println("");
            String n = lista.get(i);
            System.out.print("   "+n+"\t");
            for(int k = 0 ; k < i; k++)
               System.out.print(" "+"\t");
            for(int j = i; j < lista.size(); j++){
            //int cifras=(int) Math.pow(10,1);
               System.out.print(avgMat[i][j]+"\t");
            }   
         }
         System.out.println("");
      
         System.out.println("**********  FIN DE MATRIZ AVG ***********");
         System.out.println("");
         System.out.println("");
      
      
      }	
      public void showAVGVect(){
         if(avgVector==null){
            System.out.println("No hay vector AVG");
            return;
         }
         System.out.println("VECTOR AVGVEC");     
         for (String n : lista)
            System.out.print("\t"+n);
         System.out.println("");
         for(Vector<Double> V: avgVector){
            for(Double valor:V)
               System.out.print("\t"+valor);
         
            System.out.println("");
         }
         for(int i = 0; i < lista.size(); i++)
            System.out.print("\t"+promVector[i]);
      
         System.out.println(" ");
      }	
   
   }