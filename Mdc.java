package selfFun;

import java.util.*;

public class Mdc {

	// key is loci, value is each loci's portPattern, in inner hashMap, key is
	// pattern, value is 0, when all 1 trigger
	public static HashMap<String, HashMap<String, Integer>> lociPatWithSign = new HashMap<String, HashMap<String, Integer>>();
	// store message ordered by delay number
	public static ArrayList<String> sortMesList = new ArrayList<String>();
	// key is message ordered by delay number, value is 0, when 1 delete
	public static HashMap<String, Integer> mesHMWithSign = new HashMap<String, Integer>();
	int count = 0;

	public void scheduler(HashMap<String, String> lociPattern,
			HashMap<String, String> lociBody, ArrayList<String> mesList) {
		//System.out.println("Here is MDC: GOGOGOGOGO!!!");
		// ArrayList<String> sortMesList = new ArrayList<String>();

		// return a sorting message list using the delay number
		sortMesList = sortingMesList(mesList);

		// key is port, value is 0, when 1 delete
		for (int s = 0; s < sortMesList.size(); s++) {
			String key = sortMesList.get(s);
			mesHMWithSign.put(key, 0);
		}

//		 System.out.println("OOOOOOOOOOO: " + mesHMWithSign);
//		 System.out.println(sortMesList);
//		 System.out.println(lociPattern);

		// only store loci list
		ArrayList<String> lociList = new ArrayList<String>();
		for (String key : lociPattern.keySet()) {
			String lociName = key;
			lociList.add(lociName);

			// try to store loci and its pattern with sign, when all 1, then
			// trigger
			String pattern = lociPattern.get(lociName);
			pattern = pattern.replace(")", ";");
			// System.out.println("SSSSSSSSSSS: " + pattern);
			String[] patArr = pattern.split(";");

			// create a inner hashMap of lociPatWithSign
			HashMap<String, Integer> patWSign = new HashMap<String, Integer>();
			// set key is port, value is 0, when satisfy value is 1
			for (int i = 0; i < patArr.length; i++) {
				patWSign.put(patArr[i], 0);
			}

			//System.out.println("AA: " + patWSign);
			lociPatWithSign.put(lociName, patWSign);
			// System.out.println("**********" + patArr[0]);
			// System.out.println(patArr[1]);
			// System.out.println(patArr[2]);

		}

		//System.out.println("FF: " + lociPatWithSign);
		//System.out.println("OLD sorting list: " + sortMesList);

		int i = 0;
		while (i < sortMesList.size()) {
			String eachMes = sortMesList.get(i);
			char[] eachMesCA = eachMes.toCharArray();
			for (int j = 0; j < eachMesCA.length; j++) {
				// capture message lociName
				if (eachMesCA[j] == '@') {
					String mesLoci = String.valueOf(eachMesCA)
							.substring(j + 1, eachMesCA.length).trim();
					eachMes = eachMes.substring(0, j).trim();

					if (lociList.contains(mesLoci)) {
						// if trigger, call this lociName's body
						boolean trigger = judgeTrigger(lociPattern, mesLoci,
								eachMes);

						if (trigger) {
							System.out.println("Trigger SuCceSs!");

//							System.out.println("Before set 0: "
//									+ lociPatWithSign);
							// *** key point ***
							// should set value from 1 to 0, then delete those
							// message from list
							
							for (String key : lociPatWithSign.get(mesLoci)
									.keySet()) {
								lociPatWithSign.get(mesLoci).put(key, 0);
							}
					
							// good job, then delete these messages
//							System.out.println("After set 1: "
//									+ lociPatWithSign);

							// delete these messages
							
							// check available message from beginning
							i = 0;
							// call trigger message, print the result, then
							// done!
							triggerMessage(mesLoci, lociBody);
							
							break;
						}
					}
				}
			}
			i++;
		}

	}

