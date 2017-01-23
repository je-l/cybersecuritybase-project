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
        List<Signup> singups = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(databaseAddress, "sa", "")) {
            String query = "SELECT * FROM Signup WHERE name = '" + username + "' AND publicness = TRUE";
            ResultSet resultSet = connection.createStatement().executeQuery(query);

            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                boolean publicness = resultSet.getBoolean("publicness");
                singups.add(new Signup(name, address, publicness));

            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        return singups;
    }

    public void saveUserToDatabase(String user, String address, boolean publicness) {
        try (Connection connection = DriverManager.getConnection(databaseAddress, "sa", "")) {
            String query = "INSERT INTO Signup (id, name, address, publicness) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, new Random().nextInt(1000000000));
            statement.setString(2, user);
            statement.setString(3, address);
            statement.setBoolean(4, publicness);
            System.out.println("Tryign to insert: " + statement.toString());
            statement.executeUpdate();
        } catch (Exception e) {
            System.out.println("Error: " + e);
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
