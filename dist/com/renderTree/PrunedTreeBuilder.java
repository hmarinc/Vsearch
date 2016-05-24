package com.renderTree;
//Incluye union de Rwords en una sola Rlabel 
import com.clustering.*;

import org.w3c.dom.* ;
import org.lobobrowser.html.domimpl.*;
import org.lobobrowser.html.renderer.*;
import java.util.* ;   
import java.io.*;	  


public class PrunedTreeBuilder{      
      
   public static void showRenderableTreeByNumber(NodeRenderable root, int espacios){
   
   //despliega la info del nodo actual y el de sus childs
      if(root == null) 
         return;
   
      String type = root.getType();
      String label = root.getLabel();
      int numNode = root.getNumNodo();
   
      Renderable ren = root.getRenderable();
      LinkedList<NodeRenderable> childs = root.getChilds();
               
      for(int j = 0; j< espacios; j++) System.out.print(" ");   
      System.out.println(type + "("+numNode+"): -  "+label);
   
      if(childs != null)
            
         for(int i = 0; i < childs.size(); i++)
            showRenderableTreeByNumber(childs.get(i), espacios+3);   
   }

   public static void showRenderableTree(NodeRenderable root, int espacios, PrintWriter printWriter){
   //se guarda la salida en un archivo
   //despliega la info del nodo actual y el de sus childs
      if(root == null) 
         return;
   
      String type = root.getType();
      String label = root.getLabel();
      Renderable ren = root.getRenderable();
      LinkedList<NodeRenderable> childs = root.getChilds();
   
      if (ren instanceof RLine){
         
         for(int j = 0; j< espacios; j++) printWriter.print(" ");   
         printWriter.println("RLine ("+childs.size()+"): -  "+label);
         
         for(int i = 0; i < childs.size(); i++)
            showRenderableTree(childs.get(i), espacios+3,printWriter);
      }
      else if (ren instanceof RBlock){
         for(int j = 0; j< espacios; j++) printWriter.print(" ");
        
         if(ren instanceof RTableCell){
            RTableCell rtc = (RTableCell)ren;					
            printWriter.println("RBlock: TBC(" + rtc.getVirtualRow() + "<"+rtc.getRowSpan()+">," + rtc.getVirtualColumn() + "<"+rtc.getColSpan()+">) - " + root.getLabel());
         
         }
         else
            printWriter.println("RBlock: - "+root.getLabel() );//+ ((RBlock)ren).getModelNode());
      
         for(int i = 0; i < childs.size(); i++)
            showRenderableTree(childs.get(i), espacios+3,printWriter);
      
      	                
      }
      else if (ren instanceof RTable){ 
         for(int j = 0; j< espacios; j++) printWriter.print(" ");
         printWriter.println("RTable: - " + root.getLabel());
      
         if(childs != null)
            for(int i = 0; i < childs.size(); i++)
               showRenderableTree(childs.get(i), espacios+3,printWriter);
      
      }
      else if (ren instanceof RWord){  
         for(int j = 0; j< espacios; j++) printWriter.print(" ");
         printWriter.println(ren);   
      }
      else if (ren instanceof RUIControl){
      
         for(int j = 0; j< espacios; j++) printWriter.print(" ");
      
         if(type.equals(""))
            printWriter.println("UI: "+ ((RUIControl)ren).getModelNode());
         else{
         
            if(type.equals("radio")){
               LinkedList<String> list = root.getValues();
               try{
                  String str = list.get(0);
                  for(int i = 1; i < list.size(); i++)
                     str += ", " + list.get(i);
                  
                  printWriter.println("UI: "+ type + " - " + label);
               	
                  for(int j = 0; j< espacios+10; j++) printWriter.print(" ");
                  printWriter.println("{" + str + "}");
               }
               catch(Exception e){
                  printWriter.println("UI: "+ type + " - " + label );
               }
            }
            else
               printWriter.println("UI: "+ type + " - " + label);
         }
      }
      else if (ren instanceof RBlank){
         for(int j = 0; j< espacios; j++) printWriter.print(" ");
         printWriter.println("Blank");
      }
      else if (ren instanceof RStyleChanger){
         for(int j = 0; j< espacios; j++) printWriter.print(" ");
         printWriter.println("StyleChanger");
      
      }
      else if (ren instanceof RGroup){
         for(int j = 0; j< espacios; j++) printWriter.print(" ");   
         printWriter.println("RGroup ("+childs.size()+"): -  "+label);
         
         for(int i = 0; i < childs.size(); i++)
            showRenderableTree(childs.get(i), espacios+3,printWriter);
      
      }
      else{	   	       
         
         for(int j = 0; j< espacios; j++) printWriter.print(" ");
         printWriter.println(ren);
         
      }
   
   }

