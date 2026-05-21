package hospital.util;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AIHelper {
    private static final String API_KEY = "AIzaSyAb9efYUUNO6sC0K8RX3Xd6AM6ZfagcRUM";
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-flash-latest:generateContent?key=" + API_KEY;

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
        System.out.println("Hello! I am your AI Medical Assistant powered by Gemini.");
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
        try {
            String systemPrompt = "You are a professional, helpful, and empathetic AI Medical Assistant for a Hospital Management System. Your role is to provide basic health guidance, symptom checking, and to advise users when to seek emergency care. Do not diagnose conditions definitively. Keep your responses concise and readable for a CLI interface. Avoid heavy markdown formats, use simple text.";

            StringBuilder requestBodyBuilder = new StringBuilder();
            requestBodyBuilder.append("{\n");
            requestBodyBuilder.append("  \"contents\": [\n");

            // Add system instruction as first user message if this is the first message
            if (history.size() == 1) {
                requestBodyBuilder.append("    {\n");
                requestBodyBuilder.append("      \"role\": \"user\",\n");
                requestBodyBuilder.append("      \"parts\": [{\"text\": \"").append(escapeJson(systemPrompt)).append("\"}]\n");
                requestBodyBuilder.append("    },\n");
                requestBodyBuilder.append("    {\n");
                requestBodyBuilder.append("      \"role\": \"model\",\n");
                requestBodyBuilder.append("      \"parts\": [{\"text\": \"Understood. I'm ready to assist as your AI Medical Assistant.\"}]\n");
                requestBodyBuilder.append("    },\n");
            }

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
                    
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBodyBuilder.toString()))
                    .build();
                    
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return extractTextFromJson(response.body());
            } else {
                System.err.println("API Error (" + response.statusCode() + "): " + response.body());
                return null;
            }
        } catch (Exception e) {
            System.err.println("Exception during API call: " + e.getMessage());
            return null;
        }
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
            String targetKey = "\"text\":";
            int textIndex = json.indexOf(targetKey);
            if (textIndex == -1) return "Could not parse response.";
            
            int startIndex = json.indexOf("\"", textIndex + targetKey.length());
            if (startIndex == -1) return "Could not parse response.";
            startIndex++; // skip the opening quote
            
            int endIndex = startIndex;
            while (endIndex < json.length()) {
                if (json.charAt(endIndex) == '\"' && json.charAt(endIndex - 1) != '\\') {
                    // Make sure the backslash itself is not escaped
                    int backslashCount = 0;
                    for (int i = endIndex - 1; i >= startIndex; i--) {
                        if (json.charAt(i) == '\\') backslashCount++;
                        else break;
                    }
                    if (backslashCount % 2 == 0) {
                        break;
                    }
                }
                endIndex++;
            }
            
            String text = json.substring(startIndex, endIndex);
            return text.replace("\\n", "\n")
                       .replace("\\\"", "\"")
                       .replace("\\\\", "\\")
                       .replace("\\t", "\t")
                       .replaceAll("\\*\\*", ""); // Remove markdown bold for CLI readability
        } catch (Exception e) {
            return "Error parsing JSON response.";
        }
    }
}
