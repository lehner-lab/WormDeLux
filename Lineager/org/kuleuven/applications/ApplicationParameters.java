package org.kuleuven.applications;

import java.applet.Applet;
import java.util.Hashtable;

public class ApplicationParameters {
  protected Hashtable<String, String> parameters = new Hashtable<String, String>();
  
  public ApplicationParameters(String[] arguments) {
    for (String argument: arguments) {
      String key = "", value = "";
      int index = argument.indexOf("=");

      if (index != -1) {
        key = argument.substring(0, index);
        key =key.trim();
        value = argument.substring(index + 1);
        value = value.trim();
        parameters.put(key.toUpperCase(), value);
      }
    }
  }
  
  public ApplicationParameters(Applet applet) {
    String[][] parameterInfo = applet.getParameterInfo();
    
    for (String[] parameter: parameterInfo) {
      String parameterName = parameter[0].toUpperCase();
      String parameterValue = applet.getParameter(parameterName);
      
      if (parameterValue != null)
        parameters.put(parameterName, parameterValue);
    }
  }
  public void add(String key, String value){
    parameters.put(key.toUpperCase(),value);
  }
  public String get(String parameterName) {
    return parameters.get(parameterName.toUpperCase());
  }
}