   public static void showRenderableTree(NodeRenderable root, int espacios){
   
   //despliega la info del nodo actual y el de sus childs
      if(root == null) 
         return;
   
      String type = root.getType();
      String label = root.getLabel();
      Renderable ren = root.getRenderable();
      LinkedList<NodeRenderable> childs = root.getChilds();
   
      if (ren instanceof RGroup){
         for(int j = 0; j< espacios; j++) System.out.print(" ");   
         System.out.println("RGroup ("+childs.size()+"): -  "+label);
         
         for(int i = 0; i < childs.size(); i++)
            showRenderableTree(childs.get(i), espacios+3);
      }
      else if (ren instanceof RLine){
         
         for(int j = 0; j< espacios; j++) System.out.print(" ");   
         System.out.println("RLine ("+childs.size()+"): -  "+label);
         
         for(int i = 0; i < childs.size(); i++)
            showRenderableTree(childs.get(i), espacios+3);
      }
      else if (ren instanceof RBlock){
         for(int j = 0; j< espacios; j++) System.out.print(" ");
        
         if(ren instanceof RTableCell){
            RTableCell rtc = (RTableCell)ren;					
            System.out.println("RBlock: TBC(" + rtc.getVirtualRow() + "<"+rtc.getRowSpan()+">," + rtc.getVirtualColumn() + "<"+rtc.getColSpan()+">) - " + root.getLabel());
         
         }
         else
            System.out.println("RBlock: - "+root.getLabel() );//+ ((RBlock)ren).getModelNode());
      
         for(int i = 0; i < childs.size(); i++)
            showRenderableTree(childs.get(i), espacios+3);
      
      	                
      }
      else if (ren instanceof RTable){ 
         for(int j = 0; j< espacios; j++) System.out.print(" ");
         System.out.println("RTable: - " + root.getLabel());
      
         if(childs != null)
            for(int i = 0; i < childs.size(); i++)
               showRenderableTree(childs.get(i), espacios+3);
      
      }
      else if (ren instanceof RBlank){
         for(int j = 0; j< espacios; j++) System.out.print(" ");
         System.out.println("Blank");
      }
      else if (ren instanceof RStyleChanger){
         for(int j = 0; j< espacios; j++) System.out.print(" ");
         System.out.println("StyleChanger");
      
      }
      else if (ren instanceof RGroup){
         for(int j = 0; j< espacios; j++) System.out.print(" ");   
         System.out.println("RGroup ("+childs.size()+"): -  "+label);
         
      }
      else if (ren instanceof RWord){  
         for(int j = 0; j< espacios; j++) System.out.print(" ");
         System.out.println(ren);   
      }
      else if (ren instanceof RUIControl){
      
         for(int j = 0; j< espacios; j++) System.out.print(" ");
      
         if(type.equals(""))
            System.out.println("UI: "+ ((RUIControl)ren).getModelNode());
         else{
         
            if(type.equals("radio")){
               LinkedList<String> list = root.getValues();
               try{
                  String str = list.get(0);
                  for(int i = 1; i < list.size(); i++)
                     str += ", " + list.get(i);
                  
                  System.out.println("UI: "+ type + " - " + label);
               	
                  for(int j = 0; j< espacios+10; j++) System.out.print(" ");
                  System.out.println("{" + str + "}");
               }
               catch(Exception e){
                  System.out.println("UI: "+ type + " - " + label );
               }
            }
            
            else
               System.out.println("UI: "+ type + " - " + label);
         }
      }
      else{	   	       
         
         //for(int j = 0; j< espacios; j++) System.out.print(" ");
         //System.out.println(ren);
         
      }
   }
			
