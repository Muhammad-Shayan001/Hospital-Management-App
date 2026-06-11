package hospital.util;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;

public class AIHelper {
    private static final String PROVIDER = loadProvider();
    private static final String API_KEY = loadApiKey(PROVIDER);
    private static final String MODEL_NAME = loadModelName(PROVIDER);
    private static final String API_URL = buildApiUrl(PROVIDER, MODEL_NAME);

    private static String loadProvider() {
        try {
            Properties props = new Properties();
            Path configPath = Paths.get(System.getProperty("user.dir"), "config.properties");
            if (Files.exists(configPath)) {
                try (FileInputStream input = new FileInputStream(configPath.toFile())) {
                    props.load(input);
                }
            }

            String provider = System.getenv("AI_PROVIDER");
            if (provider == null || provider.isBlank()) {
                provider = props.getProperty("AI_PROVIDER", "");
            }

            provider = provider.trim().toLowerCase();
            if (provider.equals("groq") || provider.equals("gemini")) {
                return provider;
            }

            String apiKey = readConfiguredKey(props);
            if (apiKey.startsWith("gsk_")) {
                return "groq";
            }
            return "gemini";
        } catch (Exception e) {
            return "gemini";
        }
    }

    private static String loadApiKey(String provider) {
        try {
            Properties props = new Properties();
            Path configPath = Paths.get(System.getProperty("user.dir"), "config.properties");
            if (Files.exists(configPath)) {
                try (FileInputStream input = new FileInputStream(configPath.toFile())) {
                    props.load(input);
                }
            }

            String apiKey = provider.equals("groq") ? System.getenv("GROQ_API_KEY") : System.getenv("GEMINI_API_KEY");
            if (apiKey == null || apiKey.isBlank()) {
                apiKey = provider.equals("groq")
                        ? props.getProperty("GROQ_API_KEY", props.getProperty("GEMINI_API_KEY", ""))
                        : props.getProperty("GEMINI_API_KEY", props.getProperty("GROQ_API_KEY", ""));
            }

            apiKey = apiKey.trim();
            if (apiKey.isEmpty() || apiKey.equalsIgnoreCase("your_new_api_key_here") || apiKey.equalsIgnoreCase("your_groq_api_key_here")) {
                return "";
            }
            return apiKey;
        } catch (Exception e) {
            System.err.println("Error: config.properties not found or missing AI API key");
            return "";
        }
    }

    private static String loadModelName(String provider) {
        try {
            Properties props = new Properties();
            Path configPath = Paths.get(System.getProperty("user.dir"), "config.properties");
            if (Files.exists(configPath)) {
                try (FileInputStream input = new FileInputStream(configPath.toFile())) {
                    props.load(input);
                }
            }

            String defaultModel = provider.equals("groq") ? "llama-3.3-70b-versatile" : "gemini-1.5-flash";
            String modelName = provider.equals("groq")
                    ? props.getProperty("GROQ_MODEL", props.getProperty("GEMINI_MODEL", defaultModel))
                    : props.getProperty("GEMINI_MODEL", props.getProperty("GROQ_MODEL", defaultModel));
            modelName = modelName.trim();
            return modelName.isEmpty() ? defaultModel : modelName;
        } catch (Exception e) {
            return provider.equals("groq") ? "llama-3.3-70b-versatile" : "gemini-1.5-flash";
        }
    }

    private static String buildApiUrl(String provider, String modelName) {
        if ("groq".equals(provider)) {
            return "https://api.groq.com/openai/v1/chat/completions";
        }
        return "https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent";
    }

    private static String readConfiguredKey(Properties props) {
        String apiKey = System.getenv("GROQ_API_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            apiKey = System.getenv("GEMINI_API_KEY");
        }
        if (apiKey == null || apiKey.isBlank()) {
            apiKey = props.getProperty("GROQ_API_KEY", props.getProperty("GEMINI_API_KEY", ""));
        }
        return apiKey == null ? "" : apiKey.trim();
    }

    private static class Message {
        String role;
        String text;
        Message(String role, String text) {
            this.role = role;
            this.text = text;
        }
    }

    public static void startChat(Scanner scanner) {
        System.out.println("\n==================================================");
        System.out.println("         🤖 AI Medical Assistant          ");
        System.out.println("==================================================");
        System.out.println("Hello! I am your AI Medical Assistant powered by " + ("groq".equals(PROVIDER) ? "Groq" : "Gemini") + ".");
        System.out.println("I can provide basic health guidance and symptom checking.");
        System.out.println("Type 'exit' or 'quit' to leave the chat.");
        System.out.println("Note: This is for guidance only. Please consult a doctor for serious issues.\n");
        
        HttpClient client = HttpClient.newHttpClient();
        List<Message> history = new ArrayList<>();
        
        while(true) {
            System.out.print("You: ");
            String input = scanner.nextLine().trim();
            
            if(input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                System.out.println("AI: Goodbye! Stay healthy and take care.");
                break;
            }
            
            if(input.isEmpty()) continue;
            
            history.add(new Message("user", input));
            
            System.out.println("\nAI is typing...");
            String responseText = getGeminiResponse(client, history);
            
            if (responseText != null && !responseText.isEmpty()) {
                System.out.println("AI: " + responseText + "\n");
                history.add(new Message("model", responseText));
            } else {
                System.out.println("AI: Sorry, I am having trouble connecting to the server right now. Please try again later.\n");
                history.remove(history.size() - 1); // remove the failed user prompt
            }
        }
    }

