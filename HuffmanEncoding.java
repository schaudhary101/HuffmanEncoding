import java.io.*;
import java.util.*;
import java.util.Map.Entry;

// Shaket Chaudhary and Juan Castano
// Fall 2016

public class HuffmanEncoding {
	
	public static Map<Character, Integer> frequencyTable(String fileName) {
		// Create a map that will keep track of character frequencies
		Map<Character, Integer> charFrequencyTable = new TreeMap<Character, Integer>();
		BufferedReader input = null; // we keep it null so we can run the try catch

		// Open the file, if possible
		try {
			input = new BufferedReader(new FileReader(fileName));
		} catch (FileNotFoundException e) {
			System.err.println("Cannot open file.\n" + e.getMessage());
		}

		// Read the file
		try {
			// go through every character in the file
			int r;
			while ((r = input.read()) != -1) {
				char c = (char) r;
				// increase count in frequency table if it already exists
				if (charFrequencyTable.containsKey(c)) {
					// Increment the count
					charFrequencyTable.put(c, charFrequencyTable.get(c) + 1);
				} 
				// Since the character does no exist we will put in into the map
				else {
					// Add the new word
					charFrequencyTable.put(c, 1);
				}
			}
		} catch (IOException e) {
			System.err.println("IO error while reading.\n" + e.getMessage());
		}

		// Close the file, if possible
		try {
			input.close();
		} catch (IOException e) {
			System.err.println("Cannot close file.\n" + e.getMessage());
		}
		return charFrequencyTable;
	}

	public static PriorityQueue<BinaryTree<DataHolder>> createPQ(Map<Character, Integer> charFrequencyTable) {
		// Create an Arraylist to add the map entries to
		ArrayList<BinaryTree<DataHolder>> listOfTrees = new ArrayList<BinaryTree<DataHolder>>();
		
		//Iterate through the map
		for (Entry<Character, Integer> entry : charFrequencyTable.entrySet()) {
			// Get the key and frequency from the map
			char key = entry.getKey();
			int frequency = entry.getValue();

			// Add the values into a node
			DataHolder node = new DataHolder(key, frequency);

			// add the node to a tree
			BinaryTree<DataHolder> tree = new BinaryTree<DataHolder>(node);

			// Add the tree to a priority queue
			listOfTrees.add(tree);
		}
		// Using an anonymous function to write the compare function that will place the most frequent numbers first
		class FrequencyComparator implements Comparator<BinaryTree<DataHolder>> {
			public int compare(BinaryTree<DataHolder> s1, BinaryTree<DataHolder> s2) {
				if (s1.getData().getFrequency() < s2.getData().getFrequency()) {
					return -1;
				}
				if (s1.getData().getFrequency() > s2.getData().getFrequency()) {
					return 1;
				} else {
					return 0;
				}
			}
		}
		// initialize the compare function
		Comparator<BinaryTree<DataHolder>> freqCompare = new FrequencyComparator();
		//initialize the Priority queue with the compare function
		PriorityQueue<BinaryTree<DataHolder>> pq = new PriorityQueue<BinaryTree<DataHolder>>(freqCompare);
		pq.addAll(listOfTrees);
		
		return pq;
	}
	
	public static BinaryTree<DataHolder> createTree(PriorityQueue<BinaryTree<DataHolder>> pq){
		// We need two elements in the pq for the function to run
		if (pq.size() == 1) {
			BinaryTree<DataHolder> tempFirst = pq.remove();
			DataHolder tempNode = new DataHolder(tempFirst.getData().getFrequency());
			BinaryTree<DataHolder> padre = new BinaryTree<DataHolder>(tempNode, tempFirst, tempFirst);
			pq.add(padre);
		}
		while (pq.size() >1){
			BinaryTree<DataHolder> first = pq.remove();
			BinaryTree<DataHolder> second = pq.remove();
			//Sum together the frequencies into a new node
			DataHolder node = new DataHolder(first.getData().getFrequency() + second.getData().getFrequency());
			// place the new node into the binary tree
			BinaryTree<DataHolder> parent = new BinaryTree<DataHolder>(node, first, second);
			pq.add(parent);
		}
		// when the size comes down to one it needs a special case to add to the priority queue
		
		return pq.remove();
	}
	
	public static void codeRetrievalHelper(Map<Character, String> code, BinaryTree<DataHolder> tree, String number){
		if (tree.isLeaf()){
			//put the character into the map with the number as the value
			code.put(tree.getData().getC(), number);
			//reset the number
			number = "";
		}
		if (tree.hasRight()) {
			//add a 1 to the number if it has a right and then recurse
			codeRetrievalHelper(code, tree.getRight(), number + "1");
		}
		if (tree.hasLeft()) {
			//add a 0 to the number if it has a left and then recurse
			codeRetrievalHelper(code, tree.getLeft(), number + "0");
		}
	}
	
	public static Map<Character, String> codeRetrieval(BinaryTree<DataHolder> tree){
		Map<Character, String> code = new TreeMap<Character, String>();
		//initialize an empty string we will concatenate into in the helper method
		String number = "";
		//call helper function to recurse
		codeRetrievalHelper(code, tree, number);
		return code;
		
	}
	
