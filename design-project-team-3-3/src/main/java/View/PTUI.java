package View;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

import com.google.gson.Gson;

import Database.CollectionService;
import Model.Comic;
import Model.ComicElement;
import Model.Command.CommandController;
import Model.Command.SearchComicCommand;
import Model.Composite.CollectionHierarchy;
import Model.Composite.Publishers;
import Model.Composite.Series;
import Model.Composite.Volumes;
import Model.PersonalCollection;

/**
 * Plain Text User Interface — the only class that reads from stdin and
 * writes to stdout. All business logic is delegated to CommandController
 * and CollectionService; this class only handles menus and formatting.
 */
public class PTUI {

    // ── wired-up collaborators ────────────────────────────────────────────────
    private final Scanner           scanner   = new Scanner(System.in);
    private       CollectionService service;
    private       CommandController controller;
    private       PersonalCollection collection;
    private       CollectionHierarchy hierarchy;
    private       int               userID;
    private       String            username;

    // ── db config (mirrors DatabaseTest) ─────────────────────────────────────
    private static class DbConfig {
        String db_host;
        long   db_port;
        String db_name;
        String username;
        String password;
    }

    // =========================================================================
    // Entry point
    // =========================================================================

    public static void main(String[] args) {
        new PTUI().run();
    }

    private void run() {
        System.out.println("=== Welcome to COMIX ===");

        // 1. Connect to the database
        if (!connectToDatabase()) {
            System.out.println("Could not connect to database. Exiting.");
            return;
        }

        // 2. Login
        if (!loginMenu()) {
            System.out.println("Goodbye.");
            closeConnection();
            return;
        }

        // 3. Main menu loop
        mainMenu();

        closeConnection();
        System.out.println("Goodbye, " + username + "!");
    }

    // =========================================================================
    // Database connection
    // =========================================================================

