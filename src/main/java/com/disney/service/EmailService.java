package com.disney.service;

import java.io.IOException;

public interface EmailService {
    
    void sendWelcomeEmailTo(String to) throws IOException;

}
