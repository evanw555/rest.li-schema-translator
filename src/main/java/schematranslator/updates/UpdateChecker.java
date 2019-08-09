package schematranslator.updates;

import com.linkedin.common.Version;
import com.linkedin.data.DataMap;
import com.linkedin.data.codec.JacksonDataCodec;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.function.Consumer;
import schematranslator.AppProperties;


public class UpdateChecker {
  public void checkForUpdates(Consumer<UpdateInfo> callback) throws IOException {
    // Fetch latest version info from remote repo
    InputStream is = AppProperties.getUpdateUrl().openStream();
    JacksonDataCodec codec = new JacksonDataCodec();
    DataMap latestReleaseInfo = codec.readMap(is);
    is.close();
    String tagName = latestReleaseInfo.getString("tag_name");
    String htmlLink = latestReleaseInfo.getString("html_url");
    Version latestVersion = new Version(tagName);

    // Create link to download latest release
    URI latestReleaseLink;
    try {
      latestReleaseLink = new URI(htmlLink);
    } catch (URISyntaxException e) {
      e.printStackTrace();
      return;
    }

    // Give this information back to the application
    Version currentVersion = AppProperties.getVersion();
    callback.accept(new UpdateInfo(currentVersion, latestVersion, latestReleaseLink));
  }
}
