package com.renderTree;
/**
Clase que resuelve el etiquetado de las hojas de una RLine.
Esta version 2 se realizo a finales de septiembre de 2013, e intenta ser una version mejorada de la version 1.
*/
import java.util.*;
import org.lobobrowser.html.renderer.*;

 
public class RLineLabeler{

   public static String sameUIsLabels(LinkedList<NodeRenderable> prevList){
      //etiquetado de UIs cuando en la lista existe el mismo numero de UIs y de Labels
      if(prevList.size() == 0) 
         return null;
         
      int i;
      NodeRenderable first = null;
      NodeRenderable second = null;
      
      i = 0;
      //toma un par de Nodos y determina cual es la etiqueta y cual es el UI
      while( (i+1) < prevList.size()){
         first = prevList.get(i);
         second = prevList.get(i+1);
            
         RWord rw = BlockUtil.labelInRenderable(first);
         if(rw != null){
            if( BlockUtil.hasUINotLabeled(second)) 
               second.setLabel(rw.getAsLabel());
            prevList.remove(i);
            i++;
            continue;
         }
         else{
            rw = BlockUtil.labelInRenderable(second);
            if(rw != null){
               if( BlockUtil.hasUINotLabeled(first)) 
                  first.setLabel(rw.getAsLabel());
               prevList.remove(i+1);
               i++;
               continue;
            }
         }       
         i++;  
      }
      return null;
   }
   
   public static String tryLabelRLine(LinkedList<NodeRenderable> prevList){
   //verifica si este RLine tiene el patron con etiqueta en el primer elemento y Uis en los elementos restantes
      if(prevList.size() == 0) 
         return null;
         
      int i;
      NodeRenderable first = null;
      NodeRenderable second = null;
      
      first = prevList.get(0);
      RWord rw = BlockUtil.labelInRenderable(first);
      if(rw == null) 
         return null;
            //verifica que todos los demas elementos no son Etiqueta
      String label = rw.getAsLabel();
            
      i = 1;
      boolean isLabel = false;
      while(i < prevList.size()){
         first = prevList.get(i);
         rw = BlockUtil.labelInRenderable(first);
         if(rw != null){
            isLabel = true;
            break;
         }
         i++;
      }
         
      if(isLabel) 
         return null;   
      //el patron si se cumple
      
      prevList.remove(0);
      
      return label;         
   }
   
