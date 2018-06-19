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

    private String object;
    private String stage;

    //плановые
    private HashMap<FinPeriod, Double> FOMap = new HashMap<>();
    private HashMap<FinPeriod, Double> TZMap = new HashMap<>();

    //оставшееся
    private HashMap<FinPeriod, Double> FOOMap = new HashMap<>();
    private HashMap<FinPeriod, Double> TZOMap = new HashMap<>();

    //плановые по месяцам
    private HashMap<String, Double> FOMapMonth = new HashMap<>();
    private HashMap<String, Double> TZMapMonth = new HashMap<>();

    //оставшееся по месяцам
    private HashMap<String, Double> FOOMapMonth = new HashMap<>();
    private HashMap<String, Double> TZOMapMonth = new HashMap<>();

    private static int PROJECT                  = 0;
    private static int ACTIVITY_CODE            = 1;
    private static int RESOURCE_NAME            = 2;
    private static int OBJECT                   = 3;
    private static int STAGE                    = 4;
    private static int CONTRACTOR               = 5;
    private static int RESOURCE_TYPE_STANDART   = 6;
    private static int RESOURCE_TYPE_M3         = 7;

    private static int LAST                     = 8;


    public PrimaFO(Row row, String type, FinPeriodHelper finPeriodHelper,
                   HashSet<String> projectObjectNameSet, HashSet<String> resourceNameSet,
                   HashSet<String> primaContractorNameSet, int count, boolean pvStandart) {
        int resourceType = RESOURCE_TYPE_M3;
        if (pvStandart) resourceType = RESOURCE_TYPE_STANDART;
        this.activityCode = getCellValue(row, ACTIVITY_CODE, count);
        this.resourceName = getCellValue(row, RESOURCE_NAME, count);
        this.contractor = getCellValue(row, CONTRACTOR, count);
        this.resourceType = getCellValue(row, resourceType, count);
        this.project = getCellValue(row, PROJECT, count);

        this.object = getCellValue(row, OBJECT, count);
        this.stage = getCellValue(row, STAGE, count);

        projectObjectNameSet.add(project);
        resourceNameSet.add(resourceName);
        primaContractorNameSet.add(contractor);

        for (int i = LAST; i < row.getLastCellNum(); i++) {
            String finName = finPeriodHelper.getColumnMap().get(i).getMonthPeriod();
            if (row.getCell(i) != null) {
                if (type.equals("ФО")) {
                    if (row.getCell(i).getCellTypeEnum() == CellType.NUMERIC) {
                        this.FOMap.put(finPeriodHelper.getColumnMap().get(i), row.getCell(i).getNumericCellValue());
                        addToFOMapMonth(finName, row.getCell(i).getNumericCellValue(), type);
                    } else {
                        this.FOMap.put(finPeriodHelper.getColumnMap().get(i), Double.valueOf(row.getCell(i).getStringCellValue()));
                        addToFOMapMonth(finName, Double.valueOf(row.getCell(i).getStringCellValue()), type);
                    }
                } else if (type.equals("ПТВР")) {
                    if (row.getCell(i).getCellTypeEnum() == CellType.NUMERIC) {
                        this.TZMap.put(finPeriodHelper.getColumnMap().get(i), row.getCell(i).getNumericCellValue());
                        addToFOMapMonth(finName, row.getCell(i).getNumericCellValue(), type);
                    } else {
                        this.TZMap.put(finPeriodHelper.getColumnMap().get(i), Double.valueOf(row.getCell(i).getStringCellValue()));
                        addToFOMapMonth(finName, Double.valueOf(row.getCell(i).getStringCellValue()), type);
                    }
                } else if (type.equals("ПТОР")) {
                    if (row.getCell(i).getCellTypeEnum() == CellType.NUMERIC) {
                        this.TZOMap.put(finPeriodHelper.getColumnMap().get(i), row.getCell(i).getNumericCellValue());
                        addToFOMapMonth(finName, row.getCell(i).getNumericCellValue(), type);
                    } else {
                        this.TZOMap.put(finPeriodHelper.getColumnMap().get(i), Double.valueOf(row.getCell(i).getStringCellValue()));
                        addToFOMapMonth(finName, Double.valueOf(row.getCell(i).getStringCellValue()), type);
                    }
                } else if (type.equals("ФОО")) {
                    if (row.getCell(i).getCellTypeEnum() == CellType.NUMERIC) {
                        this.FOOMap.put(finPeriodHelper.getColumnMap().get(i), row.getCell(i).getNumericCellValue());
                        addToFOMapMonth(finName, row.getCell(i).getNumericCellValue(), type);
                    } else {
                        this.FOOMap.put(finPeriodHelper.getColumnMap().get(i), Double.valueOf(row.getCell(i).getStringCellValue()));
                        addToFOMapMonth(finName, Double.valueOf(row.getCell(i).getStringCellValue()), type);
                    }
                }
            }
        }

    }

    private void addToFOMapMonth(String finName, Double value, String type) {
        if (type.equals("ФО")) {
            if (FOMapMonth.containsKey(finName)) {
                Double newValue = FOMapMonth.get(finName) + value;
                FOMapMonth.put(finName, newValue);
            } else {
                FOMapMonth.put(finName, value);
            }
        } else if (type.equals("ПТВР")) {
            if (TZMapMonth.containsKey(finName)) {
                Double newValue = TZMapMonth.get(finName) + value;
                TZMapMonth.put(finName, newValue);
            } else {
                TZMapMonth.put(finName, value);
            }
        } else if (type.equals("ПТОР")) {
            if (TZOMapMonth.containsKey(finName)) {
                Double newValue = TZOMapMonth.get(finName) + value;
                TZOMapMonth.put(finName, newValue);
            } else {
                TZOMapMonth.put(finName, value);
            }
        } else if (type.equals("ФОО")) {
            if (FOOMapMonth.containsKey(finName)) {
                Double newValue = FOOMapMonth.get(finName) + value;
                FOOMapMonth.put(finName, newValue);
            } else {
                FOOMapMonth.put(finName, value);
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

    public String getKey() {
        return object + "-" + stage;
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getProject() {
        return project;
    }

    public String getObject() {
        return object;
    }

    public String getStage() {
        return stage;
    }

    public HashMap<FinPeriod, Double> getFOMap() {
        return FOMap;
    }

    public HashMap<FinPeriod, Double> getTZMap() {
        return TZMap;
    }

    public HashMap<String, Double> getFOMapMonth() {
        return FOMapMonth;
    }

    public HashMap<String, Double> getTZMapMonth() {
        return TZMapMonth;
    }

    public HashMap<FinPeriod, Double> getFOOMap() {
        return FOOMap;
    }

    public HashMap<FinPeriod, Double> getTZOMap() {
        return TZOMap;
    }

    public HashMap<String, Double> getFOOMapMonth() {
        return FOOMapMonth;
    }

    public HashMap<String, Double> getTZOMapMonth() {
        return TZOMapMonth;
    }

    public void setFOMap(HashMap<FinPeriod, Double> FOMap) {
        this.FOMap = FOMap;
    }

    public void setFOMapMonth(HashMap<String, Double> FOMapMonth) {
        this.FOMapMonth = FOMapMonth;
    }
}
