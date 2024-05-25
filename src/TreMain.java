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

    private static Mat largeImage; // Матрица для хранения исходного изображения
    private static Rectangle roiRect; // Прямоугольник для выделения области интереса (ROI)
    private static boolean drawing; // Флаг, указывающий, выделяется ли ROI в данный момент
    private static double scaleFactor; // Коэффициент масштабирования изображения
    private static int roiCount = 0; // Счетчик ROI

    // Точка входа в программу
    public static void main(String[] args) {
        // Загрузка изображения из файла
        largeImage = Imgcodecs.imread("C:\\Users\\ivahn\\IdeaProjects\\tsuab\\src\\img\\big12.jpeg");

        // Создание окна приложения
        JFrame frame = new JFrame("Select ROI");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Создание панели для рисования изображения
        JPanel panel = new JPanel() {
            // Переопределение метода для отрисовки компонента
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (largeImage != null) {
                    // Конвертация Mat в BufferedImage
                    BufferedImage image = matToBufferedImage(largeImage);
                    // Масштабирование изображения
                    scaleImage(image, this);
                    // Отрисовка изображения
                    g.drawImage(image, 0, 0, (int) (image.getWidth() * scaleFactor), (int) (image.getHeight() * scaleFactor), null);
                    // Отрисовка прямоугольника ROI
                    if (roiRect != null) {
                        g.setColor(Color.RED);
                        g.drawRect((int) (roiRect.x * scaleFactor), (int) (roiRect.y * scaleFactor), (int) (roiRect.width * scaleFactor), (int) (roiRect.height * scaleFactor));
                    }
                }
            }
        };

        // Добавление слушателя событий мыши для обработки нажатия кнопки мыши
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // Установка начальных координат и размеров прямоугольника ROI
                Point scaledPoint = scalePoint(e.getPoint(), scaleFactor);
                roiRect = new Rectangle(scaledPoint.x, scaledPoint.y, 0, 0);
                drawing = true;
            }
        });

        // Добавление слушателя событий мыши для обработки перетаскивания мыши
        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // Изменение размеров прямоугольника ROI во время рисования
                if (drawing) {
                    Point scaledPoint = scalePoint(e.getPoint(), scaleFactor);
                    roiRect.width = scaledPoint.x - roiRect.x;
                    roiRect.height = scaledPoint.y - roiRect.y;
                    panel.repaint();
                }
            }
        });

        // Добавление слушателя событий мыши для обработки отпускания кнопки мыши
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                // Завершение рисования прямоугольника ROI
                drawing = false;
                Point scaledPoint = scalePoint(e.getPoint(), scaleFactor);
                roiRect.width = scaledPoint.x - roiRect.x;
                roiRect.height = scaledPoint.y - roiRect.y;

                // Проверка на валидность прямоугольника ROI и его сохранение
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

        // Добавление слушателя событий компонента для обработки изменения размера окна
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                panel.repaint();
            }
        });

        // Добавление панели на окно и упаковка окна
        frame.getContentPane().add(panel);
        frame.pack();
        // Отображение окна
        frame.setVisible(true);
    }

    // Метод для масштабирования точки
    private static Point scalePoint(Point point, double scaleFactor) {
        return new Point((int) (point.x / scaleFactor), (int) (point.y / scaleFactor));
    }

    // Метод для масштабирования изображения
    private static void scaleImage(BufferedImage image, JPanel panel) {
        scaleFactor = Math.min((double) panel.getWidth() / image.getWidth(), (double) panel.getHeight() / image.getHeight());
    }
    private static BufferedImage matToBufferedImage(Mat matrix) {
        // Определение типа буферизованного изображения на основе количества каналов в матрице
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if (matrix.channels() > 1) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        // Вычисление размера буфера
        int bufferSize = matrix.channels() * matrix.cols() * matrix.rows();
        byte[] buffer = new byte[bufferSize];
        // Получение данных из матрицы в буфер
        matrix.get(0, 0, buffer);
        // Создание буферизованного изображения
        BufferedImage image = new BufferedImage(matrix.cols(), matrix.rows(), type);
        // Получение пикселей буферизованного изображения
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        // Копирование данных из буфера в пиксели буферизованного изображения
        System.arraycopy(buffer, 0, targetPixels, 0, buffer.length);
        return image;
    }
}