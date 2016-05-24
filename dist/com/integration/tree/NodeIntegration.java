/**
* Un objeto NodeRenderable encapsula a un nodo del tipo Renderable presente en el arbol de renderizado.
* 
*/
package com.integration.tree;

import com.renderTree.NodeRenderable;
import java.io.Serializable;
import org.lobobrowser.html.domimpl.*;
import org.lobobrowser.html.renderer.*;
import java.util.* ;

public class NodeIntegration implements Serializable{

   private String label;                          //etiqueta del nodo
   private int originalNumNode;                   //numero de nodo en el arbol rendertree    
   private int numNodo;                           //numero de nodo en este arbol (integracion)
   private LinkedList<NodeIntegration> childList;  // hijos de este nodo, null cuando el nodo es una hoja
   private int dist;                              //distancia del nodo dentro del arbol
	
   public NodeIntegration(NodeRenderable node){
      this.originalNumNode = node.getNumNodo();		//numero de nodo en el arbol render	    
      this.label = node.getNewLabel();             //la etiqueta de este nodo es la etiqueta de remplazo del nodo original
      childList = null;   
      numNodo = 0;
      dist = 0;
   }
   
 	
   public int getNumNodo(){
      return numNodo;
   }
   
   public void setDistance(int dist){
      this.dist = dist;
   }
   
   public int getDistance(){
      return dist;
   }
   
   public void setNumNodo(int numNodo){
      this.numNodo = numNodo;
   }
   
   public void emptyLabel(){
      label = null;
   }
   
   public void emptyChilds(){
      childList = null;
   }
   public String getLabel(){
      return label;
   }
   
   public LinkedList<NodeIntegration> getChilds(){
      return childList;
   }
   
   public void addChild(NodeIntegration child){
      if(childList == null) 
         childList = new LinkedList<NodeIntegration>();
      childList.add(child);
   }
   
   public int numChilds(){
      if (childList != null)
         return childList.size();
      else 
         return -1;
   }
   
   public String toString(){
      return ("NodeIntegration: " + "Nodo original = " + originalNumNode + ", numNodo = " + numNodo + ", label = " + label);
   }
}