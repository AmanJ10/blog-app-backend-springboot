package com.aman.blog.services.impl;

import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Blob;
import com.google.firebase.cloud.StorageClient;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class FirebaseStorageService {

    public String uploadFile(MultipartFile file, String folderName) throws IOException {
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

        // 👇 Combine folder + filename (this creates a virtual folder in Firebase Storage)
        String objectName = folderName + "/" + fileName;

        Bucket bucket = StorageClient.getInstance().bucket();
        Blob blob = bucket.create(objectName, file.getBytes(), file.getContentType());

        // 🔹 Instead of using the Google Cloud Storage URL, generate a Firebase public download URL

        return String.format(
                "https://firebasestorage.googleapis.com/v0/b/%s/o/%s?alt=media",
                bucket.getName(),
                objectName.replace("/", "%2F")  // encode slashes for URL
        );
    }

    public void deleteFileByUrl(String fileUrl) {
        try {
            // Extract bucket name and path from the public URL
            // Example URL: https://storage.googleapis.com/my-bucket/spring-boot-blog-images/abc123.jpg
            String bucketName = StorageClient.getInstance().bucket().getName();
            String prefix = "https://storage.googleapis.com/" + bucketName + "/";
            String objectName = fileUrl.replace(prefix, ""); // Remove URL prefix

            Bucket bucket = StorageClient.getInstance().bucket();
            boolean deleted = bucket.get(objectName) != null && bucket.get(objectName).delete();

            if (deleted) {
                System.out.println("✅ Deleted from Firebase: " + objectName);
            } else {
                System.out.println("⚠️ File not found or already deleted: " + objectName);
            }
        } catch (Exception e) {
            System.err.println("❌ Error deleting file from Firebase: " + e.getMessage());
        }
    }

}
