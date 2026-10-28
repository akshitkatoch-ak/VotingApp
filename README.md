# Online Voting System

A simple web-based voting application built with **Java Servlets** and **Apache Tomcat 11**. Users enter their name, pick their favorite subject, and see live results. Each user can vote only once, and all votes are saved to a file so they survive a server restart.

## Features

- Display voting options (favorite subject)
- Submit a vote through an HTML form (POST)
- Prevent multiple voting using **HttpSession** and a unique voter name
- Let different users vote one after another using the "Vote as another user" link
- Display results with vote counts, percentages and progress bars
- Persist votes in a text file (data is kept after Tomcat restarts)
- HTML output is escaped to prevent XSS

## Voting Options

1. Advance Internet Programming
2. Advance Database Management System
3. Python Programming
4. Design and Analysis of Algorithm
5. Mathematics

To change the options, edit the `OPTIONS` array in `VoteStore.java` and delete the old `votes.txt`.

## Concepts Covered

- Session tracking (`HttpSession`)
- Form handling (`doGet` / `doPost`)
- Servlet logic and the Post/Redirect/Get pattern
- File-based data storage
- Thread safety with `synchronized`

## Tech Stack

| Component | Version |
|-----------|---------|
| Java (JDK) | 17 or higher |
| Servlet API | Jakarta Servlet 6.1 |
| Server | Apache Tomcat 11 |
| IDE | Eclipse IDE for Enterprise Java and Web Developers |

> Tomcat 10 and later use the `jakarta.servlet` packages, not `javax.servlet`.

## Project Structure

```
VotingApp
├── src/main/java/com/voting
│   ├── Page.java           # Shared HTML layout and escaping helper
│   ├── ResultServlet.java  # /results: shows vote counts
│   ├── VoteServlet.java    # /vote: shows the form and records the vote
│   └── VoteStore.java      # Vote data, file storage, duplicate-voter check
└── src/main/webapp
    ├── index.html          # Redirects to /vote
    └── WEB-INF/web.xml     # Deployment descriptor (session timeout 30 min)
```

## How to Run in Eclipse

1. **Install** JDK 17+, Eclipse IDE for Enterprise Java and Web Developers, and [Apache Tomcat 11](https://tomcat.apache.org/download-11.cgi).
2. **Add Tomcat to Eclipse:** Window → Preferences → Server → Runtime Environments → Add → Apache Tomcat v11.0, then browse to the Tomcat folder.
3. **Create the project:** File → New → Dynamic Web Project, name it `VotingApp`, select the Tomcat v11.0 runtime, and tick *Generate web.xml deployment descriptor*.
4. **Add the code:**
   - Create the package `com.voting` under `src/main/java` and add the four `.java` files.
   - Put `index.html` in `src/main/webapp`.
   - Replace `WEB-INF/web.xml` with the one from this repository.
5. **Run:** right-click the project → Run As → Run on Server → Tomcat v11.0 → Finish.
6. **Open** <http://localhost:8080/VotingApp/> in your browser.

## How It Works

| URL | Method | Purpose |
|-----|--------|---------|
| `/vote` | GET | Shows the voting form, or an "already voted" message if the session has voted |
| `/vote` | POST | Validates the name and choice, saves the vote, marks the session as voted |
| `/vote?new=1` | GET | Ends the current session so another user can vote |
| `/results` | GET | Shows the current results |

**Duplicate-vote prevention**

1. The session stores a `hasVoted` flag, so the same browser session cannot vote twice.
2. The voter name must be unique (compared ignoring case and extra spaces), so a name cannot vote twice even from a new session.

## Data Storage

Votes are appended to a text file, one line per vote (`voterName|option`):

```
<user home>/votingapp-data/votes.txt
```

For example, `C:\Users\<YourName>\votingapp-data\votes.txt` on Windows. The file is loaded when the application starts, and its exact path is printed in the Eclipse console.

**To reset all votes:** stop Tomcat, delete `votes.txt`, and start Tomcat again.

## Testing

1. Vote as `Akshit`. You are taken to the results page.
2. Click **Vote as another user** and vote as `Rahul`. The total goes up.
3. Try voting again as `Akshit`. The app shows "This name has already voted".
4. Restart Tomcat. The results are still there.

## Troubleshooting

- **Red errors on `jakarta.servlet`:** Project → Properties → Java Build Path → Libraries → Add Library → Server Runtime → Tomcat 11, then Project → Clean.
- **404 error:** use the exact context path `/VotingApp/` and restart the server.
- **Port 8080 in use:** change the HTTP port by double-clicking the server in the Servers tab.

## Limitations and Future Improvements

- Voters are identified by the name they type, so the system relies on honest input. A real system would use login and authentication.
- Storage is a plain text file. It could be replaced with a database (JDBC with MySQL or PostgreSQL).
- Options are hard-coded. An admin page could manage them.
- No admin page for resetting or exporting results.

## Author

**Akshit Katoch**
