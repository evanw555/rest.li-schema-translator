package schematranslator.gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.swing.*;
import schematranslator.updates.UpdateInfo;


public class UpdateLabel extends JLabel {

  public UpdateLabel(UpdateInfo updateInfo) {
    super(updateInfo.getUpdateText() + (updateInfo.isUpdateAvailable() ? " - Click here to download" : ""));

    if (updateInfo.isUpdateAvailable()) {
      setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
      addMouseListener(new MouseAdapter() {

        @Override
        public void mouseClicked(MouseEvent e) {
          if (e.getButton() == MouseEvent.BUTTON1) {
            try {
              if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(updateInfo.getLatestReleaseLink());
              }
            } catch (IOException ex) {
              ex.printStackTrace();
            }
          }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
          setForeground(Color.BLUE.darker());
        }

        @Override
        public void mouseExited(MouseEvent e) {
          setForeground(Color.BLACK);
        }
      });
    }
  }
}
