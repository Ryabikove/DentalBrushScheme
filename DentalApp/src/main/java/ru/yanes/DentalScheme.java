package ru.yanes;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.text.PlainDocument;

import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;

import ru.yanes.data.Brush;
import ru.yanes.data.Mouth;
import ru.yanes.data.Space;
import ru.yanes.data.Tooth;

public class DentalScheme extends JFrame {
	private Brush selectedBrush;
	private final int[] windowMinSize = new int[]{1600, 1300};
	private final int[] brushPanelPreferSize = new int[]{400, 0};
	private final int[] commentPanelPreferSize = new int[]{400, 0};
	private final int[] toolPanelPreferSize = new int[]{0, 30};
	private String basicFont;
	private int count = 0;
	private final String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

	private final Color defaultBackground = new Color(230, 240, 230);

	public DentalScheme() {
		Mouth mouth = new Mouth();
		basicFont = fonts[count];
		System.out.println("Font Name: " + basicFont);

		this.setTitle("\ud83e\uddb7 Дентальная схема");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setMinimumSize(new Dimension(windowMinSize[0], windowMinSize[1]));

		DentalPanel dentalPanel = new DentalPanel(mouth);
		this.add(new BrushPanel(dentalPanel), BorderLayout.EAST);
		this.add(new ToolPanel(this), BorderLayout.NORTH);
		this.add(new ToolPanel(dentalPanel), BorderLayout.NORTH);
		this.add(dentalPanel, BorderLayout.CENTER);

		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		SwingUtilities.invokeLater(DentalScheme::new);
	}

	class ToolPanel extends JPanel {
		private final DentalPanel frameToPrint;

		public ToolPanel(DentalPanel dentalPanel) {
			this.frameToPrint = dentalPanel;
			this.setPreferredSize(new Dimension(toolPanelPreferSize[0], toolPanelPreferSize[1]));
			this.setBackground(defaultBackground);
			this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			this.setAlignmentY(TOP_ALIGNMENT);
			this.add(getPrintButton());
		}

		private JButton getPrintButton() {
			return getToolButton("Печать", e -> {

				PrinterJob job = PrinterJob.getPrinterJob();

				job.setPrintable(frameToPrint);

				if (job.printDialog()) {
					try {
						job.print();
					} catch (PrinterException e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(frameToPrint, "Ошибка печати: " + e1.getMessage());
					}
				}
			});
		}

		private JButton getToolButton(String text, ActionListener actionListener) {
			JButton button = new JButton(text);
			button.setFont(new Font(basicFont, Font.PLAIN, 18));
			button.setPreferredSize(new Dimension(100,30));
			button.setFocusPainted(false);
			button.setOpaque(true);

			button.addActionListener(actionListener);
			return button;
		}
	}

	class CommentPanel extends JPanel {
		private final String comment = "Поле для комментария";
		private final int commentAreaRowLimit = 40;
		private final int commentAreaColumnLimit = 25;

		public CommentPanel() {
			this.setBackground(defaultBackground);
			this.setPreferredSize(new Dimension(commentPanelPreferSize[0], commentPanelPreferSize[1]));
			this.setLayout(new GridBagLayout());

			GridBagConstraints gbc = new GridBagConstraints();

			gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.insets = new Insets(15, 15, 15, 15);
			gbc.anchor = GridBagConstraints.CENTER;

			this.add(new CommentTextArea(commentAreaRowLimit, commentAreaColumnLimit, new Font(basicFont, Font.PLAIN, 18), defaultBackground), gbc);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		}

		class CommentTextArea extends JTextArea {
			private final int borderRadius = 20;
			private final float borderThickness = 1.5F;

			private Color background;

			public CommentTextArea(int rows, int columns, Font font, Color background) {
				super(rows, columns);

				this.background = background;

				this.setEditable(true);
				this.setLineWrap(true);
				this.setWrapStyleWord(true);
				this.setOpaque(false);

				this.setFont(font);

				this.setBorder(new RoundBorder(borderThickness, borderRadius, background.darker()));
				PlainDocument document = (PlainDocument) this.getDocument();
				document.setDocumentFilter(new TextLimitDocumentFilter(rows, columns * rows));
			}

			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				Graphics2D g2d = (Graphics2D) g.create();
				g2d.setColor(background);

				FontMetrics fontMetrics = g2d.getFontMetrics();
				int lineHeight = fontMetrics.getHeight();
				int textStartX = getInsets().left;

