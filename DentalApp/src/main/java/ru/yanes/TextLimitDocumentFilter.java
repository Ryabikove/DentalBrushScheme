package ru.yanes;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

class TextLimitDocumentFilter extends DocumentFilter {
	private final int rowLimit;
	private final int charLimit;

	TextLimitDocumentFilter(int rows, int chars) {
		this.rowLimit = rows;
		this.charLimit = chars;
	}

	@Override
	public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
		String current = fb.getDocument().getText(0, fb.getDocument().getLength());
		String newText = current.substring(0, offset) + string + current.substring(offset);
		if (newText.length() <= charLimit && lineCount(newText) <= rowLimit) {
			super.insertString(fb, offset, string, attr);
		}
	}

	@Override
	public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
		String current = fb.getDocument().getText(0, fb.getDocument().getLength());
		String newText = current.substring(0, offset) + text + current.substring(offset + length);

		if (newText.length() <= charLimit && lineCount(newText) <= rowLimit) {
			super.replace(fb, offset, length, text, attrs);
		}
	}

	private int lineCount(String text) {
		int count = text.isEmpty() ? 0 : 1;
		for (char c : text.toCharArray()) {
			if (c == '\n') count++;
		}
		return count;
	}
}