package server.application;

import server.exceptions.IDGeneratorException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SQLSequenceIDGenerator extends IDGenerator {
    private final Connection connection;
    private final String seqName;

    public SQLSequenceIDGenerator(Connection connection, String seqName) {
        this.connection = connection;
        this.seqName = seqName;

        generateID();
    }

    @Override
    public int generateID() {
        try (PreparedStatement p = connection.prepareStatement("SELECT nextval(?);")) {
            p.setString(1, seqName);
            ResultSet res = p.executeQuery();

            res.next();
            return res.getInt(1);
        } catch (SQLException e) {
            throw new IDGeneratorException("Не удалось получить id из sequence", e);
        }
    }
}
