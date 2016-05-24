package com.renderTree;
/**
Clase auxiliar que realiza varias operaciones sobre una lista de objetos del tipo Noderenderable durante el proceso
de etiquetado y podado del arbol de renderizado.
*/
import java.util.*;
import org.lobobrowser.html.renderer.*;
 
 
public class Collapser{

   private static Vector findFirstRLine(int i, LinkedList<NodeRenderable> prevList){
      Vector data = new Vector();
      NodeRenderable node = null;
      String type = null;
      NodeRenderable rLine = null;
      String label = null;
   
      while(i < prevList.size()){
      //busca un RLine que se pueda concatener, es decir que no este etiquetado
         node = prevList.get(i);
         type = node.getType();
                     
         if(!type.equals("RLine")){ //solo considera RLines, ya que si son RBlock o table, estos bloques deben mantenerse
            i++;
            continue;
         }
      		
         label = node.getLabel();
         
         if(!label.equals("no label")){   //si es rline pero ya tiene etiqueta, no se considera
            i++;
            continue;	
         }
         
         rLine = node;
         break;
      }
   
      data.addElement(rLine);
      data.addElement(i);
   
      return data;
   }
   
       
   private static Vector findFirstRWord(int i, LinkedList<NodeRenderable> prevList){
      NodeRenderable node = null;
      Renderable ren = null;
      RWord rw = null;
      Vector data = new Vector();
      
      while (i < prevList.size()){
         node = prevList.get(i);
         ren = node.getRenderable();
        
         if((ren instanceof RBlank) || (ren instanceof RStyleChanger)){
            prevList.remove(i);
            continue;
         } 	  
         if(ren instanceof RWord){ 
            rw = (RWord)ren;
            break; //lo encuentra y termina
         }
         i++;
      }
      data.addElement(rw);
      data.addElement(i);         
      return data;
   }

      
   private static Vector findFirstRLabel(int i, LinkedList<NodeRenderable> prevList){
      NodeRenderable node = null;
      String type = null;
      Vector data = new Vector();
      
      while (i < prevList.size()){
         node = prevList.get(i);
         type = node.getType();
        
         if(type.equals("label")){ 
            break; //lo encuentra y termina
         }
         i++;
      }
      if( i == prevList.size())
         node = null;
         
      data.addElement(node);
      data.addElement(i);         
      return data;
   }


   private static int concatenarRLines(int i, LinkedList<NodeRenderable> prevList, NodeRenderable nodeAcc, boolean simple, boolean initial){
   
      String label = null;
      String type = null;
      NodeRenderable next = null;
      LinkedList<NodeRenderable> childsRLine = null;
      LinkedList<NodeRenderable> childsRLineNext = null;
      NodeRenderable rLine = null;
      
      childsRLine = nodeAcc.getChilds();
   
      while(i < prevList.size()){ //busca el siguiente para colapsar,
                  	
         next = prevList.get(i);  //obtiene el siguiente	
         type = next.getType();
         	
         if(!type.equals("RLine")){
            i++;
            break;
         }
          
          //es un RLine  
         label = next.getLabel();
         	
         if(!label.equals("no label")){
            i++;
            break;
         }
            
          //es un RLine sin etiqueta, obtine sus hijos
         childsRLineNext = next.getChilds();       
         NodeRenderable child = null;
                     	
         for(int r = 0; r < childsRLineNext.size(); r++){
            child = childsRLineNext.get(r);
            child.setFather(nodeAcc);
            childsRLine.add(child);
         }
         
         prevList.remove(i);      
      }
   
   //aqui ya se tiene la lista de elementos de la RLine final 
      
      if(initial){ //si es la concatenación inicial y no la concatenacion de rlines por concatnacion de rblocks
      //System.out.println("Collapsing words from this List");
      //para prueba, se pueden concatenar las RWord
         collapseRWords(childsRLine);   
         //showList(childsRLine);     
      }
                           
      if(!simple && (childsRLine.size() > 0)){
         String etiq = RLineLabeler.label(childsRLine);
         if(etiq != null )
            nodeAcc.setLabel(etiq); 
      }
      
      return i;
   }
   
   public static void collapseRLines(LinkedList<NodeRenderable> prevList, boolean simple, boolean initial){
   //concatena el contenido de las RLines consecutivas en esta lista
   //toma la primera lista como fija y entonces recorre todas las siguientes 
   //pasando su contenido a la de referencia
   
      int i = 0;      
      while( i < prevList.size()){
         Vector data = findFirstRLine(i, prevList);   
         NodeRenderable nodeAcc = (NodeRenderable)data.elementAt(0);
         i = (int)data.elementAt(1);
      
         if(nodeAcc == null) 
            break;
         i = concatenarRLines(i+1, prevList, nodeAcc, simple, initial);
      }     
   }
   