   public static void showPrunedTreeNewLabel(NodeRenderable root, int espacios){
   
   //despliega la info del nodo actual y el de sus childs
      if(root == null) 
         return;
   
      String type = root.getType();
      String label = root.getNewLabel();
      Renderable ren = root.getRenderable();
      LinkedList<NodeRenderable> childs = root.getChilds();
   
      if (ren instanceof RLine){
         
         for(int j = 0; j< espacios; j++) System.out.print(" ");   
         System.out.println("RLine ("+childs.size()+"): -  "+label);
         
         for(int i = 0; i < childs.size(); i++)
            showPrunedTreeNewLabel(childs.get(i), espacios+3);
      }
      else if (ren instanceof RBlock){
         for(int j = 0; j< espacios; j++) System.out.print(" ");
        
         if(ren instanceof RTableCell){
            RTableCell rtc = (RTableCell)ren;					
            System.out.println("RBlock: TBC(" + rtc.getVirtualRow() + "<"+rtc.getRowSpan()+">," + rtc.getVirtualColumn() + "<"+rtc.getColSpan()+">) - " + label);
         
         }
         else
            System.out.println("RBlock: - "+label );//+ ((RBlock)ren).getModelNode());
      
         for(int i = 0; i < childs.size(); i++)
            showPrunedTreeNewLabel(childs.get(i), espacios+3);
      
      	                
      }
      else if (ren instanceof RTable){ 
         for(int j = 0; j< espacios; j++) System.out.print(" ");
         System.out.println("RTable: - " + label);
      
         if(childs != null)
            for(int i = 0; i < childs.size(); i++)
               showPrunedTreeNewLabel(childs.get(i), espacios+3);
      
      }
      else if (ren instanceof RWord){  
         for(int j = 0; j< espacios; j++) System.out.print(" ");
         System.out.println(ren);   
      }
      else if (ren instanceof RUIControl){
      
         for(int j = 0; j< espacios; j++) System.out.print(" ");
      
         if(type.equals(""))
            System.out.println("UI: "+ ((RUIControl)ren).getModelNode());
         else{
         
            if(type.equals("radio")){
               LinkedList<String> list = root.getValues();
               try{
                  String str = list.get(0);
                  for(int i = 1; i < list.size(); i++)
                     str += ", " + list.get(i);
                  
                  System.out.println("UI: "+ type + " - " + label);
               	
                  for(int j = 0; j< espacios+10; j++) System.out.print(" ");
                  System.out.println("{" + str + "}");
               }
               catch(Exception e){
                  System.out.println("UI: "+ type + " - " + label );
               }
            }
            else
               System.out.println("UI: "+ type + " - " + label);
         }
      }
      else{	   	       
         
         //for(int j = 0; j< espacios; j++) System.out.print(" ");
         //System.out.println(ren);
         
      }
   }
               
