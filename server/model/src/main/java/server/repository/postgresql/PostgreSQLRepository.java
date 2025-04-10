package server.repository.postgresql;

import common.domain.Coordinates;
import common.domain.Organization;
import common.domain.OrganizationTemplate;
import server.exceptions.RepositoryAccessDeniedException;
import server.exceptions.RepositoryAccessFailedException;
import server.logger.Logger;
import server.repository.Repository;
import server.repository.postgresql.dto.CoordinatesDTO;
import server.repository.postgresql.dto.LocationDTO;

import javax.sql.RowSet;
import java.io.Closeable;
import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class PostgreSQLRepository extends Repository implements AutoCloseable {
    Connection connection;

    public PostgreSQLRepository(String url, String user, String password) throws RepositoryAccessDeniedException {
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager.getConnection(url, user, password);
        } catch (SQLException | ClassNotFoundException e) {
            throw new RepositoryAccessDeniedException(e);
        }
    }

    @Override
    public HashSet<Organization> load() throws RepositoryAccessDeniedException {


        return null;
    }

    @Override
    public void add(Organization org) throws RepositoryAccessFailedException {
        // TODO: PGobject
        try (PreparedStatement p = connection.prepareStatement("""
                INSERT INTO organizations(
                    id,
                    name,
                    coordinates,
                    creation_date,
                    creation_date_zone,
                    annual_turnover,
                    full_name,
                    employees_count,
                    type,
                    street,
                    town
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);
                """)) {
            p.setInt(1, org.getId());
            p.setString(2, org.getName());
            p.setObject(3, CoordinatesDTO.fromCoordinates(org.getCoordinates()));
            p.setObject(4, org.getCreationDate().toLocalDateTime());
            p.setString(5, org.getCreationDate().getZone().getId());
            p.setFloat(6, org.getAnnualTurnover());
            p.setString(7, org.getFullName());
            p.setInt(8, org.getEmployeesCount());
            p.setString(9, org.getType().toString());
            p.setString(10, org.getOfficialAddress().getStreet());
            p.setObject(11,
                    LocationDTO.fromLocation(org.getOfficialAddress().getTown()));

            p.executeUpdate();
        } catch (SQLException e) {
            throw new RepositoryAccessFailedException(e);
        }

    }

    @Override
    public void remove(int id) throws RepositoryAccessFailedException {

    }

    @Override
    public void clear() throws RepositoryAccessFailedException {

    }

    @Override
    public void update(int id, OrganizationTemplate template) throws RepositoryAccessFailedException {

    }

    @Override
    public void close() throws SQLException {
        connection.close();
    }
}
