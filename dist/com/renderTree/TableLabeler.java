/**
* Resuelve las etiquetas de una tabla
*/
package com.renderTree;

import org.lobobrowser.html.renderer.*;
import java.util.*;

public class TableLabeler{
   static Hashtable<Integer,LinkedList<NodeRenderable>> table = null;
 
   public static void decodeTable(LinkedList<NodeRenderable> prevList){
      table = new Hashtable<Integer,LinkedList<NodeRenderable>>();
   
   //cada nodo en la lista en un table cell
      int i = 0;
      int n = prevList.size();
      RTableCell rtc = null;
      NodeRenderable cell = null;
      Renderable ren = null;
      int row = -1;
      int r,c;
      LinkedList<NodeRenderable> rowList = new LinkedList<NodeRenderable>();
      
      while(i < n){
         cell = prevList.get(i);   
         ren = cell.getRenderable();
         
         rtc = (RTableCell)ren;
         r = rtc.getVirtualRow();
         c = rtc.getVirtualColumn();
         
         if( r != row){ //un renglon diferente
            if(row >= 0){ //si teniamos un renglon valido
               table.put(row,rowList);
            }
            if(!table.containsKey(r))//nuevo renglon
               rowList = new LinkedList<NodeRenderable>();     
            else
               rowList = table.get(r);
         
            row = r;   //current row   
         
         }
         
         rowList.add(cell);
            
         i++;
      }
      
      showDecode();
   }

   public static void showDecode(){
   //Muestra la tabla decodificada por renglones
      if(table == null) 
         return;
   
      Enumeration e = table.keys();
      int clave;
      LinkedList<NodeRenderable> valor = null;
      while( e.hasMoreElements() ){
         clave = (Integer)e.nextElement();
         valor = table.get(clave);
         //System.out.println( "Row " + clave);
         //NodeListUtil.showList(valor);
      }      
   }


   public static void asignarEtiquestasEnTable(LinkedList<NodeRenderable> prevList){
   //Recibe una lista con nodos RTableCell. El objetivo es buscar etiquetas y asociarlas a UIs o a grupos de UIs   
     
     //System.out.println("Asignado etiquetas a esta tabla ");
     //NodeListUtil.showList(prevList);
            
      Renderable ren = null;
      int MAXROWS = 30;
      int MAXCOLS = 30;
   	
      int numRows = 0;    //almacenaran el tamaño real de la tabla
      int numCols = 0;
      
      //Se creara una matriz, que reflejará a la tabla de Nodos que se esta recibiendo
      NodeRenderable matrix[][] = new NodeRenderable[MAXROWS][MAXCOLS];	
      for(int i = 0; i < MAXROWS; i++)
         for(int j = 0; j < MAXCOLS; j++)
            matrix[i][j] = null;
   	
      
      int i = 0;
      RWord rw = null;
      NodeRenderable cell = null;
      NodeRenderable radio = null;
         
      //primero se llena la matriz
      while(i < prevList.size()){
         cell = prevList.get(i);
         ren = cell.getRenderable();
         
         RTableCell rtc = (RTableCell)ren;
         int row = rtc.getVirtualRow();
         int col = rtc.getVirtualColumn();
         
         if(row > numRows) numRows = row;
         if(col > numCols) numCols = col;
            
         matrix[row][col] = cell;
         i++;
      }
   
   //SE RESUELVEN LAS ETIQUETAS POR LINEAS
      labelByLineInTable(matrix, numRows, numCols);
   
   //SE RESUELVEN LAS ETIQUETAS DE LOS UI FALTANTES, YA SEA POR AGRUPACIÓN
      procesarTabla(matrix, numRows, numCols);                 
          
         //La tabla final es la que tiene la asociación final
         //showRTable( matrix, numRows, numCols);
         //se forma la lista final
      prevList.clear();
         
      for(i = 0; i <= numRows; i++){
         for(int j = 0; j <= numCols; j++){
            cell = matrix[i][j];
            
            if(cell == null) 
               continue;
                  
                  
            prevList.add(cell);
         }
      }
        
        //System.out.println("Termina asignar Labels en tabla");  	
   }

