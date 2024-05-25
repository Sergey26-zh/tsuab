import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImageDifference {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        // Загружаем изображения
        Mat image1 = Imgcodecs.imread("C:\\Users\\ivahn\\IdeaProjects\\tsuab\\src\\img\\1.jpeg");
        Mat image2 = Imgcodecs.imread("C:\\Users\\ivahn\\IdeaProjects\\tsuab\\src\\img\\2.jpeg");

        // Проверяем, что изображения успешно загружены
        if (image1.empty() || image2.empty()) {
            System.out.println("Не удалось загрузить изображения");
            return;
        }

        // Сглаживаем изображения
        Mat blurredImage1 = new Mat();
        Mat blurredImage2 = new Mat();
        Imgproc.GaussianBlur(image1, blurredImage1, new Size(0, 0), 3);
        Imgproc.GaussianBlur(image2, blurredImage2, new Size(0, 0), 3);

        // Вычитаем изображения
        Mat difference = new Mat();
        Core.absdiff(blurredImage1, blurredImage2, difference);

        // Сохраняем результат
        Imgcodecs.imwrite("C:\\Users\\ivahn\\IdeaProjects\\tsuab\\src\\img\\result.jpg", difference);
        System.out.println("Разница сохранена в файле result.jpg");
    }
}
