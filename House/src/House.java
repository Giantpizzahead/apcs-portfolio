/**
 * (Project 1/3: House)
 * 
 * A fun little Swing application that draws a house, along with
 * a couple of bouncing smileys.
 * 
 * @author Kyle Fu
 */

import java.awt.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

public class House extends JFrame {
    final static int WIDTH = 600, HEIGHT = 600;

    public static void main(String[] args) {
        House house = new House();
    }

    public House() {
        setTitle("House");
        setSize(WIDTH, HEIGHT);
        setResizable(true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        MainPanel mainPanel = new MainPanel();
        setContentPane(mainPanel);
        setVisible(true);

        while (isVisible()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            repaint();
        }
    }

    class MainPanel extends JPanel {
        final int NUM_SMILIES = 10;
        final int SMILEY_MIN_SIZE = 25, SMILEY_MAX_SIZE = 75;
        final float SMILEY_MIN_SPEED = 1.0f, SMILEY_MAX_SPEED = 6.0f;
        BufferedImage baseSmileyImg;
        Smiley[] smilies;

        public MainPanel() {
            try {
                baseSmileyImg = ImageIO.read(new File("smiley.png"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            smilies = new Smiley[NUM_SMILIES];
            Random random = new Random();
            for (int i = 0; i < NUM_SMILIES; i++) {
                int size = random.nextInt(SMILEY_MAX_SIZE - SMILEY_MIN_SIZE + 1) + SMILEY_MIN_SIZE;
                float speed = random.nextFloat() * (SMILEY_MAX_SPEED - SMILEY_MIN_SPEED) + SMILEY_MIN_SPEED;
                speed /= Math.pow(2, size / 75f);
                float dir = (float) (random.nextFloat() * 2 * Math.PI);
                float cx = (float) (Math.cos(dir) * speed);
                float cy = (float) (Math.sin(dir) * speed);
                smilies[i] = new Smiley(size,
                        random.nextInt(House.this.getWidth() - size - 45) + 5,
                        random.nextInt(House.this.getHeight() - size - 45) + 5,
                        cx, cy);
            }
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);

            g.setColor(new Color(100, 50, 0));
            g.fillRect(0, 50, 100, 100);
            g.setColor(new Color(200, 100, 0));
            g.fillRect(35, 100, 30, 50);
            g.setColor(Color.BLACK);
            g.fillOval(53, 125, 7, 7);
            g.setColor(Color.RED);
            g.fillPolygon(new int[] {0, 50, 100}, new int[] {50, 0, 50}, 3);

            g.setColor(Color.BLUE);
            g.setFont(new Font("Roboto", Font.BOLD, 20));
            g.drawString("House", 22, 175);

            for (Smiley smiley : smilies) {
                smiley.move();
                smiley.draw(g);
            }
        }

        class Smiley {
            Image smileyImg;
            int size;
            float x, y, cx, cy;

            Smiley(int size, float x, float y, float cx, float cy) {
                smileyImg = baseSmileyImg.getScaledInstance(size, size, Image.SCALE_SMOOTH);
                this.size = size;
                this.x = x;
                this.y = y;
                this.cx = cx;
                this.cy = cy;
            }

            void move() {
                x += cx;
                y += cy;
                // Bounce the image!
                if (x < 0 || x > getWidth() - size) cx *= -1;
                if (y < 0 || y > getHeight() - size) cy *= -1;
            }

            void draw(Graphics g) {
                g.drawImage(smileyImg, (int) x, (int) y, null);
            }
        }
    }
}
