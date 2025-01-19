package project.unit;

import java.security.SecureRandom;

public class OTPGenerator {

    public static String generateNumericOTP(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder();

        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10)); // Append a random digit (0-9)
        }

        return otp.toString();
    }

}