   private static void labelByLineInTable(NodeRenderable matrix[][], int numRows, int numCols){
   
   
   //SOLO SE CONSIDERAN CELDAS CON ETIQUETAS CON SPAN EN ROWS A LO MAS EN UNO
   //analiza renglon por renglon y trata de hacer las asignaciones de etiqueta correspondientes
      NodeRenderable cell = null;
      Renderable ren = null;
      LinkedList<NodeRenderable> list = null;
      
      for(int i = 0; i <= numRows; i++){
         //cuenta Labels y UIS en cada celda de este renglon
         int numLabels = 0;
         int numUIs = 0;
         list = new LinkedList<NodeRenderable>();
         
         for(int j = 0; j <= numCols; j++){
            cell = matrix[i][j];   
            if(cell == null) //salta espacio vacios
               continue;
               
            ren = cell.getRenderable();
         
            RTableCell rtc = (RTableCell)ren;
            int row = rtc.getVirtualRow();
            int col = rtc.getVirtualColumn();
            int rSpan = rtc.getRowSpan();
            int cSpan = rtc.getColSpan();
            
            if(rSpan > 1) //solo considera la celda si pertenece a un row con span a lo mas en uno
               continue;
            
            if(BlockUtil.hasUIorBlock(cell)){
               numUIs++;
               list.add(cell);
            }
            else if (BlockUtil.hasLabel(cell)){ 
               list.add(cell);
               numLabels++;
            }
         }
         
         //se ha obtenido un nuevo renglon
         if( (numLabels > 0) && (numLabels == numUIs) ){
         //hay almenos una etiqueta y las etiquetas son iguales a los Uis
            int k = 0;
            while( k < list.size()){
            
               NodeRenderable posibleUi = list.get(k);
               
               if(BlockUtil.hasUIorBlock(posibleUi))
                  if((k-1) >= 0){
                     NodeRenderable posibleLabel = list.get(k-1);
                  
                     if(BlockUtil.hasLabel(posibleLabel)){
                        RWord rw = BlockUtil.labelInRenderable(posibleLabel);   
                        tryAssignLabelUI(posibleUi, rw.getAsLabel());
                        
                        //no necesariamente la posicion en la lista se corresponde con la posicion en la 
                        //tabla
                        Renderable ren1 = posibleLabel.getRenderable();
                        RTableCell tc = (RTableCell)ren1;
                        int col = tc.getVirtualColumn();
                        matrix[i][col] = null;   
                        list.remove(k-1);     //remueve la etiqueta de la lista
                        continue;
                     }
                     else{//revisa a la DERECHA
                        if((k+1) < list.size() ){
                           posibleLabel = list.get(k+1);
                           if(BlockUtil.hasLabel(posibleLabel)){
                              RWord rw = BlockUtil.labelInRenderable(posibleLabel);   
                              tryAssignLabelUI(posibleUi, rw.getAsLabel());
                              
                              Renderable ren1 = posibleLabel.getRenderable();
                           
                              RTableCell tc = (RTableCell)ren1;
                              int col = tc.getVirtualColumn();
                              matrix[i][col] = null;
                              list.remove(k+1);
                              k++;
                              continue;
                           }
                        }
                     } 
                  } 
                  else{
                  //revisa a la DERECHA
                     if((k+1) < list.size() ){
                        NodeRenderable posibleLabel = list.get(k+1);
                        if(BlockUtil.hasLabel(posibleLabel)){
                           RWord rw = BlockUtil.labelInRenderable(posibleLabel);   
                           tryAssignLabelUI(posibleUi, rw.getAsLabel());
                           
                           Renderable ren1 = posibleLabel.getRenderable();
                        
                           RTableCell tc = (RTableCell)ren1;
                           int col = tc.getVirtualColumn();
                           matrix[i][col] = null;
                           list.remove(k+1);
                           k++;
                           continue;
                        }   
                     }
                  } 
               k++;
            }
         
         }
         else if((numLabels > 0) && (numLabels < numUIs)){//hay al menos una etiqueta y el numero de etiquetas es menor al numero de UIs, salir, posible agrupación
         //posible agrupación
         
         }
         else if((numLabels > 0) && (numUIs < numLabels)){
            int k = 0;
            while( k < list.size()){
            
              //primero localiza a un UI para intentar buscarle su etiqueta   
               NodeRenderable posibleUI = list.get(k);
               
               if(!BlockUtil.hasUIorBlock(posibleUI)){
                  k++;   
                  continue;
               }
            
            //encontro un UI   
               String lbant = null;
               String lbpos = null;
               NodeRenderable ant = null;
               NodeRenderable post = null;
               
               if( (k-1) >= 0 ){
                  ant = list.get(k-1);
                  RWord rw = BlockUtil.labelInRenderable(ant);   
                  if(rw != null)
                     lbant = rw.getAsLabel();      
               }
               if( (k+1) < list.size() ){
                  post = list.get(k+1);
                  RWord rw = BlockUtil.labelInRenderable(post);   
                  if(rw != null)
                     lbpos = rw.getAsLabel();  
               }
              
               if( lbant != null & lbpos != null){//estoy entre dos etiquetas, tomar la de la derecha
                  tryAssignLabelUI(posibleUI, lbpos);
                  Renderable ren1 = post.getRenderable();
                  RTableCell tc = (RTableCell)ren1;
                  int col = tc.getVirtualColumn();
                  matrix[i][col] = null;
                  list.remove(k+1);
                  k++;
                  continue;
               }
               else if( lbant != null){//tomar la etiqueta disponible
                  tryAssignLabelUI(posibleUI, lbant);
                  Renderable ren1 = ant.getRenderable();
                  RTableCell tc = (RTableCell)ren1;
                  int col = tc.getVirtualColumn();
                  matrix[i][col] = null;
                  list.remove(k-1);
                  continue;
               }
               else if( lbpos != null){//tomar la etiqueta disponible
                  tryAssignLabelUI(posibleUI, lbpos);
                  Renderable ren1 = post.getRenderable();
                  RTableCell tc = (RTableCell)ren1;
                  int col = tc.getVirtualColumn();
                  matrix[i][col] = null;
                  list.remove(k+1);
                  k++;
                  continue;
               }
               
               k++;
            }
         
         }  
      }
         
   }
         
        
   public static void procesarTabla(NodeRenderable matrix[][], int numRows, int numCols){
      NodeRenderable node = null;
      RWord rw = null;
      
      for(int row = 0; row <= numRows; row++)            
         for(int col = 0; col <= numCols; col++){
         
            node = matrix[row][col];
            
            if (node == null) 
               continue;
            
            rw = BlockUtil.labelInRenderable(node);   
            if(rw == null)
               continue;
         
            Renderable ren1 = node.getRenderable();
            RTableCell tc = (RTableCell)ren1;
            int cSpan = tc.getColSpan();
            int rSpan = tc.getRowSpan();
         
            String label = rw.getAsLabel();
            LinkedList<NodeRenderable> nodeList = null;
               
         //System.out.println("NUEVA ETIQUETA");   
            nodeList = agruparHaciaAdelante(label,matrix,row,col,rSpan,cSpan, numRows, numCols);
         
            if(nodeList.size() == 0)
               nodeList = agruparHaciaAbajo(label,matrix, row,col,rSpan,cSpan, numRows, numCols);
         
                      
         
            if(nodeList.size()>0){
               //NodeListUtil.showList(nodeList);
               if(resolverLabelList(nodeList, label)){
                  matrix[row][col] = null;
               }
               
               matrix[row][col] = null;
                
            //se vuuelve a colocar el contenido de la lista en la tabla
               int h = 0;
               while(h < nodeList.size()){
                  node = nodeList.get(h);
               
                  Renderable ren = node.getRenderable();
               
                  RTableCell rtc = (RTableCell)ren;
                  int rowi = rtc.getVirtualRow();
                  int colj = rtc.getVirtualColumn();
               
               
                  matrix[rowi][colj] = node;
                  h++;
               }   
            }
            else{
               matrix[row][col] = null;
               //System.out.println("No hubo agrupaciones para esta etiqueta, se perderá: " + label);
            }
         
           
         }               
   }

