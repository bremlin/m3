package mz.xls.objects;

import mz.xls.read.ContractorHelper;
import org.apache.poi.ss.usermodel.Row;

import java.util.Date;

public class ChFact {

    private Date date;
    private String project;
    private String peredel;

    private FinPeriod period;

    private Contractor contractor;

    private Integer fact;

    public ChFact(Row row, ContractorHelper contractorHelper) {
        this.date = row.getCell(0).getDateCellValue();
        this.project = row.getCell(1).getStringCellValue();
        if (row.getCell(2) != null) {
            this.peredel = row.getCell(2).getStringCellValue();
        } else {
            this.peredel = "";
        }
        if (contractorHelper.containsKey(row.getCell(3).getStringCellValue().toUpperCase())) {
            this.contractor = contractorHelper.get(row.getCell(3).getStringCellValue().toUpperCase());
        } else {
            System.out.println("Подрядчика нет: " + row.getCell(3).getStringCellValue());
        }
        if (row.getCell(5) != null) {
            this.fact = (int) row.getCell(5).getNumericCellValue();
        } else {
            this.fact = 0;
        }
    }

    public void setPeriod(FinPeriod finPeriod) {
        period = finPeriod;
    }

    public Date getDate() {
        return date;
    }

    public String getProject() {
        return project;
    }

    public String getPeredel() {
        return peredel;
    }

    public Contractor getContractor() {
        return contractor;
    }

    public Integer getFact() {
        return fact;
    }

    public FinPeriod getPeriod() {
        return period;
    }

    public String getKey() {
        StringBuilder sb = new StringBuilder();
        sb.append(project);
        sb.append(peredel);
        return sb.toString();
    }
}
