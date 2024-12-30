import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.math.BigInteger;
import java.util.*;
import org.json.JSONObject;

public class ShamirSecretSharing {
    public static void main(String[] args) {
        try {
            // Load the JSON files for test cases
            JSONObject testcase1 = loadJSONFromFile("testcase1.json");
            JSONObject testcase2 = loadJSONFromFile("testcase2.json");

            // Process each test case and print results
            System.out.println("Test Case 1 Result: " + findConstantTerm(testcase1));
            System.out.println("Test Case 2 Result: " + findConstantTerm(testcase2));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads and parses a JSON file into a JSONObject.
     */
    public static JSONObject loadJSONFromFile(String filePath) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(filePath)));
        return new JSONObject(content);
    }

    /**
     * Finds the constant term 'c' of the polynomial using Lagrange interpolation.
     */
    public static BigInteger findConstantTerm(JSONObject testCase) {
        // Extract n and k
        JSONObject keys = testCase.getJSONObject("keys");
        int n = keys.getInt("n");
        int k = keys.getInt("k");

        // Ensure the input is valid
        if (n < k) {
            throw new IllegalArgumentException("Number of roots (n) must be greater than or equal to k.");
        }

        // Extract and decode points
        List<BigInteger> xValues = new ArrayList<>();
        List<BigInteger> yValues = new ArrayList<>();

        for (String key : testCase.keySet()) {
            if (key.equals("keys")) continue;

            JSONObject point = testCase.getJSONObject(key);
            int x = Integer.parseInt(key); // x is the key of the object
            int base = point.getInt("base"); // base for decoding y
            String encodedValue = point.getString("value"); // y value in encoded format
            BigInteger y = new BigInteger(encodedValue, base); // Decode y to a BigInteger

            xValues.add(BigInteger.valueOf(x));
            yValues.add(y);
        }

        // Use Lagrange interpolation to calculate the constant term
        return lagrangeInterpolation(xValues, yValues, k);
    }

    /**
     * Performs Lagrange interpolation to find the constant term.
     */
    public static BigInteger lagrangeInterpolation(List<BigInteger> x, List<BigInteger> y, int k) {
        BigInteger result = BigInteger.ZERO;

        for (int i = 0; i < k; i++) {
            BigInteger term = y.get(i); // y_i
            BigInteger denominator = BigInteger.ONE; // Product of (x_i - x_j)
            BigInteger numerator = BigInteger.ONE; // Product of -x_j

            for (int j = 0; j < k; j++) {
                if (i != j) {
                    numerator = numerator.multiply(x.get(j).negate()); // Multiply by -x_j
                    denominator = denominator.multiply(x.get(i).subtract(x.get(j))); // Multiply by (x_i - x_j)
                }
            }

            // Calculate term: y_i * (numerator / denominator)
            term = term.multiply(numerator).divide(denominator);
            result = result.add(term); // Add to result
        }

        return result;
    }
}
