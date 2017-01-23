## Course project 1 for
[https://cybersecuritybase.github.io/](https://cybersecuritybase.github.io/)

Participants can register to the event using a form. They can choose if they
want the registration be public or not.

### Installation `git clone`

`cd` into repo

`mvn package`

### Usage

`java -jar target/cybersecuritybase-project-1.0-SNAPSHOT.jar`

The server should now run at port 8080

### 1. Injection flaw
1. Start the server
2. Go to `http://localhost:8080/`
3. Register to the event.
4. Enter `' OR TRUE --` to the filter field.
5. Click filter

### 2. XSS flaw
1. Start the server
2. Go to `http://localhost:8080/`
3. Register to the event with name `<script>alert("xss flaw");</script>`
4. Now alert box is displayed in the registree page, and attacker could exploit
   the flaw with javascript.

Now non-public registration are displayed, incorrectly.

### 3. Coming soon
