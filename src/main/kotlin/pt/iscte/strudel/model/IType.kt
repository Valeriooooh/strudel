package pt.iscte.strudel.model

import pt.iscte.strudel.model.impl.ArrayType
import pt.iscte.strudel.model.impl.RecordAllocation
import pt.iscte.strudel.model.impl.ReferenceType


interface IType : IProgramElement {
    val isVoid: Boolean
        get() = this === VOID
    val isUnbound: Boolean
        get() = this is UnboundType
    val isValueType: Boolean
        get() = this is IValueType<*>
    val isNumber: Boolean
        get() = this === INT || this === DOUBLE
    val isBoolean: Boolean
        get() = this === BOOLEAN

    val isReference: Boolean
        get() = this is IReferenceType
    val isArrayReference: Boolean
        get() = isReference && (this as IReferenceType).target is IArrayType
    val isRecordReference: Boolean
        get() = isReference && (this as IReferenceType).target is IRecordType

    val asRecordType: IRecordType
        get() = (this as IReferenceType).target as IRecordType

    fun reference(): IReferenceType = ReferenceType(this)

    val defaultExpression: IExpression

    fun array(): IArrayType = ArrayType(this)

    fun array(n: Int): IArrayType {
        var a = array()
        (1 until n).forEach { _ ->
            a = a.array()
        }
        return a
    }

}

object VOID : IType {

    override fun reference(): IReferenceType {
        throw RuntimeException("not valid")
    }

    override fun toString(): String {
        return "void"
    }

    override fun setProperty(key: String, value: Any?) {
        TODO("Not yet implemented")
    }

    override fun getProperty(key: String): Any? {
        TODO("Not yet implemented")
    }

    override var id: String? = "void"
        set(value) = check(false)

    override val defaultExpression: IExpression
        get() = NULL_LITERAL

    override fun cloneProperties(e: IProgramElement) {
        throw UnsupportedOperationException()
    }
}

val ANY = UnboundType()

class UnboundType(override val defaultExpression: IExpression = NULL_LITERAL) : IType {
    override fun reference(): IReferenceType {
        TODO("Not yet implemented")
    }

    override fun setProperty(key: String, value: Any?) {
        TODO("Not yet implemented")
    }

    override fun getProperty(key: String): Any {
        TODO("Not yet implemented")
    }

    override fun cloneProperties(e: IProgramElement) {
        TODO("Not yet implemented")
    }

    override var id: String?
        get() = "Object"
        set(value) {}

    override fun isSame(e: IProgramElement): Boolean {
        return e is IType
    }

    override fun toString(): String = "Object"

}

class JavaType(val type: Class<*>) : IType {
    override fun reference(): IReferenceType {
        return ReferenceType(this)
    }

    override val defaultExpression: IExpression
        get() = NULL_LITERAL

    override fun setProperty(key: String, value: Any?) {
        throw UnsupportedOperationException()
    }

    override fun getProperty(key: String): Any {
        throw UnsupportedOperationException()
    }

    override fun cloneProperties(e: IProgramElement) {
        throw UnsupportedOperationException()
    }

    override var id: String?
        get() = type.name
        set(value) {
            throw UnsupportedOperationException()
        }

    override fun toString(): String = type.name

    override fun isSame(e: IProgramElement): Boolean {
        return e is JavaType && e.type === type
    }


}