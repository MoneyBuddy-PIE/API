package moneybuddy.fr.moneybuddy.config;

import java.net.URI;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;


@Configuration
@Component
@RequiredArgsConstructor
public class CloudflareBucketConfig {

    @Value("${cloudflare.api}")
    private String endpoint;

    @Value("${cloudflare.access_key}")
    private String access_key;

    @Value("${cloudflare.secret_key}")
    private String secret_key;
    
    @Bean
    public S3Client s3Client() {
        System.out.println(endpoint);
        System.out.println(access_key);
        System.out.println(secret_key);

        S3Configuration serviceConfig = S3Configuration.builder()
                // path-style is required for R2
                .pathStyleAccessEnabled(true)
                // disable AWS4 chunked uploads
                .chunkedEncodingEnabled(false)
                .build();

        return S3Client.builder()
                .httpClientBuilder(ApacheHttpClient.builder())
                .region(Region.of("auto"))
                .endpointOverride(URI.create(endpoint))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create( access_key, secret_key)
                        ))
                .serviceConfiguration(serviceConfig)
                .build();

    }


}
