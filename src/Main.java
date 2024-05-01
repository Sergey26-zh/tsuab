import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Путь к папке с изображениями для сравнения
        String folderPath = "C:\\Users\\ivahn\\IdeaProjects\\tsuab\\src\\img\\comparison_images";

        // Загрузка основного изображения
        Mat mainImage = Imgcodecs.imread("C:\\Users\\ivahn\\IdeaProjects\\tsuab\\src\\img\\yt_1200.jpg");

        // Создание папки для сохранения результатов
        String outputPath = "C:\\Users\\ivahn\\IdeaProjects\\tsuab\\src\\img\\comparison_results";
        File outputFolder = new File(outputPath);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        // Сравнение каждого изображения из папки с основным изображением
        File folder = new File(folderPath);
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    Mat comparisonImage = Imgcodecs.imread(file.getAbsolutePath());

                    // Проверка на корректность загрузки
                    if (mainImage.empty() || comparisonImage.empty()) {
                        System.out.println("Не удалось загрузить изображения");
                        return;
                    }

                    // Преобразование изображений в градации серого
                    Mat grayMainImage = new Mat();
                    Mat grayComparisonImage = new Mat();
                    Imgproc.cvtColor(mainImage, grayMainImage, Imgproc.COLOR_BGR2GRAY);
                    Imgproc.cvtColor(comparisonImage, grayComparisonImage, Imgproc.COLOR_BGR2GRAY);

                    // Вычисление разницы между изображениями
                    Mat difference = new Mat();
                    Core.absdiff(grayMainImage, grayComparisonImage, difference);

                    // Сохранение разностного изображения
                    String outputFileName = outputPath + "\\" + file.getName();
                    Imgcodecs.imwrite(outputFileName, difference);
                }
            }
        }

        System.out.println("Сравнение завершено");
    }
}
