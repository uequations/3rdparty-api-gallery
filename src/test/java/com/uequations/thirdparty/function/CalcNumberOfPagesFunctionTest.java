package com.uequations.thirdparty.function;

import org.junit.Assert;
import org.junit.Test;

public class CalcNumberOfPagesFunctionTest {

    @Test
    public void applyAsInt1() {

        int dividend = 21;
        int divisor = 20;

        Assert.assertEquals(2, new CalcNumberOfPagesFunction().applyAsInt(dividend, divisor));
    }

    @Test
    public void applyAsInt2() {

        int dividend = 20;
        int divisor = 20;

        Assert.assertEquals(1, new CalcNumberOfPagesFunction().applyAsInt(dividend, divisor));
    }
}