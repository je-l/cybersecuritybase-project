package sec.project.config;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import org.h2.tools.RunScript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sec.project.domain.Signup;
import sec.project.repository.SignupRepository;

@Service
public class CustomUserDetailsService {

    private Map<String, String> accountDetails;
    private String databaseAddress;

    @Autowired
    private SignupRepository signupRepository;

    @PostConstruct
    public void init() {
        // this data would typically be retrieved from a database
        this.accountDetails = new TreeMap<>();
        this.accountDetails.put("ted", "$2a$06$rtacOjuBuSlhnqMO2GKxW.Bs8J6KI0kYjw/gtF0bfErYgFyNTZRDm");

        databaseAddress = "jdbc:h2:file:./sql/database";

        try (Connection connection = DriverManager.getConnection(databaseAddress, "sa", "")) {
            RunScript.execute(connection, new FileReader("sql/database-schema.sql"));
            RunScript.execute(connection, new FileReader("sql/database-import.sql"));
        } catch (Exception e) {
            System.out.println("Error using database: " + e);
        }

    }

    public Signup loadUserByUsername(String username) throws UsernameNotFoundException {
        try (Connection connection = DriverManager.getConnection(databaseAddress, "sa", "")) {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM Registree");
            return new Signup(resultSet.getString("name"), resultSet.getString("address"), resultSet.getBoolean("publicness"));
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return null;
    }
}
