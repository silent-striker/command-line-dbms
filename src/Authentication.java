import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Authentication{
    private final String USER_FILE = "users";
    private final String DELIMITER = ";";
    private final FileUtils fileUtils = new FileUtils();
    private final AuthenticationHelper  authenticationHelper = new AuthenticationHelper();


//    to register a new user
    public boolean registerUser(String userName, String password) {
//        only allow valid users with username and password to register
        if(!authenticationHelper.isValidUserNameAndPassword(userName, password)){
            fileUtils.logMessage("User registration failed");
            return false;
        }

        if(authenticationHelper.isUserAlreadyRegistered(userName)){
            fileUtils.logMessage("Registration failed, user already exists");
        }

        try{
            String hashedPassword = authenticationHelper.hashPassword(password);
            String newUserString = userName + DELIMITER + hashedPassword;
            fileUtils.writeToFile(USER_FILE, newUserString);
        } catch (Exception e){
            e.printStackTrace();
            fileUtils.logMessage("Exception in registering user with username: "+userName);
        }

        fileUtils.logMessage("New User successfully registered with user name: "+userName);

        return true;
    }

//    for user login
    public boolean loginUser(String userName, String password) {
//        checking format of username and password
        if(!authenticationHelper.isValidUserNameAndPassword(userName, password)){
            return false;
        }

//        checking if user is present
        if(!authenticationHelper.isUserAlreadyRegistered(userName)){
            fileUtils.logMessage("Login Failed, user not found: "+userName);
            System.out.println("User not found! with username "+userName);
            return false;
        }

        try{
            String hashedPassword = authenticationHelper.hashPassword(password);
            List<String> users = fileUtils.fetchFileData(USER_FILE);
            for(String user: users){
                String[] parts = user.split(DELIMITER);

                if(parts[0].equals(userName) && hashedPassword.equals(parts[1])){
                    fileUtils.logMessage(userName+" login verified");
                    return true;
                }
            }
        } catch (NoSuchAlgorithmException e){
            fileUtils.logMessage("Exception in user login");
            System.out.println("Exception in login, please try again!");
        }
        return false;
    }

//    to generate a catcha for verification
    public String generateCaptcha() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        Random random = new Random();
        StringBuilder captchaCode = new StringBuilder();
        int captchaLength = 6;

        for (int itr = 0; itr < captchaLength; itr++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            captchaCode.append(randomChar);
        }

        return captchaCode.toString();
    }

//    Validating the generated and typed captcha
    public boolean validateCaptcha(String captcha, String userInput) {
        return captcha.equals(userInput);
    }
}
