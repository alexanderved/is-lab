package common.network.responses;

import java.util.List;

public class PrintFieldDescendingAnnualTurnoverResponse extends SuccessResponse {
    private final List<Float> annualTurnovers;

    public PrintFieldDescendingAnnualTurnoverResponse(List<Float> annualTurnovers) {
        super(null);

        this.annualTurnovers = annualTurnovers;
        this.annualTurnovers.sort((a, b) -> -a.compareTo(b));
    }

    public List<Float> getDescendingAnnualTurnovers() {
        return annualTurnovers;
    }

    @Override
    public String getMessage() {
        List<String> annualTurnoversStrings = getDescendingAnnualTurnovers()
            .stream()
            .map(String::valueOf)
            .toList();

        return String.join("\n", annualTurnoversStrings);
    }
}