				g2d.setColor(defaultBackground.darker());
				int y = getInsets().top + fontMetrics.getAscent();
				while (y < getHeight()) {
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
	}

	class BrushPanel extends JPanel {
		private final DentalPanel dentalPanel;
		private final int brushRadius = 70;

		BrushPanel(DentalPanel dentalPanel) {
			this.dentalPanel = dentalPanel;
			this.setBackground(defaultBackground);
			this.setPreferredSize(new Dimension(brushPanelPreferSize[0], brushPanelPreferSize[1]));

			this.addMouseListener(new MouseAdapter() {
					@Override
					public void mousePressed(MouseEvent e) {
						handlePanelClick(e.getX(), e.getY());
					}
			});
		}

		private void handlePanelClick(int mouseX, int mouseY) {
			int centerX = getWidth() / 2;
			int centerY = getHeight() / 2;

			// Check Brush click target
			if (checkBrushClick(mouseX, mouseY, centerX, centerY)) {
				repaint();
				dentalPanel.repaint();
				return;
			}
		}

		private boolean checkBrushClick(int mouseX, int mouseY, int centerX, int centerY) {
			int brushCount = Brush.values().length;

			for (int i = 1; i <= brushCount; i++) {

				int brushPos = this.getHeight() / (brushCount + 1) * i;

				//If x,y of mouse in brush
				if (getDistance(centerX, brushPos, mouseX, mouseY) <= (double) brushRadius / 2) {
					Brush clickedBrush = Brush.values()[i - 1];

					//Then if brush has been selected - unpin, if it hasn't - pin
					if (Objects.equals(clickedBrush, selectedBrush)) {
						System.out.println("Unpinned brush - " + clickedBrush.getDiameter());
						selectedBrush = null;
					} else {
						System.out.println("Pinned brush - " + clickedBrush.getDiameter());
						selectedBrush = clickedBrush;
					}

					return true;
				}

			}

			return false;
		}

		private double getDistance(int x1, int y1, int x2, int y2) {
			return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int brushCount = Brush.values().length;
			int centerX = this.getWidth() / 2;

			for (int i = 1; i <= brushCount; i++) {
				Brush brush = Brush.values()[i - 1];
				int brushPos = this.getHeight() / (brushCount + 1) * i;

				// Save the original transform state
				AffineTransform originalTransform = g2d.getTransform();

				// Translate the origin to our calculated x, y coordinates
				g2d.translate(centerX, brushPos);

				if (Objects.equals(brush,selectedBrush)){
					g2d.setColor(Color.LIGHT_GRAY);
					g2d.setStroke(new BasicStroke(10F));
					g2d.drawOval(-brushRadius / 2, -brushRadius / 2, brushRadius + 5, brushRadius + 5);
				}

				// Draw the brush shape (centered on 0,0 since we translated)
				g2d.setColor(brush.getColor());
				g2d.fillOval(-brushRadius / 2, -brushRadius / 2, brushRadius, brushRadius);


				// Draw the outline
				g2d.setColor(Color.BLACK);
				g2d.setStroke(new BasicStroke(2.5F));
				g2d.drawOval(-brushRadius / 2, -brushRadius / 2, brushRadius, brushRadius);

				// Draw brush diameter
				String posText = String.valueOf(brush.getDiameter());
				g2d.setFont(new Font("Arial", Font.BOLD, 14));
				FontMetrics fontMetrics = g2d.getFontMetrics();
				int textX = -fontMetrics.stringWidth(posText) / 2;
				int textY = fontMetrics.getHeight() / 2 - 2;

				g2d.drawString(posText, textX, textY);

				// Restore the original transform so the next tooth draws correctly
				g2d.setTransform(originalTransform);
			}

			g2d.dispose();
		}


	}

	class DentalPanel extends JPanel implements Printable {
		private final Mouth mouth;

		// Define the ellipse boundaries for the arch
		private final int radiusX = 350;
		private final int radiusY = 430;
		//Define start and end angles for each jaw
		private final double startAngleUpperJaw = Math.PI * 1.05;
		private final double endAngleUpperJaw = Math.PI * 1.95;
		private final double startAngleLowerJaw = Math.PI * 0.95;
		private final double endAngleLowerJaw = Math.PI * 0.05;

		// Define tooth width and height
		private final int toothWidth = 45;
		private final int toothHeight = 55;
		private final Color basicToothColor = Color.WHITE;