   private static void tryGrouping(LinkedList<NodeRenderable> prevList){
      //intenta hacer gupos bajo el patron: etiqueta seguido de mas de un UI
      int i = 0;
      NodeRenderable node = null;
      RWord rw = null;
      
      while(i < prevList.size()){
        //busca una etiqueta
         while(i < prevList.size()){
            node = prevList.get(i);
            rw = BlockUtil.labelInRenderable(node);
            if(rw != null)//es una etiqueta
               break;
            i++;
         }
        
         if( rw == null) //no hay etiqeutas, salir
            break; 
            
         //a partir de la etiqueta encontrada, buscar grupos consecutivos de UIs, generalmente son Select
         int j = i + 1;
         LinkedList<NodeRenderable> grupo = new LinkedList<NodeRenderable>();
         
         while(j < prevList.size()){
            node = prevList.get(j);
            if(BlockUtil.hasUINotLabeled(node)){
               grupo.add(node);
            }
            else
               break;
            j++;
         }
         //si se formó un grupo (almenos dos UIs), entonces se crea un nuevo RLine con este grupo como hijos
         if(grupo.size() > 1){
            Renderable gr = new RGroup();
            NodeRenderable nnode = new NodeRenderable(gr, "grupo");
            for(NodeRenderable nr : grupo)
               nnode.addChild(nr);
            //se etiqueta el nuevo nodo
            nnode.setLabel(rw.getAsLabel());
            //quita todos los elementos desde la posicion i hasta la pos j-1 inclusive y en su lugar inserta el nuevo nodo   
            
            for(int k = i; k < j; k++)
               prevList.remove(i); 
                
            prevList.add(i,nnode);
            i = i+1;
            rw = null;   
            continue;
         }
         else{
            rw = null;
            i = j;
         }   
      }
   }
   public static String moreUIsThanLabels(LinkedList<NodeRenderable> prevList){
    //hay más UIs que etiquetas, posibles agrupaciones
      
      String label = tryLabelRLine(prevList);//caso primer elemento Label y los restantes son UIs no etiquetados
      if(label != null)// se tiene una etiqueta de grupo
         return label;
    
      //System.out.println("Caso mas UIs que labels");
      //NodeListUtil.showList(prevList);
      
    //primero etiquetar posibles checkbox o radio en este RLine
      int numLabeled = labelingCheckRadio(prevList);
      
      //segundo resolver etiquetas de posibles agrupaciones
    //una agrupacion se define como una etiqueta seguida de dos o mas UIs hasta el final de
    //la lista o hasta encontrar una siguiente etiqueta
      tryGrouping(prevList);
      
      //se cuentan las etiquetas y se decide si llamar al caso 1 o al 3
      Vector v = countUIsAndLabels(prevList);
      int numUis = (Integer)v.elementAt(0);       //contar UIs y Words
      int numLabels = (Integer)v.elementAt(1);
   //System.out.println("numUIs = " + numUis + " numLabels = " + numLabels);            
      
      String result = null;   
      if ( numUis < numLabels )  //el problema es saber qué etiquetas descartar
         result = moreLabelsThanUIs(prevList);  
      //este caso ya no deberia existir
      //else if ( numUis > numLabels ) //el problema es determinar las agrupaciones
         //result = moreUIsThanLabels(prevList);
      else if ( numUis == numLabels )                          //relación uno a uno
         result = sameUIsLabels(prevList);
   
      return null;
    
   }
   
