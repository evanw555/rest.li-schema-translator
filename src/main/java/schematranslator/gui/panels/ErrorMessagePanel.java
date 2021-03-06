package schematranslator.gui.panels;

import java.awt.*;
import javax.swing.*;


public class ErrorMessagePanel extends JPanel {
  private JTextArea _textArea;

  public ErrorMessagePanel() {
    _textArea = new JTextArea();
    _textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
    _textArea.setLineWrap(true);
    _textArea.setEditable(false);

    JScrollPane scrollPane = new JScrollPane(_textArea,
                                             JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                             JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
    this.setLayout(new BorderLayout());
    this.add(scrollPane, BorderLayout.CENTER);

    clear();
  }

  public void clear() {
    _textArea.setText("");
    _textArea.setBackground(new Color(221, 221, 221));
  }

  public void setError(Throwable e) {
    _textArea.setText(e.getMessage());
    _textArea.setCaretPosition(0);
    _textArea.setBackground(Color.WHITE);
  }
}
