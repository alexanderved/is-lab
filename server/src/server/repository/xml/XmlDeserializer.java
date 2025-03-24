package server.repository.xml;

import java.io.Reader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import common.domain.Organization;
import common.exceptions.DataParserException;
import common.utils.fields.AddressField;
import common.utils.fields.CoordinatesField;
import common.utils.fields.Field;
import common.utils.fields.LocationField;
import common.utils.fields.OrganizationField;
import common.utils.parsers.AddressParser;
import common.utils.parsers.CoordinatesParser;
import common.utils.parsers.LocationParser;
import common.utils.parsers.OrganizationParser;
import common.utils.parsers.OrganizationTemplateParser;
import server.exceptions.RepositoryDataCorruptedException;

final class XmlDeserializer {
    private final static String COLLECTION = "collection";
    private final static String ORGANIZATION = "organization";

    static HashSet<Organization> deserializeOrganizationCollection(
            Reader reader) throws RepositoryDataCorruptedException {
        HashSet<Organization> collection = new HashSet<>();

        XMLInputFactory factory = XMLInputFactory.newFactory();
        XMLStreamReader xmlReader = null;
        try {
            xmlReader = factory.createXMLStreamReader(reader);

            boolean isBreak = false;
            while (xmlReader.hasNext()) {
                int event = xmlReader.next();
                switch (event) {
                    case XMLEvent.START_DOCUMENT:
                        continue;

                    case XMLEvent.CHARACTERS:
                        if (xmlReader.getText().isBlank()) {
                            continue;
                        }
                        throw new RepositoryDataCorruptedException();

                    case XMLEvent.END_ELEMENT:
                        if (xmlReader.getLocalName().equals(COLLECTION)) {
                            isBreak = true;
                            break;
                        }
                        throw new RepositoryDataCorruptedException();

                    case XMLEvent.START_ELEMENT:
                        switch (xmlReader.getLocalName()) {
                            case COLLECTION:
                                continue;

                            case ORGANIZATION:
                                collection.add(deserializeOrganization(xmlReader));
                                break;

                            default:
                                throw new RepositoryDataCorruptedException();
                        }

                        break;

                    default:
                        break;
                }

                if (isBreak) {
                    break;
                }
            }

            if (!isBreak) {
                throw new RepositoryDataCorruptedException();
            }
        } catch (XMLStreamException | DataParserException e) {
            throw new RepositoryDataCorruptedException(e);
        } finally {
            try {
                xmlReader.close();
            } catch (XMLStreamException e) {
                throw new RepositoryDataCorruptedException(e);
            }
        }

        return collection;
    }

    private static Organization deserializeOrganization(XMLStreamReader xmlReader)
            throws RepositoryDataCorruptedException, DataParserException, XMLStreamException {
        final Map<OrganizationField, Object> orgData = new HashMap<>();

        boolean isBreak = false;
        while (xmlReader.hasNext()) {
            int event = xmlReader.next();
            switch (event) {
                case XMLEvent.CHARACTERS:
                    if (xmlReader.getText().isBlank()) {
                        continue;
                    }
                    throw new RepositoryDataCorruptedException();

                case XMLEvent.END_ELEMENT:
                    if (xmlReader.getLocalName().equals(ORGANIZATION)) {
                        isBreak = true;
                        break;
                    }
                    throw new RepositoryDataCorruptedException();

                case XMLEvent.START_ELEMENT:
                    String localName = xmlReader.getLocalName();
                    switch (localName) {
                        case "id":
                            deserializeSimpleField(xmlReader, orgData, OrganizationField.ID,
                                    OrganizationParser::parseId);
                            break;

                        case "name":
                            deserializeSimpleField(xmlReader, orgData, OrganizationField.NAME,
                                    OrganizationTemplateParser::parseName);
                            break;

                        case "coordinates":
                            deserializeCoordinates(xmlReader, orgData);
                            break;

                        case "creationDate":
                            deserializeSimpleField(xmlReader, orgData, OrganizationField.CREATION_DATE,
                                    OrganizationParser::parseCreationDate);
                            break;

                        case "annualTurnover":
                            deserializeSimpleField(xmlReader, orgData, OrganizationField.ANNUAL_TURNOVER,
                                    OrganizationTemplateParser::parseAnnualTurnover);
                            break;

                        case "fullName":
                            deserializeSimpleField(xmlReader, orgData, OrganizationField.FULL_NAME,
                                    OrganizationTemplateParser::parseFullName);
                            break;

                        case "employeesCount":
                            deserializeSimpleField(xmlReader, orgData, OrganizationField.EMPLOYEES_COUNT,
                                    OrganizationTemplateParser::parseEmployeesCount);
                            break;

                        case "type":
                            deserializeSimpleField(xmlReader, orgData, OrganizationField.TYPE,
                                    OrganizationTemplateParser::parseType);
                            break;

                        case "officialAddress":
                            deserializeOfficialAddress(xmlReader, orgData);
                            break;

                        default:
                            throw new RepositoryDataCorruptedException();
                    }

                    break;

                default:
                    break;
            }

            if (isBreak) {
                break;
            }
        }

        return OrganizationParser.parse(orgData);
    }

