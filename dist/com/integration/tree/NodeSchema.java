
package com.integration.tree;

import com.renderTree.NodeRenderable;
import java.io.Serializable;
import org.lobobrowser.html.domimpl.*;
import org.lobobrowser.html.renderer.*;
import java.util.* ;

public class NodeSchema implements Serializable{

   private String symbol;                          //etiqueta del nodo
   private double distance;                           //la distancia de este nodo en la matriz promedio
   private LinkedList<NodeSchema> childList;  // hijos de este nodo, null cuando el nodo es una hoja
   boolean treatedAsLeaf;
   int row;
	
   public NodeSchema(String symbol, double distance, int row){
      this.symbol = symbol;
      this.distance = distance;
      childList = null;   
      treatedAsLeaf = false;
      this.row = row;
   }
   
 	
   public String getSymbol(){
      return symbol;
   }         
   
   public int getRow(){
      return row;
   }
	
	 
	     
   public void setRow(int r){
      row = r;
   }    
   
   public double getDistance(){
      return distance;
   }   
   
   public void emptyChilds(){
      childList = null;
   }
   public void setSymbol(String Symbol){
      this.symbol = symbol;
   }
   
   public LinkedList<NodeSchema> getChilds(){
      return childList;
   }
   
   public void addChild(NodeSchema child){
      if(childList == null) 
         childList = new LinkedList<NodeSchema>();
      childList.add(child);
   }
   
   public int numChilds(){
      if (childList != null)
         return childList.size();
      else 
         return -1;
   }
   
   public String toString(){
      return ("[" + symbol + "," + distance + "]");
   }
}