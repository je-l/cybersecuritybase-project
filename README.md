## Course project 1 for [https://cybersecuritybase.github.io/](https://cybersecuritybase.github.io/)

Participants can register to the event using a form. They can choose if they
want the registration be public or not.

### Installation

With maven:

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
6. Now non-public registrations are displayed, incorrectly. 

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

(Clear the database entries by deleting .db files in db directory)

Changing the input prompt value from unescaped to escaped in done.html file
should fix this problem.

### Missing function level access control
1. Start the server
2. Go to `http://localhost:8080/signups/2`
3. Private information is now displayed.

Requiring authentication of users before giving the signup page would fix this
problem. Only the personal page should be viewable.

### CSRF
1. Make sure database is in original state: remove all .db files in db/
   directory (if any).
2. Start server.
3. Go to `http://localhost:8080/`
4. Register to the event with name `<img src="http://localhost:8080/signups/1/delete" width="0" height="0" />`
5. Select registration as public.
6. Now when registree list is opened, Jack's signup will be removed.

The server doesn't have any authentication, so anyone opening the done.html
page will cause Jack to be removed from the list. Normally this flaw would 
happen only when Jack opens the page. Using a session token in url or http
request would be a solution for this flaw. Also escaping the name string
so that img tags could not be used.

Delete the .db files to revert the database to original state.

### Using Components with Known Vulnerabilities
[OWASP Dependency Check](https://www.owasp.org/index.php/OWASP_Dependency_Check)
was used to identify this flaw. The tool reported one of the flaws to be
[CVE-2016-9878](https://web.nvd.nist.gov/view/vuln/detail?vulnId=CVE-2016-9878).
This project is using Spring boot framework, which has spring core as a
dependency.  Spring core version is 4.3.4 so the project has known unsafe
component.

To view all of the project dependencies:

1. `cd` into project dir where pom.xml is.
2. `mvn dependency:list` or just `mvn dependency:list grep spring-core`
3. Spring framework core version 4.3.4 should be listed.

This flaw can be fixed by changing the parent artifact version in pom.xml from
version 1.4.2 to version 1.4.3
