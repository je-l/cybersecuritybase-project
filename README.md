## Course project 1 for [https://cybersecuritybase.github.io/](https://cybersecuritybase.github.io/)

Participants can register to the event using a form. They can choose if they
want the registration be public or not.

### Installation

`git clone`

`cd` into repo

`mvn package`

### Usage

`java -jar target/cybersecuritybase-project-1.0-SNAPSHOT.jar`

The server should now run at port 8080

### Injection flaw
1. Start the server
2. Go to `http://localhost:8080/`
3. Register to the event.
4. Enter `' OR TRUE --` to the filter field.
5. Click filter

Changing the database queries into prepared statements would fix this problem.
So instead of

`SELECT * FROM Signup WHERE name = '" + username + "' AND publicness = TRUE`

we would use

`SELECT * FROM Signup WHERE name = ? AND publicness = TRUE`

with prepared statements.


### XSS flaw
1. Start the server
2. Go to `http://localhost:8080/`
3. Register to the event with name `<script>alert("xss flaw");</script>`
4. Now alert box is displayed in the registree page, and attacker could exploit
   the flaw with javascript.

Now non-public registration are displayed, incorrectly. (Clear the database
entries by deleting .db files in db directory)

Changing the input prompt value from unescaped to escaped in done.html file
should fix this problem.

### Missing function level access control
1. Start the server
2. Go to `http://localhost:8080/signups/2`
3. Private information is now displayed.

Requiring authentication of users before giving the signup page would fix this
problem. Only the personal page would be viewable.
