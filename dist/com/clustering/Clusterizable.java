package com.clustering;
import java.io.Serializable;
/*
 * Clase generica que permite modelar un nodo que se ingresa al cluster. La informacion
 * de interes del nodo del arbol es su etiqueta, numero de formulario, id que es el id dentro del arbol

*/
public abstract class  Clusterizable implements Serializable{

   private int numForm;              //numero de formulario
   private String label;             //etiqueta en este formulario
	private int numNode;              //numero de nodo 
   private String newLabel;          //etiqueta de remplazo, depues del clustering
   
   public Clusterizable(int numForm, String label, int numNode){
      this.numForm = numForm;
      this.label = label;      //etiqueta asociada a este Nodo, o la etiqueta del valor select
		this.numNode = numNode;
   }
	
   public int getNumNode(){
      return numNode;
   }
	
   public int getForm(){
      return numForm;
   }
 
   public String getLabel(){
      return label;
   }
	
	  	
   public void setLabel(String lb){
      label = lb;
   }
	      
   public String getID(){
      return ("ID: " + getForm() + getNumNode());
   }

   
   public abstract double compare(Clusterizable element2);

   public abstract String show();
}