   public static void showRTable(NodeRenderable matrix[][], int rows, int cols){
      System.out.println("\n ---****--- Showing table ----****---");
   
      for(int i = 0; i <= rows; i++){
         System.out.println("");
         
         for(int j = 0; j <= cols; j++){
            NodeRenderable root = matrix[i][j];
            System.out.println("[" + i + ", " + j + "]: ");
            PrunedTreeBuilder.showRenderableTree(matrix[i][j], 0);
         }
      
      }
      
      System.out.println("\n ---****--------------------****---");
   }

   public static boolean tryAssignLabelUI(NodeRenderable nodeCell, String label){ 
    //se intenta etiquetar un nodo
      NodeRenderable node = nodeCell;
      String  typeElement = node.getType();
      LinkedList<NodeRenderable> childs = null;
      
      //ya que lo que se recibe es un RBlock, se desciende hasta el RLine  
      while(!typeElement.equals("RLine")){
         childs = node.getChilds();
         if(childs == null) 
            break;
         if(childs.size() == 0)
            break;
               
         node = childs.get(0);
         typeElement = node.getType();
      }	
      	//si no es RLine
      if(!typeElement.equals("RLine")){
         return false;
      }
         
      childs = node.getChilds();
       
      if(childs == null)
         return false;
      
      int numChilds = childs.size();
      //se realiza el etiquetado
         
      if(numChilds == 0)
         return false;
      else if(numChilds > 1){
         if(correctLabel(childs,label))
            return true;
         
         String lb = node.getLabel();   	
         if( lb.equals("no label")){
            node.setLabel(label);
            return true;
         }
         else{ //la etiqueta va en el RBlock
            nodeCell.setLabel(label);
            return true;
         }
         
      }
      else if(numChilds == 1){
         NodeRenderable rUI = childs.get(0);
         String lb = rUI.getLabel();
         	
         if( lb.equals("no label")){
            rUI.setLabel(label);
            return true;
         }
         else   
            return false;
      }
      
      return false;
   }
   private static boolean correctLabel(LinkedList<NodeRenderable> childs,String label){
      //esta funcion hace una correccion de la etiqueta sobre la lista de entrada perteneciente a un RLine
      //si el primer elemento es un único RUI que es text o select no etiquetado, la etiqueta se asocia a él
      if(childs == null)
         return false;
      if(childs.size() == 0)
         return false;
      
      Vector v = RLineLabeler.countUIsAndLabels(childs);
      int numUIs = (Integer)v.elementAt(0);
      if(numUIs != 1)
         return false;
      //verifica si el primer elemento en la lista es un UI no etiquetado
      NodeRenderable node = childs.get(0);
      if(BlockUtil.hasUINotLabeled(node)){
         node.setLabel(label);
         return true;
      }
      else
         return false;
   }
   public static LinkedList<NodeRenderable> agruparHaciaAbajo(String label, NodeRenderable matrix[][], int row, int col, int rSpan, int cSpan, int numRows, int numCols){ 
      //lista de nodos a agrupar
      LinkedList<NodeRenderable> nodeList = new LinkedList<NodeRenderable>();
      NodeRenderable node = null;
      RWord rw = null;
      boolean end = false;
   
      int maxCol = col + cSpan;                  //el ancho que debe considerarse en la agrupación
      for (int i = row+rSpan; i <= numRows; i++){    //comienza en el siguiente renglon valido
         if(end) 
            break;   
         for(int j = col; j < maxCol; j++){
            
            node = matrix[i][j];
            
            if(node == null){ 
               end = true;
               break;
            
            }
               
            if((rw = BlockUtil.labelInRenderable(node)) != null) {       //una etiqueta termina la agrupacion hacia abajo              
               end = true;
               break;
            }
                        
            nodeList.add(node);
            matrix[i][j] = null;
            Renderable ren = node.getRenderable();
            RTableCell rtc = (RTableCell)ren;             
            j = j + rtc.getColSpan()-1;
            
         }
      }
      
      //System.out.println("Agrupación hacia ABAJO para la etiqueta : " + label);
      return nodeList;
   }
   
