package com.renderTree;
//Incluye union de Rwords en una sola Rlabel 
import org.lobobrowser.html.renderer.*;
 import java.util.*;
 /**
Clase auxiliar que realiza varias operaciones sobre un objeto del tipo Noderenderable durante el proceso
de etiquetado y podado del arbol de renderizado.
*/
	  
public class BlockUtil{


   public static boolean hasUIorBlock(NodeRenderable node){
   //recibe un RTableCell
   //regresa true si el RtableCell contiene un UI o es un grupo no etiquetado
   //regresa false si este tablecell contiene una etiqueta
   
      if(BlockUtil.hasLabel(node))
         return false;
   
      return true;
   }

   //agregar metodo para verificar si el container tiene o no un UI etiqueta, si lo tiene, ya no se colapsa con otros containers
      

   public static boolean hasUINotLabeled(NodeRenderable node){
   //regresa true si este nodo tiene un UI no etiquetado
      String label = node.getLabel();
      if(!label.equals("no label") )
         return false;
   
      Renderable ren = node.getRenderable();
      if(ren instanceof RUIControl)
         return true;
      else 
         return false;   
   }
   
   public static boolean hasUniqueUILabeled(NodeRenderable node){
   
      LinkedList<NodeRenderable> prevList = node.getChilds();
      if(prevList == null) 
         return false;
      if(prevList.size() == 0) 
         return false;
   
      NodeRenderable n = prevList.get(0);
      String type = n.getType();
      if(!type.equals("RLine")) 
         return false;
      prevList = n.getChilds();
      if(prevList == null) 
         return false;
      if(prevList.size() != 1) 
         return false;
      n = prevList.get(0);
      Renderable ren = n.getRenderable();
   
      if(! (ren instanceof RUIControl)) 
         return false;
      String label = n.getLabel();
      if(label.equals("no label")) 
         return false;
      else
         return true;
   }
   public static boolean hasLabel(NodeRenderable node){
   //regresa true si es un UI no etiquetado o si contiene a un UI no etiquetado
   
      String type = node.getType();
   
      if(type.equals("label")) 
         return true;
               
      LinkedList<NodeRenderable> childs = node.getChilds();
      if(childs == null) 
         return false;
      
      if(childs.size() != 1) 
         return false;
      
      return (hasLabel(childs.get(0)));
   }


   public static RWord labelInRenderable(NodeRenderable node){
   //verifica si este nodeRenderable es o tiene una label
   //Posibles casos:
   // 1) Es un label
   // 2) Es RLine que contiene label
   // 3) Es RBlock que contiene RLine que contiene label
   // 4) Es un container que contiene en el nivel mas bajao un label
      if(node == null) 
         return null;
   
      NodeRenderable child = node;
   
      String type = child.getType();
      LinkedList<NodeRenderable> childs = null;
           
      while(!type.equals("label")){
         childs = child.getChilds();
         if(childs == null) 
            return null;
            
         if(childs.size() == 0 || childs.size() > 1) 
            return null;
        
         child = childs.get(0);
         type = child.getType();    
      }
   
      if(type.equals("label")){
         //System.out.println("+++++++++++ Label in this NODE");
         //showRenderableTree(node,0);
         RWord rw = (RWord)(child.getRenderable());
         return rw;
      }
   
      return null;
   }


   public static NodeRenderable radioInRenderable(NodeRenderable node){
   //verifica si este nodeRenderable es o tiene un radio
   //Posibles casos:
   // 1) Es un radio o chechbox
   // 2) Es RLine que contiene radio o checbox
   // 3) Es RBlock que contiene RLine que contiene radio o checbox
   // 4) Es un container que contiene en el nivel mas bajo un radio o checbox
      if(node == null) 
         return null;
   
      NodeRenderable child = node;
   
      String type = child.getType();
      LinkedList<NodeRenderable> childs = null;
           
      while(!(type.equals("radio") || type.equals("checkbox"))){
         childs = child.getChilds();
         if(childs == null) 
            return null;
            
         if(childs.size() == 0 || childs.size() > 1) 
            return null;
        
         child = childs.get(0);
         type = child.getType();    
      }
   
      if(type.equals("radio") || type.equals("checkbox"))
         return child;
      
   
      return null;
   }

   public static boolean labeled(NodeRenderable node){
   //Indica si este nodeRenderable esta o no etiquetado
      String label = node.getLabel();
      return(!label.equals("no label"));
   }

