package org.jmolecules.konsist

import org.jmolecules.ddd.annotation.Identity
import org.jmolecules.ddd.types.AggregateRoot
import org.jmolecules.ddd.types.Association
import org.jmolecules.ddd.types.Entity
import org.jmolecules.ddd.types.Identifier
import org.jmolecules.ddd.types.ValueObject
import java.util.UUID


class SampleIdentifier : Identifier

abstract class SampleAggregate : AggregateRoot<SampleAggregate, SampleIdentifier> {
    var valid: SampleEntity? = null

    var invalid: OtherEntity? = null
    var invalidInCollection: MutableCollection<OtherEntity>? = null
    var invalidInMap: MutableMap<String?, OtherEntity>? = null

    var invalidAggregate: OtherAggregate? = null
    var invalidAggregateInCollection: MutableCollection<OtherAggregate>? = null
    var invalidAggregateInMap: MutableMap<String?, OtherAggregate>? = null
    var invalidAggregateInNestedCollection: MutableCollection<MutableCollection<OtherAggregate>>? = null
    var invalidAggregateInCollectionInMap: MutableMap<String?, MutableList<OtherAggregate>>? = null

    var invalidAnnotatedAggregate: AnnotatedAggregate? = null

    var association: Association<OtherAggregate, SampleIdentifier>? = null

    var validAnnotatedEntity: AnnotatedEntity? = null
}

abstract class SampleEntity : Entity<SampleAggregate, SampleIdentifier> {
    var childEntities: MutableList<SampleChildEntity>? = null
}

abstract class SampleChildEntity : Entity<SampleAggregate, SampleIdentifier> {
    var grandChildEntity: SampleGrandChildEntity? = null
}

abstract class SampleGrandChildEntity : Entity<SampleAggregate, SampleIdentifier> {
    var otherEntity: OtherEntity? = null
}

abstract class OtherAggregate : AggregateRoot<OtherAggregate, SampleIdentifier>

abstract class OtherEntity : Entity<OtherAggregate, SampleIdentifier>

@org.jmolecules.ddd.annotation.AggregateRoot
class AnnotatedAggregate

@org.jmolecules.ddd.annotation.Entity
interface AnnotatedEntity {
    @get:Identity
    val id: Long?
}

data class SampleValueObject(
    val entity: SampleEntity,
    val annotatedEntity: AnnotatedEntity,
    val aggregate: SampleAggregate,
    val annotatedAggregate: AnnotatedAggregate
) : ValueObject

@org.jmolecules.ddd.annotation.AggregateRoot
class OtherAnnotatedAggregate {
    @field:Identity
    var id: Long? = null
    var invalidAnnotatedAggregate: AnnotatedAggregate? = null

    var invalidAnnotatedAggregateInCollection: MutableCollection<AnnotatedAggregate>? = null
    var invalidAnnotatedAggregateInNestedCollection: MutableCollection<MutableCollection<AnnotatedAggregate>>? = null

    var invalidAnnotatedAggregateInMap: MutableMap<String?, AnnotatedAggregate>? = null
    var invalidAnnotatedAggregateInCollectionInMap: MutableMap<String?, MutableCollection<AnnotatedAggregate>>? = null
}

@org.jmolecules.ddd.annotation.AggregateRoot
data class ThirdAnnotatedAggregate(
    @field:Identity
    var id: Long,
    var valid: AnnotatedEntity
)


// GH-301
@org.jmolecules.ddd.annotation.AggregateRoot
data class MyAggregateRoot(
    @field:Identity
    var id: UUID,
    var myInnerClass: MyInnerClass
) {
    @JvmInline
    value class MyInnerClass(val param: String) : ValueObject
}