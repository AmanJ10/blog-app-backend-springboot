//package com.aman.blog.config;
//
//import com.google.auth.oauth2.GoogleCredentials;
//import com.google.firebase.FirebaseApp;
//import com.google.firebase.FirebaseOptions;
//import jakarta.annotation.PostConstruct;
//import org.springframework.context.annotation.Configuration;
//
//import java.io.FileInputStream;
//import java.io.IOException;
//
//@Configuration
//public class FirebaseConfig {
//    @PostConstruct
//    public void initFirebase() throws IOException {
//        FileInputStream serviceAccount =
//                new FileInputStream("src/main/resources/rentretreat-2fba4-firebase-adminsdk-at7q8-26059b3fdb.json");
//
//        FirebaseOptions options = FirebaseOptions.builder()
//                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
//                .setStorageBucket("rentretreat-2fba4.appspot.com")
//                .build();
//
//        if (FirebaseApp.getApps().isEmpty()) {
//            FirebaseApp.initializeApp(options);
//        }
//    }
//}


package com.aman.blog.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initFirebase() throws IOException {
        FirebaseOptions options;

        // 1️⃣ Use FIREBASE_CONFIG env var if present (for deployment)
        String firebaseConfig = System.getenv("FIREBASE_CONFIG");

        if (firebaseConfig != null && !firebaseConfig.isEmpty()) {
            options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(
                            new ByteArrayInputStream(firebaseConfig.getBytes(StandardCharsets.UTF_8))
                    ))
                    .setStorageBucket("rentretreat-2fba4.appspot.com")
                    .build();
        }
        // 2️⃣ Otherwise use local file (for development)
        else {
            FileInputStream serviceAccount =
                    new FileInputStream("/Users/amanjoharapurkar/java-secrets/firebase-key.json");


            options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setStorageBucket("rentretreat-2fba4.appspot.com")
                    .build();
        }

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }
    }
}
