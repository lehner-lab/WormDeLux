package org.kuleuven.utilities;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DirectoryUtilities {
  public static boolean deleteFile(File dir) {
    if (dir.isDirectory()) {
        String[] children = dir.list();
        for (int i=0; i<children.length; i++) {
            boolean success = deleteFile(new File(dir, children[i]));
            if (!success) {
                return false;
            }
        }
    }

    // The directory is now empty so delete it
    return dir.delete();
}
  public static List<File> getFileListing(File dir) {
    List<File> result = new ArrayList<File>();
    for(String filename: dir.list()) 
      result.add(new File(dir, filename));
    return result;
  }
  public static boolean deleteFiles(java.util.Collection<String> files2BeRemoved) {
		boolean result = true;
		for (String file : files2BeRemoved) {
			File f = new File(file);
			if(f.exists()){
				if(!deleteFile(f))
					result = false;		
			}				
		}
		return result;
	}

}