   public static void simplyfyBlock(NodeRenderable block){
   //absorbe block consecutivos a partir del block principal
      
      if(block == null) 
         return;
            
      LinkedList<NodeRenderable> childs = block.getChilds();
      if(childs == null) 
         return;
      if(childs.size() != 1) 
         return;
      
      //obtiene el hijo de este block
      NodeRenderable child = childs.get(0);
      Renderable ren = child.getRenderable();
         
      if ( !(ren instanceof RBlock)) 
         return;
      
     //tenemos un rblock como unico hijo
      LinkedList<NodeRenderable> prevList = block.getChilds();  
      childs = child.getChilds();
      
      prevList.remove(0);
      for(int j = 0; j < childs.size(); j++){
         child =  childs.get(j);
         child.setFather(block);
         prevList.add(child);
      }
      
   }

   public static void correctLabelInRBlock(NodeRenderable rblock){
   //Esta funcion hace una correccion sobre la etiqueta para un RBlock.
   //Si el RBlock trae RLines, donde dicho RLines se compone de varios elementos pero el primero es el unico etiquetado, entonces esa etiqueta pasa a ser etiqueta del grupo
   
      //System.out.println("***********  CALLING CORRECT RTABLECELL ************");
      //showRenderableTree(rblock, 0);
   //caso en el que en el RBlock existe un grupo de UI dentro de un RLine, y donde el primer UI tiene etiqueta pero los restantes no la tienen
      Renderable ren = rblock.getRenderable();
      LinkedList<NodeRenderable> childs = rblock.getChilds(); //se esperan hijos del tipo RLine
      LinkedList<NodeRenderable> childsRLine = null;
      Renderable renRLine = null;
      NodeRenderable child = null;
      NodeRenderable ui = null;
      
      String label = rblock.getLabel();
      //if(!label.equals("no label")) 
         //return;  //si el bloque ya esta etiquetado no hay nada que hacer
   	
      if(childs.size() == 0) //si no hay RLines en el RBlock donde hacer las busquedas
         return;
   
      for(int i = 0; i < childs.size(); i++){  //checa cada RLine
      
         child = childs.get(i);
         renRLine = child.getRenderable();
      
         if(!(renRLine instanceof RLine)) 
            continue;
            
         childsRLine = child.getChilds();   
      
         if(childsRLine == null) 
            continue;
         if(childsRLine.size() == 0) 
            continue;
      	
      	//verifica el contenido del RLine
         if(childsRLine.size() > 1){ //al menos dos UIs
            ui = childsRLine.get(0);
            String posibleBlockLabel = ui.getLabel();
            //System.out.println("*******************  POSSIBLE LABEL ::: " + posibleBlockLabel);
            if(!posibleBlockLabel.equals("no label")){
               boolean noMoreLabels = true;
               int k = 1;
               while( k < childsRLine.size()){//revisa que todos los demas UI no tengan etiquea
                  ui = childsRLine.get(k);
                  k++;
                  label = ui.getLabel();
                  if(!label.equals("no label")){
                     noMoreLabels = false;
                     break;
                  }
               }
               if(!noMoreLabels) 
                  return;
                  
               ui = childsRLine.get(0);
               ui.setLabel("no label");
               rblock.setLabel(posibleBlockLabel);
            }
         }
      }
   }
	 
   public static boolean isBlockEmpty(NodeRenderable block){
      LinkedList<NodeRenderable> prevList = block.getChilds();
      if(prevList.size() == 0) 
         return true;
      else 
         return false;
   }  
                
   public static void absorbeRBlocksAnidados(NodeRenderable block){
   //se recibe una lista de hijos de RBlock, 
   //solo considera el caso en que el unico hijo de este block es otro rblock
   //los hijos del rblock hijo pasan a ser hijos del nodo actual
   
      LinkedList<NodeRenderable> prevList = block.getChilds();
      String labelMain = block.getLabel();
   
      if(prevList.size() != 1) 
         return;
      
      NodeRenderable cell = prevList.get(0);//el unico hijo
      Renderable ren = cell.getRenderable();
   	
      if(!(ren instanceof RBlock) && !(ren instanceof RTable))//el unico hijo debe ser un rblock o rtable
         return;
      
      
      String label = cell.getLabel(); //la etiqueta del nodo hijo
      
   	//si los dos bloques estan etiquetados entonces no se puede absorber
   	
      if(!labelMain.equals("no label") && !label.equals("no label"))
         return;
   	
      LinkedList<NodeRenderable> childsSon = cell.getChilds();
         	
      NodeRenderable child = null;
     
      for(int j = 0; j < childsSon.size(); j++){
         child =  childsSon.get(j);
         child.setFather(block);
         prevList.add(child);
      }
         //remueve el hijo actual
      prevList.remove(0);
      
      
      if(labelMain.equals("no label"))   
         block.setLabel(label);   
      	
      //System.out.println("After absorbing, main label is: " + block.getLabel());
      //System.out.println("ENDS CASE 9");
                 
   }

}