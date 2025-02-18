# Flow Log Tagger

## Overview
This Java program processes a flow log file and assigns tags to each log entry based on a lookup table provided in a CSV file. It then generates a summary of tag counts and port/protocol usage.

## Assumptions
- The program supports only **default log format** (version 2) as per AWS flow logs.
- The **lookup table** contains mappings of `dstport` and `protocol` to a `tag`.
- Flow logs and the lookup table are **plain text ASCII files**.
- **Case-insensitive matching** is applied when looking up port/protocol combinations.
- If no matching tag is found, the log entry is categorized as `Untagged`.

## Input Files
1. **Flow Log File**: Contains network traffic logs with details such as source/destination IP, ports, and protocols.
2. **Lookup Table File**: CSV file with `dstport`, `protocol`, and `tag` columns.

### Example Lookup Table
```
dstport,protocol,tag
25,tcp,sv_P1
68,udp,sv_P2
23,tcp,sv_P1
31,udp,SV_P3
443,tcp,sv_P2
22,tcp,sv_P4
3389,tcp,sv_P5
0,icmp,sv_P5
110,tcp,email
993,tcp,email
143,tcp,email
```

## Output
The program generates:
1. **Tag Count Summary** - Number of times each tag appears in the log.
2. **Port/Protocol Combination Count** - How often each port/protocol pair occurs.

### Sample Output
```
Tag Counts:
Tag,Count
sv_P2,1
sv_P1,2
sv_P4,1
email,3
Untagged,9

Port/Protocol Combination Counts:
Port,Protocol,Count
22,tcp,1
23,tcp,1
25,tcp,1
110,tcp,1
143,tcp,1
443,tcp,1
993,tcp,1
1024,tcp,1
49158,tcp,1
80,tcp,1
```

## Installation & Usage
### Prerequisites
- Java 8 or later

### Compiling and Running the Program
1. Clone the repository:
   ```sh
   git clone <repo-link>
   cd <repo-folder>
   ```
2. Compile the Java program:
   ```sh
   javac FlowLogParser.java
   ```
3. Run the program with input files:
   ```sh
   java FlowLogParser <flow_log_file> <lookup_table_file>
   ```
4. The output will be saved as `output.txt`.

## Testing
The program has been tested with:
- Various lookup table sizes (small and large files)
- Logs with mixed case protocols (e.g., `TCP` vs `tcp`)
- Edge cases such as missing fields and untagged entries

## Additional Notes
- The program **does not support custom log formats**.
- The only supported **log version is 2**.

## References
- AWS Flow Logs Documentation: [Link](https://docs.aws.amazon.com/vpc/latest/userguide/flow-log-records.html)