   public static NodeRenderable buildTreeRenderables(Renderable root, NodeRenderable father, boolean simple){
   //la bandera simple indica si el arbol se construye:
   // --> simple (sin etiquetar y solo para visualizar como esta represetnado el formulario un arbol render)
   // --> completo (Se hace el podado y etiquetado del arbol)
   
      NodeRenderable newRenderable = null;
   
      if (root instanceof RLine){
         Iterator itr = ((RLine)root).getRenderables();	
      
         LinkedList<NodeRenderable> prevList = new LinkedList<NodeRenderable>();
         NodeRenderable newRenderableChild = null;
      
         while(itr.hasNext()){
            Renderable ren = (Renderable)itr.next();
            newRenderableChild = buildTreeRenderables(ren,null,simple);
            if(newRenderableChild != null){
               prevList.add(newRenderableChild);
            }	
         }  
      
         if(prevList.size()>0){ 
         //todos los hijos de un RLine son hojas, deben ser o etiquetas o UIs           	
            newRenderable = new NodeRenderable(root, "RLine");
            newRenderable.setFather(father);         
            for (int i = 0; i < prevList.size(); i++){
               newRenderableChild = prevList.get(i);
               newRenderableChild.setFather(newRenderable);
               newRenderable.addChild(newRenderableChild);
            }
         }
      }
      else if (root instanceof RBlock){
         Iterator itr1 = ((RBlock)root).getRenderables();	
         Iterator itr = ((RBlockViewport)itr1.next()).getRenderables();
      
         LinkedList<NodeRenderable> prevList = new LinkedList<NodeRenderable>();
         NodeRenderable newRenderableChild = null;
      
         while(itr.hasNext()){
            Renderable ren = (Renderable)itr.next();
            newRenderableChild = buildTreeRenderables(ren,null,simple);
            if(newRenderableChild != null)
               prevList.add(newRenderableChild);	
         }  
      
         if(prevList.size()>0){
         
            Collapser.collapseRLines(prevList,simple,true);   //simple = false, initial = true
            //se descartan los RLines o contenedores vacios
            if(!simple)//render completo, etiquetar
               NodeListUtil.removeEmptyContainers(prevList);
               
            if(root instanceof RBlock){   
               if(!simple){
                  NodeListUtil.asignarLabelCaso9(prevList);//Patron BLOC_Label seguido de BLOCKs
                  Collapser.collapseRBlocksConsecutivos(prevList);// si el rblock contiene dos rblocks consecutivos sin etiqueta se colapsan   
               }
               newRenderable = new NodeRenderable(root, "RBlock");    
            }            	     
            else
               newRenderable = new NodeRenderable(root, "RTableCell"); 
         
            if(prevList.size() == 0) 
               return null;//una celda vacia no sirve de nada
                     
            newRenderable.setFather(father);
           
            for (int i = 0; i < prevList.size(); i++){
               newRenderableChild = prevList.get(i);
               newRenderableChild.setFather(newRenderable);
               newRenderable.addChild(newRenderableChild);
            }   
         	                    
         //Si este Block1 contiene a otro unico Block2, los hijos de Block2 pasan a ser los hijos de Block1  
            if(!simple)
               if(prevList.size() == 1)
                  BlockUtil.absorbeRBlocksAnidados(newRenderable);   
         }
      }
      else if (root instanceof RTable){ 
        
         Iterator itr = ((RTable)root).getRenderables();
         LinkedList<NodeRenderable> prevList = new LinkedList<NodeRenderable>();
         NodeRenderable newRenderableChild = null;
      
         while(itr.hasNext()){
            Renderable ren = (Renderable)itr.next();   
            newRenderableChild = buildTreeRenderables(ren, null,simple);
         
            if(newRenderableChild != null)
               prevList.add(newRenderableChild);	
         }
      
         if(prevList.size()>0){   
            //System.out.println("Tabla de entrada para  etiquetar");
            //NodeListUtil.showList(prevList);
            
            if(!simple){
               TableLabeler.asignarEtiquestasEnTable(prevList);      
               NodeListUtil.asignarLabelCaso9(prevList);   
               Collapser.collapseRBlocksConsecutivos(prevList);
            }
            
            if(prevList.size() > 0){             	 
               newRenderable = new NodeRenderable(root, "RTable"); 
               newRenderable.setFather(father);
                        	        
               for (int i = 0; i < prevList.size(); i++){
                  newRenderableChild = prevList.get(i);
                  newRenderableChild.setFather(newRenderable);
                  newRenderable.addChild(newRenderableChild);
               }
            
               if(!simple)
                  if(prevList.size() == 1)
                     BlockUtil.absorbeRBlocksAnidados(newRenderable);
            }    
         } 
         //System.out.println("Termina Processing table");        
      }
      else if (root instanceof RWord){  
         String tt="\u00A0";
         RWord rw = (RWord)root;
         String word = rw.getWord();
         
         if(word.contains(tt)){
            //System.out.println("Calling replacement in RWORD: " + word + ". L = " + word.length());
            word = word.replaceAll(tt," ");
            word = word.trim();
            //System.out.println("After: " + word + ". L = " + word.length());
            rw.updateWord(word);    
         }  
         
         if(!word.isEmpty()){		
         //if(word.length()>1 || isNumeric(word)){	//solo palabrar mayores a 1 letra	   
            newRenderable = new NodeRenderable(root, "label");         
            newRenderable.setFather(father);
         //}
         }
                     
      }
      else if (root instanceof RUIControl){
        
         ModelNode node = ((RUIControl)root).getModelNode();
         NodeImpl ni = (NodeImpl)node;
         String nodeName = ni.getNodeName().toLowerCase(); 
         if(nodeName.equals("hr")||nodeName.equals("img")|| nodeName.equals("iframe")) 
            return null;
      
         if(nodeName.equals("select")){
            newRenderable = new NodeRenderable(root, "select");
            return newRenderable; 
         }
         
         String valor="";
         NamedNodeMap attribs = ni.getAttributes();
         int length = attribs.getLength();
         for (int i = 0; i < length; i++) {
            Attr attr = (Attr) attribs.item(i);
            if(attr.getNodeName().toLowerCase().equals("type")){
               valor = attr.getNodeValue().toLowerCase();
               break;
            }
         }
      
         if(valor.equals("image")|| valor.equals("submit")|| valor.equals("button")|| valor.equals("reset"))
            return null;
         if(valor.equals("") && nodeName.equals("input")){
            newRenderable = new NodeRenderable(root, "text");  
            return newRenderable; 
         }             
         newRenderable = new NodeRenderable(root, valor);
         newRenderable.setFather(father);
      }
      else if (root instanceof RBlank){
         newRenderable = new NodeRenderable(root, "Blank");
      }
      else if (root instanceof RStyleChanger){
         newRenderable = new NodeRenderable(root, "StyChanger");
      }
   
      return newRenderable;
   
   
   }

