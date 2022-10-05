package hexlet.code.migration;

import io.ebean.annotation.Platform;
import io.ebean.dbmigration.DbMigration;

import java.io.IOException;

public final class MigrationGenerator {

    private MigrationGenerator() {

    }

    public static void main(String[] args) throws IOException {
        DbMigration dbMigration = DbMigration.create();
        dbMigration.addPlatform(Platform.H2);
        dbMigration.addPlatform(Platform.POSTGRES);
        dbMigration.generateMigration();
    }
}