	// judge port name and type
	public boolean judgeTrigger(HashMap<String, String> lociPattern,
			String lociName, String eachMes) {
		// System.out.println("CALLLLLLLLLLLLLLLLLL this method");
		boolean result = false;

		// well, just pattern with loci
		String fullPat = eachMes.concat('@' + lociName);
//		System.out.println("FULL Pattern: " + fullPat);
		// eachMes = P11(true)@L1
		// parse this loci's pattern into portList and typeList
		ArrayList<String> portList = new ArrayList<String>();
		ArrayList<String> typeList = new ArrayList<String>();
		HashMap<String, ArrayList<String>> portType = new HashMap<String, ArrayList<String>>();

		// format the system port and type then store to list
		HashMap<String, Integer> patWSign1 = new HashMap<String, Integer>();
		patWSign1 = lociPatWithSign.get(lociName);
		// System.out.println("KKKKKKKKKK: " + patWSign1);
		for (String key : patWSign1.keySet()) {

			// System.out.println("HHHHHHHHHHHHHH: " + key.trim());
			String pat1 = key.trim();
			char[] pat1CA = pat1.toCharArray();
			for (int j = 0; j < pat1CA.length; j++) {
				if (pat1CA[j] == '(') {
					// store port name into list
					String portName = String.valueOf(pat1CA).substring(0, j)
							.trim();
					portList.add(portName);
					// store type name into list
					String types = String.valueOf(pat1CA)
							.substring(j + 1, pat1CA.length).trim();
					String[] typeArr = types.split(",");
					for (int k = 0; k < typeArr.length; k++) {
						String typ = typeArr[k];
						String[] finalTyp = typ.split(" ");
						String typeName = finalTyp[0].toUpperCase();
						// should keep this?
						// String value = finalTyp[1];
						typeList.add(typeName);
						// duplicated port actually
						portType.put(portName, typeList);

					}
					break;
				}
			}

			// System.out.println("LLLLLLL: " + portList);
			// System.out.println("LLLLLLL: " + typeList);

		}

		// now judge port name then type name if yes then set value to 1
		char[] eachMesCA = eachMes.toCharArray();
		int portFlag = 0;
		int typeFlag = 0;

		for (int m = 0; m < eachMesCA.length; m++) {

			String values = "";
			String mesPort = "";
			// judge port name
			if (eachMesCA[m] == '(') {
				// if the message's port is in loci's portList
				mesPort = String.valueOf(eachMesCA).substring(0, m).trim();
				if (portList.contains(mesPort)) {
					portFlag = 1;
				}
				// get types
				for (int n = m; n < eachMesCA.length; n++) {
					if (eachMesCA[n] == ')') {
						// what if the value is null !P11@L1 #2;
						values = String.valueOf(eachMesCA).substring(m + 1, n)
								.trim();
					}
				}
			}

			// judge type with a given port
			if (values == "") {
				// check the port's type **********************
			} else {
				String[] valuesArr = values.split(",");
				for (int p = 0; p < valuesArr.length; p++) {
					String eachValue = valuesArr[p].trim();
					// after eachValue, then judge with pattern
					String mesType = "";
					if (eachValue.toUpperCase().equals("TRUE")
							|| eachValue.toUpperCase().equals("FALSE")) {
						mesType = "BOOL";
					} else {
						// if it is a integer
						try {
							// this number maybe use later
							int num = Integer.parseInt(eachValue);
							mesType = "INT";
							// is an integer!
						} catch (NumberFormatException e) {
							System.out
									.println("\nSo far it just can regocnize Integer and Boolean type.");
							System.exit(0);
						}
					}
					// System.out.println("111111111111: " + mesType);
					// if the type is in a given port
					if (portType.get(mesPort).contains(mesType)) {
						typeFlag = 1;
					}
				}
			}

			// if this message's port and type are in a given pattern
			// then set that port's sign is 1.
			// if all port's sign of a given loci are 1, then trigger!
			if (portFlag == 1 && typeFlag == 1) {

				for (String key1 : patWSign1.keySet()) {
					char[] keyCA = key1.toCharArray();
					for (int x = 0; x < keyCA.length; x++) {
						if (keyCA[x] == '(') {
	
							String patWSign1KeyPort = String.valueOf(keyCA)
									.substring(0, x).trim();

							if (patWSign1KeyPort.equals(mesPort)
									&& patWSign1.get(key1).equals(0)) {
										
								// set the value to 1
								// lociPatWithSign.put(key1, 1);
								// System.out.println("!!!!!!: "
								// + patWSign1KeyPort);
								// System.out.println("!!!!!: " + patWSign1);
								// System.out.println("!!!!: "
								// + patWSign1.get(key1));
								patWSign1.put(key1, 1);
								// System.out.println("!!!!!!!!!!!!!: " +
								// patWSign1);
								// update static variable: lociPatWithSign
								lociPatWithSign.put(lociName, patWSign1);
								// System.out.println("FINALFINALFINAL: " +
								// lociPatWithSign);

								// if trigger, then return true;
								// System.out.println("pppppp: " +
								// lociPatWithSign.get(lociName).get(key1));

								// **** set 0 to 1 from mesHMWithSign
								mesHMWithSign.remove(fullPat);
								mesHMWithSign.put(fullPat, 1);

//								System.out.println("FIND: *****" +fullPat);
								if (lociPatWithSign.get(lociName).get(key1) == 1) {

									count++;
//								 System.out.println("ppppppppppp: " +
//									lociPatWithSign.get(lociName).size());
									if (count == lociPatWithSign.get(lociName)
											.size()) {
										result = true;
										// System.out.println("JESUSJESUSJESUS");
									}
								}

								break;
							}
						}
					}
				}
			}
			
		}
//		System.out.println("HOW about this?: "
//				+ mesHMWithSign);
//		 System.out.println("989898989898: " + typeList);
//		 System.out.println("989898989898: " + portList);
//		 System.out.println("989898989898: " + portType);
//		 System.out.println("989898989898: " + lociPatWithSign);

		return result;
	}