		// Define space width and height
		private final int spaceWidth = 15;
		private final int spaceHeight = 45;
		// Define space [x] and [y] arrays
		private final int[] basicSpaceX = new int[]{0, spaceWidth, -spaceWidth};
		private final int[] outerSpaceY = new int[]{-toothHeight / 3, -spaceHeight, -spaceHeight};
		private final int[] innerSpaceY = new int[]{toothHeight / 3, spaceHeight, spaceHeight};
		//Define basic space color
		private final Color basicSpaceColor = defaultBackground;

		// Collision detection radius for teeth
		private final int toothRadius = 25;
		// Collision detection radius for spaces
		private final int spaceRadius = 13;

		public DentalPanel(Mouth mouth) {
			this.mouth = mouth;
			this.setBackground(defaultBackground);

			addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent e) {
					handlePanelClick(e.getX(), e.getY());
				}
			});
		}

		private void handlePanelClick(int mouseX, int mouseY) {
			int centerX = getWidth() / 2;
			int centerY = getHeight() / 2;

			// Check Upper Jaw click targets
			if (checkJawClick(mouseX, mouseY, true, centerX, centerY - 40, startAngleUpperJaw, endAngleUpperJaw)) {
				repaint();
				return;
			}

			// Check Lower Jaw click targets
			if (checkJawClick(mouseX, mouseY, false, centerX, centerY + 40, startAngleLowerJaw, endAngleLowerJaw)) {
				repaint();
				return;
			}
		}

		private boolean checkJawClick(int mouseX, int mouseY, boolean upper,
		                              int centerX, int centerY, double startAngle, double endAngle) {

			int start = upper ? 0 : 16;
			double angleStep = (endAngle - startAngle) / 15;

			// 1. Check if the user clicked any Tooth
			for (int i = 0; i < 16; i++) {
				double currentAngle = startAngle + angleStep * (double) i;
				int x = (int) (centerX + radiusX * Math.cos(currentAngle));
				int y = (int) (centerY + radiusY * Math.sin(currentAngle));

				if (getDistance(mouseX, mouseY, x, y) <= toothRadius) {
					Tooth clickedTooth = mouth.getTooth(start + i);
					// Toggle availability state
					clickedTooth.setAvailable(!clickedTooth.isAvailable());
					System.out.println("Toggled Tooth " + clickedTooth.getPosition() + " to available = " + clickedTooth.isAvailable());
					return true;
				}

				if (i < 15 && Objects.nonNull(selectedBrush)) {
					Space clickedSpace;
					if (upper) {
						clickedSpace = this.mouth.getSpace(start + i);
					} else {
						clickedSpace = this.mouth.getSpace(start + i - 1);
					}

					if (clickedSpace.isAvailable()) {
						double midAngle = currentAngle + angleStep / 2;
						int midX = (int) (centerX + radiusX * Math.cos(midAngle));
						int midY = (int) (centerY + radiusY * Math.sin(midAngle));

						//Y of outer triangle center
						double localOuterY = Arrays.stream(outerSpaceY).average().getAsDouble();
						//Y of inner triangle center
						double localInnerY = Arrays.stream(innerSpaceY).average().getAsDouble();


						//Rotated outer x - (midX + dx * cos(θ) - dy * sin(θ))
						int outerX = (int) Math.round(midX + basicSpaceX[0] * Math.cos(midAngle + Math.PI / 2) - localOuterY * Math.sin(midAngle + Math.PI / 2));
						// and y - (midY + dx * sin(θ) + dy * cos(θ))
						int outerY = (int) Math.round(midY + basicSpaceX[0] * Math.sin(midAngle + Math.PI / 2) + localOuterY * Math.cos(midAngle + Math.PI / 2));
						// dx = x - midX; x = localX + midX -> dx = localX + midX - midX -> dx = localX
						// dy = y - midY; y = localY + midY -> dy = localY + midY - midY -> dy = localY

						//Rotated inner x and y
						int innerX = (int) Math.round(midX - localInnerY * Math.sin(midAngle + Math.PI / 2));
						int innerY = (int) Math.round(midY + localInnerY * Math.cos(midAngle + Math.PI / 2));

						//Check if inner space was clicked
						if (getDistance(mouseX, mouseY, outerX, outerY) <= spaceRadius) {
							clickedSpace.setOuterBrush(selectedBrush);
							return true;
						//Check if outer space was clicked
						} else if (getDistance(mouseX, mouseY, innerX, innerY) <= spaceRadius) {
							clickedSpace.setInnerBrush(selectedBrush);
							return true;
						}
					}
				}
			}

			return false;
		}

