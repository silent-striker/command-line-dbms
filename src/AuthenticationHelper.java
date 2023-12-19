import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class AuthenticationHelper {
    private final String ENCRYPTION_TYPE = "SHA-1";
    private final String USER_FILE = "users";
    private final String DELIMITER = ";";
    private final FileUtils fileUtils = new FileUtils();

    //    checking if user is already present
    public boolean isUserAlreadyRegistered(String userName){
        List<String> userFileData = fileUtils.fetchFileData(USER_FILE);
        for(String s: userFileData){
            String existingName = s.split(DELIMITER)[0];
            if(userName.equals(existingName)){
                return true;
            }
        }

        return false;
    }

    private boolean isAlphaNumeric(String input){
        for(int i=0; i < input.length(); i++){
            if(!Character.isDigit(input.charAt(i)) && !Character.isLetter(input.charAt(i))){
                return false;
            }
        }
        return true;
    }

    public boolean isValidPassword(String password){
//        check is alphanumberic
        if(!isAlphaNumeric(password)){
            System.out.println("Password invalid! use only alphanumeric");
            return false;
        }

//        check is 10 chars max
        if(password.length() > 10){
            System.out.println("Password can have max 10 characters!");
            return false;
        }

        return true;
    }


    public boolean isValidUserNameAndPassword(String userName, String password){
//        checking userName and password are present
        if(userName.isEmpty() || password.isEmpty()){
            System.out.println("user id or password is empty");
            return false;
        }

//        check characters are alphanumeric for username
        if(!isAlphaNumeric(userName)){
            System.out.println("Username invalid! use only alphanumeric");
            return false;
        }

//        check for characters in password
        if(!isValidPassword(password)){
            return false;
        }

        return true;
    }

    public String hashPassword(String plainText) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance(ENCRYPTION_TYPE);
        byte[] inputBytes = plainText.getBytes();
        byte[] digestedInput = messageDigest.digest(inputBytes);

//        hashed password in hexadecimal
        StringBuilder hashedPassword = new StringBuilder();
        for(byte b: digestedInput){
//            "%02x" specifies hexadecimal format
            hashedPassword.append(String.format("%02x", b));
        }

        return hashedPassword.toString();
    }
}
