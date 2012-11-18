package doc.commons;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class DialogOkCancel {
	
	public static void main(String argv[]) {
	    if (JOptionPane.showConfirmDialog(new JFrame(),
	    		argv[0], argv[1],
	        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
	      System.exit(0);

	  }
}
