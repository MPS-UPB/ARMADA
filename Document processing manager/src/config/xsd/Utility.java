package config.xsd;
import java.io.File;

import javax.swing.JOptionPane;

public class Utility {

	public static boolean testPropertyExistsInList(String property,
			String[] list) {

		for (String str : list) {
			if (str.equals(property))
				return true;
		}
		return false;
	}

	public static boolean checkFileExists(String path) {
		File f = new File(path);
		
		return f.exists();
	}
	public static boolean deleteFile(String path){
		File file = new File(path);
		return file.delete();
	}
	
    static public void showError(String msg) {
        JOptionPane.showMessageDialog(null,
                msg,
                "Eroare",
                JOptionPane.ERROR_MESSAGE);
    }
    
    static public void showMessage(String title, String msg) {
        JOptionPane.showMessageDialog(null,
                msg,
                title,
                JOptionPane.PLAIN_MESSAGE);
    }
  
}
