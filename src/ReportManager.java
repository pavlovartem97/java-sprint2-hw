import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
public class ReportManager {
    // Задача решена в полной постановке, когда могут быть заданны отчеты за разные годы, за разные месяцы с учетом пропусков
    // Этим обусловлен выбор следующих структур данных

    // Ключ - год, потом месяц, потом отчет за месяц
    HashMap<Integer, HashMap<Integer, ArrayList<MonthlyReport>>> monthlyReports;

    // Ключ год, значение - отчетная информация за год
    HashMap<Integer, ArrayList<YearlyReport>> yearlyReports;

    // Список файлов, где хранятся месячные отчеты
    ArrayList<String> monthlyReportsFilenames;

    // Список файлов, где хранятся месячные отчеты
    ArrayList<String> yearlyReportsFilenames;

    ReportManager() {
        monthlyReportsFilenames = new ArrayList<>();
        monthlyReportsFilenames.add("resources/m.202101.csv");
        monthlyReportsFilenames.add("resources/m.202102.csv");
        monthlyReportsFilenames.add("resources/m.202103.csv");

        yearlyReportsFilenames = new ArrayList<>();
        yearlyReportsFilenames.add("resources/y.2021.csv");
    }

    void ParseYearlyReports() {
        for (String reportFilename: yearlyReportsFilenames) {
            int[] data = GetMonthAndYearFromFilename(reportFilename);
            int year = data[0];
            ArrayList<YearlyReport> yearyReport = ParseYearReport(reportFilename);

            if (yearyReport != null) {
                if (yearlyReports == null) {
                    yearlyReports = new HashMap<>();
                }
                yearlyReports.put(year, yearyReport);
            }
        }
        System.out.println("Годовые отчеты успешно загружены");
    }

    void ParseMonthyReports() {
        for (String reportFilename: monthlyReportsFilenames) {
            // Получаем месяц и год по имени файла
            int[] data = GetMonthAndYearFromFilename(reportFilename);
            int year = data[0];
            int month = data[1];
            ArrayList<MonthlyReport> monthyReports = ParseMonthReport(reportFilename);

            if (monthyReports != null) {
                if (monthlyReports == null) {
                    monthlyReports = new HashMap<>();
                }

                HashMap<Integer, ArrayList<MonthlyReport>> curReport;
                if (monthlyReports.containsKey(year)){
                    curReport = monthlyReports.get(year);
                }
                else{
                    curReport = new HashMap<>();
                }

                curReport.put(month, monthyReports);
                monthlyReports.put(year, curReport);
            }
        }
        System.out.println("Месячные отчеты успешно загружены");
    }

    int[] GetMonthAndYearFromFilename(String filename) {
        int[] data = new int[2];
        // Выделяем в названии файла место, где хранится информация о годе и месяце
        String[] buf = filename.split("/");
        buf = buf[buf.length - 1].split("\\.");
        // Считали год
        data[0] = Integer.parseInt(buf[1].substring(0, 4));
        if (buf[1].length() == 4) {
            return data;
        }
        // Считали месяц
        data[1] = Integer.parseInt(buf[1].substring(4, 6));
        return data;
    }
    ArrayList<MonthlyReport> ParseMonthReport(String filename) {
        String fileContents = FileParser.readFileContentsOrNull(filename);
        if (fileContents == null) {
            return null;
        }

        ArrayList<MonthlyReport> monthyReports = new ArrayList<MonthlyReport>();
        String[] lines = fileContents.split(System.lineSeparator());
        for(int i = 1; i < lines.length; ++i){
            MonthlyReport monthlyReport = new MonthlyReport();
            String[] lineData = lines[i].split(",");
            monthlyReport.itemName = lineData[0];
            monthlyReport.isExpense = Boolean.parseBoolean(lineData[1]);
            monthlyReport.quantity = Integer.parseInt(lineData[2]);
            monthlyReport.sumOfOne = Integer.parseInt(lineData[3]);
            monthyReports.add(monthlyReport);
        }
        return monthyReports;
    }

