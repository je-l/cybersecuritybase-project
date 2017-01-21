package sec.project.config;

import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
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
        databaseAddress = "jdbc:h2:file:./sql/database";

        try (Connection connection = DriverManager.getConnection(databaseAddress, "sa", "")) {
            connection.createStatement().execute("DROP TABLE Signup");
            RunScript.execute(connection, new FileReader("sql/database-schema.sql"));
            RunScript.execute(connection, new FileReader("sql/database-import.sql"));

        } catch (Exception e) {
            System.out.println("Error using database: " + e);
        }

        try (Connection connection = DriverManager.getConnection(databaseAddress, "sa", "")) {

            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM Signup");
            List<Signup> signups = new ArrayList<>();
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                String address = resultSet.getString("address");
                boolean publicness = resultSet.getBoolean("publicness");
                Signup news = new Signup(name, address, publicness);
                signups.add(news);
                signupRepository.save(news);

            }
        } catch (Exception e) {
            System.out.println("Error reading database: " + e);
        }

    }

    public Signup loadUserByUsername(String username) throws UsernameNotFoundException {
        try (Connection connection = DriverManager.getConnection(databaseAddress, "sa", "")) {
            ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM Signup");
            return new Signup(resultSet.getString("name"), resultSet.getString("address"), resultSet.getBoolean("publicness"));
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return null;
    }
}
