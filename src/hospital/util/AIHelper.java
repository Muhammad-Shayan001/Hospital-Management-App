package hospital.util;

import java.util.Scanner;

public class AIHelper {
    public static void startChat(Scanner scanner) {
        System.out.println("\n--- 🤖 AI Medical Assistant ---");
        System.out.println("Hello! I can provide basic health guidance and detect emergencies.");
        System.out.println("Type 'exit' to leave the chat.");
        
        while(true) {
            System.out.print("\nYou: ");
            String input = scanner.nextLine().toLowerCase();
            
            if(input.equals("exit") || input.equals("quit")) {
                System.out.println("AI: Goodbye! Stay healthy.");
                break;
            }
            
            if(input.contains("chest pain") || input.contains("breathing problem") || input.contains("severe bleeding") || input.contains("emergency") || input.contains("heart attack")) {
                System.out.println("AI: 🚨 EMERGENCY DETECTED! 🚨");
                System.out.println("AI: Please visit the nearest hospital immediately or call an ambulance right away!");
                System.out.println("AI: (We have marked this session as urgent in our system.)");
            } else if (input.contains("fever") || input.contains("headache") || input.contains("cold") || input.contains("cough")) {
                System.out.println("AI: It sounds like a common ailment. Rest, stay hydrated, and monitor your temperature.");
                System.out.println("AI: If symptoms persist for more than a few days, please book an appointment with a doctor.");
            } else {
                System.out.println("AI: Thank you for sharing. I recommend keeping track of any changes in how you feel.");
            }
            
            System.out.println("\n⚠️ This information is for guidance only. Please consult a qualified doctor for proper medical diagnosis and treatment.");
        }
    }
}
