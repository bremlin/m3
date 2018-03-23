package mz.xls.read;

import mz.xls.objects.Contractor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.HashMap;

public class ContractorHelper extends HashMap<String, Contractor> {

    public ContractorHelper(Sheet sheet) {
        boolean flag = false;

        for (Row row : sheet) {
            if (flag) {
                Contractor contractor = new Contractor(row);
                this.put(contractor.getCode(), contractor);
            } else {
                String cellNum = row.getCell(0).getStringCellValue();
                if (cellNum.equals("№")) flag = true;
            }
        }
    }

    public ArrayList<String> getContractors() {
        return new ArrayList<>(this.keySet());
    }
}