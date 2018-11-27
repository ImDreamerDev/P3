package dk.aau.ds304e18.database;

import org.junit.jupiter.api.BeforeAll;

class DatabaseParserTest {
    @BeforeAll
    static void init() {
        DatabaseManager.isTests = true;
    }

}