		private double getDistance(int x1, int y1, int x2, int y2) {
			return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);

			// Enable antialiasing for smooth circles and text
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			int centerX = this.getWidth() / 2;
			int centerY = this.getHeight() / 2;

			// Draw Upper Jaw (Angles from roughly 170 degrees to 10 degrees) and it is wider than lower jaw (radius X and Y smaller)
			this.drawJaw(g2d, true, centerX, centerY - 40, startAngleUpperJaw, endAngleUpperJaw);


			// Draw Lower Jaw (Angles from roughly 190 degrees to 350 degrees)
			this.drawJaw(g2d, false, centerX, centerY + 40, startAngleLowerJaw, endAngleLowerJaw);

			g2d.setFont(new Font("Arial", Font.BOLD, 25));
			g2d.setColor(Color.DARK_GRAY);

			// Define padding and metrics for jaws markers
			FontMetrics fm = g2d.getFontMetrics();
			int padding = 100;

			// Upper Jaw marker definition
			String topText = "Верхняя челюсть";
			int topTextWidth = fm.stringWidth(topText);
			int topTextX = centerX - topTextWidth / 2;
			int topTextY = centerY - radiusY - padding;
			g2d.drawString(topText, topTextX, topTextY);

			// Lower Jaw marker definition
			String bottomText = "Нижняя челюсть";
			int bottomTextWidth = fm.stringWidth(bottomText);
			int bottomTextX = centerX - bottomTextWidth / 2;
			int bottomTextY = centerY + radiusY + padding + fm.getAscent();
			g2d.drawString(bottomText, bottomTextX, bottomTextY);

			// Left side marker
			String leftText = "Лево";
			int leftTextWidth = fm.stringWidth(leftText);
			int leftTextX = centerX - radiusX - leftTextWidth / 2;
			g2d.drawString(leftText, leftTextX, centerY);

