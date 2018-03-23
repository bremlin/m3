package mz.xls.write;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class WriteRelationContractor {

    private File file;
    private XSSFWorkbook workbook;

    public WriteRelationContractor(File file, XSSFWorkbook workbook) {
        this.file = file;
        this.workbook = workbook;
    }

    public void write(HashMap<String, String> relations, String type) throws IOException {
        XSSFSheet sheet = writeHead(type);
        int rowCount = 1;

        for (Map.Entry<String, String> entry : relations.entrySet()) {
            XSSFRow row = sheet.createRow(rowCount++);
            int cellCount = 0;

            Cell cellPrimaContractor = row.createCell(cellCount++);
            cellPrimaContractor.setCellValue(entry.getKey());

            Cell cellXlsContractor = row.createCell(cellCount);
            cellXlsContractor.setCellValue(entry.getValue());
        }

        workbook.write(new FileOutputStream(file));
    }

    public void writeObject(HashMap<String, ArrayList<String>> relations, String type) throws IOException {

        XSSFSheet sheet = writeHead(type);
        int rowCount = 1;

        for (Map.Entry<String, ArrayList<String>> entry : relations.entrySet()) {
            for (String val : entry.getValue()) {
                XSSFRow row = sheet.createRow(rowCount++);
                int cellCount = 0;

                Cell cellPrimaContractor = row.createCell(cellCount++);
                cellPrimaContractor.setCellValue(entry.getKey());

                Cell cellXlsContractor = row.createCell(cellCount);
                cellXlsContractor.setCellValue(val);
            }
        }

        workbook.write(new FileOutputStream(file));
    }

    private XSSFSheet writeHead(String type) {
        if (workbook.getSheet(type) != null) {
            int i = workbook.getSheetIndex(type);
            workbook.removeSheetAt(i);
        }
        XSSFSheet sheet = workbook.createSheet(type);

        XSSFRow rowName = sheet.createRow(0);

        int cellCountName = 0;
        Cell contractorPrimaText = rowName.createCell(cellCountName++);
        if (type.equals("Связи-Подрядчики")) {
            contractorPrimaText.setCellValue("Primavera-Подрядчик");
        } else if (type.equals("Связи-Объект")) {
            contractorPrimaText.setCellValue("Primavera-Объект");
        }

        Cell contractorXlsText = rowName.createCell(cellCountName);
        if (type.equals("Связи-Подрядчики")) {
            contractorXlsText.setCellValue("Excel-Подрядчик");
        } else if (type.equals("Связи-Объект")) {
            contractorXlsText.setCellValue("Excel-Объект");
        }
        return sheet;
    }
}
