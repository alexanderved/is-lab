package common.network.responses;

public class AverageOfEmployeesCountResponse extends SuccessResponse {
    private final int avg;

    public AverageOfEmployeesCountResponse(int avg) {
        super("Среднее число сотрудников в организациях: " + avg);
        
        this.avg = avg;
    }

    public int getAverageOfEmployeesCount() {
        return avg;
    }
}