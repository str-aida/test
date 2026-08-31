package com.Trabajo_Final_Beltran.config;

import com.Trabajo_Final_Beltran.enums.StorageProvider;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "storage")
public class StorageProperties {

  private StorageProvider provider;

  private Local local = new Local();

  private S3 s3 = new S3();

  @Getter
  @Setter
  public static class Local {

    private String path;
  }

  @Getter
  @Setter
  public static class S3 {

    private String endpoint;

    private String region;

    private String bucket;

    private String accessKey;

    private String secretKey;

    private String publicUrl;
  }
}