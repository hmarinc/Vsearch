package com.process;

import java.io.IOException;
import java.util.Vector;
import java.awt.*;
import java.util.*;  
import com.integration.*;
import com.integration.tree.*;
import com.clustering.*;
import com.process.*;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

//import com.bvertical.process.sources.AbstractSource;
//import com.bvertical.process.sources.AlibrisSource;
//import com.bvertical.process.sources.AllbookstoresSource;
//import com.bvertical.process.sources.AmazonSource;

public class Mediator {
  //Funcion para distribuir los datos a las diferentes fuentes
   public Map<Integer,String> startProcess(Map<NodeCluster,LinkedList<JComponent>> fields, Map<NodeCluster,LinkedList<Clusterizable>> idClusters)
   {
      Map<Clusterizable,LinkedList<JComponent>> webquery = new HashMap<Clusterizable,LinkedList<JComponent>>();
   	
      for (Map.Entry<NodeCluster, LinkedList<JComponent>> me : fields.entrySet()){ // assuming your map is Map<String, String>  
         if(idClusters.containsKey(me.getKey())){  
            LinkedList<JComponent> field= (LinkedList<JComponent>)me.getValue();
            LinkedList<Clusterizable> cluster = (LinkedList<Clusterizable>)idClusters.get(me.getKey());
            for(Clusterizable c : cluster)
               webquery.put(c,field);
         }
      }
      
      Map<Integer,String> webQueries = buildQueries(webquery);
      
      //Wrapper wrapper = new Wrapper(); 
      //LinkedList <String> listURLs = wrapper.assignUrl(webQueries,urls); 
      return webQueries;  
   }
   
   public Map<Integer,String> buildQueries(Map<Clusterizable,LinkedList<JComponent>>webquery ){
    
      String name=""; 
      Map<Integer,String>queries = new HashMap<Integer,String>(); 
      Iterator<Clusterizable> keySetIterator = webquery.keySet().iterator();
      while(keySetIterator.hasNext()){
         Clusterizable key = keySetIterator.next();
         LinkedList<JComponent> list= webquery.get(key);
         
         if(key instanceof UIcontent){
            UIcontent k= (UIcontent)key;
            name=k.getName();
         }
         if(key instanceof UIcompuesto){
            UIcompuesto k = (UIcompuesto) key;
            name=k.getName();
         }
         
         if(key instanceof GroupElement){
            name=key.getLabel();
         }
         
          
         String value="";
         String query="";
         for(JComponent c: list){
            if(c instanceof JTextField)
               value =((JTextField)c).getText();
            if(c instanceof JComboBox)
               value =(String) (((JComboBox)c).getSelectedItem());
            if(c instanceof JCheckBox){
               if(((JCheckBox)c).isSelected())
                  value= ((JCheckBox)c).getText();
            }
            if(c instanceof JRadioButton)	
               value = ((JRadioButton)c).getText();}
               		
               		
         int numForm = key.getForm();     		
         if(queries.containsKey(numForm)){
            query= queries.get(numForm);
            query=query+"&"+name+"="+value;
            queries.put(numForm,query);
         
         }
         else{
            query=name+"="+value;
            queries.put(numForm,query);
         
         }   
      }
           
      Iterator<Integer> keySetIterator2 = queries.keySet().iterator();
   
      while(keySetIterator2.hasNext()){
         Integer key = keySetIterator2.next();
         String q= queries.get(key);
         
         System.out.println(" FORMULARIO:"+key+" QUERY:"+q);
      
      }
      return queries;
   }
}