   public static void ajustarEtiquetas(NodeRenderable nr){
   //toma cada nodo y primero trata de desender la etiqueta de este hasa el RLine
   //despues intenta eliminar todos los "contenedores inecesarios"
   
      String type = nr.getType();
      LinkedList<NodeRenderable> childs = nr.getChilds();
      if(childs == null) 
         return; //noda que hacer si no hay nodos
   
      if(!(type.equals("RBlock") || type.equals("RTable") || type.equals("RTableCell")))
         return;
    
    
      String label = nr.getLabel();
      if(!label.equals("no label")){
         if(childs.size() == 1){//si tiene mas de un hijo no se puede descender
            String lb = childs.get(0).getLabel();
            if(lb.equals("no label")){ //si el nodo hijo no esta etiquetado
            //System.out.println("Intentando desdencer la etiqueta: " + label);
               boolean desc = tryDescendLabelToRLine(childs.get(0),label);//trata de descender la etiqueta al nodo hijo
               if(desc){
                  nr.setLabel("no label");
               //showRenderableTree(nr,0);
               //return;
               }
            }
         }
      }
        
        //checar los hijos
   //System.out.println("Calling recursivelly CASE 1 for childs");
      for(int i = 0; i < childs.size(); i++)
         ajustarEtiquetas(childs.get(i));         	         
   }

   public static boolean tryDescendLabelToRLine(NodeRenderable nr, String label){
   
   //nr es el nodo no esta etiquetado, trata de asignarle la etiqueta o descender
      String type =  nr.getType();
   
      if(!(type.equals("RLine") || type.equals("RBlock") || type.equals("RTable") || type.equals("RTableCell")))
         return false;//si no es un container
   
      LinkedList<NodeRenderable> childs = nr.getChilds();
   
      if(childs == null) 
         return false; //si no hay hijos, no se puede descender
      
      if(childs.size() == 0)//si tiene mas de un hijo, se asigna la etiqueta y termina
         return false;       
      if(childs.size() > 1){
         nr.setLabel(label);
         return true;
      }
      if(type.equals("RLine")){//si ya estoy sobre un Rline, se verifica si este contiene un UI no etiquetado
         if(childs.size() == 1){
            String tp = childs.get(0).getType();
            String lel = childs.get(0).getLabel();
         
            if(!tp.equals("label") && lel.equals("no label"))
               childs.get(0).setLabel(label);
            return true;
         }
         nr.setLabel(label); //la etiqueta se asigna al RLine
         return true;
      }
   
   //tiene un solo hijo
      nr = childs.get(0);//se recupera el hijo
      String lb = nr.getLabel();//etiqueta del hijo
      type =  nr.getType();//tipo del hijo
   
      if(lb.equals("no label") && (type.equals("RLine") || type.equals("RBlock") || type.equals("RTable") || type.equals("RTableCell")))
         return tryDescendLabelToRLine(nr, label);
      else
         return false;
     
   
             
   }

