import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class JsonMd5Hasher {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java -jar JsonMd5Hasher.jar <PRN Number> <path to JSON file>");
            System.exit(1);
        }

        String prnNumber = args[0].toLowerCase();
        String filePath = args[1];

        String destinationValue = getDestinationValue(filePath);
        if (destinationValue == null) {
            System.err.println("Key 'destination' not found in the JSON file.");
            System.exit(1);
        }

        String randomString = generateRandomString(8);
        String concatenatedString = prnNumber + destinationValue + randomString;
        String md5Hash = generateMd5Hash(concatenatedString);

        System.out.println(md5Hash + ";" + randomString);
    }

    private static String getDestinationValue(String filePath) {
        try (InputStream inputStream = new FileInputStream(filePath)) {
            JSONTokener tokener = new JSONTokener(inputStream);
            JSONObject jsonObject = new JSONObject(tokener);
            return findDestinationValue(jsonObject);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String findDestinationValue(JSONObject jsonObject) {
        for (String key : jsonObject.keySet()) {
            if (key.equalsIgnoreCase("destination")) {
                return jsonObject.getString(key);
            } else {
                Object value = jsonObject.get(key);
                if (value instanceof JSONObject) {
                    String result = findDestinationValue((JSONObject) value);
                    if (result != null) {
                        return result;
                    }
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }

    private static String generateMd5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
