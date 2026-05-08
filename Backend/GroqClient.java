import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class GroqClient {

    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String API_KEY = "YOUR_GROQ_API_KEY";

    public String callGroq(String prompt) {
        int retries = 3;
        int delay = 2000;

        for (int i = 0; i < retries; i++) {
            try {
                URL url = new URL(API_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                String jsonInput = "{"
                        + "\"model\":\"llama3-70b-8192\","
                        + "\"messages\":[{\"role\":\"user\",\"content\":\"" + prompt + "\"}]"
                        + "}";

                try (OutputStream os = conn.getOutputStream()) {
                    os.write(jsonInput.getBytes());
                }

                BufferedReader br = new BufferedReader(
                        new InputStreamReader(conn.getInputStream())
                );

                StringBuilder response = new StringBuilder();
                String line;

                while ((line = br.readLine()) != null) {
                    response.append(line);
                }

                br.close();

                return parseResponse(response.toString());

            } catch (Exception e) {
                System.out.println("Retry " + (i + 1) + " failed: " + e.getMessage());

                try {
                    Thread.sleep(delay);
                    delay *= 2;
                } catch (InterruptedException ignored) {}
            }
        }

        return "Groq API failed after 3 retries";
    }

    private String parseResponse(String json) {
        int start = json.indexOf("\"content\":\"") + 11;
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }
}