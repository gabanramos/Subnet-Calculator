
public class Address {
	
	// --------------- VARIABLES
	
	// number of bits for subnet mask
	int slash;
	
	// class for the address
	int addressClass;
	
	// amount of possible subnets
	int subnetsPossible;
	
	// amount of bits for subnets
	int bitsForSubnets;
	
	// amount of bits for hosts
	int bitsForHosts;
	
	// amount of possible hosts
	int hostsPossible;
		
	// as it comes from user
	String rawInput;
	
	// holds the network address without the slash notation
	String address;
	String binaryAddress;
	
	// holds subnet in decimal and binary format
	String subnetMask;
	String binarySubnetMask;
	
	// subnet index
	String subnetIndex;
	
	// holds wildcard in decimal and binary format
	String wildcard;
	String binaryWildcard;
	
	// holds network address in decimal and binary
	String netAddress;
	String binaryNetAddress;
	
	// holds broadcast address in decimal and binary
	String broadcast;
	String binaryBroadcast;
	
	// first and last hosts in decimal and binary
	String firstHost;
	String lastHost;
	String binaryFirstHost;
	String binaryLastHost;
	
	// -------------------------
	
	// CONSTRUCTOR
	public Address(String inputParam) {		
		
		rawInput = inputParam;
		
		// will hold input separated into 5 pieces
		String [] splitAddress;
		
		// breaks the input into chunks representing 4 octets and the slash part 
		// this array should be of length 5
		splitAddress = inputParam.split("\\.| ");
		validateInput(splitAddress);
		
		
		// creates net address using 4 octets and adding dots
		this.address =	splitAddress[0] + "." +
				splitAddress[1] + "." +
				splitAddress[2] + "." +
				splitAddress[3];
		
		// creates net address in binary form
		binaryAddress = addressToBinary(address);
		
		// holds the slash notation value e.g. /17 will hold 17
		slash = Integer.parseInt(splitAddress[4]);
		
		// creates subnet mask and wildcard, in binary and in decimal
		createSubnetWildcard();
		
		// network address, in decimal and binary
		binaryNetAddress = binaryAND(binaryAddress, binarySubnetMask);
		netAddress = binaryToAddress(binaryNetAddress);
		
		// BROADCAST ADDRESS
		binaryBroadcast = binaryOR(binaryWildcard, binaryNetAddress);
		broadcast = binaryToAddress(binaryBroadcast);
		
		// FIRST HOST ADDRESS
		binaryFirstHost = flipLAST(binaryNetAddress);
		firstHost = binaryToAddress(binaryFirstHost);
		
		// LAST HOST ADDRESS
		binaryLastHost = flipLAST(binaryBroadcast);
		lastHost = binaryToAddress(binaryLastHost);
		
		// Class, from 1-5
		addressClass = this.getClass(binaryAddress);
		
		// How many bits are used for subnet
		bitsForSubnets = (slash - (addressClass * 8));
		if (bitsForSubnets < 0) {
			System.out.println("Error in assigning variable bitsForSubnets");
			System.exit(0);
		}
		
		// How many subnets are possible
		subnetsPossible = (int) Math.pow(2, bitsForSubnets);
		
		// Subnet Index, in binary
		subnetIndex = calculateSubnetIndex();
		
		// Bits reserved for hosts
		bitsForHosts = 32 - slash;
		
		// How many hosts are possible 
		// Subtract 2 because first is net address, last is broadcast
		hostsPossible = (int) Math.pow(2, bitsForHosts) - 2;

		
	}




	// WORKING METHODS
	
