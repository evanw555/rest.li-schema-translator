package schematranslator.updates;

import com.linkedin.common.Version;
import com.linkedin.data.DataMap;
import com.linkedin.data.codec.JacksonDataCodec;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import javax.net.ssl.HttpsURLConnection;
import schematranslator.AppProperties;


public class UpdateChecker {
  public UpdateInfo checkForUpdates() throws IOException, URISyntaxException {
    // Fetch latest version info from remote repo
    HttpsURLConnection connection = (HttpsURLConnection) AppProperties.getUpdateUrl().openConnection();
    connection.connect();

    // Get response headers
    String rateLimitLimit = connection.getHeaderField("X-RateLimit-Limit");
    String rateLimitRemaining = connection.getHeaderField("X-RateLimit-Remaining");
    String rateLimitReset = connection.getHeaderField("X-RateLimit-Reset");
    System.out.println(String.format("Update checker quota remaining = %s/%s", rateLimitRemaining, rateLimitLimit));

    if (connection.getResponseCode() == 200) {
      // Handle successful response
      InputStream is = connection.getInputStream();
      JacksonDataCodec codec = new JacksonDataCodec();
      DataMap latestReleaseInfo = codec.readMap(is);
      is.close();
      String tagName = latestReleaseInfo.getString("tag_name");
      String htmlLink = latestReleaseInfo.getString("html_url");
      Version latestVersion = new Version(tagName);

      // Create link to download latest release
      URI latestReleaseLink  = new URI(htmlLink);

      // Give this information back to the application
      Version currentVersion = AppProperties.getVersion();
      return new UpdateInfo(currentVersion, latestVersion, latestReleaseLink);
    } else {
      // Handle error response
      if (rateLimitReset == null || rateLimitLimit == null) {
        throw new RuntimeException("Received error response code " + connection.getResponseCode());
      }

      long secondsUntilReset = Long.valueOf(rateLimitReset) - Instant.now().getEpochSecond();

      String rateLimitErrorMessage =
          String.format("Rate limit (%s requests) exceeded, resets in %02dm:%02ds",
              rateLimitLimit,
              secondsUntilReset / 60,
              secondsUntilReset % 60);

      throw new RuntimeException(rateLimitErrorMessage);
    }
  }
}
