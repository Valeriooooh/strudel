package pt.iscte.strudel.vm

import pt.iscte.strudel.model.IRecordType
import pt.iscte.strudel.model.IType


interface IHeapMemory {
    //@Throws(RuntimeError::class)
    fun allocateArray(baseType: IType, vararg dimensions: Int): IArray

    //@Throws(RuntimeError::class)
    fun allocateRecord(type: IRecordType): IRecord

    // TODO
    val memory: Int
        get() = 0

    interface IListener {
        fun allocated(value: IValue) {}
        fun deallocated(value: IValue) {}
    }
}