    private static <E extends Field, T> void deserializeSimpleField(XMLStreamReader xmlReader,
            Map<E, Object> storage, E field, FieldDataHandler<T> handler)
            throws RepositoryDataCorruptedException, DataParserException, XMLStreamException {
        String fieldString = null;

        boolean isBreak = false;
        while (xmlReader.hasNext()) {
            int event = xmlReader.next();

            switch (event) {
                case XMLEvent.CHARACTERS:
                    fieldString = xmlReader.getText().trim();
                    fieldString = fieldString.isEmpty() ? null : fieldString;

                    break;

                case XMLEvent.END_ELEMENT:
                    if (xmlReader.getLocalName().equals(field.getName())) {
                        isBreak = true;
                        break;
                    }
                    throw new RepositoryDataCorruptedException();

                default:
                    break;
            }

            if (isBreak) {
                break;
            }
        }

        storage.put(field, handler.handle(fieldString));
    }

    private static void deserializeCoordinates(XMLStreamReader xmlReader, Map<OrganizationField, Object> storage)
            throws RepositoryDataCorruptedException, DataParserException, XMLStreamException {
        final Map<CoordinatesField, Object> coordsData = new HashMap<>();

        boolean isBreak = false;
        while (xmlReader.hasNext()) {
            int event = xmlReader.next();

            switch (event) {
                case XMLEvent.CHARACTERS:
                    if (xmlReader.getText().isBlank()) {
                        continue;
                    }
                    throw new RepositoryDataCorruptedException();

                case XMLEvent.START_ELEMENT:
                    String localName = xmlReader.getLocalName();
                    switch (localName) {
                        case "x":
                            deserializeSimpleField(xmlReader, coordsData, CoordinatesField.X,
                                    CoordinatesParser::parseX);
                            break;

                        case "y":
                            deserializeSimpleField(xmlReader, coordsData, CoordinatesField.Y,
                                    CoordinatesParser::parseY);
                            break;

                        default:
                            throw new RepositoryDataCorruptedException();
                    }

                    break;

                case XMLEvent.END_ELEMENT:
                    if (xmlReader.getLocalName().equals("coordinates")) {
                        isBreak = true;
                        break;
                    }
                    throw new RepositoryDataCorruptedException();

                default:
                    throw new RepositoryDataCorruptedException();
            }

            if (isBreak) {
                break;
            }
        }

        storage.put(OrganizationField.COORDINATES, CoordinatesParser.parse(coordsData));
    }

    private static void deserializeOfficialAddress(XMLStreamReader xmlReader, Map<OrganizationField, Object> storage)
            throws RepositoryDataCorruptedException, DataParserException, XMLStreamException {
        final Map<AddressField, Object> addrData = new HashMap<>();

        boolean isBreak = false;
        while (xmlReader.hasNext()) {
            int event = xmlReader.next();

            switch (event) {
                case XMLEvent.CHARACTERS:
                    if (xmlReader.getText().isBlank()) {
                        continue;
                    }
                    throw new RepositoryDataCorruptedException();

                case XMLEvent.START_ELEMENT:
                    String localName = xmlReader.getLocalName();
                    switch (localName) {
                        case "street":
                            deserializeSimpleField(xmlReader, addrData, AddressField.STREET,
                                    AddressParser::parseStreet);
                            break;

                        case "town":
                            deserializeTown(xmlReader, addrData);
                            break;

                        default:
                            throw new RepositoryDataCorruptedException();
                    }

                    break;

                case XMLEvent.END_ELEMENT:
                    if (xmlReader.getLocalName().equals("officialAddress")) {
                        isBreak = true;
                        break;
                    }
                    throw new RepositoryDataCorruptedException();

                default:
                    throw new RepositoryDataCorruptedException();
            }

            if (isBreak) {
                break;
            }
        }

        storage.put(OrganizationField.OFFICIAL_ADDRESS, AddressParser.parse(addrData));
    }

    private static void deserializeTown(XMLStreamReader xmlReader, Map<AddressField, Object> storage)
            throws RepositoryDataCorruptedException, DataParserException, XMLStreamException {
        final Map<LocationField, Object> locData = new HashMap<>();

        boolean isBreak = false;
        while (xmlReader.hasNext()) {
            int event = xmlReader.next();

            switch (event) {
                case XMLEvent.CHARACTERS:
                    if (xmlReader.getText().isBlank()) {
                        continue;
                    }
                    throw new RepositoryDataCorruptedException();

                case XMLEvent.START_ELEMENT:
                    String localName = xmlReader.getLocalName();
                    switch (localName) {
                        case "x":
                            deserializeSimpleField(xmlReader, locData, LocationField.X, LocationParser::parseX);
                            break;

                        case "y":
                            deserializeSimpleField(xmlReader, locData, LocationField.Y, LocationParser::parseY);
                            break;

                        case "z":
                            deserializeSimpleField(xmlReader, locData, LocationField.Z, LocationParser::parseZ);
                            break;

                        default:
                            throw new RepositoryDataCorruptedException();
                    }

                    break;

                case XMLEvent.END_ELEMENT:
                    if (xmlReader.getLocalName().equals("town")) {
                        isBreak = true;
                        break;
                    }
                    throw new RepositoryDataCorruptedException();

                default:
                    throw new RepositoryDataCorruptedException();
            }

            if (isBreak) {
                break;
            }
        }

        storage.put(AddressField.TOWN, LocationParser.parse(locData));
    }

    @FunctionalInterface
    private interface FieldDataHandler<T> {
        T handle(String field) throws DataParserException;
    }
}
