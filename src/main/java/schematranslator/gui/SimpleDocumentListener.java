package schematranslator.gui;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

@FunctionalInterface
public interface SimpleDocumentListener extends DocumentListener {
  @Override
  default void insertUpdate(DocumentEvent e) {
    onChange(e);
  }

  @Override
  default void removeUpdate(DocumentEvent e) {
    onChange(e);
  }

  @Override
  default void changedUpdate(DocumentEvent e) {
    onChange(e);
  }

  void onChange(DocumentEvent e);
}
