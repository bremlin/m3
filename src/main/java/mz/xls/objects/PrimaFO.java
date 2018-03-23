package mz.xls.objects;

import mz.xls.read.FinPeriodHelper;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.util.HashMap;
import java.util.HashSet;

public class PrimaFO {

    private String activityCode;
    private String resourceName;
    private String contractor;
    private String resourceType;
    private String project;

    private HashMap<FinPeriod, Double> FOMap = new HashMap<>();
    private HashMap<FinPeriod, Double> TZMap = new HashMap<>();

    public PrimaFO(Row row, String type, FinPeriodHelper finPeriodHelper,
                   HashSet<String> projectObjectNameSet, HashSet<String> resourceNameSet,
                   HashSet<String> primaContractorNameSet, int count) {
        this.activityCode = getCellValue(row, 1, count);
        this.resourceName = getCellValue(row, 2, count);
        this.contractor = getCellValue(row, 3, count);
        this.resourceType = getCellValue(row, 4, count);
        this.project = getCellValue(row, 0, count);

        projectObjectNameSet.add(project);
        resourceNameSet.add(resourceName);
        primaContractorNameSet.add(contractor);

        for (int i = 5; i < row.getLastCellNum(); i++) {
            if (row.getCell(i) != null) {
                if (type.equals("ФО")) {
                    if (row.getCell(i).getCellTypeEnum() == CellType.NUMERIC) {
                        this.FOMap.put(finPeriodHelper.getColumnMap().get(i), row.getCell(i).getNumericCellValue());
                    } else {
                        this.FOMap.put(finPeriodHelper.getColumnMap().get(i), Double.valueOf(row.getCell(i).getStringCellValue()));
                    }
                } else if (type.equals("ПТВР")) {
                    if (row.getCell(i).getCellTypeEnum() == CellType.NUMERIC) {
                        this.TZMap.put(finPeriodHelper.getColumnMap().get(i), row.getCell(i).getNumericCellValue());
                    } else {
                        this.TZMap.put(finPeriodHelper.getColumnMap().get(i), Double.valueOf(row.getCell(i).getStringCellValue()));
                    }
                }
            }
        }

    }

    private String getCellValue(Row row, Integer i, int count) {
        if (row.getCell(i) != null) {
            if (row.getCell(i).getCellTypeEnum() == CellType.STRING) {
                return row.getCell(i).getStringCellValue();
            } else {
                return "Ошибка в строке " + count;
            }
        } else {
            return "";
        }
    }

    public String getActivityCode() {
        return activityCode;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getContractor() {
        return contractor;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getProject() {
        return project;
    }

    public HashMap<FinPeriod, Double> getFOMap() {
        return FOMap;
    }

    public HashMap<FinPeriod, Double> getTZMap() {
        return TZMap;
    }

    public void setFOMap(HashMap<FinPeriod, Double> FOMap) {
        this.FOMap = FOMap;
    }
}