   public static LinkedList<NodeRenderable> agruparHaciaAdelante(String label,NodeRenderable matrix[][], int row, int col, int rSpan, int cSpan, int numRows, int numCols){ 
   
      LinkedList<NodeRenderable> nodeList = new LinkedList<NodeRenderable>();
      
      NodeRenderable node = null;
      RWord rw = null;
      boolean end = false;
      int limite = numCols+1;                   //el límite en teoria es la última columna
      int maxrow = row + rSpan;                 //debe abarcar todos los renglones del span de esta etiqueta
      
      for (int i = row; i < maxrow; i++){
         if( end ) 
            break;
         for(int j = col+cSpan; j < limite; j++){  //agrupacion hacia adelante, comienza en la siguiente columna valida, termina cuando encuentra null u otra etiqueta
            node = matrix[i][j];
            if(node == null) 
               continue;
            if(BlockUtil.isBlockEmpty(node)) 
               continue;
               
            if((rw = BlockUtil.labelInRenderable(node)) != null){ //si encuentro otra etqueta esa sera el limite
               limite = j;
               break;
            }
               
            nodeList.add(node);
            matrix[i][j] = null;
            Renderable ren = node.getRenderable();
            RTableCell rtc = (RTableCell)ren;             
            j = j + rtc.getColSpan()-1;
            
         }
      }
   
      
      //System.out.println("Agrupación hacia ADELANTE para la etiqueta : " + label);
      return nodeList;
   }