	// checks if input is in correct form
	// IF SO, creates an array of strings, each element one chunk of the address
	public void validateInput(String[] splitAddressParam) {
		// checks the length. Should be 5 (4 octets and slash notation)
		if (splitAddressParam.length != 5) {
			System.out.println("Error, input in wrong format. Not 5 chunks");
			System.exit(0);
		}

		// checks if there is a / in last octet, and strips it
		if (splitAddressParam[4].contains("/")) {
			splitAddressParam[4] = splitAddressParam[4].replace("/", "");
		} else {
			System.out.println("Error, input in wrong format. Last chunk without '/'");
			System.exit(0);			
		}

		// checks if resulting chunks are numbers, and within 0-255
		for (int i = 0; i < splitAddressParam.length; i++) {
		try{
		    int number = Integer.parseInt(splitAddressParam[i]);
		    if (number > 255) 
			throw new NumberFormatException("Number bigger than 255");
		    if(i == 4 && number <= 0) {
			throw new NumberFormatException("Slash smaller than 0");
			}
		    if(i == 4 && number > 32) {
			throw new NumberFormatException("Slash bigger than 32");
			}

		}
		catch (NumberFormatException ex){
		    System.out.println("Chunk #" + (i+1) + " is not a valid number. " + ex.getMessage());
		    System.exit(0);
		}



		}
				
				
	}

	// creates the output as presented on Instructions
	public StringBuilder printInfo() {
		
		StringBuilder output = new StringBuilder();
		
		// HEADER
		output.append("\n");
		output.append("Input: " + rawInput);
		output.append("\n");
		
		
		//OUTPUT HEADER
		output.append("\n");
		output.append("Output:");
		output.append("\n");
		
		
		//ADDRESS
		output.append(String.format("%20s %-20s %s\n", "Address:", address, formatBinary(binaryAddress)));
		
		// NETMASK
		output.append(String.format("%20s %-20s %s\n", "Netmask:", subnetMask + " = " + slash, formatBinary(binarySubnetMask)));

		// WILDCARD
		output.append(String.format("%20s %-20s %s\n", "Wildcard:", wildcard, formatBinary(binaryWildcard)));
		
		// SEPARATOR
		output.append("=>\n");
		
		// SUBNET
		output.append(String.format("%20s %-20s %s\n", "Subnet (Network):", netAddress + "/" + slash, formatBinary(binaryNetAddress) + " (Class " + letterOfClass(addressClass) + ")"));
		
		// BROADCAST
		output.append(String.format("%20s %-20s %s\n", "Broadcast:", broadcast, formatBinary(binaryBroadcast)));
		
		// FIRST HOST
		output.append(String.format("%20s %-20s %s\n", "HostMin (FHIP):", firstHost, formatBinary(binaryFirstHost)));

		// LAST HOST
		output.append(String.format("%20s %-20s %s\n", "HostMax (LHIP):", lastHost, formatBinary(binaryLastHost)));
		
		// bits for Subnets
		output.append("s=" + bitsForSubnets + "\n");
		
		// amount of subnets possible
		output.append("S=" + subnetsPossible + "\n");
		
		// Subnet Index
		output.append("Subnet Index (" + subnetIndex + ") = " + binaryToNumber(subnetIndex) + "\n");
		
		// bits for hosts
		output.append("h=" + bitsForHosts + "\n");

		// amount of hosts possible
		output.append("HIPs Hosts/Net: " + hostsPossible);


		
		return output;
	}

	





