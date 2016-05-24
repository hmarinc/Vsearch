/**
* Un objeto NodeRenderable encapsula a un nodo del tipo Renderable presente en el arbol de renderizado.
* 
*/
package com.renderTree;

import org.lobobrowser.html.domimpl.*;
import org.lobobrowser.html.renderer.*;
import java.util.* ;
import java.io.*;

public class NodeRenderable implements Serializable{

   private Renderable node;                       //nodo original en rendertree
   private String type;                           //{block, rline, table, tablecell, ui}
   private String label;                          //etiqueta del nodo
   private String newLabel;                       //nueva etiqueta en el remplazo, para el caso de UIs, campo compuesto y grupo
   private NodeRenderable father;                 //padre de este nodo, opcional, se puede quitar
	
   private LinkedList<NodeRenderable> childList;  // hijos de este nodo, null cuando el nodo es una hoja
   private LinkedList<String> values;             // Solo cuando el Renderable es un UI, cuando es contenedor, values = null   
   private int numNodo;                           //numero de nodo en el arbol
	
   public NodeRenderable(Renderable node, String type){
      this.node = node;			    
      this.type = type;
      this.label = "no label";
      childList = new LinkedList<NodeRenderable>();
      father = null;
      values = null;
      newLabel = null;   
      numNodo = 0;
   }
    	
   public int getNumNodo(){
      return numNodo;
   }
      
   public void setNumNodo(int numNodo){
      this.numNodo = numNodo;
   }
   
   public LinkedList<String> getValues(){
      return values;
   }
   public void setValues(String value){
      if(values == null)
         values = new LinkedList<String>();	 
      values.add(value);			
   }
   
   public String getNewLabel(){
      return newLabel;
   }

   public void setNewLabel(String newLabel){
      this.newLabel =  newLabel;
   }
	
   public void setFather(NodeRenderable father){
      this.father = father;
   }
	
   public NodeRenderable getFather(){
      return father;
   }
	
   public Renderable getRenderable(){
      return node;
   }		
   
   public String getLabel(){
      return this.label;
   }
	
   public void setLabel(String lb){
      this.label = lb;
   }

   public void setLabel(String word, LinkedList<String> words){
      this.label = word;
      if(words != null)
         for(int i = 0; i<words.size(); i++)
            this.label = this.label + " " + words.get(i);
   }
   public LinkedList<NodeRenderable> getChilds(){
      return childList;
   }
   public void addChild(NodeRenderable child){
      if(childList == null) 
         childList = new LinkedList<NodeRenderable>();
      childList.add(child);
   }
   
   public int numChilds(){
      if (childList != null)
         return childList.size();
      else 
         return -1;
   }
   
   public String getType(){
      return type;
   }
   public String toString(){
   
      ModelNode mn = node.getModelNode();
      if( mn == null) 
         return "Group";
      
      NodeImpl ni = (NodeImpl)mn;   
      return ni.getNodeName();
   }
}