   public static void removerBloques(NodeRenderable nr){
   
      String type = nr.getType();
      NodeRenderable child = null;
      String labelFather = "";
      String labelChild = "";
     
      if(!(type.equals("RBlock") || type.equals("RTable") || type.equals("RTableCell")))
         return;
   
      
      LinkedList<NodeRenderable> childs = nr.getChilds();
      LinkedList<NodeRenderable> childs2 = null;
   
   
      if(childs != null){
         int i = 0;
         while(i < childs.size()) { 
            child = childs.get(i);
            childs2 = child.getChilds();
            if(childs2 == null){
               childs.remove(i);
               continue;
            }
            if(childs2.size() == 0){
               childs.remove(i);
               continue;
            }
         
            removerBloques(child);                //llamada recursiva
            i++;
         }
      
      //elimina las entradas que quedaron vacias
         NodeListUtil.cleanList(childs);
         	
         LinkedList<NodeRenderable> childsinChild = null;
      
         if(childs.size() == 1){//posible colapsar
            labelFather = nr.getLabel();
            child = childs.get(0);
            type = child.getType();
            if((type.equals("RBlock") || type.equals("RTable") || type.equals("RTableCell") || type.equals("RLine"))){
            //la absorcion no se da solo si ambos bloques estan etiquetados
               labelChild = child.getLabel();
            
                         
               childsinChild = child.getChilds();
               for (i = 0; i < childsinChild.size(); i++)
                  childs.add(childsinChild.get(i));
            
            //el padre absorve la etiqueta del hijo si el padre no esta etiquetado y el hijo si, de lo contrario, la etiqueta que prevalece es la del 
            //si alguno de los bloques ya está etiquetado, entonces no se colapsan  
               if(labelFather.equals("no label") && !(labelChild.equals("no label")) )
                  nr.setLabel(labelChild);
            
            //si el container ya se etiqueto, y este contiene a un grupo de Radios, hace el ajuste repectivo para
            //generar un solo radio etiquetado
            
            
               childs.remove(0);//remueve al child
            //ajustarRLineUIRadio(nr); 
            
            }
              
         }
         else{// colapsarHijos(childs);
         
         //los hijos de cada hijo h de NR toma el lugar de h                                 
         //colapsa todos los hijos del nodo actual
            i = 0;
         
            while(i < childs.size()){
               child = childs.get(i);
               labelChild = child.getLabel();
            
               if(!labelChild.equals("no label")){
                  i++;
                  continue;
               }
            
            //si el hijo esta etiquetado, se queda tal cual, si no esta etiquetado, sus hijos toman su lugar
               childsinChild = child.getChilds();
            
               if(childsinChild == null){
                  System.out.println("CHILDS is NULL: ");
               //showRenderableTree(child,0);
               //System.out.println("CHILDS is NULL-->END: ");
                  i++;
                  continue;
               }
            
               int k = 0;
               for(k = 0; k < childsinChild.size(); k++)
                  childs.add(i+k,childsinChild.get(k));
               childs.remove(i+k);
               i = i+k;
            }
         
         //ajustarRLineUIRadio(nr);
         }
      }
   
   }

   public static void removerLabels(NodeRenderable nr){
   //quita todas las labels que hayan quedado como hojas en el arbol final
   
      String type = null;
      
      LinkedList<NodeRenderable> childs = nr.getChilds();
      LinkedList<NodeRenderable> childs2 = null;
      NodeRenderable child = null;
      
      if(childs == null)
         return;
      
      int i = 0;
      while(i < childs.size()) { 
         child = childs.get(i);
         childs2 = child.getChilds();
         int tam = 0;
         if (childs2 != null)
            tam = childs2.size();
         if(tam < 1){
            type = child.getType();
            if(type.equals("label")){
               childs.remove(i);
               continue;
            }
         }
         else{
            removerLabels(child);
            childs2 = child.getChilds();
            if(childs2.size() == 0){
               childs.remove(i);
               continue;
            }
         }
         i++;
      }  
                 
   }