   public static void removeLabel(int row, int col, Vector xyLabels){
      for(int i = 0; i < xyLabels.size(); i++){
         Vector v = (Vector)xyLabels.elementAt(i);
         int rowi = (int)v.elementAt(0);
         int colj = (int)v.elementAt(2);
         
         if(row == rowi && col == colj){
            xyLabels.remove(i);
            return;
         }
         
      }
   }

   public static boolean resolverLabelList(LinkedList<NodeRenderable> nodeList, String label){
      if(nodeList.size() == 1){
      //solo es un RBlock en la lista, el cual puede tener solo un UI o un grupo de UIs
         NodeRenderable node = nodeList.get(0);
         return tryAssignLabelUI(node, label);
      }
      else{
      //collapse RBLOCKs
         Collapser.collapseRBlocksConsecutivos(nodeList);
      //la lista debe quedar con un solo rblock
         NodeRenderable node = nodeList.get(0);
         return tryAssignLabelUI(node, label);
      }
   }
   
   public static void showLabels(Vector xyLabels){
   
      System.out.println("Labels in table: ");
   
      for(int i = 0; i < xyLabels.size(); i++){
         Vector v = (Vector)xyLabels.elementAt(i);
         int row = (int)v.elementAt(0);
         int rSpan = (int)v.elementAt(1);
         int col = (int)v.elementAt(2);
         int cSpan = (int)v.elementAt(3);
         String label = (String)v.elementAt(4);
         
         System.out.println("[" + row + "("+ rSpan +"), " + col + "(" + cSpan + ")] <->" + label);
      
      }
   }
   

}