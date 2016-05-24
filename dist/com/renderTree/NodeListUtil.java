package com.renderTree;
/**
Clase auxiliar que realiza varias operaciones sobre una lista de objetos del tipo Noderenderable durante el proceso
de etiquetado y podado del arbol de renderizado.
*/
import java.util.*;
import org.lobobrowser.html.renderer.*;

 
public class NodeListUtil{
//regresa el tipo del siguiente renderable al actual en la posicion i dentro de un arreglo de hijos
   public static String typeNearRenderable(LinkedList<NodeRenderable> prevList, int i){
      NodeRenderable nren= null;
      if(i>=0 && i <prevList.size() ){
         nren = prevList.get(i);
         String type = nren.getType();
         return type;
      }
      
      return null;
   
   }
 
   public static void showList(LinkedList<NodeRenderable> prevList){
   
      if(prevList.size() == 0) 
         return;
     
      System.out.println("***** SHOWING LIST") ;
      for(int i = 0; i < prevList.size(); i++){
         PrunedTreeBuilder.showRenderableTree(prevList.get(i), 0);
      }
      System.out.println("***** END  SHOWLIST") ;
      
   }
      
 	/*
   public static boolean tryAssignLabel(int i, LinkedList<NodeRenderable> prevList, NodeRenderable uiRen){
   	 
   	 
      if(i >= prevList.size() || i < 0) 
         return false;
               
      NodeRenderable next = prevList.get(i);
      Renderable ren = next.getRenderable();
                  
      if(ren instanceof RBlock){
         RWord rw = BlockUtil.labelInRenderable(next);
         if(rw != null){
            uiRen.setLabel(rw.getWord(),rw.getAllWords());
            prevList.remove(i);
            return true;
         }
      }
      else if(ren instanceof RLine){       	
         LinkedList<NodeRenderable> childs = next.getChilds();    
         if(childs.size()>1){
            return false;
         }
                  
         NodeRenderable plb = childs.get(0);                //debe ser el primer elemento de este RLine
         String type = plb.getType();
         if(type.equals("label")){
            String str = next.getLabel();
            if(!str.equals("no label")){
               uiRen.setLabel(str,null);
               prevList.remove(i);
               return true;
            }
         	
            RWord rw = (RWord)(plb.getRenderable());   
            uiRen.setLabel(rw.getWord(),rw.getAllWords());
            prevList.remove(i);
            return true;
         }
      }
      else if(ren instanceof RWord){
         RWord rw = (RWord)ren;   
         uiRen.setLabel(rw.getWord(),rw.getAllWords());
         prevList.remove(i);
         return true;
      }
      else{
         return false;
      }
                  
      return false;	 	 
   }  
   */
   public static void asignarLabelCaso9(LinkedList<NodeRenderable> prevList){
   //la lista trae containers, 
   //se busca el patron container con Label seguido de otro container
   //se localiza el container con la label
   //todos los containers siguientes se colapsan y la etiqueta se asigna a la lista resultante
     
      NodeRenderable container = null;
      String typeElement = null;
      NodeRenderable next = null;
      
      LinkedList<NodeRenderable> childsRLine = null;      
      NodeRenderable possibleLabel = null;
      String type=null;
      Renderable lb1=null;
      RWord lb=null;
      int i = 0;
        
      while(i < prevList.size()){
        //busca un container con label, 
         while( i < prevList.size()){
            lb = BlockUtil.labelInRenderable(prevList.get(i));
            if(lb != null) 
               break;
            else i++;
         }
      
         if((i+1) >= prevList.size()){ 
            break;//no encontro ninguna label o no hay siguiente elemento para asignar esta label
         }
       
      //recupera el siguiente posible container para etiquetar
         container = prevList.get(i+1);
         Renderable ren = container.getRenderable();
         //si el siguiente elemento no es un container, terminar y repetir el proceso
         if(!(ren instanceof RBlock || ren instanceof RTable || ren instanceof RLine)){
            i+=2;
            continue;
         }
         
         //el siguiente elemento es un container, intenta agrupar los container consecutivos a este
         int j = i + 2;
         LinkedList<NodeRenderable> childsContainer = container.getChilds();
         LinkedList<NodeRenderable> childNext = null;
         String label = null;
      	          		
         while( j < prevList.size()){
            next = prevList.get(j);
            ren = next.getRenderable();
         
            if(!(ren instanceof RBlock || ren instanceof RTable || ren instanceof RLine))
               break;
         
            label = next.getLabel();//descartar si es un bloque que ya esta etiquetado
            if(!label.equals("no label"))
               break;
               
            if(BlockUtil.labelInRenderable(next) != null)	
               break;               //descartar si es bloque que contine una etiqueta
               
            //otra condición, contine un UI ya etiquetado    
            if( BlockUtil.hasUniqueUILabeled(next))	
               break;               //descartar si es bloque con UI unico ya etiquetado   
            
            //System.out.println("Probando la funcion ");
            //PrunedTreeBuilder.showRenderableTree(next,0);
            //System.out.println("Termina");
         	//obtiene los hijos de este container y los agrega a los hijos del container base
            childNext = next.getChilds();
            
            if(childNext != null)
               for(int k = 0; k < childNext.size(); k++)
                  childsContainer.add(childNext.get(k));
               //elimina el renderable colapsado
            prevList.remove(j);   	
         }
      
         container.setLabel(lb.getAsLabel());   
         prevList.remove(i);//borra la etiqueta
         i++;
      }
   }

   public static int labelsInThisList(LinkedList<NodeRenderable> prevList){
   //Retorna el numero de Labels en esta lista
      int i = 0;
      NodeRenderable cell = null;
      int num = 0;
   	
      while(i < prevList.size()){
         cell = prevList.get(i);
         if(BlockUtil.labelInRenderable(cell) != null) 
            num++;
            
         i++;
      }
   
      return num;
   }
   
   public static void cleanList(LinkedList<NodeRenderable> prevList){
   	//elimina las entradas vacias en este block
      int i = 0;
      NodeRenderable child = null;
      LinkedList<NodeRenderable> childs = null;
   	
      while(i < prevList.size()){
         child = prevList.get(i);
         childs = child.getChilds();
      
         if(childs == null){
            i++;
            continue;
         }
         
         if(childs.size() == 0)
            prevList.remove(i);
         else
            i++;
      }
   }
		
   public static void removeEmptyContainers(LinkedList<NodeRenderable> prevList){
      //para cada container vacio, se elimina
      int i = 0;
      String type = null;
      NodeRenderable node = null;
      LinkedList<NodeRenderable> childs = null;
      while(i < prevList.size()){
         node = prevList.get(i);
         type = node.getType();
         if(type.equals("RBlock") || type.equals("RLine") || type.equals("RTable")){
            childs = node.getChilds();
            if(childs.size() == 0){
               prevList.remove(i);
               continue;
            }
         }
      
         i++;
      }
   }
   
   /*   
   public static void resolverRBlockContent(LinkedList<NodeRenderable> prevList){
   //La lista contiene Rlines, se intenta resolver las etiquetas contenidos en esas 
   //listas para UIs contenidas en esa misma lista
      if(prevList.size() == 0) 
         return;
         
      RLineResolver3.resolveRLine(prevList);
   }
		*/
       

}