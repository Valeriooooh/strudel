package pt.iscte.strudel.tests

import org.junit.platform.suite.api.SelectClasses
import org.junit.platform.suite.api.Suite
import pt.iscte.strudel.tests.javaparser.TestFieldInitializers
import pt.iscte.strudel.tests.javaparser.TestLoopIterationMax
import pt.iscte.strudel.tests.javaparser.TestSortingAlgorithms
import pt.iscte.strudel.tests.javaparser.TestThrow
import pt.iscte.strudel.tests.temp.TestUnboundType
import pt.iscte.strudel.tests.temp.TestsPaper

@Suite
@SelectClasses(
    TestEmpty::class,

    // ifs
    TestAbsInt::class,
    TestAbsDouble::class,
    TestIsEven::class,

    // unary operators
    TestNot::class,
    TestRound::class,
    TestUnaryPlus::class,

    // recursion
    TestFactorial::class,

    // array functions
    TestSumDoubleArray::class,
    TestMaxArray::class,
    TestNaturals::class,
    TestArrayExists::class,
    TestCharArrayCount::class,
    TestSumPositivesArrayContinue::class,
    TestBinarySearch::class,

    // array procedures
    TestSwap::class,
    TestReplaceFirstReturn::class,
    TestReplaceFirstBreak::class,
    TestInvert::class,
    //	TestSelectionSort.class,

    // array errors
    TestArrayIndexOutOfBounds::class,
    TestDivByZeroInt::class,
    TestDivByZeroIntMod::class,
    TestDivByZeroDouble::class,

    TestLoopIterationMax::class,

    // matrix functions
    TestMatrixSum::class,
    TestMatrixIdentity::class,
    //	TestMatrixTranspose.class,
    // multiplication

    // matrix procedures
    TestMatrixScalar::class,
    // swap lines

    //	TestIsSame.class

    TestArrayList::class,
    TestLinkedList::class,

    TestAvgInvoke::class,
   // TestUnboundType::class,
    TestBuiltinRound::class,
    TestJDKLinkedList::class,
    TestBuiltinString::class,

    //--- direct source
    TestSortingAlgorithms::class,

    TestThrow::class,

    TestFieldInitializers::class

)
class AllTests