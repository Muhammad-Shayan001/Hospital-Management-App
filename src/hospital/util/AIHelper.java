package hospital.util;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class AIHelper {
    private static final String GROQ_API_KEY = loadGroqApiKey();
    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";
    private static final String MODEL = "llama-3.3-70b-versatile";

    private static String loadGroqApiKey() {
        try {
            Properties props = new Properties();
            if (Files.exists(Paths.get("config.properties"))) {
                try (FileInputStream fis = new FileInputStream("config.properties")) {
                    props.load(fis);
                }
            }
            
            String apiKey = System.getenv("GROQ_API_KEY");
            if (apiKey == null || apiKey.isBlank()) {
                apiKey = props.getProperty("GROQ_API_KEY", "").trim();
            }
            
            if (apiKey.isEmpty() || apiKey.equals("your_groq_api_key_here")) {
                System.err.println("❌ ERROR: GROQ_API_KEY not found in config.properties or environment variables");
                return "";
            }
            return apiKey;
        } catch (Exception e) {
            System.err.println("❌ ERROR loading config: " + e.getMessage());
            return "";
        }
    }

    public static void startChat(Scanner scanner) {
        if (GROQ_API_KEY.isEmpty()) {
            System.out.println("❌ Cannot start AI Chat: API Key is missing!");
            return;
        }

        System.out.println("\n" + "=".repeat(50));
        System.out.println("         🤖 AI Medical Health Assistant");
        System.out.println("=".repeat(50));
        System.out.println("I'm powered by Groq AI - your health companion");
        System.out.println("Ask me about symptoms, medications, health tips, etc.");
        System.out.println("Type 'exit' to leave\n");

        HttpClient client = HttpClient.newHttpClient();
        List<Message> conversationHistory = new ArrayList<>();

        while (true) {
            System.out.print("📝 You: ");
            String userInput = scanner.nextLine().trim();

            if (userInput.equalsIgnoreCase("exit") || userInput.equalsIgnoreCase("quit")) {
                System.out.println("\n🏥 AI: Stay healthy! Goodbye! 👋\n");
                break;
            }

            if (userInput.isEmpty()) {
                continue;
            }

            conversationHistory.add(new Message("user", userInput));
            System.out.println("🤔 AI is thinking...");

            String aiResponse = getGroqResponse(client, conversationHistory);

            if (aiResponse != null && !aiResponse.isEmpty()) {
                System.out.println("💬 AI: " + aiResponse + "\n");
                conversationHistory.add(new Message("assistant", aiResponse));
            } else {
                System.out.println("❌ AI: Sorry, I couldn't get a response. Please try again.\n");
                conversationHistory.remove(conversationHistory.size() - 1);
            }
        }
    }

    private static String getGroqResponse(HttpClient client, List<Message> messages) {
        try {
            String requestBody = buildRequestBody(messages);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(GROQ_API_URL))
                    .header("Authorization", "Bearer " + GROQ_API_KEY)
                    .header("Content-Type", "application/json")
                    .timeout(java.time.Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return extractResponseText(response.body());
            } else {
                System.err.println("❌ API Error: " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            return null;
        }
    }

    private static String buildRequestBody(List<Message> messages) {
        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"model\": \"").append(MODEL).append("\",\n");
        json.append("  \"messages\": [\n");
        json.append("    {\n");
        json.append("      \"role\": \"system\",\n");
        json.append("      \"content\": \"You are a helpful health and medical assistant. Provide accurate, safe health guidance. Always encourage consulting a doctor for serious symptoms.\"\n");
        json.append("    }");

        for (Message msg : messages) {
            json.append(",\n");
            json.append("    {\n");
            json.append("      \"role\": \"").append(msg.role).append("\",\n");
            json.append("      \"content\": \"").append(escapeJson(msg.content)).append("\"\n");
            json.append("    }");
        }

        json.append("\n  ],\n");
        json.append("  \"temperature\": 0.7\n");
        json.append("}\n");

        return json.toString();
    }

    private static String extractResponseText(String jsonResponse) {
        try {
            int startIndex = jsonResponse.indexOf("\"content\":");
            if (startIndex == -1) return null;
            
            startIndex = jsonResponse.indexOf("\"", startIndex + 10);
            if (startIndex == -1) return null;
            
            startIndex++;
            int endIndex = jsonResponse.indexOf("\"", startIndex);
            if (endIndex == -1) return null;

            String content = jsonResponse.substring(startIndex, endIndex);
            return unescapeJson(content);
        } catch (Exception e) {
            return null;
        }
    }

    private static String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }

    private static String unescapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\n", "\n")
                   .replace("\\r", "\r")
                   .replace("\\t", "\\t")
                   .replace("\\\"", "\"")
                   .replace("\\\\", "\\");
    }

    private static class Message {
        String role;
        String content;

        Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}