	public static void compress(Map<Character, String> code, String in, String out) throws IOException{
		BufferedReader input = null;
		BufferedBitWriter output = new BufferedBitWriter(out);
		// Open the file, if possible
		try {
			input = new BufferedReader(new FileReader(in));
		} catch (FileNotFoundException e) {
			System.err.println("Cannot open file.\n" + e.getMessage());
		}
		// Read the file
		try {
			//Go through the file character by character
			int r;
			while ((r = input.read()) != -1) {
				//make the character into a primitive type
				Character c = new Character((char)(r));
				String temp = code.get(c);
				//iterate through the string
				for (int i = 0; i < temp.length(); i++ ){
					if(temp.charAt(i) == '0'){
						output.writeBit(false);
					}
					if(temp.charAt(i) == '1'){
						output.writeBit(true);
					}
				}
			} 
			
			output.close();
		} catch (IOException e) {
			System.err.println("IO error while reading.\n" + e.getMessage());
		}
		
		// Close the file, if possible
		try {
			input.close();
		} catch (IOException e) {
			System.err.println("Cannot close file.\n" + e.getMessage());
		}
	}
	public static void decompress(Map<Character, String> code,BinaryTree<DataHolder> tree, String in, String out ) throws IOException{
		// call binary tree, run through it, until you get to a leaf
		//we want the original tree and one to iterate through
		BufferedBitReader input = null;
		BufferedWriter output = new BufferedWriter(new FileWriter(out));
		//save start node
		BinaryTree<DataHolder> root = tree;
		
		// Open the file, if possible
		try {
			input = new BufferedBitReader(in);
		} catch (FileNotFoundException e) {
			System.err.println("Cannot open file.\n" + e.getMessage());
		}
		try {
			//Go through the file character by character
			while ((input).hasNext()) {
				boolean bit = input.readBit();
				
				if (bit == true){
					tree = tree.getRight();
				}
				if (bit == false){
					tree = tree.getLeft();
				}
				if (tree.isLeaf()){
					output.write(tree.getData().getC());
					//jump to the start of the tree
					tree = root;
				}
			} 
			output.close();
		
		} catch (IOException e) {
			System.err.println("IO error while reading.\n" + e.getMessage());
		}
		
		// Close the file, if possible
		try {
			input.close();
		} catch (IOException e) {
			System.err.println("Cannot close file.\n" + e.getMessage());
		}
	}
	
	public static void main(String[] args) throws Exception {
		//WarAndPeace
		Map<Character, Integer> charFrequencyTable = frequencyTable("inputs/WarAndPeace.txt");
		PriorityQueue<BinaryTree<DataHolder>> WarAndPeacePQ = createPQ(charFrequencyTable);
		BinaryTree<DataHolder> WarAndPeaceTree = createTree(WarAndPeacePQ);
		Map<Character, String> WarAndPeaceCode = codeRetrieval(WarAndPeaceTree);
		compress(WarAndPeaceCode, "inputs/WarAndPeace.txt", "output/WarAndPeaceCompressed.txt");
		decompress(WarAndPeaceCode, WarAndPeaceTree, "output/WarAndPeaceCompressed.txt", "output/WarAndPeaceDecompressed.txt");
		
		//USConsitution
		Map<Character, Integer> ConsiutitionFrequency = frequencyTable("inputs/WarAndPeace.txt");
		PriorityQueue<BinaryTree<DataHolder>> ConstitutionPQ = createPQ(ConsiutitionFrequency);
		BinaryTree<DataHolder> ConstitutionTree = createTree(ConstitutionPQ);
		Map<Character, String> ConstitutionCode = codeRetrieval(ConstitutionTree);
		compress(ConstitutionCode, "inputs/USConstitution.txt", "output/ConstitutionCompressed.txt");
		decompress(ConstitutionCode, ConstitutionTree, "output/ConstitutionCompressed.txt", "output/ConstitutionDecompressed.txt");
		
		//small file
		Map<Character, Integer> SmallFrequency = frequencyTable("inputs/WarAndPeace.txt");
		PriorityQueue<BinaryTree<DataHolder>> SmallPQ = createPQ(SmallFrequency);
		BinaryTree<DataHolder> SmallTree = createTree(SmallPQ);
		Map<Character, String> SmallCode = codeRetrieval(ConstitutionTree);
		compress(SmallCode, "inputs/smallTest.txt", "output/smallCompressed.txt");
		decompress(SmallCode, SmallTree, "output/smallCompressed.txt", "output/smallDecompressed.txt");
		
		//one character file
		Map<Character, Integer> OneFrequency = frequencyTable("inputs/One.txt");
		PriorityQueue<BinaryTree<DataHolder>> OnePQ = createPQ(OneFrequency);
		BinaryTree<DataHolder> OneTree = createTree(OnePQ);
		Map<Character, String> OneCode = codeRetrieval(OneTree);
		compress(OneCode, "inputs/One.txt", "output/OneCompressed.txt");
		decompress(OneCode, OneTree, "output/OneCompressed.txt", "output/OneDecompressed.txt");	
	}
}
