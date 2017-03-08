# Password Manager

Desktop application to store passwords, credit card numbers and other sensitive information. Application uses 256-bit AES encryption.

## Security Considerations

If the application is used as a standalone JAR file JRE must be updated with [Java Cryptography Extension (JCE) Unlimited Strength Jurisdiction Policy Files](http://www.oracle.com/technetwork/java/javase/downloads/index.html).

Application enforces security via file encryption only. Application makes no effort to counter attacks targeted to user account, operating system or hardware including RAM.
