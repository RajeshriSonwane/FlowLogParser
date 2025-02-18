import java.io.*;
import java.util.*;

public class FlowLogParser {

    /**
     * Loads a lookup table from a CSV file.
     * Each entry in the table maps a "port,protocol" pair to a corresponding tag.
     */
    private static Map<String, String> loadLookupTable(String filename) {
        Map<String, String> lookupTable = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            br.readLine(); // Skip the header line
            
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 3){
                    continue; // Skip invalid lines
                }
                
                String key = (parts[0].trim() + "," + parts[1].trim()).toLowerCase();
                lookupTable.put(key, parts[2].trim());
            }
        } catch (IOException e) {
            System.err.println("Error reading lookup table: " + e.getMessage());
        }
        return lookupTable;
    }

    /**
     * Parses a flow log file and counts occurrences of tags and port/protocol combinations.
     */
    private static void parseFlowLogs(
        String filename,
        Map<String, String> lookupTable,
        Map<String, Integer> tagCounts,
        Map<String, Integer> portProtocolCounts
    ) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length < 13){
                    continue; // Skip invalid lines
                }
                
                String dstPort = parts[5];
                String protocol = getProtocol(parts[7]);
                String key = (dstPort + "," + protocol).toLowerCase();
                
                String tag = lookupTable.getOrDefault(key, "Untagged");
                tagCounts.put(tag, tagCounts.getOrDefault(tag, 0) + 1);
                portProtocolCounts.put(key, portProtocolCounts.getOrDefault(key, 0) + 1);
            }
        } catch (IOException e) {
            System.err.println("Error reading flow logs: " + e.getMessage());
        }
    }

    /**
     * Converts a protocol number to its corresponding string representation.
     */
    private static String getProtocol(String protocolNumber) {
        switch (protocolNumber) {
            case "6":
                return "tcp";
            case "17": 
                return "udp";
            default: 
                return protocolNumber.toLowerCase();
        }
    }

    /**
     * Writes tag and port/protocol count results to an output file.
     */
    private static void writeOutput(
        String filename,
        Map<String, Integer> tagCounts,
        Map<String, Integer> portProtocolCounts
    ) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write("Tag Counts:\nTag,Count\n");
            tagCounts.forEach((tag, count) -> writeLine(bw, tag + "," + count));
            
            bw.write("\nPort/Protocol Combination Counts:\nPort,Protocol,Count\n");
            portProtocolCounts.forEach((key, count) -> writeLine(bw, key + "," + count));
        } catch (IOException e) {
            System.err.println("Error writing output file: " + e.getMessage());
        }
    }

    /**
     * Helper method to write a line to a BufferedWriter.
     */
    private static void writeLine(BufferedWriter bw, String line) {
        try {
            bw.write(line + "\n");
        } catch (IOException e) {
            System.err.println("Error writing line: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        final String LOOKUP_FILE = "./lookup.csv";
        final String FLOW_LOG_FILE = "./flowlog.txt";
        final String OUTPUT_FILE = "./output.txt";

        Map<String, String> lookupTable = loadLookupTable(LOOKUP_FILE);
        Map<String, Integer> tagCounts = new HashMap<>();
        Map<String, Integer> portProtocolCounts = new HashMap<>();
        
        parseFlowLogs(FLOW_LOG_FILE, lookupTable, tagCounts, portProtocolCounts);
        writeOutput(OUTPUT_FILE, tagCounts, portProtocolCounts);
    }
}
