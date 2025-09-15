package org.jmolecules.konsist

import com.lemonappdev.konsist.api.declaration.KoClassDeclaration
import com.lemonappdev.konsist.api.declaration.combined.KoClassAndInterfaceDeclaration
import com.lemonappdev.konsist.api.verify.assertFalse
import com.lemonappdev.konsist.api.verify.assertTrue
import kotlin.collections.filter

fun <T : KoClassAndInterfaceDeclaration> List<T>.aggregateReferencesShouldBeViaIdOrAssociation() : List<T> {
    filter { it.isIdentifiable() }
        .assertFalse(testName = "Identifiable use id reference or Association") {
            it.hasPropertyWithType { it.isAggregateRoot() }
        }
    return this
}

fun <T : KoClassAndInterfaceDeclaration> List<T>.annotatedEntitiesAndAggregatesNeedToHaveAnIdentifier() : List<T> {
    filter { it.isAnnotatedIdentifiable() }
        .assertTrue(testName = "Identifiable declares property (meta-)annotated with @Identity") {
            it.hasProperty { it.hasAnnotationOf(org.jmolecules.ddd.annotation.Identity::class) }
        }
    return this
}

fun <T : KoClassAndInterfaceDeclaration> List<T>.valueObjectsMustNotReferToIdentifiables() : List<T> {
    filter { it.isValueObject() || it.isIdentifier() }
        .assertFalse(testName = "ValueObjects must not refer to Identifiables") {
            it.hasPropertyWithType { it.isIdentifiable() }
        }
    return this
}

internal fun KoClassAndInterfaceDeclaration.isValueObject() =
    ((this as? KoClassDeclaration)?.hasValueModifier ?: false) ||
            hasParentInterfaceOf(org.jmolecules.ddd.types.ValueObject::class) ||
            hasAnnotationOf(org.jmolecules.ddd.annotation.ValueObject::class)

internal fun KoClassAndInterfaceDeclaration.isIdentifier() =
    hasParentInterfaceOf(org.jmolecules.ddd.types.Identifier::class)

internal fun KoClassAndInterfaceDeclaration.isIdentifiable() =
    hasParentInterfaceOf(org.jmolecules.ddd.types.Identifiable::class) || isAnnotatedIdentifiable()

internal fun KoClassAndInterfaceDeclaration.isAnnotatedIdentifiable() =
    hasAnnotationOf(org.jmolecules.ddd.annotation.AggregateRoot::class, org.jmolecules.ddd.annotation.Entity::class)

internal fun KoClassAndInterfaceDeclaration.isAggregateRoot() =
    hasParentInterfaceOf(org.jmolecules.ddd.types.AggregateRoot::class) ||
            hasAnnotationOf(org.jmolecules.ddd.annotation.AggregateRoot::class)

private fun KoClassAndInterfaceDeclaration.hasPropertyWithType(predicate: ((KoClassAndInterfaceDeclaration) -> Boolean)) : Boolean =
    hasProperty {
        it.hasType {
            it.hasSourceDeclaration {
                it.hasClassOrInterfaceDeclaration { predicate.invoke(it) }
            }
        }
    }
