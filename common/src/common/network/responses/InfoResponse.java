package common.network.responses;

import java.time.ZonedDateTime;

public class InfoResponse extends SuccessResponse {
    private final String collectionType;
    private final int collectionSize;
    private final ZonedDateTime collectionInitDate;
    private final ZonedDateTime collectionLastModDate;

    public InfoResponse(String collectionType, int collectionSize,
            ZonedDateTime collectionInitDate, ZonedDateTime collectionLastModDate) {
        super(getInfoString(collectionType, collectionSize, collectionInitDate, collectionLastModDate));

        this.collectionType = collectionType;
        this.collectionSize = collectionSize;
        this.collectionInitDate = collectionInitDate;
        this.collectionLastModDate = collectionLastModDate;
    }

    public String getCollecetionType() {
        return collectionType;
    }
    
    public int getCollecetionSize() {
        return collectionSize;
    }
    
    public ZonedDateTime getCollecetionInitDate() {
        return collectionInitDate;
    }

    public ZonedDateTime getCollectionLastModDate() {
        return collectionLastModDate;
    }

    private static String getInfoString(String collectionType, int collectionSize,
            ZonedDateTime collectionInitDate, ZonedDateTime collectionLastModDate) {
        String s = "Информация о коллекции:";
        s += "\tТип: " + collectionType;
        s += "\tКоличество элементов: " + collectionSize;
        s += "\tДата инициализации: " + collectionInitDate;
        s += "\tДата модификации: " + collectionLastModDate;

        return s;

    }
}