			//Right side marker
			String rightText = "Право";
			int rightTextWidth = fm.stringWidth(rightText);
			int rightTextX = centerX + radiusX - rightTextWidth / 2;
			g2d.drawString(rightText, rightTextX, centerY);
		}

		private void drawJaw(Graphics2D g2d, boolean upper, int centerX, int centerY, double startAngle, double endAngle) {

			double angleStep = (endAngle - startAngle) / 15;
			int start = upper ? 0 : 16;

			for(int i = 0; i < 16; ++i) {
				// Retrieve the specific tooth based on our visual map[cite: 2]
				Tooth tooth = this.mouth.getTooth(start + i);

				double currentAngle = startAngle + angleStep * (double) i;

				// Calculate x and y using polar equations for an ellipse
				int x = (int)(centerX + radiusX * Math.cos(currentAngle));
				int y = (int)(centerY + radiusY * Math.sin(currentAngle));

				this.drawTooth(g2d, tooth, x, y, currentAngle);


				if (i < 15) {
					Space space;
					if (upper) {
						space = this.mouth.getSpace(start + i);
					} else {
						space = this.mouth.getSpace(start + i - 1);
					}

					if (Objects.nonNull(selectedBrush) || (Objects.nonNull(space.getOuterBrush()) || Objects.nonNull(space.getInnerBrush()))) {
						double midAngle = currentAngle + angleStep / 2;
						int midX = (int)(centerX + radiusX * Math.cos(midAngle));
						int midY = (int)(centerY + radiusY * Math.sin(midAngle));

						this.drawSpaces(g2d, space, midX, midY, midAngle);
					}
				}
			}
		}

		private void drawTooth(Graphics2D g2d, Tooth tooth, int x, int y, double angle) {

//			System.out.println("Drawing tooth - " + tooth.getPosition() + ";");

			// Save the original transform state
			AffineTransform originalTransform = g2d.getTransform();

			// Translate the origin to our calculated x, y coordinates
			g2d.translate(x, y);

			// Rotate the canvas so the tooth points outward from the center
			// Adding Math.PI / 2 ensures the "top" of the tooth shape points away from the center
			g2d.rotate(angle + (Math.PI / 2));

			// Draw the tooth shape (centered on 0,0 since we translated)
			g2d.setColor(tooth.isAvailable()? basicToothColor : basicToothColor.darker());
			g2d.fillRoundRect(-toothWidth / 2, -toothHeight / 2, toothWidth, toothHeight, 20, 20);

			// Draw the outline
			g2d.setColor(Color.BLACK);
			g2d.setStroke(new BasicStroke(2.5F));
			g2d.drawRoundRect(-toothWidth / 2, -toothHeight / 2, toothWidth, toothHeight, 20, 20);

			// Draw the FDI position text
			String posText = String.valueOf(tooth.getPosition());
			g2d.setFont(new Font("Arial", Font.BOLD, 16));
			FontMetrics fontMetrics = g2d.getFontMetrics();
			int textX = -fontMetrics.stringWidth(posText) / 2;
			int textY = -fontMetrics.getAscent() / 2 - 2;

			g2d.drawString(posText, textX, textY);

			// Restore the original transform so the next tooth draws correctly
			g2d.setTransform(originalTransform);
		}

		private void drawSpaces(Graphics2D g2d, Space space, int x, int y, double angle) {
			// Save the original transform state
			AffineTransform originalTransform = g2d.getTransform();

			// Translate the origin to our calculated x, y coordinates
			g2d.translate(x, y);

			// Rotate the canvas so the tooth points outward from the center
			// Adding Math.PI / 2 ensures the "top" of the tooth shape points away from the center
			g2d.rotate(angle + (Math.PI / 2));

			Function<Brush, Color> getColor = (brush) -> {
				if (space.isAvailable()) {
					if (Objects.nonNull(brush)) {
						return brush.getColor();
					} else  {
						return basicSpaceColor;
					}
				}
				return basicSpaceColor.darker();
			};

//			System.out.println("Drawing spaces '" + space.getPosition() + "' between left tooth - " + space.getLeftTooth().getPosition() + " and right tooth - " + space.getRightTooth().getPosition() + ";");

			Stroke stroke;
			//Set stroke params
			if (space.isAvailable() && Objects.isNull(space.getInnerBrush()) &&  Objects.isNull(space.getOuterBrush())) {
				stroke = new BasicStroke(2.0F, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10.0f, new  float[]{19.0f, 5.0f}, 0.0f);
			} else {
				stroke = new BasicStroke(2.0F);
			}

			// Draw outer space
			if (Objects.isNull(space.getInnerBrush())) {
				drawSpace(g2d, basicSpaceX, outerSpaceY, getColor.apply(space.getOuterBrush()), stroke);
			}

			// Draw inner space
			if (Objects.isNull(space.getOuterBrush())) {
				drawSpace(g2d, basicSpaceX, innerSpaceY, getColor.apply(space.getInnerBrush()), stroke);
			}

			// Draw the FDI position text
//			String posText = String.valueOf(space.getPosition());
//			g2d.setFont(new Font("Arial", Font.BOLD, 16));
//			FontMetrics fontMetrics = g2d.getFontMetrics();
//			int textX = -fontMetrics.stringWidth(posText) / 2;
//			int textY = fontMetrics.getAscent() / 2 - 2;
//			g2d.drawString(posText, textX, textY);

			// Restore the original transform so the next space draws correctly
			g2d.setTransform(originalTransform);
		}

		//TODO refactor to inner method
		private void drawSpace(Graphics2D g2d, int[] xPoints, int[] yPoints, Color color, Stroke stroke) {

			//Draw the shape
			g2d.setColor(color);
			g2d.fillPolygon(xPoints, yPoints, 3);

			// Draw the outline
			g2d.setColor(Color.BLACK);
			g2d.setStroke(stroke);
			g2d.drawPolygon(xPoints, yPoints, 3);
		}

		@Override
		public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
			if (pageIndex > 0) {
				return Printable.NO_SUCH_PAGE;
			}
			int width = this.getWidth();
			int height = this.getHeight();
			BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2dImage = image.createGraphics();

			this.printAll(g2dImage);
			g2dImage.dispose();

			Graphics2D g2d = (Graphics2D) graphics;

			double scaleX = pageFormat.getImageableWidth() / width;
			double scaleY = pageFormat.getImageableHeight() / height;
			double scale = Math.min(scaleX, scaleY);

			g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
			g2d.scale(scale, scale);
			g2d.drawImage(image, 0, 0, null);
//			this.printAll(g2d);

			return Printable.PAGE_EXISTS;
		}
	}
}
