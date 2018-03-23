package mz.xls.objects;

import org.apache.poi.ss.usermodel.Row;

public class Contractor {

    private int id;

    private String code;
    private String name;

    private double wormWH;
    private double coldWH;

    public Contractor(Row row) {
        this.id = (int) row.getCell(0).getNumericCellValue();
        this.code = row.getCell(1).getStringCellValue().toUpperCase();
        this.name = row.getCell(4).getStringCellValue();
        this.wormWH = row.getCell(2).getNumericCellValue();
        this.coldWH = row.getCell(3).getNumericCellValue();
    }

    public int getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        if (name != null) {
            return name;
        } else {
            return "";
        }
    }

    public double getWormWH() {
        return wormWH;
    }

    public double getColdWH() {
        return coldWH;
    }
}
