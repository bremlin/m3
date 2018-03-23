package mz.xls.read;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.HashMap;

public class ReadRelationObject extends HashMap<String, ArrayList<String>> {

    public ReadRelationObject(Workbook workbook, String type) {

        if (workbook.getSheet(type) != null) {
            Sheet sheet = workbook.getSheet(type);

            boolean flag = false;

            int i = 0;
            for (Row row : sheet) {
                if (flag) {
                    addRow(row);
                } else {
                    Cell cell = row.getCell(0);
                    if (cell.getStringCellValue().contains("Primavera-Объект")) {
                        flag = true;
                    }
                }
            }
        }

    }

    private void addRow(Row row) {
        String primaString = row.getCell(0).getStringCellValue();
        String excelString = row.getCell(1).getStringCellValue();

        if (this.containsKey(primaString)) {
            this.get(primaString).add(excelString);
        } else {
            ArrayList<String> tempList = new ArrayList<>();
            tempList.add(excelString);
            this.put(primaString, tempList);
        }
    }
}
