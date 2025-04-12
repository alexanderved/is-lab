package server.repository.postgresql;

import common.domain.*;
import common.exceptions.InvalidFieldFormatException;
import org.postgresql.util.PGobject;
import server.application.IDGenerator;
import server.application.SQLSequenceIDGenerator;
import server.exceptions.RepositoryAccessDeniedException;
import server.exceptions.RepositoryAccessFailedException;
import server.logger.Logger;
import server.repository.Repository;
import server.repository.postgresql.dto.CoordinatesDTO;
import server.repository.postgresql.dto.LocationDTO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.HashSet;

public class PostgreSQLRepository extends Repository implements AutoCloseable {
    Connection connection;

    public PostgreSQLRepository(String url, String user, String password)
            throws RepositoryAccessDeniedException {
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://" + url, user, password);
        } catch (SQLException e) {
            throw new RepositoryAccessDeniedException(e);
        }
    }

    public IDGenerator createIDGenerator() {
        return new SQLSequenceIDGenerator(connection, "organization_id_generator");
    }

    @Override
    public HashSet<Organization> load() throws RepositoryAccessDeniedException {
        HashSet<Organization> orgs = new HashSet<>();

        String query = """
                SELECT organizations.id,
                        organizations.name,
                        organizations.coordinates,
                        organizations.creation_date,
                        organizations.annual_turnover,
                        organizations.full_name,
                        organizations.employees_count,
                        organizations.type,
                        organizations.street,
                        organizations.town
                    FROM organizations;
                """;

        try (PreparedStatement p = connection.prepareStatement(query)) {
            ResultSet res = p.executeQuery();

            while (res.next()) {
                int id = 0;
                try {
                    id = res.getInt(1);

                    OrganizationTemplate.Builder templateBuilder = new OrganizationTemplate.Builder()
                            .setName(res.getString(2))
                            .setCoordinates(CoordinatesDTO.fromPGobject(
                                    res.getObject(3, PGobject.class)).toCoordinates());

                    ZonedDateTime creationDate = res.getObject(4, OffsetDateTime.class)
                            .toZonedDateTime();

                    float annualTurnover = res.getFloat(5);
                    templateBuilder.setAnnualTurnover(annualTurnover == 0.0f ? null : annualTurnover);

                    templateBuilder.setFullName(res.getString(6))
                            .setEmployeesCount(res.getInt(7))
                            .setType(OrganizationType.valueOf(res.getString(8)));

                    String street = res.getString(9);
                    PGobject townObject = res.getObject(10, PGobject.class);
                    if (street != null || townObject.getValue() != null) {
                        Location town = LocationDTO.fromPGobject(townObject).toLocation();
                        templateBuilder.setOfficialAddress(new Address(street, town));
                    }

                    Organization org = new Organization(id, creationDate, templateBuilder.build());
                    orgs.add(org);
                } catch (InvalidFieldFormatException e) {
                    Logger.getGlobal().warn("Данные организации с id = " + id + " повреждены", e);
                    try {
                        remove(id);
                    } catch (RepositoryAccessFailedException repoAccessEx) {
                        Logger.getGlobal().warn("Не удалось удалить поврежденные данные", repoAccessEx);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RepositoryAccessDeniedException(e);
        }

        return orgs;
    }

    @Override
    public void add(Organization org) throws RepositoryAccessFailedException {
        String query = """
                INSERT INTO organizations(
                    id,
                    name,
                    coordinates,
                    creation_date,
                    annual_turnover,
                    full_name,
                    employees_count,
                    type,
                    street,
                    town
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?::organization_type, ?, ?);
                """;

        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setInt(1, org.getId());
            p.setString(2, org.getName());
            p.setObject(3,
                    CoordinatesDTO.fromCoordinates(org.getCoordinates()).toPGobject());
            p.setObject(4, org.getCreationDate().toOffsetDateTime());

            if (org.getAnnualTurnover() == null) {
                p.setNull(5, Types.FLOAT);
            } else {
                p.setFloat(5, org.getAnnualTurnover());
            }

            p.setString(6, org.getFullName());
            p.setInt(7, org.getEmployeesCount());
            p.setString(8, org.getType().toString());

            if (org.getOfficialAddress() == null) {
                p.setString(9, null);
                p.setNull(10, Types.STRUCT);
            } else {
                p.setString(9, org.getOfficialAddress().getStreet());
                p.setObject(10,
                        LocationDTO.fromLocation(org.getOfficialAddress().getTown()).toPGobject());
            }

            p.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryAccessFailedException(e);
        }
    }

    @Override
    public void remove(int id) throws RepositoryAccessFailedException {
        String query = "DELETE FROM organizations WHERE organizations.id = ?;";

        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setInt(1, id);
            p.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryAccessFailedException(e);
        }
    }

    @Override
    public void clear() throws RepositoryAccessFailedException {
        String query = "TRUNCATE TABLE organizations;";

        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryAccessFailedException(e);
        }
    }

    @Override
    public void update(int id, OrganizationTemplate template) throws RepositoryAccessFailedException {
        String query = """
                UPDATE organizations
                    SET name = ?,
                        coordinates = ?,
                        annual_turnover = ?,
                        full_name = ?,
                        employees_count = ?,
                        type = ?::organization_type,
                        street = ?,
                        town = ?
                    WHERE id = ?;
                """;

        try (PreparedStatement p = connection.prepareStatement(query)) {
            p.setString(1, template.getName());
            p.setObject(2,
                    CoordinatesDTO.fromCoordinates(template.getCoordinates()).toPGobject());

            if (template.getAnnualTurnover() == null) {
                p.setNull(3, Types.FLOAT);
            } else {
                p.setFloat(3, template.getAnnualTurnover());
            }

            p.setString(4, template.getFullName());
            p.setInt(5, template.getEmployeesCount());
            p.setString(6, template.getType().toString());

            if (template.getOfficialAddress() == null) {
                p.setString(7, null);
                p.setNull(8, Types.STRUCT);
            } else {
                p.setString(7, template.getOfficialAddress().getStreet());
                p.setObject(8,
                        LocationDTO.fromLocation(template.getOfficialAddress().getTown()).toPGobject());
            }

            p.setInt(9, id);

            p.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryAccessFailedException(e);
        }
    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }
}
