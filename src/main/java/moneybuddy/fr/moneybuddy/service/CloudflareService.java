/*
								* Copyright moneybuddy.fr moneybuddy
								*/
package moneybuddy.fr.moneybuddy.service;

import java.io.IOException;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import moneybuddy.fr.moneybuddy.config.CloudflareBucketConfig;
import moneybuddy.fr.moneybuddy.exception.CloudFlareNotSupportedFileType;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
@RequiredArgsConstructor
public class CloudflareService {

  private final CloudflareBucketConfig cloudflareBucketConfig;

  @Value("${cloudflare.bucket_name}")
  private String bucket_name;

  public void remove(String id) {
    S3Client s3 = cloudflareBucketConfig.s3Client();
    DeleteObjectRequest deleteRequest =
        DeleteObjectRequest.builder().bucket(bucket_name).key(id).build();
    s3.deleteObject(deleteRequest);
  }

  public String uploadImage(MultipartFile file) throws FileUploadException {
    String original = file.getOriginalFilename().toLowerCase();
    String contentType = file.getContentType();

    String ext = getFileExtension(original);
    String folder =
        switch (ext) {
          case "jpg", "jpeg", "png", "gif", "webp" -> "images";
          case "mp4", "mov" -> "videos";
          case "pdf", "doc", "docx", "txt" -> "documents";
          default -> throw new CloudFlareNotSupportedFileType(ext);
        };

    String key = String.format("%s/%s-%s", folder, UUID.randomUUID(), original);

    PutObjectRequest req =
        PutObjectRequest.builder().bucket(bucket_name).key(key).contentType(contentType).build();

    S3Client s3 = cloudflareBucketConfig.s3Client();

    try {
      s3.putObject(req, RequestBody.fromBytes(file.getBytes()));
    } catch (IOException e) {
      throw new FileUploadException("File upload to Cloudflare R2 failed", e);
    }

    return key;
  }

  private String getFileExtension(String filename) {
    int idx = filename.lastIndexOf('.');
    if (idx < 0 || idx == filename.length() - 1) {
      throw new IllegalArgumentException("Invalid file extension in filename: " + filename);
    }
    return filename.substring(idx + 1);
  }
}