   public static String moreLabelsThanUIs(LinkedList<NodeRenderable> prevList){
      //hay mas etiquetas que UIS, determinar cuales descartar
      //primero se resuelven los radios, luego los checkbox, luego los text y luego los select
      
      labeling(prevList);  //hacer el etiquetado
       
      //ahora, remover las etiquetas que quedaron
      int i = 0;
      while( i < prevList.size()){
         NodeRenderable node = prevList.get(i);
         if ( BlockUtil.labelInRenderable(node) != null ){
            prevList.remove(i);
            continue;
         }
         i++;
      }
      
      return null; 
   }
  
    
   public static int labelingCheckRadio(LinkedList<NodeRenderable> prevList){
   //encuentra un uiRadio o uiCheck, despues decide que etiqueta asignarle, la preferencia es la de abajo
      int i = 0;
      int numLabeled = 0;
     //busca hasta encontrar un radio o check en esta lista que no este etiquetado
      while( i < prevList.size()){
         NodeRenderable nodeUI = prevList.get(i);   
         String type = nodeUI.getType();
         if( !BlockUtil.hasUINotLabeled(nodeUI)){
            i++;
            continue;
         }
         
         //encontro un UI no etiquetado, ahora se verifica si es check o radio
         if(!(type.equals("radio") || type.equals("checkbox"))){
            i++;
            continue;
         }
      
        //enocntro un UI no etiquetado que es check o radio, busca la posible etique, la preferencia es buscar hacia abajo
         RWord rwAnt = null;
         RWord rwPos = null;
         NodeRenderable next = null;
         NodeRenderable ant = null;
            
         if( (i-1) >= 0){
            ant = prevList.get(i-1);
            rwAnt = BlockUtil.labelInRenderable(ant);
         }      
         if( (i+1) < prevList.size()){
            next = prevList.get(i+1);
            rwPos = BlockUtil.labelInRenderable(next);
         }
               
         if(rwAnt != null && rwPos != null){ //si hay etiquetas tanto arriba como abajo, tomamos la de abajo
            nodeUI.setLabel(rwPos.getAsLabel());
            numLabeled++;
            prevList.remove(i+1);
            continue;       
         }
         else if( rwAnt != null ){//si solo de de arriba es etiqueta, la tomamos
            nodeUI.setLabel(rwAnt.getAsLabel());
            numLabeled++;
            prevList.remove(i-1);
            continue;
         }
         else if( rwPos != null ){//si solo la de abajo es etiqueta, la tomamos
            nodeUI.setLabel(rwPos.getAsLabel());
            numLabeled++;
            prevList.remove(i+1);
            i++;
            continue;
         }                  
         i++;  
      }
      return numLabeled;
   }
   
    
   public static void labeling(LinkedList<NodeRenderable> prevList){
      int i = 0;
     
     //busca hasta encontrar un UI en esta lista que no este etiquetado
     //al menos existe un UI en esta lista, y hay mas de una etiqueta 
      while( i < prevList.size()){
         NodeRenderable nodeUI = prevList.get(i);   
         if( !BlockUtil.hasUINotLabeled(nodeUI)){
            i++;
            continue;
         }
         
         String type = nodeUI.getType();   
         //intenta tomar las etiquetas par arriba o abajo de este UI
         RWord rwAnt = null;
         RWord rwPos = null;
         NodeRenderable next = null;
         NodeRenderable ant = null;
            
         if( (i-1) >= 0){
            ant = prevList.get(i-1);
            rwAnt = BlockUtil.labelInRenderable(ant);
         }      
         if( (i+1) < prevList.size()){
            next = prevList.get(i+1);
            rwPos = BlockUtil.labelInRenderable(next);
         }
               
         if(rwAnt != null && rwPos != null){ 
            if(type.equals("radio") || type.equals("checkbox")){
               nodeUI.setLabel(rwPos.getAsLabel());
               prevList.remove(i+1);
               continue;
            }
            else if(type.equals("text") || type.equals("select")){
               nodeUI.setLabel(rwAnt.getAsLabel());
               prevList.remove(i-1);
               i++;//salta la etiqueta Pos
               continue;
            }    
         }
         else if( rwAnt != null ){
            nodeUI.setLabel(rwAnt.getAsLabel());
            prevList.remove(i-1);
            continue;
         }
         else if( rwPos != null ){
            nodeUI.setLabel(rwPos.getAsLabel());
            prevList.remove(i+1);
            i++;
            continue;
         }                  
         i++;  
      }
   
      return;
   }
   
   public static Vector countUIsAndLabels(LinkedList<NodeRenderable> prevList){
      Vector v = new Vector();
      int numUis = 0;       //contar UIs y Words
      int numLabels = 0;
   
      for(NodeRenderable node : prevList)
         if(BlockUtil.hasUINotLabeled(node)) numUis++;
         else if(BlockUtil.hasLabel(node)) numLabels++;
   
      v.addElement(numUis);
      v.addElement(numLabels);
      
      return v;
   }
   
   public static String label(LinkedList<NodeRenderable> prevList){      
      Vector v = countUIsAndLabels(prevList); 
      int numUis = (Integer)v.elementAt(0);
      int numLabels = (Integer)v.elementAt(1);  
                     
      if(numLabels == 0){ //no hay etiquetas, salir
         return null;
      }   
      
      if(numUis == 0){ //no hay UIs, salir   
         if(numLabels > 1 ) 
         //solo hay labels en este RLine, se concatenan todas si existe mas de una
            //pueden existir UIs pero ya están etiquetados
            Collapser.colapseRLabels(prevList);   
         return null;   
      }
      
      //si llego aqui es porque al menos hay un UI y un label en la lista
      //etiquetar primero los checkbox o radio en esta lista      
      String result = null;
   
      if ( numUis == numLabels )  //si la relacion es uno - uno, etiquetar
         result = sameUIsLabels(prevList);        
      else if ( numUis < numLabels )  //mas labels, el problema es saber qué etiquetas descartar
         result = moreLabelsThanUIs(prevList);  
      else if ( numUis > numLabels ) //mas UIs, el problema es determinar las agrupaciones
         result = moreUIsThanLabels(prevList);
            
      return result; 
   }      
}