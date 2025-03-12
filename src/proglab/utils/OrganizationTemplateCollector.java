package proglab.utils;

import proglab.domain.OrganizationTemplate;
import proglab.exceptions.InputDeniedException;
import proglab.exceptions.InputFailedException;
import proglab.exceptions.UnexpectedEODException;
import proglab.utils.fields.AddressField;
import proglab.utils.fields.CoordinatesField;
import proglab.utils.fields.LocationField;
import proglab.utils.fields.OrganizationField;
import proglab.utils.parsers.AddressParser;
import proglab.utils.parsers.CoordinatesParser;
import proglab.utils.parsers.LocationParser;
import proglab.utils.parsers.OrganizationTemplateParser;
import proglab.view.View;
import proglab.view.inputrequests.InputRequestFactory;

public class OrganizationTemplateCollector {
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
