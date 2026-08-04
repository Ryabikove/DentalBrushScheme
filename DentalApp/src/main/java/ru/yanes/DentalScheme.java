package ru.yanes;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Objects;
import java.util.function.Function;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import ru.yanes.data.Brush;
import ru.yanes.data.Mouth;
import ru.yanes.data.Space;
import ru.yanes.data.Tooth;

public class DentalScheme extends JFrame {
	private Brush selectedBrush;
	private final int[] windowMinSize = new int[]{1200, 1200};
	private final int[] brushPanelMinSize = new int[]{200, 0};

	public DentalScheme() {
		Mouth mouth = new Mouth();

		this.setTitle("\ud83e\uddb7 Дентальная схема");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setMinimumSize(new Dimension(windowMinSize[0], windowMinSize[1]));


		this.add(new BrushPanel(), BorderLayout.EAST);
		this.add(new DentalPanel(mouth), BorderLayout.CENTER);

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

	class BrushPanel extends JPanel {
		private final int brushRadius = 70;
		private Brush selectedBrush;

		BrushPanel() {
			this.setBackground(Color.LIGHT_GRAY);
			this.setPreferredSize(new Dimension(brushPanelMinSize[0], brushPanelMinSize[1]));

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
					g2d.setColor(Color.WHITE);
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
		}


	}

	class DentalPanel extends JPanel {
		private final Mouth mouth;

		// Define the ellipse boundaries for the arch
		private final int radiusX = 350;
		private final int radiusY = 430;

		// Define tooth width and height
		private final int toothWidth = 45;
		private final int toothHeight = 55;
		private final Color basicToothColor = Color.WHITE;

		// Define space width and height
		private final int spaceWidth = 15;
		private final int spaceHeight = 45;
		private final Color basicSpaceColor = Color.LIGHT_GRAY;

		// Collision detection radius for teeth
		private final int toothRadius = 25;
		// Collision detection radius for spaces
		private final int spaceRadius = 20;

		public DentalPanel(Mouth mouth) {
			this.mouth = mouth;
			this.setBackground(Color.LIGHT_GRAY);

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
			if (checkJawClick(upperJawIndices, mouseX, mouseY, centerX, centerY + 40, Math.PI * 1.05, Math.PI * 1.95)) {
				repaint();
				return;
			}

			// Check Lower Jaw click targets
			if (checkJawClick(lowerJawIndices, mouseX, mouseY, centerX, centerY - 40, Math.PI * 0.95, Math.PI * 0.05)) {
				repaint();
				return;
			}
		}

		private boolean checkJawClick(int[] jawIndices, int mouseX, int mouseY,
		                              int centerX, int centerY, double startAngle, double endAngle) {
			int toothCount = jawIndices.length;
			double angleStep = (endAngle - startAngle) / (toothCount - 1);

			// 1. Check if the user clicked any Tooth
			for (int i = 0; i < toothCount; i++) {
				double currentAngle = startAngle + (i * angleStep);
				int x = (int) (centerX + radiusX * Math.cos(currentAngle));
				int y = (int) (centerY - radiusY * Math.sin(currentAngle));

				if (getDistance(mouseX, mouseY, x, y) <= toothRadius) {
					Tooth clickedTooth = mouth.getTooth(jawIndices[i]);
					// Toggle availability state
					clickedTooth.setAvailable(!clickedTooth.isAvailable());
					System.out.println("Toggled Tooth " + clickedTooth.getPosition() + " to available = " + clickedTooth.isAvailable());
					return true;
				}
			}
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
			this.drawJaw(g2d, this.upperJawIndices, centerX, centerY + 40, Math.PI * 0.95, Math.PI * 0.05);

			// Draw Lower Jaw (Angles from roughly 190 degrees to 350 degrees)
			this.drawJaw(g2d, this.lowerJawIndices, centerX, centerY - 40, Math.PI * 1.05, Math.PI * 1.95);

			g2d.setFont(new Font("Arial", Font.BOLD, 28));
			g2d.setColor(Color.DARK_GRAY);

			// Define padding and metrics for jaws markers
			FontMetrics fm = g2d.getFontMetrics();
			int padding = 50;

			// Upper Jaw marker definition
			String topText = "Верхняя челюсть";
			int topTextWidth = fm.stringWidth(topText);
			int topTextX = centerX - topTextWidth / 2;
			int topTextY = centerY - 50 - radiusY - padding;
			g2d.drawString(topText, topTextX, topTextY);

			// Lower Jaw marker definition
			String bottomText = "Нижняя челюсть";
			int bottomTextWidth = fm.stringWidth(bottomText);
			int bottomTextX = centerX - bottomTextWidth / 2;
			int bottomTextY = centerY + 50 + radiusY + padding + fm.getAscent();
			g2d.drawString(bottomText, bottomTextX, bottomTextY);
		}

		private void drawJaw(Graphics2D g2d, int[] jawIndices, int centerX, int centerY, double startAngle, double endAngle) {

			int toothCount = jawIndices.length;
			double angleStep = (endAngle - startAngle) / (toothCount - 1);

			for(int i = 0; i < toothCount; ++i) {
				// Retrieve the specific tooth based on our visual map[cite: 2]
				Tooth tooth = this.mouth.getTooth(jawIndices[i]);

				double currentAngle = startAngle + angleStep * (double)i;

				// Calculate x and y using polar equations for an ellipse
				int x = (int)(centerX + radiusX * Math.cos(currentAngle));
				int y = (int)(centerY + radiusY * Math.sin(currentAngle));

				this.drawTooth(g2d, tooth, x, y, currentAngle);


				if (i != 0) {
					Space space = this.mouth.getSpace(i - 1);

					double midAngle = currentAngle - angleStep / 2;
					int midX = (int)(centerX + radiusX * Math.cos(midAngle));
					int midY = (int)(centerY + radiusY * Math.sin(midAngle));

					this.drawSpaces(g2d, space, midX, midY, midAngle);
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

			Function<Brush, Color> getColor = brush -> {
				if (Objects.nonNull(brush)) {
					return brush.getColor();
				} else  {
					return basicSpaceColor;
				}
			};

//			System.out.println("Drawing spaces '" + space.getPosition() + "' between left tooth - " + space.getLeftTooth().getPosition() + " and right tooth - " + space.getRightTooth().getPosition() + ";");

			// Draw outer space
			drawSpace(g2d, new int[]{0, spaceWidth, -spaceWidth}, new int[]{-toothHeight / 3, -spaceHeight, -spaceHeight},
					space.isAvailable()? getColor.apply(space.getOuterBrush()) : basicSpaceColor.darker());

			// Draw inner space
			drawSpace(g2d, new int[]{0, spaceWidth, -spaceWidth}, new int[]{toothHeight / 3, spaceHeight, spaceHeight},
					space.isAvailable()? getColor.apply(space.getInnerBrush()) : basicSpaceColor.darker());

			// Draw the FDI position text
//			String posText = String.valueOf(space.getPosition());
//			g2d.setFont(new Font("Arial", Font.BOLD, 16));
//			FontMetrics fontMetrics = g2d.getFontMetrics();
//			int textX = -fontMetrics.stringWidth(posText) / 2;
//			int textY = fontMetrics.getAscent() / 2 - 2;
//			g2d.drawString(posText, textX, textY);

			// Restore the original transform so the next tooth draws correctly
			g2d.setTransform(originalTransform);
		}

		//TODO refactor to inner method
		private void drawSpace(Graphics2D g2d, int[] xPoints, int[] yPoints, Color color) {

			//Draw the shape
			g2d.setColor(color);
			g2d.fillPolygon(xPoints, yPoints, 3);

			// Draw the outline
			g2d.setColor(Color.BLACK);
			g2d.setStroke(new BasicStroke(2.5F));
			g2d.drawPolygon(xPoints, yPoints, 3);
		}

	}
}
