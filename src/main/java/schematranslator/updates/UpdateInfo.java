package schematranslator.updates;

import com.linkedin.common.Version;
import java.net.URI;


public class UpdateInfo {
  private Version currentVersion, latestVersion;
  private URI latestReleaseLink;

  public UpdateInfo(Version currentVersion, Version latestVersion, URI latestReleaseLink) {
    this.currentVersion = currentVersion;
    this.latestVersion = latestVersion;
    this.latestReleaseLink = latestReleaseLink;
  }

  public boolean isUpdateAvailable() {
    return currentVersion.compareTo(latestVersion) < 0;
  }

  public String getUpdateText() {
    if (isUpdateAvailable()) {
      return String.format("Version %s available (currently on version %s)", latestVersion, currentVersion);
    } else {
      return String.format("No update available (currently on version %s)", currentVersion);
    }
  }

  public URI getLatestReleaseLink() {
    return latestReleaseLink;
  }
}
