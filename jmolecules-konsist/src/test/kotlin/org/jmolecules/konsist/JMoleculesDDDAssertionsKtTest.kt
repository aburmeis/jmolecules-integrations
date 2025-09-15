package org.jmolecules.konsist

import com.lemonappdev.konsist.api.Konsist
import com.lemonappdev.konsist.core.exception.KoAssertionFailedException
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class JMoleculesDDDAssertionsKtTest {

    companion object {
        val SCOPE = Konsist.scopeFromFile("jmolecules-konsist/src/test/kotlin/org/jmolecules/konsist/Samples.kt")
    }

    @Test
    @Disabled("Kotlin file loading fails")
    fun detectsViolations() {
        val exeception = assertThrows<KoAssertionFailedException> {
            SCOPE.classesAndInterfaces()
                .entitiesShouldBeDeclaredForUseInSameAggregate()
                .aggregateReferencesShouldBeViaIdOrAssociation()
                .annotatedEntitiesAndAggregatesNeedToHaveAnIdentifier()
                .valueObjectsMustNotReferToIdentifiables()
        }
    }
}
