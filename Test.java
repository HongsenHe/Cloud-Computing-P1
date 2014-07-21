package selfFun; 
import java.util.*;
import java.io.*;
public class Test{
public static void main(String [] args){
//System.out.println(333);
HashMap<String, String> lociPattern1 = new HashMap<String, String> ();
HashMap<String, String> lociBody1 = new HashMap<String, String> ();
ArrayList<String> mesList1 = new ArrayList<String>();
String message1;
lociPattern1.put("L1","P11(bool bMin) P12(int i1) P12(int i2)");
lociBody1.put("L1","{int iResult =bMin ? i1 < i2 ? i1 : i2 : i1 < i2 ? i2 : i1;! P21(bMin, iResult) @ L2;}");
lociPattern1.put("L2","P21(bool bMin, int iResult)");
lociBody1.put("L2","{if ( bMin ) {cout <<  The minimum is   << iResult << endl;} else {cout <<  The maximum is   << iResult << endl;}}");
message1 = "P11(true)@L1";
mesList1.add(message1);
message1 = "P12(3)@L1";
mesList1.add(message1);
message1 = "P12(5)@L1";
mesList1.add(message1);
Mdc mdc = new Mdc();
mdc.scheduler(lociPattern1, lociBody1, mesList1);

}
}
