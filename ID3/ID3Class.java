package ID3;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class ID3Class {
    
        public static ArrayList<Double> fList = new ArrayList<>();
	public static ArrayList<Double> tempgainList;
	static String pathName;
	static int row;
	static int noofattributes;
	static String filename1, filename2, filename3;
	public static ArrayList<ArrayList<Integer>> inputArray = new ArrayList<>();
	public static ArrayList<Integer> inputsubArray = new ArrayList<>();
	public static Hashtable<String, String> partitionList = new Hashtable<>();
	public static ArrayList<ArrayList<Double>> gainList = new ArrayList<>();
	
	/**
	 * @param args
	 */

        public static void main(String[] args) {

		pathName = System.getProperty("user.dir") + (ID3Class.class.getPackage() == null ? "" : "\\" + "\\src\\" + ID3Class.class.getPackage().getName().replace('.', '\\'));
		
                Scanner s1 = new Scanner(System.in);
		System.out.print("The name of dataset file is: ");
		filename1 = s1.nextLine();
		
                Scanner s2 = new Scanner(System.in);
		System.out.print("The name of partition file is: ");
		filename2 = s1.nextLine();

		Scanner s3 = new Scanner(System.in);
		System.out.print("The name of output file is: ");
		filename3 = s1.nextLine();

		try {
			readSourceFile(filename1);
			readPartitionFile(filename2);
		} catch (IOException e) {
			e.printStackTrace();
		}

		cal_Entropy();
		
                partition();

	}

	public static void readSourceFile(String filename) throws IOException {
		java.io.FileReader infile = new java.io.FileReader(pathName + "\\" + filename);
		java.util.Scanner indata = new java.util.Scanner(infile);
		String temp = "";
		temp = indata.nextLine();
		String[] firstrow = temp.split(" ");
		row = Integer.parseInt(firstrow[0]);
		noofattributes = Integer.parseInt(firstrow[1]);
		temp = "";
		inputArray = new ArrayList<>(noofattributes);

		for (int i = 0; i < noofattributes; i++) {
			infile = new java.io.FileReader(pathName + "\\" + filename);
			indata = new java.util.Scanner(infile);
			temp = indata.nextLine();
			temp = "";
			inputsubArray = new ArrayList<>();

			while (indata.hasNextLine()) {
				temp = indata.nextLine();
				String[] inputs = temp.split(" ");
				inputsubArray.add(Integer.parseInt(inputs[i]));
			}
			inputArray.add(inputsubArray);
		}
		infile.close();
	}

	public static void readPartitionFile(String filename) throws IOException {
		java.io.FileReader infile = new java.io.FileReader(pathName + "\\" + filename);
		java.util.Scanner indata = new java.util.Scanner(infile);
		String temp = "";
		while (indata.hasNextLine()) {
			temp = indata.nextLine();
			String[] tempArray = temp.split(" ");
			String str = "";
			for (int i = 1; i < tempArray.length; i++) {
				str += tempArray[i] + " ";
			}
			str = str.substring(0, str.length() - 1);
			partitionList.put(tempArray[0], str);
		}
		infile.close();
	}

	public static void cal_Entropy() {
		int tempRow;

		ArrayList<Double> tempEntropyList;

		for (Map.Entry<String, String> entry : partitionList.entrySet()) {

			tempEntropyList = new ArrayList<>();
			tempgainList = new ArrayList<>();

			String[] temp = entry.getValue().split(" ");
			tempRow = temp.length;
			int nozero = 0, noone = 0;
			for (int i = 0; i < temp.length; i++) {

				if (inputArray.get(noofattributes - 1).get(Integer.parseInt(temp[i]) - 1) == 1) {
					noone++;
				} else if (inputArray.get(noofattributes - 1).get(Integer.parseInt(temp[i]) - 1) == 0) {
					nozero++;
				}
			}
			double first, second;
			if (nozero == 0 || noone == 0) {
				first = 0;
				second = 0;
			} else {
				first = (double) (((double) nozero / (double) tempRow) * (Math.log10(tempRow / nozero) / Math.log10(2)));
				second = (double) (((double) noone / (double) tempRow) * (Math.log10(tempRow / noone) / Math.log10(2)));
			}
			double entropy = first + second;

                        for (int j = 0; j < noofattributes - 1; j++) {
				int subzero = 0, subone = 0, subtwo = 0;
				int sub_zero_target_zero = 0, sub_zero_target_one = 0, sub_zero_target_two, sub_one_target_zero = 0, sub_one_target_one = 0, sub_one_target_two, sub_two_target_zero = 0, sub_two_target_one = 0, sub_two_target_two;
				for (int i = 0; i < temp.length; i++) {
					if (inputArray.get(j).get(Integer.parseInt(temp[i]) - 1) == 1) {
						subone++;
						if (inputArray.get(noofattributes - 1).get(Integer.parseInt(temp[i]) - 1) == 0) {
							sub_one_target_zero++;
						} else if (inputArray.get(noofattributes - 1).get(Integer.parseInt(temp[i]) - 1) == 1) {
							sub_one_target_one++;
						}
					} else if (inputArray.get(j).get(Integer.parseInt(temp[i]) - 1) == 0) {
						subzero++;
						if (inputArray.get(noofattributes - 1).get(Integer.parseInt(temp[i]) - 1) == 0) {
							sub_zero_target_zero++;
						} else if (inputArray.get(noofattributes - 1).get(Integer.parseInt(temp[i]) - 1) == 1) {
							sub_zero_target_one++;
						}
					} else if (inputArray.get(j).get(Integer.parseInt(temp[i]) - 1) == 2) {
						subtwo++;
						if (inputArray.get(noofattributes - 1).get(Integer.parseInt(temp[i]) - 1) == 0) {
							sub_two_target_zero++;
						} else if (inputArray.get(noofattributes - 1).get(Integer.parseInt(temp[i]) - 1) == 1) {
							sub_two_target_one++;
						}
					}
				}
				double sub_entropy, first_half, second_half, third_half;

				if (subzero == 0 || sub_zero_target_one == 0 || sub_zero_target_zero == 0) {
					first_half = 0;
				} else {
					first_half = (double) ((double) subzero / (double) tempRow) * ((((double) sub_zero_target_zero / (double) subzero) * (Math.log10((subzero)/ (double) sub_zero_target_zero) / Math.log10(2))) + (((double) sub_zero_target_one / (double) subzero) * (Math.log10((subzero) / (double) sub_zero_target_one) / Math.log10(2))));
				}

				if (subone == 0 || sub_one_target_one == 0 || sub_one_target_zero == 0) {
					second_half = 0;
				} else {
					second_half = (double) ((double) subone / (double) tempRow) * ((((double) sub_one_target_zero / (double) subone) * (Math.log10((subone) / (double) sub_one_target_zero) / Math.log10(2))) + (((double) sub_one_target_one / (double) subone) * (Math.log10((subone) / (double) sub_one_target_one) / Math.log10(2))));
				}

				if (subtwo == 0 || sub_two_target_one == 0 || sub_two_target_zero == 0) {
					third_half = 0;
				} else {
					third_half = (double) ((double) subtwo / (double) tempRow) * ((((double) sub_two_target_zero / (double) subtwo) * (Math.log10((subtwo)/ (double) sub_two_target_zero) / Math.log10(2))) + (((double) sub_two_target_one / (double) subtwo) * (Math.log10((subtwo) / (double) sub_two_target_one) / Math.log10(2))));
				}

				sub_entropy = first_half + second_half + third_half;
				tempgainList.add(entropy - sub_entropy);
			}
			gainList.add(tempgainList);

			double maxGain = 0.0;
			for (int i = 0; i < tempgainList.size(); i++) {
				maxGain = Math.max(maxGain, tempgainList.get(i));
			}
			double f = ((float) tempRow / (float) row) * maxGain;
			fList.add(f);
		}
	}

	private static void partition() {
		double maxF = 0.0;
		int index = 0;
		for (int i = 0; i < fList.size(); i++) {
			if (fList.get(i) > maxF) {
				maxF = fList.get(i);
				index = i;
			}
		}

		double maxGain = 0.0;
		int maxAtrributeIndex = 0;
		for (int j = 0; j < gainList.get(index).size(); j++) {
			if (gainList.get(index).get(j) > maxGain) {
				maxGain = gainList.get(index).get(j);
				maxAtrributeIndex = j;
			}
		}
		String group = partitionList.values().toArray()[index].toString();
		String tempGroup[] = group.split(" ");

		Iterator temp = partitionList.entrySet().iterator();

		String str = "";

		String Final = "";
		int count = 0;


		while (temp.hasNext()) {
			Map.Entry pairs = (Map.Entry) temp.next();

			if (count == index) {
				str = pairs.getKey().toString();
			} else {
				Final += pairs.getKey().toString() + " " + pairs.getValue().toString() + "\n";
			}
			count++;
		}

		String zeroGroup = str + "0 ", oneGroup = str + "1 ", twoGroup = str + "2 ";
		for (int i = 0; i < tempGroup.length; i++) {
			if (inputArray.get(maxAtrributeIndex).get(Integer.parseInt(tempGroup[i]) - 1) == 0) {
				zeroGroup += tempGroup[i] + " ";
			} else if (inputArray.get(maxAtrributeIndex).get(Integer.parseInt(tempGroup[i]) - 1) == 1) {
				oneGroup += tempGroup[i] + " ";
			} else if (inputArray.get(maxAtrributeIndex).get(Integer.parseInt(tempGroup[i]) - 1) == 2) {
				twoGroup += tempGroup[i] + " ";
			}
		}

		String outputString = "Partition " + str + " was replaed with partitions ";
		
		if (!zeroGroup.equalsIgnoreCase(str + "0")) {
			Final += zeroGroup;
			outputString += str + "0 ";
		}
		if (!oneGroup.equalsIgnoreCase(str + "1")) {
			Final += "\n" + oneGroup;
			outputString += str + "1 ";
		}
		if (!twoGroup.equalsIgnoreCase(str + "2")) {
			Final += "\n" + twoGroup;
			outputString += str + "2 ";
		}

		zeroGroup += "\n";
		oneGroup += "\n";
		twoGroup += "\n";

		outputString += " using Feature " + (maxAtrributeIndex + 1);
		System.out.println(outputString);
		
		try {
			FileWriter fstream = new FileWriter(pathName + "\\" + filename3);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(Final);
			out.close();
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}

	}
}