package ru.yanes;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import ru.yanes.data.Mouth;
import ru.yanes.data.Space;
import ru.yanes.data.Tooth;

public class DentalScheme extends JFrame {
	public DentalScheme() {
		Mouth mouth = new Mouth(32);

		this.setTitle("\ud83e\uddb7 Дентальная схема");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.add(new DentalPanel(mouth));
		this.pack();
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

	class DentalPanel extends JPanel {
		private final Mouth mouth;

		// Mapping the internal array indices of Mouth.java to the visual screen order.
		// Screen Left -> Right (Upper Jaw): 18 to 11, then 21 to 28
		private final int[] upperJawIndices = new int[]{7, 6, 5, 4, 3, 2, 1, 0, 8, 9, 10, 11, 12, 13, 14, 15};
		// Screen Left -> Right (Lower Jaw): 48 to 41, then 31 to 38
		private final int[] lowerJawIndices = new int[]{31, 30, 29, 28, 27, 26, 25, 24, 16, 17, 18, 19, 20, 21, 22, 23};
		// Define the ellipse boundaries for the arch
		private final int radiusX = 350;
		private final int radiusY = 430;
		private final int toothRadius = 25;  // Collision detection radius for teeth
		private final int spaceRadius = 20;  // Collision detection radius for spaces

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
					Tooth clickedTooth = mouth.getTooth(jawIndices[i]); //[cite: 2]
					// Toggle availability state
					clickedTooth.setAvailable(!clickedTooth.isAvailable()); //[cite: 4]
					System.out.println("Toggled Tooth " + clickedTooth.getPosition() + " to available = " + clickedTooth.isAvailable()); //[cite: 4]
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

			// Enable antialiasing for smooth circles and text
			Graphics2D g2d = (Graphics2D)g;
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

					this.drawSpace(g2d, space, midX, midY, midAngle);
				}
			}
		}

		private void drawTooth(Graphics2D g2d, Tooth tooth, int x, int y, double angle) {
			int width = 45;
			int height = 55;

			// Save the original transform state
			AffineTransform originalTransform = g2d.getTransform();

			// Translate the origin to our calculated x, y coordinates
			g2d.translate(x, y);

			// Rotate the canvas so the tooth points outward from the center
			// Adding Math.PI / 2 ensures the "top" of the tooth shape points away from the center
			g2d.rotate(angle + (Math.PI / 2));

			// Draw the tooth shape (centered on 0,0 since we translated)
			g2d.setColor(tooth.isAvailable()? Color.WHITE : Color.LIGHT_GRAY);
			g2d.fillRoundRect(-width / 2, -height / 2, width, height, 20, 20);

			// Draw the outline
			g2d.setColor(Color.BLACK);
			g2d.setStroke(new BasicStroke(2.5F));
			g2d.drawRoundRect(-width / 2, -height / 2, width, height, 20, 20);

			// Draw the FDI position text[cite: 4]
			String posText = String.valueOf(tooth.getPosition());
			g2d.setFont(new Font("Arial", Font.BOLD, 16));
			FontMetrics fontMetrics = g2d.getFontMetrics();
			int textX = -fontMetrics.stringWidth(posText) / 2;
			int textY = -fontMetrics.getAscent() / 2 - 2;

			g2d.drawString(posText, textX, textY);

			// Restore the original transform so the next tooth draws correctly
			g2d.setTransform(originalTransform);
		}

		private void drawSpace(Graphics2D g2d, Space space, int x, int y, double angle) {
			// Save the original transform state
			AffineTransform originalTransform = g2d.getTransform();

			// Translate the origin to our calculated x, y coordinates
			g2d.translate(x, y);

			// Rotate the canvas so the tooth points outward from the center
			// Adding Math.PI / 2 ensures the "top" of the tooth shape points away from the center
			g2d.rotate(angle + (Math.PI / 2));

			// Draw the space shape
			g2d.setColor(space.isAvailable()? Color.DARK_GRAY : Color.BLACK);
			g2d.drawLine(0, -25, 0, 25);

			// Restore the original transform so the next tooth draws correctly
			g2d.setTransform(originalTransform);
		}


	}
}
