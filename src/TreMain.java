import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.swing.*;
import java.awt.*;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class TreMain {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private static Mat largeImage;
    private static Rectangle roiRect;
    private static boolean drawing;
    private static double scaleFactor;
    private static int roiCount = 0;

    public static void main(String[] args) {
        largeImage = Imgcodecs.imread("C:\\Users\\ivahn\\IdeaProjects\\tsuab\\src\\img\\big12.jpeg");

        JFrame frame = new JFrame("Select ROI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (largeImage != null) {
                    BufferedImage image = matToBufferedImage(largeImage);
                    scaleImage(image, this); // Масштабируем изображение
                    g.drawImage(image, 0, 0, (int) (image.getWidth() * scaleFactor), (int) (image.getHeight() * scaleFactor), null);
                    if (roiRect != null) {
                        g.setColor(Color.RED);
                        g.drawRect((int) (roiRect.x * scaleFactor), (int) (roiRect.y * scaleFactor), (int) (roiRect.width * scaleFactor), (int) (roiRect.height * scaleFactor));
                    }
                }
            }
        };

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                Point scaledPoint = scalePoint(e.getPoint(), scaleFactor);
                roiRect = new Rectangle(scaledPoint.x, scaledPoint.y, 0, 0);
                drawing = true;
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (drawing) {
                    Point scaledPoint = scalePoint(e.getPoint(), scaleFactor);
                    roiRect.width = scaledPoint.x - roiRect.x;
                    roiRect.height = scaledPoint.y - roiRect.y;
                    panel.repaint();
                }
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                drawing = false;
                Point scaledPoint = scalePoint(e.getPoint(), scaleFactor);
                roiRect.width = scaledPoint.x - roiRect.x;
                roiRect.height = scaledPoint.y - roiRect.y;

                if (roiRect != null && roiRect.width > 0 && roiRect.height > 0) {
                    Mat roi = largeImage.submat(roiRect.y, roiRect.y + roiRect.height, roiRect.x, roiRect.x + roiRect.width);
                    roiCount++;
                    String roiFileName = "roi" + roiCount + ".jpg";
                    String roiFilePath = "C:\\Users\\ivahn\\IdeaProjects\\tsuab\\src\\img\\roi\\" + roiFileName;
                    Imgcodecs.imwrite(roiFilePath, roi);
                    System.out.println("ROI saved as " + roiFileName);
                }
            }
        });

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                panel.repaint();
            }
        });

        frame.getContentPane().add(panel);
        frame.pack();
        frame.setVisible(true);
    }

    private static Point scalePoint(Point point, double scaleFactor) {
        return new Point((int) (point.x / scaleFactor), (int) (point.y / scaleFactor));
    }

    private static void scaleImage(BufferedImage image, JPanel panel) {
        scaleFactor = Math.min((double) panel.getWidth() / image.getWidth(), (double) panel.getHeight() / image.getHeight());
    }

    private static BufferedImage matToBufferedImage(Mat matrix) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (matrix.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = matrix.channels() * matrix.cols() * matrix.rows();
        byte[] buffer = new byte[bufferSize];
        matrix.get(0, 0, buffer);
        BufferedImage image = new BufferedImage(matrix.cols(), matrix.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
        return image;
    }
}