	private String addressToBinary(String address) {
		
		StringBuilder binaryForm = new StringBuilder();
		String[] splitAddress = address.split("\\.| ");
		int[] octets = new int[splitAddress.length];
		
		// parses the strings into ints
		for(int i = 0; i < splitAddress.length; i++) {
			octets[i] = Integer.parseInt(splitAddress[i]);	
		}
		
		// builds a string with the whole address in binary
		for(int i = 0; i < octets.length; i++) {
			binaryForm.append(intToBinary(octets[i]));
		}
				
		return binaryForm.toString();
	}
	
	
	private String binaryToAddress(String binaryAddress) {
		
		StringBuilder decimalAddress = new StringBuilder();
		
		
		String[] chunks = new String[4];
		String separator = "";
		
		chunks[0] = binaryAddress.substring(0,8);
		chunks[1] = binaryAddress.substring(8,16);
		chunks[2] = binaryAddress.substring(16,24);
		chunks[3] = binaryAddress.substring(24,32);
		
		for(int i=0; i < chunks.length; i++) {
			
			int decimalChunk = 0;	
			for(int j=0; j < chunks[i].length(); j++) {
				
				if (chunks[i].charAt(j) == '1') {
					decimalChunk += (int) Math.pow(2, 7 - j);
				}
				}
			decimalAddress.append(separator + decimalChunk);
			separator = ".";
		}
		
		return decimalAddress.toString();
	}
	
	
	private String formatBinary(String binaryAddress) {
		
		StringBuilder binaryFormatted = new StringBuilder();
		
		binaryFormatted.append(binaryAddress);
		
		// adds dot for formatting
		binaryFormatted.insert(8, ".");
		binaryFormatted.insert(17, ".");
		binaryFormatted.insert(26, ".");
		
		
		// adds space separating host and net
		if(slash >=1 && slash < 8) {
			binaryFormatted.insert(slash, " ");
		}
		
		if(slash >=9 && slash < 16) {
			binaryFormatted.insert(slash+1, " ");
		}
		
		if(slash >=17 && slash < 24) {
			binaryFormatted.insert(slash+2, " ");
		}
		
		if(slash >= 25 && slash < 32) {
			binaryFormatted.insert(slash+3, " ");
		}
		
		if(slash == 8) binaryFormatted.replace(8,9," ");
		if(slash == 16) binaryFormatted.replace(17,18," ");
		if(slash == 24) binaryFormatted.replace(26,27," ");
		
		return binaryFormatted.toString();
		
	}

	
	private StringBuilder intToBinary(int number) {
		
		StringBuilder octet = new StringBuilder();
		int[] exponentValues = {128, 64, 32, 16, 8, 4, 2, 1};
		
		for (int i = 0; i < exponentValues.length; i++) {
			if(number >= exponentValues[i]) {
				octet.append("1");
				number -= exponentValues[i];
			} else {
				octet.append("0");
			}
		}
		
		if (octet.length() != 8) {
			System.out.println("Error in converting from int to binary");
			System.exit(0);
		}
		return octet;
	}

	
	private void createSubnetWildcard() {
		
		String[] netmasks = {	"255.255.255.255",	// /32 0
					"255.255.255.254",	// /31 1
					"255.255.255.252",	// /30 2
					"255.255.255.248",	// /29 3
					"255.255.255.240",	// /28 4
					"255.255.255.224",	// /27 5
					"255.255.255.192",	// /26 6
					"255.255.255.128",	// /25 7
					"255.255.255.0",	// /24 8
					"255.255.254.0",	// /23 9
					"255.255.252.0",	// /22 10
					"255.255.248.0",	// /21 11
					"255.255.240.0",	// /20 12
					"255.255.224.0",	// /19 13
					"255.255.192.0",	// /18 14
					"255.255.128.0",	// /17 15
					"255.255.0.0",		// /16 16
					"255.254.0.0",		// /15 17
					"255.252.0.0",		// /14 18
					"255.248.0.0",		// /13 19
					"255.240.0.0",		// /12 20
					"255.224.0.0",		// /11 21
					"255.192.0.0",		// /10 22
					"255.128.0.0",		// /9  23
					"255.0.0.0",		// /8  24
					"254.0.0.0",		// /7  25
					"252.0.0.0",		// /6  26
					"248.0.0.0",		// /5  27
					"240.0.0.0",		// /4  28
					"224.0.0.0",		// /3  29
					"192.0.0.0",		// /2  30
					"128.0.0.0",		// /1  31
					"0.0.0.0"};		// /0  32

		String[] wildcards = {	"0.0.0.0",
					"0.0.0.1",
					"0.0.0.3",
					"0.0.0.7",
					"0.0.0.15",
					"0.0.0.31",
					"0.0.0.63",
					"0.0.0.127",
					"0.0.0.255",
					"0.0.1.255",
					"0.0.3.255",
					"0.0.7.255",
					"0.0.15.255",
					"0.0.31.255",
					"0.0.63.255",
					"0.0.127.255",
					"0.0.255.255",
					"0.1.255.255",
					"0.3.255.255",
					"0.7.255.255",
					"0.15.255.255",
					"0.31.255.255",
					"0.63.255.255",
					"0.127.255.255",
					"0.255.255.255",
					"1.255.255.255",
					"3.255.255.255",
					"7.255.255.255",
					"15.255.255.255",
					"31.255.255.255",
					"63.255.255.255",
					"127.255.255.255",
					"255.255.255.255"};
		
		subnetMask 	= netmasks[32 - slash];
		wildcard	= wildcards[32 - slash];
		
		binarySubnetMask = addressToBinary(subnetMask);
		binaryWildcard = addressToBinary(wildcard);

		
	}
	
	
	private String binaryAND(String address1, String address2) {
		
		StringBuilder binaryResult= new StringBuilder();
		int length = address1.length();
		if(length != address2.length()) {
			System.out.println("Error. Addresses not same length");
		}
		
		
		for(int i = 0; i < length; i++) {
			if(address1.charAt(i) == 49 && address2.charAt(i) == 49) {
				binaryResult.append("1");
			} else {
				binaryResult.append("0");
			}
		}
		
		return binaryResult.toString();
	}
	
	
	private String binaryOR(String address1, String address2) {
		
		StringBuilder binaryResult= new StringBuilder();
		int length = address1.length();
		if(length != address2.length()) {
			System.out.println("Error. Addresses not same length");
		}
		
		
		for(int i = 0; i < length; i++) {
			if(address1.charAt(i) == 48 && address2.charAt(i) == 48) {
				binaryResult.append("0");
			} else {
				binaryResult.append("1");
			}
		}
		
		return binaryResult.toString();
	}

	
	private String flipLAST(String binaryAddress) {
		StringBuilder binaryFlipped = new StringBuilder();
		binaryFlipped.append(binaryAddress);
		
		if(binaryFlipped.charAt(binaryFlipped.length() - 1) == '0') {
			binaryFlipped.deleteCharAt(binaryFlipped.length() - 1);
			binaryFlipped.append("1");
		} else {
				binaryFlipped.deleteCharAt(binaryFlipped.length() - 1);
				binaryFlipped.append("0");
			}
		
		
		
		return binaryFlipped.toString();
	}

	
	private int getClass(String binaryAddress) {
		
		int addressClass = 0;

		for (int i = 0; i <= 4; i++) {
			addressClass++;
			
			if (binaryAddress.charAt(i) == '0') {
				break;
			}
		}
		
		return addressClass;
	}

	
	private String letterOfClass(int classAddress) {
		String letter = "";
		
		switch(classAddress) {
		
		case 1:
			letter = "A";
			break;
			
		case 2:
			letter = "B";
			break;
			
		case 3:
			letter = "C";
			break;
			
		case 4:
			letter = "D";
			break;
			
		case 5:
			letter = "E";
			break;
		
		default:
			System.out.println("Error in assigning class letter");
			System.exit(0);
			break;
		}
		
		return letter;
	}
	
	private String calculateSubnetIndex() {
		
		String subnetIndex = "";
		
		switch(addressClass) {
		
		case 1:
		case 2:
		case 3:
			subnetIndex = binaryNetAddress.substring((8 * addressClass), slash);
			
			break;
						
		
		default:
			System.out.println("Error in creating subnet index");
			System.exit(0);
			break;
		}
		
		
		
		return subnetIndex;
	}
	
	private int binaryToNumber(String subnetIndexParam) {
		
		int convertedNumber = 0;
		StringBuilder binaryAddress = new StringBuilder(subnetIndexParam);
		int multiplier = 1;
		
		while (binaryAddress.length() > 0) {
			
			if(binaryAddress.charAt(binaryAddress.length() - 1) == '1') {
				convertedNumber += multiplier;
			}
			
			multiplier *= 2;
			
			
			
			binaryAddress.deleteCharAt(binaryAddress.length() - 1);
		}
		
		
		return convertedNumber;
	}
	
}


