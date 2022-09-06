import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        int command = -1;
        Scanner scanner = new Scanner(System.in);
        ReportManager reportManager = new ReportManager();

        while (command != 0) {
            printMenu();
            if (scanner.hasNextInt()){
                command = scanner.nextInt();
                switch (command){
                    case(1):
                        reportManager.ParseMonthyReports();
                        break;
                    case(2):
                        reportManager.ParseYearlyReports();
                        break;
                    case(3):
                        reportManager.DataReconciliation();
                        break;
                    case(4):
                        reportManager.PrintMonthReportsInformation();
                        break;
                    case(5):
                        reportManager.PrintYearReportsInformation();
                        break;
                    case(0):
                        System.out.println("Программа успешно завершилась");
                        break;
                    default:
                        System.out.println("Ошибка ввода: команда не найдена");
                        break;
                }
            }
            else {
                System.out.println("Ошибка ввода: введите число, а не строку");
                scanner.next();
            }
        }
    }

    static void printMenu() {
        System.out.println("Введите одну из следующих команд:");
        System.out.println("1. Считать все месячные отчёты");
        System.out.println("2. Считать годовой отчёт");
        System.out.println("3. Сверить отчёты");
        System.out.println("4. Вывести информацию о всех месячных отчётах");
        System.out.println("5. Вывести информацию о годовом отчёте");
        System.out.println("0. Выйти из приложения");
    }
}