    ArrayList<YearlyReport> ParseYearReport(String filename) {
        String fileContents = FileParser.readFileContentsOrNull(filename);
        if (fileContents == null) {
            return null;
        }

        ArrayList<YearlyReport> yearReports = new ArrayList<YearlyReport>();
        String[] lines = fileContents.split(System.lineSeparator());
        for(int i = 1; i < lines.length; ++i){
            YearlyReport yearlyReport = new YearlyReport();
            String[] lineData = lines[i].split(",");
            yearlyReport.month = Integer.parseInt(lineData[0]);
            yearlyReport.amount = Integer.parseInt(lineData[1]);
            yearlyReport.isExpense = Boolean.parseBoolean(lineData[2]);
            yearReports.add(yearlyReport);
        }
        return yearReports;
    }

    void PrintMonthReportsInformation() {
        if (!CheckLoadedReports()){
            return;
        }
        for (int year: monthlyReports.keySet()){
            System.out.println(year + " год:");
            for (int month: monthlyReports.get(year).keySet()) {
                System.out.println(month + " месяц:");
                MonthlyReport mostProfitableReport = GetExtremumReport(monthlyReports.get(year).get(month), false);
                System.out.println("Самый прибыльный товар: " + mostProfitableReport.itemName + " ("
                        + mostProfitableReport.sumOfOne + ") ");

                MonthlyReport mostExpensesReport = GetExtremumReport(monthlyReports.get(year).get(month), true);
                System.out.println("Самый большой расход: " + mostExpensesReport.itemName + " ("
                        + mostExpensesReport.sumOfOne + ") ");
            }
        }
    }

    void PrintYearReportsInformation() {
        if (!CheckLoadedReports()){
            return;
        }
        for (int year: yearlyReports.keySet()){
            System.out.println(year + " год:");

            ArrayList<Integer> expenses = new ArrayList<>();
            ArrayList<Integer> incomes = new ArrayList<>();
            ArrayList<Integer>  months = new ArrayList<>();

            for (YearlyReport report: yearlyReports.get(year)) {
                if (report.isExpense){
                    expenses.add(report.amount);
                    months.add(report.month);
                }
                else {
                    incomes.add(report.amount);
                }
            }

            for (int i = 0; i < months.size(); ++i) {
                int profit = incomes.get(i) - expenses.get(i);
                System.out.println(months.get(i) + ": прибыль " + profit);
            }

            System.out.println("Средний расход за месяц: " + GetAverageValue(expenses));
            System.out.println("Средний доход за месяц: " + GetAverageValue(incomes));
        }
    }

    double GetAverageValue(ArrayList<Integer> list){
        double value = 0;
        for (int val: list){
            value += val;
        }
        value /= list.size();
        return value;
    }

    void DataReconciliation() {
        if (!CheckLoadedReports()){
            return;
        }
        boolean flag = true;
        for (int year: yearlyReports.keySet()) {
            ArrayList<YearlyReport> curYearReports = yearlyReports.get(year);
            for (YearlyReport report: curYearReports) {
                int month = report.month;
                ArrayList<MonthlyReport> curMonthReports = monthlyReports.get(year).get(month);
                double moneyFlow = GetMonthMoneyFlow(curMonthReports, report.isExpense);
                String flowValue = (report.isExpense) ? "расходы" : "доходы";

                if (moneyFlow != report.amount) {
                    System.out.println(year + ": " + flowValue + " за месяц " + month + " не совпадают");
                    flag = false;
                }
            }
        }
        if (flag){
            System.out.println("Все расходы и доходы совпадают");
        }
    }

    int GetMonthMoneyFlow(ArrayList<MonthlyReport> monthReports, boolean isExpense) {
        int moneyFlow = 0;

        for (MonthlyReport report: monthReports) {
            if (report.isExpense == isExpense)
                moneyFlow += report.quantity * report.sumOfOne;
        }
        return moneyFlow;
    }

    MonthlyReport GetExtremumReport(ArrayList<MonthlyReport> monthReports, boolean isExpense) {
        MonthlyReport extremumReport = null;
        int extremumValue = 0;

        for (MonthlyReport report : monthReports) {
            if (report.isExpense == isExpense){
                int curValue = report.quantity * report.sumOfOne;
                if (curValue > extremumValue) {
                    extremumReport = report;
                    extremumValue = curValue;
                }
            }
        }
        return extremumReport;
    }

    boolean CheckLoadedReports() {
        boolean flag = true;
        if (monthlyReports == null) {
            System.out.println("Ошшибка: месячные расчеты не были загружены");
            flag = false;
        }
        if (yearlyReports == null) {
            System.out.println("Ошибка: годовые расчеты не были загружены");
            flag = false;
        }
        return flag;
    }
}