   private static int concatenarRWords(int i, LinkedList<NodeRenderable> prevList){
   //apartir de i, concatenar todas las Rwords consecutivas, separadas por si acaso por RBlank
      
      Vector data = findFirstRWord(i, prevList);//busca la primera RWord para hacer las concatenaciones sobre esa 
      RWord rw = (RWord)data.get(0);
      i = (Integer)data.get(1);
   
      if(rw == null) //no hay RWords que concatenar
         return prevList.size();
   
      //buscar sobre esta RWord de refencia y hacer las concatenaciones
      NodeRenderable node = null;
      Renderable ren = null;   
      Renderable renNext = null;
      
      i++;
      while(i < prevList.size() ){
         node = prevList.get(i);
         ren = node.getRenderable();
         if( ren instanceof RStyleChanger ){
            prevList.remove(i);
            //verificar si la etiqueta encontrada es válida
            if(!isValidLabel(rw.getAsLabel())){
               prevList.remove(i-1);
               //System.out.println("+++++ ++ La etiqueta " + rw.getAsLabel() + " no es valida");
               return (i-1);
            }
            else
               return i;//terminamos cuando encontramos RStyleChanger
         }
         else if( ren instanceof RBlank ){
            prevList.remove(i);
            continue;
         }
         else if( ren instanceof RWord ){
            RWord rwNext = (RWord)ren;
            rw.setWord(rwNext.getWord());
            LinkedList<String> vwords = rwNext.getAllWords();
            if(vwords != null)
               for(int j = 0; j < vwords.size(); j++){
                  rw.setWord(vwords.get(j));
               }      
            prevList.remove(i);
            continue;
         }      
      }
      return i;
   }
   public static void collapseRWords(LinkedList<NodeRenderable> prevList){
   
    //concatenar Rwords consecutivos o separados por RBlank
    //al encontrar un StyleChanger, iniciar la concatenación de una
    //nueva Rlabel 
      int i = 0;   
      while (i < prevList.size()) 
         i = concatenarRWords(i, prevList);
   }      
      //regresa el tipo del siguiente renderable al actual en la posicion i dentro de un arreglo de hijos   
   public static void collapseRBlocksConsecutivos(LinkedList<NodeRenderable> prevList){
   //se recibe una lista de hijos de RBlock, se concatenan solo los RBlock consecutivos 
   //y que no esten etiquetados
   
      if(prevList.size() == 0) 
         return;
     
      //System.out.println("***** Collapsing consecutive blocks in this main block") ;
     
      int i = 0;
      NodeRenderable cell = null;
      Renderable ren = null;
   	
      NodeRenderable cellNext = null;
      String label = null;
      LinkedList<NodeRenderable> childInCell = null;   
      LinkedList<NodeRenderable> childInCellNext = null;
   
                 
      while(i < prevList.size()){
         cell = prevList.get(i);
         ren = cell.getRenderable();
      	
         if(!(ren instanceof RBlock || ren instanceof RTableCell)){
            i++;
            continue;//sigue buscando otro libre
         }     
         label = cell.getLabel();
         
         if(!label.equals("no label")){
            i++;     
            continue;
         }      		   
         i++;   	
         boolean modified = false;
      		
         while(i <  prevList.size()){
         //busca el siguiente cell y lo concatena con el anterior
            cellNext = prevList.get(i);   
            ren = cellNext.getRenderable();
         
            if(!(ren instanceof RBlock || ren instanceof RTableCell)){
               i++;
               break;//termina y consigue un nuevo block acumulador
            }
            
            label = cellNext.getLabel();
            if(!label.equals("no label")){
               i++;     
               break;//termina y consigue un nuevo block acumulador
            }      		
         
         //pasa los hijos de la celda actual a la siguiente   
            childInCell = cell.getChilds();   
            childInCellNext = cellNext.getChilds();
            NodeRenderable child = null;
         
            for(int j = 0; j < childInCellNext.size(); j++){
               child = childInCellNext.get(j);
               child.setFather(cell);
               childInCell.add(child);
            }
         //remueve el nodo actual
         
            modified = true;
            prevList.remove(i);
         }
         
         if(modified){
            childInCell = cell.getChilds();
            collapseRLines(childInCell, false,false);   //colapsa rlines sin colapsar etiquetas y sin etiquetar
            BlockUtil.correctLabelInRBlock(cell);
         }   
         i++; 
      }
               //System.out.println("**** Ending Collapsing consecutive blocks in this main block") ;
   }

   public static boolean isValidLabel(String posibleLabel){   
      //return posibleLabel.matches("([A-Za-z-/]+( |))+[0-9-]*");
      return posibleLabel.matches(".*[A-Za-z]+.*");
      //return posibleLabel.matches("[A-Za-z]+");
   }
   
   public static void colapseRLabels(LinkedList<NodeRenderable> prevList){
      
      //concatenar RLabels consecutivos
    //al encontrar un node distinto a Label, iniciar la concatenación de una
    //nueva Rlabel 
      int i = 0;   
      while (i < prevList.size()) 
         i = concatenarRLabels(i, prevList);
   }   
   
   private static int concatenarRLabels(int i, LinkedList<NodeRenderable> prevList){
   //apartir de i, concatenar todas las RLabels consecutivas
      
      Vector data = findFirstRLabel(i, prevList);//busca la primera RLabel
      NodeRenderable nodeAcc = (NodeRenderable)data.get(0);
      i = (Integer)data.get(1);
   
      if(nodeAcc == null) //no hay RLabels que concatenar
         return prevList.size();
   
      //buscar siguientes RLabel a partir de la posición i y hacer
      //las posibles concatenaciones
      Renderable ren = nodeAcc.getRenderable(); 
      RWord rw = (RWord)ren;
      
      NodeRenderable node = null;
      String type = null;   
      Renderable renNext = null;
      
      i++;
      while(i < prevList.size() ){
         node = prevList.get(i);
         type = node.getType();
         if( !type.equals("label") ){ 
            break;
         }
         else{
            ren = node.getRenderable();
            RWord rwNext = (RWord)ren;
            rw.setWord(rwNext.getAsLabel());      
            prevList.remove(i);
            continue;
         }      
      }
      
       //verificar si la etiqueta encontrada es válida
      if(!isValidLabel(rw.getAsLabel())){
         prevList.remove(i-1);
         //System.out.println("+++++ ++ La etiqueta " + rw.getAsLabel() + " no es valida");
         return i;
      }
      else
         return (i+1);//terminamos             
   }
}