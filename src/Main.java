import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        boolean isLoggedIn = false;
        System.out.println("Are you a new user? (Y/N): ");
        Scanner scanner = new Scanner(System.in);
        String userInput = scanner.nextLine();

        if (userInput.equalsIgnoreCase("Y")) {
            System.out.println("Enter a new user id: ");
            String userId = scanner.nextLine();
            System.out.println("Enter a password: ");
            String password = scanner.nextLine();

            Authentication authentication = new Authentication();
            boolean isUserRegistered = authentication.registerUser(userId, password);

            if (isUserRegistered) {
                System.out.println("User registration successful");
            } else {
                System.out.println("User registration failed");
            }
        }
        else if (userInput.equalsIgnoreCase("N")) {
            System.out.println("Enter your user id:");
            String userId = scanner.nextLine();
            System.out.println("Enter your password:");
            String password = scanner.nextLine();

            Authentication authentication = new Authentication();
            boolean manualLogin = authentication.loginUser(userId, password);

            if(manualLogin){
                System.out.println("Verify that you are not a robot");
                String generatedCaptcha = authentication.generateCaptcha();
                System.out.println("Please type the captcha below: "+generatedCaptcha);
                String inputedCaptcha = scanner.nextLine();

                Boolean captchaVerified = authentication.validateCaptcha(generatedCaptcha, inputedCaptcha);
                if(captchaVerified){
                    System.out.println("User login successful");
                    isLoggedIn = true;
                }
                else {
                    System.out.println("Captcha failed, unable verify!");
                }
            }
            else {
                System.out.println("User login failed");
            }
        } else {
            System.out.println("Invalid input!");
        }

        Query queryObj = new Query();
        while(isLoggedIn){
            System.out.println("Which query you want to run? Type exit to quit");
            String query = scanner.nextLine();
            if(query.equalsIgnoreCase("exit")) {
                break;
            }
            queryObj.queryDispatcher(query);
        }
        scanner.close();
    }
}