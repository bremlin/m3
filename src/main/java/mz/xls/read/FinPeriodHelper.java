package mz.xls.read;

import mz.xls.objects.FinPeriod;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.ArrayList;
import java.util.HashMap;

public class FinPeriodHelper extends HashMap<Long, FinPeriod> {

    private ArrayList<Long> startTimeList = new ArrayList<>();
    private HashMap<Integer, FinPeriod> columnMap = new HashMap<>();

    public FinPeriodHelper(Row row) {

        for (Cell cell : row) {
            String cellString = cell.getStringCellValue();
            if (cellString.contains("нед")) {
                FinPeriod finPeriod = new FinPeriod(cell);
                this.put(finPeriod.getStartDate().getTime(), finPeriod);
                startTimeList.add(finPeriod.getStartDate().getTime());
                columnMap.put(cell.getColumnIndex(), finPeriod);
            }
        }
    }

    public ArrayList<Long> getStartTimeList() {
        return startTimeList;
    }

    public HashMap<Integer, FinPeriod> getColumnMap() {
        return columnMap;
    }
}