    private boolean connectToDatabase() {
        try {
            Gson gson = new Gson();
            DbConfig cfg = gson.fromJson(new FileReader("db.json"), DbConfig.class);
            String url = "jdbc:postgresql://" + cfg.db_host + ":" + cfg.db_port + "/" + cfg.db_name;
            Connection con = DriverManager.getConnection(url, cfg.username, cfg.password);
            service = new CollectionService(con);
            return true;
        } catch (FileNotFoundException e) {
            System.out.println("db.json not found — please create it next to the jar.");
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
        return false;
    }

    private void closeConnection() {
        try { if (service != null) service.close(); }
        catch (SQLException e) { /* best effort */ }
    }

    // =========================================================================
    // Login
    // =========================================================================

    private boolean loginMenu() {
        System.out.println("\nAvailable users:");
        try {
            List<String> users = service.listUsers();
            for (int i = 0; i < users.size(); i++) {
                System.out.printf("  [%d] %s%n", i + 1, users.get(i));
            }
            System.out.print("Select a user (or 0 to quit): ");
            int choice = readInt();

            if (choice == 0) return false;
            if (choice < 1 || choice > users.size()) {
                System.out.println("Invalid selection.");
                return false;
            }

            username = users.get(choice - 1);
            userID   = service.findUserID(username);

            // Load persisted collection into memory
            collection = new PersonalCollection();
            service.loadCollection(userID, collection);
            controller = new CommandController(collection, userID);
            hierarchy  = new CollectionHierarchy();
            hierarchy.rebuild(collection);

            System.out.printf("%nWelcome back, %s! (%d comics in collection)%n",
                    username, collection.getIssueCount());
            return true;

        } catch (SQLException e) {
            System.out.println("Error loading users: " + e.getMessage());
            return false;
        }
    }

    // =========================================================================
    // Main menu
    // =========================================================================

    private void mainMenu() {
        boolean running = true;
        while (running) {
            System.out.println("""
                
                ── Main Menu ──────────────────────────────
                  [1] Browse my collection
                  [2] Search my collection
                  [3] Browse master database
                  [4] Search master database
                  [5] Add comic manually
                  [6] Remove a comic
                  [7] Edit a comic
                  [8] Grade a comic
                  [9] Slab a comic
                  [0] Quit
                ───────────────────────────────────────────""");
            System.out.print("Choice: ");

            switch (readInt()) {
                case 1 -> browseCollection();
                case 2 -> searchCollection();
                case 3 -> browseMasterDatabase();
                case 4 -> { searchMasterDatabase(); hierarchy.rebuild(collection); }
                case 5 -> { addComicManually();  hierarchy.rebuild(collection); }
                case 6 -> { removeComic();        hierarchy.rebuild(collection); }
                case 7 -> { editComic();          hierarchy.rebuild(collection); }
                case 8 -> gradeComic();
                case 9 -> slabComic();
                case 0 -> running = false;
                default -> System.out.println("Unknown option.");
            }
        }
    }

    // =========================================================================
    // Menu actions
    // =========================================================================

    private void browseCollection() {
        System.out.printf("%n=== My Collection ===%n");
        System.out.printf("  Total issues : %d%n", hierarchy.getTotalIssues());
        System.out.printf("  Total value  : $%.2f%n%n", hierarchy.getTotalValue());
        List<Publishers> pubs = hierarchy.getPublisherNodes();
        if (pubs.isEmpty()) { System.out.println("  (empty — add some comics first)"); return; }
        System.out.println("  Publishers:");
        printCompositeList(pubs);
        System.out.print("Select publisher # (0 to go back): ");
        int pick = readInt();
        if (pick >= 1 && pick <= pubs.size()) browsePublisher(pubs.get(pick - 1));
    }

    private void browsePublisher(Publishers pub) {
        System.out.printf("%n── %s  (%d issues | $%.2f) ──%n",
                pub.getPublisher(), pub.getIssueCount(), pub.getValue());
        List<ComicElement> children = pub.getElements();
        System.out.println("  Series:");
        printCompositeList(children);
        System.out.print("Select series # (0 to go back): ");
        int pick = readInt();
        if (pick >= 1 && pick <= children.size()
                && children.get(pick - 1) instanceof Series s) {
            browseSeries(pub, s);
        }
    }

    private void browseSeries(Publishers pub, Series series) {
        System.out.printf("%n── %s › %s  (%d issues | $%.2f) ──%n",
                pub.getPublisher(), series.getSeries(),
                series.getIssueCount(), series.getValue());
        List<ComicElement> children = series.getElements();
        System.out.println("  Volumes:");
        printCompositeList(children);
        System.out.print("Select volume # (0 to go back): ");
        int pick = readInt();
        if (pick >= 1 && pick <= children.size()
                && children.get(pick - 1) instanceof Volumes v) {
            browseVolume(pub, series, v);
        }
    }

    private void browseVolume(Publishers pub, Series series, Volumes vol) {
        System.out.printf("%n── %s › %s › Vol.%d  (%d issues | $%.2f) ──%n",
                pub.getPublisher(), series.getSeries(),
                vol.getVolume(), vol.getIssueCount(), vol.getValue());
        List<Comic> issues = vol.getElements().stream()
                .filter(e -> e instanceof Comic).map(e -> (Comic) e).toList();
        if (issues.isEmpty()) { System.out.println("  (no issues)"); return; }
        printComicTable(issues);
        System.out.print("Select issue # to view detail (0 to go back): ");
        int pick = readInt();
        if (pick >= 1 && pick <= issues.size()) printComicDetail(issues.get(pick - 1));
    }

    private void printComicDetail(Comic c) {
        System.out.println("\n" + "─".repeat(55));
        System.out.printf("  %s  Vol.%d  #%d%n", c.getSeriesTitle(), c.getVolume(), c.getIssue());
        if (!c.getStoryTitle().isBlank())  System.out.printf("  Story     : %s%n", c.getStoryTitle());
        System.out.printf(  "  Publisher : %s%n", c.getPublisher());
        if (!c.getCreators().isBlank())    System.out.printf("  Creators  : %s%n", c.getCreators());
        if (c.getPublicationDate() != null)System.out.printf("  Published : %s%n", c.getPublicationDate());
        if (!c.getDescription().isBlank()) System.out.printf("  Notes     : %s%n", c.getDescription());
        System.out.printf("  Value     : $%.2f%s%s%n", c.getValue(),
                c.isGraded() ? "  [Graded]" : "", c.isSlabbed() ? "  [Slabbed]" : "");
        System.out.println("─".repeat(55));
        pressEnterToContinue();
    }

    private void pressEnterToContinue() {
        System.out.print("Press Enter to continue..."); scanner.nextLine();
    }



    private void searchCollection() {
        System.out.print("Search term: ");
        String query = scanner.nextLine().trim();

        System.out.print("Exact match? (y/n): ");
        boolean exact = scanner.nextLine().trim().equalsIgnoreCase("y");

        System.out.println();
        System.out.print("Sort by — [1] Series/Vol/Issue  [2] Publication date: ");
        SearchComicCommand.SortOrder order = readInt() == 2
                ? SearchComicCommand.SortOrder.PUBLICATION_DATE
                : SearchComicCommand.SortOrder.SERIES_VOLUME_ISSUE;

        System.out.println(controller.searchComic(query, exact, order));
    }

    private void browseMasterDatabase() {
        try {
            List<String> publishers = service.browsePublishers();
            System.out.println("\nPublishers:");
            for (int i = 0; i < publishers.size(); i++) {
                System.out.printf("  [%d] %s%n", i + 1, publishers.get(i));
            }
            System.out.print("Select publisher to see series (0 to cancel): ");
            int choice = readInt();
            if (choice < 1 || choice > publishers.size()) return;

            String pub    = publishers.get(choice - 1);
            List<String> series = service.browseSeries(pub);
            System.out.printf("%nSeries under %s:%n", pub);
            series.forEach(s -> System.out.println("  • " + s));

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private void searchMasterDatabase() {
    System.out.print("Search master database: ");
    String query = scanner.nextLine().trim();

    try {
        List<Comic> results = service.searchDatabase(query, "default");
        if (results.isEmpty()) {
            System.out.println("No results found.");
            return;
        }
        System.out.printf("%d result(s):%n", results.size());
        printComicTable(results);

        boolean sorting = true;
        while (sorting) {
            System.out.println("\nSort by:");
            System.out.println("0. Cancel");
            System.out.println("1. Series Title (default)");
            System.out.println("2. Volume");
            System.out.println("3. Issue Number");
            System.out.println("4. Publication Date");
            System.out.println("5. Done sorting, add a comic");
            System.out.print("Enter choice: ");
            String sortChoice = scanner.nextLine().trim();

            switch (sortChoice) {
                case "0" -> { return; }
                case "5" -> { sorting = false; }
                default -> {
                    String sortBy = switch (sortChoice) {
                        case "2" -> "volume";
                        case "3" -> "issue";
                        case "4" -> "date";
                        default  -> "default";
                    };
                    results = service.searchDatabase(query, sortBy);
                    System.out.printf("%d result(s):%n", results.size());
                    printComicTable(results);
                }
            }
        }

        System.out.print("\nAdd one of these to your collection? Enter # (0 to cancel): ");
        int pick = readInt();
        if (pick < 1 || pick > results.size()) {
            System.out.println("Cancelled.");
            return;
        }
        Comic chosen = results.get(pick - 1);
        try {
            System.out.println(controller.addComic(chosen));
            service.addComic(userID, chosen);
        } catch (IllegalArgumentException e) {
            System.out.println("Comic is already in your collection.");
        }

    } catch (SQLException e) {
        System.out.println("Database error: " + e.getMessage());
    }
}

    private void addComicManually() {
        System.out.println("\n── Add Comic Manually ──");
        System.out.print("Publisher:    "); String publisher   = scanner.nextLine().trim();
        if(publisher.isBlank()){
            System.out.println("Publishers cannot be blank, cancelling action");
            return;
        }
        System.out.print("Series title: "); String seriesTitle = scanner.nextLine().trim();
        if(seriesTitle.isBlank()){
            System.out.println("Series cannot be blank, cancelling action");
            return;
        }
        System.out.print("Volume #:     "); 
        String volStr = scanner.nextLine().trim();
        int volume;
        try {
            volume = volStr.isBlank() ? 0 : Integer.parseInt(volStr);
            if (volume < 1) {
                System.out.println("Volume must be at least 1, defaulting to 1.");
                volume = 1;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input, defaulting to volume 1.");
            volume = 1;
        }
        System.out.print("Issue #:      "); String issStr = scanner.nextLine().trim();
        int issue;
        try {
            issue = issStr.isBlank() ? 0 : Integer.parseInt(issStr);
            if (issue < 1) {
                System.out.println("Issue must be at least 1, defaulting to 1.");
                issue = 1;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input, defaulting to issue 1.");
            issue = 1;
        }
        System.out.print("Value ($):    "); String valStr = scanner.nextLine().trim();
        double value;
        try {
            value = valStr.isBlank() ? 0 : Double.parseDouble(valStr);
            if (value <= 0) {
                System.out.println("Value must be at least 0, defaulting to 0.");
                value = 0;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input, defaulting to value 0.");
            value = 0;
        }
        Comic comic = new Comic(publisher, seriesTitle, volume, issue, LocalDate.now());
        comic.setValue(value);

        System.out.println(controller.addComic(comic));
    }

    private void removeComic() {
        Comic comic = pickComicFromCollection();
        if (comic == null){
            System.out.println("Remove Comic failed cancelling operation");
            return;
        } 

        String result = controller.removeComic(comic);
        System.out.println(result);
        if (result.contains("successfully")) {
            try { service.removeComic(userID, comic); }
            catch (SQLException e) { System.out.println("Warning: DB remove failed: " + e.getMessage()); }
        }
    }

    private void editComic() {
        Comic old = pickComicFromCollection();
        if (old == null) return;

        System.out.println("Editing: " + old);
        System.out.println("Enter new values (blank = keep current):");

        System.out.printf("Publishers [%s]: ", old.getPublisher());
        String pub = scanner.nextLine().trim();
        if(pub.isBlank()){
            pub=old.getPublisher();
        }

        System.out.printf("Series [%s]: ", old.getSeriesTitle());
        String title = scanner.nextLine().trim();
        if(title.isBlank()){
            title=old.getSeriesTitle();
        }

        System.out.printf("Volume #[%s]: ", old.getVolume());
        String volStr = scanner.nextLine().trim();
        int vol;
        try {
            vol = volStr.isBlank() ? old.getVolume() 
                                : Integer.parseInt(volStr);
            if (vol < 1) {
                System.out.println("Volume number must be at least 1, keeping original.");
                vol = old.getVolume();
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input, keeping original volume number.");
            vol = old.getVolume();
        }

        System.out.printf("Issue # [%s]: ", old.getIssue());
        String issStr = scanner.nextLine().trim();
        int iss;
        try {
            iss = issStr.isBlank() ? old.getIssue() 
                                : Integer.parseInt(issStr);
            if (iss < 1) {
                System.out.println("Issue number must be at least 1, keeping original.");
                iss = old.getIssue();
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input, keeping original issue number.");
            iss = old.getIssue();
        }
        
        System.out.printf("Publication Date [%s]: ", old.getPublicationDate());
        System.out.println("(press enter on each to keep current)");

        System.out.printf("  Year [%d]: ", old.getPublicationDate().getYear());
        String yearStr = scanner.nextLine().trim();
        int year;
        try {
            year = yearStr.isBlank() ? old.getPublicationDate().getYear()
                                    : Integer.parseInt(yearStr);
            if (year < 1900 || year > 2100) {
                System.out.println("Invalid year, keeping original.");
                year = old.getPublicationDate().getYear();
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input, keeping original year.");
            year = old.getPublicationDate().getYear();
        }

        System.out.printf("  Month [%d]: ", old.getPublicationDate().getMonthValue());
        String monthStr = scanner.nextLine().trim();
        int month;
        try {
            month = monthStr.isBlank() ? old.getPublicationDate().getMonthValue()
                                    : Integer.parseInt(monthStr);
            if (month < 1 || month > 12) {
                System.out.println("Invalid month, keeping original.");
                month = old.getPublicationDate().getMonthValue();
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input, keeping original month.");
            month = old.getPublicationDate().getMonthValue();
        }

        System.out.printf("  Day [%d]: ", old.getPublicationDate().getDayOfMonth());
        String dayStr = scanner.nextLine().trim();
        int day;
        try {
            day = dayStr.isBlank() ? old.getPublicationDate().getDayOfMonth()
                                : Integer.parseInt(dayStr);
            if (day < 1 || day > 31) {
                System.out.println("Invalid day, keeping original.");
                day = old.getPublicationDate().getDayOfMonth();
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input, keeping original day.");
            day = old.getPublicationDate().getDayOfMonth();
        }

        LocalDate newDate;
        try {
            newDate = LocalDate.of(year, month, day);
        } catch (DateTimeException e) {
            System.out.println("Invalid date combination, keeping original.");
            newDate = old.getPublicationDate();
        }

        System.out.printf("Description [%s]: ", old.getDescription());
        String desc = scanner.nextLine().trim();
        
        System.out.printf("Value [%.2f]: ", old.getValue());
        String valStr = scanner.nextLine().trim();
        double val;
        try {
            val = valStr.isBlank() ? old.getValue() 
                                : Double.parseDouble(valStr);
            if (val < 0) {
                System.out.println("Issue number must be at least .01, keeping original.");
                val = old.getValue();
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input, keeping original issue number.");
            val = old.getValue();
        }
        Comic updated = new Comic(pub, title,
                vol, iss, newDate);
        updated.setCreators(old.getCreators());
        updated.setCharacters(old.getCharacters());
        updated.setDescription(desc.isBlank() ? old.getDescription() : desc);
        updated.setValue(val);
        updated.setGraded(old.isGraded());
        updated.setSlabbed(old.isSlabbed());

        System.out.println(controller.editComic(old, updated));
        try { service.updateComic(userID, updated); }
        catch (SQLException e) { System.out.println("Warning: DB update failed: " + e.getMessage()); }
    }

    private void gradeComic() {
        Comic comic = pickComicFromCollection();
        if (comic == null) return;

        System.out.print("Grade (1-10): ");
        int grade = readInt();
        if (grade == 0) return;
        if (grade < 1 || grade > 10) {
            System.out.println("Grade must be between 1 and 10");
        }
        System.out.println(controller.gradeComic(comic, grade));
        try { service.updateGrade(userID, comic, grade); }
        catch (SQLException e) { 
            if (grade < 1 || grade > 10) {
                System.out.println("Grade has to be between 1 and 10."); 
            } else {
                System.out.println("Warning: DB grade update failed: " + e.getMessage()); 
            }
        }
        printComicDetail(comic);
        hierarchy.rebuild(collection);
    }

    private void slabComic() {
        Comic comic = pickComicFromCollection();
        if (comic == null) return;

        System.out.println(controller.slabComic(comic));
        try { service.updateSlabbed(userID, comic, true); }
        catch (SQLException e) { System.out.println("Warning: DB slab update failed: " + e.getMessage()); }
    }

    // =========================================================================
    // Shared UI helpers
    // =========================================================================

    /** Numbered list of the collection's comics; returns the chosen Comic or null. */
    private Comic pickComicFromCollection() {
        List<Comic> comics = collection.getComics();
        if (comics.isEmpty()) {
            System.out.println("Your collection is empty.");
            return null;
        }
        printComicTable(comics);
        System.out.print("Select comic # (0 to cancel): ");
        int pick = readInt();
        if (pick < 1 || pick > comics.size()) return null;
        return comics.get(pick - 1);
    }

    private void printComicTable(List<Comic> comics) {
        System.out.printf("%n  %-3s  %-30s  %-4s  %-5s  %-8s  %s%n",
                "#", "Series", "Vol", "Issue", "Value", "Flags");
        System.out.println("  " + "─".repeat(65));
        for (int i = 0; i < comics.size(); i++) {
            Comic c = comics.get(i);
            String flags = (c.isGraded() ? "[G]" : "") + (c.isSlabbed() ? "[S]" : "");
            System.out.printf("  %-3d  %-30s  %-4d  %-5d  $%-7.2f  %s%n",
                    i + 1,
                    truncate(c.getSeriesTitle(), 30),
                    c.getVolume(),
                    c.getIssue(),
                    c.getValue(),
                    flags);
        }
        System.out.println();
    }

    private void printCompositeList(List<? extends ComicElement> nodes) {
        for (int i = 0; i < nodes.size(); i++)
            System.out.printf("  [%d] %s%n", i + 1, nodes.get(i).toString());
    }


    private String truncate(String s, int max) {
        return s.length() <= max ? s : s.substring(0, max - 1) + "…";
    }

    private int readInt() {
        try {
            int val = Integer.parseInt(scanner.nextLine().trim());
            return val;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private double readDouble() {
        try {
            return Double.parseDouble(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}