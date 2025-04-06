package client.utils;

import common.domain.OrganizationTemplate;
import client.exceptions.InputDeniedException;
import client.exceptions.InputFailedException;
import client.exceptions.UnexpectedEODException;
import common.utils.fields.AddressField;
import common.utils.fields.CoordinatesField;
import common.utils.fields.LocationField;
import common.utils.fields.OrganizationField;
import common.utils.parsers.AddressParser;
import common.utils.parsers.CoordinatesParser;
import common.utils.parsers.LocationParser;
import common.utils.parsers.OrganizationTemplateParser;
import client.view.View;
import client.view.inputrequests.InputRequestFactory;

/**
 * Сборщик пользовательский ввод для получения шаблона организации.
 */
public class OrganizationTemplateCollector {
    /**
     * Собирает шаблон организации из пользователького ввода.
     * 
     * @param view Представление, из которого поступает пользовательский ввод.
     * @return Шаблон организации
     * @throws InputDeniedException   если сборщик не может запросить данные
     * @throws InputFailedException   если в процессе ввода данных возникла ошибка
     * @throws UnexpectedEODException если данные перестали поступать до окончания
     *                                сбора
     */
    public static OrganizationTemplate collect(View view)
            throws InputDeniedException, InputFailedException, UnexpectedEODException {
        InputRequestFactory inReqFactory = view.getInputRequestFactory();

        var coordinatesReq = inReqFactory
                .createCompositeDataInputRequest(OrganizationField.COORDINATES.getDisplayName(),
                        OrganizationTemplateParser::parseCoordinates)
                .addFieldRequest(CoordinatesField.X,
                        inReqFactory.createPrimitiveDataInputRequest(CoordinatesField.X.getDisplayName(),
                                CoordinatesParser::parseX))
                .addFieldRequest(CoordinatesField.Y,
                        inReqFactory.createPrimitiveDataInputRequest(CoordinatesField.Y.getDisplayName(),
                                CoordinatesParser::parseY));

        var townReq = inReqFactory
                .createCompositeDataInputRequest(AddressField.TOWN.getDisplayName(), AddressParser::parseTown)
                .addFieldRequest(LocationField.X,
                        inReqFactory.createPrimitiveDataInputRequest(LocationField.X.getDisplayName(),
                                LocationParser::parseX))
                .addFieldRequest(LocationField.Y,
                        inReqFactory.createPrimitiveDataInputRequest(LocationField.Y.getDisplayName(),
                                LocationParser::parseY))
                .addFieldRequest(LocationField.Z,
                        inReqFactory.createPrimitiveDataInputRequest(LocationField.Z.getDisplayName(),
                                LocationParser::parseZ));

        var officialAddressReq = inReqFactory
                .createCompositeDataInputRequest(OrganizationField.OFFICIAL_ADDRESS.getDisplayName(),
                        OrganizationTemplateParser::parseOfficialAddress)
                .addFieldRequest(AddressField.STREET,
                        inReqFactory.createPrimitiveDataInputRequest(AddressField.STREET.getDisplayName(),
                                AddressParser::parseStreet))
                .addFieldRequest(AddressField.TOWN, townReq);

        var templateReq = inReqFactory
                .createCompositeDataInputRequest("данные организации",
                        OrganizationTemplateParser::parse)
                .addFieldRequest(OrganizationField.NAME,
                        inReqFactory.createPrimitiveDataInputRequest(OrganizationField.NAME.getDisplayName(),
                                OrganizationTemplateParser::parseName))
                .addFieldRequest(OrganizationField.COORDINATES, coordinatesReq)
                .addFieldRequest(OrganizationField.ANNUAL_TURNOVER,
                        inReqFactory.createPrimitiveDataInputRequest(OrganizationField.ANNUAL_TURNOVER.getDisplayName(),
                                OrganizationTemplateParser::parseAnnualTurnover))
                .addFieldRequest(OrganizationField.FULL_NAME,
                        inReqFactory.createPrimitiveDataInputRequest(OrganizationField.FULL_NAME.getDisplayName(),
                                OrganizationTemplateParser::parseFullName))
                .addFieldRequest(OrganizationField.EMPLOYEES_COUNT,
                        inReqFactory.createPrimitiveDataInputRequest(OrganizationField.EMPLOYEES_COUNT.getDisplayName(),
                                OrganizationTemplateParser::parseEmployeesCount))
                .addFieldRequest(OrganizationField.TYPE,
                        inReqFactory.createPrimitiveDataInputRequest(OrganizationField.TYPE.getDisplayName(),
                                OrganizationField.TYPE.getExtra(), OrganizationTemplateParser::parseType))
                .addFieldRequest(OrganizationField.OFFICIAL_ADDRESS,
                        inReqFactory.createOptionalInputRequest(officialAddressReq));

        return view.input(templateReq);
    }
}