    private static String getGeminiResponse(HttpClient client, List<Message> history) {
        if (API_KEY == null || API_KEY.isBlank()) {
            System.err.println("AI API key is missing. Add GROQ_API_KEY or GEMINI_API_KEY to config.properties, or set the matching environment variable.");
            return null;
        }

        try {
            String requestBody = "groq".equals(PROVIDER)
                    ? buildGroqRequestBody(history)
                    : buildGeminiRequestBody(history);
                    
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("groq".equals(PROVIDER) ? "Authorization" : "x-goog-api-key", groqHeaderValue())
                    .timeout(java.time.Duration.ofSeconds(30))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();
                    
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return "groq".equals(PROVIDER) ? extractGroqTextFromJson(response.body()) : extractTextFromJson(response.body());
            } else {
                System.err.println("API Error (" + response.statusCode() + "): " + response.body());
                return null;
            }
        } catch (Exception e) {
            System.err.println("Exception during API call: " + e.getMessage());
            return null;
        }
    }

    private static String groqHeaderValue() {
        return "groq".equals(PROVIDER) ? "Bearer " + API_KEY : API_KEY;
    }

    private static String buildGeminiRequestBody(List<Message> history) {
        StringBuilder requestBodyBuilder = new StringBuilder();
        requestBodyBuilder.append("{\n");
        requestBodyBuilder.append("  \"contents\": [\n");

        for (int i = 0; i < history.size(); i++) {
            Message msg = history.get(i);
            requestBodyBuilder.append("    {\n");
            requestBodyBuilder.append("      \"role\": \"").append(msg.role).append("\",\n");
            requestBodyBuilder.append("      \"parts\": [{\"text\": \"").append(escapeJson(msg.text)).append("\"}]\n");
            requestBodyBuilder.append("    }");
            if (i < history.size() - 1) {
                requestBodyBuilder.append(",");
            }
            requestBodyBuilder.append("\n");
        }
        requestBodyBuilder.append("  ]\n");
        requestBodyBuilder.append("}\n");
        return requestBodyBuilder.toString();
    }

    private static String buildGroqRequestBody(List<Message> history) {
        StringBuilder requestBodyBuilder = new StringBuilder();
        requestBodyBuilder.append("{\n");
        requestBodyBuilder.append("  \"model\": \"").append(escapeJson(MODEL_NAME)).append("\",\n");
        requestBodyBuilder.append("  \"messages\": [\n");
        requestBodyBuilder.append("    {\"role\": \"system\", \"content\": \"You are a helpful medical assistant. Give concise, safe, general guidance and encourage professional care for serious symptoms.\"},\n");

        for (int i = 0; i < history.size(); i++) {
            Message msg = history.get(i);
            String role = msg.role.equalsIgnoreCase("model") ? "assistant" : msg.role;
            requestBodyBuilder.append("    {\"role\": \"").append(role).append("\", \"content\": \"").append(escapeJson(msg.text)).append("\"}");
            if (i < history.size() - 1) {
                requestBodyBuilder.append(",");
            }
            requestBodyBuilder.append("\n");
        }

        requestBodyBuilder.append("  ],\n");
        requestBodyBuilder.append("  \"temperature\": 0.7\n");
        requestBodyBuilder.append("}\n");
        return requestBodyBuilder.toString();
    }

    private static String escapeJson(String text) {
        if (text == null) return "";
        return text.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\b", "\\b")
                   .replace("\f", "\\f")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r")
                   .replace("\t", "\\t");
    }

    private static String extractTextFromJson(String json) {
        try {
            Matcher matcher = Pattern.compile("\\\"text\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\\\\\"])*)\\\"").matcher(json);
            if (!matcher.find()) return "Could not parse response.";

            String text = matcher.group(1);
            return text.replace("\\n", "\n")
                       .replace("\\r", "\r")
                       .replace("\\t", "\t")
                       .replace("\\\"", "\"")
                       .replace("\\\\", "\\")
                       .replaceAll("\\*\\*", "");
        } catch (Exception e) {
            return "Error parsing JSON response.";
        }
    }

    private static String extractGroqTextFromJson(String json) {
        try {
            Matcher matcher = Pattern.compile("\\\"content\\\"\\s*:\\s*\\\"((?:\\\\.|[^\\\\\\\"])*)\\\"").matcher(json);
            if (!matcher.find()) return "Could not parse response.";

            String text = matcher.group(1);
            return text.replace("\\n", "\n")
                       .replace("\\r", "\r")
                       .replace("\\t", "\t")
                       .replace("\\\"", "\"")
                       .replace("\\\\", "\\")
                       .replaceAll("\\*\\*", "");
        } catch (Exception e) {
            return "Error parsing JSON response.";
        }
    }
}
