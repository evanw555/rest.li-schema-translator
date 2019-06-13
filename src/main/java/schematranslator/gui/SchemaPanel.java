package schematranslator.gui;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.BevelBorder;


public class SchemaPanel extends JPanel {
  private String _title;
  private JTextArea _textArea;

  public SchemaPanel(String title, Consumer<String> onChange) {
    _title = title;

    this.setBorder(new BevelBorder(BevelBorder.LOWERED));
    this.setLayout(new BorderLayout());
    this.add(new JLabel(title), BorderLayout.NORTH);

    _textArea = new JTextArea("");
    _textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
    _textArea.setLineWrap(true);
    _textArea.setColumns(40);
    _textArea.setTabSize(2);
    _textArea.getDocument().addDocumentListener((SimpleDocumentListener) (documentEvent) -> {
      if (_textArea.isFocusOwner()) {
        onChange.accept(_textArea.getText());
      }
    });

    JScrollPane scrollPane = new JScrollPane(_textArea);
    scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

    this.add(scrollPane, BorderLayout.CENTER);
  }

  public void setText(String text) {
    // Ensure that the text is different to avoid a needless chain of updates
    if (!_textArea.getText().trim().equals(text))
    {
      _textArea.setText(text);
    }
  }

  public void setError(boolean isError) {
    _textArea.setBackground(isError ? new Color(255, 200, 200) : Color.WHITE);
  }
}
