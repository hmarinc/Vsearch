   package com.integratedGI;
   import java.awt.Component;
   import java.awt.GridLayout;
   import java.awt.Menu;
   import java.awt.MenuBar;
   import java.awt.MenuItem;
   import java.awt.event.ActionEvent;
   import java.awt.event.ActionListener;
   import java.awt.event.WindowAdapter;
   import java.awt.event.WindowEvent;
   import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
   import javax.swing.ImageIcon;
   import javax.swing.JComponent;
   import javax.swing.JFrame;
   import javax.swing.JLabel;
   import javax.swing.JOptionPane;
   import javax.swing.JPanel;
   import javax.swing.JTabbedPane;

   import com.integration.*;
   import com.integration.tree.*;

   public class IntegratedWQI extends JFrame implements ActionListener {
      JPanel panelPrincipal;
      InterfazUnifiedWQI libros;
     //public static void trasverseTree(NodeSchema root){
      //  JFrame frame = new IntegratedWQI("Vertical Search Engine Version 1.0");
  
    //public IntegratedWQI(){
	     //    JFrame frame =   new IntegratedWQI("JFrame Source Demo");
        //}
     
	 
      public IntegratedWQI(NodeSchema tree) {
    
         
        
         JFrame frame =   new JFrame("Vertical Search Engine Version 1.0");
          panelPrincipal = new JPanel();
         JTabbedPane tabbedPane = new JTabbedPane();
         Component panel1 = makeTextPanel("Busqueda");
         InterfazUnifiedWQI intLibro = new InterfazUnifiedWQI(frame,tree);
      
         Component panel3 = intLibro.gestionLibros();
         tabbedPane.addTab("Searching WQIs", new ImageIcon(
            "com/bvertical/imagenes/libro.gif"), panel3, "Searching WQIs");
      
      /* cada una de las pestañas */
         tabbedPane
            .addTab("Information...", new ImageIcon(
            		"com/bvertical/imagenes/prestamos.gif"), panel1,
            		"Information...");
      // tabbedPane.setBackgroundAt(Color.yellow) //agregarcolor
      
      // Add the tabbed pane to this panel.
         panelPrincipal.setLayout(new GridLayout(1, 1));
         panelPrincipal.add(tabbedPane);
      
         addWindowListener(
               new WindowAdapter() {
                  public void windowClosing(WindowEvent e) {
                     System.exit(0);
                  
                  }
               });
      
         getContentPane().add(panelPrincipal);// ,
      
      /* Menus */
      
         MenuItem menuImprimirVencidos = new MenuItem("Opcion 1");
         MenuItem menuImprimirPrestamos = new MenuItem("Opcion 2");
         MenuItem menuImprimirListadoLibros = new MenuItem("Opcion 3");
      
         MenuItem menuInfoLibros = new MenuItem("Libros");
      
         menuImprimirVencidos.addActionListener(this);
         menuImprimirPrestamos.addActionListener(this);
         menuImprimirListadoLibros.addActionListener(this);
         menuInfoLibros.addActionListener(this);
      
         Menu menuImprimir = new Menu("Imprimir");
         Menu menuInfo = new Menu("Información");
      
         menuImprimir.add(menuImprimirVencidos);
         menuImprimir.add(menuImprimirPrestamos);
         menuImprimir.add(menuImprimirListadoLibros);
      
         menuInfo.add(menuInfoLibros);
      
      // Instancia un objeto MenuBar y le añade el objeto Menu previamente
      // definida.
         MenuBar barraMenu = new MenuBar();
         barraMenu.add(menuImprimir);
         barraMenu.add(menuInfo);
      
      // Se instancia un objeto Frame y se le asocian los objetos MenuBar
      // anteriores.
      
         setMenuBar(barraMenu);
      
         setSize(620, 440);
         setVisible(true);
         setLocation(300, 150);
      
      }
   
   	
   
   
   
   
      protected JComponent makeTextPanel(String text) {
         JPanel panel = new JPanel(false);
         JLabel filler = new JLabel(new ImageIcon(
            "com/bvertical/imagenes/FONDO1.jpg"));
         filler.setHorizontalAlignment(JLabel.CENTER);
         panel.setLayout(new GridLayout(1, 1));
         panel.add(filler);
      
         return panel;
      }
   
      public void actionPerformed(ActionEvent evt) {
      // Verifica que elemento del menu ha
      // generado el evento
         String s = (String) evt.getActionCommand();
      
         if (s.equals("Libros")) {
            JOptionPane.showMessageDialog(null, "Número de titulos: \t",
               "Información de libros", JOptionPane.INFORMATION_MESSAGE);
            return;
         }
      
         if (s.equals("Usuarios")) {
            java.util.Vector v = new java.util.Vector();
         
            JOptionPane.showMessageDialog(null, "Usuarios registrados: ",
               "Información de usuarios", JOptionPane.INFORMATION_MESSAGE);
            return;
         }
      
         if (s.equals("Prestamos vencidos")) {
            return;
         }
      
         if (s.equals("Lista de prestamos")) {
            return;
         }
         if (s.equals("Lista de libros")) {
            return;
         }
      }
   
      private void imprimirListaLibros() {
      
      }
   
   // ///////////////////// main //////////////////////////////////
      public static void main(String[] args) {
       //JFrame frame = new IntegratedWQI("Vertical Search Engine Version 1.0");
      }
   
   }
