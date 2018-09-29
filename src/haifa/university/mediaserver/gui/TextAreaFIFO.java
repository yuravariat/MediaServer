package haifa.university.mediaserver.gui;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

public class TextAreaFIFO extends JTextArea implements DocumentListener {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int maxLines;

    public TextAreaFIFO(int lines) {
        maxLines = lines;
        getDocument().addDocumentListener(this);
    }

    public void insertUpdate(DocumentEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                removeLines();
            }
        });
    }

    public void removeUpdate(DocumentEvent e) {
    }

    public void changedUpdate(DocumentEvent e) {
    }

    public void removeLines() {
        Element root = getDocument().getDefaultRootElement();
        while (root.getElementCount() > maxLines) {
            Element firstLine = root.getElement(0);
            try {
                getDocument().remove(0, firstLine.getEndOffset());
            } catch (BadLocationException ble) {
                System.out.println(ble);
            }
        }
    }
}