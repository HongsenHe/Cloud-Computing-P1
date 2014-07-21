package selfFun;

import java.util.*;
import java.io.*;

public class CodeGen {

	private static Scanner mdcInput;

	public static void main(String[] args) throws FileNotFoundException {

		// key is head, value is body
		HashMap<String, String> headBody = new HashMap<String, String>();
		// just store message, parse later
		ArrayList<String> mesList = new ArrayList<String>();

		// create a new file: test.java
		try {
			// create a file named "testfile.txt" in the current working
			// directory
			File myFile = new File("Test.java");
			if (true) {
				myFile.delete();
			}
			myFile.createNewFile();
			System.out.println("Success!");

		} catch (IOException ioe) {
			ioe.printStackTrace();
		}

		// write something into this file and generate another .java file
		PrintWriter writer = new PrintWriter("Test.java");
		writer.println("package selfFun; \nimport java.util.*;" + "\nimport java.io.*;"
				+ "\npublic class Test{"
				+ "\npublic static void main(String [] args){"
				+ "\n//System.out.println(333);");
		writer.println("HashMap<String, String> lociPattern1 = new HashMap<String, String> ();");
		writer.println("HashMap<String, String> lociBody1 = new HashMap<String, String> ();");
		writer.println("ArrayList<String> mesList1 = new ArrayList<String>();");
		writer.println("String message1;");

		mdcInput = new Scanner(System.in);
		String input = "";
		while (mdcInput.hasNextLine()) {
			String line = mdcInput.nextLine().trim();
			char[] lineChar = line.toCharArray();
			// delete comments
			String b = "";
			for (int i = 0; i < lineChar.length; i++) {
				if (lineChar[i] == '/' && lineChar[i + 1] == '/') {
					if (i == 0) {
						b = line.substring(0, lineChar.length);
					} else {
						b = line.substring(i, lineChar.length);
					}
					line = line.replace(b, "").trim();
				}
			}
			input += line;
		}
		//System.out.println("New Output: " + input);

		// now we have a input without other formats
		char[] inputCharArr = input.toCharArray();
		// ***** Key Point *****
		// start parsing from char[0] to the end
		int i = 0;
		while (i < inputCharArr.length) {
			// looking for head and its body, then remove
			String head = "";
			String body = "";
			if (inputCharArr[i] == '?') {
				for (int j = i; j < inputCharArr.length; j++) {
					// in t10 file
					if (inputCharArr[j] == '{' && inputCharArr[0] != '!') {
						head = String.valueOf(inputCharArr).substring(0, j);
						//System.out.println();
						//System.out.println("Head: " + head);
						// remove head part
						String rmHead = head;
						input = input.replace(rmHead, "");
						//System.out.println();
						//System.out.println("Input without Head: " + input);
						char[] inputCharArr1 = input.toCharArray();
						// looking for the body part
						int count = 0;
						for (int m = 0; m < inputCharArr1.length; m++) {
							if (inputCharArr1[m] == '{') {
								count++;
							} else if (inputCharArr1[m] == '}') {
								count--;
							}
							// target the body part, then remove
							if (count == 0) {
								body = input.substring(0, m + 1);

								// store head and body into headBody
								headBody.put(head, body);

							//	System.out.println();
							//	System.out.println("Body: " + body);

								String rmBody = body;
								input = input.replace(rmBody, "");
								// every time delete body part, update this
								inputCharArr = input.toCharArray();
//								System.out.println();
//								System.out.println("Input without Body: "
//										+ input);

								i = 0;
								break;
							}
						}
						break;
					}

					// in t11 file
					else if (inputCharArr[j] == '{') {
						// go back to check the beginning
						for (int k = i; k > 0; k--) {
							if (inputCharArr[k] == '}'
									|| inputCharArr[k] == ';') {
								// target the head and remove it
								head = String.valueOf(inputCharArr).substring(
										k + 1, j);
//								System.out.println();
//								System.out.println("Head: " + head);

								// looking for the body part and remove it
								int count = 0;
								for (int m = j; m < inputCharArr.length; m++) {
									if (inputCharArr[m] == '{') {
										count++;
									} else if (inputCharArr[m] == '}') {
										count--;
									}
									// target the body part
									if (count == 0) {
										body = input.substring(j, m + 1);

										// store head and body into headBody
										headBody.put(head, body);

//										System.out.println();
//										System.out.println("Body: " + body);
										// remove head and body together
										String rmHeadandBody = head + body;
										input = input
												.replace(rmHeadandBody, "");
										// every time delete body part, update
										inputCharArr = input.toCharArray();
//										System.out.println();
//										System.out
//												.println("Input without Head and Body: "
//														+ input);
										i = 0;
										break;
									}
								}
								break;
							}
						}
					} // else if end

				} // ? loop end

			} // ? if end
			i++;
		}
//		System.out.println("\nNew Output: " + input);

		// ******** Key Point **********
		// parse headBody, key is head, value is body, string type
		HashMap<String, String> lociPattern = new HashMap<String, String>();
		HashMap<String, String> lociBody = new HashMap<String, String>();

		for (String key : headBody.keySet()) {
			String headStr = key;
//			System.out.println("Head: " + headStr);
			// separate Loci name as key
			char[] headCharArr = headStr.toCharArray();
			for (int m = 0; m < headCharArr.length; m++) {
				if (headCharArr[m] == '?') {
					String lociName = String.valueOf(headCharArr)
							.substring(0, m).trim();
					String pattern = String.valueOf(headCharArr)
							.substring(m + 1, headCharArr.length).trim();
					// Store lociName and its pattern into this hashMap
					lociPattern.put(lociName, pattern);
//					System.out.println("Loci: " + lociName);
//					System.out.println("Pattern: " + pattern);
					String body = headBody.get(headStr);

					// special handle, remove quote
					body = body.replace('"', ' ');
//					System.out.println("New Body: " + body);
					// Store lociName and its body into this hashMap
					lociBody.put(lociName, body);

					// send the data into test.java file
					writer.println("lociPattern1.put(\"" + lociName + "\",\""
							+ pattern + "\");");
					writer.println("lociBody1.put(\"" + lociName + "\",\""
							+ body + "\");");
					break;
				}
			}
		}
		
		// continue to parse ! keyword then send to test.java file
		String [] mesStrArr = input.split(";");
		for(int mesI = 0; mesI < mesStrArr.length; mesI++){
			String message = mesStrArr[mesI].trim();
			message = message.replace("!", "").trim();
			mesList.add(message);

			// send message into test.java file
			writer.println("message1 = \"" + message + "\";");
			writer.println("mesList1.add(message1);");
			
//			System.out.println("Message: " + message);
			// could delete this message
			//writer.println("System.out.println(mesList1.size() + 100000);");
		}
		
		// now in test.java file we have two hashMap and one messageList
//		System.out.println("fff: " + input);

		// in test.java file, call Mdc.scheduler
		writer.println("Mdc mdc = new Mdc();");
		writer.println("mdc.scheduler(lociPattern1, lociBody1, mesList1);");
		// could delete this message
		//writer.println("System.out.println(\"In test file: lociPattern1: \" + lociPattern1.containsKey(\"L2\"));");
		writer.println("\n}" + "\n}");
		writer.close();

	}
}
