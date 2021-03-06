import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class LongestPrefixMatcher {
    private class Pair {
        private String s;
        private int i;

        public String getS() {
            return s;
        }

        public int getI() {
            return i;
        }

        Pair(String s, int i) {
            this.s = s;
            this.i = i;
        }
    }

    // TODO: Request access token from your student assistant
	public static final String ACCESS_TOKEN = "s1743171_iu5my";
	
	public static final String ROUTES_FILE  = "routes.txt";
	public static final String LOOKUP_FILE  = "lookup.txt";

    private final ArrayList<Pair> lolaList;

	/**
	 * Main entry point
	 */
	public static void main(String[] args) {
		System.out.println(ACCESS_TOKEN);
		new LongestPrefixMatcher();
	}

	/**
	 * Constructs a new LongestPrefixMatcher and starts routing
	 */
	public LongestPrefixMatcher() {
		this.lolaList = new ArrayList<>();
        this.readRoutes();
        this.sortLola();
		this.readLookup();
	}

    private void sortLola() {
        Collections.sort(lolaList, new Comparator<Pair>() {
            @Override
            public int compare(Pair o1, Pair o2) {
                return Integer.compare(o1.getS().length(), o2.getS().length()) * -1;
            }
        });
    }

	/**
	 * Adds a route to the routing tables
	 * @param ip The IP the block starts at in integer representation
	 * @param prefixLength The number of bits indicating the network part
	 *                     of the address range (notation ip/prefixLength)
	 * @param portNumber The port number the IP block should route to
	 */
	private void addRoute(int ip, byte prefixLength, int portNumber) { 
        String f = String.format("%32s", Integer.toBinaryString(ip)).replace(' ', '0').substring(0, prefixLength);
        lolaList.add(new Pair(f, portNumber));
    }

	/**
	 * Looks up an IP address in the routing tables
	 * @param ip The IP address to be looked up in integer representation
	 * @return The port number this IP maps to
	 */
	private int lookup(int ip) {
		String sIP = String.format("%32s", Integer.toBinaryString(ip)).replace(" ", "0");
        for (Pair pair : lolaList) {
            String s = pair.getS();
            if (s.equals(sIP.substring(0, s.length()))) {
                return pair.getI();
            }
        }
        return -1;
	}

	/**
	 * Converts an integer representation IP to the human readable form
	 * @param ip The IP address to convert
	 * @return The String representation for the IP (as xxx.xxx.xxx.xxx)
	 */
	private String ipToHuman(int ip) {
		return Integer.toString(ip >> 24 & 0xff) + "." +
			   Integer.toString(ip >> 16 & 0xff) + "." +
			   Integer.toString(ip >> 8 & 0xff) + "." +
			   Integer.toString(ip & 0xff);
	}

	/**
	 * Reads routes from routes.txt and parses each
	 */
	private void readRoutes() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(ROUTES_FILE));
			String line;
			while ((line = br.readLine()) != null) {
				this.parseRoute(line);
			}
		} catch (IOException e) {
			System.err.println("Could not open " + ROUTES_FILE);
		} finally {
			if (br != null) {
				try { br.close(); }
				catch (IOException e) { }
			}
		}
	}

	/**
	 * Parses a route and passes it to this.addRoute
	 */
	private void parseRoute(String line) {
		String[] split = line.split("\t");
		int portNumber = Integer.parseInt(split[1]);

		split = split[0].split("/");
		byte prefixLength = Byte.parseByte(split[1]);

		int ip = this.parseIP(split[0]);

		addRoute(ip, prefixLength, portNumber);
	}
	
	/**
	 * Reads IPs to look up from lookup.bin and passes them to this.lookup
	 */
	private void readLookup() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(LOOKUP_FILE));
			int count = 0;
			StringBuilder sb = new StringBuilder(1024 * 4);
			// writing each lookup result to disk separately is very slow;
			// therefore, we collect up to 1024 results into a string and
			// write that all at once.

			String line;
			while ((line = br.readLine()) != null) {
				sb.append(Integer.toString(this.lookup(this.parseIP(line))) + "\n");
				count++;

				if (count >= 1024) {
					System.out.print(sb);
					sb.delete(0, sb.capacity());
					count = 0;
				}
			}

			System.out.print(sb);
		} catch (IOException e) {
			System.err.println("Could not open " + LOOKUP_FILE);
		} finally {
			if (br != null) {
				try { br.close(); }
				catch (IOException e) { }
			}
		}
	}

	private int parseIP(String ipString) {
		String[] ipParts = ipString.split("\\.");
		
		int ip = 0;
		for (int i = 0; i < 4; i++) {
			ip |= Integer.parseInt(ipParts[i]) << (24 - (8 * i));
		}

		return ip;
	}
}
