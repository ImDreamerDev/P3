package dk.aau.ds304e18.database;

import org.junit.jupiter.api.BeforeAll;

public class DatabaseParserTest {
    @BeforeAll
    static void init() {
        DatabaseManager.isTests = true;
    }

}
