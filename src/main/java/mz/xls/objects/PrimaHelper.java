package mz.xls.objects;

import mz.xls.read.FinPeriodHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class PrimaHelper extends HashMap<String, HashMap<String, ArrayList<PrimaFO>>> {    //Project, FinPeriod, PrimaFO

    private FinPeriodHelper finPeriodHelper;

    private HashMap<String, PrimaFO> primaFoMap = new HashMap<>();

    private HashSet<String> projectObjectName = new HashSet<>();
    private HashSet<String> resourceName = new HashSet<>();
    private HashSet<String> primaContractorName = new HashSet<>();

    private HashMap<String, HashSet<String>> primaRelationMap = new HashMap<>();

    public PrimaHelper(Workbook workbook, boolean pvStandart) {

        Sheet sheet = workbook.getSheet("ПТВР");

        boolean flag = false;

        int i = 0;
        for (Row row : sheet) {
            if (flag) {
                PrimaFO primaFO = new PrimaFO(row, sheet.getSheetName(), getFinPeriodHelper(), projectObjectName,
                        resourceName, primaContractorName, i, pvStandart);
                fillRelationsMap(primaFO);
                String key = primaFO.getKey();
                primaFoMap.put(primaFO.getKey() + "|" + primaFO.getActivityCode(), primaFO);

                if (this.containsKey(key)) {
                    if (this.get(key).containsKey(primaFO.getContractor())) {
                        this.get(key).get(primaFO.getContractor()).add(primaFO);
                    } else {
                        ArrayList<PrimaFO> tempList = new ArrayList<>();
                        tempList.add(primaFO);

                        this.get(key).put(primaFO.getContractor(), tempList);
                    }
                } else {
                    HashMap<String, ArrayList<PrimaFO>> tempFinPeriodMap = new HashMap<>();
                    ArrayList<PrimaFO> tempList = new ArrayList<>();
                    tempList.add(primaFO);

                    tempFinPeriodMap.put(primaFO.getContractor(), tempList);
                    this.put(primaFO.getKey(), tempFinPeriodMap);
                }
            } else {
                finPeriodHelper = new FinPeriodHelper(row);
                flag = true;
            }
        }

        readFO(workbook, "ФО", pvStandart);
//        readFO(workbook, "ФОО");
//        readFO(workbook, "ПТОР");

    }

    private void readFO(Workbook workbook, String type, boolean pvStandart) {
        Sheet sheetFO = workbook.getSheet(type);
        boolean flag = false;

        int i = 0;
        for (Row row : sheetFO) {
            if (flag) {
                PrimaFO primaFO = new PrimaFO(row, sheetFO.getSheetName(), getFinPeriodHelper(), projectObjectName,
                        resourceName, primaContractorName, i, pvStandart);
                fillRelationsMap(primaFO);
                String key = primaFO.getKey();

                if (primaFoMap.containsKey(primaFO.getKey() + "|" + primaFO.getActivityCode())) {
                    primaFoMap.get(primaFO.getKey() + "|" + primaFO.getActivityCode()).setFOMap(primaFO.getFOMap());
                    primaFoMap.get(primaFO.getKey() + "|" + primaFO.getActivityCode()).setFOMapMonth(primaFO.getFOMapMonth());
                } else {
                    if (this.containsKey(key)) {
                        if (this.get(key).containsKey(primaFO.getContractor())) {
                            this.get(key).get(primaFO.getContractor()).add(primaFO);
                        } else {
                            ArrayList<PrimaFO> tempList = new ArrayList<>();
                            tempList.add(primaFO);

                            this.get(key).put(primaFO.getContractor(), tempList);
                        }
                    } else {
                        HashMap<String, ArrayList<PrimaFO>> tempFinPeriodMap = new HashMap<>();
                        ArrayList<PrimaFO> tempList = new ArrayList<>();
                        tempList.add(primaFO);

                        tempFinPeriodMap.put(primaFO.getContractor(), tempList);
                        this.put(primaFO.getKey(), tempFinPeriodMap);
                    }
                }
            } else {
                flag = true;
            }
        }
    }

    private void fillRelationsMap(PrimaFO primaFO) {
        if (primaRelationMap.containsKey(primaFO.getObject())) {
            primaRelationMap.get(primaFO.getObject()).add(primaFO.getStage());
        } else {
            HashSet<String> tempList = new HashSet<>();
            tempList.add(primaFO.getStage());
            primaRelationMap.put(primaFO.getObject(), tempList);
        }
    }

    public FinPeriodHelper getFinPeriodHelper() {
        return finPeriodHelper;
    }

    public ArrayList<String> getProjectObjectName() {
        return new ArrayList<>(projectObjectName);
    }

    public HashSet<String> getResourceName() {
        return resourceName;
    }

    public HashMap<String, HashSet<String>> getPrimaRelationMap() {
        return primaRelationMap;
    }

    public ArrayList<String> getProjectsList() {
        return new ArrayList<>(primaRelationMap.keySet());
    }

    public ArrayList<String> getPrimaObjectList(String projectName) {
        return new ArrayList<>(primaRelationMap.get(projectName));
    }

    public ArrayList<String> getPrimaContractorName() {
        return new ArrayList<>(primaContractorName);
    }
}
