import org.apache.poi.POIDocument;

public class Main {
    public static void main(String[] args) {
        try {
            ExcelReader reader = new ExcelReader();
            reader.processFile();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}