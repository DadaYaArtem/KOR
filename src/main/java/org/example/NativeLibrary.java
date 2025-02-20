package org.example;

public class NativeLibrary {
    static {
        System.load("C:\\OAiP\\Politteh\\MyLibrary.dll"); // Завантаження DLL
    }

    public native boolean validateEmail(String email);
    public native boolean validateUserId(int userId);
    public native void logMessage(String message);
    public native int generateUniqueID();

    public static void main(String[] args) {
        NativeLibrary lib = new NativeLibrary();

        // Перевірка email
        System.out.println("Перевірка 'test@example.com': " + lib.validateEmail("test@example.com"));  //  true
        System.out.println("Перевірка 'invalid-email': " + lib.validateEmail("invalid-email"));        //  false
        System.out.println("Перевірка 'hello@domain.co': " + lib.validateEmail("hello@domain.co"));    //  true

        // Перевірка userId
        System.out.println("Перевірка userId = 100: " + lib.validateUserId(100));  //  true
        System.out.println("Перевірка userId = 0: " + lib.validateUserId(0));    //  false
        System.out.println("Перевірка userId = -5: " + lib.validateUserId(-5));  //  false

        // Логування повідомлення
        lib.logMessage("Test message.");

        // Генерація ID
        System.out.println("Згенерований унікальний ідентифікатор: " + lib.generateUniqueID());
    }
}

