package org.kuleuven.utilities;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class StringUtilities {
	/**
	 * this is a slow operation for large arrays due to the copying of large char arrays. If you want to do many replacements consider using the underlying char array directly.
	 * @param s
	 * @param pos
	 * @param c
	 * @return
	 */
	public static String replaceCharAt(String s, int pos, char c) {
		
		   return s.substring(0,pos) + c + s.substring(pos+1);
		}
  public static String commaSeparatedString(Collection<? extends Object> objects) {
    StringBuffer result = new StringBuffer();
    Iterator<? extends Object> iterator = objects.iterator();
    
    if (iterator.hasNext()) {
      result.append(iterator.next());
      
      while (iterator.hasNext()) {
        result.append(", ");
        result.append(iterator.next());
      }
    }
    
    return result.toString();
  }
  
  public static String join(Collection<?> s, String delimiter) {
    StringBuffer buffer = new StringBuffer();
    Iterator<?> iter = s.iterator();
    if (iter.hasNext()) {
      buffer.append(iter.next().toString());
    }
    while (iter.hasNext()) {
      buffer.append(delimiter);
      buffer.append(iter.next().toString());
    }
    return buffer.toString();
  }
  
  public static String join(Object[] objects, String delimiter) {
    StringBuffer buffer = new StringBuffer();
    if (objects.length != 0) buffer.append(objects[0].toString());
    for (int i = 1; i < objects.length; i++){
      buffer.append(delimiter);
      buffer.append(objects[i].toString());
    }
    return buffer.toString();
  }
  
  
  public static int[] parseIntegersFromCommaSeparatedString(String values) {
    StringTokenizer stringTokenizer = new StringTokenizer(values, ", ", false);
    int[] result = new int[stringTokenizer.countTokens()];
    
    for (int i = 0; i < result.length; i++)
      result[i] = Integer.parseInt(stringTokenizer.nextToken());
    
    return result;
  }
  
  public static int twoHexDigitsToInt(String value, int index) {
    return Integer.parseInt(value.substring(index, index + 2), 16);
  }
  public static boolean isInteger(String string){
    try{
    Integer.parseInt(string);
    }catch (NumberFormatException e) {
      return false;
    }
    return true;
  }
  public static boolean isNumber(String string) {
    string.trim();
    if (string.length()==1) {
      return Character.isDigit(string.charAt(0));
    }
    Pattern pattern = Pattern.compile("^-?\\d[0-9.,]*E?-?[0-9]*\\d$");
    return pattern.matcher(string).matches();
  }
  
  public static boolean isRomanNumeral(String string) {
    return (string.equals("I") ||
        string.equals("II") ||
        string.equals("III") ||
        string.equals("IV") ||
        string.equals("V") ||
        string.equals("VI") ||
        string.equals("VII") ||
        string.equals("VIII") ||
        string.equals("IX") ||
        string.equals("IX"));
  }
  
  public static boolean isGreekLetter(String string) {
    String lcstring = string.toLowerCase();
    return (lcstring.equals("alpha") ||
        lcstring.equals("beta") ||
        lcstring.equals("gamma") ||
        lcstring.equals("delta") ||
        lcstring.equals("epsilon") ||
        lcstring.equals("zeta") ||
        lcstring.equals("eta") ||
        lcstring.equals("theta") ||
        lcstring.equals("iota") ||
        lcstring.equals("kappa") ||
        lcstring.equals("lambda") ||
        lcstring.equals("mu") ||
        lcstring.equals("nu") ||
        lcstring.equals("xi") ||
        lcstring.equals("omicron") ||
        lcstring.equals("pi") ||
        lcstring.equals("rho") ||
        lcstring.equals("sigma") ||
        lcstring.equals("tau") ||
        lcstring.equals("upsilon") ||
        lcstring.equals("phi") ||
        lcstring.equals("chi") ||
        lcstring.equals("psi") ||
        lcstring.equals("omega"));
  }
  
  //Adds PSF file specific escape characters to string
  //Author: Martijn
  public static String escape(String string){
    StringBuffer result = new StringBuffer();
    for (int i = 0; i < string.length(); i++){
      char currentChar = string.charAt(i);
      if (currentChar == '"' || currentChar == '?' || currentChar == ';' || currentChar == '\\' || currentChar == '|') {
        result.append('\\');         
      } 
      result.append(currentChar);
    }
    return result.toString();
  }
  
  //Removes any escape characters from string
  //Author: Martijn
  public static String unescape(String string){
    StringBuffer result = new StringBuffer();  
    if (string.length() > 0){
      if (string.charAt(0)=='"' && string.charAt(string.length()-1)=='"'){
        result.append(string.substring(1,string.length()-1));
      } else {
        boolean escape = false;
        char currentchar;
        for (int i = 0; i < string.length(); i++){
          currentchar = string.charAt(i);
          if (escape){
            escape = false;
            result.append(currentchar);
          }else{
            if (currentchar == '\\') {
              escape = true;
            } else {
              result.append(currentchar);
            }         
          }
        }
      }
    }
    return result.toString();
  }
  
  //Safesplit works the same as default split, but takes escapes into account
  //Author: Martijn
  public static List<String> safeSplit(String string, char divider){
    List<String> result = new ArrayList<String>();
    if(string.length()==0){
      result.add("");
      return result;
    }
    boolean literal = false;
    boolean escape = false;
    int startpos = 0;
    int i = 0;
    char currentchar;
    while (i < string.length()){
      currentchar = string.charAt(i);
      if (currentchar =='"'){literal = !literal;}
      if (!literal && (currentchar == divider && !escape)){
        result.add(string.substring(startpos,i));
        startpos = i+1;
      }
      if (currentchar == '\\'){escape = !escape;} else {escape = false;}
      i++;
    }
    //if (startpos != i){
      result.add(string.substring(startpos,i));
    //}
    return result;
  } 
  

  
  public static boolean containsNumber(String string) {
    for (int i = 0; i < string.length(); i++){
      if (string.charAt(i)< 58 && string.charAt(i)> 47){
        return true;
      }
    }
    return false;
  }

  public static int countNumbers(String string) {
    int total = 0;
    for (int i = 0; i < string.length(); i++){
      if (string.charAt(i)< 58 && string.charAt(i)> 47){
        total++;
      }
    }
    return total;
  }

  public static boolean containsLetter(String string) {
    for (int i = 0; i < string.length(); i++){
      if (Character.isLetter(string.charAt(i))){
        return true;
      }
    }
    return false;
  }  
  
  
  public static int countLetters(String string) {
    int total = 0;
    for (int i = 0; i < string.length(); i++){
      if (Character.isLetter(string.charAt(i))){
        total++;
      }
    }
    return total;
  }  

  public static boolean containsCurlyBracket(String string) {
    for (int i = 0; i < string.length(); i++){
      if (isCurlyBracket(string.charAt(i))){
        return true;
      }
    }
    return false;
  }  
  

  public static boolean containsParenthesis(String string) {
    for (int i = 0; i < string.length(); i++){
      if (isParenthesis(string.charAt(i))){
        return true;
      }
    }
    return false;
  }  
  

  public static boolean containsBracket(String string) {
    for (int i = 0; i < string.length(); i++){
      if (isBracket(string.charAt(i))){
        return true;
      }
    }
    return false;
  }
  

  public static boolean containsArrow(String string) {
    for (int i = 0; i < string.length(); i++){
      if (isArrow(string.charAt(i))){
        return true;
      }
    }
    return false;
  }

  public static boolean isParenthesis(char ch) {
    return (ch == ('(') ||
        ch == (')'));
  }
  
//Checks whether the word is a brackets
  //Author: Kristina
  public static boolean isBracket(char ch) {
    return (ch == ('[') ||
        ch == (']'));
  }
  

  public static boolean isArrow(char ch) {
    return (ch == ('<') ||
        ch == ('>'));
  }
  
//Checks whether the word is a curly bracket
  //Author: Kristina
  public static boolean isCurlyBracket(char ch) {
    return (ch == ('{') ||
        ch == ('}'));
  }
  
  //Converts a string to a list of words
  //Author: Martijn
  public static List<String> mapToWords(String string) {
    List<String> result = new ArrayList<String>();
    
    int start = 0;
    int i = 0;
    for (; i < string.length(); i++){
      char ch = string.charAt(i);
      if (!Character.isLetterOrDigit(ch) &&
          !(ch == '\'' && i>0 && Character.isLetter(string.charAt(i-1)) && string.length()-1 > i && string.charAt(i+1) == 's' && (string.length()-2 == i || !Character.isLetterOrDigit(string.charAt(i+2))))){ //leaves ' in possesive pattern
        if (start != i) {
          result.add(string.substring(start,i));
        }
        start = i+1;
      }
    }
    if (start != i) {
      result.add(string.substring(start,i));
    }    
    return result;
  }
  
  //Returns a string with the current time
  //Author: Martijn
  public static String now(){
    Date d = new Date();
    //DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM);
    DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
    return df.format(d);
  }
  
  //Checks whether the word is an abbreviation
  //Author: Martijn
  public static boolean isAbbr(String word){
    int lowercase = 0;
    int uppercase = 0;
    int number = 0;
    int charInt = 0;
    for (int i = 0; i < word.length(); i++){
      charInt = word.charAt(i);
      if (charInt<58){
        if (charInt>47) {number++;}
      } else if (charInt<91) {
        if (charInt>64) {uppercase++;}
      } else if (charInt<123 && charInt>96) {lowercase++;}
    }
    return (uppercase>0 && lowercase < uppercase);
  }
  
  /**
   * If only the first letter of a word is a capital, the word is reduced to lowercase, else the original string is returned
   * @param string
   * @return
   */
  public static String firstLetterToLowerCase(String string){
    boolean uppercase = false;
    int charInt = 0;
    for (int i = 1; i < string.length(); i++){
      charInt = string.charAt(i);
      if (charInt<91) 
        if (charInt>64) {uppercase = true; break;}
    }
    if (!uppercase) return string.toLowerCase(); else return string;
  }
  
  public static int countsCharactersInUpperCase(String string){
    int uppercase = 0;
    int charInt = 0;
    for (int i = 0; i < string.length(); i++){
      charInt = string.charAt(i);
      if (charInt>64 && charInt<91){
        uppercase++;
      }
    }
    return uppercase;
  }
  
  //Converts a double to a formatted string. Examples of valid patterns are:
  //"###,###.###"
  //"###.##"
  //"000000.000"
  //"$###,###.###"
  //"\u00a5###,###.###"
  //# indicates optional number, 0 indicates forced number (will be printed as 0 when 0)
  //Author: Martijn
  public static String formatNumber(String pattern, double number){
    DecimalFormat myFormatter = new DecimalFormat(pattern);
    return myFormatter.format(number);
  }
  
  public static boolean isPlural(String string){
    if (string.length() > 1)
      if (string.charAt(string.length()-1) == 's')
        if (Character.isLetter(string.charAt(string.length()-2)))
          return true;
    return false; 
  }
  
  public static String findBetween(String source, String pre, String post){
    int start = source.indexOf(pre);
    if (start == -1) return "";
    int end = source.indexOf(post, start+pre.length());
    if (end == -1) return "";
    return source.substring(start+pre.length(), end);    
  }
  
  public static List<String> multiFindBetween(String source, String pre, String post){
    List<String> result = new ArrayList<String>();
    int start = 0;
    int end = 0;
    while (start != -1 && end != -1){
      start = source.indexOf(pre, end);
      if (start != -1){
        end = source.indexOf(post, start+pre.length());
          if (end != -1)
            result.add(source.substring(start+pre.length(), end));
      }
    }
    return result;
  }  
  /**
   * Returns true if every parenthesis in the string is matched
   * @param string
   * @return
   */
  public static boolean parenthesisMatch(String string){
    int count = 0;
    for (int i = 0; i < string.length(); i++){
      char ch = string.charAt(i);
      if (ch == '(')
        count++;
      else if (ch == ')'){
        count--;
        if (count == -1)
          return false;
      }
    }
    return (count == 0);
  }
  
  public static String daysToSortableDateString(long days) {   
    long ms = days * DateUtilities.day;
    ms -= calendar.getTimeZone().getOffset(ms);
    calendar.setTimeInMillis(ms);
    StringBuilder sb = new StringBuilder();
    sb.append(calendar.get(Calendar.YEAR));
    sb.append(StringUtilities.formatNumber("00", calendar.get(Calendar.MONTH)+1));
    sb.append(StringUtilities.formatNumber("00", calendar.get(Calendar.DATE)));
    return sb.toString();
  }

  
  public static String millisecondsToSortableTimeString(long ms) {
    ms -= calendar.getTimeZone().getOffset(ms+2*DateUtilities.hour);
    calendar.setTimeInMillis(ms);
    StringBuilder sb = new StringBuilder();
    sb.append(calendar.get(Calendar.YEAR));
    sb.append(StringUtilities.formatNumber("00", calendar.get(Calendar.MONTH)+1));
    sb.append(StringUtilities.formatNumber("00", calendar.get(Calendar.DATE)));
    sb.append(StringUtilities.formatNumber("00", calendar.get(Calendar.HOUR)));
    sb.append(StringUtilities.formatNumber("00", calendar.get(Calendar.MINUTE)));
    sb.append(StringUtilities.formatNumber("00", calendar.get(Calendar.SECOND)));
    return sb.toString();
  }
  
  public static long sortableTimeStringToDays(String string){
    int year = Integer.parseInt(string.substring(0,4));
    int month = Integer.parseInt(string.substring(4,6))-1;
    int day = Integer.parseInt(string.substring(6,8));
    calendar.set(year, month, day);    
    long time = calendar.getTimeInMillis();
    time += calendar.getTimeZone().getOffset(time);

    if (string.length() > 8){
      int hour = Integer.parseInt(string.substring(8,10));
      time += hour * 60 * 60 * 1000;
      if (string.length() > 8){
        int minute = Integer.parseInt(string.substring(10,12));
        time += minute * 60 * 1000;
        if (string.length() > 8){
          int second = Integer.parseInt(string.substring(12,14));
          time += second * 1000;
        }
      }
    }
    return time / DateUtilities.day;
  }
  
  private static Calendar calendar = new GregorianCalendar();
  
 /* public static String replaceInternationalChars(String string){
    char result[] = string.toCharArray();
    for (int i = 0; i < result.length; i++){
      char ch = result[i];
      if (ch == '??') result[i] = 'O';
      else if (ch == '??') result[i] = 'o';
      else if (ch == '??') result[i] = 'o';
      else if (ch == '??') result[i] = 'o';
      else if (ch == '??') result[i] = 'o';
      else if (ch == '??') result[i] = 'o';
      else if (ch == '??') result[i] = 'o';
      else if (ch == '??') result[i] = 'i';
      else if (ch == '??') result[i] = 'i';
      else if (ch == '??') result[i] = 'i';
      else if (ch == '??') result[i] = 'i';
      else if (ch == '??') result[i] = 'e';
      else if (ch == '??') result[i] = 'e';
      else if (ch == '??') result[i] = 'e';
      else if (ch == '??') result[i] = 'e';
      else if (ch == '??') result[i] = 'e';
      else if (ch == '??') result[i] = 'c';
      else if (ch == '??') result[i] = 'a';
      else if (ch == '??') result[i] = 'a';
      else if (ch == '??') result[i] = 'a';
      else if (ch == '??') result[i] = 'a';
      else if (ch == '??') result[i] = 'a';
      else if (ch == '??') result[i] = 'a';
      else if (ch == '??') result[i] = 'u';
      else if (ch == '??') result[i] = 'u';
      else if (ch == '??') result[i] = 'u';
      else if (ch == '??') result[i] = 'y';
      else if (ch == '??') result[i] = 'n';
    }
    return new String(result);
  }*/
  
}
