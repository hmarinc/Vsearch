package com.integration;

import java.io.* ;
import com.clustering.*;
import com.renderTree.*;
import java.util.*;

public class Mapping{
//usa la informacion del proceso de clustering para actualizar los nombres de los nodos en cada uno de los arboles pruned render tree
   public static void upDateTrees(Map<NodeCluster,LinkedList<Clusterizable>> clusters, LinkedList<PrunedTree> arboles){
      PrunedTree arbol = null;  
      int numForm;
      int numNode;
      String idCluster="";
      
      Set<NodeCluster> keySet = clusters.keySet();
      for(NodeCluster key : keySet){
         LinkedList<Clusterizable> cluster= clusters.get(key);
         String newLabel = key.getLabel();
         for(Clusterizable ei: cluster){
            numForm = ei.getForm();
            numNode = ei.getNumNode();
            idCluster = MapaGlobal.newEntre(newLabel);
            arbol = arboles.get(numForm);
            arbol.renameNode(numNode,idCluster);                      
         }
      }
   }
}