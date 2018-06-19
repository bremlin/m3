package mz.xls.objects;

import org.apache.poi.ss.usermodel.Cell;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FinPeriod {

    private String monthPeriod;
    private String name;
    private Date startDate;
    private Date finishDate;

    private Date thursday;

    public FinPeriod(Cell cell) {
        this.name = cell.getStringCellValue();

        int tire = name.indexOf("-");

        DateFormat format = new SimpleDateFormat("dd.MM.yyyy");

        try {
            this.finishDate = format.parse(name.substring(tire + 1, name.length()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendarFinish = Calendar.getInstance();
        calendarFinish.setTime(finishDate);

        Calendar canlendarStart = Calendar.getInstance();
        canlendarStart.set(Calendar.HOUR_OF_DAY, 0);
        canlendarStart.set(Calendar.MINUTE, 0);
        canlendarStart.set(Calendar.SECOND, 0);
        canlendarStart.set(Calendar.MILLISECOND, 0);

        Integer numDate = Integer.valueOf(name.substring(tire - 2, tire));

        int year = calendarFinish.get(Calendar.YEAR);
        int month = calendarFinish.get(Calendar.MONTH);
        int day = calendarFinish.get(Calendar.DAY_OF_MONTH);

        if (numDate < day) {
            canlendarStart.set(year, month, numDate);
        } else {
            if (month > 0) {
                canlendarStart.set(year, month - 1, numDate);
            } else {
                canlendarStart.set(year - 1, Calendar.DECEMBER, numDate);
            }
        }
        this.startDate = canlendarStart.getTime();

        //Разнесение финпериодов по месяцам по правилу четверга (месяц считается тем, куда входит четверг)
        int yearStart = canlendarStart.get(Calendar.YEAR);
        int monthStart = canlendarStart.get(Calendar.MONTH);
        int dayStart = canlendarStart.get(Calendar.DAY_OF_MONTH);

        if (dayStart < 25) {
            monthPeriod = (monthStart + 1) + "." + yearStart;
        } else {
            if (canlendarStart.getActualMaximum(Calendar.DAY_OF_MONTH) - dayStart > 2) {
                monthPeriod = (monthStart + 1) + "." + yearStart;
            } else {
                if (monthStart > 10) {
                    monthPeriod = 1 + "." + (yearStart + 1);
                } else {
                    monthPeriod = (monthStart + 2) + "." + yearStart;
                }
            }
        }
    }

    public String getName() {
        return name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public String getMonthPeriod() {
        return monthPeriod;
    }

    public Date getFinishDate() {
        return finishDate;
    }
}