   private static String[] convNodeRendeToUI(NodeRenderable nr){
   
      boolean ban=false;
      String name="";
      String valor="";
      LinkedList value = new LinkedList();  
      String type = nr.getType();
      String label = nr.getLabel();
      Renderable ren = nr.getRenderable();
      int numNode = nr.getNumNodo();
      ModelNode node = ((RUIControl)ren).getModelNode();
      NodeImpl ni = (NodeImpl)node;
   
   //Obtener atributos del nodos UI <UI NODE:nombre,label,value> 
      NamedNodeMap attribs = ni.getAttributes();
      int length = attribs.getLength();
      System.out.println("Calculando UIcontent para radio. Atributos = " + length);
      for (int i = 0; i < length; i++) {
         Attr attr = (Attr) attribs.item(i);
         name = attr.getValue().toLowerCase();
         if (attr.getName().toLowerCase().equals("value")){
            valor=attr.getValue().toLowerCase();
            valor = valor.trim();
            valor = valor.replaceAll("\\s+", " ");
            value.add(valor);
         }
      }			
   		
               		
      String retvalor[] = new String[2];
      retvalor[0] = name;
      retvalor[1] = valor;
   
      return retvalor;
   }


   public static void colapseRadios(NodeRenderable nr){
   //busca nodos que sean UIs radios, consecutivos y los agrupa en un solo UI
      
      LinkedList<NodeRenderable> childs = nr.getChilds();
      NodeRenderable child = null;
      Renderable ren = null;
      
      if(childs == null)
         return;
      if(childs.size() == 0)
         return;
          
      int i = 0;
      String nameRadio = null;
      NodeRenderable accRadio = null;
       
      while(i < childs.size()) { 
         child = childs.get(i);
         ren = child.getRenderable();   
         if ( ren instanceof RUIControl ){ 
            String type = child.getType();   
            if(type.equals("radio")){
               String valor[] = convNodeRendeToUI(child);
               if (nameRadio == null){
                  nameRadio = valor[0];
                  accRadio = child;
                  //accRadio.setValues(child.getLabel() + "#" + valor[1]);
                  accRadio.setValues(child.getLabel());
                  accRadio.setLabel(nameRadio);  //la etiqueta contiene el nombre de la variable
                  i++;
                  continue;
               }
               else if(nameRadio.equals(valor[0])){
                  accRadio.setValues(child.getLabel());
                  childs.remove(i);
                  continue;
               }
               else{//ya cambio el radio
                  nameRadio = valor[0];
                  accRadio = child;
                  accRadio.setValues(child.getLabel());
                  accRadio.setLabel(nameRadio);
                  i++;
                  continue;
               }   
            }
         }
         else{
            colapseRadios(child);
         } 
         i++;             
      }               
   }


   public static void ajusteFinal(NodeRenderable nr){
   //busca nodos que sean UIs sin etiquetas y los intenta agregar a posibles grupos
      System.out.println("Entrando a ajuste final con " + nr);
      String type = null;
      
      LinkedList<NodeRenderable> childs = nr.getChilds();
      LinkedList<NodeRenderable> childs2 = null;
      NodeRenderable child = null;
      Renderable ren = null;
      
      if(childs == null)
         return;
      if(childs.size() == 0)
         return;
         
      int i = 0;
      //en la lista de hijos busca un UI sin etiqueta
      while(i < childs.size()) { 
         child = childs.get(i);
         System.out.println("Avanzando en lista con " + child);      
         ren = child.getRenderable();   
         if ( !(ren instanceof RUIControl)){
            System.out.println("Este " + child + " no es un UI");
            ajusteFinal(child);   
            i++;
            continue;
         }
         
         System.out.println("Este " + child + " es un UI");
         //si es un UI, se verifica que no este etiquetado
         String label = child.getLabel();
         if(label.equals("no label")){
         //revisa el nodo anterior
            System.out.println(" y no esta etiquetado");
            NodeRenderable anterior = null;
            if((i-1) >= 0)
               anterior = childs.get(i-1);
            
            if(anterior != null){
               ren = anterior.getRenderable();
               if (!(ren instanceof RUIControl)){
                  System.out.println("Encontramos caso para ajuste, este es el nodo " + child);
                  System.out.println("y este es el grupo al que se agrega " + anterior);
                  LinkedList<NodeRenderable> childs3 = anterior.getChilds();
                  childs3.add(child);
                  childs.remove(i);
                  continue;
               }
            }
         }
        
         i++;
                     
      }  
      System.out.println("Saliendo de ajuste final con " + nr);          
   }

}