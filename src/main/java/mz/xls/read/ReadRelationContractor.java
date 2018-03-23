package mz.xls.read;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.HashMap;

public class ReadRelationContractor extends HashMap<String, String> {

    public ReadRelationContractor(Workbook workbook, String type) {

        if (workbook.getSheet(type) != null) {
            Sheet sheet = workbook.getSheet(type);

            boolean flag = false;

            int i = 0;
            for (Row row : sheet) {
                if (flag) {
                    addRow(row);
                } else {
                    Cell cell = row.getCell(0);
                    if (cell.getStringCellValue().contains("Primavera-Подрядчик")) {
                        flag = true;
                    }
                }
            }
        }

    }

    private void addRow(Row row) {
        this.put(row.getCell(0).getStringCellValue(), row.getCell(1).getStringCellValue());
    }
}
