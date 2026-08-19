package ru.yanes;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.text.PlainDocument;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class CommentTextArea extends JTextArea {
	private final int borderRadius = 20;
	private final float borderThickness = 2F;
	private final int rowLimit;
	private final int colLimit;

	private Color background;

	public CommentTextArea(int rows, int columns, Font font, Color background) {
		super(rows, columns);
		this.rowLimit = rows;
		this.colLimit = columns;

		this.background = background;

		this.setEditable(true);
		this.setLineWrap(true);
		this.setWrapStyleWord(true);
		this.setOpaque(false);
		this.setFont(font);

		this.setBorder(new CommentTextArea.RoundBorder(borderThickness, borderRadius, background.darker()));

		PlainDocument document = (PlainDocument) this.getDocument();
		document.setDocumentFilter(new TextLimitDocumentFilter(rowLimit, colLimit * rowLimit));
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setColor(background);

		FontMetrics fontMetrics = g2d.getFontMetrics();
		int lineHeight = fontMetrics.getHeight();
		int textStartX = getInsets().left;

		g2d.setColor(background.darker());
		int y = getInsets().top + fontMetrics.getAscent() + 1;
		for (int i = 0; i < rowLimit; i++) {
			g2d.drawLine(textStartX, y, getWidth() - getInsets().right, y);
			y += lineHeight;
		}

		super.paintComponent(g2d);
		g2d.dispose();
	}

	class RoundBorder extends AbstractBorder {
		private final float borderThickness;
		private final int cornerRadius;
		private final Color borderColor;

		public RoundBorder(float borderThickness, int cornerRadius, Color borderColor) {
			this.borderThickness = borderThickness;
			this.cornerRadius = cornerRadius;
			this.borderColor = borderColor;
		}

		@Override
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
			Graphics2D g2d = (Graphics2D) g.create();
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setColor(borderColor);
			g2d.setStroke(new BasicStroke(borderThickness));
			g2d.drawRoundRect(x, y, width - 1, height - 1, cornerRadius, cornerRadius);
			g2d.dispose();
		}

		@Override
		public Insets getBorderInsets(Component c) {
			int offset = cornerRadius;
			return new Insets(offset, offset, offset, offset);
		}
	}
}