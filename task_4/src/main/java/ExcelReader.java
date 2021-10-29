
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ExcelReader {

    private static final int SRC_ROW = 0;

    private static final int DEST_ROW = 1;

    private static final int SRC_COLUMN = 0;

    private static final int DEST_COLUMN = 1;

    Workbook workbook;

    File workingFile;

    public ExcelReader() throws IOException {
        Path resourceDirectory = Paths.get("src","main","resources");
        workingFile = new File(resourceDirectory + File.separator + "input.xlsx");
        try(FileInputStream fis = new FileInputStream(workingFile)) {
            workbook = WorkbookFactory.create(fis);
        }
    }

    public void processFile() throws IOException {
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            String src = getDataFromCell(i);
            if (src == null) {
                System.out.printf("На листе N %d отсутствует контент в ячейке A1", i);
            } else {
                String dest = camelCaseConverter(src);
                writeDataToCell(i, dest);
            }
        }
    }

    public void close() throws IOException {
       workbook.close();
    }


    private String getDataFromCell(int sheetNumber) throws IOException {
        Sheet sheet =  workbook.getSheetAt(sheetNumber);
        if (sheet.getRow(SRC_ROW) != null && sheet.getRow(SRC_ROW).getCell(SRC_COLUMN) != null) {
            return sheet.getRow(0).getCell(0).getStringCellValue();
        }
        return null;
    }

    private void writeDataToCell(int sheetNumber, String data) throws IOException {
        Sheet sheet =  workbook.getSheetAt(sheetNumber);
        Row row = sheet.createRow(DEST_ROW);
        Cell cell = row.createCell(DEST_COLUMN, CellType.STRING);
        cell.setCellValue(data);
        FileOutputStream os = new FileOutputStream(workingFile);
        workbook.write(os);
        os.close();
    }

    private static String camelCaseConverter(String data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length(); i++) {
            if (data.charAt(i) == '_') {
                continue;
            }
            if (i > 0 && Character.isAlphabetic(data.charAt(i)) && data.charAt(i - 1) == '_') {
                sb.append(Character.toUpperCase(data.charAt(i)));
            } else {
                sb.append(data.charAt(i));
            }
        }
        return sb.toString();
    }
}
