package org.kuleuven.utilities;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.List;

public class ObjectUtilities {
  public static void write (Object object, String filename) throws IOException {
    ObjectOutputStream out = null;

    try {
        out = new ObjectOutputStream (new FileOutputStream (filename));

        out.writeObject (object);
        out.flush ();
    } finally {
        if (out != null) {
            try {
                out.close ();
            } catch (IOException exception) {}
        }
    }
}

public static Object read (String filename) throws ClassNotFoundException, IOException {
    ObjectInputStream in = null;

    try {
        in = new ObjectInputStream (new FileInputStream (filename));

        return in.readObject ();
    } finally {
        if (in != null) {
            try {
                in.close ();
            } catch (IOException exception) {}
        }
    }
}
public static double[] makePrimitiveArrayFromList(List<Double> list){
	double[] result =new double[list.size()];
	for (int i=0;i<list.size();i++){
		result[i]=list.get(i);	
	}
	return result;
}
public static String[] makeArrayFromCollection(Collection<String> list){
	return list.toArray(new String[list.size()]);
}
}
