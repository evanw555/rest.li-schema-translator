package schematranslator;

import com.linkedin.common.Version;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;


public class AppProperties {
  private static Properties properties;
  static {
    properties = new Properties();
    try {
      properties.load(AppProperties.class.getResourceAsStream("app.properties"));
    } catch (IOException e) {
      throw new IllegalStateException("Unable to load app properties", e);
    }
  }

  private AppProperties() {}

  public static Version getVersion() {
    return new Version(properties.getProperty("version"));
  }

  public static URL getUpdateUrl() throws MalformedURLException {
    return new URL(properties.getProperty("updates.uri"));
  }

  public static URI getDocsUri() throws URISyntaxException {
    return new URI(properties.getProperty("docs.uri"));
  }
}
