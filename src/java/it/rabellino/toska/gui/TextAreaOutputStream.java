package it.rabellino.toska.gui;

import java.awt.TextArea;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
/**
 * @author <a href="gianugo@apache.org">Gianugo Rabellino</a>
 *
 */
public class TextAreaOutputStream extends OutputStream {
  
  protected JTextArea area;
  protected JScrollPane pane;
  protected byte[] bytes = new byte[80];
  protected int written;
  /**
   * Constructor for TextAreaOutputStream.
   */
  public TextAreaOutputStream() {
    super();
    pane  = new JScrollPane();
    //pane.getViewport().setAutoscrolls(true);
    area  = new JTextArea(5,40);
    area.setLineWrap(true);
    area.setWrapStyleWord(true);
    
    pane.getViewport().add(area);
    area.append("This is the log window. Messages will appear here\n");
    written = 0;
  }

  /**
   * @see OutputStream#write(int)
   */
  public void write(int wrt) throws IOException {
    System.out.println("int");
    if (written < 80) {
      bytes[written] = (byte)wrt;
      written++;
      return;
    }     
    area.append(new String(bytes, 0, written));
    written = 0;    
  }
  
  public void write(byte[] bytes, int offset, int len) {    
    area.append(new String(bytes, offset, len));
  } 

  public void write(byte[] bytes) throws IOException {
    area.append(new String(bytes)); 
    this.area.setCaretPosition(area.getText().length());
    //this.pane.getViewport().setViewPosition()
  } 

  public void flush() {
    area.append(new String(bytes, 0, written));
    written = 0;    
  }
  
  /**
   * Gets the pane.
   * @return Returns a JScrollPane
   */
  public JScrollPane getPane() {
    return pane;
  }

  public JTextArea getArea() {
    return area;
  }

  public static void main(String args[]) {
    JFrame mainFrame = new JFrame("Test frame");
    TextAreaOutputStream taos = new TextAreaOutputStream();
    mainFrame.getContentPane().add(taos.getPane());
    mainFrame.setVisible(true);
    try {
      taos.write("This is a test".getBytes());
      //taos.flush();
    } catch (IOException ioe) {
      ioe.printStackTrace(System.err);
    }    
  }


}
