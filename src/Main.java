import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    public static void main(String[] args) {
        // Загружаем изображения
        String largeImagePath = "C:\\Users\\ivahn\\IdeaProjects\\tsuab\\src\\img\\big\\big12.jpeg";
        String smallImagePath = "C:\\Users\\ivahn\\IdeaProjects\\tsuab\\src\\img\\small\\small112.jpeg";
        // Проверяем существование файлов
        if (!new java.io.File(largeImagePath).exists() || !new java.io.File(smallImagePath).exists()) {
            System.out.println("Один или оба файла не существуют или недоступны.");
            return;
        }

        // Загружаем изображения
        Mat largeImage = Imgcodecs.imread(largeImagePath);
        Mat smallImage = Imgcodecs.imread(smallImagePath);


        // Применяем фильтр Гаусса к маленькому изображению
        Imgproc.GaussianBlur(smallImage, smallImage, new org.opencv.core.Size(5, 5), 0);

        // Создаем пустую матрицу для результата
        Mat result = new Mat(largeImage.rows() - smallImage.rows() + 1, largeImage.cols() - smallImage.cols() + 1, CvType.CV_32FC1);

        // Ищем совпадения шаблона
        Imgproc.matchTemplate(largeImage, smallImage, result, Imgproc.TM_CCOEFF_NORMED);

        // Находим где находится изображение маленькое
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        Point matchLoc = mmr.maxLoc;

        // Выводим координаты верхнего левого угла рамки в консоль
        System.out.println("Координаты верхнего левого угла рамки: (" + matchLoc.x + ", " + matchLoc.y + ")");

        // Рисуем прямоугольник вокруг найденного изображения
        Imgproc.rectangle(largeImage, matchLoc, new Point(matchLoc.x + smallImage.width(), matchLoc.y + smallImage.height()), new Scalar(0, 255, 0), 2);

        // Выводим результат
        Imgcodecs.imwrite("C:\\Users\\ivahn\\IdeaProjects\\tsuab\\src\\img\\result.jpg", largeImage);
    }
}