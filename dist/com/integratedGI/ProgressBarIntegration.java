/*
 * Modified source code (see details below) to run the first step in the integration process
 * of WQI is background and showing the progress of the task.
 * Heidy M. Marin Castro
 * 2014 All rights reserved.
*/

/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 
package com.integratedGI;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.beans.*;
import java.util.*;
import java.io.*;
import com.sources.*;
import com.fieldsIdentify.*;
import com.integration.tree.*;

public class ProgressBarIntegration{

   private JProgressBar progressBar;
   private JTextArea taskOutput;
   private JDialog dlg;
   NodeSchema ns;
   
   
   public ProgressBarIntegration(JFrame frame) {
      ns = null;
      
      JPanel panelMain = new JPanel();
      panelMain.setLayout(new BorderLayout());      
      progressBar = new JProgressBar(0, 200);
      progressBar.setValue(0);
      progressBar.setStringPainted(true);
   
      taskOutput = new JTextArea(5, 20);
      taskOutput.setMargin(new Insets(5,5,5,5));
      taskOutput.setEditable(false);
   
      JPanel panel = new JPanel();
      panel.add(progressBar);
   
      panelMain.add(panel, BorderLayout.PAGE_START);
      panelMain.add(new JScrollPane(taskOutput), BorderLayout.CENTER);
      panelMain.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
   
      panelMain.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      panelMain.setOpaque(true); //content panes must be opaque
   
      dlg = new JDialog(frame, "Buiding the Integrated WQI", true);   
      dlg.setContentPane(panelMain);     
      dlg.pack();
      System.out.println("========= SHOWED ================");   
      
      
               //arrancar un hilo para ejecutar la identificacion de WQI
      Thread work = 
         new Thread() {
            public void run() {
               IntegrationMain fi = new IntegrationMain();
               ns = fi.mainProcess(progressBar,taskOutput);
               dlg.setVisible(false);
               dlg.dispose();   
            }  
         };
   
      work.start();
      dlg.setVisible(true);   
   }

      
         /**
    * Create the GUI and show it. As with all GUI code, this must run
    * on the event-dispatching thread.
    */
    
          
   public NodeSchema getResult(){
      return ns;
   }    
/*
   public static void main(String[] args) {
      //Schedule a job for the event-dispatching thread:
      //creating and showing this application's GUI.
      javax.swing.SwingUtilities.invokeLater(
            new Runnable() {
               public void run() {
                  createAndShowGUI();
               }
            });
   }
   */
}