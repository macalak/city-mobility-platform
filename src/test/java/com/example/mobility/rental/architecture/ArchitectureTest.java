package com.example.mobility.rental.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.mobility.MobilityApplication;
import com.example.mobility.rental.application.service.RentalApplicationService;
import com.example.mobility.rental.domain.Rental;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import java.net.URI;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class ArchitectureTest {

    private static final JavaClasses CLASSES = new ClassFileImporter()
            .importPath(Path.of(URI.create(MobilityApplication.class.getProtectionDomain()
                    .getCodeSource().getLocation().toExternalForm())));

    @Test
    void importsProductionClassesWithoutTestClasses() {
        assertThat(CLASSES.contain(Rental.class)).isTrue();
        assertThat(CLASSES.contain(RentalApplicationService.class)).isTrue();
        assertThat(CLASSES.contain(ArchitectureTest.class)).isFalse();
    }

    @Test
    void domainShouldNotDependOnSpringJacksonPersistenceOrAdapters() {
        noClasses()
                .that()
                .resideInAnyPackage("com.example.mobility.rental.domain..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("org.springframework..", "com.fasterxml.jackson..", "jakarta.persistence..", "javax.persistence..", "com.example.mobility.rental.adapter..")
                .check(CLASSES);
    }

    @Test
    void applicationShouldNotDependOnAdapters() {
        noClasses()
                .that()
                .resideInAnyPackage("com.example.mobility.rental.application..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("com.example.mobility.rental.adapter..")
                .check(CLASSES);
    }

    @Test
    void adaptersShouldNotDependOnCompositionRoot() {
        noClasses()
                .that()
                .resideInAnyPackage("com.example.mobility.rental.adapter..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("com.example.mobility.bootstrap..")
                .check(CLASSES);
    }

    @Test
    void providerSpecificPackagesShouldNotBeReferencedByDomainOrApplication() {
        noClasses()
                .that()
                .resideInAnyPackage("com.example.mobility.rental.domain..", "com.example.mobility.rental.application..")
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage("com.example.mobility.rental.adapter.out.provider..")
                .check(CLASSES);
    
    }
}

