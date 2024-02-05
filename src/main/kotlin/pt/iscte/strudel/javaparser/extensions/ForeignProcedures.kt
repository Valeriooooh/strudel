package pt.iscte.strudel.javaparser.extensions

import com.github.javaparser.ast.expr.MethodCallExpr
import pt.iscte.strudel.javaparser.THIS_PARAM
import pt.iscte.strudel.model.IModule
import pt.iscte.strudel.model.IProcedureDeclaration
import pt.iscte.strudel.model.IType
import pt.iscte.strudel.model.JavaType
import pt.iscte.strudel.model.impl.PolymophicProcedure
import pt.iscte.strudel.vm.impl.ForeignProcedure
import java.lang.reflect.Method

internal fun createForeignProcedure(module: IModule, scope: String?, method: Method, isStatic: Boolean, types: Map<String, IType>): ForeignProcedure {
    val thisParamType = if (isStatic) listOf() else listOf(getTypeByName(method.declaringClass.canonicalName, types))
    val parameterTypes = thisParamType + method.parameters.map { getTypeByName(it.type.canonicalName, types) }
    return ForeignProcedure(
        module,
        scope,
        method.name,
        getTypeByName(method.returnType.canonicalName, types),
        parameterTypes
    )
    { vm, args ->
        val res =
            if (isStatic) // static --> no caller
                method.invoke(null, *args.map { it.value }.toTypedArray())
            else if (args.size == 1) { // not static --> caller is $this parameter
                method.invoke(args.first().value)
            } else if (args.size > 1) {
                val caller = args.first().value // should be $this
                if (caller!!.javaClass != method.declaringClass)
                    error("Cannot invoke instance method $method with object instance $caller: is ${caller.javaClass.canonicalName}, should be ${method.declaringClass.canonicalName}")
                val arguments = args.slice(1 until args.size).map { it.value }
                method.invoke(caller, *arguments.toTypedArray())
            } else error("Cannot invoke instance method $method with 0 arguments: missing (at least) object reference $THIS_PARAM")
        when (res) {
            is String -> getStringValue(res)
            else -> vm.getValue(res)
        }
    }
}

internal fun MethodCallExpr.asForeignProcedure(module: IModule, namespace: String?, types: Map<String, IType>): IProcedureDeclaration? {
    val returnType = resolve().returnType

    fun Class<*>.rootComponentType(): Class<*>? {
        var current = this.componentType
        while (current != null && current.componentType != null)
            current = current.componentType
        return current
    }

    if (isAbstractMethodCall) {
        println("Handing abstract method call $this for namespace $namespace...")
        if (namespace != null) {
            val clazz: Class<*> = getClassByName(namespace)
            module.types.filterIsInstance<JavaType>().forEach {
                val t: Class<*> = it.type.rootComponentType() ?: it.type
                if (clazz.isAssignableFrom(t) && !t.isInterface) {
                    val args = arguments.map { arg -> arg.getResolvedJavaType() }
                    println("\t${clazz.canonicalName} is assignable from ${t.canonicalName}, finding method ${t.canonicalName}.$nameAsString(${args.joinToString { it.canonicalName }})...")
                    val implementation = t.findCompatibleMethod(nameAsString, args)
                    if (implementation != null) {
                        println("\tAdding method $implementation to module because ${clazz.canonicalName} is assignable from ${t.canonicalName}")
                        val foreign: ForeignProcedure = createForeignProcedure(module, t.canonicalName, implementation, false, types)
                        module.members.add(foreign)
                    }
                }
            }
        }

        return PolymophicProcedure(
            module,
            namespace,
            nameAsString,
            returnType.toIType(types)
        )
    }
    else if (scope.isPresent) {
        val clazz: Class<*> = scope.get().getResolvedJavaType()

        val args = arguments.map { it.getResolvedJavaType() }.toTypedArray()
        val method: Method = clazz.getMethod(nameAsString, *args)

        val isStaticMethod = this.resolve().isStatic
        return createForeignProcedure(module, clazz.canonicalName, method, isStaticMethod, types)
    }

    return null
}