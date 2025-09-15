/*
 * Copyright 2020-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jmolecules.konsist

import com.lemonappdev.konsist.api.declaration.KoClassDeclaration
import com.lemonappdev.konsist.api.declaration.combined.KoClassAndInterfaceDeclaration
import com.lemonappdev.konsist.api.verify.assertFalse
import com.lemonappdev.konsist.api.verify.assertTrue
import kotlin.collections.filter


/**
 * Verifies that fields that implement [org.jmolecules.ddd.types.Entity] within a type implementing
 * [org.jmolecules.ddd.types.AggregateRoot] declare the aggregate type as the owning aggregate.
 *
 * ```
 * class Customer : AggregateRoot<Customer, CustomerId> { … }
 * class Address : Entity<Customer, AddressId> { … }
 *
 * class LineItem : Entity<Order, LineItemId> { … }
 * data class Order(
 *   val lineItems : List<LineItem>, // valid
 *   val shippingAddress : Address, // invalid as Address is declared to belong to Customer
 * ) : AggregateRoot<Order, OrderId>
 * ```
 *
 * @return this list.
 */
fun <T : KoClassAndInterfaceDeclaration> List<T>.entitiesShouldBeDeclaredForUseInSameAggregate() : List<T> {
    //TODO
    return this
}

/**
 * Verifies that one [org.jmolecules.ddd.types.AggregateRoot] does not reference another via the remote
 * AggregateRoot type but rather via their identifier type or an explicit [org.jmolecules.ddd.types.Association] type.
 *
 * ```
 * class Customer : AggregateRoot<Customer, CustomerId> { … }
 *
 * data class Order(
 *   val customer : Customer, // invalid
 *   val customerId : CustomerId, // valid
 *   val customer : Association<Customer>, // valid
 * ) : AggregateRoot<Order, OrderId>
 * ```
 *
 * @return this list.
 */
fun <T : KoClassAndInterfaceDeclaration> List<T>.aggregateReferencesShouldBeViaIdOrAssociation() : List<T> {
    filter { it.isIdentifiable() }
        .assertFalse(testName = "Identifiable use id reference or Association") {
            it.hasPropertyWithType { it.isAggregateRoot() }
        }
    return this
}

/**
 * Verifies that classes annotated with [org.jmolecules.ddd.annotation.AggregateRoot] or
 * [org.jmolecules.ddd.annotation.Entity] declare a field annotated with [org.intellij.lang.annotations.Identifier].
 *
 * @return this list.
 */
fun <T : KoClassAndInterfaceDeclaration> List<T>.annotatedEntitiesAndAggregatesNeedToHaveAnIdentifier() : List<T> {
    filter { it.isAnnotatedIdentifiable() }
        .assertTrue(testName = "Identifiable declares property (meta-)annotated with @Identity") {
            it.hasProperty { it.hasAnnotationOf(org.jmolecules.ddd.annotation.Identity::class) }
        }
    return this
}

/**
 * Verifies that value objects and identifiers do not refer to identifiables.
 *
 * @return this list.
 */
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