	// will return trigger message
	public void triggerMessage(String lociName,
			HashMap<String, String> lociBody) {
		String body = "";
		body = lociBody.get(lociName);
		System.out.println("\nWith Java, the following messages are not be translate: \n" + body);
		
		
		char[] bodyCA = body.toCharArray();
		for (int i = 0; i < bodyCA.length; i++) {
			if (bodyCA[i] == '!') {
				for (int j = i; j < bodyCA.length; j++) {
					if (bodyCA[j] == ';') {
						String mesInBody = String.valueOf(bodyCA)
								.substring(i + 1, j).trim();

						// should store this new message in body
						sortMesList.add(mesInBody);
//						System.out.println("NEW sorting eeeeeeeelist: "
//								+ sortMesList);

						sortingMesList(sortMesList);
//						System.out.println("NEW sorting list: " + sortMesList);
						break;
					}
				}
			}

		}
	}

	public ArrayList<String> sortingMesList(ArrayList<String> mesL) {
		// delay number as key, others as number
		HashMap<Double, String> mesHashM = new HashMap<Double, String>();

		int mesI = 0;
		double index = 1;

		// parsing each message
		while (mesI < mesL.size()) {
			String eachMes = mesL.get(mesI);
			char[] eachMesCharArr = eachMes.toCharArray();

			// parsing each char in one message
			for (int i = 0; i < eachMesCharArr.length; i++) {
				// default value
				double delayNum = 0.0;
				String portValue = "";
				int flag = 0;

				// if the message contains a delay number
				if (eachMesCharArr[i] == '#') {
					String delay = String.valueOf(eachMesCharArr)
							.substring(i + 1, eachMesCharArr.length).trim();
					delayNum = Double.parseDouble(delay);
					// store port name and value
					portValue = String.valueOf(eachMesCharArr).substring(0, i)
							.trim();
					i = eachMesCharArr.length - 1;
					flag = 1;
				}
				// if the message doesn't have a delay number, then store 0
				else if (eachMesCharArr[i] != '#') {
					if (i == eachMesCharArr.length - 1) {
						portValue = eachMes;
						flag = 1;
					} else {
						continue;
					}
				}

				// now we have a delay number and sorting with it
				if (flag == 1) {
					// if the current key is in hashMap
					if (mesHashM.containsKey(delayNum)) {
						// #1 -> #1.01
						delayNum = delayNum + index / 100;
						index++;
					}
					mesHashM.put(delayNum, portValue);

				}
			}
			mesI++;
		}

		// now we have a hashMap, key is delay number value is port and value
		// here is sorting
		Object[] key = mesHashM.keySet().toArray();
		Arrays.sort(key);
		for (int k = 0; k < key.length; k++) {
			String portBody = mesHashM.get(key[k]);
			// update the message list, message body only, no delay, sorting by
			// delay time
			if (!sortMesList.contains(portBody)) {
				sortMesList.add(portBody);
			}
		}

		// result only contains the port and type name with ordered
		return sortMesList;
	}

}
