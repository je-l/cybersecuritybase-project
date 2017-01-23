package sec.project.config;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.annotation.PostConstruct;
import org.h2.tools.RunScript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sec.project.domain.Signup;
import sec.project.repository.SignupRepository;

@Service
public class CustomUserDetailsService {

    private String databaseAddress;

    @Autowired
    private SignupRepository signupRepository;

    @PostConstruct
    public void init() {
        this.databaseAddress = "jdbc:h2:file:./db/database";

        try (Connection connection = DriverManager.getConnection(databaseAddress, "sa", "")) {
            if (!databaseExists(connection, "SIGNUP")) {
                RunScript.execute(connection, new FileReader("db/database-schema.sql"));
                RunScript.execute(connection, new FileReader("db/database-import.sql"));
            }

        } catch (Exception e) {
            System.out.println("Error using database: " + e);
        }

        try (Connection connection = DriverManager.getConnection(databaseAddress, "sa", "")) {

            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM Signup");
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                boolean publicness = resultSet.getBoolean("publicness");
                Signup news = new Signup(name, address, publicness);
                signupRepository.save(news);

            }
        } catch (Exception e) {
            System.out.println("Error reading database: " + e);
        }

    }

    public List<Signup> loadUserByUsername(String username) throws UsernameNotFoundException {
        List<Signup> signups = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(databaseAddress, "sa", "")) {
            String query = "SELECT * FROM Signup WHERE name = '" + username + "' AND publicness = TRUE";
            ResultSet resultSet = connection.createStatement().executeQuery(query);

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                boolean publicness = resultSet.getBoolean("publicness");
                signups.add(new Signup(name, address, publicness));

            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        return signups;
    }

    public void saveUserToDatabase(Signup sig) {
        try (Connection connection = DriverManager.getConnection(databaseAddress, "sa", "")) {
            String query = "INSERT INTO Signup (id, name, address, publicness) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, sig.getId());
            statement.setString(2, sig.getName());
            statement.setString(3, sig.getAddress());
            statement.setBoolean(4, sig.isPublic());
            statement.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error inserting new user into db: " + e);
        }
    }
    
    public void removeUserFromDatabase(long id) {
        try (Connection connection = DriverManager.getConnection(databaseAddress, "sa", "")) {
            String query = "DELETE FROM Signup WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (Exception e) { 
            System.out.println("Error removing user from db: " + e);
        }
    }

    private boolean databaseExists(Connection connection, String name) throws SQLException {
        DatabaseMetaData dbmd = connection.getMetaData();
        String[] tab = {"TABLE"};
        ResultSet tables = dbmd.getTables(null, null, null, tab);
        while (tables.next()) {
            if (tables.getString("TABLE_NAME").equals(name)) {
                return true;
            }
        }
        return false;
    }
}
